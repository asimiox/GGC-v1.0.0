package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val userProfile by UserProfileManager.userProfile.collectAsState()
    val notificationRepository = remember { NotificationRepository.getInstance(context) }

    var currentRoute by remember { mutableStateOf(NavRoutes.HOME) }
    var previousRoute by remember { mutableStateOf(NavRoutes.HOME) }
    var incomingAlertNotification by remember { mutableStateOf<AppNotificationDto?>(null) }

    // Dedicated Super Administrator Control Center (Completely Separate Flow)
    if (userProfile.isAdmin) {
        AdminDashboardScreen(
            onNavigateBack = {
                // Session reset handled by ViewModel
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
                    previousRoute = currentRoute
                    currentRoute = route
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
                    onNavigateToPrograms = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.PROGRAMS
                    },
                    onNavigateToCoursesOutline = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.COURSES_OUTLINE
                    },
                    onNavigateToAdminRegistry = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.ADMIN_REGISTRY
                    },
                    onNavigateToContentManagement = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.CONTENT_MANAGEMENT
                    }
                )
                NavRoutes.ACADEMICS -> AcademicsScreen()
                NavRoutes.NOTICES -> NoticesScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.EVENTS -> EventsScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.PROFILE -> ProfileScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.ADMISSION -> AdmissionScreen(
                    onNavigateToPrograms = {
                        previousRoute = NavRoutes.ADMISSION
                        currentRoute = NavRoutes.PROGRAMS
                    },
                    onNavigateToFaculty = {
                        previousRoute = NavRoutes.ADMISSION
                        currentRoute = NavRoutes.FACULTY
                    }
                )
                NavRoutes.ALUMNI -> AlumniScreen()
                NavRoutes.ABOUT -> AboutScreen(
                    onNavigateToFaculty = {
                        previousRoute = NavRoutes.ABOUT
                        currentRoute = NavRoutes.FACULTY
                    },
                    onNavigateToAdminRegistry = {
                        previousRoute = NavRoutes.ABOUT
                        currentRoute = NavRoutes.ADMIN_REGISTRY
                    },
                    onNavigateToContentManagement = {
                        previousRoute = NavRoutes.ABOUT
                        currentRoute = NavRoutes.CONTENT_MANAGEMENT
                    }
                )
                NavRoutes.PROGRAMS -> ProgramsScreen(
                    onBack = { currentRoute = previousRoute },
                    onNavigateToFaculty = {
                        previousRoute = NavRoutes.PROGRAMS
                        currentRoute = NavRoutes.FACULTY
                    },
                    onNavigateToCoursesOutline = {
                        previousRoute = NavRoutes.PROGRAMS
                        currentRoute = NavRoutes.COURSES_OUTLINE
                    }
                )
                NavRoutes.FACULTY -> FacultyTabScreen(
                    onBack = { currentRoute = previousRoute }
                )
                NavRoutes.COURSES_OUTLINE -> CoursesOutlineScreen(
                    onBack = { currentRoute = previousRoute }
                )
                NavRoutes.ADMIN_REGISTRY -> OfficialRegistryScreen(
                    onBack = { currentRoute = previousRoute }
                )
                NavRoutes.CONTENT_MANAGEMENT -> ContentManagementScreen(
                    onBack = { currentRoute = previousRoute }
                )
                NavRoutes.HOD_DASHBOARD -> com.example.ui.screens.hod.HodDashboardScreen(
                    onNavigateBack = { currentRoute = previousRoute }
                )
                else -> HomeScreen(
                    onNavigateToPrograms = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.PROGRAMS
                    },
                    onNavigateToCoursesOutline = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.COURSES_OUTLINE
                    },
                    onNavigateToAdminRegistry = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.ADMIN_REGISTRY
                    },
                    onNavigateToContentManagement = {
                        previousRoute = NavRoutes.HOME
                        currentRoute = NavRoutes.CONTENT_MANAGEMENT
                    }
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
                        "course_outline" -> {
                            previousRoute = currentRoute
                            currentRoute = NavRoutes.COURSES_OUTLINE
                        }
                        "faculty" -> {
                            previousRoute = currentRoute
                            currentRoute = NavRoutes.FACULTY
                        }
                        else -> {
                            previousRoute = currentRoute
                            currentRoute = NavRoutes.HOME
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
