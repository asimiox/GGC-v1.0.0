package com.example.data.repository

import com.example.data.datasource.remote.AdminHodRemoteDataSource
import com.example.data.model.AdminOperationResultDto
import com.example.data.model.AdminSystemOverviewDto
import com.example.data.model.AuthResult
import com.example.data.model.DepartmentFacultyListDto
import com.example.data.model.HodDepartmentOverviewDto

/**
 * Clean Architecture repository providing management operations for Admin and HOD users.
 */
class AdminHodRepository(
    private val remoteDataSource: AdminHodRemoteDataSource = AdminHodRemoteDataSource()
) {
    /**
     * Loads the department overview for the currently authenticated HOD.
     */
    suspend fun getHodDepartmentOverview(): AuthResult<HodDepartmentOverviewDto> {
        return remoteDataSource.getHodDepartmentOverview()
    }

    /**
     * Loads the department faculty members roster for the authenticated HOD.
     */
    suspend fun getHodDepartmentFaculty(): AuthResult<DepartmentFacultyListDto> {
        return remoteDataSource.getHodDepartmentFaculty()
    }

    /**
     * Loads college-wide system summary stats for system administrators.
     */
    suspend fun getAdminSystemOverview(): AuthResult<AdminSystemOverviewDto> {
        return remoteDataSource.getAdminSystemOverview()
    }

    /**
     * Assigns/promotes a verified faculty member to HOD for a department.
     */
    suspend fun assignHod(facultyUserId: String, departmentName: String): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.assignHod(facultyUserId, departmentName)
    }

    /**
     * Revokes HOD privileges from a user and reverts them to Teacher role.
     */
    suspend fun revokeHod(targetUserId: String): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.revokeHod(targetUserId)
    }

    /**
     * Creates and provisions an HOD account with Name, Department, HOD ID, and Password (default 00000).
     */
    suspend fun createOrAppointHod(
        name: String,
        department: String,
        hodId: String,
        password: String = "00000"
    ): AuthResult<AdminOperationResultDto> {
        return remoteDataSource.createOrAppointHod(name, department, hodId, password)
    }
}
