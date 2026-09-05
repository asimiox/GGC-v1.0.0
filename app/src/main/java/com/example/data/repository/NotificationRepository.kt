package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.NotificationRemoteDataSource
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.UserProfile
import com.example.util.SystemNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Clean Architecture repository managing official Realtime and In-App Notifications
 * for Govt. Graduate College Mandi Bahauddin.
 */
class NotificationRepository private constructor(
    private val context: Context,
    private val remoteDataSource: NotificationRemoteDataSource = NotificationRemoteDataSource.getInstance()
) {
    private val TAG = "NotificationRepository"
    private val PREFS_NAME = "ggc_notifications_prefs"
    private val KEY_READ_IDS = "read_notification_ids"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val _notifications = MutableStateFlow<List<AppNotificationDto>>(emptyList())
    val notifications: StateFlow<List<AppNotificationDto>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _incomingNotification = MutableSharedFlow<AppNotificationDto>(extraBufferCapacity = 16)
    val incomingNotification: SharedFlow<AppNotificationDto> = _incomingNotification.asSharedFlow()

    private val _isRealtimeActive = MutableStateFlow(false)
    val isRealtimeActive: StateFlow<Boolean> = _isRealtimeActive.asStateFlow()

    private var currentUserProfile: UserProfile? = null
    private var isSubscribed = false

    init {
        // Collect incoming realtime notifications
        scope.launch {
            remoteDataSource.incomingNotifications.collect { incoming ->
                handleIncomingRealtimeNotification(incoming)
            }
        }
    }

    /**
     * Start the Supabase Realtime subscription for authenticated user.
     */
    fun startRealtimeSubscription(userProfile: UserProfile) {
        currentUserProfile = userProfile
        if (!isSubscribed) {
            isSubscribed = true
            _isRealtimeActive.value = true
            remoteDataSource.startRealtimeSubscription()
            Log.d(TAG, "Realtime subscription initiated for ${userProfile.name} (${userProfile.appRole.displayName})")
        }
    }

    /**
     * Stop Realtime subscription on logout or session tear down.
     */
    fun stopRealtimeSubscription() {
        if (isSubscribed) {
            isSubscribed = false
            _isRealtimeActive.value = false
            remoteDataSource.stopRealtimeSubscription()
            currentUserProfile = null
            Log.d(TAG, "Realtime subscription stopped cleanly.")
        }
    }

    /**
     * Loads notifications for the current authenticated user with pagination and authorization filtering.
     */
    suspend fun fetchNotifications(
        userProfile: UserProfile,
        page: Int = 0,
        pageSize: Int = 30,
        forceRefresh: Boolean = false
    ): AuthResult<List<AppNotificationDto>> {
        currentUserProfile = userProfile
        val offset = page * pageSize

        val result = remoteDataSource.getNotifications(limit = pageSize, offset = offset)
        return when (result) {
            is AuthResult.Success -> {
                val readIds = getReadNotificationIds()
                val authorizedList = result.data
                    .filter { it.isAuthorizedFor(userProfile) }
                    .map { notif ->
                        val notifId = notif.id ?: ""
                        notif.copy(isRead = readIds.contains(notifId) || notif.isRead)
                    }

                mutex.withLock {
                    if (page == 0 || forceRefresh) {
                        _notifications.value = authorizedList
                    } else {
                        // Append without duplicates
                        val existingIds = _notifications.value.mapNotNull { it.id }.toSet()
                        val uniqueNew = authorizedList.filter { it.id !in existingIds }
                        _notifications.value = _notifications.value + uniqueNew
                    }
                    updateUnreadCount()
                }
                AuthResult.Success(_notifications.value)
            }
            is AuthResult.Error -> {
                Log.e(TAG, "Error fetching notifications: ${result.message}")
                AuthResult.Error(result.message)
            }
        }
    }

    /**
     * Marks a single notification as read.
     */
    fun markAsRead(notificationId: String) {
        if (notificationId.isBlank()) return

        val readIds = getReadNotificationIds().toMutableSet()
        readIds.add(notificationId)
        saveReadNotificationIds(readIds)

        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
        updateUnreadCount()
    }

    /**
     * Marks all currently loaded notifications as read.
     */
    fun markAllAsRead() {
        val readIds = getReadNotificationIds().toMutableSet()
        _notifications.value.forEach { notif ->
            notif.id?.let { readIds.add(it) }
        }
        saveReadNotificationIds(readIds)

        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        updateUnreadCount()
    }

    /**
     * Dispatches a new notification to Supabase and triggers local Realtime broadcast.
     */
    suspend fun dispatchNotification(notification: AppNotificationDto): AuthResult<AppNotificationDto> {
        val res = remoteDataSource.insertNotification(notification)
        // Also trigger locally immediately for the publisher/active session
        handleIncomingRealtimeNotification(notification)
        return res
    }

    private fun handleIncomingRealtimeNotification(incoming: AppNotificationDto) {
        val profile = currentUserProfile ?: UserProfileManager.userProfile.value

        // 1. Check authorization
        if (!incoming.isAuthorizedFor(profile)) {
            Log.d(TAG, "Incoming notification '${incoming.title}' ignored (not authorized for current user)")
            return
        }

        // 2. Check if already exists
        val currentList = _notifications.value
        val alreadyExists = currentList.any { it.id == incoming.id || (it.title == incoming.title && it.relatedContentId == incoming.relatedContentId) }
        if (alreadyExists) {
            return
        }

        // 3. Mark as unread initially
        val readIds = getReadNotificationIds()
        val notifWithState = incoming.copy(isRead = incoming.id in readIds)

        // 4. Prepend to current notifications
        _notifications.value = listOf(notifWithState) + currentList
        updateUnreadCount()

        // 5. Emit to incoming flow for in-app alert banner
        _incomingNotification.tryEmit(notifWithState)
        Log.d(TAG, "Realtime notification displayed: ${incoming.title}")

        // 6. SYSTEM PUSH NOTIFICATION (WhatsApp-like alert: sound + vibration + heads-up banner)
        if (!notifWithState.isRead) {
            SystemNotificationHelper.showSystemPushNotification(context, notifWithState)
            notifWithState.id?.let { com.example.util.NotificationBackgroundSyncManager.markDelivered(context, it) }
        }
    }

    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }

    private fun getReadNotificationIds(): Set<String> {
        return prefs.getStringSet(KEY_READ_IDS, emptySet()) ?: emptySet()
    }

    private fun saveReadNotificationIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_READ_IDS, ids).apply()
    }

    companion object {
        @Volatile
        private var instance: NotificationRepository? = null

        fun getInstance(context: Context): NotificationRepository {
            return instance ?: synchronized(this) {
                instance ?: NotificationRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
