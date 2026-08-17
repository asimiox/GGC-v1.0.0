package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.R
import com.example.data.UserProfileManager
import com.example.data.model.AppRole
import com.example.ui.screens.courses.CoursesOutlineScreen
import com.example.ui.screens.documents.OfficialDocumentsScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.notices.NoticesScreen
import com.example.ui.screens.prospectus.ProspectusScreen

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)

enum class HomeSubScreen {
    NONE,
    FEE_STRUCTURE,
    ANNOUNCEMENT,
    PROSPECTUS,
    COURSES_OUTLINE,
    EVENTS,
    DOCUMENTS
}

@Composable
fun HomeScreen(
    onNavigateToPrograms: () -> Unit = {},
    onNavigateToCoursesOutline: () -> Unit = {},
    onNavigateToAdminRegistry: () -> Unit = {},
    onNavigateToContentManagement: () -> Unit = {}
) {
    val userProfile by UserProfileManager.userProfile.collectAsState()
    var activeSubScreen by remember { mutableStateOf(HomeSubScreen.NONE) }
    val scrollState = rememberScrollState()

    if (activeSubScreen != HomeSubScreen.NONE) {
        when (activeSubScreen) {
            HomeSubScreen.FEE_STRUCTURE -> OfficialDocumentsScreen(
                initialCategory = "Fee Structure",
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.ANNOUNCEMENT -> NoticesScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.PROSPECTUS -> ProspectusScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.COURSES_OUTLINE -> CoursesOutlineScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.EVENTS -> EventsScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.DOCUMENTS -> OfficialDocumentsScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.NONE -> {}
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .testTag("home_screen_container")
    ) {
        // 1. Official App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                    contentDescription = "GGC Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("home_official_logo")
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "GGC M.B.Din",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Text(
                        text = "Official App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }
            }

            IconButton(
                onClick = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT },
                modifier = Modifier.testTag("home_notifications_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = BrandNavy,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Compute live greeting, day, and date
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

            // 2. Personalized User Bento Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_user_bento_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
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
                            color = Color(0xFFC59B27)
                        )

                        Text(
                            text = "$currentDayName, $currentDateFormatted",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userProfile.fullName.ifBlank { "College Student / Scholar" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (userProfile.appRole) {
                            AppRole.STUDENT_BS -> "BS Student • ${userProfile.department ?: "Degree Program"}"
                            AppRole.STUDENT_INTER -> "Intermediate Student • ${userProfile.department ?: "Stream"}"
                            AppRole.TEACHER -> "Faculty Member • ${userProfile.department ?: "Academic Department"}"
                            AppRole.HOD -> "Head of Department • ${userProfile.department ?: "Department"}"
                            AppRole.ADMIN -> "College Administrator • Official Portal"
                            else -> "Authorized College Member"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Affiliated with Univ of Punjab",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFC59B27).copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Est. 1959",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFF8E1)
                            )
                        }
                    }
                }
            }

            // 3. Grid of Bento Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Programs",
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    testTag = "bento_btn_programs",
                    onClick = onNavigateToPrograms
                )

                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Fee Structure",
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    testTag = "bento_btn_fee_structure",
                    onClick = { activeSubScreen = HomeSubScreen.FEE_STRUCTURE }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Circulars & Notices",
                    icon = Icons.Outlined.Campaign,
                    testTag = "bento_btn_announcement",
                    onClick = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT }
                )

                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Prospectus",
                    icon = Icons.Outlined.Description,
                    testTag = "bento_btn_prospectus",
                    onClick = { activeSubScreen = HomeSubScreen.PROSPECTUS }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "College Events",
                    icon = Icons.Default.Event,
                    testTag = "bento_btn_events",
                    onClick = { activeSubScreen = HomeSubScreen.EVENTS }
                )

                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Rules & Documents",
                    icon = Icons.Outlined.Description,
                    testTag = "bento_btn_documents",
                    onClick = { activeSubScreen = HomeSubScreen.DOCUMENTS }
                )
            }

            // 4. Courses Outline Full-Width Bento Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { activeSubScreen = HomeSubScreen.COURSES_OUTLINE }
                    .testTag("bento_btn_courses_outline"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Layers,
                                contentDescription = "Courses Outline",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Courses Outline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Semester-wise syllabi, course codes & PDF outlines",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Courses Outline",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Admin Registry & Content Management Cards for authorized HODs and Administrators
            if (userProfile.isVerified && (userProfile.appRole == AppRole.ADMIN || userProfile.appRole == AppRole.HOD)) {
                // Content Portal Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigateToContentManagement() }
                        .testTag("bento_btn_content_management"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC59B27).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Campaign,
                                    contentDescription = "Content Management Portal",
                                    tint = Color(0xFFC59B27),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Content Management Portal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Manage Announcements, Events, Documents & Outlines",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Content Portal",
                            tint = Color(0xFFC59B27),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Registry Management Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigateToAdminRegistry() }
                        .testTag("bento_btn_admin_registry"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030D2B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC59B27).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Registry Management",
                                    tint = Color(0xFFC59B27),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Registry Management",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Manage BS, Inter & Faculty registries",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Registry Management",
                            tint = Color(0xFFC59B27),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BentoActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    containerColor: Color = Color.White,
    contentColor: Color = BrandNavy,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
