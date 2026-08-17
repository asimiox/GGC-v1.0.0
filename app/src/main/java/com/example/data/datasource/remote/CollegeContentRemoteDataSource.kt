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
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

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
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get departments: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve departments")
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
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get academic programs: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve programs")
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
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get courses: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve courses")
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
            if (outline.id.isNullOrBlank()) {
                val inserted = client.from("course_outlines").insert(outline) {
                    select()
                }.decodeSingle<CourseOutlineDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("course_outlines").update(outline) {
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
            if (announcement.id.isNullOrBlank()) {
                val inserted = client.from("announcements").insert(announcement) {
                    select()
                }.decodeSingle<AnnouncementDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("announcements").update(announcement) {
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
            if (event.id.isNullOrBlank()) {
                val inserted = client.from("college_events").insert(event) {
                    select()
                }.decodeSingle<CollegeEventDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("college_events").update(event) {
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
            if (document.id.isNullOrBlank()) {
                val inserted = client.from("official_documents").insert(document) {
                    select()
                }.decodeSingle<OfficialDocumentDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("official_documents").update(document) {
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
            if (prospectus.id.isNullOrBlank()) {
                val inserted = client.from("prospectus").insert(prospectus) {
                    select()
                }.decodeSingle<ProspectusDto>()
                AuthResult.Success(inserted)
            } else {
                val updated = client.from("prospectus").update(prospectus) {
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
