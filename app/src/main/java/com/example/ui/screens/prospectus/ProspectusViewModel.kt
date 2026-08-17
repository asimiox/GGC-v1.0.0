package com.example.ui.screens.prospectus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.ProspectusDto
import com.example.data.repository.CollegeContentRepository
import com.example.data.repository.CollegeStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProspectusUiState(
    val isLoading: Boolean = false,
    val currentProspectus: ProspectusDto? = null,
    val allProspectusList: List<ProspectusDto> = emptyList(),
    val errorMessage: String? = null
)

class ProspectusViewModel(
    private val contentRepository: CollegeContentRepository = CollegeContentRepository(),
    private val storageRepository: CollegeStorageRepository = CollegeStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProspectusUiState())
    val uiState: StateFlow<ProspectusUiState> = _uiState.asStateFlow()

    init {
        loadPublishedProspectus()
    }

    fun loadPublishedProspectus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Strict read-only for students and teachers: includeUnpublished = false
            when (val result = contentRepository.getProspectusList(includeUnpublished = false)) {
                is AuthResult.Success -> {
                    val list = result.data.sortedByDescending { it.academicSession }
                    val current = list.firstOrNull { it.isCurrent } ?: list.firstOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentProspectus = current,
                            allProspectusList = list,
                            errorMessage = null
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun getProspectusDownloadUrl(storagePath: String): String {
        return storageRepository.getProspectusUrl(storagePath)
    }
}
