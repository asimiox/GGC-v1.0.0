package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"

    // Student Navigation Destinations (Exact required 5 main tabs)
    const val HOME = "home_tab"
    const val ACADEMICS = "academics_tab"
    const val NOTICES = "notices_tab"
    const val EVENTS = "events_tab"
    const val PROFILE = "profile_tab"

    // Sub & College routes
    const val ADMISSION = "admission_tab"
    const val ALUMNI = "alumni_tab"
    const val ABOUT = "about_tab"
    const val PROGRAMS = "programs"
    const val FACULTY = "faculty"
    const val COURSES_OUTLINE = "courses_outline"
    const val ADMIN_REGISTRY = "admin_registry"
    const val CONTENT_MANAGEMENT = "content_management"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val HOD_DASHBOARD = "hod_dashboard"
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

    object Academics : BottomNavItem(
        route = NavRoutes.ACADEMICS,
        title = "Academics",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        testTag = "nav_academics"
    )

    object Notices : BottomNavItem(
        route = NavRoutes.NOTICES,
        title = "Notices",
        selectedIcon = Icons.Filled.Campaign,
        unselectedIcon = Icons.Outlined.Campaign,
        testTag = "nav_notices"
    )

    object Events : BottomNavItem(
        route = NavRoutes.EVENTS,
        title = "Events",
        selectedIcon = Icons.Filled.Event,
        unselectedIcon = Icons.Outlined.Event,
        testTag = "nav_events"
    )

    object Profile : BottomNavItem(
        route = NavRoutes.PROFILE,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )

    object ContentHub : BottomNavItem(
        route = NavRoutes.CONTENT_MANAGEMENT,
        title = "Content Hub",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        testTag = "nav_content_hub"
    )

    // Legacy/College tabs
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

    companion object {
        val studentItems: List<BottomNavItem> = listOf(
            Home,
            Academics,
            Notices,
            Events,
            Profile
        )

        val facultyItems: List<BottomNavItem> = listOf(
            Home,
            ContentHub,
            Notices,
            Events,
            Profile
        )

        val defaultItems: List<BottomNavItem> = listOf(
            Home,
            Admission,
            Alumni,
            About
        )
    }
}

