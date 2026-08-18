package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotificationDto
import com.example.data.model.NotificationType
import kotlinx.coroutines.delay

private val BrandNavy = Color(0xFF061B52)
private val UrgentRed = Color(0xFFD32F2F)
private val AccentGold = Color(0xFFC59B27)

@Composable
fun InAppNotificationBanner(
    notification: AppNotificationDto?,
    onDismiss: () -> Unit,
    onOpenContent: (AppNotificationDto) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(notification) {
        if (notification != null) {
            delay(7000) // Auto-dismiss after 7 seconds
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        if (notification != null) {
            val isUrgent = notification.isPriority || notification.typeEnum == NotificationType.ANNOUNCEMENT_PRIORITY
            val icon: ImageVector = when (notification.typeEnum) {
                NotificationType.ANNOUNCEMENT_PRIORITY -> Icons.Default.Warning
                NotificationType.ANNOUNCEMENT_NEW -> Icons.Outlined.Campaign
                NotificationType.EVENT_NEW, NotificationType.EVENT_UPDATE -> Icons.Default.Event
                NotificationType.DOCUMENT_NEW -> Icons.Outlined.Description
                NotificationType.COURSE_OUTLINE_NEW -> Icons.Outlined.Layers
                NotificationType.PROSPECTUS_NEW -> Icons.AutoMirrored.Outlined.MenuBook
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("in_app_notification_banner")
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenContent(notification) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUrgent) Color(0xFF1E0A0A) else BrandNavy
                    ),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isUrgent) UrgentRed.copy(alpha = 0.25f) else AccentGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Notification Icon",
                                        tint = if (isUrgent) Color(0xFFFF8A80) else AccentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

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
                                            color = if (isUrgent) Color(0xFFFF8A80) else AccentGold
                                        )
                                    }

                                    Text(
                                        text = notification.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("notif_banner_close_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = notification.message,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onOpenContent(notification) },
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("notif_banner_view_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isUrgent) UrgentRed else AccentGold,
                                    contentColor = if (isUrgent) Color.White else BrandNavy
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "View Now",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
