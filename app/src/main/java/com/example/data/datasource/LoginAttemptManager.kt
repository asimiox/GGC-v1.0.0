package com.example.data.datasource

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.Locale

/**
 * Manages login attempts and 24-hour security lockout across all authentication portals
 * (Faculty, BS Students, Intermediate Students, and Administrators).
 *
 * Rules:
 * - 1st failed attempt -> "Invalid Credentials. 2 attempts left."
 * - 2nd failed attempt -> "Invalid Credentials. 1 attempt left."
 * - 3rd failed attempt -> Account locked for 24 hours with live countdown timer.
 * - Successful login -> Resets failed attempts to 0.
 */
object LoginAttemptManager {
    private const val TAG = "LoginAttemptManager"
    private const val PREFS_NAME = "ggc_login_attempts_prefs"
    private const val PREFIX_ATTEMPTS = "attempts_"
    private const val PREFIX_BLOCKED_UNTIL = "blocked_until_"

    const val MAX_ATTEMPTS = 3
    const val BLOCK_DURATION_MS = 24L * 60L * 60L * 1000L // 24 Hours = 86,400,000 ms

    sealed class AttemptResult {
        data class Failed(val attemptsLeft: Int, val message: String) : AttemptResult()
        data class Blocked(val remainingMs: Long, val message: String) : AttemptResult()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun normalize(identifier: String?): String {
        return identifier?.trim()?.lowercase()?.ifBlank { "anonymous_user" } ?: "anonymous_user"
    }

    /**
     * Checks if this identifier is currently under a 24-hour block.
     * Automatically clears expired blocks.
     */
    fun isBlocked(context: Context, identifier: String?): Boolean {
        val cleanId = normalize(identifier)
        val prefs = getPrefs(context)
        val blockedUntil = prefs.getLong(PREFIX_BLOCKED_UNTIL + cleanId, 0L)
        val now = System.currentTimeMillis()

        if (blockedUntil > now) {
            return true
        }

        // If block has expired, clear it
        if (blockedUntil > 0L) {
            clearLockout(context, cleanId)
        }
        return false
    }

    /**
     * Returns remaining lockout time in milliseconds, or 0 if not blocked.
     */
    fun getRemainingBlockTimeMs(context: Context, identifier: String?): Long {
        val cleanId = normalize(identifier)
        val prefs = getPrefs(context)
        val blockedUntil = prefs.getLong(PREFIX_BLOCKED_UNTIL + cleanId, 0L)
        val now = System.currentTimeMillis()

        return if (blockedUntil > now) {
            blockedUntil - now
        } else {
            if (blockedUntil > 0L) {
                clearLockout(context, cleanId)
            }
            0L
        }
    }

    /**
     * Formats milliseconds into clean HH:mm:ss string.
     */
    fun formatRemainingTime(remainingMs: Long): String {
        if (remainingMs <= 0) return "00:00:00"
        val totalSeconds = (remainingMs + 999) / 1000 // Round up to next second
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Records a failed login attempt for the given identifier.
     * Enforces the 3-attempt limit and 24-hour lockout.
     */
    fun recordFailedAttempt(context: Context, identifier: String?): AttemptResult {
        val cleanId = normalize(identifier)
        val prefs = getPrefs(context)
        val now = System.currentTimeMillis()

        // Check if already blocked
        val existingBlockedUntil = prefs.getLong(PREFIX_BLOCKED_UNTIL + cleanId, 0L)
        if (existingBlockedUntil > now) {
            val remainingMs = existingBlockedUntil - now
            val formatted = formatRemainingTime(remainingMs)
            return AttemptResult.Blocked(
                remainingMs = remainingMs,
                message = "Account blocked for 24 hours. Time remaining: $formatted"
            )
        }

        val currentAttempts = prefs.getInt(PREFIX_ATTEMPTS + cleanId, 0)
        val newAttempts = currentAttempts + 1

        Log.d(TAG, "Recorded failed attempt for $cleanId: $newAttempts of $MAX_ATTEMPTS")

        return if (newAttempts >= MAX_ATTEMPTS) {
            val blockUntil = now + BLOCK_DURATION_MS
            prefs.edit()
                .putInt(PREFIX_ATTEMPTS + cleanId, MAX_ATTEMPTS)
                .putLong(PREFIX_BLOCKED_UNTIL + cleanId, blockUntil)
                .apply()

            val formatted = formatRemainingTime(BLOCK_DURATION_MS)
            AttemptResult.Blocked(
                remainingMs = BLOCK_DURATION_MS,
                message = "Account blocked for 24 hours. Time remaining: $formatted"
            )
        } else {
            prefs.edit()
                .putInt(PREFIX_ATTEMPTS + cleanId, newAttempts)
                .apply()

            val attemptsLeft = MAX_ATTEMPTS - newAttempts
            val message = if (attemptsLeft == 1) {
                "Invalid Credentials. 1 attempt left."
            } else {
                "Invalid Credentials. 2 attempts left."
            }

            AttemptResult.Failed(
                attemptsLeft = attemptsLeft,
                message = message
            )
        }
    }

    /**
     * Resets failed attempts and unlocks account upon successful authentication.
     */
    fun recordSuccessfulLogin(context: Context, identifier: String?) {
        val cleanId = normalize(identifier)
        clearLockout(context, cleanId)
        Log.d(TAG, "Login successful for $cleanId: attempts reset.")
    }

    /**
     * Clears lockout and attempt count for an identifier.
     */
    fun clearLockout(context: Context, identifier: String?) {
        val cleanId = normalize(identifier)
        val prefs = getPrefs(context)
        prefs.edit()
            .remove(PREFIX_ATTEMPTS + cleanId)
            .remove(PREFIX_BLOCKED_UNTIL + cleanId)
            .apply()
    }
}
