package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AuthResult
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Remote Data Source for managing official BS, Intermediate, and Faculty registries.
 * Strictly verifies Admin and HOD authorization at the PostgreSQL layer.
 */
class OfficialRegistryRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "OfficialRegistryRemoteDS"

    // =========================================================================
    // 1. OFFICIAL BS STUDENTS REGISTRY
    // =========================================================================

    /**
     * Fetches official BS students registry records with optional filters.
     */
    suspend fun fetchOfficialBsStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialBsStudentDto>> {
        return try {
            val list = client.from("official_bs_students").select {
                filter {
                    if (!program.isNullOrBlank()) {
                        eq("program", program)
                    }
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialBsStudentDto>()

            val filteredList = if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                list.filter { student ->
                    student.rollNumber.lowercase().contains(query) ||
                    student.registrationNumber.lowercase().contains(query) ||
                    (student.firstName?.lowercase()?.contains(query) == true) ||
                    (student.lastName?.lowercase()?.contains(query) == true) ||
                    student.program.lowercase().contains(query)
                }
            } else {
                list
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching BS students registry: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to fetch official BS students list")
        }
    }

    /**
     * Inserts or updates an official BS student record (Admin / HOD).
     */
    suspend fun manageBsStudentRecord(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                id?.takeIf { it.isNotBlank() }?.let { put("p_id", it) }
                put("p_roll_number", rollNumber.trim())
                put("p_registration_number", registrationNumber.trim())
                put("p_program", program.trim())
                put("p_session", session.trim())
                firstName?.takeIf { it.isNotBlank() }?.let { put("p_first_name", it.trim()) }
                lastName?.takeIf { it.isNotBlank() }?.let { put("p_last_name", it.trim()) }
                put("p_is_active", isActive)
            }
            val response = client.postgrest.rpc("admin_manage_bs_student_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to save BS student record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error managing BS student record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update BS student registry")
        }
    }

    /**
     * Safely deletes an unclaimed official BS student record.
     */
    suspend fun deleteBsStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_id", id)
            }
            val response = client.postgrest.rpc("admin_delete_bs_student_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to delete BS student record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting BS student record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete BS record")
        }
    }

    // =========================================================================
    // 2. OFFICIAL INTERMEDIATE STUDENTS REGISTRY
    // =========================================================================

    /**
     * Fetches official Intermediate students registry records with optional filters.
     */
    suspend fun fetchOfficialIntermediateStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialIntermediateStudentDto>> {
        return try {
            val list = client.from("official_intermediate_students").select {
                filter {
                    if (!program.isNullOrBlank()) {
                        eq("program", program)
                    }
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialIntermediateStudentDto>()

            val filteredList = if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                list.filter { student ->
                    student.rollNumber.lowercase().contains(query) ||
                    student.registrationNumber.lowercase().contains(query) ||
                    (student.firstName?.lowercase()?.contains(query) == true) ||
                    (student.lastName?.lowercase()?.contains(query) == true) ||
                    student.program.lowercase().contains(query)
                }
            } else {
                list
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Intermediate students registry: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to fetch official Intermediate students list")
        }
    }

    /**
     * Inserts or updates an official Intermediate student record (Admin / HOD).
     */
    suspend fun manageIntermediateStudentRecord(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String = "2024-2026",
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                id?.takeIf { it.isNotBlank() }?.let { put("p_id", it) }
                put("p_roll_number", rollNumber.trim())
                put("p_registration_number", registrationNumber.trim())
                put("p_program", program.trim())
                put("p_session", session.trim())
                firstName?.takeIf { it.isNotBlank() }?.let { put("p_first_name", it.trim()) }
                lastName?.takeIf { it.isNotBlank() }?.let { put("p_last_name", it.trim()) }
                put("p_is_active", isActive)
            }
            val response = client.postgrest.rpc("admin_manage_intermediate_student_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to save Intermediate student record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error managing Intermediate student record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update Intermediate student registry")
        }
    }

    /**
     * Safely deletes an unclaimed official Intermediate student record.
     */
    suspend fun deleteIntermediateStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_id", id)
            }
            val response = client.postgrest.rpc("admin_delete_intermediate_student_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to delete Intermediate student record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting Intermediate student record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete Intermediate record")
        }
    }

    // =========================================================================
    // 3. OFFICIAL FACULTY REGISTRY
    // =========================================================================

    /**
     * Fetches official Faculty registry records with optional department and search filters.
     */
    suspend fun fetchOfficialFaculty(
        department: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialFacultyRegistryDto>> {
        return try {
            val list = client.from("official_faculty").select {
                filter {
                    if (!department.isNullOrBlank()) {
                        eq("department", department)
                    }
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialFacultyRegistryDto>()

            val filteredList = if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                list.filter { faculty ->
                    faculty.facultyId.lowercase().contains(query) ||
                    faculty.fullName.lowercase().contains(query) ||
                    (faculty.institutionalEmail?.lowercase()?.contains(query) == true) ||
                    faculty.department.lowercase().contains(query) ||
                    faculty.designation.lowercase().contains(query)
                }
            } else {
                list
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Faculty registry: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to fetch official faculty list")
        }
    }

    /**
     * Provisions a teacher account securely by an Administrator.
     * Inserts into faculty_profiles, user_roles with role 'teacher', and official_faculty.
     */
    suspend fun provisionTeacherAccount(
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        institutionalEmail: String? = null,
        username: String,
        temporaryPassword: String,
        phoneNumber: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_faculty_id", facultyId.trim())
                put("p_full_name", fullName.trim())
                put("p_department", department.trim())
                put("p_designation", designation.trim())
                put("p_qualification", qualification.trim())
                institutionalEmail?.takeIf { it.isNotBlank() }?.let { put("p_institutional_email", it.trim()) }
                put("p_username", username.trim().lowercase())
                put("p_temporary_password", temporaryPassword.trim())
                phoneNumber?.takeIf { it.isNotBlank() }?.let { put("p_phone_number", it.trim()) }
                put("p_is_active", isActive)
            }
            val response = client.postgrest.rpc("admin_provision_teacher", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to provision teacher account")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error provisioning teacher account: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to provision teacher account")
        }
    }

    /**
     * Inserts or updates an official Faculty record (Admin / HOD).
     */
    suspend fun manageFacultyRecord(
        id: String? = null,
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        institutionalEmail: String? = null,
        phoneNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                id?.takeIf { it.isNotBlank() }?.let { put("p_id", it) }
                put("p_faculty_id", facultyId.trim())
                put("p_full_name", fullName.trim())
                firstName?.takeIf { it.isNotBlank() }?.let { put("p_first_name", it.trim()) }
                lastName?.takeIf { it.isNotBlank() }?.let { put("p_last_name", it.trim()) }
                put("p_department", department.trim())
                put("p_designation", designation.trim())
                put("p_qualification", qualification.trim())
                institutionalEmail?.takeIf { it.isNotBlank() }?.let { put("p_institutional_email", it.trim()) }
                phoneNumber?.takeIf { it.isNotBlank() }?.let { put("p_phone_number", it.trim()) }
                put("p_is_active", isActive)
            }
            val response = client.postgrest.rpc("admin_manage_faculty_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to save faculty record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error managing Faculty record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update Faculty registry")
        }
    }

    /**
     * Safely deletes an unclaimed official Faculty record.
     */
    suspend fun deleteFacultyRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_id", id)
            }
            val response = client.postgrest.rpc("admin_delete_faculty_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to delete faculty record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting Faculty record: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to delete Faculty record")
        }
    }

    // =========================================================================
    // 4. COMMON STATUS & AUDIT SAFETY RPCs
    // =========================================================================

    /**
     * Toggles the active status of any registry record (Admin / HOD).
     */
    suspend fun setRegistryRecordActive(
        registryType: String, // "bs_student", "intermediate_student", "faculty"
        recordId: String,
        isActive: Boolean
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_registry_type", registryType)
                put("p_record_id", recordId)
                put("p_is_active", isActive)
            }
            val response = client.postgrest.rpc("admin_set_registry_record_active", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to update record active status")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setRegistryRecordActive: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to alter active status")
        }
    }

    /**
     * Resets a claimed registry record for administrative correction (Super Admin only).
     */
    suspend fun resetClaimedRecord(
        registryType: String, // "bs_student", "intermediate_student", "faculty"
        recordId: String,
        reason: String = "Administrative correction"
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_registry_type", registryType)
                put("p_record_id", recordId)
                put("p_reason", reason)
            }
            val response = client.postgrest.rpc("admin_reset_claimed_registry_record", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to reset claimed record")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in resetClaimedRecord: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to reset record")
        }
    }
}
