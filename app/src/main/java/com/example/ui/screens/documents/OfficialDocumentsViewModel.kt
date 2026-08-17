package com.example.ui.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.OfficialDocumentDto
import com.example.data.repository.CollegeContentRepository
import com.example.data.repository.CollegeStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DocumentsUiState(
    val isLoading: Boolean = false,
    val documents: List<OfficialDocumentDto> = emptyList(),
    val filteredDocuments: List<OfficialDocumentDto> = emptyList(),
    val selectedType: String = "All",
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class OfficialDocumentsViewModel(
    private val contentRepository: CollegeContentRepository = CollegeContentRepository(),
    private val storageRepository: CollegeStorageRepository = CollegeStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentsUiState())
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    init {
        loadPublishedDocuments()
    }

    fun loadPublishedDocuments(initialType: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (initialType != null) {
                _uiState.update { it.copy(selectedType = initialType) }
            }
            // Strict read-only for students and teachers: includeUnpublished = false
            when (val result = contentRepository.getOfficialDocuments(includeUnpublished = false)) {
                is AuthResult.Success -> {
                    val list = result.data.sortedByDescending { it.createdAt ?: "" }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            documents = list,
                            filteredDocuments = applyFilters(list, state.selectedType, state.searchQuery),
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

    fun setSelectedType(type: String) {
        _uiState.update { state ->
            state.copy(
                selectedType = type,
                filteredDocuments = applyFilters(state.documents, type, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredDocuments = applyFilters(state.documents, state.selectedType, query)
            )
        }
    }

    fun getDocumentUrl(storagePath: String): String {
        return storageRepository.getOfficialDocumentUrl(storagePath)
    }

    private fun applyFilters(
        list: List<OfficialDocumentDto>,
        type: String,
        query: String
    ): List<OfficialDocumentDto> {
        val cleanQuery = query.trim()
        return list.filter { doc ->
            val matchesType = when (type) {
                "All" -> true
                "Rules" -> doc.documentType.equals("rules_regulations", ignoreCase = true) || doc.documentType.contains("rules", ignoreCase = true)
                "Fee Structure" -> doc.documentType.equals("fee_structure", ignoreCase = true) || doc.title.contains("fee", ignoreCase = true)
                "Academic" -> doc.documentType.equals("academic_notice", ignoreCase = true) || doc.documentType.contains("academic", ignoreCase = true)
                "Examinations" -> doc.documentType.equals("examination", ignoreCase = true)
                "Forms" -> doc.documentType.equals("form", ignoreCase = true)
                "Admissions" -> doc.documentType.equals("admission", ignoreCase = true)
                else -> doc.documentType.equals(type, ignoreCase = true)
            }

            val matchesQuery = if (cleanQuery.isEmpty()) true else {
                doc.title.contains(cleanQuery, ignoreCase = true) ||
                (doc.description?.contains(cleanQuery, ignoreCase = true) == true) ||
                doc.fileName.contains(cleanQuery, ignoreCase = true) ||
                (doc.academicSession?.contains(cleanQuery, ignoreCase = true) == true)
            }

            matchesType && matchesQuery
        }
    }
}
