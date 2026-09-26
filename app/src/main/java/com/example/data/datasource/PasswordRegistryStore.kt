package com.example.data.datasource

import android.content.Context
import android.util.Log
import java.security.MessageDigest

/**
 * Universal Password UI, Authentication & Verification State Registry.
 *
 * Requirements:
 * - Default universal password across GGC M.B.Din is strictly "00000".
 * - Supports custom updated passwords with secure salted SHA-256 verification.
 * - Manages UI flags so that once a user updates their password in the database,
 *   the "Change Password Alert" dialog is never shown on subsequent logins.
 */
object PasswordRegistryStore {
    private const val TAG = "PasswordRegistryStore"
    private const val PREFS_NAME = "ggc_password_registry_prefs"
    private const val PREF_PREFIX_HAS_CHANGED = "has_changed_"
    private const val PREF_PREFIX_PROMPT_SHOWN = "pwd_prompt_shown_"
    private const val PREF_PREFIX_PWD_HASH = "pwd_hash_"

    private val memoryHasChanged = mutableSetOf<String>()
    private val memoryPromptShown = mutableSetOf<String>()
    private val memoryPasswordHashes = mutableMapOf<String, String>()
    private var isInitialized = false
    private var appContext: Context? = null

    val ADMIN_ALIASES = setOf(
        "SHARK1708",
        "THEASIMNAWAZ@GMAIL.COM",
        "ADMIN",
        "ADMIN@GGC.EDU.PK",
        "ADMIN_CENTRAL",
        "ADMIN-01",
        "PRINCIPAL",
        "PRINCIPAL@GGCMBDIN.EDU.PK",
        "AMIR.AHMAD",
        "AMEER.AHMAD"
    )

    fun init(context: Context) {
        if (isInitialized && appContext != null) return
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load existing flags and password hashes
        val editor = prefs.edit()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith("pwd_user_") || key == "custom_admin_password") {
                editor.remove(key)
            } else if (key.startsWith(PREF_PREFIX_HAS_CHANGED) && value == true) {
                val id = key.removePrefix(PREF_PREFIX_HAS_CHANGED)
                memoryHasChanged.add(id.uppercase())
            } else if (key.startsWith(PREF_PREFIX_PROMPT_SHOWN) && value == true) {
                val id = key.removePrefix(PREF_PREFIX_PROMPT_SHOWN)
                memoryPromptShown.add(id.uppercase())
            } else if (key.startsWith(PREF_PREFIX_PWD_HASH) && value is String && value.isNotBlank()) {
                val id = key.removePrefix(PREF_PREFIX_PWD_HASH)
                memoryPasswordHashes[id.uppercase()] = value
            }
        }
        editor.apply()

        isInitialized = true
        Log.d(TAG, "PasswordRegistryStore initialized securely.")
    }

    fun hashPassword(password: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val salted = "${password.trim()}ggc_mbdin_salt_2026".toByteArray(Charsets.UTF_8)
            val digest = md.digest(salted)
            digest.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            password.trim()
        }
    }

    /**
     * Checks if this identifier corresponds to an administrator.
     */
    fun isAdminIdentifier(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return false
        val upper = identifier.trim().uppercase()
        return ADMIN_ALIASES.contains(upper) || upper.contains("ADMIN") || upper.contains("PRINCIPAL")
    }

    /**
     * Returns true if the user has updated their initial/default password.
     */
    fun hasCustomPassword(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return false
        val clean = identifier.trim().uppercase()
        if (isAdminIdentifier(clean)) {
            if (ADMIN_ALIASES.any { memoryHasChanged.contains(it) || memoryPasswordHashes.containsKey(it) }) {
                return true
            }
        }
        if (memoryHasChanged.contains(clean) || memoryPasswordHashes.containsKey(clean)) {
            return true
        }
        val ctx = appContext ?: return false
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_PREFIX_HAS_CHANGED + clean, false) ||
                prefs.getString(PREF_PREFIX_PWD_HASH + clean, null)?.isNotBlank() == true
    }

    fun hasStoredHash(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return false
        val clean = identifier.trim().uppercase()
        if (isAdminIdentifier(clean)) {
            if (ADMIN_ALIASES.any { memoryPasswordHashes.containsKey(it) }) return true
        }
        return memoryPasswordHashes.containsKey(clean)
    }

    /**
     * Saves user password hash locally and sets changed flags across all relevant aliases.
     */
    fun saveUserPassword(identifier: String, newPassword: String, additionalAliases: List<String> = emptyList()) {
        val clean = identifier.trim().uppercase()
        val hash = hashPassword(newPassword)
        val targets = mutableSetOf(clean)
        additionalAliases.forEach { if (it.isNotBlank()) targets.add(it.trim().uppercase()) }

        if (isAdminIdentifier(clean)) {
            targets.addAll(ADMIN_ALIASES)
        }

        targets.forEach { target ->
            memoryHasChanged.add(target)
            memoryPromptShown.add(target)
            memoryPasswordHashes[target] = hash
        }

        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            targets.forEach { target ->
                editor.putBoolean(PREF_PREFIX_HAS_CHANGED + target, true)
                editor.putBoolean(PREF_PREFIX_PROMPT_SHOWN + target, true)
                editor.putString(PREF_PREFIX_PWD_HASH + target, hash)
            }
            editor.apply()
        } catch (_: Exception) {}
    }

    /**
     * Verifies if the supplied password attempt is valid for this identifier.
     * Supports:
     * - Universal default password "00000" if no custom password has been configured.
     * - Hashed verification against updated custom password.
     * - Admin master credentials.
     */
    fun verifyPassword(identifier: String?, passwordAttempt: String): Boolean {
        val cleanAttempt = passwordAttempt.trim()
        if (cleanAttempt.isBlank()) return false
        val cleanId = identifier?.trim()?.uppercase() ?: ""

        // 1. If user has not configured a custom password, default password "00000" is always valid
        val isCustom = hasCustomPassword(cleanId)
        if (!isCustom) {
            if (cleanAttempt == "00000") return true
            if (isAdminIdentifier(cleanId) && (cleanAttempt == "shark" || cleanAttempt == "admin" || cleanAttempt == "shark1708")) {
                return true
            }
        }

        // 2. Check if the attempt matches stored hash for this user
        val attemptHash = hashPassword(cleanAttempt)
        if (cleanId.isNotBlank()) {
            val stored = memoryPasswordHashes[cleanId]
            if (stored != null && stored == attemptHash) {
                return true
            }

            if (isAdminIdentifier(cleanId)) {
                for (alias in ADMIN_ALIASES) {
                    val aliasStored = memoryPasswordHashes[alias]
                    if (aliasStored != null && aliasStored == attemptHash) {
                        return true
                    }
                }
            }
        }

        // 3. Fallback: if entered attempt is 00000 and no custom hash is found in memory, accept as default
        if (cleanAttempt == "00000" && !hasStoredHash(cleanId)) {
            return true
        }

        return false
    }

    /**
     * Marks that the user has changed their password.
     */
    fun markPasswordChanged(identifier: String) {
        val clean = identifier.trim().uppercase()
        val targets = mutableSetOf(clean)
        if (isAdminIdentifier(clean)) {
            targets.addAll(ADMIN_ALIASES)
        }
        targets.forEach { target ->
            memoryHasChanged.add(target)
            memoryPromptShown.add(target)
        }
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            targets.forEach { target ->
                editor.putBoolean(PREF_PREFIX_HAS_CHANGED + target, true)
                editor.putBoolean(PREF_PREFIX_PROMPT_SHOWN + target, true)
            }
            editor.apply()
        } catch (_: Exception) {}
    }

    /**
     * Returns true if the first-login password change prompt was already shown to this user
     * or if the user has already updated their password.
     */
    fun hasShownLoginPasswordPrompt(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return true
        val clean = identifier.trim().uppercase()

        // If user already updated their password, NEVER show the Change Password prompt
        if (hasCustomPassword(clean)) return true

        if (isAdminIdentifier(clean)) {
            if (ADMIN_ALIASES.any { memoryPromptShown.contains(it) }) return true
        }
        if (memoryPromptShown.contains(clean)) return true

        val ctx = appContext ?: return false
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_PREFIX_PROMPT_SHOWN + clean, false)
    }

    /**
     * Marks the first-login password change prompt as shown for this identifier.
     */
    fun markLoginPasswordPromptShown(identifier: String?) {
        if (identifier.isNullOrBlank()) return
        val clean = identifier.trim().uppercase()
        val targets = mutableSetOf(clean)
        if (isAdminIdentifier(clean)) {
            targets.addAll(ADMIN_ALIASES)
        }
        targets.forEach { target ->
            memoryPromptShown.add(target)
        }

        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            targets.forEach { target ->
                editor.putBoolean(PREF_PREFIX_PROMPT_SHOWN + target, true)
            }
            editor.apply()
        } catch (_: Exception) {}
    }
}
