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
    val selectedTab: IntermediateAuthTab = IntermediateAuthTab.LOGIN,
    val regForm: IntermediateRegistrationForm = IntermediateRegistrationForm(),
    val loginForm: IntermediateLoginForm = IntermediateLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLockedOut: Boolean = false,
    val lockoutRemainingTime: String? = null,
    val transferPromptData: com.example.ui.components.SessionTransferPromptData? = null
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

    private var countdownJob: kotlinx.coroutines.Job? = null

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

    fun updateLoginUsernameOrRoll(value: String, context: Context? = null) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(usernameOrRoll = value),
            errorMessage = if (_uiState.value.isLockedOut) _uiState.value.errorMessage else null
        )
        if (context != null) {
            checkLockoutStatus(context, value)
        }
    }

    fun updateLoginPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(password = value),
            errorMessage = if (_uiState.value.isLockedOut) _uiState.value.errorMessage else null
        )
    }

    fun checkLockoutStatus(context: Context, idInput: String? = null) {
        val id = idInput ?: _uiState.value.loginForm.usernameOrRoll
        if (id.isNotBlank() && com.example.data.datasource.LoginAttemptManager.isBlocked(context, id)) {
            val remaining = com.example.data.datasource.LoginAttemptManager.getRemainingBlockTimeMs(context, id)
            val formatted = com.example.data.datasource.LoginAttemptManager.formatRemainingTime(remaining)
            _uiState.value = _uiState.value.copy(
                isLockedOut = true,
                lockoutRemainingTime = formatted,
                errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
            )
            startCountdownTimer(context, id)
        } else if (_uiState.value.isLockedOut && (id.isBlank() || !com.example.data.datasource.LoginAttemptManager.isBlocked(context, id))) {
            stopCountdownTimer()
            _uiState.value = _uiState.value.copy(
                isLockedOut = false,
                lockoutRemainingTime = null,
                errorMessage = null
            )
        }
    }

    private fun startCountdownTimer(context: Context, id: String) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val remaining = com.example.data.datasource.LoginAttemptManager.getRemainingBlockTimeMs(context, id)
                if (remaining <= 0) {
                    com.example.data.datasource.LoginAttemptManager.clearLockout(context, id)
                    _uiState.value = _uiState.value.copy(
                        isLockedOut = false,
                        lockoutRemainingTime = null,
                        errorMessage = null
                    )
                    break
                }
                val formatted = com.example.data.datasource.LoginAttemptManager.formatRemainingTime(remaining)
                _uiState.value = _uiState.value.copy(
                    isLockedOut = true,
                    lockoutRemainingTime = formatted,
                    errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
                )
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    private fun stopCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = null
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
        val identifier = form.usernameOrRoll.trim()

        if (identifier.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your College Roll Number or Registration Number.")
            return
        }

        // Check if currently locked out
        if (com.example.data.datasource.LoginAttemptManager.isBlocked(context, identifier)) {
            val remaining = com.example.data.datasource.LoginAttemptManager.getRemainingBlockTimeMs(context, identifier)
            val formatted = com.example.data.datasource.LoginAttemptManager.formatRemainingTime(remaining)
            _uiState.value = _uiState.value.copy(
                isLockedOut = true,
                lockoutRemainingTime = formatted,
                errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
            )
            startCountdownTimer(context, identifier)
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
                    com.example.data.datasource.LoginAttemptManager.recordSuccessfulLogin(context, identifier)
                    stopCountdownTimer()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLockedOut = false,
                        lockoutRemainingTime = null,
                        successMessage = result.message ?: "Login successful!"
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.code?.startsWith("SESSION_BLOCKED:") == true) {
                        val parts = result.code.split(":")
                        val activeDeviceName = parts.getOrNull(1)?.ifBlank { "Another Device" } ?: "Another Device"
                        val activeDeviceId = parts.getOrNull(2) ?: ""
                        val userIdentifier = parts.getOrNull(3)?.ifBlank { form.usernameOrRoll } ?: form.usernameOrRoll
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            transferPromptData = com.example.ui.components.SessionTransferPromptData(
                                activeDeviceName = activeDeviceName,
                                activeDeviceId = activeDeviceId,
                                userIdentifier = userIdentifier,
                                role = com.example.data.model.AppRole.STUDENT_INTERMEDIATE
                            )
                        )
                    } else {
                        val attemptResult = com.example.data.datasource.LoginAttemptManager.recordFailedAttempt(context, identifier)
                        when (attemptResult) {
                            is com.example.data.datasource.LoginAttemptManager.AttemptResult.Failed -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isLockedOut = false,
                                    lockoutRemainingTime = null,
                                    errorMessage = attemptResult.message
                                )
                            }
                            is com.example.data.datasource.LoginAttemptManager.AttemptResult.Blocked -> {
                                val formatted = com.example.data.datasource.LoginAttemptManager.formatRemainingTime(attemptResult.remainingMs)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isLockedOut = true,
                                    lockoutRemainingTime = formatted,
                                    errorMessage = attemptResult.message
                                )
                                startCountdownTimer(context, identifier)
                            }
                        }
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
                    role = com.example.data.model.AppRole.STUDENT_INTERMEDIATE,
                    forceOverride = true
                )
            }
            loginStudent(context, onSuccess)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopCountdownTimer()
    }
}
