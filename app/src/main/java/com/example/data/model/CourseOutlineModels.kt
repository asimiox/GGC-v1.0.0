package com.example.data.model

data class CourseItem(
    val code: String,
    val title: String,
    val creditHours: Int,
    val description: String = ""
) {
    val isLab: Boolean
        get() = code.endsWith("-L", ignoreCase = true) ||
                code.endsWith("L", ignoreCase = false) && code.any { it.isDigit() } ||
                title.contains("Lab", ignoreCase = true)

    val isNonCredit: Boolean
        get() = creditHours == 0 || description.contains("Non-Credit", ignoreCase = true)
}

data class SemesterOutline(
    val semesterNumber: Int,
    val semesterName: String = "Semester $semesterNumber",
    val courses: List<CourseItem>
) {
    val totalCreditHours: Int
        get() = courses.sumOf { it.creditHours }

    val courseCount: Int
        get() = courses.size
}

data class ProgramCourseOutline(
    val id: String,
    val name: String,
    val headOfDepartment: String,
    val duration: String,
    val eligibility: String,
    val about: String? = null,
    val isIntermediate: Boolean = false,
    val semesters: List<SemesterOutline> = emptyList(),
    val note: String? = null
) {
    val totalSemesters: Int
        get() = semesters.size

    val totalCourses: Int
        get() = semesters.sumOf { it.courseCount }

    val totalCredits: Int
        get() = semesters.sumOf { it.totalCreditHours }
}
