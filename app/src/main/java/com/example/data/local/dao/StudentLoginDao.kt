package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.StudentLoginEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentLoginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogin(login: StudentLoginEntity): Long

    @Query("SELECT * FROM student_logins ORDER BY loginTimestamp DESC")
    fun getAllLoginsFlow(): Flow<List<StudentLoginEntity>>

    @Query("SELECT * FROM student_logins ORDER BY loginTimestamp DESC")
    suspend fun getAllLogins(): List<StudentLoginEntity>

    @Query("SELECT * FROM student_logins WHERE programLevel = :level ORDER BY loginTimestamp DESC")
    fun getLoginsByLevelFlow(level: String): Flow<List<StudentLoginEntity>>

    @Query("SELECT * FROM student_logins WHERE username = :username ORDER BY loginTimestamp DESC")
    fun getLoginsByUsername(username: String): Flow<List<StudentLoginEntity>>

    @Query("SELECT COUNT(*) FROM student_logins")
    fun getTotalLoginCountFlow(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT username) FROM student_logins")
    fun getDistinctStudentCountFlow(): Flow<Int>

    @Query("DELETE FROM student_logins")
    suspend fun clearAllLogins()
}
