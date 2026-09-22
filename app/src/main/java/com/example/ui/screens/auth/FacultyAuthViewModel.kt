package com.example.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.LoginAttemptManager
import com.example.data.datasource.OfficialFacultyData
import com.example.data.model.AuthResult
import com.example.data.model.FacultyLoginForm
import com.example.data.repository.FacultyAuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FacultyAuthUiState(
    val loginForm: FacultyLoginForm = FacultyLoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLockedOut: Boolean = false,
    val lockoutRemainingTime: String? = null,
    val transferPromptData: com.example.ui.components.SessionTransferPromptData? = null
)

class FacultyAuthViewModel(
    private val repository: FacultyAuthRepository = FacultyAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FacultyAuthUiState())
    val uiState: StateFlow<FacultyAuthUiState> = _uiState.asStateFlow()

    val departments = OfficialFacultyData.getAllDepartments()
    private var countdownJob: Job? = null

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun updateLoginUsernameOrFacultyId(value: String, context: Context? = null) {
        _uiState.value = _uiState.value.copy(
            loginForm = _uiState.value.loginForm.copy(usernameOrFacultyId = value),
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
        val id = idInput ?: _uiState.value.loginForm.usernameOrFacultyId
        if (id.isNotBlank() && LoginAttemptManager.isBlocked(context, id)) {
            val remaining = LoginAttemptManager.getRemainingBlockTimeMs(context, id)
            val formatted = LoginAttemptManager.formatRemainingTime(remaining)
            _uiState.value = _uiState.value.copy(
                isLockedOut = true,
                lockoutRemainingTime = formatted,
                errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
            )
            startCountdownTimer(context, id)
        } else if (_uiState.value.isLockedOut && (id.isBlank() || !LoginAttemptManager.isBlocked(context, id))) {
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
                val remaining = LoginAttemptManager.getRemainingBlockTimeMs(context, id)
                if (remaining <= 0) {
                    LoginAttemptManager.clearLockout(context, id)
                    _uiState.value = _uiState.value.copy(
                        isLockedOut = false,
                        lockoutRemainingTime = null,
                        errorMessage = null
                    )
                    break
                }
                val formatted = LoginAttemptManager.formatRemainingTime(remaining)
                _uiState.value = _uiState.value.copy(
                    isLockedOut = true,
                    lockoutRemainingTime = formatted,
                    errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
                )
                delay(1000L)
            }
        }
    }

    private fun stopCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = null
    }

    fun loginFaculty(context: Context, onSuccess: () -> Unit) {
        val form = _uiState.value.loginForm
        val identifier = form.usernameOrFacultyId.trim()

        if (identifier.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Faculty ID, Username, or Institutional Email.")
            return
        }

        // Check if currently locked out
        if (LoginAttemptManager.isBlocked(context, identifier)) {
            val remaining = LoginAttemptManager.getRemainingBlockTimeMs(context, identifier)
            val formatted = LoginAttemptManager.formatRemainingTime(remaining)
            _uiState.value = _uiState.value.copy(
                isLockedOut = true,
                lockoutRemainingTime = formatted,
                errorMessage = "Account blocked for 24 hours. Time remaining: $formatted"
            )
            startCountdownTimer(context, identifier)
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
                    LoginAttemptManager.recordSuccessfulLogin(context, identifier)
                    stopCountdownTimer()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLockedOut = false,
                        lockoutRemainingTime = null,
                        successMessage = "Faculty portal login successful!"
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.code?.startsWith("SESSION_BLOCKED:") == true) {
                        val parts = result.code.split(":")
                        val activeDeviceName = parts.getOrNull(1)?.ifBlank { "Another Device" } ?: "Another Device"
                        val activeDeviceId = parts.getOrNull(2) ?: ""
                        val userIdentifier = parts.getOrNull(3)?.ifBlank { form.usernameOrFacultyId } ?: form.usernameOrFacultyId
                        val roleKey = parts.getOrNull(4)
                        val sessionRole = when (roleKey) {
                            "admin" -> com.example.data.model.AppRole.ADMIN
                            "hod" -> com.example.data.model.AppRole.HOD
                            else -> com.example.data.model.AppRole.TEACHER
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            transferPromptData = com.example.ui.components.SessionTransferPromptData(
                                activeDeviceName = activeDeviceName,
                                activeDeviceId = activeDeviceId,
                                userIdentifier = userIdentifier,
                                role = sessionRole
                            )
                        )
                    } else {
                        // Record failed credential attempt
                        val attemptResult = LoginAttemptManager.recordFailedAttempt(context, identifier)
                        when (attemptResult) {
                            is LoginAttemptManager.AttemptResult.Failed -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isLockedOut = false,
                                    lockoutRemainingTime = null,
                                    errorMessage = attemptResult.message
                                )
                            }
                            is LoginAttemptManager.AttemptResult.Blocked -> {
                                val formatted = LoginAttemptManager.formatRemainingTime(attemptResult.remainingMs)
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
                    role = com.example.data.model.AppRole.TEACHER,
                    forceOverride = true
                )
            }
            loginFaculty(context, onSuccess)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopCountdownTimer()
    }
}
