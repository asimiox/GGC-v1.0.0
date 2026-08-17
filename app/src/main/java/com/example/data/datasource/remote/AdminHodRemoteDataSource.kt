package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AdminSystemOverviewDto
import com.example.data.model.AuthResult
import com.example.data.model.DepartmentFacultyListDto
import com.example.data.model.HodDepartmentOverviewDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Remote Data Source for protected Admin and HOD administrative RPC calls.
 * All procedures enforce strict server-side authentication and role verification in PostgreSQL.
 */
class AdminHodRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "AdminHodRemoteDS"

    /**
     * Retrieves overview statistics for the calling HOD's assigned department.
     */
    suspend fun getHodDepartmentOverview(): AuthResult<HodDepartmentOverviewDto> {
        return try {
            val response = client.postgrest.rpc("hod_get_department_overview")
            val overview = json.decodeFromString<HodDepartmentOverviewDto>(response.data)
            if (overview.success) {
                AuthResult.Success(overview)
            } else {
                AuthResult.Error(overview.error ?: "Failed to fetch HOD department overview")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in hod_get_department_overview: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to load HOD overview")
        }
    }

    /**
     * Retrieves the faculty member roster for the calling HOD's department.
     */
    suspend fun getHodDepartmentFaculty(): AuthResult<DepartmentFacultyListDto> {
        return try {
            val response = client.postgrest.rpc("hod_get_department_faculty")
            val list = json.decodeFromString<DepartmentFacultyListDto>(response.data)
            if (list.success) {
                AuthResult.Success(list)
            } else {
                AuthResult.Error(list.error ?: "Failed to fetch department faculty roster")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in hod_get_department_faculty: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to load department faculty")
        }
    }

    /**
     * Retrieves college-wide system overview statistics for Administrators.
     */
    suspend fun getAdminSystemOverview(): AuthResult<AdminSystemOverviewDto> {
        return try {
            val response = client.postgrest.rpc("admin_get_system_overview")
            val overview = json.decodeFromString<AdminSystemOverviewDto>(response.data)
            if (overview.success) {
                AuthResult.Success(overview)
            } else {
                AuthResult.Error(overview.error ?: "Failed to fetch administrative system overview")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in admin_get_system_overview: ${e.message}", e)
            AuthResult.Error(e.message ?: "Failed to load admin overview")
        }
    }

    /**
     * Promotes a verified faculty member to Head of Department (Admin only).
     */
    suspend fun assignHod(facultyUserId: String, departmentName: String): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_faculty_user_id", facultyUserId)
                put("p_department_name", departmentName)
            }
            val response = client.postgrest.rpc("admin_assign_hod", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to assign HOD role")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in admin_assign_hod: ${e.message}", e)
            AuthResult.Error(e.message ?: "Administrative operation failed")
        }
    }

    /**
     * Revokes HOD privileges from a user and reverts them to Teacher role (Admin only).
     */
    suspend fun revokeHod(targetUserId: String): AuthResult<AdminOperationResultDto> {
        return try {
            val params = buildJsonObject {
                put("p_target_user_id", targetUserId)
            }
            val response = client.postgrest.rpc("admin_revoke_hod", params)
            val result = json.decodeFromString<AdminOperationResultDto>(response.data)
            if (result.success) {
                AuthResult.Success(result)
            } else {
                AuthResult.Error(result.error ?: "Failed to revoke HOD role")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in admin_revoke_hod: ${e.message}", e)
            AuthResult.Error(e.message ?: "Administrative operation failed")
        }
    }
}
