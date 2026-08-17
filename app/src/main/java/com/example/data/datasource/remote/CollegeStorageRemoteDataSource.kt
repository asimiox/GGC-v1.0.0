package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AuthResult
import io.github.jan.supabase.storage.storage

/**
 * Remote Data Source for managing Supabase Storage interactions for official college files.
 * Adheres strictly to the GGC M.B.Din RBAC security model.
 */
class CollegeStorageRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val TAG = "CollegeStorageRemoteDS"

    companion object {
        // Logical storage bucket constants
        const val BUCKET_PROSPECTUS = "college-prospectus"
        const val BUCKET_OFFICIAL_DOCUMENTS = "official-documents"
        const val BUCKET_ANNOUNCEMENTS = "announcement-attachments"
        const val BUCKET_COURSE_OUTLINES = "course-outlines"
        const val BUCKET_PROFILE_PHOTOS = "profile-photos"
        const val BUCKET_COLLEGE_MEDIA = "college-media"

        /**
         * Helper path builders to ensure consistent naming conventions
         */
        fun buildProspectusPath(sessionYear: String, fileName: String): String {
            val cleanYear = sessionYear.trim().replace(" ", "_")
            val cleanName = fileName.trim().replace(" ", "_")
            return "$cleanYear/$cleanName"
        }

        fun buildDocumentPath(category: String, departmentCode: String?, fileName: String): String {
            val cleanCat = category.trim().lowercase().replace(" ", "_")
            val cleanDept = departmentCode?.trim()?.lowercase()?.replace(" ", "_") ?: "general"
            val cleanName = fileName.trim().replace(" ", "_")
            return "$cleanCat/$cleanDept/$cleanName"
        }

        fun buildAnnouncementAttachmentPath(departmentCode: String?, fileName: String): String {
            val cleanDept = departmentCode?.trim()?.lowercase()?.replace(" ", "_") ?: "general"
            val cleanName = fileName.trim().replace(" ", "_")
            return "$cleanDept/$cleanName"
        }

        fun buildCourseOutlinePath(departmentCode: String, programCode: String, fileName: String): String {
            val cleanDept = departmentCode.trim().lowercase().replace(" ", "_")
            val cleanProg = programCode.trim().lowercase().replace(" ", "_")
            val cleanName = fileName.trim().replace(" ", "_")
            return "$cleanDept/$cleanProg/$cleanName"
        }

        fun buildProfilePhotoPath(isFaculty: Boolean, userId: String, fileName: String): String {
            val folder = if (isFaculty) "faculty" else "students"
            val cleanUid = userId.trim()
            val cleanName = fileName.trim().replace(" ", "_")
            return "$folder/$cleanUid/$cleanName"
        }
    }

    /**
     * Obtains the public URL for a published college file.
     */
    fun getPublicUrl(bucketId: String, path: String): String {
        return try {
            client.storage.from(bucketId).publicUrl(path)
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving public URL for $bucketId/$path: ${e.message}", e)
            ""
        }
    }

    /**
     * Downloads file bytes from a public or authorized storage bucket.
     */
    suspend fun downloadFileBytes(bucketId: String, path: String): AuthResult<ByteArray> {
        return try {
            val bytes = client.storage.from(bucketId).downloadPublic(path)
            AuthResult.Success(bytes)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download $bucketId/$path: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to download file from storage")
        }
    }
}
