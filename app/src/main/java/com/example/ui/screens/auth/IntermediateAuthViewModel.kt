package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.IntermediateLoginForm
import com.example.data.model.IntermediateRegistrationForm
import com.example.data.repository.IntermediateAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class IntermediateAuthTab {
    SIGNUP,
    LOGIN
}

data class IntermediateAuthUiState(
    val selectedTab: IntermediateAuthTab = IntermediateAuthTab.SIGNUP,
    val regForm: IntermediateRegistrationForm = IntermediateRegistrationForm(),
    val loginForm: IntermediateLoginForm = IntermediateLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

class IntermediateAuthViewModel(
    private val repository: IntermediateAuthRepository = IntermediateAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(IntermediateAuthUiState())
    val uiState: StateFlow<IntermediateAuthUiState> = _uiState.asStateFlow()

    val intermediatePrograms = listOf(
        "F.Sc Pre-Med",
        "F.Sc Pre-Eng",
        "ICs",
        "ICom",
        "FA",
        "FA.IT"
    )

    fun switchTab(tab: IntermediateAuthTab) {
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

    fun updateRegFirstName(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(firstName = value),
            errorMessage = null
        )
    }

    fun updateRegLastName(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(lastName = value),
            errorMessage = null
        )
    }

    fun updateRegRollNumber(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(rollNumber = value),
            errorMessage = null
        )
    }

    fun updateRegRegistrationNumber(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(registrationNumber = value),
            errorMessage = null
        )
    }

    fun updateRegProgram(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(program = value),
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

    fun updateLoginUsernameOrRoll(value: String) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(usernameOrRoll = value),
            errorMessage = null
        )
    }

    fun updateLoginPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(password = value),
            errorMessage = null
        )
    }

    fun registerStudent(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.regForm

        // Client-side validation checks
        if (form.firstName.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your First Name.")
            return
        }
        if (form.lastName.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Last Name.")
            return
        }
        if (form.rollNumber.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your College Roll Number.")
            return
        }
        if (form.registrationNumber.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your College Registration Number.")
            return
        }
        if (form.program.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select your Program/Class.")
            return
        }
        if (form.username.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please choose a Username.")
            return
        }
        if (form.username.trim().length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "Username must be at least 3 characters long.")
            return
        }
        if (form.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a Password.")
            return
        }
        if (form.password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters long.")
            return
        }
        if (form.password != form.confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match. Please re-check.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            when (val result = repository.registerIntermediateStudent(context, form)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = result.message ?: "Account created successfully!"
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

    fun loginStudent(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.loginForm
        if (form.usernameOrRoll.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Username or Roll Number.")
            return
        }
        if (form.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            when (val result = repository.loginIntermediateStudent(context, form)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = result.message ?: "Login successful!"
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
