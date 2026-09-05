package com.example.data.repository

import android.content.Context
import com.example.data.local.CollegeAppDatabase
import com.example.data.local.entity.StudentLoginEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LoggedInStudentSummary(
    val username: String,
    val fullName: String,
    val rollNumber: String,
    val registrationNumber: String?,
    val programLevel: String,
    val programName: String,
    val semester: String?,
    val totalLogins: Int,
    val firstLoginFormatted: String,
    val lastLoginFormatted: String,
    val lastLoginTimestamp: Long,
    val isCurrentlyActive: Boolean = true
)

class StudentAuditRepository private constructor(context: Context) {
    private val database = CollegeAppDatabase.getInstance(context)
    private val studentLoginDao = database.studentLoginDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.getSharedPreferences("student_audit_prefs", Context.MODE_PRIVATE)
            if (!prefs.getBoolean("clean_fresh_zero_v2", false)) {
                studentLoginDao.clearAllLogins()
                prefs.edit().putBoolean("clean_fresh_zero_v2", true).apply()
            }
        }
    }

    val allLoginsFlow: Flow<List<StudentLoginEntity>> = studentLoginDao.getAllLoginsFlow()
    val totalLoginCountFlow: Flow<Int> = studentLoginDao.getTotalLoginCountFlow()
    val distinctStudentCountFlow: Flow<Int> = studentLoginDao.getDistinctStudentCountFlow()

    val distinctStudentsFlow: Flow<List<LoggedInStudentSummary>> = allLoginsFlow.map { list ->
        list.groupBy { it.username.lowercase().trim() }
            .map { (_, logins) ->
                val sorted = logins.sortedByDescending { it.loginTimestamp }
                val latest = sorted.first()
                val earliest = sorted.last()
                LoggedInStudentSummary(
                    username = latest.username,
                    fullName = latest.fullName,
                    rollNumber = latest.rollNumber,
                    registrationNumber = latest.registrationNumber,
                    programLevel = latest.programLevel,
                    programName = latest.programName,
                    semester = latest.semester,
                    totalLogins = sorted.size,
                    firstLoginFormatted = earliest.loginTimeFormatted,
                    lastLoginFormatted = latest.loginTimeFormatted,
                    lastLoginTimestamp = latest.loginTimestamp,
                    isCurrentlyActive = (System.currentTimeMillis() - latest.loginTimestamp) < (24 * 60 * 60 * 1000)
                )
            }
            .sortedByDescending { it.lastLoginTimestamp }
    }

    fun getLoginsForStudentFlow(username: String, rollNumber: String = ""): Flow<List<StudentLoginEntity>> =
        allLoginsFlow.map { list ->
            list.filter {
                it.username.equals(username.trim(), ignoreCase = true) ||
                    (rollNumber.isNotBlank() && it.rollNumber.equals(rollNumber.trim(), ignoreCase = true))
            }.sortedByDescending { it.loginTimestamp }
        }

    suspend fun recordStudentLogin(
        username: String,
        fullName: String,
        rollNumber: String,
        registrationNumber: String? = null,
        programLevel: String,
        programName: String,
        semester: String? = null,
        deviceInfo: String = "Android Mobile App (GGC Portal)"
    ) = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val currentTime = System.currentTimeMillis()
        val formatted = dateFormat.format(Date(currentTime))

        val entity = StudentLoginEntity(
            username = username.trim().lowercase(),
            fullName = fullName.trim(),
            rollNumber = rollNumber.trim().uppercase(),
            registrationNumber = registrationNumber?.trim()?.uppercase(),
            programLevel = programLevel.trim(),
            programName = programName.trim(),
            semester = semester?.trim(),
            loginTimestamp = currentTime,
            loginTimeFormatted = formatted,
            deviceInfo = deviceInfo,
            sessionStatus = "Active"
        )
        studentLoginDao.insertLogin(entity)
    }

    suspend fun clearAllAuditLogs() = withContext(Dispatchers.IO) {
        studentLoginDao.clearAllLogins()
    }

    companion object {
        @Volatile
        private var INSTANCE: StudentAuditRepository? = null

        fun getInstance(context: Context): StudentAuditRepository {
            return INSTANCE ?: synchronized(this) {
                val repo = StudentAuditRepository(context.applicationContext)
                INSTANCE = repo
                repo
            }
        }
    }
}
