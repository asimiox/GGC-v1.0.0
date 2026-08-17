package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Strict 5-Role Hierarchy for GGC M.B.Din Official App
 */
@Serializable
enum class AppRole(val roleKey: String, val displayName: String) {
    @SerialName("student_bs")
    STUDENT_BS("student_bs", "BS Student"),

    @SerialName("student_intermediate")
    STUDENT_INTERMEDIATE("student_intermediate", "Intermediate Student"),

    @SerialName("teacher")
    TEACHER("teacher", "Faculty Teacher"),

    @SerialName("hod")
    HOD("hod", "Head of Department (HOD)"),

    @SerialName("admin")
    ADMIN("admin", "System Administrator");

    val isStudent: Boolean
        get() = this == STUDENT_BS || this == STUDENT_INTERMEDIATE

    val isTeacherLevel: Boolean
        get() = this == TEACHER || this == HOD || this == ADMIN

    val isHodLevel: Boolean
        get() = this == HOD || this == ADMIN

    val isAdminLevel: Boolean
        get() = this == ADMIN

    companion object {
        fun fromKey(key: String?): AppRole {
            if (key.isNullOrBlank()) return STUDENT_BS
            return entries.firstOrNull { it.roleKey.equals(key.trim(), ignoreCase = true) }
                ?: when (key.trim().lowercase()) {
                    "student", "bs" -> STUDENT_BS
                    "intermediate", "inter", "fa", "fsc", "ics", "icom" -> STUDENT_INTERMEDIATE
                    "faculty", "teacher", "lecturer", "assistant professor", "associate professor" -> TEACHER
                    "hod", "head of department", "principal", "vice principal" -> HOD
                    "admin", "administrator", "superadmin" -> ADMIN
                    else -> STUDENT_BS
                }
        }
    }
}

@Serializable
data class UserRoleDto(
    @SerialName("user_id") val userId: String? = null,
    val role: String,
    val department: String? = null,
    @SerialName("assigned_at") val assignedAt: String? = null,
    val authenticated: Boolean = true
) {
    val appRole: AppRole
        get() = AppRole.fromKey(role)
}
