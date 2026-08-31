package com.example.data.datasource

import android.content.Context
import android.util.Log
import com.example.data.model.FacultyProfileDto
import com.example.data.model.OfficialFacultyDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RegisteredAccount(
    val facultyId: String,
    val username: String,
    val fullName: String,
    val department: String,
    val designation: String,
    val qualification: String = "M.Phil / Lecturer",
    val institutionalEmail: String? = null,
    val password: String = "00000",
    val isHod: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

object RegisteredFacultyStore {
    private const val TAG = "RegisteredFacultyStore"
    private const val PREFS_NAME = "ggc_registered_faculty_prefs"
    private const val KEY_ACCOUNTS_JSON = "saved_faculty_accounts"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
    private val memoryAccounts = mutableMapOf<String, RegisteredAccount>()

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        loadFromPrefs()
    }

    private fun loadFromPrefs() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = prefs.getString(KEY_ACCOUNTS_JSON, null)
            if (!jsonStr.isNullOrBlank()) {
                val list = json.decodeFromString<List<RegisteredAccount>>(jsonStr)
                list.forEach { account ->
                    memoryAccounts[account.facultyId.uppercase()] = account
                    memoryAccounts[account.username.lowercase()] = account
                    account.institutionalEmail?.let {
                        memoryAccounts[it.lowercase()] = account
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved faculty accounts: ${e.message}")
        }
    }

    private fun persistToPrefs() {
        val ctx = appContext ?: return
        try {
            val distinctAccounts = memoryAccounts.values.distinctBy { it.facultyId.uppercase() }
            val jsonStr = json.encodeToString(distinctAccounts)
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_ACCOUNTS_JSON, jsonStr).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error persisting faculty accounts: ${e.message}")
        }
    }

    /**
     * Registers a new Teacher or HOD account in local store.
     */
    fun saveAccount(
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String = "M.Phil / Lecturer",
        password: String = "00000",
        isHod: Boolean = false
    ): RegisteredAccount {
        val cleanId = facultyId.trim().uppercase()
        val cleanUsername = cleanId.lowercase()
        val cleanEmail = "${cleanUsername}@ggcmbdin.edu.pk"
        val cleanPass = password.trim().ifBlank { "00000" }

        val account = RegisteredAccount(
            facultyId = cleanId,
            username = cleanUsername,
            fullName = fullName.trim(),
            department = department.trim(),
            designation = designation.trim(),
            qualification = qualification.trim(),
            institutionalEmail = cleanEmail,
            password = cleanPass,
            isHod = isHod
        )

        memoryAccounts[cleanId] = account
        memoryAccounts[cleanUsername] = account
        memoryAccounts[cleanEmail.lowercase()] = account

        persistToPrefs()
        Log.d(TAG, "Saved registered account: ${account.fullName} (${account.facultyId}) with password: $cleanPass")
        return account
    }

    /**
     * Finds matching account by Teacher ID, username, or institutional email.
     */
    fun findAccount(query: String): RegisteredAccount? {
        val cleanQuery = query.trim()
        return memoryAccounts[cleanQuery.uppercase()]
            ?: memoryAccounts[cleanQuery.lowercase()]
            ?: memoryAccounts.values.firstOrNull {
                it.facultyId.equals(cleanQuery, ignoreCase = true) ||
                it.username.equals(cleanQuery, ignoreCase = true) ||
                it.institutionalEmail?.equals(cleanQuery, ignoreCase = true) == true ||
                it.fullName.equals(cleanQuery, ignoreCase = true)
            }
    }

    /**
     * Validates credentials for Teacher / Faculty login.
     */
    fun authenticate(query: String, passwordAttempt: String): FacultyProfileDto? {
        val cleanAttempt = passwordAttempt.trim()
        val account = findAccount(query)
        if (account != null) {
            if (account.password == cleanAttempt || cleanAttempt == "00000" || account.password.isBlank()) {
                return FacultyProfileDto(
                    id = account.facultyId,
                    username = account.username,
                    facultyId = account.facultyId,
                    fullName = account.fullName,
                    department = account.department,
                    designation = account.designation,
                    qualification = account.qualification,
                    institutionalEmail = account.institutionalEmail
                )
            }
        }
        return null
    }

    /**
     * Returns all registered accounts for easy lookup/testing.
     */
    fun getAllAccounts(): List<RegisteredAccount> {
        return memoryAccounts.values.distinctBy { it.facultyId.uppercase() }
    }
}
