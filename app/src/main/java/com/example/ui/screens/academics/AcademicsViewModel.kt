package com.example.ui.screens.academics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AcademicRepository
import com.example.data.repository.AcademicRepositoryImpl
import com.example.ui.screens.academics.models.AcademicResource
import com.example.ui.screens.academics.models.Department
import com.example.ui.screens.academics.models.FacultyMember
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.academics.models.ResourceType
import com.example.ui.screens.academics.models.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AcademicNavDestination {
    object DepartmentList : AcademicNavDestination()
    data class DepartmentDetail(val departmentId: String) : AcademicNavDestination()
    data class ProgramCurriculum(val programId: String, val departmentId: String) : AcademicNavDestination()
    data class SubjectDetail(val subjectId: String, val programId: String, val departmentId: String) : AcademicNavDestination()
}

data class AcademicsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedResourceTypeFilter: String = "All", // "All", "Course Outline", "Lecture Notes", "Past Papers"
    val departments: List<Department> = emptyList(),
    val currentDestination: AcademicNavDestination = AcademicNavDestination.DepartmentList,
    val selectedDepartment: Department? = null,
    val selectedProgram: Program? = null,
    val selectedSemesterNumber: Int = 1,
    val selectedSubject: Subject? = null,
    val departmentFaculty: List<FacultyMember> = emptyList(),
    val previewResource: AcademicResource? = null,
    val downloadedResourceIds: Set<String> = emptySet(),
    val bookmarkedResourceIds: Set<String> = emptySet()
)

class AcademicsViewModel(
    private val repository: AcademicRepository = AcademicRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademicsUiState())
    val uiState: StateFlow<AcademicsUiState> = _uiState.asStateFlow()

    init {
        loadDepartments()
        observeResourceStates()
    }

    private fun observeResourceStates() {
        viewModelScope.launch {
            repository.getDownloadedIds().collect { ids ->
                _uiState.update { it.copy(downloadedResourceIds = ids) }
            }
        }
        viewModelScope.launch {
            repository.getBookmarkedIds().collect { ids ->
                _uiState.update { it.copy(bookmarkedResourceIds = ids) }
            }
        }
    }

    fun loadDepartments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val query = _uiState.value.searchQuery
            val category = _uiState.value.selectedCategory
            val result = repository.searchAcademicContent(query, category)
            result.onSuccess { depts ->
                _uiState.update { it.copy(isLoading = false, departments = depts) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Failed to load academic data") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadDepartments()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadDepartments()
    }

    fun onResourceTypeFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedResourceTypeFilter = filter) }
    }

    fun selectDepartment(department: Department) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDepartment = department) }
            val facultyResult = repository.getFacultyByDepartment(department.id)
            val facultyList = facultyResult.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedDepartment = department,
                    departmentFaculty = facultyList,
                    currentDestination = AcademicNavDestination.DepartmentDetail(department.id)
                )
            }
        }
    }

    fun selectProgram(program: Program, department: Department) {
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                selectedProgram = program,
                selectedSemesterNumber = 1,
                currentDestination = AcademicNavDestination.ProgramCurriculum(program.id, department.id)
            )
        }
    }

    fun selectSemester(semesterNumber: Int) {
        _uiState.update { it.copy(selectedSemesterNumber = semesterNumber, selectedSubject = null) }
    }

    fun selectSubject(subject: Subject) {
        val program = _uiState.value.selectedProgram
        val department = _uiState.value.selectedDepartment
        if (program != null && department != null) {
            _uiState.update {
                it.copy(
                    selectedSubject = subject,
                    currentDestination = AcademicNavDestination.SubjectDetail(subject.id, program.id, department.id)
                )
            }
        }
    }

    fun navigateBack() {
        when (_uiState.value.currentDestination) {
            is AcademicNavDestination.SubjectDetail -> {
                val prog = _uiState.value.selectedProgram
                val dept = _uiState.value.selectedDepartment
                if (prog != null && dept != null) {
                    _uiState.update {
                        it.copy(
                            selectedSubject = null,
                            currentDestination = AcademicNavDestination.ProgramCurriculum(prog.id, dept.id)
                        )
                    }
                } else {
                    _uiState.update { it.copy(currentDestination = AcademicNavDestination.DepartmentList) }
                }
            }
            is AcademicNavDestination.ProgramCurriculum -> {
                val dept = _uiState.value.selectedDepartment
                if (dept != null) {
                    _uiState.update {
                        it.copy(
                            selectedProgram = null,
                            currentDestination = AcademicNavDestination.DepartmentDetail(dept.id)
                        )
                    }
                } else {
                    _uiState.update { it.copy(currentDestination = AcademicNavDestination.DepartmentList) }
                }
            }
            is AcademicNavDestination.DepartmentDetail -> {
                _uiState.update {
                    it.copy(
                        selectedDepartment = null,
                        currentDestination = AcademicNavDestination.DepartmentList
                    )
                }
            }
            is AcademicNavDestination.DepartmentList -> {
                // Already at root
            }
        }
    }

    fun openResourcePreview(resource: AcademicResource) {
        _uiState.update { it.copy(previewResource = resource) }
    }

    fun closeResourcePreview() {
        _uiState.update { it.copy(previewResource = null) }
    }

    fun downloadResource(resourceId: String) {
        repository.markDownloaded(resourceId)
    }

    fun toggleBookmark(resourceId: String): Boolean {
        return repository.toggleBookmark(resourceId)
    }

    fun toggleOfflineMode() {
        _uiState.update { it.copy(isOffline = !it.isOffline) }
    }
}
