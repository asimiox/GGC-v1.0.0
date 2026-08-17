package com.example.data.repository

import android.content.Context
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.IntermediateAuthRemoteDataSource
import com.example.data.model.AuthResult
import com.example.data.model.IntermediateLoginForm
import com.example.data.model.IntermediateRegistrationForm
import com.example.data.model.IntermediateStudentProfileDto

class IntermediateAuthRepository(
    private val remoteDataSource: IntermediateAuthRemoteDataSource = IntermediateAuthRemoteDataSource()
) {
    suspend fun checkEligibility(
        rollNumber: String,
        registrationNumber: String,
        program: String,
        username: String
    ): AuthResult<Unit> {
        return remoteDataSource.checkEligibility(rollNumber, registrationNumber, program, username)
    }

    suspend fun registerIntermediateStudent(
        context: Context,
        form: IntermediateRegistrationForm
    ): AuthResult<IntermediateStudentProfileDto> {
        val result = remoteDataSource.registerIntermediateStudent(form)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedIntermediateProfile(
                context = context,
                firstName = profile.firstName,
                lastName = profile.lastName,
                rollNumber = profile.rollNumber,
                registrationNumber = profile.registrationNumber,
                programName = profile.program,
                username = profile.username,
                userId = profile.id
            )
        }
        return result
    }

    suspend fun loginIntermediateStudent(
        context: Context,
        form: IntermediateLoginForm
    ): AuthResult<IntermediateStudentProfileDto> {
        val result = remoteDataSource.loginIntermediateStudent(form.usernameOrRoll, form.password)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedIntermediateProfile(
                context = context,
                firstName = profile.firstName,
                lastName = profile.lastName,
                rollNumber = profile.rollNumber,
                registrationNumber = profile.registrationNumber,
                programName = profile.program,
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
