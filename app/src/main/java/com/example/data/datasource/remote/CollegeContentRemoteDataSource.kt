package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AnnouncementDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto
import com.example.data.model.AcademicCatalogDefaults
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CollegeContentRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val TAG = "CollegeContentRemoteDS"

    // =========================================================================
    // 1. DEPARTMENTS & ACADEMIC HIERARCHY
    // =========================================================================

    suspend fun getDepartments(): AuthResult<List<DepartmentDto>> {
        return try {
            val list = client.from("departments")
                .select {
                    filter {
                        eq("is_active", true)
                    }
                    order("name", Order.ASCENDING)
                }.decodeList<DepartmentDto>()
            if (list.isNotEmpty()) {
                AuthResult.Success(list)
            } else {
                AuthResult.Success(AcademicCatalogDefaults.defaultDepartments)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Departments remote fetch fallback: ${e.message}")
            AuthResult.Success(AcademicCatalogDefaults.defaultDepartments)
        }
    }

    suspend fun getPrograms(departmentId: String? = null, includeUnpublished: Boolean = false): AuthResult<List<AcademicProgramDto>> {
        return try {
            val list = client.from("academic_programs")
                .select {
                    filter {
                        if (!departmentId.isNullOrBlank()) {
                            eq("department_id", departmentId)
                        }
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                    }
                    order("title", Order.ASCENDING)
                }.decodeList<AcademicProgramDto>()
            if (list.isNotEmpty()) {
                AuthResult.Success(list)
            } else {
                AuthResult.Success(AcademicCatalogDefaults.getProgramsForDepartment(departmentId))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Programs remote fetch fallback: ${e.message}")
            AuthResult.Success(AcademicCatalogDefaults.getProgramsForDepartment(departmentId))
        }
    }

    suspend fun getProgramsByDepartment(departmentId: String): AuthResult<List<AcademicProgramDto>> {
        return getPrograms(departmentId = departmentId, includeUnpublished = false)
    }

    suspend fun getCourses(
        programId: String? = null,
        departmentId: String? = null,
        semesterNumber: Int? = null,
        includeUnpublished: Boolean = false
    ): AuthResult<List<CourseDto>> {
        return try {
            val list = client.from("courses")
                .select {
                    filter {
                        if (!programId.isNullOrBlank()) {
                            eq("program_id", programId)
                        }
                        if (!departmentId.isNullOrBlank()) {
                            eq("department_id", departmentId)
                        }
                        if (semesterNumber != null) {
                            eq("semester_number", semesterNumber)
                        }
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                    }
                    order("code", Order.ASCENDING)
                }.decodeList<CourseDto>()
            if (list.isNotEmpty()) {
                AuthResult.Success(list)
            } else {
                AuthResult.Success(AcademicCatalogDefaults.getCoursesForProgram(programId, semesterNumber))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Courses remote fetch fallback: ${e.message}")
            AuthResult.Success(AcademicCatalogDefaults.getCoursesForProgram(programId, semesterNumber))
        }
    }

    suspend fun ensureProgramExists(program: AcademicProgramDto): String {
        return try {
            val existing = client.from("academic_programs")
                .select {
                    filter {
                        eq("code", program.code)
                    }
                }.decodeSingleOrNull<AcademicProgramDto>()

            if (existing?.id != null) {
                existing.id
            } else {
                val payload = buildJsonObject {
                    if (!program.id.isNullOrBlank() && !program.id.startsWith("prog_")) {
                        put("id", program.id)
                    }
                    put("department_id", program.departmentId)
                    put("title", program.title)
                    put("code", program.code)
                    put("degree_type", program.degreeType)
                    put("duration_years", program.durationYears)
                    put("total_semesters", program.totalSemesters)
                    put("is_published", true)
                }
                val inserted = client.from("academic_programs").insert(payload) {
                    select()
                }.decodeSingle<AcademicProgramDto>()
                inserted.id ?: program.id ?: ""
            }
        } catch (e: Exception) {
            Log.w(TAG, "ensureProgramExists notice: ${e.message}")
            program.id ?: ""
        }
    }

    suspend fun ensureCourseExists(course: CourseDto): String {
        return try {
            val existing = client.from("courses")
                .select {
                    filter {
                        eq("code", course.code)
                    }
                }.decodeSingleOrNull<CourseDto>()

            if (existing?.id != null) {
                existing.id
            } else {
                val payload = buildJsonObject {
                    if (!course.id.isNullOrBlank() && !course.id.startsWith("course_")) {
                        put("id", course.id)
                    }
                    put("program_id", course.programId)
                    put("department_id", course.departmentId)
                    put("code", course.code)
                    put("title", course.title)
                    put("credit_hours", course.creditHours)
                    put("semester_number", course.semesterNumber)
                    if (!course.category.isNullOrBlank()) put("category", course.category)
                    put("is_published", true)
                }
                val inserted = client.from("courses").insert(payload) {
                    select()
                }.decodeSingle<CourseDto>()
                inserted.id ?: course.id ?: ""
            }
        } catch (e: Exception) {
            Log.w(TAG, "ensureCourseExists notice: ${e.message}")
            course.id ?: ""
        }
    }

    // =========================================================================
    // 2. COURSE OUTLINES MANAGEMENT
    // =========================================================================

    suspend fun getCourseOutlines(
        courseId: String? = null,
        programId: String? = null,
        departmentId: String? = null,
        includeUnpublished: Boolean = true
    ): AuthResult<List<CourseOutlineDto>> {
        return try {
            val list = client.from("course_outlines")
                .select {
                    filter {
                        if (!courseId.isNullOrBlank()) {
                            eq("course_id", courseId)
                        }
                        if (!programId.isNullOrBlank()) {
                            eq("program_id", programId)
                        }
                        if (!departmentId.isNullOrBlank()) {
                            eq("department_id", departmentId)
                        }
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                    }
                    order("created_at", Order.DESCENDING)
                }.decodeList<CourseOutlineDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get course outlines: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve course outlines")
        }
    }

    suspend fun saveCourseOutline(outline: CourseOutlineDto): AuthResult<CourseOutlineDto> {
        return try {
            // Guarantee program and course exist in database tables if needed
            var finalProgramId = outline.programId
            var finalCourseId = outline.courseId

            // If this course is from the default catalog or user created, ensure it exists in DB
            val defaultCourse = AcademicCatalogDefaults.defaultCourses.firstOrNull { it.id == outline.courseId || it.code == outline.courseId }
            if (defaultCourse != null) {
                finalCourseId = ensureCourseExists(defaultCourse)
                if (finalProgramId.isNullOrBlank()) {
                    finalProgramId = defaultCourse.programId
                }
            }

            val defaultProg = AcademicCatalogDefaults.defaultPrograms.firstOrNull { it.id == finalProgramId || it.code == finalProgramId }
            if (defaultProg != null) {
                finalProgramId = ensureProgramExists(defaultProg)
            }

            val payload = buildJsonObject {
                put("course_id", finalCourseId)
                if (!finalProgramId.isNullOrBlank()) put("program_id", finalProgramId)
                if (!outline.departmentId.isNullOrBlank()) put("department_id", outline.departmentId)
                put("title", outline.title.trim())
                if (!outline.sessionYear.isNullOrBlank()) put("session_year", outline.sessionYear.trim())
                put("semester_number", outline.semesterNumber)
                if (!outline.outlineContent.isNullOrBlank()) put("outline_content", outline.outlineContent.trim())
                if (!outline.storagePath.isNullOrBlank()) put("storage_path", outline.storagePath.trim())
                if (!outline.fileName.isNullOrBlank()) put("file_name", outline.fileName.trim())
                if (outline.fileSizeBytes != null) put("file_size_bytes", outline.fileSizeBytes)
                put("mime_type", outline.mimeType)
                put("is_published", outline.isPublished)
            }
            if (outline.id.isNullOrBlank()) {
                val inserted = client.from("course_outlines").insert(payload) {
                    select()
                }.decodeSingle<CourseOutlineDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("course_outlines").update({
                    set("course_id", finalCourseId)
                    if (finalProgramId != null) set("program_id", finalProgramId)
                    if (outline.departmentId != null) set("department_id", outline.departmentId)
                    set("title", outline.title.trim())
                    if (outline.sessionYear != null) set("session_year", outline.sessionYear.trim())
                    set("semester_number", outline.semesterNumber)
                    if (outline.outlineContent != null) set("outline_content", outline.outlineContent.trim())
                    if (outline.storagePath != null) set("storage_path", outline.storagePath.trim())
                    if (outline.fileName != null) set("file_name", outline.fileName.trim())
                    if (outline.fileSizeBytes != null) set("file_size_bytes", outline.fileSizeBytes)
                    set("mime_type", outline.mimeType)
                    set("is_published", outline.isPublished)
                }) {
                    filter { eq("id", outline.id) }
                    select()
                }.decodeSingle<CourseOutlineDto>()
                AuthResult.Success(updated)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save course outline: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to save course outline")
        }
    }

    suspend fun deleteCourseOutline(id: String): AuthResult<Unit> {
        return try {
            client.from("course_outlines").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete course outline: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete course outline")
        }
    }

    suspend fun setCourseOutlinePublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return try {
            client.from("course_outlines").update({
                set("is_published", isPublished)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update course outline status: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update status")
        }
    }

    // =========================================================================
    // 3. ANNOUNCEMENTS / NOTICES MANAGEMENT
    // =========================================================================

    suspend fun getAnnouncements(
        departmentId: String? = null,
        includeUnpublished: Boolean = false
    ): AuthResult<List<AnnouncementDto>> {
        return try {
            val list = client.from("announcements")
                .select {
                    filter {
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                        if (!departmentId.isNullOrBlank()) {
                            or {
                                eq("department_id", departmentId)
                                exact("department_id", null)
                            }
                        }
                    }
                    order("is_pinned", Order.DESCENDING)
                    order("published_at", Order.DESCENDING)
                }.decodeList<AnnouncementDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get announcements: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve announcements")
        }
    }

    suspend fun saveAnnouncement(announcement: AnnouncementDto): AuthResult<AnnouncementDto> {
        return try {
            val payload = buildJsonObject {
                put("title", announcement.title.trim())
                put("content", announcement.content.trim())
                put("category", announcement.category.trim())
                if (!announcement.departmentId.isNullOrBlank()) put("department_id", announcement.departmentId)
                put("author_name", announcement.authorName.trim())
                put("is_pinned", announcement.isPinned)
                put("is_published", announcement.isPublished)
                if (!announcement.publishedAt.isNullOrBlank()) put("published_at", announcement.publishedAt)
                if (!announcement.attachmentStoragePath.isNullOrBlank()) put("attachment_storage_path", announcement.attachmentStoragePath)
                if (!announcement.attachmentName.isNullOrBlank()) put("attachment_name", announcement.attachmentName)
                if (announcement.attachmentSizeBytes != null) put("attachment_size_bytes", announcement.attachmentSizeBytes)
            }
            if (announcement.id.isNullOrBlank()) {
                val inserted = client.from("announcements").insert(payload) {
                    select()
                }.decodeSingle<AnnouncementDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("announcements").update({
                    set("title", announcement.title.trim())
                    set("content", announcement.content.trim())
                    set("category", announcement.category.trim())
                    if (announcement.departmentId != null) set("department_id", announcement.departmentId)
                    set("author_name", announcement.authorName.trim())
                    set("is_pinned", announcement.isPinned)
                    set("is_published", announcement.isPublished)
                    if (announcement.publishedAt != null) set("published_at", announcement.publishedAt)
                    if (announcement.attachmentStoragePath != null) set("attachment_storage_path", announcement.attachmentStoragePath)
                    if (announcement.attachmentName != null) set("attachment_name", announcement.attachmentName)
                    if (announcement.attachmentSizeBytes != null) set("attachment_size_bytes", announcement.attachmentSizeBytes)
                }) {
                    filter { eq("id", announcement.id) }
                    select()
                }.decodeSingle<AnnouncementDto>()
                AuthResult.Success(updated)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save announcement: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to save announcement")
        }
    }

    suspend fun deleteAnnouncement(id: String): AuthResult<Unit> {
        return try {
            client.from("announcements").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete announcement: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete announcement")
        }
    }

    suspend fun setAnnouncementPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return try {
            client.from("announcements").update({
                set("is_published", isPublished)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update announcement publish state: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update publish state")
        }
    }

    // =========================================================================
    // 4. EVENTS MANAGEMENT
    // =========================================================================

    suspend fun getEvents(
        departmentId: String? = null,
        includeUnpublished: Boolean = false
    ): AuthResult<List<CollegeEventDto>> {
        return try {
            val list = client.from("college_events")
                .select {
                    filter {
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                        if (!departmentId.isNullOrBlank()) {
                            or {
                                eq("department_id", departmentId)
                                exact("department_id", null)
                            }
                        }
                    }
                    order("event_date", Order.DESCENDING)
                }.decodeList<CollegeEventDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get college events: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve college events")
        }
    }

    suspend fun saveEvent(event: CollegeEventDto): AuthResult<CollegeEventDto> {
        return try {
            val payload = buildJsonObject {
                put("title", event.title.trim())
                put("description", event.description.trim())
                put("event_date", event.eventDate.trim())
                if (!event.eventTime.isNullOrBlank()) put("event_time", event.eventTime.trim())
                if (!event.venue.isNullOrBlank()) put("venue", event.venue.trim())
                put("category", event.category.trim())
                if (!event.departmentId.isNullOrBlank()) put("department_id", event.departmentId)
                put("is_upcoming", event.isUpcoming)
                put("is_published", event.isPublished)
                if (!event.bannerStoragePath.isNullOrBlank()) put("banner_storage_path", event.bannerStoragePath)
                if (!event.attachmentName.isNullOrBlank()) put("attachment_name", event.attachmentName)
            }
            if (event.id.isNullOrBlank()) {
                val inserted = client.from("college_events").insert(payload) {
                    select()
                }.decodeSingle<CollegeEventDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("college_events").update({
                    set("title", event.title.trim())
                    set("description", event.description.trim())
                    set("event_date", event.eventDate.trim())
                    if (event.eventTime != null) set("event_time", event.eventTime.trim())
                    if (event.venue != null) set("venue", event.venue.trim())
                    set("category", event.category.trim())
                    if (event.departmentId != null) set("department_id", event.departmentId)
                    set("is_upcoming", event.isUpcoming)
                    set("is_published", event.isPublished)
                    if (event.bannerStoragePath != null) set("banner_storage_path", event.bannerStoragePath)
                    if (event.attachmentName != null) set("attachment_name", event.attachmentName)
                }) {
                    filter { eq("id", event.id) }
                    select()
                }.decodeSingle<CollegeEventDto>()
                AuthResult.Success(updated)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save event: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to save event")
        }
    }

    suspend fun deleteEvent(id: String): AuthResult<Unit> {
        return try {
            client.from("college_events").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete event: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete event")
        }
    }

    suspend fun setEventPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return try {
            client.from("college_events").update({
                set("is_published", isPublished)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update event publish state: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update event status")
        }
    }

    // =========================================================================
    // 5. OFFICIAL DOCUMENTS MANAGEMENT
    // =========================================================================

    suspend fun getOfficialDocuments(
        type: String? = null,
        departmentId: String? = null,
        includeUnpublished: Boolean = false
    ): AuthResult<List<OfficialDocumentDto>> {
        return try {
            val list = client.from("official_documents")
                .select {
                    filter {
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                        if (!type.isNullOrBlank()) {
                            eq("document_type", type)
                        }
                        if (!departmentId.isNullOrBlank()) {
                            or {
                                eq("department_id", departmentId)
                                exact("department_id", null)
                            }
                        }
                    }
                    order("created_at", Order.DESCENDING)
                }.decodeList<OfficialDocumentDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get official documents: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve official documents")
        }
    }

    suspend fun saveOfficialDocument(document: OfficialDocumentDto): AuthResult<OfficialDocumentDto> {
        return try {
            val payload = buildJsonObject {
                put("title", document.title.trim())
                if (!document.description.isNullOrBlank()) put("description", document.description.trim())
                put("document_type", document.documentType.trim())
                if (!document.departmentId.isNullOrBlank()) put("department_id", document.departmentId.trim())
                put("storage_path", document.storagePath.trim())
                put("file_name", document.fileName.trim())
                if (document.fileSizeBytes != null) put("file_size_bytes", document.fileSizeBytes)
                put("mime_type", document.mimeType)
                put("is_published", document.isPublished)
            }
            if (document.id.isNullOrBlank()) {
                val inserted = client.from("official_documents").insert(payload) {
                    select()
                }.decodeSingle<OfficialDocumentDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("official_documents").update({
                    set("title", document.title.trim())
                    if (document.description != null) set("description", document.description.trim())
                    set("document_type", document.documentType.trim())
                    if (document.departmentId != null) set("department_id", document.departmentId.trim())
                    set("storage_path", document.storagePath.trim())
                    set("file_name", document.fileName.trim())
                    if (document.fileSizeBytes != null) set("file_size_bytes", document.fileSizeBytes)
                    set("mime_type", document.mimeType)
                    set("is_published", document.isPublished)
                }) {
                    filter { eq("id", document.id) }
                    select()
                }.decodeSingle<OfficialDocumentDto>()
                AuthResult.Success(updated)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save official document: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to save document")
        }
    }

    suspend fun deleteOfficialDocument(id: String): AuthResult<Unit> {
        return try {
            client.from("official_documents").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete official document: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete official document")
        }
    }

    suspend fun setOfficialDocumentPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return try {
            client.from("official_documents").update({
                set("is_published", isPublished)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update document publish state: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update publish state")
        }
    }

    // =========================================================================
    // 6. PROSPECTUS MANAGEMENT
    // =========================================================================

    suspend fun getProspectusList(includeUnpublished: Boolean = false): AuthResult<List<ProspectusDto>> {
        return try {
            val list = client.from("prospectus")
                .select {
                    filter {
                        if (!includeUnpublished) {
                            eq("is_published", true)
                        }
                    }
                    order("created_at", Order.DESCENDING)
                }.decodeList<ProspectusDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get prospectus list: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve prospectus records")
        }
    }

    suspend fun getCurrentProspectus(): AuthResult<ProspectusDto?> {
        return try {
            val list = client.from("prospectus")
                .select {
                    filter {
                        eq("is_published", true)
                        eq("is_current", true)
                    }
                    order("created_at", Order.DESCENDING)
                }.decodeList<ProspectusDto>()
            AuthResult.Success(list.firstOrNull())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current prospectus: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve current prospectus")
        }
    }

    suspend fun saveProspectus(prospectus: ProspectusDto): AuthResult<ProspectusDto> {
        return try {
            val payload = buildJsonObject {
                put("title", prospectus.title.trim())
                put("academic_session", prospectus.academicSession.trim())
                if (!prospectus.programLevel.isNullOrBlank()) put("program_level", prospectus.programLevel.trim())
                if (!prospectus.description.isNullOrBlank()) put("description", prospectus.description.trim())
                put("storage_path", prospectus.storagePath.trim())
                put("file_name", prospectus.fileName.trim())
                if (prospectus.fileSizeBytes != null) put("file_size_bytes", prospectus.fileSizeBytes)
                put("mime_type", prospectus.mimeType)
                if (!prospectus.coverImageStoragePath.isNullOrBlank()) put("cover_image_storage_path", prospectus.coverImageStoragePath.trim())
                put("is_current", prospectus.isCurrent)
                put("is_published", prospectus.isPublished)
            }
            if (prospectus.id.isNullOrBlank()) {
                val inserted = client.from("prospectus").insert(payload) {
                    select()
                }.decodeSingle<ProspectusDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("prospectus").update({
                    set("title", prospectus.title.trim())
                    set("academic_session", prospectus.academicSession.trim())
                    if (prospectus.programLevel != null) set("program_level", prospectus.programLevel.trim())
                    if (prospectus.description != null) set("description", prospectus.description.trim())
                    set("storage_path", prospectus.storagePath.trim())
                    set("file_name", prospectus.fileName.trim())
                    if (prospectus.fileSizeBytes != null) set("file_size_bytes", prospectus.fileSizeBytes)
                    set("mime_type", prospectus.mimeType)
                    if (prospectus.coverImageStoragePath != null) set("cover_image_storage_path", prospectus.coverImageStoragePath.trim())
                    set("is_current", prospectus.isCurrent)
                    set("is_published", prospectus.isPublished)
                }) {
                    filter { eq("id", prospectus.id) }
                    select()
                }.decodeSingle<ProspectusDto>()
                AuthResult.Success(updated)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save prospectus: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to save prospectus")
        }
    }

    suspend fun deleteProspectus(id: String): AuthResult<Unit> {
        return try {
            client.from("prospectus").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete prospectus: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete prospectus")
        }
    }

    suspend fun setProspectusCurrent(id: String, isCurrent: Boolean): AuthResult<Unit> {
        return try {
            if (isCurrent) {
                // If setting this one to current, first set all others to is_current = false
                try {
                    client.from("prospectus").update({
                        set("is_current", false)
                    }) {
                        filter { neq("id", id) }
                    }
                } catch (_: Exception) {}
            }
            client.from("prospectus").update({
                set("is_current", isCurrent)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set prospectus current status: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update status")
        }
    }

    suspend fun setProspectusPublished(id: String, isPublished: Boolean): AuthResult<Unit> {
        return try {
            client.from("prospectus").update({
                set("is_published", isPublished)
            }) {
                filter { eq("id", id) }
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update prospectus publish state: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update publish state")
        }
    }
}
