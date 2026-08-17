package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 1. Department DTO
 */
@Serializable
data class DepartmentDto(
    val id: String? = null,
    val name: String,
    val code: String,
    val category: String = "Sciences",
    val description: String? = null,
    @SerialName("hod_name") val hodName: String? = null,
    @SerialName("hod_qualification") val hodQualification: String? = null,
    @SerialName("hod_email") val hodEmail: String? = null,
    @SerialName("icon_name") val iconName: String = "ic_school",
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 2. Academic Program DTO
 */
@Serializable
data class AcademicProgramDto(
    val id: String? = null,
    @SerialName("department_id") val departmentId: String,
    val title: String,
    val code: String,
    @SerialName("degree_type") val degreeType: String = "BS 4-Years",
    @SerialName("duration_years") val durationYears: Int = 4,
    @SerialName("total_semesters") val totalSemesters: Int = 8,
    @SerialName("total_credit_hours") val totalCreditHours: Int? = 130,
    val eligibility: String? = null,
    val description: String? = null,
    @SerialName("is_intermediate") val isIntermediate: Boolean = false,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 3. Course / Subject DTO
 */
@Serializable
data class CourseDto(
    val id: String? = null,
    @SerialName("program_id") val programId: String,
    @SerialName("department_id") val departmentId: String,
    val code: String,
    val title: String,
    @SerialName("credit_hours") val creditHours: String = "3 (3-0)",
    @SerialName("semester_number") val semesterNumber: Int = 1,
    val category: String? = "Major Core",
    val description: String? = null,
    @SerialName("syllabus_topics") val syllabusTopics: List<String> = emptyList(),
    @SerialName("recommended_books") val recommendedBooks: List<String> = emptyList(),
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 4. Course Outline DTO
 */
@Serializable
data class CourseOutlineDto(
    val id: String? = null,
    @SerialName("course_id") val courseId: String,
    @SerialName("program_id") val programId: String? = null,
    @SerialName("department_id") val departmentId: String? = null,
    val title: String,
    @SerialName("session_year") val sessionYear: String? = null,
    @SerialName("semester_number") val semesterNumber: Int = 1,
    @SerialName("outline_content") val outlineContent: String? = null,
    @SerialName("storage_path") val storagePath: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("file_size_bytes") val fileSizeBytes: Long? = null,
    @SerialName("mime_type") val mimeType: String = "application/pdf",
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 5. Announcement / Notice DTO
 */
@Serializable
data class AnnouncementDto(
    val id: String? = null,
    val title: String,
    val content: String,
    val category: String = "General",
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("author_id") val authorId: String? = null,
    @SerialName("author_name") val authorName: String = "College Administration",
    @SerialName("is_pinned") val isPinned: Boolean = false,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("attachment_storage_path") val attachmentStoragePath: String? = null,
    @SerialName("attachment_name") val attachmentName: String? = null,
    @SerialName("attachment_size_bytes") val attachmentSizeBytes: Long? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 6. College Event DTO
 */
@Serializable
data class CollegeEventDto(
    val id: String? = null,
    val title: String,
    val description: String,
    @SerialName("event_date") val eventDate: String,
    @SerialName("event_time") val eventTime: String? = null,
    val venue: String? = "College Auditorium",
    val category: String = "College",
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("is_upcoming") val isUpcoming: Boolean = true,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("banner_storage_path") val bannerStoragePath: String? = null,
    @SerialName("attachment_name") val attachmentName: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 7. Official Document DTO
 */
@Serializable
data class OfficialDocumentDto(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    @SerialName("document_type") val documentType: String, // 'admission', 'academic_notice', 'rules_regulations', 'form', 'fee_structure', 'examination', 'other'
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("file_size_bytes") val fileSizeBytes: Long? = null,
    @SerialName("mime_type") val mimeType: String = "application/pdf",
    @SerialName("academic_session") val academicSession: String? = null,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("uploaded_by") val uploadedBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * 8. Prospectus DTO
 */
@Serializable
data class ProspectusDto(
    val id: String? = null,
    val title: String,
    @SerialName("academic_session") val academicSession: String,
    @SerialName("program_level") val programLevel: String? = "Comprehensive",
    val description: String? = null,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("file_size_bytes") val fileSizeBytes: Long? = null,
    @SerialName("mime_type") val mimeType: String = "application/pdf",
    @SerialName("cover_image_storage_path") val coverImageStoragePath: String? = null,
    @SerialName("is_current") val isCurrent: Boolean = false,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("uploaded_by") val uploadedBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
