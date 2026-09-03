package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PostViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostViewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertView(view: PostViewEntity): Long

    @Update
    suspend fun updateView(view: PostViewEntity)

    @Query("SELECT * FROM post_views WHERE postId = :postId ORDER BY viewTimestamp DESC")
    fun getViewsForPostFlow(postId: String): Flow<List<PostViewEntity>>

    @Query("SELECT * FROM post_views WHERE postId = :postId ORDER BY viewTimestamp DESC")
    suspend fun getViewsForPost(postId: String): List<PostViewEntity>

    @Query("SELECT * FROM post_views WHERE postId = :postId AND viewerUsername = :username LIMIT 1")
    suspend fun findExistingUserView(postId: String, username: String): PostViewEntity?

    @Query("SELECT COUNT(*) FROM post_views WHERE postId = :postId")
    fun getViewCountForPostFlow(postId: String): Flow<Int>

    @Query("SELECT * FROM post_views ORDER BY viewTimestamp DESC")
    fun getAllViewsFlow(): Flow<List<PostViewEntity>>

    @Query("DELETE FROM post_views WHERE postId = :postId")
    suspend fun clearViewsForPost(postId: String)

    @Query("DELETE FROM post_views")
    suspend fun clearAllViews()
}
