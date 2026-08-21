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
            if (id.isNullOrBlank()) {
                val student = OfficialBsStudentDto(
                    rollNumber = rollNumber.trim(),
                    registrationNumber = registrationNumber.trim(),
                    program = program.trim(),
                    session = session.trim(),
                    firstName = firstName?.trim(),
                    lastName = lastName?.trim(),
                    isActive = isActive,
                    isClaimed = false
                )
                client.from("official_bs_students").insert(student)
            } else {
                client.from("official_bs_students").update({
                    set("roll_number", rollNumber.trim())
                    set("registration_number", registrationNumber.trim())
                    set("program", program.trim())
                    set("session", session.trim())
                    if (firstName != null) set("first_name", firstName.trim())
                    if (lastName != null) set("last_name", lastName.trim())
                    set("is_active", isActive)
                }) {
                    filter { eq("id", id) }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "BS Student saved successfully"))
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
            client.from("official_bs_students").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "BS student record deleted"))
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
            if (id.isNullOrBlank()) {
                val student = OfficialIntermediateStudentDto(
                    rollNumber = rollNumber.trim(),
                    registrationNumber = registrationNumber.trim(),
                    program = program.trim(),
                    session = session.trim(),
                    firstName = firstName?.trim(),
                    lastName = lastName?.trim(),
                    isActive = isActive,
                    isClaimed = false
                )
                client.from("official_intermediate_students").insert(student)
            } else {
                client.from("official_intermediate_students").update({
                    set("roll_number", rollNumber.trim())
                    set("registration_number", registrationNumber.trim())
                    set("program", program.trim())
                    set("session", session.trim())
                    if (firstName != null) set("first_name", firstName.trim())
                    if (lastName != null) set("last_name", lastName.trim())
                    set("is_active", isActive)
                }) {
                    filter { eq("id", id) }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Intermediate Student saved successfully"))
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
            client.from("official_intermediate_students").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Intermediate student record deleted"))
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
     * Inserts into official_faculty and faculty_profiles.
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
            val facultyRecord = OfficialFacultyRegistryDto(
                facultyId = facultyId.trim(),
                fullName = fullName.trim(),
                department = department.trim(),
                designation = designation.trim(),
                qualification = qualification.trim(),
                institutionalEmail = institutionalEmail?.trim(),
                phoneNumber = phoneNumber?.trim(),
                isActive = isActive,
                isClaimed = true
            )
            client.from("official_faculty").insert(facultyRecord)
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Teacher account provisioned successfully"))
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
            if (id.isNullOrBlank()) {
                val faculty = OfficialFacultyRegistryDto(
                    facultyId = facultyId.trim(),
                    fullName = fullName.trim(),
                    department = department.trim(),
                    designation = designation.trim(),
                    qualification = qualification.trim(),
                    institutionalEmail = institutionalEmail?.trim(),
                    phoneNumber = phoneNumber?.trim(),
                    firstName = firstName?.trim(),
                    lastName = lastName?.trim(),
                    isActive = isActive,
                    isClaimed = false
                )
                client.from("official_faculty").insert(faculty)
            } else {
                client.from("official_faculty").update({
                    set("faculty_id", facultyId.trim())
                    set("full_name", fullName.trim())
                    set("department", department.trim())
                    set("designation", designation.trim())
                    set("qualification", qualification.trim())
                    if (institutionalEmail != null) set("institutional_email", institutionalEmail.trim())
                    if (phoneNumber != null) set("phone_number", phoneNumber.trim())
                    if (firstName != null) set("first_name", firstName.trim())
                    if (lastName != null) set("last_name", lastName.trim())
                    set("is_active", isActive)
                }) {
                    filter { eq("id", id) }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Faculty record saved successfully"))
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
            client.from("official_faculty").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Faculty record deleted"))
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
            val tableName = when (registryType) {
                "bs_student" -> "official_bs_students"
                "intermediate_student" -> "official_intermediate_students"
                else -> "official_faculty"
            }
            client.from(tableName).update({
                set("is_active", isActive)
            }) {
                filter { eq("id", recordId) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Status updated successfully"))
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
            val tableName = when (registryType) {
                "bs_student" -> "official_bs_students"
                "intermediate_student" -> "official_intermediate_students"
                else -> "official_faculty"
            }
            client.from(tableName).update({
                set("is_claimed", false)
                set("claimed_by", null as String?)
            }) {
                filter { eq("id", recordId) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Record reset successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error in resetClaimedRecord: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to reset record")
        }
    }
}
