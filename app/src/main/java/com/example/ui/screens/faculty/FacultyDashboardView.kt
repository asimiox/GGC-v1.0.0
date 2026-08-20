package com.example.ui.screens.faculty

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.screens.admin.content.ContentSectionTab
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
fun FacultyDashboardView(
    userProfile: UserProfile,
    unreadCount: Int,
    onNavigateToContentTab: (ContentSectionTab) -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotificationCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Live greeting & date
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
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("faculty_dashboard_scroll"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Official Faculty Header Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToProfile() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                        contentDescription = "GGC Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("faculty_header_logo")
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "GGC M.B.Din",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandGold)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Faculty & Academic Portal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = BrandGold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile button
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.testTag("faculty_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Faculty Profile",
                            tint = BrandNavy,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Notification Bell
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = onNavigateToNotificationCenter,
                            modifier = Modifier.testTag("faculty_notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = BrandNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD32F2F))
                                    .testTag("faculty_bell_unread_badge"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
        }

        // 2. Faculty Hero Card & Teaching Profile
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onNavigateToProfile() }
                        .testTag("faculty_hero_card"),
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

                        Spacer(modifier = Modifier.height(10.dp))

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
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Faculty Member",
                                    tint = BrandGoldLight,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userProfile.name.ifBlank { "Faculty Member" },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                val designationText = userProfile.designation ?: "Assistant Professor / Lecturer"
                                val departmentText = userProfile.department ?: "Department of IT & Academics"

                                Text(
                                    text = "$designationText • $departmentText",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verification and affiliation badge
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
                                        contentDescription = "Verified Staff",
                                        tint = BrandGoldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official Faculty Staff • Verified",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Profile",
                                tint = BrandGoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Content Creation Action Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Quick Publishing Actions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Create authorized academic content with one tap",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // + Announcement
                    Button(
                        onClick = { onNavigateToContentTab(ContentSectionTab.ANNOUNCEMENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_action_add_notice"),
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

                    // + Course Outline / Notes
                    Button(
                        onClick = { onNavigateToContentTab(ContentSectionTab.COURSE_OUTLINES) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_action_add_outline"),
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

                    // + Event
                    Button(
                        onClick = { onNavigateToContentTab(ContentSectionTab.EVENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_action_add_event"),
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
            }
        }

        // 4. Dedicated Faculty Management Bento Grid (Alternating Navy / White Ladder)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Faculty Management Hub",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )

                // Row 1: My Courses (Navy) & Create Announcement (White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Card 1: My Courses & Syllabi (Navy)
                    FacultyBentoCard(
                        title = "My Courses",
                        subtitle = "Syllabi & Outlines",
                        icon = Icons.Default.AutoStories,
                        isDark = true,
                        badgeText = "Curriculum",
                        onClick = onNavigateToCourses,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_courses")
                    )

                    // Card 2: Create Announcement (White)
                    FacultyBentoCard(
                        title = "Announcements",
                        subtitle = "Post & Edit Notices",
                        icon = Icons.Default.Campaign,
                        isDark = false,
                        badgeText = "Publish",
                        onClick = { onNavigateToContentTab(ContentSectionTab.ANNOUNCEMENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_announcements")
                    )
                }

                // Row 2: Manage Notes & Materials (White) & College Events (Navy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Card 3: Manage Notes & Study Materials (White)
                    FacultyBentoCard(
                        title = "Course Notes",
                        subtitle = "Lecture Slides & PDFs",
                        icon = Icons.Default.UploadFile,
                        isDark = false,
                        badgeText = "Materials",
                        onClick = { onNavigateToContentTab(ContentSectionTab.COURSE_OUTLINES) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_notes")
                    )

                    // Card 4: Academic Events & Seminars (Navy)
                    FacultyBentoCard(
                        title = "College Events",
                        subtitle = "Seminars & Workshops",
                        icon = Icons.Default.Event,
                        isDark = true,
                        badgeText = "Organize",
                        onClick = { onNavigateToContentTab(ContentSectionTab.EVENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_events")
                    )
                }

                // Row 3: Academic Resources (Navy) & My Faculty Profile (White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Card 5: Official Documents & Resources (Navy)
                    FacultyBentoCard(
                        title = "Official Docs",
                        subtitle = "Rules & Guidelines",
                        icon = Icons.Default.Description,
                        isDark = true,
                        badgeText = "Registry",
                        onClick = { onNavigateToContentTab(ContentSectionTab.DOCUMENTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_documents")
                    )

                    // Card 6: My Profile & Settings (White)
                    FacultyBentoCard(
                        title = "My Profile",
                        subtitle = "Staff Credentials",
                        icon = Icons.Default.Badge,
                        isDark = false,
                        badgeText = "Faculty ID",
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faculty_card_profile")
                    )
                }
            }
        }

        // 5. Full-Width Content Management Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigateToContentTab(ContentSectionTab.ANNOUNCEMENTS) }
                        .testTag("faculty_open_content_hub_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFC59B27).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Content Hub",
                                tint = BrandGoldLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Content Management Hub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Create, edit, delete & publish department content",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BrandGoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FacultyBentoCard(
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
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
