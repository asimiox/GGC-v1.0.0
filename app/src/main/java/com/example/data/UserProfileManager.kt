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
    private const val KEY_ONBOARDED = "user_onboarded"

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
        _userProfile.value = UserProfile(
            name = name,
            programLevel = level,
            programName = program,
            semester = if (level == "Intermediate") null else semester
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

        _userProfile.value = UserProfile(
            name = cleanName,
            programLevel = level,
            programName = programName,
            semester = if (level == "Intermediate") null else semester
        )
    }

    fun isOnboarded(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDED, false)
    }
}
