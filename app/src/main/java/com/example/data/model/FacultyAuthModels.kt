package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FacultyProfileDto(
    val id: String,
    val username: String,
    @SerialName("faculty_id") val facultyId: String,
    @SerialName("full_name") val fullName: String,
    val department: String,
    val designation: String,
    val qualification: String,
    @SerialName("institutional_email") val institutionalEmail: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("official_record_id") val officialRecordId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class OfficialFacultyDto(
    val id: String,
    @SerialName("faculty_id") val facultyId: String,
    @SerialName("full_name") val fullName: String,
    val department: String,
    val designation: String,
    val qualification: String,
    @SerialName("institutional_email") val institutionalEmail: String? = null,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("claimed_by_user_id") val claimedByUserId: String? = null,
    @SerialName("claimed_at") val claimedAt: String? = null
)

data class FacultyRegistrationForm(
    val facultyId: String = "",
    val fullName: String = "",
    val department: String = "",
    val designation: String = "",
    val qualification: String = "",
    val institutionalEmail: String = "",
    val phoneNumber: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class FacultyLoginForm(
    val usernameOrFacultyId: String = "",
    val password: String = ""
)
