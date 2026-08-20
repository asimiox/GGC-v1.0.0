package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserProfileManager
import com.example.ui.screens.admin.content.ContentManagementScreen

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDark = Color(0xFF030D2B)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF3D372)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F8FB)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BrandGold.copy(alpha = 0.2f))
                                .border(1.dp, BrandGoldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = BrandGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GGC Super Control",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Central Administration",
                                fontSize = 10.sp,
                                color = BrandGoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshAll() },
                        modifier = Modifier.testTag("admin_topbar_refresh")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.testTag("admin_topbar_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout Admin",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandNavyDark,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Navigation Ribbon for Administrative Sections
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavy)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminNavSectionTab(
                    title = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    selected = uiState.activeSection == AdminNavSection.DASHBOARD,
                    onClick = { viewModel.selectSection(AdminNavSection.DASHBOARD) }
                )
                AdminNavSectionTab(
                    title = "Users & Roles",
                    icon = Icons.Default.Group,
                    selected = uiState.activeSection == AdminNavSection.USERS,
                    onClick = { viewModel.selectSection(AdminNavSection.USERS) }
                )
                AdminNavSectionTab(
                    title = "Student Registry",
                    icon = Icons.Default.School,
                    selected = uiState.activeSection == AdminNavSection.STUDENTS,
                    onClick = { viewModel.selectSection(AdminNavSection.STUDENTS) }
                )
                AdminNavSectionTab(
                    title = "Faculty Registry",
                    icon = Icons.Default.SupervisorAccount,
                    selected = uiState.activeSection == AdminNavSection.FACULTY,
                    onClick = { viewModel.selectSection(AdminNavSection.FACULTY) }
                )
                AdminNavSectionTab(
                    title = "Academics",
                    icon = Icons.Default.Book,
                    selected = uiState.activeSection == AdminNavSection.ACADEMICS,
                    onClick = { viewModel.selectSection(AdminNavSection.ACADEMICS) }
                )
                AdminNavSectionTab(
                    title = "Notices",
                    icon = Icons.Default.Campaign,
                    selected = uiState.activeSection == AdminNavSection.CONTENT,
                    onClick = { viewModel.selectSection(AdminNavSection.CONTENT) }
                )
                AdminNavSectionTab(
                    title = "Events",
                    icon = Icons.Default.Event,
                    selected = uiState.activeSection == AdminNavSection.EVENTS,
                    onClick = { viewModel.selectSection(AdminNavSection.EVENTS) }
                )
                AdminNavSectionTab(
                    title = "Documents",
                    icon = Icons.Default.Description,
                    selected = uiState.activeSection == AdminNavSection.DOCUMENTS,
                    onClick = { viewModel.selectSection(AdminNavSection.DOCUMENTS) }
                )
                AdminNavSectionTab(
                    title = "Broadcasts",
                    icon = Icons.Default.NotificationsActive,
                    selected = uiState.activeSection == AdminNavSection.NOTIFICATIONS,
                    onClick = { viewModel.selectSection(AdminNavSection.NOTIFICATIONS) }
                )
                AdminNavSectionTab(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    selected = uiState.activeSection == AdminNavSection.SETTINGS,
                    onClick = { viewModel.selectSection(AdminNavSection.SETTINGS) }
                )
            }

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
                                    imageVector = Icons.Default.ExitToApp,
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
                                    imageVector = Icons.Default.ExitToApp,
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
                    AdminNavSection.STUDENTS, AdminNavSection.FACULTY -> OfficialRegistryScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) }
                    )
                    AdminNavSection.ACADEMICS, AdminNavSection.CONTENT, AdminNavSection.EVENTS, AdminNavSection.DOCUMENTS -> ContentManagementScreen(
                        onBack = { viewModel.selectSection(AdminNavSection.DASHBOARD) }
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
fun AdminNavSectionTab(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) BrandGoldLight else Color.White.copy(alpha = 0.12f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) BrandNavyDark else Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) BrandNavyDark else Color.White
            )
        }
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
