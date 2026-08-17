package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AuthResult
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Remote Data Source for managing official student and faculty registries.
 * Strictly checks Admin authorization on the Supabase PostgreSQL server.
 */
class OfficialRegistryRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "OfficialRegistryRemoteDS"

    /**
     * Inserts or updates an official BS student record (Admin only).
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
                id?.let { put("p_id", it) }
                put("p_roll_number", rollNumber)
                put("p_registration_number", registrationNumber)
                put("p_program", program)
                put("p_session", session)
                firstName?.let { put("p_first_name", it) }
                lastName?.let { put("p_last_name", it) }
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
            Log.e(TAG, "Error in manageBsStudentRecord: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update BS student registry")
        }
    }

    /**
     * Inserts or updates an official Intermediate student record (Admin only).
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
                id?.let { put("p_id", it) }
                put("p_roll_number", rollNumber)
                put("p_registration_number", registrationNumber)
                put("p_program", program)
                put("p_session", session)
                firstName?.let { put("p_first_name", it) }
                lastName?.let { put("p_last_name", it) }
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
            Log.e(TAG, "Error in manageIntermediateStudentRecord: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update Intermediate student registry")
        }
    }

    /**
     * Inserts or updates an official Faculty record (Admin only).
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
                id?.let { put("p_id", it) }
                put("p_faculty_id", facultyId)
                put("p_full_name", fullName)
                firstName?.let { put("p_first_name", it) }
                lastName?.let { put("p_last_name", it) }
                put("p_department", department)
                put("p_designation", designation)
                put("p_qualification", qualification)
                institutionalEmail?.let { put("p_institutional_email", it) }
                phoneNumber?.let { put("p_phone_number", it) }
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
            Log.e(TAG, "Error in manageFacultyRecord: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to update Faculty registry")
        }
    }

    /**
     * Toggles the active status of any registry record (Admin only).
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
     * Resets a falsely or incorrectly claimed registry record (Admin only).
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
