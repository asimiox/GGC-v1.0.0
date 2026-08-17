package com.example.data.repository

import com.example.data.datasource.remote.OfficialRegistryRemoteDataSource
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AuthResult

/**
 * Clean Architecture repository providing type-safe administrative management of college registries.
 */
class OfficialRegistryRepository(
    private val remoteDataSource: OfficialRegistryRemoteDataSource = OfficialRegistryRemoteDataSource()
) {
    /**
     * Admin: Insert or update an official BS student record.
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
        return remoteDataSource.manageBsStudentRecord(
            id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
        )
    }

    /**
     * Admin: Insert or update an official Intermediate student record.
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
        return remoteDataSource.manageIntermediateStudentRecord(
            id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
        )
    }

    /**
     * Admin: Insert or update an official Faculty record.
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
        return remoteDataSource.manageFacultyRecord(
            id, facultyId, fullName, department, designation, qualification,
            institutionalEmail, phoneNumber, firstName, lastName, isActive
        )
    }

    /**
     * Admin: Toggle active status on any registry record.
     */
    suspend fun setRegistryRecordActive(
        registryType: String,
        recordId: String,
        isActive: Boolean
    ): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.setRegistryRecordActive(registryType, recordId, isActive)
    }

    /**
     * Admin: Reset an erroneously claimed registry record so it can be reclaimed by its rightful owner.
     */
    suspend fun resetClaimedRecord(
        registryType: String,
        recordId: String,
        reason: String = "Administrative correction"
    ): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.resetClaimedRecord(registryType, recordId, reason)
    }
}
