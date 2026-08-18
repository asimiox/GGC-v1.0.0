package com.example.ui.screens.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppNotificationDto
import com.example.data.model.NotificationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val AccentGold = Color(0xFFC59B27)
private val UrgentRed = Color(0xFFD32F2F)
private val BrandTextMuted = Color(0xFF7A879D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    onBack: () -> Unit = {},
    onNavigateToContent: (contentType: String, contentId: String?) -> Unit = { _, _ -> },
    viewModel: NotificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Pagination trigger
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.canLoadMore && !uiState.isLoading) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notifications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )

                        if (uiState.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(UrgentRed)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .testTag("notif_unread_badge")
                            ) {
                                Text(
                                    text = "${uiState.unreadCount} New",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Realtime pulse indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E7D32).copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("notif_live_indicator")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Live",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("notif_center_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandNavy
                        )
                    }
                },
                actions = {
                    if (uiState.unreadCount > 0) {
                        IconButton(
                            onClick = { viewModel.markAllAsRead() },
                            modifier = Modifier.testTag("notif_mark_all_read_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all as read",
                                tint = AccentGold
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.loadNotifications(forceRefresh = true) },
                        modifier = Modifier.testTag("notif_center_refresh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = BrandNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("notification_center_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandBackground)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(NotificationFilter.entries) { filter ->
                    val isSelected = uiState.selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = when (filter) {
                                    NotificationFilter.ALL -> "All (${uiState.notifications.size})"
                                    NotificationFilter.UNREAD -> "Unread (${uiState.unreadCount})"
                                    else -> filter.label
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandNavy,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF0F2F6),
                            labelColor = BrandNavy
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("notif_filter_${filter.name.lowercase()}")
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

            // Content Area
            when {
                uiState.isLoading && uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = BrandNavy,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Connecting to official feed...",
                                fontSize = 13.sp,
                                color = BrandTextMuted
                            )
                        }
                    }
                }

                uiState.error != null && uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = UrgentRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Unable to load notifications",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = uiState.error ?: "Network connection error",
                                fontSize = 13.sp,
                                color = BrandTextMuted,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.loadNotifications(forceRefresh = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("notif_error_retry_btn")
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                uiState.filteredNotifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsActive,
                                    contentDescription = "No Notifications",
                                    tint = AccentGold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = if (uiState.selectedFilter == NotificationFilter.UNREAD) "All Caught Up!" else "No Notifications",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            Text(
                                text = if (uiState.selectedFilter == NotificationFilter.UNREAD)
                                    "You have read all official college notices and circulars."
                                else
                                    "No notifications in this category yet. Official updates will appear in real time.",
                                fontSize = 13.sp,
                                color = BrandTextMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("notif_list_container"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredNotifications,
                            key = { it.id ?: it.title.hashCode().toString() }
                        ) { notification ->
                            NotificationCardItem(
                                notification = notification,
                                onMarkAsRead = {
                                    notification.id?.let { id -> viewModel.markAsRead(id) }
                                },
                                onCardClick = {
                                    notification.id?.let { id -> viewModel.markAsRead(id) }
                                    val contentType = notification.contentType ?: "announcement"
                                    onNavigateToContent(contentType, notification.relatedContentId)
                                }
                            )
                        }

                        if (uiState.isLoading && uiState.notifications.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = BrandNavy,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCardItem(
    notification: AppNotificationDto,
    onMarkAsRead: () -> Unit,
    onCardClick: () -> Unit
) {
    val isUrgent = notification.isPriority || notification.typeEnum == NotificationType.ANNOUNCEMENT_PRIORITY
    val isUnread = !notification.isRead

    val icon: ImageVector = when (notification.typeEnum) {
        NotificationType.ANNOUNCEMENT_PRIORITY -> Icons.Default.Warning
        NotificationType.ANNOUNCEMENT_NEW -> Icons.Outlined.Campaign
        NotificationType.EVENT_NEW, NotificationType.EVENT_UPDATE -> Icons.Default.Event
        NotificationType.DOCUMENT_NEW -> Icons.Outlined.Description
        NotificationType.COURSE_OUTLINE_NEW -> Icons.Outlined.Layers
        NotificationType.PROSPECTUS_NEW -> Icons.AutoMirrored.Outlined.MenuBook
    }

    val iconContainerColor = when {
        isUrgent -> UrgentRed.copy(alpha = 0.12f)
        notification.typeEnum == NotificationType.EVENT_NEW -> Color(0xFF00897B).copy(alpha = 0.12f)
        notification.typeEnum == NotificationType.DOCUMENT_NEW -> Color(0xFF1976D2).copy(alpha = 0.12f)
        notification.typeEnum == NotificationType.COURSE_OUTLINE_NEW -> Color(0xFF7B1FA2).copy(alpha = 0.12f)
        else -> BrandNavy.copy(alpha = 0.1f)
    }

    val iconTint = when {
        isUrgent -> UrgentRed
        notification.typeEnum == NotificationType.EVENT_NEW -> Color(0xFF00897B)
        notification.typeEnum == NotificationType.DOCUMENT_NEW -> Color(0xFF1976D2)
        notification.typeEnum == NotificationType.COURSE_OUTLINE_NEW -> Color(0xFF7B1FA2)
        else -> BrandNavy
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("notif_item_${notification.id ?: "item"}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) Color.White else Color(0xFFFCFCFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnread) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconContainerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = notification.typeEnum.displayName,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isUrgent) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(UrgentRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "URGENT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Text(
                                text = notification.typeEnum.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = iconTint
                            )

                            if (!notification.departmentName.isNullOrBlank()) {
                                Text(
                                    text = " • ${notification.departmentName}",
                                    fontSize = 11.sp,
                                    color = BrandTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Text(
                            text = notification.title,
                            fontSize = 15.sp,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            color = BrandNavy,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentGold)
                            .testTag("notif_unread_dot")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.message,
                fontSize = 13.sp,
                color = if (isUnread) Color(0xFF333333) else BrandTextMuted,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRelativeTime(notification.createdAt),
                    fontSize = 11.sp,
                    color = BrandTextMuted
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isUnread) {
                        TextButton(
                            onClick = onMarkAsRead,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Mark as read",
                                tint = AccentGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mark read",
                                fontSize = 11.sp,
                                color = AccentGold,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = "View details →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

private fun formatRelativeTime(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Recent"
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val cleanDate = dateString.substringBefore("Z").substringBefore("+")
        val date = sdf.parse(cleanDate) ?: Date()
        val diffMillis = System.currentTimeMillis() - date.time

        val minutes = diffMillis / (1000 * 60)
        val hours = diffMillis / (1000 * 60 * 60)
        val days = diffMillis / (1000 * 60 * 60 * 24)

        when {
            minutes < 2 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("dd MMM, yyyy", Locale.US).format(date)
        }
    } catch (_: Exception) {
        dateString.take(10)
    }
}
