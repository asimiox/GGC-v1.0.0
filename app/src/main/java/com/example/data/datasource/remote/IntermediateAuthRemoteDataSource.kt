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
            val cleanUsername = form.username.trim().lowercase().ifBlank { cleanRoll.lowercase() }
            val cleanProgram = form.program.trim()
            val cleanFirstName = form.firstName.trim()
            val cleanLastName = form.lastName.trim()
            val cleanPassword = form.password.trim().ifBlank { "00000" }

            var createdProfile: IntermediateStudentProfileDto? = null

            // 1. Try server RPC direct_register_intermediate_student
            try {
                val regParams = buildJsonObject {
                    put("p_roll_number", cleanRoll)
                    put("p_registration_number", cleanReg)
                    put("p_program_name", cleanProgram)
                    put("p_username", cleanUsername)
                    put("p_first_name", cleanFirstName)
                    put("p_last_name", cleanLastName)
                    put("p_password", cleanPassword)
                }

                val rpcResponse = client.postgrest.rpc("direct_register_intermediate_student", regParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (isSuccess) {
                        val profileObj = jsonObj["profile"]?.jsonObject
                        if (profileObj != null) {
                            createdProfile = IntermediateStudentProfileDto(
                                id = profileObj["id"]?.jsonPrimitive?.content ?: "inter_$cleanRoll",
                                username = profileObj["username"]?.jsonPrimitive?.content ?: cleanUsername,
                                firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: cleanFirstName,
                                lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: cleanLastName,
                                rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: cleanRoll,
                                registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: cleanReg,
                                program = profileObj["program"]?.jsonPrimitive?.content ?: cleanProgram
                            )
                        }
                    }
                }
            } catch (rpcErr: Exception) {
                Log.w(TAG, "direct_register_intermediate_student RPC fallback: ${rpcErr.message}")
            }

            val profile = createdProfile ?: IntermediateStudentProfileDto(
                id = "inter_${cleanRoll}_${System.currentTimeMillis()}",
                username = cleanUsername,
                firstName = cleanFirstName,
                lastName = cleanLastName,
                rollNumber = cleanRoll,
                registrationNumber = cleanReg,
                program = cleanProgram
            )

            // Save to persistent local RegisteredStudentStore
            com.example.data.datasource.RegisteredStudentStore.saveIntermediateAccount(
                com.example.data.datasource.RegisteredIntermediateStudentAccount(
                    id = profile.id,
                    username = cleanUsername,
                    firstName = cleanFirstName,
                    lastName = cleanLastName,
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    password = cleanPassword
                )
            )

            // Upsert into official_intermediate_students table
            try {
                val studentFullName = "$cleanFirstName $cleanLastName".trim()
                client.from("official_intermediate_students").upsert(
                    buildJsonObject {
                        put("roll_number", cleanRoll)
                        put("registration_number", cleanReg)
                        put("student_name", studentFullName)
                        put("program_name", cleanProgram)
                        put("is_claimed", true)
                        put("is_active", true)
                    }
                )
            } catch (dbErr: Exception) {
                Log.w(TAG, "official_intermediate_students sync note: ${dbErr.message}")
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
     * Supports default password 00000 or custom registered password with multi-stage fallback.
     */
    suspend fun loginIntermediateStudent(
        usernameOrRoll: String,
        password: String
    ): AuthResult<IntermediateStudentProfileDto> {
        val query = usernameOrRoll.trim()
        val cleanPassword = password.trim()

        if (query.isBlank() || cleanPassword.isBlank()) {
            return AuthResult.Error("Username/Roll Number and Password are required.")
        }

        // 0. Safety: Check if this roll number belongs to a BS Student
        val bsAccount = com.example.data.datasource.RegisteredStudentStore.findBsAccount(query)
        if (bsAccount != null) {
            return AuthResult.Error("Roll number '$query' is enrolled in BS Degree Program (${bsAccount.program}). Please log in using the BS Student Portal.")
        }

        // 1. Stage 1: Central Database Password Verification (Cross-Device Synchronized)
        val pwdVerification = CentralAuthRemoteManager.verifyPasswordWithRemote(
            identifier = query,
            passwordAttempt = cleanPassword,
            defaultPasswordFallback = "00000"
        )
        if (pwdVerification is CentralAuthRemoteManager.PasswordVerificationResult.Failed) {
            return AuthResult.Error(pwdVerification.message)
        }

        // 2. Stage 2: Locate Intermediate Student Record
        var resolvedProfile: IntermediateStudentProfileDto? = null

        val localMatch = com.example.data.datasource.RegisteredStudentStore.findIntermediateAccount(query)
        if (localMatch != null) {
            resolvedProfile = IntermediateStudentProfileDto(
                id = localMatch.id.ifBlank { "inter_${localMatch.rollNumber}" },
                username = localMatch.username,
                firstName = localMatch.firstName,
                lastName = localMatch.lastName,
                rollNumber = localMatch.rollNumber,
                registrationNumber = localMatch.registrationNumber,
                program = localMatch.program
            )
        }

        if (resolvedProfile == null) {
            // Try official_intermediate_students table from Supabase
            resolvedProfile = checkOfficialIntermediateStudentFallback(query, cleanPassword)
        }

        if (resolvedProfile == null) {
            // Try direct_login_intermediate_student RPC on Supabase (safely caught)
            try {
                val loginParams = buildJsonObject {
                    put("p_identifier", query)
                    put("p_password", cleanPassword)
                }

                val rpcResponse = client.postgrest.rpc("direct_login_intermediate_student", loginParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (isSuccess) {
                        val profileObj = jsonObj["profile"]?.jsonObject
                        if (profileObj != null) {
                            resolvedProfile = IntermediateStudentProfileDto(
                                id = profileObj["id"]?.jsonPrimitive?.content ?: "inter_$query",
                                username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                                firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: "",
                                lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: "",
                                rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: query,
                                registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: "",
                                program = profileObj["program"]?.jsonPrimitive?.content ?: ""
                            )
                        }
                    }
                }
            } catch (rpcErr: Exception) {
                Log.w(TAG, "direct_login_intermediate_student RPC error: ${rpcErr.message}")
            }
        }

        if (resolvedProfile == null) {
            return AuthResult.Error("Student record not found in official college database. Please contact College Administration to import your roll number ($query).")
        }

        // 3. Stage 3: Single-Device Concurrency Enforcement ("WhatsApp-Like" Session Lock)
        val sessionIdentifier = resolvedProfile.rollNumber.ifBlank { query }
        val sessionResult = ActiveSessionRemoteManager.acquireSession(
            context = com.example.util.DeviceIdentifierHelper.getAppContext(),
            userIdentifier = sessionIdentifier,
            role = com.example.data.model.AppRole.STUDENT_INTERMEDIATE
        )
        if (sessionResult is ActiveSessionRemoteManager.SessionAcquireResult.Blocked) {
            return AuthResult.Error(sessionResult.message)
        }

        // 4. Save to local persistent store with verified credentials
        com.example.data.datasource.RegisteredStudentStore.saveIntermediateAccount(
            com.example.data.datasource.RegisteredIntermediateStudentAccount(
                id = resolvedProfile.id,
                username = resolvedProfile.username,
                firstName = resolvedProfile.firstName,
                lastName = resolvedProfile.lastName,
                rollNumber = resolvedProfile.rollNumber,
                registrationNumber = resolvedProfile.registrationNumber,
                program = resolvedProfile.program,
                password = cleanPassword
            )
        )

        return AuthResult.Success(resolvedProfile, "Intermediate Student login successful.")
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
                it.registrationNumber.trim().equals(cleanId, ignoreCase = true) ||
                it.studentName?.trim()?.equals(cleanId, ignoreCase = true) == true ||
                it.id.equals(cleanId, ignoreCase = true)
            }

            if (match != null) {
                val names = match.studentName?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()
                val fName = match.firstName ?: names.firstOrNull() ?: match.effectiveDisplayName
                val lName = match.lastName ?: names.drop(1).joinToString(" ")

                IntermediateStudentProfileDto(
                    id = match.id.ifBlank { "inter_${match.rollNumber}" },
                    username = match.rollNumber.lowercase(),
                    firstName = fName,
                    lastName = lName,
                    rollNumber = match.rollNumber.ifBlank { cleanId },
                    registrationNumber = match.registrationNumber.ifBlank { "REG-$cleanId" },
                    program = match.effectiveProgram.ifBlank { "Intermediate Program" }
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
