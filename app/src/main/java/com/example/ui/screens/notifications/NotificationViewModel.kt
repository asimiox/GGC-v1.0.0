package com.example.ui.screens.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserProfileManager
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.NotificationType
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationFilter(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    URGENT("Urgent"),
    NOTICES("Notices"),
    EVENTS("Events"),
    DOCUMENTS("Documents"),
    OUTLINES("Outlines"),
    PROSPECTUS("Prospectus")
}

data class NotificationUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val notifications: List<AppNotificationDto> = emptyList(),
    val filteredNotifications: List<AppNotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val error: String? = null,
    val isRealtimeConnected: Boolean = false,
    val inAppBanner: AppNotificationDto? = null,
    val page: Int = 0,
    val canLoadMore: Boolean = true
)

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotificationRepository.getInstance(application)

    private val _uiState = MutableStateFlow(NotificationUiState(isLoading = true))
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        // Collect notifications flow from repository
        viewModelScope.launch {
            repository.notifications.collect { list ->
                _uiState.update { state ->
                    state.copy(
                        notifications = list,
                        filteredNotifications = filterList(list, state.selectedFilter)
                    )
                }
            }
        }

        // Collect unread count
        viewModelScope.launch {
            repository.unreadCount.collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }

        // Collect realtime active state
        viewModelScope.launch {
            repository.isRealtimeActive.collect { active ->
                _uiState.update { it.copy(isRealtimeConnected = active) }
            }
        }

        // Collect incoming realtime notifications for in-app alert banner
        viewModelScope.launch {
            repository.incomingNotification.collect { incoming ->
                _uiState.update { it.copy(inAppBanner = incoming) }
            }
        }

        // Start subscription and fetch initial list
        val userProfile = UserProfileManager.userProfile.value
        repository.startRealtimeSubscription(userProfile)
        loadNotifications(forceRefresh = true)
    }

    fun loadNotifications(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null, page = 0) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            val userProfile = UserProfileManager.userProfile.value
            val result = repository.fetchNotifications(
                userProfile = userProfile,
                page = 0,
                pageSize = 30,
                forceRefresh = forceRefresh
            )

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            page = 0,
                            canLoadMore = result.data.size >= 30
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoading || !_uiState.value.canLoadMore) return

        viewModelScope.launch {
            val nextPage = _uiState.value.page + 1
            val userProfile = UserProfileManager.userProfile.value
            val result = repository.fetchNotifications(
                userProfile = userProfile,
                page = nextPage,
                pageSize = 30,
                forceRefresh = false
            )

            if (result is AuthResult.Success) {
                _uiState.update {
                    it.copy(
                        page = nextPage,
                        canLoadMore = result.data.size >= 30 * (nextPage + 1)
                    )
                }
            }
        }
    }

    fun setFilter(filter: NotificationFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredNotifications = filterList(state.notifications, filter)
            )
        }
    }

    fun markAsRead(notificationId: String) {
        repository.markAsRead(notificationId)
    }

    fun markAllAsRead() {
        repository.markAllAsRead()
    }

    fun dismissInAppBanner() {
        _uiState.update { it.copy(inAppBanner = null) }
    }

    private fun filterList(
        list: List<AppNotificationDto>,
        filter: NotificationFilter
    ): List<AppNotificationDto> {
        return when (filter) {
            NotificationFilter.ALL -> list
            NotificationFilter.UNREAD -> list.filter { !it.isRead }
            NotificationFilter.URGENT -> list.filter { it.isPriority || it.typeEnum == NotificationType.ANNOUNCEMENT_PRIORITY }
            NotificationFilter.NOTICES -> list.filter {
                it.typeEnum == NotificationType.ANNOUNCEMENT_NEW ||
                it.typeEnum == NotificationType.ANNOUNCEMENT_PRIORITY ||
                it.contentType == "announcement"
            }
            NotificationFilter.EVENTS -> list.filter {
                it.typeEnum == NotificationType.EVENT_NEW ||
                it.typeEnum == NotificationType.EVENT_UPDATE ||
                it.contentType == "event"
            }
            NotificationFilter.DOCUMENTS -> list.filter {
                it.typeEnum == NotificationType.DOCUMENT_NEW || it.contentType == "document"
            }
            NotificationFilter.OUTLINES -> list.filter {
                it.typeEnum == NotificationType.COURSE_OUTLINE_NEW || it.contentType == "course_outline"
            }
            NotificationFilter.PROSPECTUS -> list.filter {
                it.typeEnum == NotificationType.PROSPECTUS_NEW || it.contentType == "prospectus"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Repository lifecycle is managed globally, but ViewModel cleans up its references
    }
}
