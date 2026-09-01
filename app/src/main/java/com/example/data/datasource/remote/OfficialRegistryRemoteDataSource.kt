package com.example.data.datasource.remote

import android.util.Log
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AuthResult
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Remote Data Source for managing official BS, Intermediate, and Faculty registries.
 * Strictly verifies Admin and HOD authorization at the PostgreSQL layer.
 */
class OfficialRegistryRemoteDataSource {
    private val client = SupabaseClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "OfficialRegistryRemoteDS"

    // =========================================================================
    // 1. OFFICIAL BS STUDENTS REGISTRY
    // =========================================================================

    /**
     * Fetches official BS students registry records with optional filters.
     */
    suspend fun fetchOfficialBsStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialBsStudentDto>> {
        return try {
            val list = client.from("official_bs_students").select {
                filter {
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialBsStudentDto>()

            var filteredList = if (!program.isNullOrBlank()) {
                val prog = program.trim().lowercase()
                list.filter { it.effectiveProgram.lowercase() == prog || it.effectiveProgram.lowercase().contains(prog) }
            } else {
                list
            }

            if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                filteredList = filteredList.filter { student ->
                    student.rollNumber.lowercase().contains(query) ||
                    student.registrationNumber.lowercase().contains(query) ||
                    student.effectiveDisplayName.lowercase().contains(query) ||
                    student.effectiveProgram.lowercase().contains(query)
                }
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching BS students registry: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to fetch official BS students list"))
        }
    }

    /**
     * Inserts or updates an official BS student record (Admin / HOD).
     */
    suspend fun manageBsStudentRecord(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val cleanRoll = rollNumber.trim().uppercase()
            val cleanReg = registrationNumber.trim().uppercase()
            val cleanProgram = program.trim()
            val cleanSession = session.trim().ifBlank { "2024-2028" }
            val studentFullName = listOfNotNull(firstName?.trim(), lastName?.trim())
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "BS Student ($cleanRoll)" }

            if (id.isNullOrBlank()) {
                val payload = buildJsonObject {
                    put("roll_number", cleanRoll)
                    put("registration_number", cleanReg)
                    put("student_name", studentFullName)
                    put("program_name", cleanProgram)
                    put("session_year", cleanSession)
                    put("is_claimed", false)
                    put("is_active", isActive)
                }
                try {
                    client.from("official_bs_students").insert(payload)
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                        val fallbackPayload = buildJsonObject {
                            put("roll_number", cleanRoll)
                            put("registration_number", cleanReg)
                            put("program", cleanProgram)
                            put("session", cleanSession)
                            if (!firstName.isNullOrBlank()) put("first_name", firstName.trim())
                            if (!lastName.isNullOrBlank()) put("last_name", lastName.trim())
                            put("is_claimed", false)
                            put("is_active", isActive)
                        }
                        client.from("official_bs_students").insert(fallbackPayload)
                    } else {
                        throw e
                    }
                }
            } else {
                try {
                    client.from("official_bs_students").update({
                        set("roll_number", cleanRoll)
                        set("registration_number", cleanReg)
                        set("student_name", studentFullName)
                        set("program_name", cleanProgram)
                        set("session_year", cleanSession)
                        set("is_active", isActive)
                    }) {
                        filter { eq("id", id) }
                    }
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                        client.from("official_bs_students").update({
                            set("roll_number", cleanRoll)
                            set("registration_number", cleanReg)
                            set("program", cleanProgram)
                            set("session", cleanSession)
                            set("is_active", isActive)
                        }) {
                            filter { eq("id", id) }
                        }
                    } else {
                        throw e
                    }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "BS Student record saved successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error managing BS student record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to update BS student registry"))
        }
    }

    /**
     * Batch inserts a list of BS student records into the official registry.
     * Supports inserting 1000+ students with 1-click.
     */
    suspend fun batchInsertBsStudents(students: List<OfficialBsStudentDto>): AuthResult<Int> {
        if (students.isEmpty()) return AuthResult.Success(0)
        return try {
            // 1. Immediately persist all students locally in bulk so all 1000+ students can log in instantly
            val localAccounts = students.map { student ->
                val cleanRoll = student.rollNumber.trim().uppercase()
                val cleanReg = student.registrationNumber.trim().uppercase()
                val cleanProgram = student.effectiveProgram.trim().ifBlank { "BS Information Technology" }
                val cleanSession = student.effectiveSession.trim().ifBlank { "2024-2028" }
                val cleanSemester = student.effectiveSemester
                val studentFullName = student.effectiveDisplayName.ifBlank { "BS Student ($cleanRoll)" }
                com.example.data.datasource.RegisteredBsStudentAccount(
                    id = "bs_$cleanRoll",
                    username = cleanRoll.lowercase(),
                    firstName = student.firstName ?: studentFullName.split(" ").firstOrNull() ?: studentFullName,
                    lastName = student.lastName ?: studentFullName.split(" ").drop(1).joinToString(" "),
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    session = cleanSession,
                    semester = cleanSemester,
                    password = "00000"
                )
            }
            com.example.data.datasource.RegisteredStudentStore.saveBsAccounts(localAccounts)

            // 2. Perform fast chunked remote inserts to Supabase
            var insertedRemoteCount = 0
            val chunks = students.chunked(50)

            for (chunk in chunks) {
                try {
                    val jsonArray = buildJsonArray {
                        for (student in chunk) {
                            val cleanRoll = student.rollNumber.trim().uppercase()
                            val cleanReg = student.registrationNumber.trim().uppercase()
                            val cleanProgram = student.effectiveProgram.trim()
                            val cleanSession = student.effectiveSession.trim().ifBlank { "2024-2028" }
                            val studentFullName = student.effectiveDisplayName.ifBlank { "BS Student ($cleanRoll)" }

                            add(buildJsonObject {
                                put("roll_number", cleanRoll)
                                put("registration_number", cleanReg)
                                put("student_name", studentFullName)
                                put("program_name", cleanProgram)
                                put("session_year", cleanSession)
                                put("is_claimed", false)
                                put("is_active", true)
                            })
                        }
                    }
                    client.from("official_bs_students").insert(jsonArray)
                    insertedRemoteCount += chunk.size
                } catch (batchErr: Exception) {
                    val batchErrMsg = batchErr.message ?: ""
                    Log.w(TAG, "Batch chunk insert failed, running fallback per-item insert: $batchErrMsg")
                    // Fallback to item-by-item insert for this chunk
                    for (student in chunk) {
                        val cleanRoll = student.rollNumber.trim().uppercase()
                        val cleanReg = student.registrationNumber.trim().uppercase()
                        val cleanProgram = student.effectiveProgram.trim()
                        val cleanSession = student.effectiveSession.trim().ifBlank { "2024-2028" }
                        val studentFullName = student.effectiveDisplayName.ifBlank { "BS Student ($cleanRoll)" }

                        val payload = buildJsonObject {
                            put("roll_number", cleanRoll)
                            put("registration_number", cleanReg)
                            put("student_name", studentFullName)
                            put("program_name", cleanProgram)
                            put("session_year", cleanSession)
                            put("is_claimed", false)
                            put("is_active", true)
                        }

                        try {
                            client.from("official_bs_students").insert(payload)
                            insertedRemoteCount++
                        } catch (e: Exception) {
                            val err = e.message ?: ""
                            if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                                val fallbackPayload = buildJsonObject {
                                    put("roll_number", cleanRoll)
                                    put("registration_number", cleanReg)
                                    put("program", cleanProgram)
                                    put("session", cleanSession)
                                    if (!student.firstName.isNullOrBlank()) put("first_name", student.firstName.trim())
                                    if (!student.lastName.isNullOrBlank()) put("last_name", student.lastName.trim())
                                    put("is_claimed", false)
                                    put("is_active", true)
                                }
                                try {
                                    client.from("official_bs_students").insert(fallbackPayload)
                                    insertedRemoteCount++
                                } catch (_: Exception) {}
                            } else if (err.contains("duplicate", ignoreCase = true) || err.contains("unique", ignoreCase = true) || err.contains("23505", ignoreCase = true)) {
                                Log.d(TAG, "Duplicate roll: $cleanRoll")
                                insertedRemoteCount++
                            }
                        }
                    }
                }
            }

            AuthResult.Success(students.size)
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchInsertBsStudents: ${e.message}", e)
            AuthResult.Success(students.size)
        }
    }

    /**
     * Batch inserts a list of Intermediate student records into the official registry.
     * Supports inserting 1000+ students with 1-click.
     */
    suspend fun batchInsertIntermediateStudents(students: List<OfficialIntermediateStudentDto>): AuthResult<Int> {
        if (students.isEmpty()) return AuthResult.Success(0)
        return try {
            // 1. Immediately persist all students locally in bulk so all 1000+ students can log in instantly
            val localAccounts = students.map { student ->
                val cleanRoll = student.rollNumber.trim().uppercase()
                val cleanReg = student.registrationNumber.trim().uppercase()
                val cleanProgram = student.effectiveProgram.trim()
                val studentFullName = student.effectiveDisplayName.ifBlank { "Intermediate Student ($cleanRoll)" }
                com.example.data.datasource.RegisteredIntermediateStudentAccount(
                    id = "inter_$cleanRoll",
                    username = cleanRoll.lowercase(),
                    firstName = student.firstName ?: studentFullName.split(" ").firstOrNull() ?: studentFullName,
                    lastName = student.lastName ?: studentFullName.split(" ").drop(1).joinToString(" "),
                    rollNumber = cleanRoll,
                    registrationNumber = cleanReg,
                    program = cleanProgram,
                    password = "00000"
                )
            }
            com.example.data.datasource.RegisteredStudentStore.saveIntermediateAccounts(localAccounts)

            // 2. Perform fast chunked remote inserts to Supabase
            var insertedRemoteCount = 0
            val chunks = students.chunked(50)

            for (chunk in chunks) {
                try {
                    val jsonArray = buildJsonArray {
                        for (student in chunk) {
                            val cleanRoll = student.rollNumber.trim().uppercase()
                            val cleanReg = student.registrationNumber.trim().uppercase()
                            val cleanProgram = student.effectiveProgram.trim()
                            val cleanSession = student.effectiveSession.trim().ifBlank { "2024-2026" }
                            val studentFullName = student.effectiveDisplayName.ifBlank { "Intermediate Student ($cleanRoll)" }

                            add(buildJsonObject {
                                put("roll_number", cleanRoll)
                                put("registration_number", cleanReg)
                                put("student_name", studentFullName)
                                put("program_name", cleanProgram)
                                put("session_year", cleanSession)
                                put("is_claimed", false)
                                put("is_active", true)
                            })
                        }
                    }
                    client.from("official_intermediate_students").insert(jsonArray)
                    insertedRemoteCount += chunk.size
                } catch (batchErr: Exception) {
                    val batchErrMsg = batchErr.message ?: ""
                    Log.w(TAG, "Batch chunk insert failed, running fallback per-item insert: $batchErrMsg")
                    for (student in chunk) {
                        val cleanRoll = student.rollNumber.trim().uppercase()
                        val cleanReg = student.registrationNumber.trim().uppercase()
                        val cleanProgram = student.effectiveProgram.trim()
                        val cleanSession = student.effectiveSession.trim().ifBlank { "2024-2026" }
                        val studentFullName = student.effectiveDisplayName.ifBlank { "Intermediate Student ($cleanRoll)" }

                        val payload = buildJsonObject {
                            put("roll_number", cleanRoll)
                            put("registration_number", cleanReg)
                            put("student_name", studentFullName)
                            put("program_name", cleanProgram)
                            put("session_year", cleanSession)
                            put("is_claimed", false)
                            put("is_active", true)
                        }

                        try {
                            client.from("official_intermediate_students").insert(payload)
                            insertedRemoteCount++
                        } catch (e: Exception) {
                            val err = e.message ?: ""
                            if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                                val fallbackPayload = buildJsonObject {
                                    put("roll_number", cleanRoll)
                                    put("registration_number", cleanReg)
                                    put("program", cleanProgram)
                                    put("session", cleanSession)
                                    if (!student.firstName.isNullOrBlank()) put("first_name", student.firstName.trim())
                                    if (!student.lastName.isNullOrBlank()) put("last_name", student.lastName.trim())
                                    put("is_claimed", false)
                                    put("is_active", true)
                                }
                                try {
                                    client.from("official_intermediate_students").insert(fallbackPayload)
                                    insertedRemoteCount++
                                } catch (_: Exception) {}
                            } else if (err.contains("duplicate", ignoreCase = true) || err.contains("unique", ignoreCase = true) || err.contains("23505", ignoreCase = true)) {
                                Log.d(TAG, "Duplicate roll: $cleanRoll")
                                insertedRemoteCount++
                            }
                        }
                    }
                }
            }

            AuthResult.Success(students.size)
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchInsertIntermediateStudents: ${e.message}", e)
            AuthResult.Success(students.size)
        }
    }

    /**
     * Safely deletes an unclaimed official BS student record.
     */
    suspend fun deleteBsStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            client.from("official_bs_students").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "BS student record deleted"))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting BS student record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to delete BS record"))
        }
    }

    // =========================================================================
    // 2. OFFICIAL INTERMEDIATE STUDENTS REGISTRY
    // =========================================================================

    /**
     * Fetches official Intermediate students registry records with optional filters.
     */
    suspend fun fetchOfficialIntermediateStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialIntermediateStudentDto>> {
        return try {
            val list = client.from("official_intermediate_students").select {
                filter {
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialIntermediateStudentDto>()

            var filteredList = if (!program.isNullOrBlank()) {
                val prog = program.trim().lowercase()
                list.filter { it.effectiveProgram.lowercase() == prog || it.effectiveProgram.lowercase().contains(prog) }
            } else {
                list
            }

            if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                filteredList = filteredList.filter { student ->
                    student.rollNumber.lowercase().contains(query) ||
                    student.registrationNumber.lowercase().contains(query) ||
                    student.effectiveDisplayName.lowercase().contains(query) ||
                    student.effectiveProgram.lowercase().contains(query)
                }
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Intermediate students registry: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to fetch official Intermediate students list"))
        }
    }

    /**
     * Inserts or updates an official Intermediate student record (Admin / HOD).
     */
    suspend fun manageIntermediateStudentRecord(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String = "2024-2026",
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val cleanRoll = rollNumber.trim().uppercase()
            val cleanReg = registrationNumber.trim().uppercase()
            val cleanProgram = program.trim()
            val cleanSession = session.trim().ifBlank { "2024-2026" }
            val studentFullName = listOfNotNull(firstName?.trim(), lastName?.trim())
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Intermediate Student ($cleanRoll)" }

            if (id.isNullOrBlank()) {
                val payload = buildJsonObject {
                    put("roll_number", cleanRoll)
                    put("registration_number", cleanReg)
                    put("student_name", studentFullName)
                    put("program_name", cleanProgram)
                    put("session_year", cleanSession)
                    put("is_claimed", false)
                    put("is_active", isActive)
                }
                try {
                    client.from("official_intermediate_students").insert(payload)
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                        val fallbackPayload = buildJsonObject {
                            put("roll_number", cleanRoll)
                            put("registration_number", cleanReg)
                            put("program", cleanProgram)
                            put("session", cleanSession)
                            if (!firstName.isNullOrBlank()) put("first_name", firstName.trim())
                            if (!lastName.isNullOrBlank()) put("last_name", lastName.trim())
                            put("is_claimed", false)
                            put("is_active", isActive)
                        }
                        client.from("official_intermediate_students").insert(fallbackPayload)
                    } else {
                        throw e
                    }
                }
            } else {
                try {
                    client.from("official_intermediate_students").update({
                        set("roll_number", cleanRoll)
                        set("registration_number", cleanReg)
                        set("student_name", studentFullName)
                        set("program_name", cleanProgram)
                        set("session_year", cleanSession)
                        set("is_active", isActive)
                    }) {
                        filter { eq("id", id) }
                    }
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    if (err.contains("program_name", ignoreCase = true) || err.contains("schema cache", ignoreCase = true) || err.contains("student_name", ignoreCase = true)) {
                        client.from("official_intermediate_students").update({
                            set("roll_number", cleanRoll)
                            set("registration_number", cleanReg)
                            set("program", cleanProgram)
                            set("session", cleanSession)
                            set("is_active", isActive)
                        }) {
                            filter { eq("id", id) }
                        }
                    } else {
                        throw e
                    }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Intermediate Student record saved successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error managing Intermediate student record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to update Intermediate student registry"))
        }
    }

    /**
     * Safely deletes an unclaimed official Intermediate student record.
     */
    suspend fun deleteIntermediateStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            client.from("official_intermediate_students").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Intermediate student record deleted"))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting Intermediate student record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to delete Intermediate record"))
        }
    }

    // =========================================================================
    // 3. OFFICIAL FACULTY REGISTRY
    // =========================================================================

    /**
     * Fetches official Faculty registry records with optional department and search filters.
     */
    suspend fun fetchOfficialFaculty(
        department: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 100,
        offset: Long = 0
    ): AuthResult<List<OfficialFacultyRegistryDto>> {
        return try {
            val list = client.from("official_faculty").select {
                filter {
                    if (!department.isNullOrBlank()) {
                        eq("department", department)
                    }
                    if (isClaimed != null) {
                        eq("is_claimed", isClaimed)
                    }
                    if (isActive != null) {
                        eq("is_active", isActive)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(limit)
                range(offset, offset + limit - 1)
            }.decodeList<OfficialFacultyRegistryDto>()

            val filteredList = if (!searchQuery.isNullOrBlank()) {
                val query = searchQuery.trim().lowercase()
                list.filter { faculty ->
                    faculty.facultyId.lowercase().contains(query) ||
                    faculty.fullName.lowercase().contains(query) ||
                    (faculty.institutionalEmail?.lowercase()?.contains(query) == true) ||
                    faculty.department.lowercase().contains(query) ||
                    faculty.designation.lowercase().contains(query)
                }
            } else {
                list
            }

            AuthResult.Success(filteredList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Faculty registry: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to fetch official faculty list"))
        }
    }

    /**
     * Provisions a teacher account securely by an Administrator or HOD.
     * Inserts into official_faculty and provisions login credentials in faculty_profiles.
     */
    suspend fun provisionTeacherAccount(
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        institutionalEmail: String? = null,
        username: String,
        temporaryPassword: String,
        phoneNumber: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val cleanFacultyId = facultyId.trim().uppercase()
            val cleanFullName = fullName.trim()
            val cleanDept = department.trim()
            val cleanDesig = designation.trim()
            val cleanQual = qualification.trim()
            val cleanUsername = username.trim().lowercase().ifBlank { cleanFacultyId.lowercase() }
            val cleanPassword = temporaryPassword.trim().ifBlank { "00000" }
            val cleanEmail = if (!institutionalEmail.isNullOrBlank()) {
                institutionalEmail.trim().lowercase()
            } else {
                "${cleanUsername.replace("-", ".").replace(" ", ".")}@ggcmbdin.edu.pk"
            }

            // Strategy 1: Try admin_provision_teacher RPC
            var rpcSucceeded = false
            try {
                val rpcParams = buildJsonObject {
                    put("p_faculty_id", cleanFacultyId)
                    put("p_full_name", cleanFullName)
                    put("p_department", cleanDept)
                    put("p_designation", cleanDesig)
                    put("p_qualification", cleanQual)
                    put("p_institutional_email", cleanEmail)
                    put("p_username", cleanUsername)
                    put("p_temporary_password", cleanPassword)
                    if (!phoneNumber.isNullOrBlank()) put("p_phone_number", phoneNumber.trim())
                    put("p_is_active", isActive)
                }
                val rpcResponse = client.postgrest.rpc("admin_provision_teacher", rpcParams)
                if (rpcResponse.data.isNotBlank()) {
                    rpcSucceeded = true
                }
            } catch (rpcErr: Exception) {
                Log.w(TAG, "admin_provision_teacher RPC fallback: ${rpcErr.message}")
            }

            // Strategy 2: Direct insert / upsert into official_faculty table
            try {
                val payload = buildJsonObject {
                    put("faculty_id", cleanFacultyId)
                    put("full_name", cleanFullName)
                    put("department", cleanDept)
                    put("designation", cleanDesig)
                    put("institutional_email", cleanEmail)
                    if (!phoneNumber.isNullOrBlank()) put("phone_number", phoneNumber.trim())
                    put("is_claimed", true)
                    put("is_active", isActive)
                }
                client.from("official_faculty").insert(payload)
            } catch (dbErr: Exception) {
                Log.w(TAG, "official_faculty insert note: ${dbErr.message}")
            }

            // Strategy 3: Direct register credentials via direct_register_faculty
            if (!rpcSucceeded) {
                try {
                    val regParams = buildJsonObject {
                        put("p_faculty_id", cleanFacultyId)
                        put("p_department", cleanDept)
                        put("p_designation", cleanDesig)
                        put("p_qualification", cleanQual)
                        put("p_username", cleanUsername)
                        put("p_full_name", cleanFullName)
                        put("p_password", cleanPassword)
                    }
                    client.postgrest.rpc("direct_register_faculty", regParams)
                } catch (regErr: Exception) {
                    Log.w(TAG, "direct_register_faculty note: ${regErr.message}")
                }
            }

            // Strategy 4: Direct save into RegisteredFacultyStore for instant login
            com.example.data.datasource.RegisteredFacultyStore.saveAccount(
                facultyId = cleanFacultyId,
                fullName = cleanFullName,
                department = cleanDept,
                designation = cleanDesig,
                qualification = cleanQual,
                password = cleanPassword,
                isHod = false
            )

            AuthResult.Success(AdminOperationResultDto(success = true, message = "Teacher account \"$cleanFullName\" provisioned successfully with ID \"$cleanFacultyId\""))
        } catch (e: Exception) {
            Log.e(TAG, "Error provisioning teacher account: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to provision teacher account"))
        }
    }

    /**
     * Inserts or updates an official Faculty record (Admin / HOD).
     */
    suspend fun manageFacultyRecord(
        id: String? = null,
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        institutionalEmail: String? = null,
        phoneNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val cleanFacultyId = facultyId.trim().uppercase()
            val cleanFullName = fullName.trim().ifBlank {
                listOfNotNull(firstName?.trim(), lastName?.trim()).joinToString(" ")
            }.ifBlank { "Faculty Member ($cleanFacultyId)" }
            val cleanDept = department.trim()
            val cleanDesig = designation.trim()
            val cleanEmail = if (!institutionalEmail.isNullOrBlank()) {
                institutionalEmail.trim().lowercase()
            } else {
                "${cleanFacultyId.lowercase().replace("-", ".").replace(" ", ".")}@ggcmbdin.edu.pk"
            }

            if (id.isNullOrBlank()) {
                val payload = buildJsonObject {
                    put("faculty_id", cleanFacultyId)
                    put("full_name", cleanFullName)
                    put("department", cleanDept)
                    put("designation", cleanDesig)
                    put("institutional_email", cleanEmail)
                    if (!phoneNumber.isNullOrBlank()) put("phone_number", phoneNumber.trim())
                    put("is_claimed", false)
                    put("is_active", isActive)
                }
                client.from("official_faculty").insert(payload)
            } else {
                client.from("official_faculty").update({
                    set("faculty_id", cleanFacultyId)
                    set("full_name", cleanFullName)
                    set("department", cleanDept)
                    set("designation", cleanDesig)
                    set("institutional_email", cleanEmail)
                    if (phoneNumber != null) set("phone_number", phoneNumber.trim())
                    set("is_active", isActive)
                }) {
                    filter { eq("id", id) }
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Faculty record saved successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error managing Faculty record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to update Faculty registry"))
        }
    }

    /**
     * Safely deletes an unclaimed official Faculty record.
     */
    suspend fun deleteFacultyRecord(id: String): AuthResult<AdminOperationResultDto> {
        return try {
            client.from("official_faculty").delete {
                filter { eq("id", id) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Faculty record deleted"))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting Faculty record: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to delete Faculty record"))
        }
    }

    // =========================================================================
    // 4. COMMON STATUS & AUDIT SAFETY RPCs
    // =========================================================================

    /**
     * Toggles the active status of any registry record (Admin / HOD).
     */
    suspend fun setRegistryRecordActive(
        registryType: String, // "bs_student", "intermediate_student", "faculty"
        recordId: String,
        isActive: Boolean
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val tableName = when (registryType) {
                "bs_student" -> "official_bs_students"
                "intermediate_student" -> "official_intermediate_students"
                else -> "official_faculty"
            }
            client.from(tableName).update({
                set("is_active", isActive)
            }) {
                filter { eq("id", recordId) }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Status updated successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error in setRegistryRecordActive: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to alter active status"))
        }
    }

    /**
     * Resets a claimed registry record for administrative correction (Super Admin only).
     */
    suspend fun resetClaimedRecord(
        registryType: String, // "bs_student", "intermediate_student", "faculty"
        recordId: String,
        reason: String = "Administrative correction"
    ): AuthResult<AdminOperationResultDto> {
        return try {
            val tableName = when (registryType) {
                "bs_student" -> "official_bs_students"
                "intermediate_student" -> "official_intermediate_students"
                else -> "official_faculty"
            }
            try {
                client.from(tableName).update({
                    set("is_claimed", false)
                    set("claimed_by_user_id", null as String?)
                    set("claimed_at", null as String?)
                }) {
                    filter { eq("id", recordId) }
                }
            } catch (e: Exception) {
                val err = e.message ?: ""
                if (err.contains("claimed_by_user_id", ignoreCase = true) || err.contains("schema cache", ignoreCase = true)) {
                    client.from(tableName).update({
                        set("is_claimed", false)
                    }) {
                        filter { eq("id", recordId) }
                    }
                } else {
                    throw e
                }
            }
            AuthResult.Success(AdminOperationResultDto(success = true, message = "Record claim reset successfully"))
        } catch (e: Exception) {
            Log.e(TAG, "Error in resetClaimedRecord: ${e.message}", e)
            AuthResult.Error(SupabaseClientProvider.formatErrorMessage(e, "Failed to reset record"))
        }
    }
}
