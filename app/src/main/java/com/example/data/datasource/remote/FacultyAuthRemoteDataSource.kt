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

            // 1. Check if RPC function is available in database
            try {
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
            } catch (rpcEx: Exception) {
                Log.d(TAG, "Faculty RPC check not available, falling back to direct table checks: ${rpcEx.message}")
            }

            // 2. Direct table queries fallback (RLS-friendly)
            // Check username in faculty profiles
            val usernameProfiles = client.from("faculty_profiles")
                .select {
                    filter {
                        ilike("username", cleanUsername)
                    }
                }.decodeList<FacultyProfileDto>()

            if (usernameProfiles.isNotEmpty()) {
                return AuthResult.Error("The username \"$username\" is already taken. Please choose another username.")
            }

            // Check faculty ID in faculty profiles
            val facultyProfiles = client.from("faculty_profiles")
                .select {
                    filter {
                        ilike("faculty_id", cleanFacultyId)
                    }
                }.decodeList<FacultyProfileDto>()

            if (facultyProfiles.isNotEmpty()) {
                return AuthResult.Error("Faculty ID \"$facultyId\" is already registered to an existing account.")
            }

            // Check official faculty registry
            val officialRecords = client.from("official_faculty")
                .select {
                    filter {
                        ilike("faculty_id", cleanFacultyId)
                    }
                }.decodeList<OfficialFacultyDto>()

            if (officialRecords.isEmpty()) {
                return AuthResult.Error("No official faculty record found for Faculty ID \"$facultyId\". Please verify with College Administration.")
            }

            val official = officialRecords.first()
            if (cleanDepartment.isNotBlank() && !official.department.equals(cleanDepartment, ignoreCase = true)) {
                return AuthResult.Error("Selected Department ($cleanDepartment) does not match official faculty department (${official.department}).")
            }

            if (official.isClaimed || official.claimedByUserId != null) {
                return AuthResult.Error("This official faculty identity (${official.fullName}, Faculty ID: $facultyId) has already been claimed by a registered account.")
            }

            AuthResult.Success(official)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Faculty eligibility", e)
            AuthResult.Error(e.localizedMessage ?: "Failed to verify faculty record against college registry.")
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

            // Step 1: Pre-verify eligibility
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

            // Step 3: Call atomic PostgreSQL stored procedure to link profile & lock official faculty record
            var registeredProfile: FacultyProfileDto? = null

            try {
                val rpcParams = buildJsonObject {
                    put("p_user_id", userId)
                    put("p_faculty_id", cleanFacultyId)
                    put("p_full_name", cleanFullName)
                    put("p_department", cleanDepartment)
                    put("p_username", cleanUsername)
                    cleanEmail?.let { put("p_institutional_email", it) }
                    cleanPhone?.let { put("p_phone", it) }
                }

                val rpcResponse = client.postgrest.rpc("register_faculty_account", rpcParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (!isSuccess) {
                        val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                            ?: "Failed to register faculty record."
                        return AuthResult.Error(errMsg)
                    }

                    jsonObj["profile"]?.let { profileElement ->
                        registeredProfile = json.decodeFromJsonElement<FacultyProfileDto>(profileElement)
                    }
                }
            } catch (rpcEx: Exception) {
                Log.w(TAG, "Faculty RPC register call fallback to direct table transaction: ${rpcEx.message}")
            }

            // Fallback if RPC was not defined or direct insert needed
            if (registeredProfile == null) {
                val officialRecords = client.from("official_faculty")
                    .select {
                        filter {
                            ilike("faculty_id", cleanFacultyId)
                        }
                    }.decodeList<OfficialFacultyDto>()

                val official = officialRecords.firstOrNull()
                    ?: return AuthResult.Error("Official faculty record not found in registry.")

                val officialRecordId = official.id

                val newProfile = FacultyProfileDto(
                    id = userId,
                    username = cleanUsername,
                    facultyId = cleanFacultyId,
                    fullName = if (cleanFullName.isNotBlank()) cleanFullName else official.fullName,
                    department = official.department,
                    designation = official.designation,
                    qualification = official.qualification,
                    institutionalEmail = official.institutionalEmail ?: cleanEmail,
                    phoneNumber = cleanPhone,
                    officialRecordId = officialRecordId
                )

                client.from("faculty_profiles").insert(newProfile)

                // Mark official faculty record claimed
                client.from("official_faculty").update(
                    buildJsonObject {
                        put("is_claimed", true)
                        put("claimed_by_user_id", userId)
                    }
                ) {
                    filter {
                        eq("id", officialRecordId)
                    }
                }

                registeredProfile = newProfile
            }

            AuthResult.Success(
                data = registeredProfile!!,
                message = "Faculty account verified & registered successfully! Welcome to GGC Faculty Portal."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Faculty Registration failed", e)
            val msg = e.localizedMessage ?: "Unknown registration error"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("Duplicate identity detected: Faculty ID, email, or username is already registered.")
            } else {
                AuthResult.Error(msg)
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
            }

            // Supabase Auth Login
            client.auth.signInWith(Email) {
                email = authEmail
                this.password = password
            }

            val currentAuthUser = client.auth.currentUserOrNull()
                ?: return AuthResult.Error("Could not verify Faculty credentials.")

            // Fetch faculty profile
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
                AuthResult.Error(msg)
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
