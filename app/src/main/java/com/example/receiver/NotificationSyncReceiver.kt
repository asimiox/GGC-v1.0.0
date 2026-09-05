package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.UserProfileManager
import com.example.util.NotificationBackgroundSyncManager
import com.example.util.NotificationSyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver triggered by AlarmManager to check for new posts/notices
 * when the app is swiped away from Recents or closed.
 */
class NotificationSyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "NotificationSyncReceiver alarm triggered.")

        if (!UserProfileManager.isOnboarded(context)) {
            Log.d(TAG, "User is not logged in. Halting background sync.")
            NotificationSyncScheduler.stopSync(context)
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dispatched = NotificationBackgroundSyncManager.performSync(context)
                Log.d(TAG, "Background sync finished. Dispatched $dispatched notifications.")
            } catch (e: Exception) {
                Log.e(TAG, "Error in NotificationSyncReceiver: ${e.message}", e)
            } finally {
                // Re-arm next alarm so sync keeps running perpetually
                NotificationSyncScheduler.scheduleNextAlarm(context)
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "NotifSyncReceiver"
    }
}
