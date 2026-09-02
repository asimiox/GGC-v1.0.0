package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 7 Official Notification Types for GGC M.B.Din
 */
@Serializable
enum class NotificationType(val key: String, val displayName: String) {
    @SerialName("announcement_new")
    ANNOUNCEMENT_NEW("announcement_new", "College Notice"),

    @SerialName("announcement_priority")
    ANNOUNCEMENT_PRIORITY("announcement_priority", "Urgent Notice"),

    @SerialName("event_new")
    EVENT_NEW("event_new", "College Event"),

    @SerialName("event_update")
    EVENT_UPDATE("event_update", "Event Update"),

    @SerialName("document_new")
    DOCUMENT_NEW("document_new", "Official Document"),

    @SerialName("course_outline_new")
    COURSE_OUTLINE_NEW("course_outline_new", "Course Outline"),

    @SerialName("prospectus_new")
    PROSPECTUS_NEW("prospectus_new", "College Prospectus");

    companion object {
        fun fromKey(key: String?): NotificationType {
            if (key.isNullOrBlank()) return ANNOUNCEMENT_NEW
            return entries.firstOrNull { it.key.equals(key.trim(), ignoreCase = true) }
                ?: ANNOUNCEMENT_NEW
        }
    }
}

/**
 * Official Notification DTO matching Supabase `notifications` table schema
 */
@Serializable
data class AppNotificationDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null, // null for broadcast/audience-based, or specific user
    @SerialName("notification_type") val notificationType: String = NotificationType.ANNOUNCEMENT_NEW.key,
    val title: String,
    val message: String,
    @SerialName("related_content_id") val relatedContentId: String? = null,
    @SerialName("content_type") val contentType: String? = "announcement", // "announcement", "event", "document", "course_outline", "prospectus"
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("department_name") val departmentName: String? = null,
    @SerialName("target_role") val targetRole: String? = "all", // "all", "student_bs", "student_intermediate", "teacher", "hod", "admin"
    @SerialName("is_priority") val isPriority: Boolean = false,
    @SerialName("is_pinned") val isPinned: Boolean = false,
    @SerialName("is_read") var isRead: Boolean = false,
    @SerialName("action_url") val actionUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    val typeEnum: NotificationType
        get() = NotificationType.fromKey(notificationType)

    /**
     * Verifies that the authenticated user is authorized to see this notification based on
     * user ID, department scope, and role restrictions.
     */
    fun isAuthorizedFor(userProfile: UserProfile): Boolean {
        // 1. Direct user recipient targeting
        if (!userId.isNullOrBlank()) {
            val currentUserId = userProfile.userId ?: userProfile.registrationNumber ?: userProfile.rollNumber ?: userProfile.facultyId
            return currentUserId != null && userId.equals(currentUserId, ignoreCase = true)
        }

        // 2. Target Role filtering
        if (!targetRole.isNullOrBlank() && !targetRole.equals("all", ignoreCase = true)) {
            val role = userProfile.appRole
            val allowed = when (targetRole.trim().lowercase()) {
                "student_bs", "bs" -> role == AppRole.STUDENT_BS || role.isAdminLevel
                "student_intermediate", "intermediate", "inter" -> role == AppRole.STUDENT_INTERMEDIATE || role.isAdminLevel
                "students", "student" -> role.isStudent || role.isAdminLevel
                "teacher", "faculty" -> role.isTeacherLevel
                "hod" -> role.isHodLevel
                "admin" -> role == AppRole.ADMIN
                else -> true
            }
            if (!allowed) return false
        }

        // 3. Department scope filtering (Lenient to ensure students don't miss college-wide news)
        val notifDept = (departmentName ?: departmentId)?.trim()?.lowercase() ?: ""
        if (notifDept.isNotBlank() && notifDept != "all" && notifDept != "general") {
            val userDept = (userProfile.department ?: "").trim().lowercase()
            val userProg = (userProfile.programName).trim().lowercase()

            if (userProfile.isAdmin) {
                // Admins see everything
                return true
            } else if (userDept.isNotBlank()) {
                val matches = userDept.contains(notifDept) || notifDept.contains(userDept) || userProg.contains(notifDept)
                if (!matches && !userProfile.isFaculty) return false
            } else if (userProg.isNotBlank()) {
                // Check if program name matches department keywords (e.g. IT, Computer Science)
                val matches = userProg.contains(notifDept) || notifDept.contains(userProg) ||
                        (notifDept.contains("information technology") && (userProg.contains("it") || userProg.contains("computer"))) ||
                        (notifDept.contains("computer") && userProg.contains("information technology"))
                // Allow general college news if no strict mismatch
            }
        }

        return true
    }
}
