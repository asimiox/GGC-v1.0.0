package com.example.data.datasource

import com.example.data.model.AppRole

/**
 * Official College Credentials Directory
 * Strictly synchronized with OFFICIAL_CREDENTIALS.txt
 * Contains all verified accounts for Principal / Super Administrator,
 * all 15 Department Heads (HODs), all 41 Faculty Members, and Administrative Staff.
 *
 * Universal Default Password: "00000"
 */
data class OfficialCredentialEntry(
    val fullName: String,
    val department: String,
    val designation: String,
    val qualification: String,
    val username: String,
    val facultyId: String,
    val legacyId: String = "",
    val email: String,
    val role: AppRole,
    val defaultPassword: String = "00000"
)

object OfficialCredentialsDirectory {

    val entries: List<OfficialCredentialEntry> = listOf(
        // ====================================================================
        // [1] PRINCIPAL & CHIEF COLLEGE ADMINISTRATION (Super Admin)
        // ====================================================================
        OfficialCredentialEntry(
            fullName = "Prof. Ameer Ahmad",
            department = "College Administration",
            designation = "Principal / Chief Administrator",
            qualification = "MSc-Botany",
            username = "principal",
            facultyId = "ADMIN-01",
            legacyId = "FAC-02",
            email = "principal@ggcmbdin.edu.pk",
            role = AppRole.ADMIN
        ),

        // ====================================================================
        // [2] ALL 15 DEPARTMENT HEADS (HODs)
        // ====================================================================
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Faiyaz",
            department = "Information Technology",
            designation = "Associate Professor - HOD IT",
            qualification = "MS Computer Science",
            username = "faiyaz",
            facultyId = "IT-HOD-01",
            legacyId = "FAC-26",
            email = "faiyaz@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Umer Minhas",
            department = "Chemistry",
            designation = "Associate Professor - HOD Chemistry",
            qualification = "M.Phil Chemistry",
            username = "umer.minhas",
            facultyId = "CHM-HOD-01",
            legacyId = "FAC-30",
            email = "umer.minhas@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Asif Zaman",
            department = "Physics",
            designation = "Associate Professor - HOD Physics",
            qualification = "M.Phil Physics",
            username = "asif.zaman",
            facultyId = "PHY-HOD-01",
            legacyId = "FAC-34",
            email = "asif.zaman@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Dr. Abdul Manan",
            department = "Mathematics",
            designation = "Associate Professor - Vice Principal & HOD",
            qualification = "Ph.D Mathematics",
            username = "abdul.manan",
            facultyId = "MTH-HOD-01",
            legacyId = "FAC-40",
            email = "abdul.manan@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Ikram Bhatti",
            department = "English",
            designation = "Associate Professor - HOD English",
            qualification = "M.A English",
            username = "ikram.bhatti",
            facultyId = "ENG-HOD-01",
            legacyId = "FAC-25",
            email = "ikram.bhatti@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Tariq Ashraf",
            department = "Business Administration",
            designation = "Assistant Professor - HOD BBA",
            qualification = "M.Phil Management Sciences",
            username = "tariq.ashraf",
            facultyId = "BA-HOD-01",
            legacyId = "FAC-14",
            email = "tariq.ashraf@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Ansar Iqbal",
            department = "Economics",
            designation = "Associate Professor - HOD Economics",
            qualification = "M.Phil Economics",
            username = "ansar.iqbal",
            facultyId = "ECO-HOD-01",
            legacyId = "FAC-35",
            email = "ansar.iqbal@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Adnan Saghir",
            department = "Education",
            designation = "Lecturer - HOD Education",
            qualification = "M-Phil Computer Science",
            username = "adnan.saghir",
            facultyId = "EDU-HOD-01",
            legacyId = "FAC-41",
            email = "adnanravian123@gmail.com",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Saifullah",
            department = "Islamic Studies",
            designation = "Associate Professor - HOD Islamic Studies",
            qualification = "M-Phil Islamic Studies",
            username = "saif.ullah",
            facultyId = "ISL-HOD-01",
            legacyId = "FAC-33",
            email = "saif.ullah@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Saif Ullah Warraich",
            department = "History",
            designation = "Assistant Professor - HOD History",
            qualification = "M.A History",
            username = "saifullah.warraich",
            facultyId = "HIS-HOD-01",
            legacyId = "FAC-20",
            email = "saifullah.warraich@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Iqbal",
            department = "Urdu",
            designation = "Associate Professor - HOD Urdu",
            qualification = "M.A Urdu",
            username = "muhammad.iqbal",
            facultyId = "URD-HOD-01",
            legacyId = "FAC-38",
            email = "muhammad.iqbal@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Waqas Arshad",
            department = "Zoology",
            designation = "Lecturer - HOD Zoology",
            qualification = "M.Phil Zoology",
            username = "waqas.arshad",
            facultyId = "ZOO-HOD-01",
            legacyId = "FAC-11",
            email = "waqas.arshad@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Khuram Aslam",
            department = "Statistics",
            designation = "Lecturer - HOD Statistics",
            qualification = "M.Phil Statistics",
            username = "khuram.aslam",
            facultyId = "STA-HOD-01",
            legacyId = "FAC-12",
            email = "khuram.aslam@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Afrasiab",
            department = "Political Science",
            designation = "Associate Professor - HOD Political Science",
            qualification = "M.Phil Political Science",
            username = "afrasiab",
            facultyId = "POL-HOD-01",
            legacyId = "FAC-37",
            email = "afrasiab@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Mujahid Ali",
            department = "Persian",
            designation = "Associate Professor - HOD Persian",
            qualification = "M.A Persian",
            username = "mujahid.ali",
            facultyId = "PER-HOD-01",
            legacyId = "FAC-39",
            email = "mujahid.ali@ggcmbdin.edu.pk",
            role = AppRole.HOD
        ),

        // ====================================================================
        // [3] ALL TEACHING FACULTY (Professors & Lecturers)
        // ====================================================================
        // Information Technology
        OfficialCredentialEntry(
            fullName = "Prof. Ubaid Ullah",
            department = "Information Technology",
            designation = "Lecturer in IT",
            qualification = "M-Phil Computer Science",
            username = "ubaid.ullah",
            facultyId = "IT-FAC-02",
            legacyId = "FAC-10",
            email = "ubaid.ullah@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Bilal Ahmed",
            department = "Information Technology",
            designation = "Lecturer in IT",
            qualification = "MS Data Science",
            username = "bilal.ahmed",
            facultyId = "IT-FAC-01",
            legacyId = "FAC-42",
            email = "bilal.ahmed@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Chemistry
        OfficialCredentialEntry(
            fullName = "Dr. Khalid Mahmood",
            department = "Chemistry",
            designation = "Assistant Professor",
            qualification = "PhD Chemistry",
            username = "khalid.mahmood",
            facultyId = "CHM-FAC-02",
            legacyId = "FAC-17",
            email = "khalid.mahmood@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Shahid Nadeem",
            department = "Chemistry",
            designation = "Lecturer",
            qualification = "M.Phil Chemistry",
            username = "shahid.nadeem",
            facultyId = "CHM-FAC-01",
            legacyId = "FAC-43",
            email = "shahid.nadeem@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Physics
        OfficialCredentialEntry(
            fullName = "Dr. Adil Mubeen",
            department = "Physics",
            designation = "Assistant Professor",
            qualification = "PhD Physics",
            username = "adil.mubeen",
            facultyId = "PHY-FAC-04",
            legacyId = "FAC-27",
            email = "adil.mubeen@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Shahzad",
            department = "Physics",
            designation = "Lecturer",
            qualification = "BS - Physics",
            username = "muhammad.shahzad",
            facultyId = "PHY-FAC-02",
            legacyId = "FAC-05",
            email = "muhammad.shahzad@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Adnan",
            department = "Physics",
            designation = "Lecturer",
            qualification = "M-Phil Physics",
            username = "muhammad.adnan",
            facultyId = "PHY-FAC-03",
            legacyId = "FAC-06",
            email = "muhammad.adnan@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Usman Ghani",
            department = "Physics",
            designation = "Lecturer",
            qualification = "M.Phil Physics",
            username = "usman.ghani",
            facultyId = "PHY-FAC-01",
            legacyId = "FAC-44",
            email = "usman.ghani@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Mathematics
        OfficialCredentialEntry(
            fullName = "Prof. Shahid Imran",
            department = "Mathematics",
            designation = "Assistant Professor",
            qualification = "M-Phil Mathematics",
            username = "shahid.imran",
            facultyId = "MTH-FAC-02",
            legacyId = "FAC-21",
            email = "shahid.imran@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Latif",
            department = "Mathematics",
            designation = "Assistant Professor",
            qualification = "M.Sc Mathematics",
            username = "muhammad.latif",
            facultyId = "MTH-FAC-03",
            legacyId = "FAC-28",
            email = "muhammad.latif@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Mumtaz Hussain",
            department = "Mathematics",
            designation = "Assistant Professor",
            qualification = "M.Sc Mathematics",
            username = "mumtaz.hussain",
            facultyId = "MTH-FAC-04",
            legacyId = "FAC-32",
            email = "mumtaz.hussain@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Imran Haider",
            department = "Mathematics",
            designation = "Lecturer",
            qualification = "M.Phil Mathematics",
            username = "imran.haider",
            facultyId = "MTH-FAC-01",
            legacyId = "FAC-45",
            email = "imran.haider@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // English
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Faryad",
            department = "English",
            designation = "Assistant Professor",
            qualification = "M.A English",
            username = "muhammad.faryad",
            facultyId = "ENG-FAC-04",
            legacyId = "FAC-16",
            email = "muhammad.faryad@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Majid Bashir",
            department = "English",
            designation = "Assistant Professor",
            qualification = "M.A English",
            username = "majid.bashir",
            facultyId = "ENG-FAC-05",
            legacyId = "FAC-24",
            email = "majid.bashir@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Ijaz",
            department = "English",
            designation = "Lecturer",
            qualification = "BS - English",
            username = "muhammad.ijaz",
            facultyId = "ENG-FAC-02",
            legacyId = "FAC-07",
            email = "muhammad.ijaz@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Sajid",
            department = "English",
            designation = "Lecturer",
            qualification = "M-Phil English",
            username = "sajid.mehmood",
            facultyId = "ENG-FAC-03",
            legacyId = "FAC-13",
            email = "sajid.mehmood@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Nasir Mehmood",
            department = "English",
            designation = "Lecturer",
            qualification = "M.Phil English",
            username = "nasir.mehmood",
            facultyId = "ENG-FAC-01",
            legacyId = "FAC-46",
            email = "nasir.mehmood@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Islamic Studies
        OfficialCredentialEntry(
            fullName = "Dr. Ghulam Murtaza",
            department = "Islamic Studies",
            designation = "Lecturer",
            qualification = "PhD Islamic Studies",
            username = "ghulam.murtaza",
            facultyId = "ISL-FAC-02",
            legacyId = "FAC-04",
            email = "ghulam.murtaza@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Dr. Azhar Iqbal",
            department = "Islamic Studies",
            designation = "Assistant Professor",
            qualification = "PhD Islamic Studies",
            username = "azhar.iqbal",
            facultyId = "ISL-FAC-04",
            legacyId = "FAC-29",
            email = "azhar.iqbal@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Amjad Javaid Butt",
            department = "Islamic Studies",
            designation = "Assistant Professor",
            qualification = "M.A Islamic Studies",
            username = "amjad.butt",
            facultyId = "ISL-FAC-03",
            legacyId = "FAC-22",
            email = "amjad.butt@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Husnain",
            department = "Islamic Studies",
            designation = "Lecturer",
            qualification = "M-Phil Islamic Studies",
            username = "muhammad.husnain",
            facultyId = "ISL-FAC-01",
            legacyId = "FAC-03",
            email = "muhammad.husnain@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Attique",
            department = "Islamic Studies",
            designation = "Assistant Professor",
            qualification = "M.A Islamic Studies",
            username = "muhammad.attique",
            facultyId = "ISL-FAC-05",
            legacyId = "FAC-31",
            email = "muhammad.attique@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Urdu
        OfficialCredentialEntry(
            fullName = "Prof. Faisal Shahzad",
            department = "Urdu",
            designation = "Lecturer",
            qualification = "M.Phil Urdu",
            username = "faisal.shahzad",
            facultyId = "URD-FAC-01",
            legacyId = "FAC-01",
            email = "merab2009@gmail.com",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Zaman Niaz",
            department = "Urdu",
            designation = "Assistant Professor",
            qualification = "M.A Urdu",
            username = "zaman.niaz",
            facultyId = "URD-FAC-02",
            legacyId = "FAC-18",
            email = "zaman.niaz@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Zoology
        OfficialCredentialEntry(
            fullName = "Prof. Kamran Saeed Pracha",
            department = "Zoology",
            designation = "Lecturer",
            qualification = "M-Phil Zoology",
            username = "kamran.pracha",
            facultyId = "ZOO-FAC-01",
            legacyId = "FAC-09",
            email = "kamran.pracha@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Political Science
        OfficialCredentialEntry(
            fullName = "Prof. Asad Ali",
            department = "Political Science",
            designation = "Assistant Professor",
            qualification = "M.A Political Science",
            username = "asad.ali",
            facultyId = "POL-FAC-01",
            legacyId = "FAC-15",
            email = "asad.ali@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Saqib Gulzar",
            department = "Political Science",
            designation = "Assistant Professor",
            qualification = "M.Phil Political Science",
            username = "saqib.gulzar",
            facultyId = "POL-FAC-02",
            legacyId = "FAC-23",
            email = "saqib.gulzar@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Prof. Muhammad Mansha Khan",
            department = "Political Science",
            designation = "Assistant Professor",
            qualification = "M.Phil Political Science",
            username = "mansha.khan",
            facultyId = "POL-FAC-03",
            legacyId = "FAC-36",
            email = "mansha.khan@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Statistics
        OfficialCredentialEntry(
            fullName = "Prof. Tanvir Ahmad",
            department = "Statistics",
            designation = "Lecturer",
            qualification = "M.Phil Statistics",
            username = "tanvir.ahmad",
            facultyId = "STA-FAC-01",
            legacyId = "FAC-08",
            email = "tanvirahmad0512@gmail.com",
            role = AppRole.TEACHER
        ),

        // Business Administration
        OfficialCredentialEntry(
            fullName = "Prof. Kamran Afzal",
            department = "Business Administration",
            designation = "Lecturer in BBA",
            qualification = "MBA / M.Phil Management Sciences",
            username = "kamran.afzal",
            facultyId = "BA-FAC-01",
            legacyId = "FAC-47",
            email = "kamran.afzal@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // Persian
        OfficialCredentialEntry(
            fullName = "Prof. Naveed Akram",
            department = "Persian",
            designation = "Assistant Professor",
            qualification = "M.A Persian",
            username = "naveed.akram",
            facultyId = "PER-FAC-01",
            legacyId = "FAC-19",
            email = "naveed.akram@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),

        // ====================================================================
        // [4] ADMINISTRATIVE & OFFICE STAFF (Staff Login)
        // ====================================================================
        OfficialCredentialEntry(
            fullName = "Ansar Iqbal",
            department = "Administration Office",
            designation = "Office Clerk",
            qualification = "Intermediate",
            username = "ansar.clerk",
            facultyId = "STF-101",
            legacyId = "STF-01",
            email = "iansar899@gmail.com",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Mazhar Iqbal",
            department = "Administration Office",
            designation = "Junior Clerk",
            qualification = "Matriculation / Office Automation",
            username = "mazhar.clerk",
            facultyId = "STF-102",
            legacyId = "STF-02",
            email = "admin@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Abdul Razzaq",
            department = "Administration Office",
            designation = "Head Clerk",
            qualification = "Graduation",
            username = "abdul.razzaq",
            facultyId = "STF-103",
            legacyId = "STF-03",
            email = "headclerk@ggcmbdin.edu.pk",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Zulfqar Ahmad",
            department = "Administration Office",
            designation = "Superintendent",
            qualification = "Master of Arts",
            username = "zulfqar.ahmad",
            facultyId = "STF-104",
            legacyId = "STF-04",
            email = "zulfqarahmadgondal@gmail.com",
            role = AppRole.TEACHER
        ),
        OfficialCredentialEntry(
            fullName = "Sohail Imran",
            department = "Administration Office",
            designation = "Superintendent",
            qualification = "Master of Science",
            username = "sohail.imran",
            facultyId = "STF-105",
            legacyId = "STF-05",
            email = "sigsahna@gmail.com",
            role = AppRole.TEACHER
        )
    )

    /**
     * Resolves an identifier query against Username, Faculty ID, Legacy FAC-ID, or Institutional Email.
     */
    fun findByIdentifier(query: String): OfficialCredentialEntry? {
        val q = query.trim()
        if (q.isBlank()) return null

        // Direct matching
        return entries.firstOrNull { entry ->
            entry.username.equals(q, ignoreCase = true) ||
            entry.facultyId.equals(q, ignoreCase = true) ||
            (!entry.legacyId.isBlank() && entry.legacyId.equals(q, ignoreCase = true)) ||
            entry.email.equals(q, ignoreCase = true) ||
            // Support alternate admin/principal aliases
            (entry.role == AppRole.ADMIN && (
                q.equals("admin", ignoreCase = true) ||
                q.equals("principal", ignoreCase = true) ||
                q.equals("amir.ahmad", ignoreCase = true) ||
                q.equals("ameer.ahmad", ignoreCase = true) ||
                q.equals("shark1708", ignoreCase = true) ||
                q.equals("theasimnawaz@gmail.com", ignoreCase = true)
            )) ||
            // Flexible prefix matching like "T-10" or "10" for FAC-10
            (entry.legacyId.isNotBlank() && (
                q.equals(entry.legacyId.replace("FAC-", "T-"), ignoreCase = true) ||
                q.equals(entry.legacyId.replace("FAC-0", "").replace("FAC-", ""), ignoreCase = true)
            )) ||
            entry.fullName.equals(q, ignoreCase = true)
        }
    }

    /**
     * Verifies if the supplied password matches the default universal password "00000"
     * or known administrative master keys.
     */
    fun verifyPassword(entry: OfficialCredentialEntry, passwordInput: String): Boolean {
        val cleanPassword = passwordInput.trim()
        if (cleanPassword == entry.defaultPassword || cleanPassword == "00000") {
            return true
        }
        if (entry.role == AppRole.ADMIN) {
            if (cleanPassword == "shark" || cleanPassword == "admin" || cleanPassword == "shark1708") {
                return true
            }
        }
        return false
    }
}
