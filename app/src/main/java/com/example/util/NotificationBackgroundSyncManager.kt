package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.CollegeContentRemoteDataSource
import com.example.data.datasource.remote.NotificationRemoteDataSource
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.NotificationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background notification sync engine that runs even when the app is closed,
 * swiped away from Recents, or the device is rebooted.
 */
object NotificationBackgroundSyncManager {
    private const val TAG = "NotifBackgroundSync"
    private const val PREFS_NAME = "ggc_background_notification_sync"
    private const val KEY_DELIVERED_IDS = "delivered_notification_ids"
    private const val KEY_LAST_SYNC_TIME = "last_sync_timestamp"
    private const val KEY_IS_INITIALIZED = "sync_is_initialized"

    /**
     * Executes the sync operation: checks Supabase for any new announcements or notifications,
     * verifies user authorization, and triggers system push alerts for newly discovered items.
     */
    suspend fun performSync(context: Context): Int = withContext(Dispatchers.IO) {
        if (!UserProfileManager.isOnboarded(context)) {
            Log.d(TAG, "User not logged in or onboarded. Skipping background sync.")
            return@withContext 0
        }

        UserProfileManager.init(context)
        val userProfile = UserProfileManager.userProfile.value

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = !prefs.getBoolean(KEY_IS_INITIALIZED, false)
        val deliveredIds = (prefs.getStringSet(KEY_DELIVERED_IDS, emptySet()) ?: emptySet()).toMutableSet()

        Log.d(TAG, "Performing background notification sync for user: ${userProfile.name} (${userProfile.appRole.displayName})")

        var newNotifsDispatched = 0
        val pendingNotifications = mutableListOf<AppNotificationDto>()

        try {
            // 1. Fetch from notifications table (or synthesized items)
            val notificationRemoteDS = NotificationRemoteDataSource()
            val notifsResult = notificationRemoteDS.getNotifications(limit = 30, offset = 0)
            if (notifsResult is AuthResult.Success) {
                pendingNotifications.addAll(notifsResult.data)
            }

            // 2. Fetch directly from announcements table to ensure zero dropped announcements
            val contentRemoteDS = CollegeContentRemoteDataSource()
            val announcementsResult = contentRemoteDS.getAnnouncements(includeUnpublished = false)
            if (announcementsResult is AuthResult.Success) {
                announcementsResult.data.forEach { ann ->
                    val notifId = "ann_${ann.id ?: ann.title.hashCode()}"
                    if (pendingNotifications.none { it.id == notifId || it.relatedContentId == ann.id }) {
                        val isPriority = ann.isPinned ||
                                ann.category.contains("urgent", ignoreCase = true) ||
                                ann.category.contains("exam", ignoreCase = true)
                        pendingNotifications.add(
                            AppNotificationDto(
                                id = notifId,
                                notificationType = if (isPriority) NotificationType.ANNOUNCEMENT_PRIORITY.key else NotificationType.ANNOUNCEMENT_NEW.key,
                                title = ann.title,
                                message = ann.content.take(160),
                                relatedContentId = ann.id,
                                contentType = "announcement",
                                departmentId = ann.departmentId,
                                targetRole = "all",
                                isPriority = isPriority,
                                isPinned = ann.isPinned,
                                createdAt = ann.publishedAt ?: ann.createdAt
                            )
                        )
                    }
                }
            }

            // 3. Check for security login transfer requests from other devices
            try {
                val currentDeviceId = DeviceIdentifierHelper.getDeviceId(context)
                val userIdentifier = userProfile.studentRollNumber.takeIf { !it.isNullOrBlank() } ?: userProfile.name
                val transferReq = com.example.data.datasource.remote.ActiveSessionRemoteManager.getPendingTransferRequest(userIdentifier, currentDeviceId)
                if (transferReq != null) {
                    val notifId = "transfer_${transferReq.requestId}"
                    if (!deliveredIds.contains(notifId)) {
                        SystemNotificationHelper.showSystemPushNotification(
                            context = context,
                            notification = AppNotificationDto(
                                id = notifId,
                                notificationType = NotificationType.SECURITY_ALERT.key,
                                title = "Security Alert: Login Request",
                                message = "Someone is trying to log in from ${transferReq.toDeviceName}. Tap to Approve or Reject.",
                                targetRole = "all",
                                priority = 100
                            )
                        )
                        deliveredIds.add(notifId)
                        newNotifsDispatched++
                    }
                }
            } catch (_: Exception) {}

            // 4. Filter notifications authorized for the current user profile
            val authorized = pendingNotifications.filter { it.isAuthorizedFor(userProfile) }

            if (isFirstRun) {
                // On very first sync initialization, mark current existing items as baseline
                val existingIds = authorized.mapNotNull { it.id }
                deliveredIds.addAll(existingIds)
                prefs.edit()
                    .putBoolean(KEY_IS_INITIALIZED, true)
                    .putStringSet(KEY_DELIVERED_IDS, deliveredIds)
                    .putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
                    .apply()
                Log.d(TAG, "Background sync baseline established with ${existingIds.size} notices.")
                return@withContext 0
            }

            // 4. Dispatch system heads-up notifications for all newly arrived items
            for (notification in authorized) {
                val notifId = notification.id ?: continue
                if (notifId !in deliveredIds) {
                    SystemNotificationHelper.showSystemPushNotification(context, notification)
                    deliveredIds.add(notifId)
                    newNotifsDispatched++
                    Log.d(TAG, "Background system push delivered: ${notification.title}")
                }
            }

            // Save updated delivered set & timestamp
            prefs.edit()
                .putStringSet(KEY_DELIVERED_IDS, deliveredIds)
                .putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
                .apply()

        } catch (e: Exception) {
            Log.e(TAG, "Error during background notification sync: ${e.message}", e)
        }

        return@withContext newNotifsDispatched
    }

    /**
     * Marks a notification as already delivered so background poller won't re-alert.
     */
    fun markDelivered(context: Context, notificationId: String) {
        if (notificationId.isBlank()) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deliveredIds = (prefs.getStringSet(KEY_DELIVERED_IDS, emptySet()) ?: emptySet()).toMutableSet()
        deliveredIds.add(notificationId)
        prefs.edit().putStringSet(KEY_DELIVERED_IDS, deliveredIds).apply()
    }

    /**
     * Clears history on user logout.
     */
    fun clearDeliveredHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Log.d(TAG, "Background notification delivered history cleared.")
    }
}
