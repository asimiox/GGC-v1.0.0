package com.example.data.model

/**
 * Official Academic Departments of Government Graduate College Mandi Bahauddin (GGCMBDIN)
 * Source: Official Portal (https://www.ggcmbdin.edu.pk)
 *
 * Rule: Admin dropdown displays ONLY these departments, with strictly one HOD per department.
 */
object GgcOfficialDepartments {

    val LIST: List<String> = listOf(
        "Information Technology",
        "Botany",
        "Business Administration",
        "Chemistry",
        "Economics",
        "Education",
        "English",
        "History",
        "Islamic Studies",
        "Mathematics",
        "Persian",
        "Physics",
        "Political Science",
        "Statistics",
        "Urdu",
        "Zoology"
    )

    /**
     * Map of Department Name to standard short code used for generating unique HOD IDs.
     */
    fun getDepartmentCode(department: String): String {
        return when {
            department.contains("Information Technology", ignoreCase = true) || department.contains("Computer", ignoreCase = true) -> "IT"
            department.contains("Botany", ignoreCase = true) -> "BOT"
            department.contains("Business", ignoreCase = true) || department.contains("BBA", ignoreCase = true) || department.contains("Commerce", ignoreCase = true) -> "BBA"
            department.contains("Chemistry", ignoreCase = true) -> "CHEM"
            department.contains("Economics", ignoreCase = true) -> "ECON"
            department.contains("Education", ignoreCase = true) -> "EDU"
            department.contains("English", ignoreCase = true) -> "ENG"
            department.contains("History", ignoreCase = true) -> "HIST"
            department.contains("Islamic", ignoreCase = true) || department.contains("Islamiyat", ignoreCase = true) -> "ISL"
            department.contains("Math", ignoreCase = true) -> "MATH"
            department.contains("Persian", ignoreCase = true) -> "PER"
            department.contains("Physics", ignoreCase = true) -> "PHY"
            department.contains("Political", ignoreCase = true) -> "POL"
            department.contains("Statistics", ignoreCase = true) -> "STAT"
            department.contains("Urdu", ignoreCase = true) -> "URDU"
            department.contains("Zoology", ignoreCase = true) -> "ZOO"
            else -> department.take(4).uppercase().replace(" ", "")
        }
    }

    /**
     * Generates standard default HOD ID based on Department Code (e.g. IT-HOD-01, MATH-HOD-01).
     */
    fun generateDefaultHodId(department: String): String {
        val code = getDepartmentCode(department)
        return "$code-HOD-01"
    }

    /**
     * Generates standard default Teacher ID based on Department Code (e.g. IT-T-01, PHY-T-01).
     */
    fun generateDefaultTeacherId(department: String, suffix: Int = 1): String {
        val code = getDepartmentCode(department)
        val numStr = if (suffix < 10) "0$suffix" else "$suffix"
        return "$code-T-$numStr"
    }

    /**
     * Maps department to default academic BS program name.
     */
    fun getDefaultProgramForDepartment(department: String): String {
        return when {
            department.contains("Information Technology", ignoreCase = true) -> "BS Information Technology"
            department.contains("Business", ignoreCase = true) || department.contains("BBA", ignoreCase = true) -> "BS Business Administration"
            department.contains("Islamic", ignoreCase = true) -> "BS Islamic Studies"
            else -> "BS $department"
        }
    }
}
