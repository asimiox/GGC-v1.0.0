package com.example.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.repository.CollegeContentRepository
import com.example.data.repository.CollegeStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventsUiState(
    val isLoading: Boolean = false,
    val upcomingEvents: List<CollegeEventDto> = emptyList(),
    val pastEvents: List<CollegeEventDto> = emptyList(),
    val selectedTab: Int = 0, // 0 = Upcoming, 1 = Past
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedEvent: CollegeEventDto? = null,
    val errorMessage: String? = null
)

class EventsViewModel(
    private val contentRepository: CollegeContentRepository = CollegeContentRepository(),
    private val storageRepository: CollegeStorageRepository = CollegeStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadPublishedEvents()
    }

    fun loadPublishedEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Strict read-only for students and teachers: includeUnpublished = false
            when (val result = contentRepository.getEvents(includeUnpublished = false)) {
                is AuthResult.Success -> {
                    val all = result.data
                    val upcoming = all.filter { it.isUpcoming }.sortedBy { it.eventDate }
                    val past = all.filter { !it.isUpcoming }.sortedByDescending { it.eventDate }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            upcomingEvents = upcoming,
                            pastEvents = past,
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

    fun setSelectedTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectEvent(event: CollegeEventDto?) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun getBannerUrl(bannerPath: String?): String? {
        if (bannerPath.isNullOrBlank()) return null
        return storageRepository.getCollegeMediaUrl(bannerPath)
    }

    fun getFilteredEvents(): List<CollegeEventDto> {
        val currentList = if (_uiState.value.selectedTab == 0) _uiState.value.upcomingEvents else _uiState.value.pastEvents
        val query = _uiState.value.searchQuery.trim()
        val category = _uiState.value.selectedCategory

        return currentList.filter { event ->
            val matchesCategory = if (category == "All") true else event.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                event.title.contains(query, ignoreCase = true) ||
                event.description.contains(query, ignoreCase = true) ||
                (event.venue?.contains(query, ignoreCase = true) == true)
            }
            matchesCategory && matchesQuery
        }
    }
}
