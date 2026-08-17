package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.OfficialFacultyData
import com.example.data.model.AuthResult
import com.example.data.model.FacultyLoginForm
import com.example.data.model.FacultyRegistrationForm
import com.example.data.repository.FacultyAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FacultyAuthTab {
    SIGNUP,
    LOGIN
}

data class FacultyAuthUiState(
    val selectedTab: FacultyAuthTab = FacultyAuthTab.LOGIN,
    val regForm: FacultyRegistrationForm = FacultyRegistrationForm(),
    val loginForm: FacultyLoginForm = FacultyLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val verifiedFacultyName: String? = null,
    val verifiedDesignation: String? = null
)

class FacultyAuthViewModel(
    private val repository: FacultyAuthRepository = FacultyAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FacultyAuthUiState())
    val uiState: StateFlow<FacultyAuthUiState> = _uiState.asStateFlow()

    val departments = OfficialFacultyData.getAllDepartments()

    fun switchTab(tab: FacultyAuthTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            errorMessage = null,
            successMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun updateRegFacultyId(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(facultyId = value),
            errorMessage = null
        )
    }

    fun updateRegFullName(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(fullName = value),
            errorMessage = null
        )
    }

    fun updateRegDepartment(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(department = value),
            errorMessage = null
        )
    }

    fun updateRegEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(institutionalEmail = value),
            errorMessage = null
        )
    }

    fun updateRegPhone(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(phoneNumber = value),
            errorMessage = null
        )
    }

    fun updateRegUsername(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(username = value),
            errorMessage = null
        )
    }

    fun updateRegPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(password = value),
            errorMessage = null
        )
    }

    fun updateRegConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(confirmPassword = value),
            errorMessage = null
        )
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

    fun registerFaculty(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.regForm

        // Client-side validation checks
        if (form.facultyId.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Official Faculty ID (e.g. FAC-01).")
            return
        }
        if (form.fullName.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Full Name as listed in the college directory.")
            return
        }
        if (form.department.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select your Academic Department.")
            return
        }
        if (form.username.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please choose a Username.")
            return
        }
        if (form.username.trim().length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "Username must be at least 3 characters.")
            return
        }
        if (form.password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }
        if (form.password != form.confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.registerFaculty(context, form)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = result.message ?: "Faculty identity verified and account created successfully!"
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
