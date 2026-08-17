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
    val id: String,
    @SerialName("roll_number") val rollNumber: String,
    @SerialName("registration_number") val registrationNumber: String,
    val program: String,
    val session: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("claimed_by_user_id") val claimedByUserId: String? = null
)

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
