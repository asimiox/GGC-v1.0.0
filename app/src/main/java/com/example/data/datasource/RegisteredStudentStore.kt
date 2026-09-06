package com.example.data.datasource

import android.content.Context
import android.util.Log
import com.example.data.model.BsStudentProfileDto
import com.example.data.model.IntermediateStudentProfileDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RegisteredBsStudentAccount(
    val id: String = "",
    val username: String,
    val firstName: String,
    val lastName: String,
    val rollNumber: String,
    val registrationNumber: String,
    val program: String,
    val session: String = "2024-2028",
    val semester: String = "Semester 1",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class RegisteredIntermediateStudentAccount(
    val id: String = "",
    val username: String,
    val firstName: String,
    val lastName: String,
    val rollNumber: String,
    val registrationNumber: String,
    val program: String,
    val group: String = "FSc Pre-Medical",
    val section: String = "A",
    val createdAt: Long = System.currentTimeMillis()
)

object RegisteredStudentStore {
    private const val TAG = "RegisteredStudentStore"
    private const val PREFS_NAME = "ggc_registered_students_prefs"
    private const val KEY_BS_STUDENTS_JSON = "saved_bs_student_accounts"
    private const val KEY_INTER_STUDENTS_JSON = "saved_inter_student_accounts"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
    private val memoryBsAccounts = mutableMapOf<String, RegisteredBsStudentAccount>()
    private val memoryInterAccounts = mutableMapOf<String, RegisteredIntermediateStudentAccount>()

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        loadFromPrefs()
    }

    private fun loadFromPrefs() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            
            // Load BS Students
            val bsJson = prefs.getString(KEY_BS_STUDENTS_JSON, null)
            if (!bsJson.isNullOrBlank()) {
                val list = json.decodeFromString<List<RegisteredBsStudentAccount>>(bsJson)
                list.forEach { account ->
                    memoryBsAccounts[account.rollNumber.uppercase()] = account
                    memoryBsAccounts[account.registrationNumber.uppercase()] = account
                    memoryBsAccounts[account.username.lowercase()] = account
                }
            }

            // Load Intermediate Students
            val interJson = prefs.getString(KEY_INTER_STUDENTS_JSON, null)
            if (!interJson.isNullOrBlank()) {
                val list = json.decodeFromString<List<RegisteredIntermediateStudentAccount>>(interJson)
                list.forEach { account ->
                    memoryInterAccounts[account.rollNumber.uppercase()] = account
                    memoryInterAccounts[account.registrationNumber.uppercase()] = account
                    memoryInterAccounts[account.username.lowercase()] = account
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved student accounts: ${e.message}")
        }
    }

    private fun persistToPrefs() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val distinctBs = memoryBsAccounts.values.distinctBy { it.rollNumber.uppercase() }
            val distinctInter = memoryInterAccounts.values.distinctBy { it.rollNumber.uppercase() }

            prefs.edit()
                .putString(KEY_BS_STUDENTS_JSON, json.encodeToString(distinctBs))
                .putString(KEY_INTER_STUDENTS_JSON, json.encodeToString(distinctInter))
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error persisting student accounts: ${e.message}")
        }
    }

    fun saveBsAccount(account: RegisteredBsStudentAccount) {
        memoryBsAccounts[account.rollNumber.uppercase()] = account
        memoryBsAccounts[account.registrationNumber.uppercase()] = account
        memoryBsAccounts[account.username.lowercase()] = account
        persistToPrefs()
    }

    fun saveBsAccounts(accounts: List<RegisteredBsStudentAccount>) {
        if (accounts.isEmpty()) return
        accounts.forEach { account ->
            memoryBsAccounts[account.rollNumber.uppercase()] = account
            memoryBsAccounts[account.registrationNumber.uppercase()] = account
            memoryBsAccounts[account.username.lowercase()] = account
        }
        persistToPrefs()
    }

    fun saveIntermediateAccount(account: RegisteredIntermediateStudentAccount) {
        memoryInterAccounts[account.rollNumber.uppercase()] = account
        memoryInterAccounts[account.registrationNumber.uppercase()] = account
        memoryInterAccounts[account.username.lowercase()] = account
        persistToPrefs()
    }

    fun saveIntermediateAccounts(accounts: List<RegisteredIntermediateStudentAccount>) {
        if (accounts.isEmpty()) return
        accounts.forEach { account ->
            memoryInterAccounts[account.rollNumber.uppercase()] = account
            memoryInterAccounts[account.registrationNumber.uppercase()] = account
            memoryInterAccounts[account.username.lowercase()] = account
        }
        persistToPrefs()
    }

    fun findBsAccount(query: String): RegisteredBsStudentAccount? {
        val cleanQuery = query.trim().uppercase()
        return memoryBsAccounts[cleanQuery] ?: memoryBsAccounts[query.trim().lowercase()]
    }

    fun findIntermediateAccount(query: String): RegisteredIntermediateStudentAccount? {
        val cleanQuery = query.trim().uppercase()
        return memoryInterAccounts[cleanQuery] ?: memoryInterAccounts[query.trim().lowercase()]
    }
}
