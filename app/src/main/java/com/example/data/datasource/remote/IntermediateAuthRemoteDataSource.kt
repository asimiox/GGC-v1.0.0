package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AuthResult
import com.example.data.model.IntermediateRegistrationForm
import com.example.data.model.IntermediateStudentProfileDto
import com.example.data.model.OfficialIntermediateStudentDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class IntermediateAuthRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "InterAuthRemote"

    private fun usernameToEmail(username: String): String {
        val clean = username.trim().lowercase().filter { it.isLetterOrDigit() || it == '.' || it == '_' }
        return "$clean@student.ggcmbdin.edu.pk"
    }

    /**
     * Checks if a roll number, registration number, or username is already claimed/used,
     * and verifies eligibility against the official students table.
     */
    suspend fun checkEligibility(
        rollNumber: String,
        registrationNumber: String,
        program: String,
        username: String
    ): AuthResult<Unit> {
        return try {
            val cleanRoll = rollNumber.trim().uppercase()
            val cleanReg = registrationNumber.trim().uppercase()
            val cleanUsername = username.trim().lowercase()
            val cleanProgram = program.trim()

            // 1. Check if RPC function is available in database
            try {
                val rpcParams = buildJsonObject {
                    put("p_roll_number", cleanRoll)
                    put("p_registration_number", cleanReg)
                    put("p_program", cleanProgram)
                    put("p_username", cleanUsername)
                }
                val rpcResponse = client.postgrest.rpc("check_intermediate_student_eligibility", rpcParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isEligible = jsonObj["eligible"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (!isEligible) {
                        val errorMsg = jsonObj["error"]?.jsonPrimitive?.content
                            ?: "Student record is not eligible for registration."
                        return AuthResult.Error(errorMsg)
                    }
                    return AuthResult.Success(Unit)
                }
            } catch (rpcEx: Exception) {
                Log.d(TAG, "RPC check not available, falling back to direct table checks: ${rpcEx.message}")
            }

            // 2. Direct table queries fallback (RLS-friendly)
            // Check username in profiles
            val usernameProfiles = client.from("intermediate_student_profiles")
                .select {
                    filter {
                        ilike("username", cleanUsername)
                    }
                }.decodeList<IntermediateStudentProfileDto>()

            if (usernameProfiles.isNotEmpty()) {
                return AuthResult.Error("The username \"$username\" is already taken. Please choose another.")
            }

            // Check roll number in profiles
            val rollProfiles = client.from("intermediate_student_profiles")
                .select {
                    filter {
                        ilike("roll_number", cleanRoll)
                    }
                }.decodeList<IntermediateStudentProfileDto>()

            if (rollProfiles.isNotEmpty()) {
                return AuthResult.Error("College Roll Number \"$rollNumber\" is already registered to an account.")
            }

            // Check registration number in profiles
            val regProfiles = client.from("intermediate_student_profiles")
                .select {
                    filter {
                        ilike("registration_number", cleanReg)
                    }
                }.decodeList<IntermediateStudentProfileDto>()

            if (regProfiles.isNotEmpty()) {
                return AuthResult.Error("Registration Number \"$registrationNumber\" is already registered to an account.")
            }

            // Check official intermediate students registry
            val officialRecords = client.from("official_intermediate_students")
                .select {
                    filter {
                        ilike("roll_number", cleanRoll)
                        ilike("registration_number", cleanReg)
                    }
                }.decodeList<OfficialIntermediateStudentDto>()

            if (officialRecords.isEmpty()) {
                return AuthResult.Error("No official student record found for Roll No: $rollNumber and Reg No: $registrationNumber. Please verify your details with College Admission Office.")
            }

            val official = officialRecords.first()
            if (!official.program.equals(cleanProgram, ignoreCase = true)) {
                return AuthResult.Error("Selected Program ($cleanProgram) does not match official enrolled program (${official.program}) for this Roll Number.")
            }

            if (official.isClaimed || official.claimedByUserId != null) {
                return AuthResult.Error("This official student identity (Roll No: $rollNumber) has already been claimed by a registered account.")
            }

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking eligibility", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify student record against college registry."))
        }
    }

    /**
     * Registers a new Intermediate student account via Supabase Auth and atomic DB verification.
     */
    suspend fun registerIntermediateStudent(
        form: IntermediateRegistrationForm
    ): AuthResult<IntermediateStudentProfileDto> {
        return try {
            val cleanRoll = form.rollNumber.trim().uppercase()
            val cleanReg = form.registrationNumber.trim().uppercase()
            val cleanUsername = form.username.trim().lowercase()
            val cleanProgram = form.program.trim()
            val cleanFirstName = form.firstName.trim()
            val cleanLastName = form.lastName.trim()
            val authEmail = usernameToEmail(cleanUsername)

            // Step 1: Pre-verify eligibility
            val check = checkEligibility(cleanRoll, cleanReg, cleanProgram, cleanUsername)
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
                Log.e(TAG, "Supabase Auth signUp failed", authEx)
                val msg = authEx.localizedMessage ?: ""
                return if (msg.contains("already registered", ignoreCase = true) || msg.contains("User already exists", ignoreCase = true)) {
                    AuthResult.Error("Username \"${form.username}\" is already registered.")
                } else {
                    AuthResult.Error("Authentication service error: $msg")
                }
            }

            val currentAuthUser = client.auth.currentUserOrNull()
            val userId = currentAuthUser?.id
                ?: return AuthResult.Error("Could not retrieve created user ID from authentication session.")

            // Step 3: Call atomic PostgreSQL stored procedure to link profile & lock official record
            var registeredProfile: IntermediateStudentProfileDto? = null

            try {
                val rpcParams = buildJsonObject {
                    put("p_user_id", userId)
                    put("p_first_name", cleanFirstName)
                    put("p_last_name", cleanLastName)
                    put("p_roll_number", cleanRoll)
                    put("p_registration_number", cleanReg)
                    put("p_program", cleanProgram)
                    put("p_username", cleanUsername)
                }

                val rpcResponse = client.postgrest.rpc("register_intermediate_student_account", rpcParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (!isSuccess) {
                        val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                            ?: "Failed to register student record."
                        return AuthResult.Error(errMsg)
                    }

                    jsonObj["profile"]?.let { profileElement ->
                        registeredProfile = json.decodeFromJsonElement<IntermediateStudentProfileDto>(profileElement)
                    }
                }
            } catch (rpcEx: Exception) {
                Log.w(TAG, "RPC register call fallback to direct table transaction: ${rpcEx.message}")
            }

            // If RPC was bypassed or direct insert needed
            if (registeredProfile == null) {
                val officialRecords = client.from("official_intermediate_students")
                    .select {
                        filter {
                            ilike("roll_number", cleanRoll)
                            ilike("registration_number", cleanReg)
                        }
                    }.decodeList<OfficialIntermediateStudentDto>()

                val officialRecordId = officialRecords.firstOrNull()?.id
                    ?: return AuthResult.Error("Official student record not found in registry.")

                val newProfile = IntermediateStudentProfileDto(
                    id = userId,
                    username = cleanUsername,
                    firstName = cleanFirstName,
                    lastName = cleanLastName,
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    officialRecordId = officialRecordId
                )

                client.from("intermediate_student_profiles").insert(newProfile)

                // Mark official record claimed
                client.from("official_intermediate_students").update(
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
                message = "Registration successful! Welcome to GGC M.B.Din Official Portal."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            val msg = e.localizedMessage ?: "Unknown registration error"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("Duplicate identity detected: Roll number, registration number, or username is already registered.")
            } else {
                AuthResult.Error(msg)
            }
        }
    }

    /**
     * Authenticates an Intermediate student by Username or College Roll Number.
     */
    suspend fun loginIntermediateStudent(
        usernameOrRoll: String,
        password: String
    ): AuthResult<IntermediateStudentProfileDto> {
        return try {
            val query = usernameOrRoll.trim()
            if (query.isBlank() || password.isBlank()) {
                return AuthResult.Error("Username/Roll Number and Password are required.")
            }

            var cleanUsername = query.lowercase()

            // Check if user entered a Roll Number instead of username
            if (!query.contains("@")) {
                val profileByRoll = client.from("intermediate_student_profiles")
                    .select {
                        filter {
                            or {
                                ilike("roll_number", query.uppercase())
                                ilike("username", query.lowercase())
                            }
                        }
                    }.decodeList<IntermediateStudentProfileDto>()

                if (profileByRoll.isNotEmpty()) {
                    cleanUsername = profileByRoll.first().username
                }
            }

            val authEmail = usernameToEmail(cleanUsername)

            // Supabase Auth Login
            client.auth.signInWith(Email) {
                email = authEmail
                this.password = password
            }

            val currentAuthUser = client.auth.currentUserOrNull()
                ?: return AuthResult.Error("Could not verify student login credentials.")

            // Fetch student profile
            val profiles = client.from("intermediate_student_profiles")
                .select {
                    filter {
                        eq("id", currentAuthUser.id)
                    }
                }.decodeList<IntermediateStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return AuthResult.Error("Student profile data could not be found.")

            AuthResult.Success(profile, "Login successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val msg = e.localizedMessage ?: "Login failed"
            if (msg.contains("Invalid login credentials", ignoreCase = true) || msg.contains("invalid", ignoreCase = true)) {
                AuthResult.Error("Invalid username, roll number, or password. Please try again.")
            } else {
                AuthResult.Error(msg)
            }
        }
    }

    suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign out error", e)
        }
    }
}
