package com.example.ui.screens.admin

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val BrandNavy = Color(0xFF061B52)
private val BrandNavySecondary = Color(0xFF0C235A)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)

@Composable
fun AdminOverviewDashboardView(
    uiState: AdminControlCenterUiState,
    onNavigateSection: (AdminNavSection) -> Unit,
    onRefresh: () -> Unit
) {
    val profile = UserProfileManager.userProfile.value

    // Live dynamic greeting & date
    val currentTime = remember { Calendar.getInstance() }
    val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
    val liveGreeting = when {
        hourOfDay in 4..11 -> "Good Morning"
        hourOfDay in 12..16 -> "Good Afternoon"
        hourOfDay in 17..21 -> "Good Evening"
        else -> "Good Night"
    }
    val liveDayFormat = remember { SimpleDateFormat("EEEE", Locale.ENGLISH) }
    val liveDateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH) }
    val todayDate = remember { Date() }
    val currentDayName = remember { liveDayFormat.format(todayDate) }
    val currentDateFormatted = remember { liveDateFormat.format(todayDate) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("admin_dashboard_scroll"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // 1. Admin Hero Bento Card (Navy Blue + Gold Accents)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("admin_hero_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = liveGreeting,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandGoldLight
                        )

                        Text(
                            text = "$currentDayName, $currentDateFormatted",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Shield",
                                tint = BrandGoldLight,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name.ifBlank { "Super Administrator" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Super Administrator • Central Administration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Status",
                                    tint = BrandGoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Central Governance • Full Access",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = "GGC M.B.Din",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Quick Administrative Actions
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Administrative Actions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Instantly open primary publishing sections",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigateSection(AdminNavSection.CONTENT) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("admin_quick_add_notice"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = BrandGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Notice",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { onNavigateSection(AdminNavSection.ACADEMICS) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("admin_quick_add_outline"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavySecondary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = BrandGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Outline",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { onNavigateSection(AdminNavSection.EVENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("admin_quick_add_event"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Event",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 3. Administrative Bento Grid (Alternating Navy / White Ladder)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Administrative Control Hub",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )

                // Row 1: BS Student Registry (Navy) & Inter Students Registry (White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminBentoCard(
                        title = "BS Students",
                        subtitle = "Registry & Enrollment",
                        icon = Icons.Default.School,
                        isDark = true,
                        badgeText = "BS Program",
                        onClick = { onNavigateSection(AdminNavSection.STUDENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_bs_students")
                    )

                    AdminBentoCard(
                        title = "Inter Students",
                        subtitle = "1st & 2nd Year Records",
                        icon = Icons.Default.Group,
                        isDark = false,
                        badgeText = "Intermediate",
                        onClick = { onNavigateSection(AdminNavSection.STUDENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_inter_students")
                    )
                }

                // Row 2: Faculty Registry (White) & User Roles Governance (Navy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminBentoCard(
                        title = "Faculty Registry",
                        subtitle = "Teachers & Directory",
                        icon = Icons.Default.SupervisorAccount,
                        isDark = false,
                        badgeText = "Faculty",
                        onClick = { onNavigateSection(AdminNavSection.FACULTY) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_faculty")
                    )

                    AdminBentoCard(
                        title = "User & Roles",
                        subtitle = "Admins, HODs & Claims",
                        icon = Icons.Default.AdminPanelSettings,
                        isDark = true,
                        badgeText = "Security",
                        onClick = { onNavigateSection(AdminNavSection.USERS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_users")
                    )
                }

                // Row 3: Academics & Curricula (Navy) & Announcements Hub (White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminBentoCard(
                        title = "Academics",
                        subtitle = "Programs & Outlines",
                        icon = Icons.Default.Book,
                        isDark = true,
                        badgeText = "Curricula",
                        onClick = { onNavigateSection(AdminNavSection.ACADEMICS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_academics")
                    )

                    AdminBentoCard(
                        title = "Announcements",
                        subtitle = "Notices & Circulars",
                        icon = Icons.Default.Campaign,
                        isDark = false,
                        badgeText = "Publish",
                        onClick = { onNavigateSection(AdminNavSection.CONTENT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_announcements")
                    )
                }

                // Row 4: College Events (White) & Official Documents (Navy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminBentoCard(
                        title = "College Events",
                        subtitle = "Seminars & Galas",
                        icon = Icons.Default.Event,
                        isDark = false,
                        badgeText = "Schedule",
                        onClick = { onNavigateSection(AdminNavSection.EVENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_events")
                    )

                    AdminBentoCard(
                        title = "Official Docs",
                        subtitle = "PDFs & Prospectus",
                        icon = Icons.Default.Description,
                        isDark = true,
                        badgeText = "Documents",
                        onClick = { onNavigateSection(AdminNavSection.DOCUMENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_documents")
                    )
                }

                // Row 5: Broadcast Alerts (Navy) & System Settings (White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminBentoCard(
                        title = "Broadcasts",
                        subtitle = "In-App Push Alerts",
                        icon = Icons.Default.NotificationsActive,
                        isDark = true,
                        badgeText = "Alerts",
                        onClick = { onNavigateSection(AdminNavSection.NOTIFICATIONS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_notifications")
                    )

                    AdminBentoCard(
                        title = "System Settings",
                        subtitle = "Governance & Audit",
                        icon = Icons.Default.Settings,
                        isDark = false,
                        badgeText = "System",
                        onClick = { onNavigateSection(AdminNavSection.SETTINGS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_bento_settings")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun AdminBentoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    badgeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(148.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) BrandNavy else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDark) Color(0xFFC59B27).copy(alpha = 0.25f)
                            else BrandIconBadgeBg
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isDark) BrandGoldLight else BrandNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF0F3FA),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color.White.copy(alpha = 0.9f) else BrandNavy,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else BrandNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isDark) Color.White.copy(alpha = 0.75f) else BrandTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
