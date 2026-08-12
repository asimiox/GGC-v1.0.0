package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.outlined.Announcement
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    
    // Bottom Bar Destinations
    const val HOME = "home_tab"
    const val ACADEMICS = "academics_tab"
    const val NOTICES = "notices_tab"
    const val COLLEGE = "college_tab"
    const val PROFILE = "profile_tab"

    // Major College Feature Routes
    const val DEPARTMENTS = "departments"
    const val DEPARTMENT_DETAIL = "department_detail"
    const val FACULTY = "faculty"
    const val PROGRAMS = "programs"
    const val EVENTS = "events"
    const val STUDENT_SECTION = "student_section"
    const val ACADEMIC_RESOURCES = "academic_resources"

    // Detail Screens
    const val COLLEGE_HISTORY = "college_history"
    const val PRINCIPAL_MESSAGE = "principal_message"
    const val VISION_MISSION = "vision_mission"
    const val CONTACT_INFO = "contact_info"
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
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        testTag = "nav_academics"
    )

    object Notices : BottomNavItem(
        route = NavRoutes.NOTICES,
        title = "Notices",
        selectedIcon = Icons.AutoMirrored.Filled.Announcement,
        unselectedIcon = Icons.AutoMirrored.Outlined.Announcement,
        testTag = "nav_notices"
    )

    object College : BottomNavItem(
        route = NavRoutes.COLLEGE,
        title = "College",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        testTag = "nav_college"
    )

    object Profile : BottomNavItem(
        route = NavRoutes.PROFILE,
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        testTag = "nav_profile"
    )
}
