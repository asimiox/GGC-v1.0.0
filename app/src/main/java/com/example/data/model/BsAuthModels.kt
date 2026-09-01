package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BsStudentProfileDto(
    val id: String,
    val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("roll_number") val rollNumber: String,
    @SerialName("registration_number") val registrationNumber: String,
    val program: String,
    val session: String? = null,
    val semester: String? = "Semester 1",
    @SerialName("official_record_id") val officialRecordId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class OfficialBsStudentDto(
    val id: String = "",
    @SerialName("roll_number") val rollNumber: String = "",
    @SerialName("registration_number") val registrationNumber: String = "",
    val program: String = "",
    @SerialName("program_name") val programName: String? = null,
    val session: String? = null,
    @SerialName("session_year") val sessionYear: String? = null,
    val semester: String? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("father_name") val fatherName: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("semester_number") val semesterNumber: Int? = null,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("claimed_by_user_id") val claimedByUserId: String? = null,
    @SerialName("claimed_at") val claimedAt: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val effectiveProgram: String
        get() = if (program.isNotBlank()) program else (programName ?: "")

    val effectiveSession: String
        get() = if (!session.isNullOrBlank()) session else (sessionYear ?: "2024-2028")

    val effectiveSemester: String
        get() = if (!semester.isNullOrBlank()) semester!! else (semesterNumber?.let { "Semester $it" } ?: "Semester 1")

    val effectiveDisplayName: String
        get() = when {
            !studentName.isNullOrBlank() -> studentName
            !firstName.isNullOrBlank() || !lastName.isNullOrBlank() -> "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
            else -> "BS Student Record"
        }
}

data class BsRegistrationForm(
    val firstName: String = "",
    val lastName: String = "",
    val rollNumber: String = "",
    val registrationNumber: String = "",
    val program: String = "",
    val session: String = "",
    val semester: String = "Semester 1",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class BsLoginForm(
    val usernameOrRoll: String = "",
    val password: String = ""
)
