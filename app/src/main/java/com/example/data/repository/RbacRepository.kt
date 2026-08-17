package com.example.data.repository

import com.example.data.datasource.remote.RbacRemoteDataSource
import com.example.data.model.AppRole
import com.example.data.model.AuthResult
import com.example.data.model.UserRoleDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RbacRepository(
    private val remoteDataSource: RbacRemoteDataSource = RbacRemoteDataSource()
) {
    private val _currentRole = MutableStateFlow<AppRole?>(null)
    val currentRole: StateFlow<AppRole?> = _currentRole.asStateFlow()

    private val _userRoleDetails = MutableStateFlow<UserRoleDto?>(null)
    val userRoleDetails: StateFlow<UserRoleDto?> = _userRoleDetails.asStateFlow()

    /**
     * Synchronizes and returns the current authenticated user's role from Supabase.
     */
    suspend fun refreshCurrentRole(): AuthResult<UserRoleDto> {
        val result = remoteDataSource.getMyRole()
        if (result is AuthResult.Success) {
            _userRoleDetails.value = result.data
            _currentRole.value = result.data.appRole
        }
        return result
    }

    /**
     * Sets the local role state (e.g., after successful sign-in or profile load).
     */
    fun setLocalRole(role: AppRole, department: String? = null, userId: String? = null) {
        _currentRole.value = role
        _userRoleDetails.value = UserRoleDto(
            userId = userId,
            role = role.roleKey,
            department = department,
            authenticated = true
        )
    }

    /**
     * Clears role state upon logout.
     */
    fun clearRole() {
        _currentRole.value = null
        _userRoleDetails.value = null
    }

    fun isStudent(): Boolean = _currentRole.value?.isStudent ?: false
    fun isTeacher(): Boolean = _currentRole.value?.isTeacherLevel ?: false
    fun isHod(): Boolean = _currentRole.value?.isHodLevel ?: false
    fun isAdmin(): Boolean = _currentRole.value?.isAdminLevel ?: false

    companion object {
        val instance: RbacRepository by lazy { RbacRepository() }
    }
}
