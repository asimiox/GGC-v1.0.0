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
        // First initialize with verified official faculty crawled from ggcmbdin.edu.pk
        seedOfficialWebsiteFaculty()

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

    /**
     * Seeds the authentic official faculty roster crawled from official website ggcmbdin.edu.pk
     */
    private fun seedOfficialWebsiteFaculty() {
        val officialRoster = listOf(
            // Principal / Executive Admin
            RegisteredAccount(
                facultyId = "ADMIN-01",
                username = "principal",
                fullName = "Prof. Ameer Ahmad",
                department = "College Administration",
                designation = "Principal & Chief Administrator",
                qualification = "M.Sc, M.Phil / Principal",
                institutionalEmail = "principal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            // HODs crawled directly from ggcmbdin.edu.pk
            RegisteredAccount(
                facultyId = "IT-HOD-01",
                username = "faiyaz",
                fullName = "Prof. Muhammad Faiyaz",
                department = "Information Technology",
                designation = "Head of Department (HOD)",
                qualification = "MS Computer Science / Associate Professor",
                institutionalEmail = "faiyaz@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "CHM-HOD-01",
                username = "umer.minhas",
                fullName = "Prof. Muhammad Umer Minhas",
                department = "Chemistry",
                designation = "Head of Department (HOD)",
                qualification = "Ph.D Chemistry / Associate Professor",
                institutionalEmail = "umer.minhas@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "PHY-HOD-01",
                username = "asif.zaman",
                fullName = "Prof. Muhammad Asif Zaman",
                department = "Physics",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Physics / Associate Professor",
                institutionalEmail = "asif.zaman@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "MTH-HOD-01",
                username = "abdul.manan",
                fullName = "Prof. Abdul Manan",
                department = "Mathematics",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Mathematics / Associate Professor",
                institutionalEmail = "abdul.manan@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ENG-HOD-01",
                username = "ikram.bhatti",
                fullName = "Prof. Ikram Bhatti",
                department = "English",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil English / Associate Professor",
                institutionalEmail = "ikram.bhatti@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "BA-HOD-01",
                username = "tariq.ashraf",
                fullName = "Prof. Tariq Ashraf",
                department = "Business Administration",
                designation = "Head of Department (HOD)",
                qualification = "MS / MBA / Associate Professor",
                institutionalEmail = "tariq.ashraf@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ECO-HOD-01",
                username = "ansar.iqbal",
                fullName = "Prof. Ansar Iqbal",
                department = "Economics",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Economics / Assistant Professor",
                institutionalEmail = "ansar.iqbal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "EDU-HOD-01",
                username = "adnan.saghir",
                fullName = "Prof. Muhammad Adnan Saghir",
                department = "Education",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Education / Assistant Professor",
                institutionalEmail = "adnan.saghir@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ISL-HOD-01",
                username = "saif.ullah",
                fullName = "Prof. Saif Ullah",
                department = "Islamic Studies",
                designation = "Head of Department (HOD)",
                qualification = "Ph.D Islamic Studies / Associate Professor",
                institutionalEmail = "saif.ullah@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "HIS-HOD-01",
                username = "saifullah.warraich",
                fullName = "Prof. Saif Ullah Warraich",
                department = "History",
                designation = "Head of Department (HOD)",
                qualification = "M.A History / Assistant Professor",
                institutionalEmail = "saifullah.warraich@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "URD-HOD-01",
                username = "muhammad.iqbal",
                fullName = "Prof. Muhammad Iqbal",
                department = "Urdu",
                designation = "Head of Department (HOD)",
                qualification = "Ph.D Urdu / Associate Professor",
                institutionalEmail = "muhammad.iqbal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ZOO-HOD-01",
                username = "waqas.arshad",
                fullName = "Prof. Waqas Arshad",
                department = "Zoology",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Zoology / Assistant Professor",
                institutionalEmail = "waqas.arshad@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "STA-HOD-01",
                username = "khuram.aslam",
                fullName = "Prof. Khuram Ijaz Aslam",
                department = "Statistics",
                designation = "Head of Department (HOD)",
                qualification = "M.Phil Statistics / Assistant Professor",
                institutionalEmail = "khuram.aslam@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "POL-HOD-01",
                username = "afrasiab",
                fullName = "Prof. Afrasiab",
                department = "Political Science",
                designation = "Head of Department (HOD)",
                qualification = "M.A Political Science / Assistant Professor",
                institutionalEmail = "afrasiab@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "PER-HOD-01",
                username = "mujahid.ali",
                fullName = "Prof. Mujahid Ali",
                department = "Persian",
                designation = "Head of Department (HOD)",
                qualification = "M.A Persian / Assistant Professor",
                institutionalEmail = "mujahid.ali@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            // Department Faculty Teachers
            RegisteredAccount(
                facultyId = "IT-FAC-01",
                username = "bilal.ahmed",
                fullName = "Prof. Bilal Ahmed",
                department = "Information Technology",
                designation = "Lecturer",
                qualification = "MS Computer Science",
                institutionalEmail = "bilal.ahmed@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "CHM-FAC-01",
                username = "shahid.nadeem",
                fullName = "Prof. Shahid Nadeem",
                department = "Chemistry",
                designation = "Lecturer",
                qualification = "M.Phil Chemistry",
                institutionalEmail = "shahid.nadeem@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "PHY-FAC-01",
                username = "usman.ghani",
                fullName = "Prof. Usman Ghani",
                department = "Physics",
                designation = "Assistant Professor",
                qualification = "M.Phil Physics",
                institutionalEmail = "usman.ghani@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "MTH-FAC-01",
                username = "imran.haider",
                fullName = "Prof. Imran Haider",
                department = "Mathematics",
                designation = "Lecturer",
                qualification = "M.Phil Mathematics",
                institutionalEmail = "imran.haider@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ENG-FAC-01",
                username = "nasir.mehmood",
                fullName = "Prof. Nasir Mehmood",
                department = "English",
                designation = "Assistant Professor",
                qualification = "M.Phil English Linguistics",
                institutionalEmail = "nasir.mehmood@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "BA-FAC-01",
                username = "kamran.afzal",
                fullName = "Prof. Kamran Afzal",
                department = "Business Administration",
                designation = "Lecturer",
                qualification = "MBA / MS Management Sciences",
                institutionalEmail = "kamran.afzal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            )
        )

        officialRoster.forEach { account ->
            memoryAccounts[account.facultyId.uppercase()] = account
            memoryAccounts[account.username.lowercase()] = account
            account.institutionalEmail?.let {
                memoryAccounts[it.lowercase()] = account
            }
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
     * Updates password for a faculty member or HOD account.
     */
    fun updatePassword(identifier: String, newPassword: String) {
        val cleanPass = newPassword.trim()
        val account = findAccount(identifier)
        if (account != null) {
            val updated = account.copy(password = cleanPass)
            memoryAccounts[account.facultyId.uppercase()] = updated
            memoryAccounts[account.username.lowercase()] = updated
            account.institutionalEmail?.let { memoryAccounts[it.lowercase()] = updated }
            persistToPrefs()
            Log.d(TAG, "Updated faculty password for ${account.fullName} (${account.facultyId})")
        }
    }

    /**
     * Validates credentials for Teacher / Faculty login.
     */
    fun authenticate(query: String, passwordAttempt: String): FacultyProfileDto? {
        val cleanQuery = query.trim()
        val cleanAttempt = passwordAttempt.trim()
        val account = findAccount(cleanQuery)
        if (account != null) {
            val isVerified = if (PasswordRegistryStore.hasCustomPassword(cleanQuery) || PasswordRegistryStore.hasCustomPassword(account.facultyId) || PasswordRegistryStore.hasCustomPassword(account.username)) {
                PasswordRegistryStore.verifyPassword(account.facultyId, cleanAttempt) || (account.password.isNotBlank() && account.password != "00000" && account.password == cleanAttempt)
            } else if (account.password.isNotBlank() && account.password != "00000") {
                account.password == cleanAttempt
            } else {
                cleanAttempt == "00000" || cleanAttempt == account.password
            }

            if (isVerified) {
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
     * Updates HOD status and department for an existing account.
     */
    fun setHodStatus(query: String, department: String, isHod: Boolean) {
        val cleanDept = department.trim()
        val account = findAccount(query)
        if (account != null) {
            // If making HOD, demote previous HOD in this department
            if (isHod) {
                memoryAccounts.values.filter { it.department.equals(cleanDept, ignoreCase = true) && it.isHod }.forEach { oldHod ->
                    val demoted = oldHod.copy(
                        designation = "Lecturer",
                        isHod = false
                    )
                    memoryAccounts[demoted.facultyId.uppercase()] = demoted
                    memoryAccounts[demoted.username.lowercase()] = demoted
                }
            }

            val updated = account.copy(
                department = cleanDept,
                designation = if (isHod) "Head of Department (HOD)" else "Lecturer",
                qualification = if (isHod) "Ph.D / Head of Department" else account.qualification,
                isHod = isHod
            )
            memoryAccounts[updated.facultyId.uppercase()] = updated
            memoryAccounts[updated.username.lowercase()] = updated
            updated.institutionalEmail?.let { memoryAccounts[it.lowercase()] = updated }
            persistToPrefs()
            Log.d(TAG, "Updated HOD status for ${updated.fullName}: isHod=$isHod in $cleanDept")
        }
    }

    /**
     * Returns all registered accounts for easy lookup/testing.
     */
    fun getAllAccounts(): List<RegisteredAccount> {
        return memoryAccounts.values.distinctBy { it.facultyId.uppercase() }
    }
}
