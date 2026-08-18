package com.example.data.repository

import com.example.data.datasource.remote.CollegeContentRemoteDataSource
import com.example.data.datasource.remote.NotificationRemoteDataSource
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto
import com.example.data.model.NotificationType
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto

/**
 * Clean Architecture repository providing official content management operations
 * for Announcements, Events, Official Documents, Prospectus, and Course Outlines.
 */
class CollegeContentRepository(
    private val remoteDataSource: CollegeContentRemoteDataSource = CollegeContentRemoteDataSource(),
    private val notificationRemoteDataSource: NotificationRemoteDataSource = NotificationRemoteDataSource()
) {

    // =========================================================================
    // 1. DEPARTMENTS & ACADEMIC HIERARCHY
    // =========================================================================

    suspend fun getDepartments(): AuthResult<List<DepartmentDto>> {
        return remoteDataSource.getDepartments()
    }

    suspend fun getPrograms(departmentId: String? = null, includeUnpublished: Boolean = false): AuthResult<List<AcademicProgramDto>> {
        return remoteDataSource.getPrograms(departmentId, includeUnpublished)
    }

    suspend fun getCourses(
        programId: String? = null,
        departmentId: String? = null,
        semesterNumber: Int? = null,
        includeUnpublished: Boolean = false
    ): AuthResult<List<CourseDto>> {
        return remoteDataSource.getCourses(programId, departmentId, semesterNumber, includeUnpublished)
    }

    // =========================================================================
    // 2. COURSE OUTLINES
    // =========================================================================

    suspend fun getCourseOutlines(
        courseId: String? = null,
        programId: String? = null,
        departmentId: String? = null,
        includeUnpublished: Boolean = true
    ): AuthResult<List<CourseOutlineDto>> {
        return remoteDataSource.getCourseOutlines(courseId, programId, departmentId, includeUnpublished)
    }

    suspend fun saveCourseOutline(outline: CourseOutlineDto): AuthResult<CourseOutlineDto> {
        val result = remoteDataSource.saveCourseOutline(outline)
        if (result is AuthResult.Success && outline.isPublished) {
            notificationRemoteDataSource.insertNotification(
                AppNotificationDto(
                    id = "out_${result.data.id ?: System.currentTimeMillis()}",
                    notificationType = NotificationType.COURSE_OUTLINE_NEW.key,
                    title = "New Course Outline: ${outline.title}",
                    message = "Semester ${outline.semesterNumber} course syllabus uploaded (${outline.fileName ?: "Document"}).",
                    relatedContentId = result.data.id,
                    contentType = "course_outline",
                    departmentId = outline.departmentId
                )
            )
        }
        return result
    }

    suspend fun deleteCourseOutline(id: String): AuthResult<Unit> {
        return remoteDataSource.deleteCourseOutline(id)
    }

    suspend fun setCourseOutlinePublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return remoteDataSource.setCourseOutlinePublished(id, isPublished)
    }

    // =========================================================================
    // 3. ANNOUNCEMENTS / NOTICES
    // =========================================================================

    suspend fun getAnnouncements(
        departmentId: String? = null,
        includeUnpublished: Boolean = true
    ): AuthResult<List<AnnouncementDto>> {
        return remoteDataSource.getAnnouncements(departmentId, includeUnpublished)
    }

    suspend fun saveAnnouncement(announcement: AnnouncementDto): AuthResult<AnnouncementDto> {
        val result = remoteDataSource.saveAnnouncement(announcement)
        if (result is AuthResult.Success && announcement.isPublished) {
            notificationRemoteDataSource.insertNotification(
                AppNotificationDto(
                    id = "ann_${result.data.id ?: System.currentTimeMillis()}",
                    notificationType = if (announcement.isPinned) NotificationType.ANNOUNCEMENT_PRIORITY.key else NotificationType.ANNOUNCEMENT_NEW.key,
                    title = announcement.title,
                    message = announcement.content.take(160),
                    relatedContentId = result.data.id,
                    contentType = "announcement",
                    departmentId = announcement.departmentId,
                    isPriority = announcement.isPinned,
                    isPinned = announcement.isPinned
                )
            )
        }
        return result
    }

    suspend fun deleteAnnouncement(id: String): AuthResult<Unit> {
        return remoteDataSource.deleteAnnouncement(id)
    }

    suspend fun setAnnouncementPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return remoteDataSource.setAnnouncementPublished(id, isPublished)
    }

    // =========================================================================
    // 4. EVENTS
    // =========================================================================

    suspend fun getEvents(
        departmentId: String? = null,
        includeUnpublished: Boolean = true
    ): AuthResult<List<CollegeEventDto>> {
        return remoteDataSource.getEvents(departmentId, includeUnpublished)
    }

    suspend fun saveEvent(event: CollegeEventDto): AuthResult<CollegeEventDto> {
        val result = remoteDataSource.saveEvent(event)
        if (result is AuthResult.Success && event.isPublished) {
            notificationRemoteDataSource.insertNotification(
                AppNotificationDto(
                    id = "ev_${result.data.id ?: System.currentTimeMillis()}",
                    notificationType = NotificationType.EVENT_NEW.key,
                    title = "College Event: ${event.title}",
                    message = "${event.eventDate} at ${event.venue ?: "College Campus"}. ${event.description.take(120)}",
                    relatedContentId = result.data.id,
                    contentType = "event",
                    departmentId = event.departmentId
                )
            )
        }
        return result
    }

    suspend fun deleteEvent(id: String): AuthResult<Unit> {
        return remoteDataSource.deleteEvent(id)
    }

    suspend fun setEventPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return remoteDataSource.setEventPublished(id, isPublished)
    }

    // =========================================================================
    // 5. OFFICIAL DOCUMENTS
    // =========================================================================

    suspend fun getOfficialDocuments(
        type: String? = null,
        departmentId: String? = null,
        includeUnpublished: Boolean = true
    ): AuthResult<List<OfficialDocumentDto>> {
        return remoteDataSource.getOfficialDocuments(type, departmentId, includeUnpublished)
    }

    suspend fun saveOfficialDocument(document: OfficialDocumentDto): AuthResult<OfficialDocumentDto> {
        val result = remoteDataSource.saveOfficialDocument(document)
        if (result is AuthResult.Success && document.isPublished) {
            notificationRemoteDataSource.insertNotification(
                AppNotificationDto(
                    id = "doc_${result.data.id ?: System.currentTimeMillis()}",
                    notificationType = NotificationType.DOCUMENT_NEW.key,
                    title = "Official Document: ${document.title}",
                    message = "Uploaded: ${document.fileName} (${document.documentType.replace('_', ' ')})",
                    relatedContentId = result.data.id,
                    contentType = "document",
                    departmentId = document.departmentId
                )
            )
        }
        return result
    }

    suspend fun deleteOfficialDocument(id: String): AuthResult<Unit> {
        return remoteDataSource.deleteOfficialDocument(id)
    }

    suspend fun setOfficialDocumentPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return remoteDataSource.setOfficialDocumentPublished(id, isPublished)
    }

    // =========================================================================
    // 6. PROSPECTUS
    // =========================================================================

    suspend fun getProspectusList(includeUnpublished: Boolean = true): AuthResult<List<ProspectusDto>> {
        return remoteDataSource.getProspectusList(includeUnpublished)
    }

    suspend fun getCurrentProspectus(): AuthResult<ProspectusDto?> {
        return remoteDataSource.getCurrentProspectus()
    }

    suspend fun saveProspectus(prospectus: ProspectusDto): AuthResult<ProspectusDto> {
        val result = remoteDataSource.saveProspectus(prospectus)
        if (result is AuthResult.Success && prospectus.isPublished) {
            notificationRemoteDataSource.insertNotification(
                AppNotificationDto(
                    id = "pro_${result.data.id ?: System.currentTimeMillis()}",
                    notificationType = NotificationType.PROSPECTUS_NEW.key,
                    title = "College Prospectus: ${prospectus.title}",
                    message = "Official admission prospectus for session ${prospectus.academicSession} is available.",
                    relatedContentId = result.data.id,
                    contentType = "prospectus"
                )
            )
        }
        return result
    }

    suspend fun deleteProspectus(id: String): AuthResult<Unit> {
        return remoteDataSource.deleteProspectus(id)
    }

    suspend fun setProspectusCurrent(id: String, isCurrent: Boolean): AuthResult<Unit> {
        return remoteDataSource.setProspectusCurrent(id, isCurrent)
    }

    suspend fun setProspectusPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return remoteDataSource.setProspectusPublished(id, isPublished)
    }
}
