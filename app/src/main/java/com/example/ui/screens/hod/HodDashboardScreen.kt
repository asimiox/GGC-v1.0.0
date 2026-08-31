package com.example.ui.screens.hod

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

data class HodMainFeatureCardItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
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
    val features = listOf(
        HodMainFeatureCardItem(
            title = "Teachers Management",
            subtitle = "View, provision, edit & remove department faculty",
            badge = "${state.totalFacultyCount} Faculty",
            icon = Icons.Default.Person,
            iconBgColor = Color(0xFFE8F5E9),
            iconTint = Color(0xFF2E7D32),
            tag = "hod_feature_teachers",
            targetScreen = HodFlowScreen.TEACHERS_MANAGEMENT
        ),
        HodMainFeatureCardItem(
            title = "Students Import & CRUD",
            subtitle = "Batch import rosters (CSV/Gazette) & manage student records",
            badge = "${state.totalStudentsCount} Students",
            icon = Icons.Default.School,
            iconBgColor = Color(0xFFE3F2FD),
            iconTint = Color(0xFF1565C0),
            tag = "hod_feature_students",
            targetScreen = HodFlowScreen.STUDENTS_MANAGEMENT
        ),
        HodMainFeatureCardItem(
            title = "Department Posts",
            subtitle = "Create, edit & delete department news and articles",
            badge = "${state.totalPostsCount} Posts",
            icon = Icons.Default.Article,
            iconBgColor = Color(0xFFFFF3E0),
            iconTint = Color(0xFFE65100),
            tag = "hod_feature_posts",
            targetScreen = HodFlowScreen.POSTS_MANAGEMENT
        ),
        HodMainFeatureCardItem(
            title = "Announcements & Notices",
            subtitle = "Broadcast notices, event alerts & date sheets with pinned priority",
            badge = "${state.totalAnnouncementsCount} Notices",
            icon = Icons.Default.Campaign,
            iconBgColor = Color(0xFFEDE7F6),
            iconTint = Color(0xFF512DA8),
            tag = "hod_feature_announcements",
            targetScreen = HodFlowScreen.ANNOUNCEMENTS_MANAGEMENT
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HOD Department Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = state.departmentName,
                            fontSize = 12.sp,
                            color = BrandGoldLight
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
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
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
                Spacer(modifier = Modifier.height(12.dp))
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
                Spacer(modifier = Modifier.height(12.dp))
            }

            // HOD Identity Card (Locked to Department)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(BrandNavy, Color(0xFF0D2D7D))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(BrandGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = BrandGoldLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = BrandGold.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = BrandGoldLight,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Head of Department (HOD)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGoldLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.hodName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Department of ${state.departmentName}",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Stats Row (Strictly for this department)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            HodQuickStatItem("Faculty", "${state.totalFacultyCount}", Color(0xFF81C784))
                            HodQuickStatItem("Students", "${state.totalStudentsCount}", Color(0xFF64B5F6))
                            HodQuickStatItem("Posts", "${state.totalPostsCount}", Color(0xFFFFB74D))
                            HodQuickStatItem("Notices", "${state.totalAnnouncementsCount}", Color(0xFFBA68C8))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Department Operations",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "All operations are strictly isolated to the ${state.departmentName} department.",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // The 4 Primary Features
            features.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag(item.tag)
                        .clickable { onFeatureClick(item.targetScreen) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.iconTint,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavy
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = item.iconBgColor,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.iconTint,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
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
