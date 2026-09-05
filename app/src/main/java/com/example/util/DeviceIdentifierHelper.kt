package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.util.UUID

/**
 * Provides persistent unique device identification and human-readable device labels
 * for single-device session enforcement (like WhatsApp / Telegram).
 */
object DeviceIdentifierHelper {
    private const val PREFS_NAME = "ggc_device_identity_prefs"
    private const val KEY_DEVICE_ID = "unique_device_uuid"
    private var cachedDeviceId: String? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
        if (cachedDeviceId == null) {
            getDeviceId(context)
        }
    }

    fun getAppContext(): Context? = appContext

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context? = null): String {
        cachedDeviceId?.let { return it }

        val ctx = context?.applicationContext ?: appContext
        if (ctx != null) {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            var id = prefs.getString(KEY_DEVICE_ID, null)
            if (id.isNullOrBlank()) {
                // Try Android ID + random UUID fallback for guaranteed uniqueness across installs
                val androidId = try {
                    Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
                } catch (_: Exception) {
                    null
                }
                id = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
                    "DEV_${androidId.uppercase()}"
                } else {
                    "DEV_${UUID.randomUUID().toString().replace("-", "").take(16).uppercase()}"
                }
                prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            }
            cachedDeviceId = id
            return id
        }

        // Fallback in case context was never initialized
        val fallback = "DEV_${UUID.randomUUID().toString().replace("-", "").take(16).uppercase()}"
        cachedDeviceId = fallback
        return fallback
    }

    fun getDeviceDisplayName(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().replaceFirstChar { it.uppercase() }
        val model = Build.MODEL.orEmpty()
        return if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else if (manufacturer.isNotBlank() && model.isNotBlank()) {
            "$manufacturer $model"
        } else if (model.isNotBlank()) {
            model
        } else {
            "Android Device"
        }
    }
}
