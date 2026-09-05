package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.example.receiver.NotificationSyncReceiver
import com.example.service.NotificationJobService

/**
 * Schedules recurring background tasks to check for new college notices and announcements
 * even when the app is completely closed or swiped away from Recents.
 */
object NotificationSyncScheduler {
    private const val TAG = "NotifSyncScheduler"
    private const val REQUEST_CODE = 44021
    private const val ALARM_INTERVAL_MS = 60_000L // 60 seconds interval for prompt delivery

    /**
     * Starts background sync by queueing the AlarmManager and registering the JobScheduler task.
     */
    fun startSync(context: Context) {
        Log.d(TAG, "Starting persistent background notification sync engines...")
        scheduleNextAlarm(context)
        scheduleJobService(context)
    }

    /**
     * Schedules the next AlarmManager wakeup. Works in Doze mode via setAndAllowWhileIdle.
     */
    fun scheduleNextAlarm(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, NotificationSyncReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAt = SystemClock.elapsedRealtime() + ALARM_INTERVAL_MS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                    }
                } catch (se: SecurityException) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                }
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
            }
            Log.d(TAG, "Next background sync alarm set for +${ALARM_INTERVAL_MS / 1000}s.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm: ${e.message}", e)
        }
    }

    /**
     * Registers a persistent periodic JobScheduler job (runs every 15 minutes, survives reboots).
     */
    private fun scheduleJobService(context: Context) {
        try {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as? JobScheduler ?: return
            val componentName = ComponentName(context, NotificationJobService::class.java)

            val builder = JobInfo.Builder(NotificationJobService.JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000L) // 15 mins periodic standard
                .setPersisted(true)

            val result = jobScheduler.schedule(builder.build())
            Log.d(TAG, "Registered JobScheduler periodic job. Code: $result")
        } catch (e: Exception) {
            Log.w(TAG, "Note on JobScheduler setup: ${e.message}")
        }
    }

    /**
     * Cancels background sync on user logout.
     */
    fun stopSync(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, NotificationSyncReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as? JobScheduler
            jobScheduler?.cancel(NotificationJobService.JOB_ID)
            Log.d(TAG, "Background sync cancelled.")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sync: ${e.message}", e)
        }
    }
}
