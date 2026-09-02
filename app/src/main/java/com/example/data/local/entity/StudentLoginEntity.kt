package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists every login event by students into local Room database.
 * Admin can view the full timeline, audit trail, and distinct logged-in student profiles.
 */
@Entity(tableName = "student_logins")
data class StudentLoginEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val fullName: String,
    val rollNumber: String,
    val registrationNumber: String? = null,
    val programLevel: String, // "BS" or "Intermediate"
    val programName: String,
    val semester: String? = null,
    val loginTimestamp: Long = System.currentTimeMillis(),
    val loginTimeFormatted: String,
    val deviceInfo: String = "Android Mobile App (GGC Portal)",
    val sessionStatus: String = "Active" // "Active", "Logged Out", "Session Restored"
)
