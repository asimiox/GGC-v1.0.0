package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Overview DTO for Head of Department (HOD) portal.
 */
@Serializable
data class HodDepartmentOverviewDto(
    val success: Boolean = false,
    val error: String? = null,
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("department_name") val departmentName: String? = null,
    @SerialName("department_code") val departmentCode: String? = null,
    val category: String? = null,
    @SerialName("hod_name") val hodName: String? = null,
    @SerialName("faculty_count") val facultyCount: Int = 0,
    @SerialName("programs_count") val programsCount: Int = 0,
    @SerialName("courses_count") val coursesCount: Int = 0,
    @SerialName("announcements_count") val announcementsCount: Int = 0,
    @SerialName("documents_count") val documentsCount: Int = 0
)

/**
 * Faculty member summary inside HOD department roster.
 */
@Serializable
data class DepartmentFacultyMemberDto(
    val id: String,
    @SerialName("faculty_id") val facultyId: String,
    @SerialName("full_name") val fullName: String,
    val designation: String,
    val qualification: String,
    @SerialName("institutional_email") val institutionalEmail: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Result DTO for Department Faculty list query.
 */
@Serializable
data class DepartmentFacultyListDto(
    val success: Boolean = false,
    val error: String? = null,
    val department: String? = null,
    val faculty: List<DepartmentFacultyMemberDto> = emptyList()
)

/**
 * System overview for College Administrators.
 */
@Serializable
data class AdminSystemOverviewDto(
    val success: Boolean = false,
    val error: String? = null,
    @SerialName("bs_students_count") val bsStudentsCount: Int = 0,
    @SerialName("intermediate_students_count") val intermediateStudentsCount: Int = 0,
    @SerialName("faculty_count") val facultyCount: Int = 0,
    @SerialName("hods_count") val hodsCount: Int = 0,
    @SerialName("admins_count") val adminsCount: Int = 0,
    @SerialName("departments_count") val departmentsCount: Int = 0,
    @SerialName("programs_count") val programsCount: Int = 0,
    @SerialName("courses_count") val coursesCount: Int = 0,
    @SerialName("announcements_count") val announcementsCount: Int = 0,
    @SerialName("documents_count") val documentsCount: Int = 0,
    @SerialName("events_count") val eventsCount: Int = 0,
    @SerialName("prospectus_count") val prospectusCount: Int = 0
)

/**
 * Response for generic administrative management operations.
 */
@Serializable
data class AdminOperationResultDto(
    val success: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    @SerialName("record_id") val recordId: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val department: String? = null
)
