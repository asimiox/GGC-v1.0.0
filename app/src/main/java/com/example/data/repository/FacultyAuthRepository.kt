package com.example.data.repository

import android.content.Context
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.FacultyAuthRemoteDataSource
import com.example.data.model.AuthResult
import com.example.data.model.FacultyLoginForm
import com.example.data.model.FacultyProfileDto
import com.example.data.model.FacultyRegistrationForm
import com.example.data.model.OfficialFacultyDto

class FacultyAuthRepository(
    private val remoteDataSource: FacultyAuthRemoteDataSource = FacultyAuthRemoteDataSource()
) {
    suspend fun checkEligibility(
        facultyId: String,
        department: String,
        username: String,
        institutionalEmail: String? = null
    ): AuthResult<OfficialFacultyDto> {
        return remoteDataSource.checkEligibility(facultyId, department, username, institutionalEmail)
    }

    suspend fun registerFaculty(
        context: Context,
        form: FacultyRegistrationForm
    ): AuthResult<FacultyProfileDto> {
        val result = remoteDataSource.registerFaculty(form)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedFacultyProfile(
                context = context,
                fullName = profile.fullName,
                department = profile.department,
                designation = profile.designation,
                qualification = profile.qualification,
                facultyId = profile.facultyId,
                institutionalEmail = profile.institutionalEmail,
                username = profile.username,
                userId = profile.id
            )
        }
        return result
    }

    suspend fun loginFaculty(
        context: Context,
        form: FacultyLoginForm
    ): AuthResult<FacultyProfileDto> {
        val result = remoteDataSource.loginFaculty(form.usernameOrFacultyId, form.password)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedFacultyProfile(
                context = context,
                fullName = profile.fullName,
                department = profile.department,
                designation = profile.designation,
                qualification = profile.qualification,
                facultyId = profile.facultyId,
                institutionalEmail = profile.institutionalEmail,
                username = profile.username,
                userId = profile.id
            )
        }
        return result
    }

    suspend fun logout(context: Context) {
        remoteDataSource.logout()
        UserProfileManager.clearProfile(context)
    }
}
