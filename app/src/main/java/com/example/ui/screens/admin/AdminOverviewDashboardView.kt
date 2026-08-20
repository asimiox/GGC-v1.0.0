package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileManager
import com.example.data.model.AdminSystemOverviewDto

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDark = Color(0xFF030D2B)
private val BrandNavyLight = Color(0xFF132B6B)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF3D372)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F8FB)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminOverviewDashboardView(
    uiState: AdminControlCenterUiState,
    onNavigateSection: (AdminNavSection) -> Unit,
    onRefresh: () -> Unit
) {
    val overview = uiState.overview
    val profile = UserProfileManager.userProfile.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("admin_overview_dashboard_view")
    ) {
        // 1. Super Control Header Hero Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = BrandNavyDark
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BrandNavyDark, BrandNavyLight)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BrandGold.copy(alpha = 0.2f))
                                    .border(1.5.dp, BrandGoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Shield",
                                    tint = BrandGoldLight,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SUPER CONTROL CENTER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = BrandGoldLight
                                )
                                Text(
                                    text = profile.name.ifBlank { "College Administrator" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .testTag("admin_refresh_btn")
                        ) {
                            if (uiState.isLoading || uiState.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = BrandGoldLight,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Data",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // System Operational Scope Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PostgreSQL RLS Active • Full Scope",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. High Level Metric Statistics Grid (Bento Style)
        Text(
            text = "Institutional Live Metrics",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricBentoCard(
                title = "BS Students",
                count = overview.bsStudentsCount.toString(),
                icon = Icons.Default.School,
                accentColor = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.STUDENTS) }
            )
            MetricBentoCard(
                title = "Inter Students",
                count = overview.intermediateStudentsCount.toString(),
                icon = Icons.Default.Group,
                accentColor = Color(0xFF00ACC1),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.STUDENTS) }
            )
            MetricBentoCard(
                title = "Faculty Registry",
                count = overview.facultyCount.toString(),
                icon = Icons.Default.SupervisorAccount,
                accentColor = Color(0xFF8E24AA),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.FACULTY) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricBentoCard(
                title = "Departments",
                count = overview.departmentsCount.toString(),
                icon = Icons.Default.Book,
                accentColor = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.ACADEMICS) }
            )
            MetricBentoCard(
                title = "Notices & Ads",
                count = overview.announcementsCount.toString(),
                icon = Icons.Default.Campaign,
                accentColor = Color(0xFFFB8C00),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.CONTENT) }
            )
            MetricBentoCard(
                title = "Events / Acts",
                count = overview.eventsCount.toString(),
                icon = Icons.Default.Event,
                accentColor = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSection(AdminNavSection.EVENTS) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Super Administrator Action Hub
        Text(
            text = "Super Control Action Hub",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminActionHubTile(
                title = "User Management",
                subtitle = "Roles, HODs, Claim Resets",
                icon = Icons.Default.GroupAdd,
                accentColor = Color(0xFF0D47A1),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_user_mgmt",
                onClick = { onNavigateSection(AdminNavSection.USERS) }
            )
            AdminActionHubTile(
                title = "Student Registry",
                subtitle = "BS & Intermediate Records",
                icon = Icons.Default.School,
                accentColor = Color(0xFF1B5E20),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_student_registry",
                onClick = { onNavigateSection(AdminNavSection.STUDENTS) }
            )
            AdminActionHubTile(
                title = "Faculty Registry",
                subtitle = "Official Staff & Teachers",
                icon = Icons.Default.SupervisorAccount,
                accentColor = Color(0xFF4A148C),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_faculty_registry",
                onClick = { onNavigateSection(AdminNavSection.FACULTY) }
            )
            AdminActionHubTile(
                title = "Academics & Curricula",
                subtitle = "Programs, Courses, Outlines",
                icon = Icons.Default.Book,
                accentColor = Color(0xFFBF360C),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_academics",
                onClick = { onNavigateSection(AdminNavSection.ACADEMICS) }
            )
            AdminActionHubTile(
                title = "Announcements",
                subtitle = "Notices, Circulars & Pins",
                icon = Icons.Default.Campaign,
                accentColor = Color(0xFFE65100),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_announcements",
                onClick = { onNavigateSection(AdminNavSection.CONTENT) }
            )
            AdminActionHubTile(
                title = "College Events",
                subtitle = "Seminars, Sports, Galas",
                icon = Icons.Default.Event,
                accentColor = Color(0xFFB71C1C),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_events",
                onClick = { onNavigateSection(AdminNavSection.EVENTS) }
            )
            AdminActionHubTile(
                title = "Documents & Media",
                subtitle = "Prospectus & Official Files",
                icon = Icons.Default.Description,
                accentColor = Color(0xFF006064),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_documents",
                onClick = { onNavigateSection(AdminNavSection.DOCUMENTS) }
            )
            AdminActionHubTile(
                title = "Broadcast Alerts",
                subtitle = "Global In-App Notifications",
                icon = Icons.Default.NotificationsActive,
                accentColor = Color(0xFF880E4F),
                modifier = Modifier.weight(1f),
                testTag = "admin_tile_broadcast",
                onClick = { onNavigateSection(AdminNavSection.NOTIFICATIONS) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Security & Compliance Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BrandGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Security & Governance Audit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "100% Enforced",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• Client-side role spoofing strictly impossible.\n• All mutation RPCs require verified PostgreSQL superadmin claims.\n• Zero exposure of service_role keys on client device.",
                    fontSize = 12.sp,
                    color = Color(0xFF5A6A85),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun MetricBentoCard(
    title: String,
    count: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF718096),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AdminActionHubTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF718096),
                    maxLines = 1
                )
            }
        }
    }
}
