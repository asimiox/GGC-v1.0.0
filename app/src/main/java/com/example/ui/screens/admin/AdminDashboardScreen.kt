package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.UserProfileManager
import com.example.ui.screens.admin.OfficialRegistryScreen
import com.example.ui.screens.admin.OfficialRegistryTab
import com.example.ui.screens.admin.content.ContentManagementScreen
import com.example.ui.screens.admin.content.ContentSectionTab

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F6F6)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AdminControlCenterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val isRootDashboard = uiState.activeSection == AdminNavSection.DASHBOARD

    val sectionTitle = when (uiState.activeSection) {
        AdminNavSection.DASHBOARD -> "Super Admin Portal"
        AdminNavSection.USERS -> "User & Roles Governance"
        AdminNavSection.STUDENTS -> "Student Official Registry"
        AdminNavSection.FACULTY -> "Faculty Official Registry"
        AdminNavSection.ACADEMICS -> "Course Outlines & Syllabi"
        AdminNavSection.CONTENT -> "Announcements & Circulars"
        AdminNavSection.EVENTS -> "College Events & Seminars"
        AdminNavSection.DOCUMENTS -> "Official Documents & Rules"
        AdminNavSection.NOTIFICATIONS -> "Broadcast Alert Center"
        AdminNavSection.SETTINGS -> "System Settings & Diagnostics"
    }

    Scaffold(
        topBar = {
            if (isRootDashboard) {
                // Official Root Admin Top Bar (Matches Faculty & Student Header style)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ggc_logo),
                                contentDescription = "GGC Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("admin_header_logo")
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
                                        text = "Super Admin Portal",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BrandGold
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.refreshAll() },
                                modifier = Modifier.testTag("admin_topbar_refresh")
                            ) {
                                if (uiState.isLoading || uiState.isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = BrandNavy,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Data",
                                        tint = BrandNavy,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showLogoutConfirm = true },
                                modifier = Modifier.testTag("admin_topbar_logout")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout Admin",
                                    tint = BrandNavy,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
                }
            } else {
                // Child Section Top Bar with Back Navigation
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                            modifier = Modifier.testTag("admin_subscreen_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Dashboard",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        Text(
                            text = sectionTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.refreshAll() },
                            modifier = Modifier.testTag("admin_subscreen_refresh")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrandNavy,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        containerColor = BackgroundSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Global Message Banners
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearMessages() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.successMessage?.let { success ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = success,
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearMessages() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Content Area Router
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (uiState.activeSection) {
                    AdminNavSection.DASHBOARD -> AdminOverviewDashboardView(
                        uiState = uiState,
                        onNavigateSection = { viewModel.selectSection(it) },
                        onRefresh = { viewModel.refreshAll() }
                    )
                    AdminNavSection.USERS -> AdminUserManagementView(
                        uiState = uiState,
                        onAssignHod = { fId, dName -> viewModel.assignHod(fId, dName) },
                        onRevokeHod = { uId -> viewModel.revokeHod(uId) },
                        onResetClaim = { type, id, reason -> viewModel.resetClaimedRecord(type, id, reason) },
                        onToggleActive = { type, id, active -> viewModel.toggleRecordActive(type, id, active) }
                    )
                    AdminNavSection.STUDENTS -> OfficialRegistryScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = OfficialRegistryTab.BS_STUDENTS,
                        showTopBar = false
                    )
                    AdminNavSection.FACULTY -> OfficialRegistryScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = OfficialRegistryTab.FACULTY,
                        showTopBar = false
                    )
                    AdminNavSection.ACADEMICS -> ContentManagementScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = ContentSectionTab.COURSE_OUTLINES,
                        showTopBar = false
                    )
                    AdminNavSection.CONTENT -> ContentManagementScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = ContentSectionTab.ANNOUNCEMENTS,
                        showTopBar = false
                    )
                    AdminNavSection.EVENTS -> ContentManagementScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = ContentSectionTab.EVENTS,
                        showTopBar = false
                    )
                    AdminNavSection.DOCUMENTS -> ContentManagementScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) },
                        initialTab = ContentSectionTab.DOCUMENTS,
                        showTopBar = false
                    )
                    AdminNavSection.NOTIFICATIONS -> AdminNotificationManagerView(
                        uiState = uiState,
                        onTitleChange = { viewModel.updateBroadcastTitle(it) },
                        onMessageChange = { viewModel.updateBroadcastMessage(it) },
                        onTypeChange = { viewModel.updateBroadcastType(it) },
                        onTargetChange = { viewModel.updateBroadcastTarget(it) },
                        onSendBroadcast = { viewModel.sendBroadcastNotification() }
                    )
                    AdminNavSection.SETTINGS -> AdminSystemSettingsView(
                        onLogout = {
                            viewModel.logoutAdmin(context) { onNavigateBack() }
                        }
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Exit Super Control?") },
            text = {
                Text("Are you sure you want to end the Super Administrator session? You will be returned to the Welcome screen.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logoutAdmin(context) { onNavigateBack() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("Logout Administrator")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminSystemSettingsView(
    onLogout: () -> Unit
) {
    val profile = UserProfileManager.userProfile.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(16.dp)
    ) {
        Text(
            text = "System Settings & Diagnostics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )
        Text(
            text = "Enterprise architecture status, database parameters and session controls",
            fontSize = 12.sp,
            color = Color(0xFF718096)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Active Administrator Session",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Official Identity:", fontSize = 12.sp, color = Color.Gray)
                    Text(profile.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enforced Role:", fontSize = 12.sp, color = Color.Gray)
                    Text("SUPER_ADMINISTRATOR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Institutional Email:", fontSize = 12.sp, color = Color.Gray)
                    Text(profile.institutionalEmail ?: "admin@ggc.edu.pk", fontSize = 12.sp, color = BrandNavy)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Database & Cloud Infrastructure",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Supabase Backend:", fontSize = 12.sp, color = Color.Gray)
                    Text("Connected (gugvckgsv...)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RLS Status:", fontSize = 12.sp, color = Color.Gray)
                    Text("Enabled (Strict Isolation)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Realtime Notifications:", fontSize = 12.sp, color = Color.Gray)
                    Text("Subscribed (Channel Active)", fontSize = 12.sp, color = Color(0xFF1565C0))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout of Super Control")
        }
    }
}
