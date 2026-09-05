package com.example.data.repository

import android.content.Context
import com.example.data.UserProfileManager
import com.example.data.local.CollegeAppDatabase
import com.example.data.local.entity.PostViewEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAnalyticsRepository private constructor(context: Context) {
    private val database = CollegeAppDatabase.getInstance(context)
    private val postViewDao = database.postViewDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.getSharedPreferences("post_analytics_prefs", Context.MODE_PRIVATE)
            if (!prefs.getBoolean("clean_fresh_zero_v2", false)) {
                postViewDao.clearAllViews()
                prefs.edit().putBoolean("clean_fresh_zero_v2", true).apply()
            }
        }
    }

    fun getViewsForPostFlow(postId: String): Flow<List<PostViewEntity>> {
        return postViewDao.getViewsForPostFlow(postId)
    }

    suspend fun getViewsForPost(postId: String): List<PostViewEntity> = withContext(Dispatchers.IO) {
        postViewDao.getViewsForPost(postId)
    }

    fun getViewCountForPostFlow(postId: String): Flow<Int> {
        return postViewDao.getViewCountForPostFlow(postId)
    }

    fun getAllViewsFlow(): Flow<List<PostViewEntity>> {
        return postViewDao.getAllViewsFlow()
    }

    fun getViewsForUserFlow(username: String, rollNumber: String = ""): Flow<List<PostViewEntity>> {
        return postViewDao.getViewsForUserFlow(username.trim(), rollNumber.trim())
    }

    suspend fun recordPostView(
        postId: String,
        postTitle: String,
        postCategory: String = "General"
    ) = withContext(Dispatchers.IO) {
        val user = UserProfileManager.userProfile.value
        val username = user.username?.ifBlank { null } ?: user.rollNumber?.ifBlank { null } ?: "guest.user"
        val name = user.name.ifBlank { "Student" }
        val roll = user.rollNumber
        val role = when {
            user.appRole.name.contains("BS") -> "BS Student"
            user.appRole.name.contains("INTERMEDIATE") -> "Intermediate Student"
            user.appRole.name.contains("ADMIN") -> "Administrator"
            user.appRole.name.contains("HOD") -> "HOD"
            user.appRole.name.contains("TEACHER") -> "Faculty"
            else -> "Student"
        }
        val program = user.programName ?: user.department ?: "College Program"

        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val formattedDate = dateFormat.format(Date(now))

        val existing = postViewDao.findExistingUserView(postId = postId, username = username)
        if (existing != null) {
            val updated = existing.copy(
                viewTimestamp = now,
                viewTimeFormatted = formattedDate,
                viewCount = existing.viewCount + 1
            )
            postViewDao.updateView(updated)
        } else {
            val newView = PostViewEntity(
                postId = postId,
                postTitle = postTitle,
                postCategory = postCategory,
                viewerUsername = username,
                viewerName = name,
                viewerRollNumber = roll,
                viewerRole = role,
                viewerProgram = program,
                viewTimestamp = now,
                viewTimeFormatted = formattedDate,
                viewCount = 1
            )
            postViewDao.insertView(newView)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PostAnalyticsRepository? = null

        fun getInstance(context: Context): PostAnalyticsRepository {
            return INSTANCE ?: synchronized(this) {
                val repo = PostAnalyticsRepository(context.applicationContext)
                INSTANCE = repo
                repo
            }
        }
    }
}
