package com.example.data.repository

import com.example.data.datasource.remote.OfficialRegistryRemoteDataSource
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AuthResult
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto

/**
 * Clean Architecture repository providing type-safe administrative management of college registries.
 */
class OfficialRegistryRepository(
    private val remoteDataSource: OfficialRegistryRemoteDataSource = OfficialRegistryRemoteDataSource()
) {

    // =========================================================================
    // 1. BS STUDENTS REGISTRY
    // =========================================================================

    suspend fun getOfficialBsStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 50,
        offset: Long = 0
    ): AuthResult<List<OfficialBsStudentDto>> {
        return remoteDataSource.fetchOfficialBsStudents(program, isClaimed, isActive, searchQuery, limit, offset)
    }

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
        return remoteDataSource.manageBsStudentRecord(
            id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
        )
    }

    suspend fun deleteBsStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.deleteBsStudentRecord(id)
    }

    // =========================================================================
    // 2. INTERMEDIATE STUDENTS REGISTRY
    // =========================================================================

    suspend fun getOfficialIntermediateStudents(
        program: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 50,
        offset: Long = 0
    ): AuthResult<List<OfficialIntermediateStudentDto>> {
        return remoteDataSource.fetchOfficialIntermediateStudents(program, isClaimed, isActive, searchQuery, limit, offset)
    }

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
        return remoteDataSource.manageIntermediateStudentRecord(
            id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
        )
    }

    suspend fun deleteIntermediateStudentRecord(id: String): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.deleteIntermediateStudentRecord(id)
    }

    // =========================================================================
    // 3. FACULTY REGISTRY
    // =========================================================================

    suspend fun getOfficialFaculty(
        department: String? = null,
        isClaimed: Boolean? = null,
        isActive: Boolean? = null,
        searchQuery: String? = null,
        limit: Long = 50,
        offset: Long = 0
    ): AuthResult<List<OfficialFacultyRegistryDto>> {
        return remoteDataSource.fetchOfficialFaculty(department, isClaimed, isActive, searchQuery, limit, offset)
    }

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
        return remoteDataSource.manageFacultyRecord(
            id, facultyId, fullName, department, designation, qualification,
            institutionalEmail, phoneNumber, firstName, lastName, isActive
        )
    }

    suspend fun deleteFacultyRecord(id: String): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.deleteFacultyRecord(id)
    }

    // =========================================================================
    // 4. COMMON STATUS & AUDIT SAFETY
    // =========================================================================

    suspend fun setRegistryRecordActive(
        registryType: String,
        recordId: String,
        isActive: Boolean
    ): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.setRegistryRecordActive(registryType, recordId, isActive)
    }

    suspend fun resetClaimedRecord(
        registryType: String,
        recordId: String,
        reason: String = "Administrative correction"
    ): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.resetClaimedRecord(registryType, recordId, reason)
    }
}
