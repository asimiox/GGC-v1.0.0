package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.UserProfileManager
import com.example.util.NotificationBackgroundSyncManager
import com.example.util.NotificationSyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Persistent Foreground Service that keeps the college notification sync engine alive
 * even when the app is swiped away from recent apps (recents task list), closed,
 * or placed under strict OEM battery saver rules.
 */
class NotificationSyncForegroundService : Service() {

    private var workerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationSyncForegroundService created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "NotificationSyncForegroundService onStartCommand received.")

        // If user is not logged in, stop immediately
        if (!UserProfileManager.isOnboarded(applicationContext)) {
            Log.d(TAG, "User is not logged in. Stopping foreground sync service.")
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = createForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FOREGROUND_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }

        // Launch persistent polling coroutine if not already running
        if (workerJob == null || workerJob?.isActive != true) {
            workerJob = scope.launch {
                Log.d(TAG, "Persistent background sync polling loop started.")
                var iteration = 0
                while (isActive) {
                    try {
                        if (!UserProfileManager.isOnboarded(applicationContext)) {
                            Log.d(TAG, "User logged out during loop. Stopping service.")
                            stopSelf()
                            break
                        }

                        val dispatched = NotificationBackgroundSyncManager.performSync(applicationContext)
                        if (dispatched > 0) {
                            Log.d(TAG, "Foreground service sync loop dispatched $dispatched new notification(s).")
                        }

                        // Also re-arm AlarmManager & JobScheduler as secondary safety nets
                        if (iteration % 5 == 0) {
                            NotificationSyncScheduler.scheduleNextAlarm(applicationContext)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in foreground sync loop: ${e.message}", e)
                    }

                    iteration++
                    // Check every 25 seconds for snappy, instant notification delivery
                    delay(25_000L)
                }
            }
        }

        return START_STICKY
    }

    /**
     * Called by the system if the user swiped the app away from recent tasks.
     * We ensure the service remains alive or restarts immediately!
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved called - user swiped app away from Recent Apps!")

        if (UserProfileManager.isOnboarded(applicationContext)) {
            // Re-arm AlarmManager immediately for 5 seconds from now
            NotificationSyncScheduler.scheduleImmediateWakeup(applicationContext)

            // Explicitly restart this foreground service via pending intent or startService
            val restartIntent = Intent(applicationContext, NotificationSyncForegroundService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(applicationContext, restartIntent)
                } else {
                    startService(restartIntent)
                }
            } catch (e: Exception) {
                Log.w(TAG, "onTaskRemoved restart attempt note: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workerJob?.cancel()
        workerJob = null
        Log.d(TAG, "NotificationSyncForegroundService destroyed.")
    }

    private fun createForegroundNotification(): Notification {
        initServiceChannel()

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            FOREGROUND_PENDING_INTENT_REQ,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_ggc_logo)
            .setContentTitle("GGC Notice & Alert Service")
            .setContentText("Monitoring college notices & instant official updates")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF061B52.toInt())
            .build()
    }

    private fun initServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GGC Notification Sync Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps notice delivery active when the app is closed or in background"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "NotifSyncFgService"
        const val CHANNEL_ID = "ggc_sync_service_status_channel"
        const val FOREGROUND_NOTIFICATION_ID = 44099
        private const val FOREGROUND_PENDING_INTENT_REQ = 44098

        fun start(context: Context) {
            if (!UserProfileManager.isOnboarded(context)) return
            val intent = Intent(context, NotificationSyncForegroundService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
                Log.d(TAG, "Requested start of NotificationSyncForegroundService.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start NotificationSyncForegroundService: ${e.message}", e)
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, NotificationSyncForegroundService::class.java)
                context.stopService(intent)
                Log.d(TAG, "Requested stop of NotificationSyncForegroundService.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop NotificationSyncForegroundService: ${e.message}", e)
            }
        }
    }
}
