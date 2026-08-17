package com.example.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResult
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import com.example.data.repository.OfficialRegistryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OfficialRegistryTab {
    BS_STUDENTS,
    INTERMEDIATE_STUDENTS,
    FACULTY
}

data class OfficialRegistryUiState(
    val selectedTab: OfficialRegistryTab = OfficialRegistryTab.BS_STUDENTS,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val bsStudents: List<OfficialBsStudentDto> = emptyList(),
    val intermediateStudents: List<OfficialIntermediateStudentDto> = emptyList(),
    val facultyList: List<OfficialFacultyRegistryDto> = emptyList(),
    val searchQuery: String = "",
    val selectedProgramFilter: String? = null,
    val selectedDepartmentFilter: String? = null,
    val filterClaimed: Boolean? = null,
    val filterActive: Boolean? = null
)

/**
 * ViewModel managing the state and operations for Official Registry Management.
 */
class OfficialRegistryViewModel(
    private val repository: OfficialRegistryRepository = OfficialRegistryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfficialRegistryUiState())
    val uiState: StateFlow<OfficialRegistryUiState> = _uiState.asStateFlow()

    fun selectTab(tab: OfficialRegistryTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null, successMessage = null) }
        loadCurrentTab()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadCurrentTab()
    }

    fun setProgramFilter(program: String?) {
        _uiState.update { it.copy(selectedProgramFilter = program) }
        loadCurrentTab()
    }

    fun setDepartmentFilter(department: String?) {
        _uiState.update { it.copy(selectedDepartmentFilter = department) }
        loadCurrentTab()
    }

    fun setClaimedFilter(claimed: Boolean?) {
        _uiState.update { it.copy(filterClaimed = claimed) }
        loadCurrentTab()
    }

    fun setActiveFilter(active: Boolean?) {
        _uiState.update { it.copy(filterActive = active) }
        loadCurrentTab()
    }

    fun loadCurrentTab() {
        when (_uiState.value.selectedTab) {
            OfficialRegistryTab.BS_STUDENTS -> loadBsStudents()
            OfficialRegistryTab.INTERMEDIATE_STUDENTS -> loadIntermediateStudents()
            OfficialRegistryTab.FACULTY -> loadFaculty()
        }
    }

    fun loadBsStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = repository.getOfficialBsStudents(
                program = state.selectedProgramFilter,
                isClaimed = state.filterClaimed,
                isActive = state.filterActive,
                searchQuery = state.searchQuery.takeIf { it.isNotBlank() }
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, bsStudents = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun loadIntermediateStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = repository.getOfficialIntermediateStudents(
                program = state.selectedProgramFilter,
                isClaimed = state.filterClaimed,
                isActive = state.filterActive,
                searchQuery = state.searchQuery.takeIf { it.isNotBlank() }
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, intermediateStudents = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun loadFaculty() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = repository.getOfficialFaculty(
                department = state.selectedDepartmentFilter,
                isClaimed = state.filterClaimed,
                isActive = state.filterActive,
                searchQuery = state.searchQuery.takeIf { it.isNotBlank() }
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, facultyList = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // BS STUDENT ACTIONS
    // =========================================================================

    fun saveBsStudent(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.manageBsStudentRecord(
                id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "BS Student record saved successfully."
                        )
                    }
                    loadBsStudents()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteBsStudent(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.deleteBsStudentRecord(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Record deleted successfully."
                        )
                    }
                    loadBsStudents()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // INTERMEDIATE STUDENT ACTIONS
    // =========================================================================

    fun saveIntermediateStudent(
        id: String? = null,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String = "2024-2026",
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.manageIntermediateStudentRecord(
                id, rollNumber, registrationNumber, program, session, firstName, lastName, isActive
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Intermediate record saved successfully."
                        )
                    }
                    loadIntermediateStudents()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteIntermediateStudent(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.deleteIntermediateStudentRecord(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Record deleted successfully."
                        )
                    }
                    loadIntermediateStudents()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // FACULTY ACTIONS
    // =========================================================================

    fun saveFaculty(
        id: String? = null,
        facultyId: String,
        fullName: String,
        department: String,
        designation: String,
        qualification: String,
        institutionalEmail: String? = null,
        phoneNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        isActive: Boolean = true,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.manageFacultyRecord(
                id, facultyId, fullName, department, designation, qualification,
                institutionalEmail, phoneNumber, firstName, lastName, isActive
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Faculty record saved successfully."
                        )
                    }
                    loadFaculty()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteFaculty(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.deleteFacultyRecord(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Record deleted successfully."
                        )
                    }
                    loadFaculty()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // COMMON AUDIT & STATUS ACTIONS
    // =========================================================================

    fun toggleRecordActive(registryType: String, recordId: String, currentActive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            val result = repository.setRegistryRecordActive(registryType, recordId, !currentActive)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(successMessage = result.data.message ?: "Status updated.")
                    }
                    loadCurrentTab()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun resetClaimedRecord(registryType: String, recordId: String, reason: String = "Administrative correction") {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val result = repository.resetClaimedRecord(registryType, recordId, reason)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = result.data.message ?: "Record claim reset successfully."
                        )
                    }
                    loadCurrentTab()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
