package com.example.data.repository

import com.example.data.datasource.remote.CollegeContentRemoteDataSource
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AnnouncementDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto

/**
 * Clean Architecture repository providing official content management operations
 * for Announcements, Events, Official Documents, Prospectus, and Course Outlines.
 */
class CollegeContentRepository(
    private val remoteDataSource: CollegeContentRemoteDataSource = CollegeContentRemoteDataSource()
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
        return remoteDataSource.saveCourseOutline(outline)
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
        return remoteDataSource.saveAnnouncement(announcement)
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
        return remoteDataSource.saveEvent(event)
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
        return remoteDataSource.saveOfficialDocument(document)
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
        return remoteDataSource.saveProspectus(prospectus)
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
