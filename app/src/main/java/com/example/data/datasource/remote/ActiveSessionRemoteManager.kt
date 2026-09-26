package com.example.data.datasource.remote

import android.content.Context
import android.util.Log
import com.example.data.model.AppRole
import com.example.util.DeviceIdentifierHelper
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Single-Device Login Enforcement Manager ("WhatsApp-Like" Single-Session Concurrency).
 *
 * Requirements:
 * - Database is the source of truth for login sessions.
 * - When user 'X' is already logged in on Device A, user 'X' cannot log in on Device B.
 * - Device B's login attempt is rejected with a clear message indicating which device is currently active.
 * - Once user 'X' explicitly logs out from Device A, the session lock is released, allowing Device B (or another device) to log in.
 * - If the same device re-authenticates, the session is seamlessly refreshed without blocking.
 */
object ActiveSessionRemoteManager {
    private const val TAG = "ActiveSessionRemote"
    const val SYSTEM_SESSION_CATEGORY = "__system_active_session__"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Serializable
    data class ActiveDeviceSession(
        val deviceId: String,
        val deviceName: String,
        val userIdentifier: String,
        val role: String,
        val isActive: Boolean,
        val loggedInAt: Long,
        val lastSeenAt: Long,
        val loggedInAtIso: String? = null
    )

    sealed class SessionAcquireResult {
        data class Success(val session: ActiveDeviceSession) : SessionAcquireResult()
        data class Blocked(
            val activeDeviceName: String,
            val activeDeviceId: String = "",
            val userIdentifier: String = "",
            val role: AppRole = AppRole.STUDENT_BS,
            val loggedInAtIso: String?,
            val message: String
        ) : SessionAcquireResult()
        data class Error(val message: String) : SessionAcquireResult()
    }

    sealed class SessionCheckResult {
        object Active : SessionCheckResult()
        data class TerminatedByOtherDevice(val otherDeviceName: String) : SessionCheckResult()
    }

    /**
     * Multi-device login: Concurrency lock removed per request.
     * All devices can log in concurrently without blocking or conflict.
     */
    suspend fun acquireSession(
        context: Context? = null,
        userIdentifier: String,
        role: AppRole,
        forceOverride: Boolean = false
    ): SessionAcquireResult = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
        val currentDeviceName = DeviceIdentifierHelper.getDeviceDisplayName()
        Log.d(TAG, "Multi-device login granted for $cleanId on $currentDeviceName")

        SessionAcquireResult.Success(
            ActiveDeviceSession(
                deviceId = currentDeviceId,
                deviceName = currentDeviceName,
                userIdentifier = cleanId,
                role = role.roleKey,
                isActive = true,
                loggedInAt = System.currentTimeMillis(),
                lastSeenAt = System.currentTimeMillis(),
                loggedInAtIso = null
            )
        )
    }

    /**
     * Releases the session lock when the user logs out from this device.
     */
    suspend fun releaseSession(
        context: Context? = null,
        userIdentifier: String
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        if (cleanId.isBlank()) return@withContext false

        val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
        val client = SupabaseClientProvider.client

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val nowIso = isoFormat.format(Date())

        // 1. Release in public.user_sessions
        try {
            // First try direct_logout_session RPC
            client.postgrest.rpc(
                function = "direct_logout_session",
                parameters = buildJsonObject {
                    put("p_identifier", cleanId)
                    put("p_device_id", currentDeviceId)
                }
            )
            Log.d(TAG, "direct_logout_session RPC executed successfully for $cleanId")
        } catch (rpcErr: Exception) {
            try {
                client.from("user_sessions").update(
                    buildJsonObject {
                        put("active", false)
                        put("revoked_at", nowIso)
                        put("last_seen_at", nowIso)
                    }
                ) {
                    filter {
                        eq("user_identifier", cleanId)
                        eq("device_id", currentDeviceId)
                        eq("active", true)
                    }
                }
            } catch (_: Exception) {
                // user_sessions may not exist yet
            }
        }

        // 2. Release in fallback table
        try {
            val title = "SESSION:$cleanId"
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_SESSION_CATEGORY)
                        eq("title", title)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val row = rows.first()
                val id = row["id"]?.jsonPrimitive?.content
                if (!id.isNullOrBlank()) {
                    val releasedSession = ActiveDeviceSession(
                        deviceId = currentDeviceId,
                        deviceName = DeviceIdentifierHelper.getDeviceDisplayName(),
                        userIdentifier = cleanId,
                        role = "none",
                        isActive = false,
                        loggedInAt = 0L,
                        lastSeenAt = System.currentTimeMillis(),
                        loggedInAtIso = null
                    )
                    val releasedContent = json.encodeToString(ActiveDeviceSession.serializer(), releasedSession)

                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", releasedContent)
                            put("updated_at", nowIso)
                        }
                    ) {
                        filter { eq("id", id) }
                    }
                    Log.d(TAG, "Successfully released session lock for $cleanId in Supabase.")
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to release session lock in fallback for $cleanId: ${e.message}")
        }
        true
    }

    /**
     * Forcibly clears the active session for an account (e.g. from Admin console or lost device support).
     */
    suspend fun forceTerminateSession(userIdentifier: String): Boolean = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        if (cleanId.isBlank()) return@withContext false
        val client = SupabaseClientProvider.client

        // 1. Call admin_force_terminate_user_session RPC
        try {
            client.postgrest.rpc(
                function = "admin_force_terminate_user_session",
                parameters = buildJsonObject {
                    put("p_identifier", cleanId)
                }
            )
        } catch (_: Exception) {
            try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                client.from("user_sessions").update(
                    buildJsonObject {
                        put("active", false)
                        put("revoked_at", isoFormat.format(Date()))
                    }
                ) {
                    filter {
                        eq("user_identifier", cleanId)
                        eq("active", true)
                    }
                }
            } catch (_: Exception) {}
        }

        // 2. Clear in fallback table
        try {
            val title = "SESSION:$cleanId"
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_SESSION_CATEGORY)
                        eq("title", title)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val row = rows.first()
                val id = row["id"]?.jsonPrimitive?.content
                if (!id.isNullOrBlank()) {
                    val clearedSession = ActiveDeviceSession(
                        deviceId = "CLEARED_BY_ADMIN",
                        deviceName = "Admin Reset",
                        userIdentifier = cleanId,
                        role = "none",
                        isActive = false,
                        loggedInAt = 0L,
                        lastSeenAt = System.currentTimeMillis(),
                        loggedInAtIso = null
                    )
                    val clearedContent = json.encodeToString(ActiveDeviceSession.serializer(), clearedSession)
                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", clearedContent)
                        }
                    ) {
                        filter { eq("id", id) }
                    }
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error forcibly terminating session: ${e.message}", e)
        }
        true
    }

    /**
     * Multi-device mode: Current device session is always valid and active.
     */
    suspend fun checkCurrentDeviceSession(
        context: Context?,
        userIdentifier: String
    ): SessionCheckResult = withContext(Dispatchers.IO) {
        SessionCheckResult.Active
    }

    private suspend fun syncFallbackSession(cleanId: String, session: ActiveDeviceSession, nowIso: String) {
        try {
            val client = SupabaseClientProvider.client
            val title = "SESSION:$cleanId"
            val content = json.encodeToString(ActiveDeviceSession.serializer(), session)
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_SESSION_CATEGORY)
                        eq("title", title)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val id = rows.first()["id"]?.jsonPrimitive?.content
                if (!id.isNullOrBlank()) {
                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", content)
                            put("updated_at", nowIso)
                        }
                    ) {
                        filter { eq("id", id) }
                    }
                }
            } else {
                client.from("announcements").insert(
                    buildJsonObject {
                        put("title", title)
                        put("content", content)
                        put("category", SYSTEM_SESSION_CATEGORY)
                        put("is_published", false)
                        put("is_pinned", false)
                    }
                )
            }
        } catch (_: Exception) {}
    }

    const val SYSTEM_TRANSFER_CATEGORY = "__system_transfer_request__"

    @Serializable
    data class SessionTransferRequest(
        val requestId: String,
        val userIdentifier: String,
        val role: String,
        val fromDeviceId: String,
        val fromDeviceName: String,
        val toDeviceId: String,
        val toDeviceName: String,
        val status: String, // "PENDING", "APPROVED", "REJECTED", "EXPIRED"
        val createdAt: Long = System.currentTimeMillis(),
        val expiresAt: Long = System.currentTimeMillis() + 90_000L
    )

    /**
     * Creates a new login transfer request from this device (Device B) to the active device (Device A).
     */
    suspend fun createTransferRequest(
        context: Context?,
        userIdentifier: String,
        role: AppRole,
        activeDeviceName: String,
        activeDeviceId: String = ""
    ): SessionTransferRequest = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
        val currentDeviceName = DeviceIdentifierHelper.getDeviceDisplayName()
        val now = System.currentTimeMillis()

        // Multi-device concurrency allowed: Instantly approved without security alerts or blocking.
        SessionTransferRequest(
            requestId = "req_${cleanId}_${now}",
            userIdentifier = cleanId,
            role = role.roleKey,
            fromDeviceId = activeDeviceId,
            fromDeviceName = activeDeviceName,
            toDeviceId = currentDeviceId,
            toDeviceName = currentDeviceName,
            status = "APPROVED",
            createdAt = now,
            expiresAt = now + 90_000L
        )
    }

    /**
     * Checks status of an existing transfer request.
     */
    suspend fun checkTransferRequestStatus(
        userIdentifier: String,
        requestId: String
    ): String = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        val client = SupabaseClientProvider.client
        val reqTitle = "TRANSFER_REQ:$cleanId"

        try {
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_TRANSFER_CATEGORY)
                        eq("title", reqTitle)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val contentStr = rows.first()["content"]?.jsonPrimitive?.content
                if (!contentStr.isNullOrBlank()) {
                    val req = json.decodeFromString<SessionTransferRequest>(contentStr)
                    if (req.requestId == requestId) {
                        if (System.currentTimeMillis() > req.expiresAt && req.status == "PENDING") {
                            return@withContext "EXPIRED"
                        }
                        return@withContext req.status
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking transfer request status: ${e.message}", e)
        }
        "EXPIRED"
    }

    /**
     * Multi-device mode: No transfer requests needed.
     */
    suspend fun getPendingTransferRequest(
        userIdentifier: String,
        currentDeviceId: String
    ): SessionTransferRequest? = withContext(Dispatchers.IO) {
        null
    }

    /**
     * Active device approves the login transfer request.
     */
    suspend fun approveTransferRequest(
        context: Context?,
        request: SessionTransferRequest
    ): Boolean = withContext(Dispatchers.IO) {
        val client = SupabaseClientProvider.client
        val cleanId = request.userIdentifier.trim().uppercase()
        val reqTitle = "TRANSFER_REQ:$cleanId"

        val approvedReq = request.copy(status = "APPROVED")
        val updatedContent = json.encodeToString(SessionTransferRequest.serializer(), approvedReq)

        try {
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_TRANSFER_CATEGORY)
                        eq("title", reqTitle)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val rowId = rows.first()["id"]?.jsonPrimitive?.content
                if (!rowId.isNullOrBlank()) {
                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", updatedContent)
                        }
                    ) {
                        filter { eq("id", rowId) }
                    }
                }
            }

            releaseSession(context, cleanId)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error approving transfer request: ${e.message}", e)
            false
        }
    }

    /**
     * Active device rejects the login transfer request.
     */
    suspend fun rejectTransferRequest(
        request: SessionTransferRequest
    ): Boolean = withContext(Dispatchers.IO) {
        val client = SupabaseClientProvider.client
        val cleanId = request.userIdentifier.trim().uppercase()
        val reqTitle = "TRANSFER_REQ:$cleanId"

        val rejectedReq = request.copy(status = "REJECTED")
        val updatedContent = json.encodeToString(SessionTransferRequest.serializer(), rejectedReq)

        try {
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_TRANSFER_CATEGORY)
                        eq("title", reqTitle)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val rowId = rows.first()["id"]?.jsonPrimitive?.content
                if (!rowId.isNullOrBlank()) {
                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", updatedContent)
                        }
                    ) {
                        filter { eq("id", rowId) }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error rejecting transfer request: ${e.message}", e)
            false
        }
    }
}
