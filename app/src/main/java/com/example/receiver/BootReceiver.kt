package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.UserProfileManager
import com.example.util.NotificationSyncScheduler

/**
 * Ensures background notification polling automatically resumes when the phone reboots
 * or the application is updated.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d(TAG, "BootReceiver received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            if (UserProfileManager.isOnboarded(context)) {
                Log.d(TAG, "User is logged in. Starting persistent notification background sync.")
                NotificationSyncScheduler.startSync(context)
            }
        }
    }

    companion object {
        private const val TAG = "GgcBootReceiver"
    }
}
