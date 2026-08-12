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
import com.example.ui.screens.college.CollegeInfoScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notices.NoticesScreen
import com.example.ui.screens.profile.ProfileScreen

@Composable
fun MainScreen(
    onNavigateToAdminLogin: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(NavRoutes.HOME) }

    Scaffold(
        topBar = {
            GgcTopAppBar(
                title = "GGC M.B.DIN",
                subtitle = when (currentTab) {
                    NavRoutes.HOME -> "Official Companion App"
                    NavRoutes.ACADEMICS -> "Academic Programs & Courses"
                    NavRoutes.NOTICES -> "Verified Official Notices"
                    NavRoutes.COLLEGE -> "College Information & History"
                    NavRoutes.PROFILE -> "Student & Portal Services"
                    else -> "Government Graduate College"
                },
                onNotificationClick = { currentTab = NavRoutes.NOTICES },
                onAdminClick = onNavigateToAdminLogin
            )
        },
        bottomBar = {
            GgcBottomBar(
                currentRoute = currentTab,
                onNavigateToRoute = { route -> currentTab = route }
            )
        },
        modifier = Modifier.testTag("main_screen_container")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavRoutes.HOME -> HomeScreen(
                    onNavigateToAcademics = { currentTab = NavRoutes.ACADEMICS },
                    onNavigateToNotices = { currentTab = NavRoutes.NOTICES },
                    onNavigateToCollege = { currentTab = NavRoutes.COLLEGE },
                    onNavigateToProfile = { currentTab = NavRoutes.PROFILE }
                )
                NavRoutes.ACADEMICS -> AcademicsScreen()
                NavRoutes.NOTICES -> NoticesScreen()
                NavRoutes.COLLEGE -> CollegeInfoScreen()
                NavRoutes.PROFILE -> ProfileScreen()
            }
        }
    }
}
