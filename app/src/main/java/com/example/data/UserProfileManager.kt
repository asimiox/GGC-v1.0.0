package com.example.data

import android.content.Context
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserProfileManager {
    private const val PREFS_NAME = "ggc_user_prefs"
    private const val KEY_NAME = "user_name"
    private const val KEY_PROGRAM_LEVEL = "user_program_level"
    private const val KEY_PROGRAM_NAME = "user_program_name"
    private const val KEY_SEMESTER = "user_semester"
    private const val KEY_ROLL_NUMBER = "user_roll_number"
    private const val KEY_REG_NUMBER = "user_reg_number"
    private const val KEY_USERNAME = "user_username"
    private const val KEY_IS_VERIFIED = "user_is_verified"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ONBOARDED = "user_onboarded"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_DEPARTMENT = "user_department"
    private const val KEY_DESIGNATION = "user_designation"
    private const val KEY_QUALIFICATION = "user_qualification"
    private const val KEY_FACULTY_ID = "user_faculty_id"
    private const val KEY_INSTITUTIONAL_EMAIL = "user_institutional_email"

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Student",
            programLevel = "BS",
            programName = "BS Information Technology",
            semester = "Semester 1"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NAME, "Student") ?: "Student"
        val level = prefs.getString(KEY_PROGRAM_LEVEL, "BS") ?: "BS"
        val program = prefs.getString(KEY_PROGRAM_NAME, "BS Information Technology") ?: "BS Information Technology"
        val semester = prefs.getString(KEY_SEMESTER, "Semester 1")
        val roll = prefs.getString(KEY_ROLL_NUMBER, null)
        val reg = prefs.getString(KEY_REG_NUMBER, null)
        val username = prefs.getString(KEY_USERNAME, null)
        val isVerified = prefs.getBoolean(KEY_IS_VERIFIED, false)
        val userId = prefs.getString(KEY_USER_ID, null)
        val userRole = prefs.getString(KEY_USER_ROLE, "Student") ?: "Student"
        val department = prefs.getString(KEY_DEPARTMENT, null)
        val designation = prefs.getString(KEY_DESIGNATION, null)
        val qualification = prefs.getString(KEY_QUALIFICATION, null)
        val facultyId = prefs.getString(KEY_FACULTY_ID, null)
        val institutionalEmail = prefs.getString(KEY_INSTITUTIONAL_EMAIL, null)

        _userProfile.value = UserProfile(
            name = name,
            programLevel = level,
            programName = program,
            semester = if (level == "Intermediate" || userRole == "Faculty") null else semester,
            rollNumber = roll,
            registrationNumber = reg,
            username = username,
            isVerified = isVerified,
            userId = userId,
            userRole = userRole,
            department = department,
            designation = designation,
            qualification = qualification,
            facultyId = facultyId,
            institutionalEmail = institutionalEmail
        )
    }

    fun saveProfile(
        context: Context,
        name: String,
        level: String,
        programName: String,
        semester: String?
    ) {
        val cleanName = name.trim().ifEmpty { "Student" }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, cleanName)
            .putString(KEY_PROGRAM_LEVEL, level)
            .putString(KEY_PROGRAM_NAME, programName)
            .putString(KEY_SEMESTER, semester)
            .putBoolean(KEY_ONBOARDED, true)
            .apply()

        _userProfile.value = _userProfile.value.copy(
            name = cleanName,
            programLevel = level,
            programName = programName,
            semester = if (level == "Intermediate") null else semester
        )
    }

    fun saveVerifiedIntermediateProfile(
        context: Context,
        firstName: String,
        lastName: String,
        rollNumber: String,
        registrationNumber: String,
        programName: String,
        username: String,
        userId: String?
    ) {
        val fullName = "${firstName.trim()} ${lastName.trim()}".trim().ifEmpty { username }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, fullName)
            .putString(KEY_PROGRAM_LEVEL, "Intermediate")
            .putString(KEY_PROGRAM_NAME, programName)
            .putString(KEY_SEMESTER, null)
            .putString(KEY_ROLL_NUMBER, rollNumber.trim().uppercase())
            .putString(KEY_REG_NUMBER, registrationNumber.trim().uppercase())
            .putString(KEY_USERNAME, username.trim().lowercase())
            .putBoolean(KEY_IS_VERIFIED, true)
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_ONBOARDED, true)
            .apply()

        _userProfile.value = UserProfile(
            name = fullName,
            programLevel = "Intermediate",
            programName = programName,
            semester = null,
            rollNumber = rollNumber.trim().uppercase(),
            registrationNumber = registrationNumber.trim().uppercase(),
            username = username.trim().lowercase(),
            isVerified = true,
            userId = userId
        )
    }

    fun saveVerifiedBsProfile(
        context: Context,
        firstName: String,
        lastName: String,
        rollNumber: String,
        registrationNumber: String,
        programName: String,
        semester: String?,
        username: String,
        userId: String?
    ) {
        val fullName = "${firstName.trim()} ${lastName.trim()}".trim().ifEmpty { username }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, fullName)
            .putString(KEY_PROGRAM_LEVEL, "BS")
            .putString(KEY_PROGRAM_NAME, programName)
            .putString(KEY_SEMESTER, semester ?: "Semester 1")
            .putString(KEY_ROLL_NUMBER, rollNumber.trim().uppercase())
            .putString(KEY_REG_NUMBER, registrationNumber.trim().uppercase())
            .putString(KEY_USERNAME, username.trim().lowercase())
            .putBoolean(KEY_IS_VERIFIED, true)
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_ONBOARDED, true)
            .apply()

        _userProfile.value = UserProfile(
            name = fullName,
            programLevel = "BS",
            programName = programName,
            semester = semester ?: "Semester 1",
            rollNumber = rollNumber.trim().uppercase(),
            registrationNumber = registrationNumber.trim().uppercase(),
            username = username.trim().lowercase(),
            isVerified = true,
            userId = userId
        )
    }

    fun saveVerifiedFacultyProfile(
        context: Context,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        facultyId: String,
        institutionalEmail: String?,
        username: String,
        userId: String?
    ) {
        val cleanName = fullName.trim().ifEmpty { username }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, cleanName)
            .putString(KEY_PROGRAM_LEVEL, "Faculty")
            .putString(KEY_PROGRAM_NAME, "Department of $department")
            .putString(KEY_SEMESTER, null)
            .putString(KEY_USER_ROLE, "Faculty")
            .putString(KEY_DEPARTMENT, department.trim())
            .putString(KEY_DESIGNATION, designation.trim())
            .putString(KEY_QUALIFICATION, qualification.trim())
            .putString(KEY_FACULTY_ID, facultyId.trim().uppercase())
            .putString(KEY_INSTITUTIONAL_EMAIL, institutionalEmail?.trim()?.lowercase())
            .putString(KEY_USERNAME, username.trim().lowercase())
            .putBoolean(KEY_IS_VERIFIED, true)
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_ONBOARDED, true)
            .apply()

        _userProfile.value = UserProfile(
            name = cleanName,
            programLevel = "Faculty",
            programName = "Department of $department",
            semester = null,
            userRole = "Faculty",
            department = department.trim(),
            designation = designation.trim(),
            qualification = qualification.trim(),
            facultyId = facultyId.trim().uppercase(),
            institutionalEmail = institutionalEmail?.trim()?.lowercase(),
            username = username.trim().lowercase(),
            isVerified = true,
            userId = userId
        )
    }

    fun clearProfile(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        _userProfile.value = UserProfile(
            name = "Student",
            programLevel = "BS",
            programName = "BS Information Technology",
            semester = "Semester 1"
        )
    }

    fun isOnboarded(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDED, false)
    }
}
