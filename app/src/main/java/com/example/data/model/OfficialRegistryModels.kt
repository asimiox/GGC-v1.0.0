package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Official Faculty Registry DTO representing an approved faculty member record in public.official_faculty.
 */
@Serializable
data class OfficialFacultyRegistryDto(
    val id: String? = null,
    @SerialName("faculty_id") val facultyId: String = "",
    @SerialName("full_name") val fullName: String = "",
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val department: String = "",
    val designation: String = "",
    val qualification: String? = null,
    @SerialName("institutional_email") val institutionalEmail: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("claimed_by_user_id") val claimedByUserId: String? = null,
    @SerialName("claimed_at") val claimedAt: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
