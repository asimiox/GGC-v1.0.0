package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.BsLoginForm
import com.example.data.model.BsRegistrationForm
import com.example.data.repository.BsAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BsAuthTab {
    SIGNUP,
    LOGIN
}

data class BsAuthUiState(
    val selectedTab: BsAuthTab = BsAuthTab.SIGNUP,
    val regForm: BsRegistrationForm = BsRegistrationForm(),
    val loginForm: BsLoginForm = BsLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

class BsAuthViewModel(
    private val repository: BsAuthRepository = BsAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BsAuthUiState())
    val uiState: StateFlow<BsAuthUiState> = _uiState.asStateFlow()

    val bsPrograms = listOf(
        "BS Information Technology",
        "BS Business Administration",
        "BS English",
        "BS Islamic Studies",
        "BS Physics",
        "BS Mathematics",
        "BS Political Science",
        "BS Urdu",
        "BS Chemistry",
        "BS Zoology"
    )

    val semesters = (1..8).map { "Semester $it" }

    fun initialize(program: String? = null, semester: String? = null) {
        if (!program.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                regForm = _uiState.value.regForm.copy(
                    program = program,
                    semester = semester ?: "Semester 1"
                )
            )
        }
    }

    fun switchTab(tab: BsAuthTab) {
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

    fun updateRegSemester(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(semester = value),
            errorMessage = null
        )
    }

    fun updateRegSession(value: String) {
        _uiState.value = _uiState.value.copy(
            regForm = _uiState.value.regForm.copy(session = value),
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
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your BS Roll Number (e.g. BSIT-2022-01).")
            return
        }
        if (form.registrationNumber.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your University Registration Number (e.g. UOG-2022-IT-001).")
            return
        }
        if (form.program.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select your BS Degree Program.")
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
            val result = repository.registerBsStudent(context, form)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = result.message ?: "BS student registered and verified successfully!"
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
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Roll No, University Reg No, or Username.")
            return
        }
        if (form.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.loginBsStudent(context, form)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Login successful!"
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
