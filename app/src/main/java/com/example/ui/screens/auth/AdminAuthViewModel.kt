package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminLoginForm
import com.example.data.model.AuthResult
import com.example.data.repository.AdminAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminAuthUiState(
    val form: AdminLoginForm = AdminLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val transferPromptData: com.example.ui.components.SessionTransferPromptData? = null
)

class AdminAuthViewModel(
    private val repository: AdminAuthRepository = AdminAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAuthUiState())
    val uiState: StateFlow<AdminAuthUiState> = _uiState.asStateFlow()

    fun updateIdentifier(value: String) {
        _uiState.value = _uiState.value.copy(
            form = _uiState.value.form.copy(identifier = value),
            errorMessage = null
        )
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            form = _uiState.value.form.copy(password = value),
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun loginAdmin(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.form
        if (form.identifier.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Administrator Username or Email.")
            return
        }
        if (form.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Administrator Password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.loginAdmin(context, form)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Administrator identity verified. Entering Super Control Center..."
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.code?.startsWith("SESSION_BLOCKED:") == true) {
                        val parts = result.code.split(":")
                        val activeDeviceName = parts.getOrNull(1)?.ifBlank { "Another Device" } ?: "Another Device"
                        val activeDeviceId = parts.getOrNull(2) ?: ""
                        val userIdentifier = parts.getOrNull(3)?.ifBlank { form.identifier } ?: form.identifier
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            transferPromptData = com.example.ui.components.SessionTransferPromptData(
                                activeDeviceName = activeDeviceName,
                                activeDeviceId = activeDeviceId,
                                userIdentifier = userIdentifier,
                                role = com.example.data.model.AppRole.ADMIN
                            )
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun dismissTransferPrompt() {
        _uiState.value = _uiState.value.copy(transferPromptData = null)
    }

    fun completeTransferLogin(context: Context, onSuccess: () -> Unit) {
        val prompt = _uiState.value.transferPromptData
        _uiState.value = _uiState.value.copy(transferPromptData = null, isLoading = true)
        viewModelScope.launch {
            if (prompt != null) {
                com.example.data.datasource.remote.ActiveSessionRemoteManager.acquireSession(
                    context = context,
                    userIdentifier = prompt.userIdentifier,
                    role = com.example.data.model.AppRole.ADMIN,
                    forceOverride = true
                )
            }
            loginAdmin(context, onSuccess)
        }
    }
}
