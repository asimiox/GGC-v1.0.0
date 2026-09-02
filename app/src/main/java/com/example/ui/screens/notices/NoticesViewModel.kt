package com.example.ui.screens.notices

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AnnouncementDto
import com.example.data.model.AuthResult
import com.example.data.repository.CollegeContentRepository
import com.example.data.repository.CollegeStorageRepository
import com.example.data.repository.PostAnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoticesUiState(
    val isLoading: Boolean = false,
    val notices: List<AnnouncementDto> = emptyList(),
    val filteredNotices: List<AnnouncementDto> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val selectedNotice: AnnouncementDto? = null
)

class NoticesViewModel(
    private val contentRepository: CollegeContentRepository = CollegeContentRepository(),
    private val storageRepository: CollegeStorageRepository = CollegeStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoticesUiState())
    val uiState: StateFlow<NoticesUiState> = _uiState.asStateFlow()

    init {
        loadPublishedNotices()
    }

    fun loadPublishedNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Strict read-only for students and teachers: includeUnpublished = false
            when (val result = contentRepository.getAnnouncements(includeUnpublished = false)) {
                is AuthResult.Success -> {
                    val list = result.data.sortedWith(
                        compareByDescending<AnnouncementDto> { it.isPinned }
                            .thenByDescending { it.publishedAt ?: it.createdAt ?: "" }
                    )
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            notices = list,
                            filteredNotices = applyFilters(list, state.selectedCategory, state.searchQuery),
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

    fun setCategory(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredNotices = applyFilters(state.notices, category, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredNotices = applyFilters(state.notices, state.selectedCategory, query)
            )
        }
    }

    fun selectNotice(notice: AnnouncementDto?) {
        _uiState.update { it.copy(selectedNotice = notice) }
    }

    fun selectNotice(context: Context, notice: AnnouncementDto?) {
        _uiState.update { it.copy(selectedNotice = notice) }
        if (notice != null) {
            viewModelScope.launch {
                try {
                    PostAnalyticsRepository.getInstance(context).recordPostView(
                        postId = notice.id ?: notice.title,
                        postTitle = notice.title,
                        postCategory = notice.category
                    )
                } catch (e: Exception) {
                    Log.e("NoticesViewModel", "Failed to record post view", e)
                }
            }
        }
    }

    fun getAttachmentUrl(storagePath: String?): String? {
        if (storagePath.isNullOrBlank()) return null
        return storageRepository.getAnnouncementAttachmentUrl(storagePath)
    }

    private fun applyFilters(
        list: List<AnnouncementDto>,
        category: String,
        query: String
    ): List<AnnouncementDto> {
        return list.filter { notice ->
            val matchesCategory = if (category == "All") true
            else notice.category.equals(category, ignoreCase = true)

            val matchesQuery = if (query.isBlank()) true
            else {
                notice.title.contains(query, ignoreCase = true) ||
                notice.content.contains(query, ignoreCase = true) ||
                notice.authorName.contains(query, ignoreCase = true)
            }

            matchesCategory && matchesQuery
        }
    }
}
