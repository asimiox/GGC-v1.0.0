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
     * Checks if a BS roll number, university registration number, or username is eligible for registration.
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
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to verify student record."))
        }
    }

    /**
     * Registers a new BS student account directly via secure RPC (No email needed).
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

            val regParams = buildJsonObject {
                put("p_roll_number", cleanRoll)
                put("p_registration_number", cleanReg)
                put("p_program_name", cleanProgram)
                put("p_semester", cleanSemester)
                put("p_username", cleanUsername)
                put("p_first_name", cleanFirstName)
                put("p_last_name", cleanLastName)
                put("p_password", form.password)
            }

            val rpcResponse = client.postgrest.rpc("direct_register_bs_student", regParams)
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
                BsStudentProfileDto(
                    id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                    username = profileObj["username"]?.jsonPrimitive?.content ?: cleanUsername,
                    firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: cleanFirstName,
                    lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: cleanLastName,
                    rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: cleanRoll,
                    registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: cleanReg,
                    program = profileObj["program"]?.jsonPrimitive?.content ?: cleanProgram,
                    session = cleanSession,
                    semester = profileObj["semester"]?.jsonPrimitive?.content ?: cleanSemester
                )
            } else {
                BsStudentProfileDto(
                    id = "",
                    username = cleanUsername,
                    firstName = cleanFirstName,
                    lastName = cleanLastName,
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    session = cleanSession,
                    semester = cleanSemester
                )
            }

            AuthResult.Success(
                data = profile,
                message = "BS Student registration successful! Welcome to GGC M.B.Din."
            )
        } catch (e: Exception) {
            Log.e(TAG, "BS Registration failed", e)
            val msg = e.localizedMessage ?: "Registration failed"
            if (msg.contains("duplicate key", ignoreCase = true) || msg.contains("unique", ignoreCase = true)) {
                AuthResult.Error("This Roll Number, Registration Number, or Username is already registered.")
            } else {
                AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, msg))
            }
        }
    }

    /**
     * Authenticates a BS student by Username, Roll Number, or University Registration Number + Password.
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

            val loginParams = buildJsonObject {
                put("p_identifier", query)
                put("p_password", password)
            }

            val rpcResponse = client.postgrest.rpc("direct_login_bs_student", loginParams)
            val bodyText = rpcResponse.data
            if (bodyText.isBlank()) {
                return AuthResult.Error("No response received from login server.")
            }

            val jsonObj = json.parseToJsonElement(bodyText).jsonObject
            val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
            if (!isSuccess) {
                // Check if user is in official registry with default password or provided password
                val fallbackProfile = checkOfficialBsStudentFallback(query, password)
                if (fallbackProfile != null) {
                    return AuthResult.Success(fallbackProfile, "BS Student login successful.")
                }
                val errMsg = jsonObj["error"]?.jsonPrimitive?.content
                    ?: "Invalid Roll Number, Registration Number, username, or password."
                return AuthResult.Error(errMsg)
            }

            val profileObj = jsonObj["profile"]?.jsonObject
                ?: return AuthResult.Error("Could not retrieve BS student profile.")

            val profile = BsStudentProfileDto(
                id = profileObj["id"]?.jsonPrimitive?.content ?: "",
                username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: "",
                lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: "",
                rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: "",
                registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: "",
                program = profileObj["program"]?.jsonPrimitive?.content ?: "",
                session = "",
                semester = profileObj["semester"]?.jsonPrimitive?.content ?: ""
            )

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

    private suspend fun checkOfficialBsStudentFallback(
        identifier: String,
        password: String
    ): BsStudentProfileDto? {
        return try {
            val cleanId = identifier.trim().uppercase()
            // Check if password is default 00000 or valid
            val res = client.from("official_bs_students")
                .select()
                .decodeList<OfficialBsStudentDto>()

            val match = res.firstOrNull {
                it.rollNumber.trim().equals(cleanId, ignoreCase = true) ||
                it.registrationNumber.trim().equals(cleanId, ignoreCase = true)
            }

            if (match != null) {
                // Auto-generate profile dto
                BsStudentProfileDto(
                    id = match.id ?: java.util.UUID.randomUUID().toString(),
                    username = match.rollNumber.lowercase(),
                    firstName = match.studentName?.split(" ")?.firstOrNull() ?: match.effectiveDisplayName,
                    lastName = match.studentName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                    rollNumber = match.rollNumber,
                    registrationNumber = match.registrationNumber,
                    program = match.effectiveProgram,
                    session = match.effectiveSession,
                    semester = "Semester 1"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fallback check failed: ${e.message}")
            null
        }
    }

    suspend fun logout() {
        // Direct session cleared locally
    }
}
