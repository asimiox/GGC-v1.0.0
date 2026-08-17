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
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.about.AboutScreen
import com.example.ui.screens.admin.OfficialRegistryScreen
import com.example.ui.screens.admin.content.ContentManagementScreen
import com.example.ui.screens.admission.AdmissionScreen
import com.example.ui.screens.alumni.AlumniScreen
import com.example.ui.screens.courses.CoursesOutlineScreen
import com.example.ui.screens.faculty.FacultyTabScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.programs.ProgramsScreen

@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf(NavRoutes.HOME) }
    var previousRoute by remember { mutableStateOf(NavRoutes.HOME) }

    Scaffold(
        bottomBar = {
            val activeBottomRoute = when (currentRoute) {
                NavRoutes.PROGRAMS, NavRoutes.FACULTY, NavRoutes.COURSES_OUTLINE, NavRoutes.ADMIN_REGISTRY, NavRoutes.CONTENT_MANAGEMENT -> previousRoute
                else -> currentRoute
            }
            GgcBottomBar(
                currentRoute = activeBottomRoute,
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
        }
    }
}
