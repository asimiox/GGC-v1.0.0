package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "Student",
    val programLevel: String = "BS",
    val programName: String = "BS Information Technology",
    val semester: String? = "Semester 1",
    val rollNumber: String? = null,
    val registrationNumber: String? = null,
    val username: String? = null,
    val isVerified: Boolean = false,
    val userId: String? = null,
    val userRole: String = "Student",
    val department: String? = null,
    val designation: String? = null,
    val qualification: String? = null,
    val facultyId: String? = null,
    val institutionalEmail: String? = null,
    val appRole: AppRole = AppRole.STUDENT_BS
) {
    val isFaculty: Boolean
        get() = appRole.isTeacherLevel || userRole.equals("Faculty", ignoreCase = true) || programLevel.equals("Faculty", ignoreCase = true)

    val isHod: Boolean
        get() = appRole == AppRole.HOD || appRole == AppRole.ADMIN

    val isAdmin: Boolean
        get() = appRole == AppRole.ADMIN
}

