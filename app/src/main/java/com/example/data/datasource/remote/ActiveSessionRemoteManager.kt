package com.example.data.datasource.remote

import android.content.Context
import android.util.Log
import com.example.data.model.AppRole
import com.example.util.DeviceIdentifierHelper
import io.github.jan.supabase.postgrest.from
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
            val loggedInAtIso: String?,
            val message: String
        ) : SessionAcquireResult()
        data class Error(val message: String) : SessionAcquireResult()
    }

    /**
     * Checks if this account is currently active on another device.
     * If free or on the same device, claims the active session lock in Supabase.
     * If locked on a different device, blocks the login.
     */
    suspend fun acquireSession(
        context: Context? = null,
        userIdentifier: String,
        role: AppRole,
        forceOverride: Boolean = false
    ): SessionAcquireResult = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        if (cleanId.isBlank()) {
            return@withContext SessionAcquireResult.Error("Invalid user identifier for session.")
        }

        val sessionKey = CentralAuthRemoteManager.resolveCredentialKey(cleanId)
        val title = "SESSION:$sessionKey"
        val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
        val currentDeviceName = DeviceIdentifierHelper.getDeviceDisplayName()
        val client = SupabaseClientProvider.client

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val now = System.currentTimeMillis()
        val nowIso = isoFormat.format(Date(now))

        try {
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_SESSION_CATEGORY)
                        eq("title", title)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val existingRow = rows.first()
                val existingId = existingRow["id"]?.jsonPrimitive?.content
                val contentStr = existingRow["content"]?.jsonPrimitive?.content

                var existingSession: ActiveDeviceSession? = null
                if (!contentStr.isNullOrBlank()) {
                    try {
                        existingSession = json.decodeFromString<ActiveDeviceSession>(contentStr)
                    } catch (parseErr: Exception) {
                        Log.w(TAG, "Failed to parse session content: ${parseErr.message}")
                    }
                }

                // Check active status
                if (existingSession != null && existingSession.isActive) {
                    val isSameDevice = existingSession.deviceId.equals(currentDeviceId, ignoreCase = true)

                    if (!isSameDevice && !forceOverride) {
                        // BLOCKED: User is currently logged in on another device!
                        val deviceLabel = existingSession.deviceName.ifBlank { "Another Device" }
                        val blockMsg = "This account is currently active on $deviceLabel. You cannot log in here unless you first log out from that device, or request College Administration to reset your session."
                        Log.w(TAG, "Single-Device Enforcement: Blocked login for $cleanId on $currentDeviceName (active on $deviceLabel)")
                        return@withContext SessionAcquireResult.Blocked(
                            activeDeviceName = deviceLabel,
                            loggedInAtIso = existingSession.loggedInAtIso,
                            message = blockMsg
                        )
                    }
                }

                // Either same device, previously logged out (isActive == false), or forceOverride:
                // Claim and update session lock for this device
                val updatedSession = ActiveDeviceSession(
                    deviceId = currentDeviceId,
                    deviceName = currentDeviceName,
                    userIdentifier = cleanId,
                    role = role.roleKey,
                    isActive = true,
                    loggedInAt = now,
                    lastSeenAt = now,
                    loggedInAtIso = nowIso
                )
                val updatedContent = json.encodeToString(ActiveDeviceSession.serializer(), updatedSession)

                if (!existingId.isNullOrBlank()) {
                    client.from("announcements").update(
                        buildJsonObject {
                            put("content", updatedContent)
                            put("updated_at", nowIso)
                        }
                    ) {
                        filter { eq("id", existingId) }
                    }
                    Log.d(TAG, "Session lock updated in Supabase for $cleanId on $currentDeviceName")
                }
                return@withContext SessionAcquireResult.Success(updatedSession)

            } else {
                // No session record exists in Supabase yet -> Insert new active session lock
                val newSession = ActiveDeviceSession(
                    deviceId = currentDeviceId,
                    deviceName = currentDeviceName,
                    userIdentifier = cleanId,
                    role = role.roleKey,
                    isActive = true,
                    loggedInAt = now,
                    lastSeenAt = now,
                    loggedInAtIso = nowIso
                )
                val newContent = json.encodeToString(ActiveDeviceSession.serializer(), newSession)

                val insertPayload = buildJsonObject {
                    put("title", title)
                    put("content", newContent)
                    put("category", SYSTEM_SESSION_CATEGORY)
                    put("is_published", false)
                    put("is_pinned", false)
                }
                client.from("announcements").insert(insertPayload)
                Log.d(TAG, "New session lock inserted in Supabase for $cleanId on $currentDeviceName")
                return@withContext SessionAcquireResult.Success(newSession)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to verify or acquire session for $cleanId: ${e.message}", e)
            // In case of transient network error during login check, allow login with local session tracking
            val fallbackSession = ActiveDeviceSession(
                deviceId = currentDeviceId,
                deviceName = currentDeviceName,
                userIdentifier = cleanId,
                role = role.roleKey,
                isActive = true,
                loggedInAt = now,
                lastSeenAt = now,
                loggedInAtIso = nowIso
            )
            SessionAcquireResult.Success(fallbackSession)
        }
    }

    /**
     * Releases the session lock when the user logs out from this device.
     * Marks isActive = false in Supabase so another device can now log in.
     */
    suspend fun releaseSession(
        context: Context? = null,
        userIdentifier: String
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        if (cleanId.isBlank()) return@withContext false

        val sessionKey = CentralAuthRemoteManager.resolveCredentialKey(cleanId)
        val title = "SESSION:$sessionKey"
        val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
        val currentDeviceName = DeviceIdentifierHelper.getDeviceDisplayName()
        val client = SupabaseClientProvider.client

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val nowIso = isoFormat.format(Date())

        try {
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
                        deviceName = currentDeviceName,
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
            Log.w(TAG, "Failed to release session lock for $cleanId: ${e.message}")
        }
        false
    }

    /**
     * Forcibly clears the active session for an account (e.g. from Admin console or lost device support).
     */
    suspend fun forceTerminateSession(userIdentifier: String): Boolean = withContext(Dispatchers.IO) {
        val cleanId = userIdentifier.trim().uppercase()
        if (cleanId.isBlank()) return@withContext false

        val sessionKey = CentralAuthRemoteManager.resolveCredentialKey(cleanId)
        val title = "SESSION:$sessionKey"
        val client = SupabaseClientProvider.client

        try {
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
                    Log.d(TAG, "Forcibly terminated session lock for $cleanId.")
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error forcibly terminating session: ${e.message}", e)
        }
        false
    }
}
