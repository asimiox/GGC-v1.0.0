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
            seedInitialPostViewsIfEmpty()
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

    private suspend fun seedInitialPostViewsIfEmpty() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

        val seedViews = listOf(
            PostViewEntity(
                postId = "ann_fee_schedule_2026",
                postTitle = "Semester Fee Submission Deadline Notice",
                postCategory = "Fee",
                viewerUsername = "asim.nawaz",
                viewerName = "Asim Nawaz",
                viewerRollNumber = "BSIT-F22-01",
                viewerRole = "BS Student",
                viewerProgram = "BS Information Technology (Sem 4)",
                viewTimestamp = now - 15 * 60 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 15 * 60 * 1000)),
                viewCount = 3
            ),
            PostViewEntity(
                postId = "ann_fee_schedule_2026",
                postTitle = "Semester Fee Submission Deadline Notice",
                postCategory = "Fee",
                viewerUsername = "hamza.tariq",
                viewerName = "Hamza Tariq",
                viewerRollNumber = "BSCS-F23-14",
                viewerRole = "BS Student",
                viewerProgram = "BS Computer Science (Sem 2)",
                viewTimestamp = now - 35 * 60 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 35 * 60 * 1000)),
                viewCount = 1
            ),
            PostViewEntity(
                postId = "ann_fee_schedule_2026",
                postTitle = "Semester Fee Submission Deadline Notice",
                postCategory = "Fee",
                viewerUsername = "ali.hassan",
                viewerName = "Muhammad Ali Hassan",
                viewerRollNumber = "FSC-24-102",
                viewerRole = "Intermediate Student",
                viewerProgram = "FSc Pre-Engineering (1st Year)",
                viewTimestamp = now - 2 * 3600 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 2 * 3600 * 1000)),
                viewCount = 2
            ),
            PostViewEntity(
                postId = "ann_midterm_schedule_2026",
                postTitle = "Mid Term Examinations Schedule Announcement",
                postCategory = "Examinations",
                viewerUsername = "asim.nawaz",
                viewerName = "Asim Nawaz",
                viewerRollNumber = "BSIT-F22-01",
                viewerRole = "BS Student",
                viewerProgram = "BS Information Technology",
                viewTimestamp = now - 40 * 60 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 40 * 60 * 1000)),
                viewCount = 2
            ),
            PostViewEntity(
                postId = "ann_midterm_schedule_2026",
                postTitle = "Mid Term Examinations Schedule Announcement",
                postCategory = "Examinations",
                viewerUsername = "zain.abbas",
                viewerName = "Zain Abbas",
                viewerRollNumber = "ICS-23-44",
                viewerRole = "Intermediate Student",
                viewerProgram = "ICS (Computer Science)",
                viewTimestamp = now - 4 * 3600 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 4 * 3600 * 1000)),
                viewCount = 1
            ),
            PostViewEntity(
                postId = "ann_midterm_schedule_2026",
                postTitle = "Mid Term Examinations Schedule Announcement",
                postCategory = "Examinations",
                viewerUsername = "usman.ghani",
                viewerName = "Usman Ghani",
                viewerRollNumber = "BSENG-F21-08",
                viewerRole = "BS Student",
                viewerProgram = "BS English (Sem 6)",
                viewTimestamp = now - 6 * 3600 * 1000,
                viewTimeFormatted = dateFormat.format(Date(now - 6 * 3600 * 1000)),
                viewCount = 1
            )
        )

        for (item in seedViews) {
            val exists = postViewDao.findExistingUserView(item.postId, item.viewerUsername)
            if (exists == null) {
                postViewDao.insertView(item)
            }
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
