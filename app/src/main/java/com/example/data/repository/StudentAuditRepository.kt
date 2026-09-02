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
            seedInitialStudentLoginsIfEmpty()
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

    private suspend fun seedInitialStudentLoginsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = studentLoginDao.getAllLogins()
        if (existing.isNotEmpty()) return@withContext

        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

        val seedLogins = listOf(
            StudentLoginEntity(
                username = "asim.nawaz",
                fullName = "Asim Nawaz",
                rollNumber = "BSIT-F22-01",
                registrationNumber = "2022-GGC-IT-01",
                programLevel = "BS",
                programName = "BS Information Technology",
                semester = "Semester 4",
                loginTimestamp = now - 12 * 60 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 12 * 60 * 1000)),
                deviceInfo = "Samsung Galaxy A54 (Android 14)",
                sessionStatus = "Active"
            ),
            StudentLoginEntity(
                username = "hamza.tariq",
                fullName = "Hamza Tariq",
                rollNumber = "BSCS-F23-14",
                registrationNumber = "2023-GGC-CS-14",
                programLevel = "BS",
                programName = "BS Computer Science",
                semester = "Semester 2",
                loginTimestamp = now - 45 * 60 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 45 * 60 * 1000)),
                deviceInfo = "Xiaomi Redmi Note 12",
                sessionStatus = "Active"
            ),
            StudentLoginEntity(
                username = "ali.hassan",
                fullName = "Muhammad Ali Hassan",
                rollNumber = "FSC-24-102",
                registrationNumber = "2024-BISE-GGC-102",
                programLevel = "Intermediate",
                programName = "FSc Pre-Engineering",
                semester = "1st Year",
                loginTimestamp = now - 2 * 3600 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 2 * 3600 * 1000)),
                deviceInfo = "Infinix Hot 30 Play",
                sessionStatus = "Active"
            ),
            StudentLoginEntity(
                username = "usman.ghani",
                fullName = "Usman Ghani",
                rollNumber = "BSENG-F21-08",
                registrationNumber = "2021-GGC-ENG-08",
                programLevel = "BS",
                programName = "BS English",
                semester = "Semester 6",
                loginTimestamp = now - 5 * 3600 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 5 * 3600 * 1000)),
                deviceInfo = "Realme C55",
                sessionStatus = "Active"
            ),
            StudentLoginEntity(
                username = "zain.abbas",
                fullName = "Zain Abbas",
                rollNumber = "ICS-23-44",
                registrationNumber = "2023-BISE-GGC-44",
                programLevel = "Intermediate",
                programName = "ICS (Computer Science)",
                semester = "2nd Year",
                loginTimestamp = now - 18 * 3600 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 18 * 3600 * 1000)),
                deviceInfo = "Vivo Y20s",
                sessionStatus = "Active"
            ),
            StudentLoginEntity(
                username = "bilal.ahmed",
                fullName = "Bilal Ahmed",
                rollNumber = "BBA-F22-19",
                registrationNumber = "2022-GGC-BBA-19",
                programLevel = "BS",
                programName = "BBA (Hons)",
                semester = "Semester 4",
                loginTimestamp = now - 28 * 3600 * 1000,
                loginTimeFormatted = dateFormat.format(Date(now - 28 * 3600 * 1000)),
                deviceInfo = "Oppo A78",
                sessionStatus = "Active"
            )
        )

        for (item in seedLogins) {
            studentLoginDao.insertLogin(item)
        }
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
