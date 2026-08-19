package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AuthResult
import com.example.data.model.FacultyLoginForm
import com.example.data.model.FacultyProfileDto
import com.example.data.model.FacultyRegistrationForm
import com.example.data.model.OfficialFacultyDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class FacultyAuthRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "FacultyAuthRemote"

    /**
     * Checks if a Faculty username is available using the secure SECURITY DEFINER RPC.
     */
    suspend fun checkUsernameAvailable(username: String): AuthResult<Boolean> {
        return try {
            val cleanUsername = username.trim().lowercase()
            if (cleanUsername.length < 3) {
                return AuthResult.Error("Username must be at least 3 characters long.")
            }
            val rpcParams = buildJsonObject {
                put("p_username", cleanUsername)
            }
            val rpcResponse = client.postgrest.rpc("check_faculty_username_available", rpcParams)
            val bodyText = rpcResponse.data
            if (bodyText.isNotBlank()) {
                val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                val isAvailable = jsonObj["available"]?.jsonPrimitive?.booleanOrNull ?: false
                if (!isAvailable) {
                    val errorMsg = jsonObj["error"]?.jsonPrimitive?.content
                        ?: "The username \"$username\" is already taken. Please choose another username."
                    return AuthResult.Error(errorMsg)
                }
                return AuthResult.Success(true)
            }
            AuthResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Faculty username availability", e)
            val msg = e.localizedMessage ?: ""
            if (msg.contains("permission denied", ignoreCase = true) || msg.contains("401", ignoreCase = true)) {
                AuthResult.Error("Unable to verify username right now. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Unable to verify username availability."))
            }
        }
    }

    /**
     * Checks if faculty credentials are valid for registration.
     */
    suspend fun checkEligibility(
        facultyId: String,
        department: String,
        username: String,
        institutionalEmail: String? = null
    ): AuthResult<OfficialFacultyDto> {
        return try {
            val cleanFacultyId = facultyId.trim().uppercase()
            val cleanDept = department.trim()
            val cleanUsername = username.trim().lowercase()

            val usernameCheck = checkUsernameAvailable(cleanUsername)
            if (usernameCheck is AuthResult.Error) {
                return AuthResult.Error(usernameCheck.message)
            }

            AuthResult.Success(
                OfficialFacultyDto(
                    id = "",
                    facultyId = cleanFacultyId,
                    fullName = cleanUsername,
                    department = cleanDept,
                    designation = "Faculty Member",
                    qualification = ""
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Faculty eligibility", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify faculty details."))
        }
    }

    /**
     * Registers a new Faculty account directly via secure RPC (No email needed).
     */
    suspend fun registerFaculty(
        form: FacultyRegistrationForm
    ): AuthResult<FacultyProfileDto> {
        return try {
            val cleanFacultyId = form.facultyId.trim().uppercase()
            val cleanDepartment = form.department.trim()
            val cleanUsername = form.username.trim().lowercase()
            val cleanFullName = form.fullName.trim()

            val regParams = buildJsonObject {
                put("p_faculty_id", cleanFacultyId)
                put("p_department", cleanDepartment)
                put("p_designation", "Faculty Member")
                put("p_qualification", "")
                put("p_username", cleanUsername)
                put("p_full_name", cleanFullName)
                put("p_password", form.password)
            }

            val rpcResponse = client.postgrest.rpc("direct_register_faculty", regParams)
            val bodyText = rpcResponse.data
            if (bodyText.isBlank()) {
                return AuthResult.Error("No response received from registration server.")
            }

            val jsonObj = json.parseToJsonElement(bodyText).jsonObject
            val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
            if (!isSuccess) {
                val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                    ?: "Registration failed. Please check your details and try again."
                return AuthResult.Error(errMsg)
            }

            val profileObj = jsonObj["profile"]?.jsonObject
            val profile = if (profileObj != null) {
                FacultyProfileDto(
                    id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                    username = profileObj["username"]?.jsonPrimitive?.content ?: cleanUsername,
                    facultyId = profileObj["faculty_id"]?.jsonPrimitive?.content ?: cleanFacultyId,
                    fullName = profileObj["full_name"]?.jsonPrimitive?.content ?: cleanFullName,
                    department = profileObj["department"]?.jsonPrimitive?.content ?: cleanDepartment,
                    designation = profileObj["designation"]?.jsonPrimitive?.content ?: "Faculty Member",
                    qualification = profileObj["qualification"]?.jsonPrimitive?.content ?: ""
                )
            } else {
                FacultyProfileDto(
                    id = "",
                    username = cleanUsername,
                    facultyId = cleanFacultyId,
                    fullName = cleanFullName,
                    department = cleanDepartment,
                    designation = "Faculty Member",
                    qualification = ""
                )
            }

            AuthResult.Success(
                data = profile,
                message = "Faculty account registered successfully! Welcome to GGC Faculty Portal."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Faculty Registration failed", e)
            val msg = e.localizedMessage ?: "Registration failed"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("This Faculty ID or Username is already registered.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    /**
     * Authenticates a Faculty member by Username or Faculty ID + Password.
     */
    suspend fun loginFaculty(
        usernameOrFacultyIdOrEmail: String,
        password: String
    ): AuthResult<FacultyProfileDto> {
        return try {
            val query = usernameOrFacultyIdOrEmail.trim()
            if (query.isBlank() || password.isBlank()) {
                return AuthResult.Error("Faculty ID / Username and Password are required.")
            }

            val loginParams = buildJsonObject {
                put("p_identifier", query)
                put("p_password", password)
            }

            val rpcResponse = client.postgrest.rpc("direct_login_faculty", loginParams)
            val bodyText = rpcResponse.data
            if (bodyText.isBlank()) {
                return AuthResult.Error("No response received from login server.")
            }

            val jsonObj = json.parseToJsonElement(bodyText).jsonObject
            val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
            if (!isSuccess) {
                val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                    ?: "Invalid Faculty ID, username, or password."
                return AuthResult.Error(errMsg)
            }

            val profileObj = jsonObj["profile"]?.jsonObject
                ?: return AuthResult.Error("Could not retrieve Faculty profile.")

            val profile = FacultyProfileDto(
                id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                facultyId = profileObj["faculty_id"]?.jsonPrimitive?.content ?: "",
                fullName = profileObj["full_name"]?.jsonPrimitive?.content ?: "",
                department = profileObj["department"]?.jsonPrimitive?.content ?: "",
                designation = profileObj["designation"]?.jsonPrimitive?.content ?: "Faculty Member",
                qualification = profileObj["qualification"]?.jsonPrimitive?.content ?: ""
            )

            AuthResult.Success(profile, "Faculty portal login successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Faculty Login failed", e)
            val msg = e.localizedMessage ?: "Login failed"
            if (msg.contains("Invalid login credentials", ignoreCase = true) || msg.contains("invalid", ignoreCase = true)) {
                AuthResult.Error("Invalid Faculty ID, username, or password. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    suspend fun logout() {
        // Direct session cleared locally
    }
}
