package com.example.data.repository

import com.example.data.datasource.remote.CollegeStorageRemoteDataSource
import com.example.data.model.AuthResult

/**
 * Clean Architecture repository providing storage access methods for the official college app.
 */
class CollegeStorageRepository(
    private val remoteDataSource: CollegeStorageRemoteDataSource = CollegeStorageRemoteDataSource()
) {
    /**
     * Resolves the public URL for a prospectus document.
     */
    fun getProspectusUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_PROSPECTUS, path)
    }

    /**
     * Resolves the public URL for an official institutional document.
     */
    fun getOfficialDocumentUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_OFFICIAL_DOCUMENTS, path)
    }

    /**
     * Resolves the public URL for an announcement attachment.
     */
    fun getAnnouncementAttachmentUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_ANNOUNCEMENTS, path)
    }

    /**
     * Resolves the public URL for a course outline document.
     */
    fun getCourseOutlineUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_COURSE_OUTLINES, path)
    }

    /**
     * Resolves the public URL for a profile avatar photo.
     */
    fun getProfilePhotoUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_PROFILE_PHOTOS, path)
    }

    /**
     * Resolves the public URL for college event or banner media.
     */
    fun getCollegeMediaUrl(path: String): String {
        return remoteDataSource.getPublicUrl(CollegeStorageRemoteDataSource.BUCKET_COLLEGE_MEDIA, path)
    }

    /**
     * Downloads file bytes for offline view / cache.
     */
    suspend fun downloadDocument(bucketId: String, path: String): AuthResult<ByteArray> {
        return remoteDataSource.downloadFileBytes(bucketId, path)
    }

    /**
     * Uploads an official prospectus document file to Supabase Storage.
     */
    suspend fun uploadProspectus(sessionYear: String, fileName: String, bytes: ByteArray): AuthResult<String> {
        val path = CollegeStorageRemoteDataSource.buildProspectusPath(sessionYear, fileName)
        return remoteDataSource.uploadFileBytes(CollegeStorageRemoteDataSource.BUCKET_PROSPECTUS, path, bytes)
    }

    /**
     * Uploads an official institutional document file to Supabase Storage.
     */
    suspend fun uploadOfficialDocument(category: String, departmentCode: String?, fileName: String, bytes: ByteArray): AuthResult<String> {
        val path = CollegeStorageRemoteDataSource.buildDocumentPath(category, departmentCode, fileName)
        return remoteDataSource.uploadFileBytes(CollegeStorageRemoteDataSource.BUCKET_OFFICIAL_DOCUMENTS, path, bytes)
    }

    /**
     * Uploads an announcement attachment to Supabase Storage.
     */
    suspend fun uploadAnnouncementAttachment(departmentCode: String?, fileName: String, bytes: ByteArray): AuthResult<String> {
        val path = CollegeStorageRemoteDataSource.buildAnnouncementAttachmentPath(departmentCode, fileName)
        return remoteDataSource.uploadFileBytes(CollegeStorageRemoteDataSource.BUCKET_ANNOUNCEMENTS, path, bytes)
    }

    /**
     * Uploads a course outline document to Supabase Storage.
     */
    suspend fun uploadCourseOutline(departmentCode: String, programCode: String, fileName: String, bytes: ByteArray): AuthResult<String> {
        val path = CollegeStorageRemoteDataSource.buildCourseOutlinePath(departmentCode, programCode, fileName)
        return remoteDataSource.uploadFileBytes(CollegeStorageRemoteDataSource.BUCKET_COURSE_OUTLINES, path, bytes)
    }

    /**
     * Uploads college media banner to Supabase Storage.
     */
    suspend fun uploadCollegeMedia(fileName: String, bytes: ByteArray): AuthResult<String> {
        val cleanName = fileName.trim().replace(" ", "_")
        val path = "events/$cleanName"
        return remoteDataSource.uploadFileBytes(CollegeStorageRemoteDataSource.BUCKET_COLLEGE_MEDIA, path, bytes)
    }

    /**
     * Deletes a storage file from a specified bucket.
     */
    suspend fun deleteFile(bucketId: String, path: String): AuthResult<Unit> {
        return remoteDataSource.deleteFile(bucketId, path)
    }
}
