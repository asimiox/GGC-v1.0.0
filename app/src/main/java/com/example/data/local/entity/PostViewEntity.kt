package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists every announcement / post view record in Room database ("Kis kis ne post dekha").
 * Tracks who read the post, when they read it, their roll number, and program.
 */
@Entity(tableName = "post_views")
data class PostViewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: String,
    val postTitle: String,
    val postCategory: String = "General",
    val viewerUsername: String,
    val viewerName: String,
    val viewerRollNumber: String? = null,
    val viewerRole: String, // "BS Student", "Intermediate Student", "Faculty", "Admin", "Guest"
    val viewerProgram: String? = null,
    val viewTimestamp: Long = System.currentTimeMillis(),
    val viewTimeFormatted: String,
    val viewCount: Int = 1
)
