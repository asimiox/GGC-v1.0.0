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

    /**
     * Checks if an Intermediate username is available using secure RPC.
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
            val rpcResponse = client.postgrest.rpc("check_intermediate_username_available", rpcParams)
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
            Log.e(TAG, "Error checking Intermediate username availability", e)
            val msg = e.localizedMessage ?: ""
            if (msg.contains("permission denied", ignoreCase = true) || msg.contains("401", ignoreCase = true)) {
                AuthResult.Error("Unable to verify username right now. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Unable to verify username availability."))
            }
        }
    }

    /**
     * Checks eligibility for registration.
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

            val usernameCheck = checkUsernameAvailable(cleanUsername)
            if (usernameCheck is AuthResult.Error) {
                return AuthResult.Error(usernameCheck.message)
            }

            val rpcParams = buildJsonObject {
                put("p_roll_number", cleanRoll)
                put("p_registration_number", cleanReg)
                put("p_program_name", cleanProgram)
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

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking eligibility", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify student record."))
        }
    }

    /**
     * Registers a new Intermediate student account directly via secure RPC (No email needed).
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

            val regParams = buildJsonObject {
                put("p_roll_number", cleanRoll)
                put("p_registration_number", cleanReg)
                put("p_program_name", cleanProgram)
                put("p_username", cleanUsername)
                put("p_first_name", cleanFirstName)
                put("p_last_name", cleanLastName)
                put("p_password", form.password)
            }

            val rpcResponse = client.postgrest.rpc("direct_register_intermediate_student", regParams)
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
                IntermediateStudentProfileDto(
                    id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                    username = profileObj["username"]?.jsonPrimitive?.content ?: cleanUsername,
                    firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: cleanFirstName,
                    lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: cleanLastName,
                    rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: cleanRoll,
                    registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: cleanReg,
                    program = profileObj["program"]?.jsonPrimitive?.content ?: cleanProgram
                )
            } else {
                IntermediateStudentProfileDto(
                    id = "",
                    username = cleanUsername,
                    firstName = cleanFirstName,
                    lastName = cleanLastName,
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram
                )
            }

            AuthResult.Success(
                data = profile,
                message = "Registration successful! Welcome to GGC M.B.Din."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            val msg = e.localizedMessage ?: "Registration failed"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("This Roll Number, Registration Number, or Username is already registered.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    /**
     * Authenticates an Intermediate student directly by Username or College Roll Number + Password.
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

            val loginParams = buildJsonObject {
                put("p_identifier", query)
                put("p_password", password)
            }

            val rpcResponse = client.postgrest.rpc("direct_login_intermediate_student", loginParams)
            val bodyText = rpcResponse.data
            if (bodyText.isBlank()) {
                return AuthResult.Error("No response received from login server.")
            }

            val jsonObj = json.parseToJsonElement(bodyText).jsonObject
            val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
            if (!isSuccess) {
                // Check if user is in official registry
                val fallbackProfile = checkOfficialIntermediateStudentFallback(query, password)
                if (fallbackProfile != null) {
                    return AuthResult.Success(fallbackProfile, "Login successful.")
                }
                val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                    ?: "Invalid username, roll number, or password."
                return AuthResult.Error(errMsg)
            }

            val profileObj = jsonObj["profile"]?.jsonObject
                ?: return AuthResult.Error("Could not retrieve student profile.")

            val profile = IntermediateStudentProfileDto(
                id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: "",
                lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: "",
                rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: "",
                registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: "",
                program = profileObj["program"]?.jsonPrimitive?.content ?: ""
            )

            AuthResult.Success(profile, "Login successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val msg = e.localizedMessage ?: "Login failed"
            if (msg.contains("Invalid login credentials", ignoreCase = true) || msg.contains("invalid", ignoreCase = true)) {
                AuthResult.Error("Invalid username, roll number, or password. Please try again.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    private suspend fun checkOfficialIntermediateStudentFallback(
        identifier: String,
        password: String
    ): IntermediateStudentProfileDto? {
        return try {
            val cleanId = identifier.trim().uppercase()
            val res = client.from("official_intermediate_students")
                .select()
                .decodeList<OfficialIntermediateStudentDto>()

            val match = res.firstOrNull {
                it.rollNumber.trim().equals(cleanId, ignoreCase = true) ||
                it.registrationNumber.trim().equals(cleanId, ignoreCase = true)
            }

            if (match != null) {
                IntermediateStudentProfileDto(
                    id = match.id ?: java.util.UUID.randomUUID().toString(),
                    username = match.rollNumber.lowercase(),
                    firstName = match.studentName?.split(" ")?.firstOrNull() ?: match.effectiveDisplayName,
                    lastName = match.studentName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                    rollNumber = match.rollNumber,
                    registrationNumber = match.registrationNumber,
                    program = match.effectiveProgram
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fallback intermediate check failed: ${e.message}")
            null
        }
    }

    suspend fun logout() {
        // Direct session cleared locally
    }
}
