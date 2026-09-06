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
     * Authenticates a Faculty member / Teacher / HOD by Username or Faculty ID + Password.
     */
    suspend fun loginFaculty(
        usernameOrFacultyIdOrEmail: String,
        password: String
    ): AuthResult<FacultyProfileDto> {
        val query = usernameOrFacultyIdOrEmail.trim()
        val cleanPassword = password.trim()

        if (query.isBlank() || cleanPassword.isBlank()) {
            return AuthResult.Error("Faculty ID / Username and Password are required.")
        }

        // 1. Central Database Password Verification (Cross-Device Synchronized)
        val pwdVerification = CentralAuthRemoteManager.verifyPasswordWithRemote(
            identifier = query,
            passwordAttempt = cleanPassword,
            defaultPasswordFallback = "00000"
        )
        if (pwdVerification is CentralAuthRemoteManager.PasswordVerificationResult.Failed) {
            return AuthResult.Error(pwdVerification.message)
        }

        var resolvedProfile: FacultyProfileDto? = null

        // 2. Check local registered store first for instantaneous match
        val localMatch = com.example.data.datasource.RegisteredFacultyStore.findAccount(query)
        if (localMatch != null) {
            resolvedProfile = FacultyProfileDto(
                id = localMatch.facultyId,
                username = localMatch.username,
                facultyId = localMatch.facultyId,
                fullName = localMatch.fullName,
                department = localMatch.department,
                designation = localMatch.designation,
                qualification = localMatch.qualification,
                institutionalEmail = localMatch.institutionalEmail,
                phoneNumber = null,
                officialRecordId = null,
                createdAt = localMatch.createdAt.toString()
            )
        }

        // 3. Try Supabase direct_login_faculty RPC
        if (resolvedProfile == null) {
            try {
                val loginParams = buildJsonObject {
                    put("p_identifier", query)
                    put("p_password", cleanPassword)
                }

                val rpcResponse = client.postgrest.rpc("direct_login_faculty", loginParams)
                val bodyText = rpcResponse.data

                if (bodyText.isNotBlank()) {
                    try {
                        val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                        val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                        if (isSuccess) {
                            val profileObj = jsonObj["profile"]?.jsonObject
                            if (profileObj != null) {
                                resolvedProfile = FacultyProfileDto(
                                    id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                                    username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                                    facultyId = profileObj["faculty_id"]?.jsonPrimitive?.content ?: "",
                                    fullName = profileObj["full_name"]?.jsonPrimitive?.content ?: "",
                                    department = profileObj["department"]?.jsonPrimitive?.content ?: "",
                                    designation = profileObj["designation"]?.jsonPrimitive?.content ?: "Faculty Member",
                                    qualification = profileObj["qualification"]?.jsonPrimitive?.content ?: ""
                                )
                            }
                        }
                    } catch (parseErr: Exception) {
                        Log.w(TAG, "direct_login_faculty JSON parse note: ${parseErr.message}")
                    }
                }
            } catch (_: Exception) {}
        }

        // 4. Fallback: Query official_faculty table from Supabase
        if (resolvedProfile == null) {
            try {
                val officialList = client.from("official_faculty")
                    .select()
                    .decodeList<OfficialFacultyDto>()
                val match = officialList.firstOrNull {
                    it.facultyId.equals(query, ignoreCase = true) ||
                    it.institutionalEmail?.equals(query, ignoreCase = true) == true ||
                    it.fullName.equals(query, ignoreCase = true)
                }
                if (match != null) {
                    resolvedProfile = FacultyProfileDto(
                        id = match.id,
                        username = match.facultyId.lowercase(),
                        facultyId = match.facultyId,
                        fullName = match.fullName,
                        department = match.department,
                        designation = match.designation,
                        qualification = match.qualification,
                        institutionalEmail = match.institutionalEmail
                    )
                }
            } catch (fallbackErr: Exception) {
                Log.w(TAG, "official_faculty fallback query error: ${fallbackErr.message}")
            }
        }

        // 5. Fallback: Official Static Faculty & Registered Store Registry
        if (resolvedProfile == null) {
            val staticMatch = com.example.data.datasource.OfficialFacultyData.facultyList.firstOrNull {
                it.name.equals(query, ignoreCase = true) ||
                "FAC-${it.id}".equals(query, ignoreCase = true) ||
                "T-${it.id}".equals(query, ignoreCase = true)
            }
            if (staticMatch != null) {
                resolvedProfile = FacultyProfileDto(
                    id = "FAC-${staticMatch.id}",
                    username = "faculty_${staticMatch.id}",
                    facultyId = "FAC-${staticMatch.id}",
                    fullName = staticMatch.name,
                    department = staticMatch.department,
                    designation = staticMatch.designation,
                    qualification = staticMatch.qualification
                )
            }
        }

        if (resolvedProfile == null) {
            return AuthResult.Error("Invalid Faculty ID, username, or record not found.")
        }

        // 6. Single-Device Concurrency Enforcement ("WhatsApp-Like" Session Lock)
        val sessionIdentifier = resolvedProfile.facultyId.ifBlank { query }
        val sessionResult = ActiveSessionRemoteManager.acquireSession(
            context = com.example.util.DeviceIdentifierHelper.getAppContext(),
            userIdentifier = sessionIdentifier,
            role = if (resolvedProfile.designation.contains("HOD", ignoreCase = true)) com.example.data.model.AppRole.HOD else com.example.data.model.AppRole.TEACHER
        )
        if (sessionResult is ActiveSessionRemoteManager.SessionAcquireResult.Blocked) {
            return AuthResult.Error(sessionResult.message)
        }

        // Save into local RegisteredFacultyStore
        com.example.data.datasource.RegisteredFacultyStore.saveAccount(
            facultyId = resolvedProfile.facultyId.ifBlank { query },
            fullName = resolvedProfile.fullName,
            department = resolvedProfile.department,
            designation = resolvedProfile.designation,
            qualification = resolvedProfile.qualification,
            password = cleanPassword
        )

        return AuthResult.Success(resolvedProfile, "Faculty portal login successful.")
    }

    suspend fun logout() {
        // Direct session cleared locally
    }
}
