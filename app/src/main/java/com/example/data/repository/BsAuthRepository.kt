package com.example.data.repository

import android.content.Context
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.BsAuthRemoteDataSource
import com.example.data.model.AuthResult
import com.example.data.model.BsLoginForm
import com.example.data.model.BsRegistrationForm
import com.example.data.model.BsStudentProfileDto

class BsAuthRepository(
    private val remoteDataSource: BsAuthRemoteDataSource = BsAuthRemoteDataSource()
) {
    suspend fun checkEligibility(
        rollNumber: String,
        registrationNumber: String,
        program: String,
        username: String
    ): AuthResult<Unit> {
        return remoteDataSource.checkEligibility(rollNumber, registrationNumber, program, username)
    }

    suspend fun registerBsStudent(
        context: Context,
        form: BsRegistrationForm
    ): AuthResult<BsStudentProfileDto> {
        val result = remoteDataSource.registerBsStudent(form)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedBsProfile(
                context = context,
                firstName = profile.firstName,
                lastName = profile.lastName,
                rollNumber = profile.rollNumber,
                registrationNumber = profile.registrationNumber,
                programName = profile.program,
                semester = profile.semester,
                username = profile.username,
                userId = profile.id
            )
        }
        return result
    }

    suspend fun loginBsStudent(
        context: Context,
        form: BsLoginForm
    ): AuthResult<BsStudentProfileDto> {
        val result = remoteDataSource.loginBsStudent(form.usernameOrRoll, form.password)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedBsProfile(
                context = context,
                firstName = profile.firstName,
                lastName = profile.lastName,
                rollNumber = profile.rollNumber,
                registrationNumber = profile.registrationNumber,
                programName = profile.program,
                semester = profile.semester,
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
