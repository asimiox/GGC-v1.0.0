package com.example.ui.screens.hod

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val testTag: String,
    val targetScreen: HodFlowScreen
)

/**
 * Main HOD Command Center & Router Screen.
 */
@Composable
fun HodDashboardScreen(
    viewModel: HodViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Handle Android hardware back press depending on current sub-screen
    BackHandler(enabled = state.currentScreen != HodFlowScreen.DASHBOARD) {
        when (state.currentScreen) {
            HodFlowScreen.DASHBOARD -> onNavigateBack()
            HodFlowScreen.ADD_TEACHER_FORM -> viewModel.navigateTo(HodFlowScreen.DASHBOARD)
            HodFlowScreen.TEACHER_CREATED_SUMMARY -> viewModel.navigateTo(HodFlowScreen.DASHBOARD)
            HodFlowScreen.UPLOAD_STUDENTS -> viewModel.navigateTo(HodFlowScreen.DASHBOARD)
            HodFlowScreen.STUDENTS_IMPORTED_PREVIEW -> viewModel.navigateTo(HodFlowScreen.UPLOAD_STUDENTS)
            HodFlowScreen.NOTICE_CATEGORY_SELECT -> viewModel.navigateTo(HodFlowScreen.DASHBOARD)
            HodFlowScreen.NOTICE_COMPOSE_SEND -> viewModel.navigateTo(HodFlowScreen.NOTICE_CATEGORY_SELECT)
            HodFlowScreen.PROFILE_SETTINGS -> viewModel.navigateTo(HodFlowScreen.DASHBOARD)
        }
    }

    when (state.currentScreen) {
        HodFlowScreen.DASHBOARD -> {
            HodDashboardMainView(
                state = state,
                onFeatureClick = { viewModel.navigateTo(it) },
                onRefresh = { viewModel.refreshDepartmentStats() },
                onNavigateBack = onNavigateBack,
                snackbarHostState = snackbarHostState
            )
        }
        HodFlowScreen.ADD_TEACHER_FORM -> {
            HodAddTeacherFormScreen(
                state = state,
                onUpdateForm = { n, d, s, id, p -> viewModel.updateTeacherForm(n, d, s, id, p) },
                onSubmit = { viewModel.createTeacherAccount() },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }
        HodFlowScreen.TEACHER_CREATED_SUMMARY -> {
            HodTeacherCreatedSummaryScreen(
                createdTeacher = state.lastCreatedTeacher,
                onNavigateToNotice = {
                    viewModel.setNoticeCategory("General Notice")
                    viewModel.navigateTo(HodFlowScreen.NOTICE_COMPOSE_SEND)
                },
                onNavigateToNewTalk = {
                    // Handled inside component
                },
                onNavigateToProfile = {
                    viewModel.navigateTo(HodFlowScreen.PROFILE_SETTINGS)
                },
                onAddAnotherTeacher = {
                    viewModel.updateTeacherForm("", "Lecturer", "", "", "00000")
                    viewModel.navigateTo(HodFlowScreen.ADD_TEACHER_FORM)
                },
                onDone = {
                    viewModel.navigateTo(HodFlowScreen.DASHBOARD)
                }
            )
        }
        HodFlowScreen.UPLOAD_STUDENTS -> {
            HodUploadStudentsScreen(
                state = state,
                onUpdateConfig = { p, sem, s -> viewModel.updateUploadConfig(p, sem, s) },
                onFileSelected = { uri -> viewModel.parseStudentFileUri(context, uri) },
                onParseText = { text -> viewModel.parseStudentDataFromText(text) },
                onLoadSample = { viewModel.loadSampleGazetteData() },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }
        HodFlowScreen.STUDENTS_IMPORTED_PREVIEW -> {
            HodStudentsImportedPreviewScreen(
                state = state,
                onToggleSelectAll = { viewModel.toggleSelectAllStudents() },
                onToggleStudent = { id -> viewModel.toggleStudentSelection(id) },
                onPushToSupabase = { viewModel.pushSelectedStudentsToSupabase() },
                onBack = { viewModel.navigateTo(HodFlowScreen.UPLOAD_STUDENTS) }
            )
        }
        HodFlowScreen.NOTICE_CATEGORY_SELECT -> {
            HodNoticeCategorySelectScreen(
                state = state,
                onSelectCategory = { cat -> viewModel.setNoticeCategory(cat) },
                onProceedToCompose = { viewModel.navigateTo(HodFlowScreen.NOTICE_COMPOSE_SEND) },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }
        HodFlowScreen.NOTICE_COMPOSE_SEND -> {
            HodNoticeComposeSendScreen(
                state = state,
                onUpdateNotice = { dept, sem, t, c -> viewModel.updateNoticeCompose(dept, sem, t, c) },
                onPost = { viewModel.postNotice() },
                onBack = { viewModel.navigateTo(HodFlowScreen.NOTICE_CATEGORY_SELECT) }
            )
        }
        HodFlowScreen.PROFILE_SETTINGS -> {
            HodProfileSettingsScreen(
                state = state,
                onUpdateForm = { id, cp, np, cfm -> viewModel.updateProfileSettingsForm(id, cp, np, cfm) },
                onSave = { ctx -> viewModel.saveProfileSettings(ctx) },
                onBack = { viewModel.navigateTo(HodFlowScreen.DASHBOARD) }
            )
        }
    }
}

/**
 * 1st Page: HOD Dashboard
 * Main features shown on dashboard:
 * - Add Teacher
 * - Upload Students Data
 * - Notice+ / Announcements+
 * - Profile Settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HodDashboardMainView(
    state: HodUiState,
    onFeatureClick: (HodFlowScreen) -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val features = listOf(
        HodMainFeatureCardItem(
            title = "Add Teacher",
            subtitle = "Provision faculty accounts with Subject, ID & Credentials",
            icon = Icons.Default.PersonAdd,
            iconBgColor = Color(0xFFE8EAF6),
            iconTint = BrandNavy,
            testTag = "hod_feature_add_teacher",
            targetScreen = HodFlowScreen.ADD_TEACHER_FORM
        ),
        HodMainFeatureCardItem(
            title = "Upload Students Data",
            subtitle = "Ingest .csv, .txt, .pdf rosters with gazette parser & sync to Supabase",
            icon = Icons.Default.CloudUpload,
            iconBgColor = Color(0xFFE0F2F1),
            iconTint = Color(0xFF00796B),
            testTag = "hod_feature_upload_students",
            targetScreen = HodFlowScreen.UPLOAD_STUDENTS
        ),
        HodMainFeatureCardItem(
            title = "Notice+ / Announcements+",
            subtitle = "Broadcast events, fees, date sheets by department & semester",
            icon = Icons.Default.Campaign,
            iconBgColor = Color(0xFFFFF3E0),
            iconTint = Color(0xFFE65100),
            testTag = "hod_feature_notice",
            targetScreen = HodFlowScreen.NOTICE_CATEGORY_SELECT
        ),
        HodMainFeatureCardItem(
            title = "Profile Settings",
            subtitle = "Manage HOD credentials, Teacher ID & Account Password",
            icon = Icons.Default.ManageAccounts,
            iconBgColor = Color(0xFFEDE7F6),
            iconTint = Color(0xFF512DA8),
            testTag = "hod_feature_profile_settings",
            targetScreen = HodFlowScreen.PROFILE_SETTINGS
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HOD Command Panel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Govt Graduate College Mandi Bahauddin",
                            fontSize = 12.sp,
                            color = BrandGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                            contentDescription = "Refresh Data",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BrandBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // HOD Identity & Department Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            Surface(
                                color = BrandGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = BrandGoldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Official HOD Authority",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGoldLight
                                    )
                                }
                            }

                            Text(
                                text = "GGC Mandi Bahauddin",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

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
                                    contentDescription = null,
                                    tint = BrandGoldLight,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = state.hodName,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Head of Department • ${state.departmentName}",
                                    fontSize = 13.sp,
                                    color = BrandGoldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Department Quick Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${state.totalFacultyCount}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Faculty Staff",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${state.totalStudentsCount}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Enrolled Students",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${state.activeNoticesCount}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Active Notices",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }

            // Features Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Department Operations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Text(
                        text = "4 Core Features",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandGold
                    )
                }
            }

            // The 4 Primary Feature Cards
            items(features.size) { index ->
                val feature = features[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onFeatureClick(feature.targetScreen) }
                        .testTag(feature.testTag),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(feature.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = null,
                                tint = feature.iconTint,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feature.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = feature.subtitle,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BrandNavy.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
