package com.example.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.example.data.UserProfileManager
import com.example.util.NotificationBackgroundSyncManager
import com.example.util.NotificationSyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * System JobService invoked by Android JobScheduler to ensure periodic notification checks
 * persist even when the app is swiped away from Recents and devices enter deep idle states.
 */
class NotificationJobService : JobService() {
    private var syncJob: Job? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "NotificationJobService started by OS.")

        if (!UserProfileManager.isOnboarded(applicationContext)) {
            Log.d(TAG, "User logged out. Cancelling background sync.")
            NotificationSyncScheduler.stopSync(applicationContext)
            return false
        }

        syncJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                NotificationBackgroundSyncManager.performSync(applicationContext)
                // Ensure alarm manager is also re-armed
                NotificationSyncScheduler.scheduleNextAlarm(applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "JobService execution error: ${e.message}", e)
            } finally {
                jobFinished(params, false)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        syncJob?.cancel()
        return true // Reschedule if aborted prematurely
    }

    companion object {
        private const val TAG = "NotifJobService"
        const val JOB_ID = 90210
    }
}
