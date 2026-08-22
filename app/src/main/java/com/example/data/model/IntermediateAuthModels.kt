package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntermediateStudentProfileDto(
    val id: String,
    val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("roll_number") val rollNumber: String,
    @SerialName("registration_number") val registrationNumber: String,
    val program: String,
    @SerialName("official_record_id") val officialRecordId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class OfficialIntermediateStudentDto(
    val id: String = "",
    @SerialName("roll_number") val rollNumber: String = "",
    @SerialName("registration_number") val registrationNumber: String = "",
    val program: String = "",
    @SerialName("program_name") val programName: String? = null,
    val session: String = "2024-2026",
    @SerialName("session_year") val sessionYear: String? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("father_name") val fatherName: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("part_number") val partNumber: Int? = null,
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
        get() = if (session.isNotBlank()) session else (sessionYear ?: "2024-2026")

    val effectiveDisplayName: String
        get() = when {
            !studentName.isNullOrBlank() -> studentName
            !firstName.isNullOrBlank() || !lastName.isNullOrBlank() -> "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
            else -> "Intermediate Student Record"
        }
}

data class IntermediateRegistrationForm(
    val firstName: String = "",
    val lastName: String = "",
    val rollNumber: String = "",
    val registrationNumber: String = "",
    val program: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class IntermediateLoginForm(
    val usernameOrRoll: String = "",
    val password: String = ""
)

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : AuthResult<T>()
    data class Error(val message: String, val code: String? = null) : AuthResult<Nothing>()
}
