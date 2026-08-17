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

    suspend fun getProgramsByDepartment(departmentId: String): AuthResult<List<AcademicProgramDto>> {
        return try {
            val list = client.from("academic_programs")
                .select {
                    filter {
                        eq("department_id", departmentId)
                        eq("is_published", true)
                    }
                    order("title", Order.ASCENDING)
                }.decodeList<AcademicProgramDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get academic programs: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve programs")
        }
    }

    suspend fun getCourses(programId: String, semesterNumber: Int? = null): AuthResult<List<CourseDto>> {
        return try {
            val list = client.from("courses")
                .select {
                    filter {
                        eq("program_id", programId)
                        eq("is_published", true)
                        if (semesterNumber != null) {
                            eq("semester_number", semesterNumber)
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

    suspend fun getCourseOutlines(courseId: String): AuthResult<List<CourseOutlineDto>> {
        return try {
            val list = client.from("course_outlines")
                .select {
                    filter {
                        eq("course_id", courseId)
                        eq("is_published", true)
                    }
                }.decodeList<CourseOutlineDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get course outlines: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve course outlines")
        }
    }

    suspend fun getAnnouncements(departmentId: String? = null): AuthResult<List<AnnouncementDto>> {
        return try {
            val list = client.from("announcements")
                .select {
                    filter {
                        eq("is_published", true)
                        if (departmentId != null) {
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

    suspend fun getEvents(): AuthResult<List<CollegeEventDto>> {
        return try {
            val list = client.from("college_events")
                .select {
                    filter {
                        eq("is_published", true)
                    }
                    order("event_date", Order.DESCENDING)
                }.decodeList<CollegeEventDto>()
            AuthResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get college events: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve college events")
        }
    }

    suspend fun getOfficialDocuments(type: String? = null): AuthResult<List<OfficialDocumentDto>> {
        return try {
            val list = client.from("official_documents")
                .select {
                    filter {
                        eq("is_published", true)
                        if (type != null) {
                            eq("document_type", type)
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
            Log.e(TAG, "Failed to get prospectus: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to retrieve prospectus")
        }
    }
}
