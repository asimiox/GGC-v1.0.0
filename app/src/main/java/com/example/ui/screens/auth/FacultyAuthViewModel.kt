package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.OfficialFacultyData
import com.example.data.model.AuthResult
import com.example.data.model.FacultyLoginForm
import com.example.data.repository.FacultyAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FacultyAuthUiState(
    val loginForm: FacultyLoginForm = FacultyLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false
)

class FacultyAuthViewModel(
    private val repository: FacultyAuthRepository = FacultyAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FacultyAuthUiState())
    val uiState: StateFlow<FacultyAuthUiState> = _uiState.asStateFlow()

    val departments = OfficialFacultyData.getAllDepartments()

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun updateLoginUsernameOrFacultyId(value: String) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(usernameOrFacultyId = value),
            errorMessage = null
        )
    }

    fun updateLoginPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(password = value),
            errorMessage = null
        )
    }

    fun loginFaculty(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.loginForm
        if (form.usernameOrFacultyId.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Faculty ID, Username, or Institutional Email.")
            return
        }
        if (form.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.loginFaculty(context, form)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Faculty portal login successful!"
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
