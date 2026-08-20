package com.example.data.repository

import android.content.Context
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.AdminAuthRemoteDataSource
import com.example.data.model.AdminLoginForm
import com.example.data.model.AdminProfileDto
import com.example.data.model.AuthResult

class AdminAuthRepository(
    private val remoteDataSource: AdminAuthRemoteDataSource = AdminAuthRemoteDataSource()
) {
    suspend fun loginAdmin(
        context: Context,
        form: AdminLoginForm
    ): AuthResult<AdminProfileDto> {
        val result = remoteDataSource.loginAdmin(form.identifier, form.password)
        if (result is AuthResult.Success) {
            val profile = result.data
            UserProfileManager.saveVerifiedAdminProfile(
                context = context,
                fullName = profile.fullName,
                username = profile.username,
                institutionalEmail = profile.email,
                userId = profile.id
            )
        }
        return result
    }

    suspend fun logout(context: Context) {
        UserProfileManager.clearProfile(context)
    }
}
