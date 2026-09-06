package com.example.ui.screens.hod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val BrandNavy = Color(0xFF061B52)
private val BrandNavySecondary = Color(0xFF0C235A)
private val BrandAccentBlue = Color(0xFF1E5BB5)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)

data class HodMainFeatureCardItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val isDark: Boolean,
    val tag: String,
    val targetScreen: HodFlowScreen
)

/**
 * Master HOD Dashboard Screen:
 * Strictly limited to the 4 requested HOD departmental operations:
 * 1. Teachers CRUD
 * 2. Students Import & CRUD
 * 3. Posts CRUD
 * 4. Announcements CRUD
 * Everything is strictly bound to the HOD's own designated department.
 */
@Composable
fun HodDashboardScreen(
    viewModel: HodViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadHodProfile()
    }

    when (state.currentScreen) {
        HodFlowScreen.DASHBOARD -> {
            HodDashboardMainView(
                state = state,
                onFeatureClick = { viewModel.navigateTo(it) },
                onRefresh = { viewModel.refreshAllData() },
                onDismissMessage = { viewModel.clearMessages() },
                onBack = onNavigateBack
            )
        }

        HodFlowScreen.TEACHERS_MANAGEMENT -> {
            HodTeachersCrudScreen(
                state = state,
                onSearchChange = { viewModel.setTeachersSearchQuery(it) },
                onRefresh = { viewModel.fetchDepartmentTeachers() },
                onCreateTeacher = { name, desig, subj, id, pass, phone ->
                    viewModel.createTeacher(name, desig, subj, id, pass, phone)
                },
                onUpdateTeacher = { id, fId, name, desig, subj, phone, isActive ->
                    viewModel.updateTeacher(id, fId, name, desig, subj, phone, isActive)
                },
                onDeleteTeacher = { id, name ->
                    viewModel.deleteTeacher(id, name)
                },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }

        HodFlowScreen.STUDENTS_MANAGEMENT -> {
            HodStudentsCrudScreen(
                state = state,
                onSearchChange = { viewModel.setStudentsSearchQuery(it) },
                onRefresh = { viewModel.fetchDepartmentStudents() },
                onCreateStudent = { roll, reg, prog, sess, first, last, active ->
                    viewModel.createBsStudent(roll, reg, prog, sess, first, last, active)
                },
                onUpdateStudent = { id, roll, reg, prog, sess, first, last, active ->
                    viewModel.updateBsStudent(id, roll, reg, prog, sess, first, last, active)
                },
                onDeleteStudent = { id, roll ->
                    viewModel.deleteBsStudent(id, roll)
                },
                onUpdateUploadConfig = { prog, sem, sess ->
                    viewModel.updateUploadConfig(prog, sem, sess)
                },
                onParseText = { viewModel.parseStudentDataFromText(it) },
                onParseFileUri = { ctx, uri -> viewModel.parseStudentFileUri(ctx, uri) },
                onToggleSelectStudent = { viewModel.toggleStudentSelection(it) },
                onToggleSelectAll = { viewModel.toggleSelectAllStudents() },
                onPushSelectedToSupabase = { viewModel.pushSelectedStudentsToSupabase() },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }

        HodFlowScreen.POSTS_MANAGEMENT -> {
            HodPostsCrudScreen(
                state = state,
                onSearchChange = { viewModel.setPostsSearchQuery(it) },
                onRefresh = { viewModel.fetchDepartmentPosts() },
                onCreatePost = { title, content, category, venue, date ->
                    viewModel.createPost(title, content, category, venue, date)
                },
                onUpdatePost = { id, title, content, category, venue, date ->
                    viewModel.updatePost(id, title, content, category, venue, date)
                },
                onDeletePost = { id, title ->
                    viewModel.deletePost(id, title)
                },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }

        HodFlowScreen.ANNOUNCEMENTS_MANAGEMENT -> {
            HodAnnouncementsCrudScreen(
                state = state,
                onSearchChange = { viewModel.setAnnouncementsSearchQuery(it) },
                onRefresh = { viewModel.fetchDepartmentAnnouncements() },
                onCreateAnnouncement = { title, content, category, isPinned ->
                    viewModel.createAnnouncement(title, content, category, isPinned)
                },
                onUpdateAnnouncement = { id, title, content, category, isPinned, isPublished ->
                    viewModel.updateAnnouncement(id, title, content, category, isPinned, isPublished)
                },
                onDeleteAnnouncement = { id, title ->
                    viewModel.deleteAnnouncement(id, title)
                },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodDashboardMainView(
    state: HodUiState,
    onFeatureClick: (HodFlowScreen) -> Unit,
    onRefresh: () -> Unit,
    onDismissMessage: () -> Unit,
    onBack: () -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HOD Command Center",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Department of ${state.departmentName}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status / Error Banners
            state.statusMessage?.let { msg ->
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(msg, fontSize = 13.sp, color = Color(0xFF1B5E20), modifier = Modifier.weight(1f))
                        IconButton(onClick = onDismissMessage, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            state.errorMessage?.let { err ->
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(err, fontSize = 13.sp, color = Color(0xFFB71C1C), modifier = Modifier.weight(1f))
                        IconButton(onClick = onDismissMessage, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 1. Official HOD Identity Card (Matching Faculty/Home Hero Card Style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("hod_identity_card"),
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
                            color = Color.White.copy(alpha = 0.9f)
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
                                contentDescription = "HOD",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.hodName.ifBlank { "Department HOD" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Head of Department • ${state.departmentName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verification Badge
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
                                contentDescription = "Verified HOD",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Official Department Head • Verified",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HodQuickStatItem("Faculty", "${state.totalFacultyCount}", Color.White)
                        HodQuickStatItem("Students", "${state.totalStudentsCount}", Color.White)
                        HodQuickStatItem("Posts", "${state.totalPostsCount}", Color.White)
                        HodQuickStatItem("Notices", "${state.totalAnnouncementsCount}", Color.White)
                    }
                }
            }

            // 2. Department Operations Title
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Department Operations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Manage department staff, student records and announcements",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )
            }

            // 3. 2x2 Bento Operations Grid (Universal Light Design)
            // Row 1: Teachers Management & Students Management
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HodBentoCard(
                    title = "Teachers Roster",
                    subtitle = "Provision & Manage Staff",
                    badgeText = "${state.totalFacultyCount} Faculty",
                    icon = Icons.Default.Person,
                    isDark = false,
                    testTag = "hod_feature_teachers",
                    onClick = { onFeatureClick(HodFlowScreen.TEACHERS_MANAGEMENT) },
                    modifier = Modifier.weight(1f)
                )

                HodBentoCard(
                    title = "Students Roster",
                    subtitle = "Import & Records CRUD",
                    badgeText = "${state.totalStudentsCount} Students",
                    icon = Icons.Default.School,
                    isDark = false,
                    testTag = "hod_feature_students",
                    onClick = { onFeatureClick(HodFlowScreen.STUDENTS_MANAGEMENT) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: Department Posts & Announcements
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HodBentoCard(
                    title = "Department Posts",
                    subtitle = "News, Articles & Updates",
                    badgeText = "${state.totalPostsCount} Posts",
                    icon = Icons.Default.Article,
                    isDark = false,
                    testTag = "hod_feature_posts",
                    onClick = { onFeatureClick(HodFlowScreen.POSTS_MANAGEMENT) },
                    modifier = Modifier.weight(1f)
                )

                HodBentoCard(
                    title = "Announcements",
                    subtitle = "Alerts, Notices & Dates",
                    badgeText = "${state.totalAnnouncementsCount} Notices",
                    icon = Icons.Default.Campaign,
                    isDark = false,
                    testTag = "hod_feature_announcements",
                    onClick = { onFeatureClick(HodFlowScreen.ANNOUNCEMENTS_MANAGEMENT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HodBentoCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    isDark: Boolean = false,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(152.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        .background(BrandIconBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = BrandNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    color = Color(0xFFF0F3FA),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HodQuickStatItem(
    label: String,
    value: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
