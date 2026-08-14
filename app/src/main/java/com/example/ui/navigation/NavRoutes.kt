package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"

    // Bottom Bar Destinations (Exact required 4 main screens)
    const val HOME = "home_tab"
    const val ADMISSION = "admission_tab"
    const val ALUMNI = "alumni_tab"
    const val ABOUT = "about_tab"

    // Sub routes
    const val PROGRAMS = "programs"
    const val FACULTY = "faculty"
    const val COURSES_OUTLINE = "courses_outline"
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(
        route = NavRoutes.HOME,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    )

    object Admission : BottomNavItem(
        route = NavRoutes.ADMISSION,
        title = "Admission",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        testTag = "nav_admission"
    )

    object Alumni : BottomNavItem(
        route = NavRoutes.ALUMNI,
        title = "Alumni",
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups,
        testTag = "nav_alumni"
    )

    object About : BottomNavItem(
        route = NavRoutes.ABOUT,
        title = "About",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        testTag = "nav_about"
    )
}

