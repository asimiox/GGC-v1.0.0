package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.GgcBottomBar
import com.example.ui.components.GgcTopAppBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.academics.AcademicsScreen
import com.example.ui.screens.academics.components.FacultyScreen
import com.example.ui.screens.academics.components.ProgramsScreen
import com.example.ui.screens.college.CollegeInfoScreen
import com.example.ui.screens.common.ComingSoonScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notices.NoticesScreen
import com.example.ui.screens.profile.ProfileScreen

@Composable
fun MainScreen(
    onNavigateToAdminLogin: () -> Unit = {}
) {
    var currentRoute by remember { mutableStateOf(NavRoutes.HOME) }

    val isMainTab = when (currentRoute) {
        NavRoutes.HOME, NavRoutes.ACADEMICS, NavRoutes.NOTICES, NavRoutes.COLLEGE, NavRoutes.PROFILE -> true
        else -> false
    }

    Scaffold(
        topBar = {
            GgcTopAppBar(
                title = "GGC M.B.Din",
                subtitle = when (currentRoute) {
                    NavRoutes.HOME -> "Official App"
                    NavRoutes.ACADEMICS, NavRoutes.DEPARTMENTS, NavRoutes.PROGRAMS -> "Academic Programs & Courses"
                    NavRoutes.NOTICES -> "Verified Official Notices"
                    NavRoutes.COLLEGE -> "College Information & History"
                    NavRoutes.PROFILE -> "Student & Portal Services"
                    NavRoutes.FACULTY -> "Faculty & Academic Staff"
                    NavRoutes.EVENTS -> "College Events & Activities"
                    NavRoutes.STUDENT_SECTION -> "Student Section & Portal"
                    NavRoutes.ACADEMIC_RESOURCES -> "Academic Resources & Notes"
                    else -> "Government Graduate College"
                },
                onBackClick = if (!isMainTab) { { currentRoute = NavRoutes.HOME } } else null,
                onNotificationClick = { currentRoute = NavRoutes.NOTICES },
                onAdminClick = onNavigateToAdminLogin
            )
        },
        bottomBar = {
            val activeBottomRoute = when (currentRoute) {
                NavRoutes.DEPARTMENTS, NavRoutes.PROGRAMS -> NavRoutes.ACADEMICS
                NavRoutes.FACULTY, NavRoutes.EVENTS, NavRoutes.STUDENT_SECTION, NavRoutes.ACADEMIC_RESOURCES -> NavRoutes.HOME
                else -> currentRoute
            }
            GgcBottomBar(
                currentRoute = activeBottomRoute,
                onNavigateToRoute = { route -> currentRoute = route }
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
                    onNavigateToAcademics = { currentRoute = NavRoutes.ACADEMICS },
                    onNavigateToNotices = { currentRoute = NavRoutes.NOTICES },
                    onNavigateToCollege = { currentRoute = NavRoutes.COLLEGE },
                    onNavigateToProfile = { currentRoute = NavRoutes.PROFILE },
                    onNavigateToDepartments = { currentRoute = NavRoutes.ACADEMICS },
                    onNavigateToFaculty = { currentRoute = NavRoutes.FACULTY },
                    onNavigateToPrograms = { currentRoute = NavRoutes.PROGRAMS },
                    onNavigateToEvents = { currentRoute = NavRoutes.EVENTS },
                    onNavigateToStudentSection = { currentRoute = NavRoutes.STUDENT_SECTION },
                    onNavigateToResources = { currentRoute = NavRoutes.ACADEMIC_RESOURCES }
                )
                NavRoutes.ACADEMICS, NavRoutes.DEPARTMENTS -> AcademicsScreen()
                NavRoutes.PROGRAMS -> ProgramsScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.FACULTY -> FacultyScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.NOTICES -> NoticesScreen()
                NavRoutes.COLLEGE -> CollegeInfoScreen()
                NavRoutes.PROFILE -> ProfileScreen()
                NavRoutes.EVENTS -> EventsScreen(
                    onBack = { currentRoute = NavRoutes.HOME }
                )
                NavRoutes.STUDENT_SECTION -> ComingSoonScreen(
                    title = "Student Section & Portal",
                    description = "Student portal services, fee schedules, code of conduct, and campus guidelines are under active preparation.",
                    onNavigateBack = { currentRoute = NavRoutes.HOME },
                    onNavigateToNotices = { currentRoute = NavRoutes.NOTICES }
                )
                NavRoutes.ACADEMIC_RESOURCES -> ComingSoonScreen(
                    title = "Academic Resources & Notes",
                    description = "Verified course outlines, lecture notes, and past paper repositories will be available in upcoming updates.",
                    onNavigateBack = { currentRoute = NavRoutes.HOME },
                    onNavigateToNotices = { currentRoute = NavRoutes.NOTICES }
                )
            }
        }
    }
}

