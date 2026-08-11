package com.example.ui.screens.academics.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Department(
    val id: String,
    val name: String,
    val code: String,
    val category: String, // Sciences, IT & CS, Humanities, Commerce, Life Sciences
    val description: String,
    val hodName: String,
    val hodQualification: String,
    val hodEmail: String,
    val iconName: String,
    val facultyCount: Int,
    val programs: List<Program>
)

data class Program(
    val id: String,
    val departmentId: String,
    val title: String, // e.g., "BS Computer Science"
    val degreeType: String, // "BS 4-Years (8 Semesters)"
    val code: String, // "BSCS"
    val durationYears: Int = 4,
    val totalSemesters: Int = 8,
    val totalCreditHours: Int = 132,
    val eligibility: String,
    val description: String,
    val semesters: List<SemesterData>
)

data class SemesterData(
    val semesterNumber: Int, // 1..8
    val title: String, // "Semester 1"
    val subjects: List<Subject>
)

data class Subject(
    val id: String,
    val code: String, // e.g. "CS-301"
    val title: String, // e.g. "Data Structures & Algorithms"
    val creditHours: String, // "3 (2-1)"
    val category: String, // "Major Core", "General", "Elective", "University Core"
    val description: String,
    val syllabusTopics: List<String>,
    val recommendedBooks: List<String>,
    val resources: List<AcademicResource>
)

enum class ResourceType(val displayName: String) {
    COURSE_OUTLINE("Course Outline"),
    LECTURE_NOTES("Lecture Notes / Handouts"),
    PAST_PAPER("Past Examination Paper"),
    LAB_MANUAL("Lab Manual")
}

data class AcademicResource(
    val id: String,
    val subjectId: String,
    val title: String,
    val type: ResourceType,
    val year: String = "2023",
    val examTerm: String? = null, // "Midterm", "Finalterm", or null
    val fileSize: String = "2.4 MB",
    val fileType: String = "PDF",
    val downloadUrl: String = "",
    val uploadedBy: String = "Department Faculty"
)

data class FacultyMember(
    val id: String,
    val departmentId: String,
    val name: String,
    val designation: String, // e.g., "Associate Professor", "Assistant Professor", "Lecturer"
    val qualification: String, // e.g., "Ph.D. Computer Science (PU)"
    val specialization: String,
    val email: String,
    val phone: String = "+92 (546) 920000"
)
