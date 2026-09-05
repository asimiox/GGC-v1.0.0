package com.example.data.datasource.remote

import android.util.Log
import com.example.data.datasource.PasswordRegistryStore
import com.example.data.model.AppRole
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
 * Universal Database-Backed Central Authentication & Password Synchronization Manager.
 * Solves cross-device password inconsistency by persisting credentials in the central Supabase database.
 *
 * Flow:
 * - When a user changes their password on Device A, it is immediately updated in Supabase.
 * - When the user enters credentials on Device B, Device B checks the central Supabase database first.
 *   This ensures that updated passwords (e.g. 'shark') are immediately recognized everywhere,
 *   and the old default password ('00000') is correctly rejected on all devices.
 */
object CentralAuthRemoteManager {
    private const val TAG = "CentralAuthRemote"
    const val SYSTEM_CREDENTIAL_CATEGORY = "__system_credential__"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed class PasswordVerificationResult {
        data class Success(val isCustomPassword: Boolean, val resolvedPassword: String) : PasswordVerificationResult()
        data class Failed(val isCustomPassword: Boolean, val message: String) : PasswordVerificationResult()
    }

    /**
     * Resolves the primary credential key for an identifier.
     * Admin identifiers map to a unified ADMIN_MASTER key so any alias receives the updated password.
     */
    fun resolveCredentialKey(identifier: String): String {
        val clean = identifier.trim().uppercase()
        return if (PasswordRegistryStore.isAdminIdentifier(clean)) {
            "ADMIN_MASTER"
        } else {
            clean
        }
    }

    /**
     * Fetches the latest updated password from the central Supabase database for the given identifier.
     * Returns null if no custom password has been set yet (user is still using default password).
     */
    suspend fun fetchRemotePassword(identifier: String): String? = withContext(Dispatchers.IO) {
        if (identifier.isBlank()) return@withContext null
        val key = resolveCredentialKey(identifier)
        val title = "CRED:$key"

        try {
            val client = SupabaseClientProvider.client
            val rows = client.from("announcements")
                .select {
                    filter {
                        eq("category", SYSTEM_CREDENTIAL_CATEGORY)
                        eq("title", title)
                    }
                    limit(1)
                }.decodeList<JsonObject>()

            if (rows.isNotEmpty()) {
                val row = rows.first()
                val contentStr = row["content"]?.jsonPrimitive?.content
                if (!contentStr.isNullOrBlank()) {
                    try {
                        val parsed = json.parseToJsonElement(contentStr) as? JsonObject
                        val pass = parsed?.get("password")?.jsonPrimitive?.content
                        if (!pass.isNullOrBlank()) {
                            Log.d(TAG, "Fetched remote password for $key from Supabase database.")
                            // Update local cache on this device
                            PasswordRegistryStore.saveRemotePasswordToLocalCache(identifier, pass)
                            return@withContext pass
                        }
                    } catch (parseErr: Exception) {
                        Log.w(TAG, "Failed to parse remote credential JSON: ${parseErr.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Remote password fetch note for $key: ${e.message}")
        }
        null
    }

    /**
     * Saves or updates a password in the central Supabase database across all associated identifiers
     * (e.g. Roll Number, Registration Number, Username, Faculty ID).
     */
    suspend fun saveRemotePassword(
        identifiers: List<String>,
        newPassword: String,
        role: AppRole
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanPass = newPassword.trim()
        if (cleanPass.isBlank()) return@withContext false

        val client = SupabaseClientProvider.client
        val distinctKeys = identifiers.filter { it.isNotBlank() }
            .map { resolveCredentialKey(it) }
            .distinct()

        val allKeys = if (role == AppRole.ADMIN || distinctKeys.contains("ADMIN_MASTER")) {
            listOf("ADMIN_MASTER", "SHARK1708", "THEASIMNAWAZ@GMAIL.COM", "ADMIN", "ADMIN@GGC.EDU.PK")
        } else {
            distinctKeys
        }

        var allSucceeded = true
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val nowIso = isoFormat.format(Date())

        for (k in allKeys) {
            val title = "CRED:$k"
            val contentJson = buildJsonObject {
                put("password", cleanPass)
                put("role", role.roleKey)
                put("updated_at", System.currentTimeMillis())
                put("updated_at_iso", nowIso)
            }.toString()

            try {
                // Check if existing record exists
                val existing = client.from("announcements")
                    .select {
                        filter {
                            eq("category", SYSTEM_CREDENTIAL_CATEGORY)
                            eq("title", title)
                        }
                        limit(1)
                    }.decodeList<JsonObject>()

                if (existing.isNotEmpty()) {
                    val existingId = existing.first()["id"]?.jsonPrimitive?.content
                    if (!existingId.isNullOrBlank()) {
                        client.from("announcements").update(
                            buildJsonObject {
                                put("content", contentJson)
                                put("updated_at", nowIso)
                            }
                        ) {
                            filter { eq("id", existingId) }
                        }
                        Log.d(TAG, "Updated existing remote credential for $title in Supabase.")
                    }
                } else {
                    val newPayload = buildJsonObject {
                        put("title", title)
                        put("content", contentJson)
                        put("category", SYSTEM_CREDENTIAL_CATEGORY)
                        put("is_published", false)
                        put("is_pinned", false)
                    }
                    client.from("announcements").insert(newPayload)
                    Log.d(TAG, "Inserted new remote credential for $title in Supabase.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save remote credential for $k: ${e.message}", e)
                allSucceeded = false
            }
        }
        allSucceeded
    }

    /**
     * Verifies the user's password attempt against the central Supabase database first,
     * falling back to local cached store and default password rules.
     */
    suspend fun verifyPasswordWithRemote(
        identifier: String,
        passwordAttempt: String,
        defaultPasswordFallback: String = "00000"
    ): PasswordVerificationResult = withContext(Dispatchers.IO) {
        val cleanAttempt = passwordAttempt.trim()
        val cleanId = identifier.trim().uppercase()

        // 1. Fetch remote password directly from Supabase database
        val remotePass = fetchRemotePassword(cleanId)

        if (!remotePass.isNullOrBlank()) {
            // User HAS a custom password stored in the central database
            return@withContext if (cleanAttempt == remotePass) {
                PasswordVerificationResult.Success(isCustomPassword = true, resolvedPassword = remotePass)
            } else {
                PasswordVerificationResult.Failed(
                    isCustomPassword = true,
                    message = "Incorrect password. You changed your password on another device. Please use your updated password."
                )
            }
        }

        // 2. Fallback check on local PasswordRegistryStore (in case device is offline or local cache has newer update)
        val localPass = PasswordRegistryStore.getCustomPassword(cleanId)
        if (!localPass.isNullOrBlank()) {
            if (cleanAttempt == localPass) {
                // Background sync local password to remote database so other devices can see it
                try {
                    val role = if (PasswordRegistryStore.isAdminIdentifier(cleanId)) AppRole.ADMIN else AppRole.STUDENT_BS
                    saveRemotePassword(listOf(cleanId), localPass, role)
                } catch (_: Exception) {}
                return@withContext PasswordVerificationResult.Success(isCustomPassword = true, resolvedPassword = localPass)
            } else {
                return@withContext PasswordVerificationResult.Failed(
                    isCustomPassword = true,
                    message = "Incorrect password. Please use your updated password."
                )
            }
        }

        // 3. User has NOT updated password - verify against default password
        val isDefaultMatch = if (PasswordRegistryStore.isAdminIdentifier(cleanId)) {
            cleanAttempt == "a\$im0011" || cleanAttempt == "admin123" || cleanAttempt == "admin"
        } else {
            cleanAttempt == defaultPasswordFallback || cleanAttempt == "00000"
        }

        if (isDefaultMatch) {
            PasswordVerificationResult.Success(isCustomPassword = false, resolvedPassword = defaultPasswordFallback)
        } else {
            PasswordVerificationResult.Failed(
                isCustomPassword = false,
                message = "Incorrect password. Please verify your credentials or use the default password ($defaultPasswordFallback)."
            )
        }
    }
}
