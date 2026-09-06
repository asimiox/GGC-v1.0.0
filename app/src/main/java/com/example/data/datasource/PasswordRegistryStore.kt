package com.example.data.datasource

import android.content.Context
import android.util.Log

/**
 * Universal Password UI & Prompt State Registry.
 *
 * NOTE: In strict accordance with security directives:
 * - Passwords are NEVER cached or stored locally in plaintext.
 * - Database is the SOLE source of truth for passwords and credentials.
 * - This store only tracks transient UI prompt flags (such as whether the first-login
 *   prompt dialog was shown or if the user has changed away from the default password).
 */
object PasswordRegistryStore {
    private const val TAG = "PasswordRegistryStore"
    private const val PREFS_NAME = "ggc_password_registry_prefs"
    private const val PREF_PREFIX_HAS_CHANGED = "has_changed_"
    private const val PREF_PREFIX_PROMPT_SHOWN = "pwd_prompt_shown_"

    private val memoryHasChanged = mutableSetOf<String>()
    private val memoryPromptShown = mutableSetOf<String>()
    private var isInitialized = false
    private var appContext: Context? = null

    fun init(context: Context) {
        if (isInitialized && appContext != null) return
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Clean up any legacy plaintext keys from older iterations
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
            }
        }
        editor.apply()

        isInitialized = true
        Log.d(TAG, "PasswordRegistryStore initialized securely (no plaintext passwords stored).")
    }

    /**
     * Checks if this identifier corresponds to an administrator.
     */
    fun isAdminIdentifier(identifier: String): Boolean {
        val upper = identifier.trim().uppercase()
        return upper == "SHARK1708" ||
               upper == "THEASIMNAWAZ@GMAIL.COM" ||
               upper == "ADMIN" ||
               upper == "ADMIN@GGC.EDU.PK" ||
               upper == "ADMIN_CENTRAL"
    }

    /**
     * Returns true if the user has updated their initial/default password.
     */
    fun hasCustomPassword(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return false
        val clean = identifier.trim().uppercase()
        return memoryHasChanged.contains(clean)
    }

    /**
     * Marks that the user has changed their password.
     */
    fun markPasswordChanged(identifier: String) {
        val clean = identifier.trim().uppercase()
        memoryHasChanged.add(clean)
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_PREFIX_HAS_CHANGED + clean, true).apply()
        } catch (_: Exception) {}
    }

    /**
     * Returns true if the first-login password change prompt was already shown to this user.
     */
    fun hasShownLoginPasswordPrompt(identifier: String?): Boolean {
        if (identifier.isNullOrBlank()) return true
        val clean = identifier.trim().uppercase()
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
        memoryPromptShown.add(clean)

        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_PREFIX_PROMPT_SHOWN + clean, true).apply()
        } catch (_: Exception) {}
    }
}
