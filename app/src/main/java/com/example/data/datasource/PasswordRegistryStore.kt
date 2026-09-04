package com.example.data.datasource

import android.content.Context
import android.util.Log
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.SupabaseClientProvider
import com.example.data.model.AppRole
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Universal Password Registry Store.
 * Provides synchronized password storage and verification across all user tiers:
 * - BS Students
 * - Intermediate Students
 * - Faculty Members & Teachers
 * - Heads of Department (HOD)
 * - Super Administrators & Principal
 *
 * Ensures that whenever any user changes their password, it is immediately persisted
 * locally, reflected in their profile, and enforced on all subsequent login attempts.
 */
object PasswordRegistryStore {
    private const val TAG = "PasswordRegistryStore"
    private const val PREFS_NAME = "ggc_password_registry_prefs"
    private const val KEY_ADMIN_PASSWORD = "custom_admin_password"
    private const val PREF_PREFIX_USER = "pwd_user_"
    private const val PREF_PREFIX_HAS_CHANGED = "has_changed_"
    private const val PREF_PREFIX_PROMPT_SHOWN = "pwd_prompt_shown_"

    private val memoryPasswords = mutableMapOf<String, String>()
    private val memoryHasChanged = mutableSetOf<String>()
    private var isInitialized = false
    private var appContext: Context? = null

    fun init(context: Context) {
        if (isInitialized && appContext != null) return
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load custom passwords into in-memory cache
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(PREF_PREFIX_USER) && value is String) {
                val id = key.removePrefix(PREF_PREFIX_USER)
                memoryPasswords[id.uppercase()] = value
            } else if (key.startsWith(PREF_PREFIX_HAS_CHANGED) && value == true) {
                val id = key.removePrefix(PREF_PREFIX_HAS_CHANGED)
                memoryHasChanged.add(id.uppercase())
            }
        }

        val customAdmin = prefs.getString(KEY_ADMIN_PASSWORD, null)
        if (!customAdmin.isNullOrBlank()) {
            listOf("SHARK1708", "THEASIMNAWAZ@GMAIL.COM", "ADMIN", "ADMIN@GGC.EDU.PK").forEach { adminId ->
                memoryPasswords[adminId] = customAdmin
                memoryHasChanged.add(adminId)
            }
        }

        isInitialized = true
        Log.d(TAG, "PasswordRegistryStore initialized with ${memoryPasswords.size} cached passwords.")
    }

    /**
     * Checks if this identifier corresponds to an administrator.
     */
    fun isAdminIdentifier(identifier: String): Boolean {
        val upper = identifier.trim().uppercase()
        return upper == "SHARK1708" ||
               upper == "THEASIMNAWAZ@GMAIL.COM" ||
               upper == "ADMIN" ||
               upper == "ADMIN@GGC.EDU.PK"
    }

    /**
     * Returns true if the user has updated their initial/default password.
     */
    fun hasCustomPassword(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return false
        val clean = identifier.trim().uppercase()

        if (isAdminIdentifier(clean)) {
            val ctx = appContext
            if (ctx != null) {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val adminPass = prefs.getString(KEY_ADMIN_PASSWORD, null)
                if (!adminPass.isNullOrBlank()) return true
            }
            return memoryPasswords.containsKey("SHARK1708") || memoryPasswords.containsKey(clean)
        }

        return memoryHasChanged.contains(clean) || memoryPasswords.containsKey(clean)
    }

    /**
     * Returns the custom password for this identifier, or null if using default.
     */
    fun getCustomPassword(identifier: String?): String? {
        if (identifier.isNullOrBlank()) return null
        val clean = identifier.trim().uppercase()

        if (isAdminIdentifier(clean)) {
            val ctx = appContext
            if (ctx != null) {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val adminPass = prefs.getString(KEY_ADMIN_PASSWORD, null)
                if (!adminPass.isNullOrBlank()) return adminPass
            }
            return memoryPasswords["SHARK1708"] ?: memoryPasswords[clean]
        }

        return memoryPasswords[clean]
    }

    /**
     * Validates whether the given passwordAttempt matches the current password for this identifier.
     * If user has updated password: ONLY the updated custom password is valid.
     * If user has NOT updated password: the role's default password is valid.
     */
    fun verifyPassword(identifier: String, passwordAttempt: String): Boolean {
        val cleanId = identifier.trim().uppercase()
        val cleanAttempt = passwordAttempt.trim()
        if (cleanAttempt.isBlank()) return false

        // 1. Admin Verification
        if (isAdminIdentifier(cleanId)) {
            val customAdminPass = getCustomPassword(cleanId)
            return if (!customAdminPass.isNullOrBlank()) {
                cleanAttempt == customAdminPass
            } else {
                cleanAttempt == "a\$im0011" || cleanAttempt == "admin123" || cleanAttempt == "admin"
            }
        }

        // 2. Students & Faculty Verification
        val customPass = memoryPasswords[cleanId]
        return if (!customPass.isNullOrBlank()) {
            cleanAttempt == customPass
        } else {
            cleanAttempt == "00000"
        }
    }

    /**
     * Checks if the one-time post-login password change popup has already been shown
     * for this user account.
     */
    fun hasShownLoginPasswordPrompt(identifier: String): Boolean {
        if (identifier.isBlank()) return false
        val key = PREF_PREFIX_PROMPT_SHOWN + identifier.trim().uppercase()
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs?.getBoolean(key, false) ?: false
    }

    /**
     * Marks the post-login password change popup as having been shown for this account,
     * ensuring it only appears strictly once after login.
     */
    fun markLoginPasswordPromptShown(identifier: String) {
        if (identifier.isBlank()) return
        val key = PREF_PREFIX_PROMPT_SHOWN + identifier.trim().uppercase()
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putBoolean(key, true)
            ?.apply()
    }

    /**
     * Updates password for the given user and all linked aliases/identifiers
     * (e.g. rollNumber, registrationNumber, username, facultyId, email).
     */
    fun updatePasswordForUser(
        context: Context,
        identifiers: List<String>,
        newPassword: String,
        appRole: AppRole
    ) {
        init(context)
        val cleanPass = newPassword.trim()
        if (cleanPass.isBlank()) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val cleanIds = identifiers.filter { it.isNotBlank() }.map { it.trim().uppercase() }.toMutableList()

        if (appRole == AppRole.ADMIN || cleanIds.any { isAdminIdentifier(it) }) {
            editor.putString(KEY_ADMIN_PASSWORD, cleanPass)
            listOf("SHARK1708", "THEASIMNAWAZ@GMAIL.COM", "ADMIN", "ADMIN@GGC.EDU.PK").forEach { adminId ->
                cleanIds.add(adminId)
            }
        }

        cleanIds.distinct().forEach { cleanId ->
            memoryPasswords[cleanId] = cleanPass
            memoryHasChanged.add(cleanId)
            editor.putString(PREF_PREFIX_USER + cleanId, cleanPass)
            editor.putBoolean(PREF_PREFIX_HAS_CHANGED + cleanId, true)
        }
        editor.apply()

        // Sync with UserProfileManager
        UserProfileManager.updatePassword(context, cleanPass)

        // Sync with RegisteredStudentStore
        if (appRole == AppRole.STUDENT_BS) {
            cleanIds.forEach { RegisteredStudentStore.updateBsPassword(it, cleanPass) }
        } else if (appRole == AppRole.STUDENT_INTERMEDIATE) {
            cleanIds.forEach { RegisteredStudentStore.updateIntermediatePassword(it, cleanPass) }
        } else if (appRole.isTeacherLevel || appRole == AppRole.HOD) {
            cleanIds.forEach { RegisteredFacultyStore.updatePassword(it, cleanPass) }
        }

        Log.d(TAG, "Successfully updated password for ${cleanIds.size} identifiers (Role: ${appRole.roleKey}).")

        // Asynchronous remote database synchronization (Best-Effort)
        CoroutineScope(Dispatchers.IO).launch {
            syncPasswordToRemote(cleanIds, cleanPass, appRole)
        }
    }

    private suspend fun syncPasswordToRemote(identifiers: List<String>, newPassword: String, appRole: AppRole) {
        try {
            val client = SupabaseClientProvider.client
            when (appRole) {
                AppRole.STUDENT_BS -> {
                    for (id in identifiers) {
                        try {
                            client.from("bs_students").update(
                                buildJsonObject { put("password", newPassword) }
                            ) {
                                filter {
                                    or {
                                        eq("roll_number", id)
                                        eq("registration_number", id)
                                        eq("username", id.lowercase())
                                    }
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
                AppRole.STUDENT_INTERMEDIATE -> {
                    for (id in identifiers) {
                        try {
                            client.from("intermediate_students").update(
                                buildJsonObject { put("password", newPassword) }
                            ) {
                                filter {
                                    or {
                                        eq("roll_number", id)
                                        eq("registration_number", id)
                                        eq("username", id.lowercase())
                                    }
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
                AppRole.TEACHER, AppRole.HOD -> {
                    for (id in identifiers) {
                        try {
                            client.from("official_faculty").update(
                                buildJsonObject { put("password", newPassword) }
                            ) {
                                filter {
                                    or {
                                        eq("faculty_id", id)
                                        eq("username", id.lowercase())
                                    }
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
                AppRole.ADMIN -> {
                    // Handled locally in PasswordRegistryStore and AdminAuthRemoteDataSource
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Remote password sync notice: ${e.message}")
        }
    }
}
