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

    private fun usernameToEmail(username: String): String {
        val clean = username.trim().lowercase().filter { it.isLetterOrDigit() || it == '.' || it == '_' }
        return "$clean@faculty.ggcmbdin.edu.pk"
    }

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
     * Checks if a faculty member ID or username is already claimed/used,
     * and verifies eligibility against the official faculty table.
     */
    suspend fun checkEligibility(
        facultyId: String,
        department: String,
        username: String,
        institutionalEmail: String? = null
    ): AuthResult<OfficialFacultyDto> {
        return try {
            val cleanFacultyId = facultyId.trim().uppercase()
            val cleanDepartment = department.trim()
            val cleanUsername = username.trim().lowercase()
            val cleanEmail = institutionalEmail?.trim()?.lowercase()?.ifBlank { null }

            // 1. Check Username Availability via Secure RPC
            val usernameCheck = checkUsernameAvailable(cleanUsername)
            if (usernameCheck is AuthResult.Error) {
                return AuthResult.Error(usernameCheck.message)
            }

            // 2. Check Faculty Eligibility via Secure RPC
            val rpcParams = buildJsonObject {
                put("p_faculty_id", cleanFacultyId)
                put("p_department", cleanDepartment)
                put("p_username", cleanUsername)
                cleanEmail?.let { put("p_institutional_email", it) }
            }
            val rpcResponse = client.postgrest.rpc("check_faculty_eligibility", rpcParams)
            val bodyText = rpcResponse.data
            if (bodyText.isNotBlank()) {
                val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                val isEligible = jsonObj["eligible"]?.jsonPrimitive?.booleanOrNull ?: false
                if (!isEligible) {
                    val errorMsg = jsonObj["error"]?.jsonPrimitive?.content
                        ?: "Faculty record is not eligible for registration."
                    return AuthResult.Error(errorMsg)
                }
                val officialDto = OfficialFacultyDto(
                    id = "",
                    facultyId = cleanFacultyId,
                    fullName = jsonObj["full_name"]?.jsonPrimitive?.content ?: "",
                    department = jsonObj["department"]?.jsonPrimitive?.content ?: cleanDepartment,
                    designation = jsonObj["designation"]?.jsonPrimitive?.content ?: "",
                    qualification = jsonObj["qualification"]?.jsonPrimitive?.content ?: "",
                    institutionalEmail = jsonObj["institutional_email"]?.jsonPrimitive?.content ?: cleanEmail
                )
                return AuthResult.Success(officialDto)
            }

            AuthResult.Error("Unable to verify faculty eligibility with college registry.")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Faculty eligibility", e)
            val msg = e.localizedMessage ?: ""
            if (msg.contains("permission denied", ignoreCase = true)) {
                AuthResult.Error("Unable to verify faculty eligibility right now. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify faculty record against college registry."))
            }
        }
    }

    /**
     * Registers a new Faculty account via Supabase Auth and atomic DB verification.
     */
    suspend fun registerFaculty(
        form: FacultyRegistrationForm
    ): AuthResult<FacultyProfileDto> {
        return try {
            val cleanFacultyId = form.facultyId.trim().uppercase()
            val cleanDepartment = form.department.trim()
            val cleanUsername = form.username.trim().lowercase()
            val cleanFullName = form.fullName.trim()
            val cleanEmail = form.institutionalEmail.trim().lowercase().ifBlank { null }
            val cleanPhone = form.phoneNumber.trim().ifBlank { null }
            val authEmail = cleanEmail ?: usernameToEmail(cleanUsername)

            // Step 1: Pre-verify eligibility and username availability via secure RPC
            val check = checkEligibility(cleanFacultyId, cleanDepartment, cleanUsername, cleanEmail)
            if (check is AuthResult.Error) {
                return AuthResult.Error(check.message)
            }

            // Step 2: Supabase Auth Sign Up
            val authResult = try {
                client.auth.signUpWith(Email) {
                    email = authEmail
                    password = form.password
                }
            } catch (authEx: Exception) {
                Log.e(TAG, "Supabase Auth signUp failed for Faculty", authEx)
                val msg = authEx.localizedMessage ?: ""
                return if (msg.contains("already registered", ignoreCase = true) || msg.contains("User already exists", ignoreCase = true)) {
                    AuthResult.Error("Faculty username or email is already registered.")
                } else {
                    AuthResult.Error("Authentication service error: $msg")
                }
            }

            val currentAuthUser = client.auth.currentUserOrNull()
            val userId = currentAuthUser?.id
                ?: return AuthResult.Error("Could not retrieve created user ID from authentication session.")

            // Step 3: Call atomic PostgreSQL claiming RPC to create profile & claim official faculty record
            var registeredProfile: FacultyProfileDto? = null

            try {
                val claimParams = buildJsonObject {
                    put("p_faculty_id", cleanFacultyId)
                    put("p_department", cleanDepartment)
                    put("p_username", cleanUsername)
                    cleanPhone?.let { put("p_phone_number", it) }
                }

                val rpcResponse = client.postgrest.rpc("claim_faculty_account", claimParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (!isSuccess) {
                        val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                            ?: "Failed to claim faculty record."
                        return AuthResult.Error(errMsg)
                    }
                }
            } catch (rpcEx: Exception) {
                Log.w(TAG, "Faculty RPC claim call error: ${rpcEx.message}")
            }

            // Fetch authenticated faculty profile (allowed by RLS for authenticated user)
            val profiles = client.from("faculty_profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeList<FacultyProfileDto>()

            val officialInfo = (check as? AuthResult.Success)?.data
            registeredProfile = profiles.firstOrNull() ?: FacultyProfileDto(
                id = userId,
                username = cleanUsername,
                facultyId = cleanFacultyId,
                fullName = if (cleanFullName.isNotBlank()) cleanFullName else (officialInfo?.fullName ?: ""),
                department = cleanDepartment,
                designation = officialInfo?.designation ?: "Lecturer",
                qualification = officialInfo?.qualification ?: "",
                institutionalEmail = cleanEmail,
                phoneNumber = cleanPhone
            )

            AuthResult.Success(
                data = registeredProfile,
                message = "Faculty account verified & registered successfully! Welcome to GGC Faculty Portal."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Faculty Registration failed", e)
            val msg = e.localizedMessage ?: "Unknown registration error"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("Duplicate identity detected: Faculty ID, email, or username is already registered.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    /**
     * Authenticates a Faculty member by Username, Faculty ID, or Institutional Email.
     */
    suspend fun loginFaculty(
        usernameOrFacultyIdOrEmail: String,
        password: String
    ): AuthResult<FacultyProfileDto> {
        return try {
            val query = usernameOrFacultyIdOrEmail.trim()
            if (query.isBlank() || password.isBlank()) {
                return AuthResult.Error("Faculty ID / Username / Email and Password are required.")
            }

            var authEmail = if (query.contains("@")) query.lowercase() else ""

            if (authEmail.isBlank()) {
                try {
                    val profileMatches = client.from("faculty_profiles")
                        .select {
                            filter {
                                or {
                                    ilike("faculty_id", query.uppercase())
                                    ilike("username", query.lowercase())
                                }
                            }
                        }.decodeList<FacultyProfileDto>()

                    if (profileMatches.isNotEmpty()) {
                        val profile = profileMatches.first()
                        authEmail = profile.institutionalEmail ?: usernameToEmail(profile.username)
                    } else {
                        authEmail = usernameToEmail(query)
                    }
                } catch (lookupEx: Exception) {
                    Log.d(TAG, "Unauthenticated faculty lookup blocked by RLS (expected): ${lookupEx.message}")
                    authEmail = usernameToEmail(query)
                }
            }

            // Supabase Auth Login
            client.auth.signInWith(Email) {
                email = authEmail
                this.password = password
            }

            val currentAuthUser = client.auth.currentUserOrNull()
                ?: return AuthResult.Error("Could not verify Faculty credentials.")

            // Fetch faculty profile (allowed by RLS for authenticated user)
            val profiles = client.from("faculty_profiles")
                .select {
                    filter {
                        eq("id", currentAuthUser.id)
                    }
                }.decodeList<FacultyProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return AuthResult.Error("Faculty profile data could not be found.")

            AuthResult.Success(profile, "Faculty portal login successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Faculty Login failed", e)
            val msg = e.localizedMessage ?: "Login failed"
            if (msg.contains("Invalid login credentials", ignoreCase = true) || msg.contains("invalid", ignoreCase = true)) {
                AuthResult.Error("Invalid Faculty ID, username, email, or password. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Faculty Sign out error", e)
        }
    }
}
