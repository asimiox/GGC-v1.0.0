package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileManager
import com.example.data.model.AppNotificationDto
import com.example.data.repository.NotificationRepository
import com.example.ui.components.GgcBottomBar
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.about.AboutScreen
import com.example.ui.screens.academics.AcademicsScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.OfficialRegistryScreen
import com.example.ui.screens.admin.content.ContentManagementScreen
import com.example.ui.screens.admission.AdmissionScreen
import com.example.ui.screens.alumni.AlumniScreen
import com.example.ui.screens.courses.CoursesOutlineScreen
import com.example.ui.screens.faculty.FacultyTabScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notices.NoticesScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.programs.ProgramsScreen

private val BrandNavy = Color(0xFF061B52)

@Composable
fun MainScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val userProfile by UserProfileManager.userProfile.collectAsState()
    val notificationRepository = remember { NotificationRepository.getInstance(context) }

    var currentRoute by remember { mutableStateOf(NavRoutes.HOME) }
    var previousRoute by remember { mutableStateOf(NavRoutes.HOME) }
    val routeHistory = remember { mutableStateListOf(NavRoutes.HOME) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var incomingAlertNotification by remember { mutableStateOf<AppNotificationDto?>(null) }

    val navigateTo: (String) -> Unit = { targetRoute ->
        if (currentRoute != targetRoute) {
            previousRoute = currentRoute
            currentRoute = targetRoute
            if (routeHistory.lastOrNull() != targetRoute) {
                routeHistory.add(targetRoute)
            }
        }
    }

    val goBack: () -> Unit = {
        if (routeHistory.size > 1) {
            routeHistory.removeAt(routeHistory.lastIndex)
            val prev = routeHistory.last()
            previousRoute = currentRoute
            currentRoute = prev
        } else if (currentRoute != NavRoutes.HOME) {
            currentRoute = NavRoutes.HOME
            routeHistory.clear()
            routeHistory.add(NavRoutes.HOME)
        } else {
            // At root Dashboard -> Ask to Logout or Exit
            showLogoutDialog = true
        }
    }

    // System Back Press Handler
    BackHandler(enabled = true) {
        goBack()
    }

    // Dedicated Super Administrator Control Center (Completely Separate Flow)
    if (userProfile.isAdmin) {
        AdminDashboardScreen(
            onNavigateBack = {
                UserProfileManager.clearProfile(context)
                onLogout()
            }
        )
        return
    }

    // Start lifecycle-aware realtime subscription for the authenticated user
    LaunchedEffect(userProfile) {
        notificationRepository.startRealtimeSubscription(userProfile)
    }

    // Collect incoming realtime events for in-app floating banner
    LaunchedEffect(Unit) {
        notificationRepository.incomingNotification.collect { incoming ->
            incomingAlertNotification = incoming
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Keep repository active for session, or teardown if exiting
        }
    }

    Scaffold(
        bottomBar = {
            val activeBottomRoute = when (currentRoute) {
                NavRoutes.PROGRAMS, NavRoutes.FACULTY, NavRoutes.COURSES_OUTLINE, NavRoutes.ADMIN_REGISTRY, NavRoutes.CONTENT_MANAGEMENT -> previousRoute
                else -> currentRoute
            }
            val navItems = if (userProfile.isFaculty) {
                BottomNavItem.facultyItems
            } else {
                BottomNavItem.studentItems
            }

            GgcBottomBar(
                currentRoute = activeBottomRoute,
                items = navItems,
                onNavigateToRoute = { route ->
                    navigateTo(route)
                }
            )
        },
        modifier = Modifier.testTag("main_screen_container")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                NavRoutes.HOME -> HomeScreen(
                    onNavigateToPrograms = { navigateTo(NavRoutes.PROGRAMS) },
                    onNavigateToCoursesOutline = { navigateTo(NavRoutes.COURSES_OUTLINE) },
                    onNavigateToAdminRegistry = { navigateTo(NavRoutes.ADMIN_REGISTRY) },
                    onNavigateToContentManagement = { navigateTo(NavRoutes.CONTENT_MANAGEMENT) }
                )
                NavRoutes.ACADEMICS -> AcademicsScreen()
                NavRoutes.NOTICES -> NoticesScreen(
                    onBack = { goBack() }
                )
                NavRoutes.EVENTS -> EventsScreen(
                    onBack = { goBack() }
                )
                NavRoutes.PROFILE -> ProfileScreen(
                    onBack = { goBack() }
                )
                NavRoutes.ADMISSION -> AdmissionScreen(
                    onNavigateToPrograms = { navigateTo(NavRoutes.PROGRAMS) },
                    onNavigateToFaculty = { navigateTo(NavRoutes.FACULTY) }
                )
                NavRoutes.ALUMNI -> AlumniScreen()
                NavRoutes.ABOUT -> AboutScreen(
                    onNavigateToFaculty = { navigateTo(NavRoutes.FACULTY) },
                    onNavigateToAdminRegistry = { navigateTo(NavRoutes.ADMIN_REGISTRY) },
                    onNavigateToContentManagement = { navigateTo(NavRoutes.CONTENT_MANAGEMENT) }
                )
                NavRoutes.PROGRAMS -> ProgramsScreen(
                    onBack = { goBack() },
                    onNavigateToFaculty = { navigateTo(NavRoutes.FACULTY) },
                    onNavigateToCoursesOutline = { navigateTo(NavRoutes.COURSES_OUTLINE) }
                )
                NavRoutes.FACULTY -> FacultyTabScreen(
                    onBack = { goBack() }
                )
                NavRoutes.COURSES_OUTLINE -> CoursesOutlineScreen(
                    onBack = { goBack() }
                )
                NavRoutes.ADMIN_REGISTRY -> OfficialRegistryScreen(
                    onBack = { goBack() }
                )
                NavRoutes.CONTENT_MANAGEMENT -> ContentManagementScreen(
                    onBack = { goBack() }
                )
                NavRoutes.HOD_DASHBOARD -> com.example.ui.screens.hod.HodDashboardScreen(
                    onNavigateBack = { goBack() }
                )
                else -> HomeScreen(
                    onNavigateToPrograms = { navigateTo(NavRoutes.PROGRAMS) },
                    onNavigateToCoursesOutline = { navigateTo(NavRoutes.COURSES_OUTLINE) },
                    onNavigateToAdminRegistry = { navigateTo(NavRoutes.ADMIN_REGISTRY) },
                    onNavigateToContentManagement = { navigateTo(NavRoutes.CONTENT_MANAGEMENT) }
                )
            }

            // Floating In-App Realtime Notification Banner
            InAppNotificationBanner(
                notification = incomingAlertNotification,
                onDismiss = { incomingAlertNotification = null },
                onOpenContent = { notif ->
                    notif.id?.let { id -> notificationRepository.markAsRead(id) }
                    incomingAlertNotification = null
                    val contentType = notif.contentType ?: "announcement"
                    when (contentType.lowercase()) {
                        "course_outline" -> navigateTo(NavRoutes.COURSES_OUTLINE)
                        "faculty" -> navigateTo(NavRoutes.FACULTY)
                        else -> navigateTo(NavRoutes.HOME)
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Dashboard Back-Press Logout & Exit Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrandNavy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout Icon",
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Exit Application or Logout?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You are currently on the Home Dashboard. Would you like to log out of your account, or exit the app?",
                        fontSize = 13.sp,
                        color = Color(0xFF4A5568),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showLogoutDialog = false
                                UserProfileManager.clearProfile(context)
                                onLogout()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dialog_logout_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD32F2F)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Logout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                showLogoutDialog = false
                                (context as? android.app.Activity)?.finish()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dialog_exit_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandNavy,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exit App", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    TextButton(
                        onClick = { showLogoutDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("dialog_cancel_btn")
                    ) {
                        Text(
                            text = "Cancel (Stay on Dashboard)",
                            color = Color(0xFF718096),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            dismissButton = {}
        )
    }
}
