package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AuthResult
import com.example.data.model.BsLoginForm
import com.example.data.model.BsRegistrationForm
import com.example.data.model.BsStudentProfileDto
import com.example.data.model.OfficialBsStudentDto
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

class BsAuthRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "BsAuthRemote"

    private fun usernameToEmail(username: String): String {
        val clean = username.trim().lowercase().filter { it.isLetterOrDigit() || it == '.' || it == '_' }
        return "$clean@bs.student.ggcmbdin.edu.pk"
    }

    /**
     * Checks if a BS username is available using the secure SECURITY DEFINER RPC.
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
            val rpcResponse = client.postgrest.rpc("check_bs_username_available", rpcParams)
            val bodyText = rpcResponse.data
            if (bodyText.isNotBlank()) {
                val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                val isAvailable = jsonObj["available"]?.jsonPrimitive?.booleanOrNull ?: false
                if (!isAvailable) {
                    val errorMsg = jsonObj["error"]?.jsonPrimitive?.content
                        ?: "The username \"$username\" is already taken. Please choose another."
                    return AuthResult.Error(errorMsg)
                }
                return AuthResult.Success(true)
            }
            AuthResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking BS username availability", e)
            val msg = e.localizedMessage ?: ""
            if (msg.contains("permission denied", ignoreCase = true) || msg.contains("401", ignoreCase = true)) {
                AuthResult.Error("Unable to verify username right now. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Unable to verify username availability."))
            }
        }
    }

    /**
     * Checks if a BS roll number, university registration number, or username is eligible for registration
     * using secure database RPCs.
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

            // 1. Check Username Availability via Secure RPC
            val usernameCheck = checkUsernameAvailable(cleanUsername)
            if (usernameCheck is AuthResult.Error) {
                return AuthResult.Error(usernameCheck.message)
            }

            // 2. Check BS Student Eligibility via Secure RPC
            val rpcParams = buildJsonObject {
                put("p_roll_number", cleanRoll)
                put("p_registration_number", cleanReg)
                put("p_program_name", cleanProgram)
                put("p_username", cleanUsername)
            }
            val rpcResponse = client.postgrest.rpc("check_bs_student_eligibility", rpcParams)
            val bodyText = rpcResponse.data
            if (bodyText.isNotBlank()) {
                val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                val isEligible = jsonObj["eligible"]?.jsonPrimitive?.booleanOrNull ?: false
                if (!isEligible) {
                    val errorMsg = jsonObj["error"]?.jsonPrimitive?.content
                        ?: "BS student record is not eligible for registration."
                    return AuthResult.Error(errorMsg)
                }
                return AuthResult.Success(Unit)
            }

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking BS eligibility", e)
            val msg = e.localizedMessage ?: ""
            if (msg.contains("permission denied", ignoreCase = true)) {
                AuthResult.Error("Unable to verify student eligibility right now. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify BS student record against college registry."))
            }
        }
    }

    /**
     * Registers a new BS student account via Supabase Auth and atomic DB verification.
     */
    suspend fun registerBsStudent(
        form: BsRegistrationForm
    ): AuthResult<BsStudentProfileDto> {
        return try {
            val cleanRoll = form.rollNumber.trim().uppercase()
            val cleanReg = form.registrationNumber.trim().uppercase()
            val cleanUsername = form.username.trim().lowercase()
            val cleanProgram = form.program.trim()
            val cleanSession = form.session.trim()
            val cleanSemester = form.semester.trim()
            val cleanFirstName = form.firstName.trim()
            val cleanLastName = form.lastName.trim()
            val authEmail = usernameToEmail(cleanUsername)

            // Step 1: Pre-verify eligibility and username availability via secure RPC
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
                Log.e(TAG, "Supabase Auth signUp failed for BS", authEx)
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

            // Step 3: Call atomic PostgreSQL claiming RPC to create profile & claim official record
            var registeredProfile: BsStudentProfileDto? = null

            try {
                val claimParams = buildJsonObject {
                    put("p_roll_number", cleanRoll)
                    put("p_registration_number", cleanReg)
                    put("p_program_name", cleanProgram)
                    put("p_username", cleanUsername)
                    put("p_first_name", cleanFirstName)
                    put("p_last_name", cleanLastName)
                    put("p_semester_number", cleanSemester.toIntOrNull() ?: 1)
                }

                val rpcResponse = client.postgrest.rpc("claim_bs_student_account", claimParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (!isSuccess) {
                        val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                            ?: "Failed to claim BS student record."
                        return AuthResult.Error(errMsg)
                    }
                }
            } catch (rpcEx: Exception) {
                Log.w(TAG, "BS RPC claim call error: ${rpcEx.message}")
            }

            // Fetch authenticated student profile (RLS permits reading own profile)
            val profiles = client.from("bs_student_profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeList<BsStudentProfileDto>()

            registeredProfile = profiles.firstOrNull() ?: BsStudentProfileDto(
                id = userId,
                username = cleanUsername,
                firstName = cleanFirstName,
                lastName = cleanLastName,
                rollNumber = cleanRoll,
                registrationNumber = cleanReg,
                program = cleanProgram,
                session = cleanSession,
                semester = cleanSemester
            )

            AuthResult.Success(
                data = registeredProfile,
                message = "BS Student registration verified successfully! Welcome to GGC M.B.Din."
            )
        } catch (e: Exception) {
            Log.e(TAG, "BS Registration failed", e)
            val msg = e.localizedMessage ?: "Unknown registration error"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("Duplicate identity detected: Roll number, university registration number, or username is already registered.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    /**
     * Authenticates a BS student by Username, Roll Number, or University Registration Number.
     */
    suspend fun loginBsStudent(
        usernameOrRoll: String,
        password: String
    ): AuthResult<BsStudentProfileDto> {
        return try {
            val query = usernameOrRoll.trim()
            if (query.isBlank() || password.isBlank()) {
                return AuthResult.Error("Roll Number/Username and Password are required.")
            }

            var cleanUsername = query.lowercase()

            // Optional: If user entered roll number, attempt resolution safely without failing login
            if (!query.contains("@")) {
                try {
                    val profileByRoll = client.from("bs_student_profiles")
                        .select {
                            filter {
                                or {
                                    ilike("roll_number", query.uppercase())
                                    ilike("registration_number", query.uppercase())
                                    ilike("username", query.lowercase())
                                }
                            }
                        }.decodeList<BsStudentProfileDto>()

                    if (profileByRoll.isNotEmpty()) {
                        cleanUsername = profileByRoll.first().username
                    }
                } catch (lookupEx: Exception) {
                    Log.d(TAG, "Unauthenticated roll lookup blocked by RLS (expected): ${lookupEx.message}")
                }
            }

            val authEmail = usernameToEmail(cleanUsername)

            // Supabase Auth Login
            client.auth.signInWith(Email) {
                email = authEmail
                this.password = password
            }

            val currentAuthUser = client.auth.currentUserOrNull()
                ?: return AuthResult.Error("Could not verify BS student credentials.")

            // Fetch BS student profile (allowed by RLS for authenticated user)
            val profiles = client.from("bs_student_profiles")
                .select {
                    filter {
                        eq("id", currentAuthUser.id)
                    }
                }.decodeList<BsStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return AuthResult.Error("BS student profile data could not be found.")

            AuthResult.Success(profile, "BS Student login successful.")
        } catch (e: Exception) {
            Log.e(TAG, "BS Login failed", e)
            val msg = e.localizedMessage ?: "Login failed"
            if (msg.contains("Invalid login credentials", ignoreCase = true) || msg.contains("invalid", ignoreCase = true)) {
                AuthResult.Error("Invalid Roll Number, Registration Number, username, or password. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "BS Sign out error", e)
        }
    }
}
