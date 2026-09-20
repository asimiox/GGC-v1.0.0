package com.example.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatBotRole
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRole
import com.example.data.model.GeminiModelType
import com.example.data.repository.GeminiChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GeminiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val selectedModel: GeminiModelType = GeminiModelType.GENERAL,
    val selectedRole: ChatBotRole = ChatBotRole.CAMPUS_ADVISOR,
    val customApiKey: String = "",
    val isApiKeyValid: Boolean = true,
    val errorMessage: String? = null
)

class GeminiChatViewModel(
    private val repository: GeminiChatRepository = GeminiChatRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GeminiChatUiState(
            isApiKeyValid = repository.isApiKeyConfigured()
        )
    )
    val uiState: StateFlow<GeminiChatUiState> = _uiState.asStateFlow()

    init {
        // Post initial welcome message from the active role
        resetWithWelcomeMessage(_uiState.value.selectedRole)
    }

    private fun resetWithWelcomeMessage(role: ChatBotRole) {
        val welcomeGreeting = when (role) {
            ChatBotRole.CAMPUS_ADVISOR ->
                "Hello! 👋 I'm your official GGC AI Campus Advisor. Ask me anything about our BS programs, admission criteria, college facilities, or campus life at Government Graduate College Mandi Bahauddin!"
            ChatBotRole.STUDY_TUTOR ->
                "Welcome to Study Tutor! 📚 I can help explain tricky concepts, solve step-by-step math and science problems, review your code, or prepare effective revision guides for your exams."
            ChatBotRole.QUICK_HELPER ->
                "Quick FAQ Mode active! ⚡ Ask any quick question regarding college timings, departments, or guidelines for instant answers."
            ChatBotRole.CAREER_COUNSELOR ->
                "Welcome! 💼 Let's discuss your future roadmap—from university admissions (MS/MPhil) and scholarships to civil service preparation and high-demand tech careers."
        }

        _uiState.update { current ->
            current.copy(
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.MODEL,
                        text = welcomeGreeting,
                        modelType = current.selectedModel,
                        roleUsedName = role.title
                    )
                ),
                errorMessage = null
            )
        }
    }

    fun selectModel(model: GeminiModelType) {
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun selectRole(role: ChatBotRole) {
        if (_uiState.value.selectedRole == role) return
        _uiState.update { it.copy(selectedRole = role) }

        // If conversation only contains the initial greeting, refresh greeting for the new role
        if (_uiState.value.messages.size <= 1) {
            resetWithWelcomeMessage(role)
        } else {
            // Append a small system transition note
            val switchNote = ChatMessage(
                role = ChatRole.MODEL,
                text = "Switched to ${role.title} mode (${role.iconEmoji}). How can I help you in this role?",
                modelType = _uiState.value.selectedModel,
                roleUsedName = role.title
            )
            _uiState.update { it.copy(messages = it.messages + switchNote) }
        }
    }

    fun setCustomApiKey(key: String) {
        _uiState.update {
            it.copy(
                customApiKey = key.trim(),
                isApiKeyValid = repository.isApiKeyConfigured(key.trim())
            )
        }
    }

    fun clearChat() {
        resetWithWelcomeMessage(_uiState.value.selectedRole)
    }

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(
            role = ChatRole.USER,
            text = trimmed
        )

        val updatedMessages = _uiState.value.messages + userMessage
        _uiState.update {
            it.copy(
                messages = updatedMessages,
                isLoading = true,
                errorMessage = null
            )
        }

        val activeRole = _uiState.value.selectedRole
        val activeModel = _uiState.value.selectedModel
        val customKey = _uiState.value.customApiKey.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            val result = repository.sendMessage(
                history = updatedMessages,
                newPrompt = trimmed,
                role = activeRole,
                modelType = activeModel,
                customApiKey = customKey
            )

            result.fold(
                onSuccess = { replyText ->
                    val botMessage = ChatMessage(
                        role = ChatRole.MODEL,
                        text = replyText,
                        modelType = activeModel,
                        roleUsedName = activeRole.title
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + botMessage,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessageText = error.message ?: "Failed to generate response. Please try again."
                    val errorBotMessage = ChatMessage(
                        role = ChatRole.MODEL,
                        text = "⚠️ $errorMessageText",
                        modelType = activeModel,
                        isError = true,
                        roleUsedName = activeRole.title
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + errorBotMessage,
                            isLoading = false,
                            errorMessage = errorMessageText
                        )
                    }
                }
            )
        }
    }

    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == ChatRole.USER }
        if (lastUserMessage != null) {
            sendMessage(lastUserMessage.text)
        }
    }
}
