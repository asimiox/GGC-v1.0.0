package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminLoginForm(
    val identifier: String = "", // email or username or admin key
    val password: String = ""
)

@Serializable
data class AdminProfileDto(
    val id: String = "",
    val username: String = "",
    @SerialName("full_name") val fullName: String = "Super Administrator",
    @SerialName("email") val email: String? = null,
    val role: String = "admin",
    val department: String = "Central Administration",
    @SerialName("is_verified") val isVerified: Boolean = true
)

@Serializable
data class AdminAuditLogDto(
    val id: String,
    val action: String,
    @SerialName("performed_by") val performedBy: String,
    @SerialName("target_entity") val targetEntity: String,
    val details: String? = null,
    val timestamp: String
)
