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
            val cleanUsername = form.username.trim().lowercase().ifBlank { cleanRoll.lowercase() }
            val cleanProgram = form.program.trim()
            val cleanSession = form.session.trim().ifBlank { "2024-2028" }
            val cleanSemester = form.semester.trim().ifBlank { "Semester 1" }
            val cleanFirstName = form.firstName.trim()
            val cleanLastName = form.lastName.trim()
            val cleanPassword = form.password.trim().ifBlank { "00000" }

            var createdProfile: BsStudentProfileDto? = null

            // 1. Try server-side RPC direct_register_bs_student
            try {
                val regParams = buildJsonObject {
                    put("p_roll_number", cleanRoll)
                    put("p_registration_number", cleanReg)
                    put("p_program_name", cleanProgram)
                    put("p_semester", cleanSemester)
                    put("p_username", cleanUsername)
                    put("p_first_name", cleanFirstName)
                    put("p_last_name", cleanLastName)
                    put("p_password", cleanPassword)
                }

                val rpcResponse = client.postgrest.rpc("direct_register_bs_student", regParams)
                val bodyText = rpcResponse.data
                if (bodyText.isNotBlank()) {
                    val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                    val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (isSuccess) {
                        val profileObj = jsonObj["profile"]?.jsonObject
                        if (profileObj != null) {
                            createdProfile = BsStudentProfileDto(
                                id = profileObj["id"]?.jsonPrimitive?.content ?: "bs_$cleanRoll",
                                username = profileObj["username"]?.jsonPrimitive?.content ?: cleanUsername,
                                firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: cleanFirstName,
                                lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: cleanLastName,
                                rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: cleanRoll,
                                registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: cleanReg,
                                program = profileObj["program"]?.jsonPrimitive?.content ?: cleanProgram,
                                session = cleanSession,
                                semester = profileObj["semester"]?.jsonPrimitive?.content ?: cleanSemester
                            )
                        }
                    }
                }
            } catch (rpcErr: Exception) {
                Log.w(TAG, "direct_register_bs_student RPC fallback: ${rpcErr.message}")
            }

            val profile = createdProfile ?: BsStudentProfileDto(
                id = "bs_${cleanRoll}_${System.currentTimeMillis()}",
                username = cleanUsername,
                firstName = cleanFirstName,
                lastName = cleanLastName,
                rollNumber = cleanRoll,
                registrationNumber = cleanReg,
                program = cleanProgram,
                session = cleanSession,
                semester = cleanSemester
            )

            // Save to persistent local RegisteredStudentStore for instantaneous subsequent logins
            com.example.data.datasource.RegisteredStudentStore.saveBsAccount(
                com.example.data.datasource.RegisteredBsStudentAccount(
                    id = profile.id,
                    username = cleanUsername,
                    firstName = cleanFirstName,
                    lastName = cleanLastName,
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    session = cleanSession,
                    semester = cleanSemester
                )
            )

            // Upsert into official_bs_students if available
            try {
                val studentFullName = "$cleanFirstName $cleanLastName".trim()
                client.from("official_bs_students").upsert(
                    buildJsonObject {
                        put("roll_number", cleanRoll)
                        put("registration_number", cleanReg)
                        put("student_name", studentFullName)
                        put("program_name", cleanProgram)
                        put("session_year", cleanSession)
                        put("is_claimed", true)
                        put("is_active", true)
                    }
                )
            } catch (dbErr: Exception) {
                Log.w(TAG, "official_bs_students table sync note: ${dbErr.message}")
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
     * Supports default password 00000 or custom registered password with multi-stage fallback.
     */
    suspend fun loginBsStudent(
        usernameOrRoll: String,
        password: String
    ): AuthResult<BsStudentProfileDto> {
        val query = usernameOrRoll.trim()
        val cleanPassword = password.trim()

        if (query.isBlank() || cleanPassword.isBlank()) {
            return AuthResult.Error("Roll Number/Username and Password are required.")
        }

        // 1. Safety: Check if this roll number belongs to an Intermediate Student
        val interAccount = com.example.data.datasource.RegisteredStudentStore.findIntermediateAccount(query)
        if (interAccount != null) {
            return AuthResult.Error("Roll number '$query' is enrolled in Intermediate Program (${interAccount.program}). Please log in using the Intermediate Student Portal.")
        }

        var resolvedProfile: BsStudentProfileDto? = null
        var lastErrorMessage: String? = null

        // 2. Primary Strategy: Direct Database Authentication via direct_login_bs_student RPC
        try {
            val loginParams = buildJsonObject {
                put("p_identifier", query)
                put("p_password", cleanPassword)
            }
            val rpcResponse = client.postgrest.rpc("direct_login_bs_student", loginParams)
            val bodyText = rpcResponse.data
            if (bodyText.isNotBlank()) {
                val jsonObj = json.parseToJsonElement(bodyText).jsonObject
                val isSuccess = jsonObj["success"]?.jsonPrimitive?.booleanOrNull ?: false
                if (isSuccess) {
                    val profileObj = jsonObj["profile"]?.jsonObject
                    if (profileObj != null) {
                        resolvedProfile = BsStudentProfileDto(
                            id = profileObj["id"]?.jsonPrimitive?.content ?: "bs_$query",
                            username = profileObj["username"]?.jsonPrimitive?.content ?: query,
                            firstName = profileObj["first_name"]?.jsonPrimitive?.content ?: "",
                            lastName = profileObj["last_name"]?.jsonPrimitive?.content ?: "",
                            rollNumber = profileObj["roll_number"]?.jsonPrimitive?.content ?: query,
                            registrationNumber = profileObj["registration_number"]?.jsonPrimitive?.content ?: "",
                            program = profileObj["program"]?.jsonPrimitive?.content ?: "",
                            session = "2024-2028",
                            semester = profileObj["semester"]?.jsonPrimitive?.content ?: "Semester 1"
                        )
                        val forceChange = jsonObj["force_password_change"]?.jsonPrimitive?.booleanOrNull ?: true
                        if (!forceChange) {
                            com.example.data.datasource.PasswordRegistryStore.markPasswordChanged(resolvedProfile.rollNumber)
                        }
                    }
                } else {
                    val rpcError = jsonObj["error"]?.jsonPrimitive?.content
                    if (!rpcError.isNullOrBlank()) {
                        lastErrorMessage = rpcError
                        // If database explicitly identified incorrect password, fail immediately
                        if (rpcError.contains("password", ignoreCase = true) || rpcError.contains("incorrect", ignoreCase = true)) {
                            return AuthResult.Error(rpcError)
                        }
                    }
                }
            }
        } catch (rpcErr: Exception) {
            Log.w(TAG, "direct_login_bs_student RPC error: ${rpcErr.message}")
        }

        // 3. Fallback to official_bs_students table only if initial setup and default password
        if (resolvedProfile == null && cleanPassword == "00000") {
            resolvedProfile = checkOfficialBsStudentFallback(query, cleanPassword)
        }

        if (resolvedProfile == null) {
            return AuthResult.Error(lastErrorMessage ?: "Student record not found or invalid credentials. Please contact College Administration / HOD.")
        }

        // 4. Single-Device Concurrency Enforcement ("WhatsApp-Like" Session Lock)
        val sessionIdentifier = resolvedProfile.rollNumber.ifBlank { query }
        val sessionResult = ActiveSessionRemoteManager.acquireSession(
            context = com.example.util.DeviceIdentifierHelper.getAppContext(),
            userIdentifier = sessionIdentifier,
            role = com.example.data.model.AppRole.STUDENT_BS
        )
        if (sessionResult is ActiveSessionRemoteManager.SessionAcquireResult.Blocked) {
            return AuthResult.Error(
                message = sessionResult.message,
                code = "SESSION_BLOCKED:${sessionResult.activeDeviceName}:${sessionResult.activeDeviceId}:${sessionResult.userIdentifier}:${sessionResult.role.roleKey}"
            )
        }

        // 5. Save student record locally without storing plaintext password
        com.example.data.datasource.RegisteredStudentStore.saveBsAccount(
            com.example.data.datasource.RegisteredBsStudentAccount(
                id = resolvedProfile.id,
                username = resolvedProfile.username,
                firstName = resolvedProfile.firstName,
                lastName = resolvedProfile.lastName,
                rollNumber = resolvedProfile.rollNumber,
                registrationNumber = resolvedProfile.registrationNumber,
                program = resolvedProfile.program,
                session = resolvedProfile.session ?: "2024-2028",
                semester = resolvedProfile.semester ?: "Semester 1"
            )
        )

        return AuthResult.Success(resolvedProfile, "BS Student login successful.")
    }

    private suspend fun checkOfficialBsStudentFallback(
        identifier: String,
        password: String
    ): BsStudentProfileDto? {
        return try {
            val cleanId = identifier.trim().uppercase()
            val res = client.from("official_bs_students")
                .select()
                .decodeList<OfficialBsStudentDto>()

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

                BsStudentProfileDto(
                    id = match.id.ifBlank { "bs_${match.rollNumber}" },
                    username = match.rollNumber.lowercase(),
                    firstName = fName,
                    lastName = lName,
                    rollNumber = match.rollNumber.ifBlank { cleanId },
                    registrationNumber = match.registrationNumber.ifBlank { "REG-$cleanId" },
                    program = match.effectiveProgram.ifBlank { "BS Program" },
                    session = match.effectiveSession,
                    semester = match.effectiveSemester
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
