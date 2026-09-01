package com.example.ui.screens.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.data.UserProfileManager
import com.example.data.model.AppRole
import com.example.data.repository.NotificationRepository
import com.example.ui.screens.courses.CoursesOutlineScreen
import com.example.ui.screens.documents.OfficialDocumentsScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.notices.NoticesScreen
import com.example.ui.screens.notifications.NotificationCenterScreen
import com.example.ui.screens.prospectus.ProspectusScreen

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.screens.auth.FacultyAuthContent
import com.example.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.launch

import com.example.ui.screens.admin.content.ContentManagementScreen
import com.example.ui.screens.admin.content.ContentSectionTab
import com.example.ui.screens.faculty.FacultyDashboardView

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)

enum class HomeSubScreen {
    NONE,
    NOTIFICATION_CENTER,
    PROFILE,
    FEE_STRUCTURE,
    ANNOUNCEMENT,
    PROSPECTUS,
    COURSES_OUTLINE,
    EVENTS,
    DOCUMENTS,
    CONTENT_MANAGEMENT,
    HOD_DASHBOARD,
    STUDENTS_MANAGEMENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPrograms: () -> Unit = {},
    onNavigateToCoursesOutline: () -> Unit = {},
    onNavigateToAdminRegistry: () -> Unit = {},
    onNavigateToContentManagement: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userProfile by UserProfileManager.userProfile.collectAsState()
    val notificationRepository = remember { NotificationRepository.getInstance(context) }
    val unreadCount by notificationRepository.unreadCount.collectAsState()

    var activeSubScreen by remember { mutableStateOf(HomeSubScreen.NONE) }
    var targetContentTab by remember { mutableStateOf(ContentSectionTab.ANNOUNCEMENTS) }
    var showFacultyAuthSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    BackHandler(enabled = activeSubScreen != HomeSubScreen.NONE || showFacultyAuthSheet) {
        if (showFacultyAuthSheet) {
            showFacultyAuthSheet = false
        } else if (activeSubScreen != HomeSubScreen.NONE) {
            activeSubScreen = HomeSubScreen.NONE
        }
    }

    if (showFacultyAuthSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFacultyAuthSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FacultyAuthContent(
                    onAuthSuccess = {
                        scope.launch {
                            sheetState.hide()
                            showFacultyAuthSheet = false
                        }
                    }
                )
            }
        }
    }

    if (activeSubScreen != HomeSubScreen.NONE) {
        when (activeSubScreen) {
            HomeSubScreen.PROFILE -> ProfileScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.NOTIFICATION_CENTER -> NotificationCenterScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE },
                onNavigateToContent = { contentType, _ ->
                    when (contentType.lowercase()) {
                        "announcement", "notice" -> activeSubScreen = HomeSubScreen.ANNOUNCEMENT
                        "event" -> activeSubScreen = HomeSubScreen.EVENTS
                        "document", "fee_structure" -> activeSubScreen = HomeSubScreen.DOCUMENTS
                        "course_outline", "syllabus" -> activeSubScreen = HomeSubScreen.COURSES_OUTLINE
                        "prospectus" -> activeSubScreen = HomeSubScreen.PROSPECTUS
                        else -> activeSubScreen = HomeSubScreen.ANNOUNCEMENT
                    }
                }
            )
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
            HomeSubScreen.CONTENT_MANAGEMENT -> ContentManagementScreen(
                onBack = { activeSubScreen = HomeSubScreen.NONE },
                initialTab = targetContentTab
            )
            HomeSubScreen.HOD_DASHBOARD -> com.example.ui.screens.hod.HodDashboardScreen(
                onNavigateBack = { activeSubScreen = HomeSubScreen.NONE }
            )
            HomeSubScreen.STUDENTS_MANAGEMENT -> {
                val hodViewModel: com.example.ui.screens.hod.HodViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val hodState by hodViewModel.uiState.collectAsState()
                com.example.ui.screens.hod.HodStudentsCrudScreen(
                    state = hodState,
                    onSearchChange = { hodViewModel.setStudentsSearchQuery(it) },
                    onRefresh = { hodViewModel.fetchDepartmentStudents() },
                    onCreateStudent = { roll, reg, prog, sess, first, last, active ->
                        hodViewModel.createBsStudent(roll, reg, prog, sess, first, last, active)
                    },
                    onUpdateStudent = { id, roll, reg, prog, sess, first, last, active ->
                        hodViewModel.updateBsStudent(id, roll, reg, prog, sess, first, last, active)
                    },
                    onDeleteStudent = { id, roll ->
                        hodViewModel.deleteBsStudent(id, roll)
                    },
                    onUpdateUploadConfig = { prog, sem, sess ->
                        hodViewModel.updateUploadConfig(prog, sem, sess)
                    },
                    onParseText = { hodViewModel.parseStudentDataFromText(it) },
                    onParseFileUri = { ctx, uri -> hodViewModel.parseStudentFileUri(ctx, uri) },
                    onToggleSelectStudent = { hodViewModel.toggleStudentSelection(it) },
                    onToggleSelectAll = { hodViewModel.toggleSelectAllStudents() },
                    onPushSelectedToSupabase = { hodViewModel.pushSelectedStudentsToSupabase() },
                    onBack = { activeSubScreen = HomeSubScreen.NONE }
                )
            }
            HomeSubScreen.NONE -> {}
        }
        return
    }

    // If User is Faculty/Teacher, render dedicated Faculty Portal Dashboard
    if (userProfile.isFaculty) {
        FacultyDashboardView(
            userProfile = userProfile,
            unreadCount = unreadCount,
            onNavigateToContentTab = { tab ->
                targetContentTab = tab
                activeSubScreen = HomeSubScreen.CONTENT_MANAGEMENT
            },
            onNavigateToCourses = onNavigateToCoursesOutline,
            onNavigateToNotices = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT },
            onNavigateToEvents = { activeSubScreen = HomeSubScreen.EVENTS },
            onNavigateToDocuments = { activeSubScreen = HomeSubScreen.DOCUMENTS },
            onNavigateToProfile = { activeSubScreen = HomeSubScreen.PROFILE },
            onNavigateToNotificationCenter = { activeSubScreen = HomeSubScreen.NOTIFICATION_CENTER },
            onNavigateToHodPanel = { activeSubScreen = HomeSubScreen.HOD_DASHBOARD },
            onNavigateToStudentsManagement = { activeSubScreen = HomeSubScreen.STUDENTS_MANAGEMENT }
        )
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { activeSubScreen = HomeSubScreen.PROFILE }
            ) {
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
                        text = if (userProfile.isFaculty) "Faculty Portal" else "Official App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile & Portals Button
                IconButton(
                    onClick = { activeSubScreen = HomeSubScreen.PROFILE },
                    modifier = Modifier.testTag("home_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Profile & Portals",
                        tint = BrandNavy,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Notification Bell
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = { activeSubScreen = HomeSubScreen.NOTIFICATION_CENTER },
                        modifier = Modifier.testTag("home_notifications_btn")
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
                                .testTag("home_bell_unread_badge"),
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

            // 2. Personalized Student Academic Bento Card (Clickable to view Profile)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { activeSubScreen = HomeSubScreen.PROFILE }
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
                        text = userProfile.name.ifBlank { "College Student" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (userProfile.programName.isNotBlank()) {
                            "${userProfile.programName}${if (!userProfile.semester.isNullOrBlank()) " • ${userProfile.semester}" else ""}"
                        } else {
                            when (userProfile.appRole) {
                                AppRole.STUDENT_BS -> "BS Student Portal"
                                AppRole.STUDENT_INTERMEDIATE -> "Intermediate Student Portal"
                                else -> "Authorized Student Portal"
                            }
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    if (!userProfile.rollNumber.isNullOrBlank() || !userProfile.registrationNumber.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!userProfile.rollNumber.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Roll: ${userProfile.rollNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                            if (!userProfile.registrationNumber.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Reg: ${userProfile.registrationNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

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

            // Only show Faculty/Admin quick access if the active session is an authenticated Faculty/Admin
            if (userProfile.isFaculty && userProfile.isVerified) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { activeSubScreen = HomeSubScreen.PROFILE }
                        .testTag("bento_btn_faculty_teacher_portal"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030D2B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
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
                                    .background(Color(0xFFC59B27).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (userProfile.appRole == AppRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.School,
                                    contentDescription = "Faculty Portal",
                                    tint = Color(0xFFE5C058),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Faculty Portal (Logged In)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Signed in as ${userProfile.name} • Tap to view",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Faculty Portal",
                            tint = Color(0xFFE5C058),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 3. Grid of Bento Action Buttons (Alternating Blue-White Ladder Structure)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Programs (Blue)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Programs",
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    testTag = "bento_btn_programs",
                    containerColor = BrandNavy,
                    contentColor = Color.White,
                    onClick = onNavigateToPrograms
                )

                // 2. Fee Structure (White)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Fee Structure",
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    testTag = "bento_btn_fee_structure",
                    containerColor = Color.White,
                    contentColor = BrandNavy,
                    onClick = { activeSubScreen = HomeSubScreen.FEE_STRUCTURE }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 3. Circulars & Notices (White)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Circulars & Notices",
                    icon = Icons.Outlined.Campaign,
                    testTag = "bento_btn_announcement",
                    containerColor = Color.White,
                    contentColor = BrandNavy,
                    onClick = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT }
                )

                // 4. Prospectus (Blue)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Prospectus",
                    icon = Icons.Outlined.Description,
                    testTag = "bento_btn_prospectus",
                    containerColor = BrandNavy,
                    contentColor = Color.White,
                    onClick = { activeSubScreen = HomeSubScreen.PROSPECTUS }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 5. College Events (Blue)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "College Events",
                    icon = Icons.Default.Event,
                    testTag = "bento_btn_events",
                    containerColor = BrandNavy,
                    contentColor = Color.White,
                    onClick = { activeSubScreen = HomeSubScreen.EVENTS }
                )

                // 6. Rules & Documents (White)
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Rules & Documents",
                    icon = Icons.Outlined.Description,
                    testTag = "bento_btn_documents",
                    containerColor = Color.White,
                    contentColor = BrandNavy,
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
