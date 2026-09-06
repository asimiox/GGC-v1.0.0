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
            // ==================== 1. PRINCIPAL & CHIEF ADMINISTRATION ====================
            RegisteredAccount(
                facultyId = "ADMIN-01",
                username = "principal",
                fullName = "Prof. Ameer Ahmad",
                department = "College Administration",
                designation = "Principal & Chief Administrator",
                qualification = "M.Sc Botany / Principal",
                institutionalEmail = "principal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // ==================== 2. OFFICIAL HEADS OF DEPARTMENT (HODs) ====================
            RegisteredAccount(
                facultyId = "IT-HOD-01",
                username = "faiyaz",
                fullName = "Prof. Muhammad Faiyaz",
                department = "Information Technology",
                designation = "Assistant Professor - HOD Information Technology",
                qualification = "M-Phil Computer Science",
                institutionalEmail = "faiyaz@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "CHM-HOD-01",
                username = "umer.minhas",
                fullName = "Prof. Muhammad Umer Minhas",
                department = "Chemistry",
                designation = "Assistant Professor - HOD Chemistry",
                qualification = "M.Sc Chemistry",
                institutionalEmail = "umer.minhas@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "PHY-HOD-01",
                username = "asif.zaman",
                fullName = "Prof. Muhammad Asif Zaman",
                department = "Physics",
                designation = "Assistant Professor - HOD Physics",
                qualification = "M.Sc Physics",
                institutionalEmail = "asif.zaman@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "MTH-HOD-01",
                username = "abdul.manan",
                fullName = "Dr. Abdul Manan",
                department = "Mathematics",
                designation = "Vice Principal - Associate Professor - HOD Mathematics",
                qualification = "PhD in Mathematics",
                institutionalEmail = "abdul.manan@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ENG-HOD-01",
                username = "ikram.bhatti",
                fullName = "Prof. Muhammad Ikram Bhatti",
                department = "English",
                designation = "Assistant Professor - HOD English",
                qualification = "M.A English",
                institutionalEmail = "ikram.bhatti@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "BA-HOD-01",
                username = "tariq.ashraf",
                fullName = "Prof. Tariq Ashraf",
                department = "BBA",
                designation = "Lecturer - HOD Business Administration",
                qualification = "M-Phil Business Administration",
                institutionalEmail = "tariq.ashraf@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ECO-HOD-01",
                username = "ansar.iqbal",
                fullName = "Prof. Ansar Iqbal",
                department = "Economics",
                designation = "Assistant Professor - HOD Economics",
                qualification = "M.A Economics",
                institutionalEmail = "ansar.iqbal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "EDU-HOD-01",
                username = "adnan.saghir",
                fullName = "Prof. Muhammad Adnan Saghir",
                department = "Education",
                designation = "Lecturer - HOD Education",
                qualification = "M-Phil Computer Science",
                institutionalEmail = "adnanravian123@gmail.com",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ISL-HOD-01",
                username = "saif.ullah",
                fullName = "Prof. Saifullah",
                department = "Islamiyat",
                designation = "Assistant Professor - HOD Islamiyat",
                qualification = "M-Phil Islamiyat",
                institutionalEmail = "saif.ullah@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "HIS-HOD-01",
                username = "saifullah.warraich",
                fullName = "Prof. Saif Ullah Warraich",
                department = "History",
                designation = "Assistant Professor - HOD History",
                qualification = "M.A History",
                institutionalEmail = "saifullah.warraich@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "URD-HOD-01",
                username = "muhammad.iqbal",
                fullName = "Prof. Muhammad Iqbal",
                department = "Urdu",
                designation = "Associate Professor - HOD Urdu",
                qualification = "M-Phil Urdu",
                institutionalEmail = "muhammad.iqbal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "ZOO-HOD-01",
                username = "waqas.arshad",
                fullName = "Prof. Waqas Arshad",
                department = "Zoology",
                designation = "Lecturer - HOD Zoology",
                qualification = "M-Phil Zoology",
                institutionalEmail = "waqas.arshad@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "STA-HOD-01",
                username = "khuram.aslam",
                fullName = "Prof. Khuram Ijaz Aslam",
                department = "Statistics",
                designation = "Lecturer - HOD Statistics",
                qualification = "M-Phil Statistics",
                institutionalEmail = "khuram.aslam@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "POL-HOD-01",
                username = "afrasiab",
                fullName = "Prof. Afrasiab",
                department = "Political Science",
                designation = "Assistant Professor - HOD Political Science",
                qualification = "M-Phil Political Science",
                institutionalEmail = "afrasiab@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),
            RegisteredAccount(
                facultyId = "PER-HOD-01",
                username = "mujahid.ali",
                fullName = "Prof. Mujahid Ali",
                department = "Persian",
                designation = "Associate Professor - HOD Persian",
                qualification = "M-Phil Persian",
                institutionalEmail = "mujahid.ali@ggcmbdin.edu.pk",
                password = "00000",
                isHod = true
            ),

            // ==================== 3. ALL OFFICIAL PROFESSORS CRAWLED FROM GGCMBDIN.EDU.PK ====================
            // Department of Urdu
            RegisteredAccount(
                facultyId = "URD-FAC-01",
                username = "faisal.shahzad",
                fullName = "Prof. Faisal Shahzad",
                department = "Urdu",
                designation = "Lecturer",
                qualification = "M.Phil Urdu",
                institutionalEmail = "merab2009@gmail.com",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "URD-FAC-02",
                username = "zaman.niaz",
                fullName = "Prof. Zaman Niaz",
                department = "Urdu",
                designation = "Assistant Professor",
                qualification = "M-Phil Urdu",
                institutionalEmail = "zaman.niaz@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // Department of Islamiyat
            RegisteredAccount(
                facultyId = "ISL-FAC-01",
                username = "muhammad.husnain",
                fullName = "Prof. Muhammad Husnain",
                department = "Islamiyat",
                designation = "Lecturer",
                qualification = "M-Phil Islamic Studies",
                institutionalEmail = "muhammad.husnain@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ISL-FAC-02",
                username = "ghulam.murtaza",
                fullName = "Dr. Ghulam Murtaza",
                department = "Islamiyat",
                designation = "Lecturer",
                qualification = "PhD Islamic Studies",
                institutionalEmail = "ghulam.murtaza@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ISL-FAC-03",
                username = "amjad.butt",
                fullName = "Prof. Amjad Javaid Butt",
                department = "Islamiyat",
                designation = "Assistant Professor",
                qualification = "M-Phil Islamic Studies",
                institutionalEmail = "amjad.butt@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ISL-FAC-04",
                username = "azhar.iqbal",
                fullName = "Dr. Azhar Iqbal",
                department = "Islamiyat",
                designation = "Assistant Professor",
                qualification = "PhD Islamic Studies",
                institutionalEmail = "azhar.iqbal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ISL-FAC-05",
                username = "muhammad.attique",
                fullName = "Prof. Muhammad Attique",
                department = "Islamiyat",
                designation = "Assistant Professor",
                qualification = "M-Phil Islamic Studies",
                institutionalEmail = "muhammad.attique@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // Department of Physics
            RegisteredAccount(
                facultyId = "PHY-FAC-02",
                username = "muhammad.shahzad",
                fullName = "Prof. Muhammad Shahzad",
                department = "Physics",
                designation = "Lecturer",
                qualification = "BS - Physics",
                institutionalEmail = "muhammad.shahzad@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "PHY-FAC-03",
                username = "muhammad.adnan",
                fullName = "Prof. Muhammad Adnan",
                department = "Physics",
                designation = "Lecturer",
                qualification = "M-Phil Physics",
                institutionalEmail = "muhammad.adnan@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "PHY-FAC-04",
                username = "adil.mubeen",
                fullName = "Dr. Adil Mubeen",
                department = "Physics",
                designation = "Assistant Professor",
                qualification = "PhD Physics",
                institutionalEmail = "adil.mubeen@ggcmbdin.edu.pk",
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

            // Department of English
            RegisteredAccount(
                facultyId = "ENG-FAC-02",
                username = "muhammad.ijaz",
                fullName = "Prof. Muhammad Ijaz",
                department = "English",
                designation = "Lecturer",
                qualification = "BS - English",
                institutionalEmail = "muhammad.ijaz@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ENG-FAC-03",
                username = "sajid.mehmood",
                fullName = "Prof. Muhammad Sajid Mehmood",
                department = "English",
                designation = "Lecturer",
                qualification = "M-Phil English",
                institutionalEmail = "sajid.mehmood@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ENG-FAC-04",
                username = "muhammad.faryad",
                fullName = "Prof. Muhammad Faryad",
                department = "English",
                designation = "Assistant Professor",
                qualification = "M.A English",
                institutionalEmail = "muhammad.faryad@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "ENG-FAC-05",
                username = "majid.bashir",
                fullName = "Prof. Majid Bashir",
                department = "English",
                designation = "Assistant Professor",
                qualification = "M-Phil English",
                institutionalEmail = "majid.bashir@ggcmbdin.edu.pk",
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

            // Department of Statistics
            RegisteredAccount(
                facultyId = "STA-FAC-01",
                username = "tanvir.ahmad",
                fullName = "Prof. Tanvir Ahmad",
                department = "Statistics",
                designation = "Lecturer",
                qualification = "M.Phil Statistics",
                institutionalEmail = "tanvirahmad0512@gmail.com",
                password = "00000",
                isHod = false
            ),

            // Department of Zoology
            RegisteredAccount(
                facultyId = "ZOO-FAC-01",
                username = "kamran.pracha",
                fullName = "Prof. Kamran Saeed Pracha",
                department = "Zoology",
                designation = "Lecturer",
                qualification = "M-Phil Zoology",
                institutionalEmail = "kamran.pracha@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // Department of Information Technology
            RegisteredAccount(
                facultyId = "IT-FAC-02",
                username = "ubaid.ullah",
                fullName = "Prof. Ubaid Ullah",
                department = "Information Technology",
                designation = "Lecturer",
                qualification = "M.Sc Information Technology",
                institutionalEmail = "ubaid.ullah@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
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

            // Department of Political Science
            RegisteredAccount(
                facultyId = "POL-FAC-01",
                username = "asad.ali",
                fullName = "Prof. Asad Ali",
                department = "Political Science",
                designation = "Lecturer",
                qualification = "BS - Political Science",
                institutionalEmail = "asad.ali@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "POL-FAC-02",
                username = "saqib.gulzar",
                fullName = "Prof. Saqib Gulzar",
                department = "Political Science",
                designation = "Assistant Professor",
                qualification = "M-Phil Political Science",
                institutionalEmail = "saqib.gulzar@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "POL-FAC-03",
                username = "mansha.khan",
                fullName = "Prof. Muhammad Mansha Khan",
                department = "Political Science",
                designation = "Assistant Professor",
                qualification = "M.A Political Science",
                institutionalEmail = "mansha.khan@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // Department of Chemistry
            RegisteredAccount(
                facultyId = "CHM-FAC-02",
                username = "khalid.mahmood",
                fullName = "Dr. Khalid Mahmood",
                department = "Chemistry",
                designation = "Assistant Professor",
                qualification = "PhD Chemistry",
                institutionalEmail = "khalid.mahmood@ggcmbdin.edu.pk",
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

            // Department of Persian
            RegisteredAccount(
                facultyId = "PER-FAC-01",
                username = "naveed.akram",
                fullName = "Prof. Naveed Akram",
                department = "Persian",
                designation = "Assistant Professor",
                qualification = "M-Phil Persian",
                institutionalEmail = "naveed.akram@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // Department of Mathematics
            RegisteredAccount(
                facultyId = "MTH-FAC-02",
                username = "shahid.imran",
                fullName = "Prof. Shahid Imran",
                department = "Mathematics",
                designation = "Assistant Professor",
                qualification = "M.Sc Mathematics",
                institutionalEmail = "shahid.imran@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "MTH-FAC-03",
                username = "muhammad.latif",
                fullName = "Prof. Muhammad Latif",
                department = "Mathematics",
                designation = "Assistant Professor",
                qualification = "M-Phil Mathematics",
                institutionalEmail = "muhammad.latif@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "MTH-FAC-04",
                username = "mumtaz.hussain",
                fullName = "Prof. Mumtaz Hussain",
                department = "Mathematics",
                designation = "Assistant Professor",
                qualification = "M.Sc Mathematics",
                institutionalEmail = "mumtaz.hussain@ggcmbdin.edu.pk",
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

            // Department of Business Administration
            RegisteredAccount(
                facultyId = "BA-FAC-01",
                username = "kamran.afzal",
                fullName = "Prof. Kamran Afzal",
                department = "BBA",
                designation = "Lecturer",
                qualification = "MBA / MS Management Sciences",
                institutionalEmail = "kamran.afzal@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),

            // ==================== 4. ADMINISTRATIVE & OFFICE STAFF ====================
            RegisteredAccount(
                facultyId = "STF-101",
                username = "ansar.clerk",
                fullName = "Ansar Iqbal",
                department = "Administrative Staff",
                designation = "Office Clerk",
                qualification = "BS",
                institutionalEmail = "iansar899@gmail.com",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "STF-102",
                username = "mazhar.clerk",
                fullName = "Mazhar Iqbal",
                department = "Administrative Staff",
                designation = "Junior Clerk",
                qualification = "Intermediate",
                institutionalEmail = "admin@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "STF-103",
                username = "abdul.razzaq",
                fullName = "Abdul Razzaq",
                department = "Administrative Staff",
                designation = "Head Clerk",
                qualification = "Graduate",
                institutionalEmail = "headclerk@ggcmbdin.edu.pk",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "STF-104",
                username = "zulfqar.ahmad",
                fullName = "Zulfqar Ahmad",
                department = "Administrative Staff",
                designation = "Superintendent",
                qualification = "BA",
                institutionalEmail = "zulfqarahmadgondal@gmail.com",
                password = "00000",
                isHod = false
            ),
            RegisteredAccount(
                facultyId = "STF-105",
                username = "sohail.imran",
                fullName = "Sohail Imran",
                department = "Administrative Staff",
                designation = "Superintendent",
                qualification = "MA",
                institutionalEmail = "sigsahna@gmail.com",
                password = "00000",
                isHod = false
            )
        )

        // Seed all into memory accounts with multi-index for seamless login matching
        officialRoster.forEach { account ->
            indexAccount(account)
        }

        // Also cross-link all 41 members from OfficialFacultyData.facultyList by numeric FAC-ID & name
        OfficialFacultyData.facultyList.forEach { member ->
            val numIdUpper = "FAC-${member.id}"
            val numIdPadded = "FAC-%02d".format(member.id)
            val cleanName = member.name.trim()

            // Find if we already have an account for this member
            val existing = findAccount(cleanName)
            if (existing != null) {
                memoryAccounts[numIdUpper] = existing
                memoryAccounts[numIdPadded] = existing
            } else {
                // Synthesize registered account for any unseeded member
                val autoUsername = cleanName.lowercase()
                    .removePrefix("prof. ")
                    .removePrefix("dr. ")
                    .trim()
                    .replace(" ", ".")
                val syntheticAccount = RegisteredAccount(
                    facultyId = numIdPadded,
                    username = autoUsername,
                    fullName = cleanName,
                    department = member.department,
                    designation = member.designation,
                    qualification = member.qualification,
                    institutionalEmail = if (member.email.isNotBlank()) member.email else "$autoUsername@ggcmbdin.edu.pk",
                    password = "00000",
                    isHod = member.isHod
                )
                indexAccount(syntheticAccount)
                memoryAccounts[numIdUpper] = syntheticAccount
                memoryAccounts[numIdPadded] = syntheticAccount
            }
        }

        // Special administrative aliases for the Principal
        val principalAccount = findAccount("ADMIN-01")
        if (principalAccount != null) {
            memoryAccounts["ADMIN"] = principalAccount
            memoryAccounts["PRINCIPAL"] = principalAccount
            memoryAccounts["AMIR.AHMAD"] = principalAccount
            memoryAccounts["AMEER.AHMAD"] = principalAccount
        }
    }

    private fun indexAccount(account: RegisteredAccount) {
        val idUp = account.facultyId.uppercase()
        val userLow = account.username.lowercase()
        memoryAccounts[idUp] = account
        memoryAccounts[userLow] = account
        account.institutionalEmail?.let {
            memoryAccounts[it.lowercase()] = account
        }

        val nameNorm = account.fullName.lowercase().trim()
        memoryAccounts[nameNorm] = account
        val cleanName = nameNorm.removePrefix("prof. ").removePrefix("dr. ").trim()
        memoryAccounts[cleanName] = account
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
        isHod: Boolean = false
    ): RegisteredAccount {
        val cleanId = facultyId.trim().uppercase()
        val cleanUsername = cleanId.lowercase()
        val cleanEmail = "${cleanUsername}@ggcmbdin.edu.pk"

        val account = RegisteredAccount(
            facultyId = cleanId,
            username = cleanUsername,
            fullName = fullName.trim(),
            department = department.trim(),
            designation = designation.trim(),
            qualification = qualification.trim(),
            institutionalEmail = cleanEmail,
            password = "",
            isHod = isHod
        )

        memoryAccounts[cleanId] = account
        memoryAccounts[cleanUsername] = account
        memoryAccounts[cleanEmail.lowercase()] = account

        persistToPrefs()
        Log.d(TAG, "Saved registered account: ${account.fullName} (${account.facultyId})")
        return account
    }

    /**
     * Finds matching account by Teacher ID, username, institutional email, or full name.
     */
    fun findAccount(query: String): RegisteredAccount? {
        val cleanQuery = query.trim()
        val queryNorm = cleanQuery.lowercase()
        val queryStripped = queryNorm.removePrefix("prof. ").removePrefix("dr. ").trim()
        return memoryAccounts[cleanQuery.uppercase()]
            ?: memoryAccounts[queryNorm]
            ?: memoryAccounts[queryStripped]
            ?: memoryAccounts.values.firstOrNull {
                it.facultyId.equals(cleanQuery, ignoreCase = true) ||
                it.username.equals(cleanQuery, ignoreCase = true) ||
                it.institutionalEmail?.equals(cleanQuery, ignoreCase = true) == true ||
                it.fullName.equals(cleanQuery, ignoreCase = true) ||
                it.fullName.removePrefix("Prof. ").removePrefix("Dr. ").trim().equals(queryStripped, ignoreCase = true)
            }
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
