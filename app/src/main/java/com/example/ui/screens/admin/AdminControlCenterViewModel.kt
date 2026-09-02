package com.example.ui.screens.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.AdminHodRemoteDataSource
import com.example.data.datasource.remote.CollegeContentRemoteDataSource
import com.example.data.datasource.remote.NotificationRemoteDataSource
import com.example.data.datasource.remote.OfficialRegistryRemoteDataSource
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AdminSystemOverviewDto
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseDto
import com.example.data.model.DepartmentDto
import com.example.data.model.NotificationType
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class AdminNavSection(val title: String) {
    DASHBOARD("Control Center"),
    STUDENT_LOGINS("Student Logins & Activity"),
    USERS("User Management"),
    STUDENTS("Student Registry"),
    FACULTY("Faculty Registry"),
    ACADEMICS("Academics"),
    CONTENT("Announcements"),
    EVENTS("Events"),
    DOCUMENTS("Documents"),
    NOTIFICATIONS("Broadcasts"),
    SETTINGS("System Settings")
}

data class AdminControlCenterUiState(
    val activeSection: AdminNavSection = AdminNavSection.DASHBOARD,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val overview: AdminSystemOverviewDto = AdminSystemOverviewDto(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // User Management & Registries Cache
    val bsStudentsList: List<OfficialBsStudentDto> = emptyList(),
    val interStudentsList: List<OfficialIntermediateStudentDto> = emptyList(),
    val facultyList: List<OfficialFacultyRegistryDto> = emptyList(),
    val departmentsList: List<DepartmentDto> = emptyList(),
    val programsList: List<AcademicProgramDto> = emptyList(),
    val coursesList: List<CourseDto> = emptyList(),
    val announcementsList: List<AnnouncementDto> = emptyList(),
    val eventsList: List<CollegeEventDto> = emptyList(),
    
    // Broadcast notification form state
    val broadcastTitle: String = "",
    val broadcastMessage: String = "",
    val broadcastType: String = NotificationType.ANNOUNCEMENT_PRIORITY.key,
    val broadcastTarget: String = "All College Members",
    val isBroadcasting: Boolean = false,

    // Post viewers ("Kis kis ne post dekha")
    val selectedPostForReaders: AnnouncementDto? = null,
    val showPostReadersDialog: Boolean = false
)

class AdminControlCenterViewModel(
    private val adminHodDataSource: AdminHodRemoteDataSource = AdminHodRemoteDataSource(),
    private val registryDataSource: OfficialRegistryRemoteDataSource = OfficialRegistryRemoteDataSource(),
    private val contentDataSource: CollegeContentRemoteDataSource = CollegeContentRemoteDataSource(),
    private val notificationDataSource: NotificationRemoteDataSource = NotificationRemoteDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminControlCenterUiState())
    val uiState: StateFlow<AdminControlCenterUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun selectSection(section: AdminNavSection) {
        _uiState.value = _uiState.value.copy(activeSection = section, errorMessage = null)
        when (section) {
            AdminNavSection.DASHBOARD -> loadDashboardData()
            AdminNavSection.USERS -> loadUserData()
            AdminNavSection.STUDENTS -> loadStudentsData()
            AdminNavSection.FACULTY -> loadFacultyData()
            AdminNavSection.ACADEMICS -> loadAcademicsData()
            AdminNavSection.CONTENT -> loadContentData()
            AdminNavSection.EVENTS -> loadEventsData()
            AdminNavSection.DOCUMENTS -> {}
            AdminNavSection.NOTIFICATIONS -> {}
            AdminNavSection.SETTINGS -> {}
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // 1. Fetch system overview statistics
            val overviewResult = adminHodDataSource.getAdminSystemOverview()
            val overview = if (overviewResult is AuthResult.Success) {
                overviewResult.data
            } else {
                _uiState.value.overview
            }

            // 2. Fetch sample recent datasets for quick stats display
            val facultyResult = registryDataSource.fetchOfficialFaculty(limit = 10)
            val faculty = if (facultyResult is AuthResult.Success) facultyResult.data else emptyList()

            val annResult = contentDataSource.getAnnouncements(includeUnpublished = true)
            val announcements = if (annResult is AuthResult.Success) annResult.data else emptyList()

            val eventsResult = contentDataSource.getEvents(includeUnpublished = true)
            val events = if (eventsResult is AuthResult.Success) eventsResult.data else emptyList()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                overview = overview,
                facultyList = faculty,
                announcementsList = announcements,
                eventsList = events
            )
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadDashboardData()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val facultyRes = registryDataSource.fetchOfficialFaculty(limit = 50)
            val deptsRes = contentDataSource.getDepartments()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                facultyList = if (facultyRes is AuthResult.Success) facultyRes.data else _uiState.value.facultyList,
                departmentsList = if (deptsRes is AuthResult.Success) deptsRes.data else _uiState.value.departmentsList
            )
        }
    }

    fun loadStudentsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val bsRes = registryDataSource.fetchOfficialBsStudents(limit = 50)
            val interRes = registryDataSource.fetchOfficialIntermediateStudents(limit = 50)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                bsStudentsList = if (bsRes is AuthResult.Success) bsRes.data else emptyList(),
                interStudentsList = if (interRes is AuthResult.Success) interRes.data else emptyList()
            )
        }
    }

    fun loadFacultyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val facultyRes = registryDataSource.fetchOfficialFaculty(limit = 100)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                facultyList = if (facultyRes is AuthResult.Success) facultyRes.data else emptyList()
            )
        }
    }

    fun loadAcademicsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val deptsRes = contentDataSource.getDepartments()
            val progRes = contentDataSource.getPrograms(includeUnpublished = true)
            val coursesRes = contentDataSource.getCourses(includeUnpublished = true)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                departmentsList = if (deptsRes is AuthResult.Success) deptsRes.data else emptyList(),
                programsList = if (progRes is AuthResult.Success) progRes.data else emptyList(),
                coursesList = if (coursesRes is AuthResult.Success) coursesRes.data else emptyList()
            )
        }
    }

    fun loadContentData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val annRes = contentDataSource.getAnnouncements(includeUnpublished = true)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                announcementsList = if (annRes is AuthResult.Success) annRes.data else emptyList()
            )
        }
    }

    fun loadEventsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val evRes = contentDataSource.getEvents(includeUnpublished = true)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                eventsList = if (evRes is AuthResult.Success) evRes.data else emptyList()
            )
        }
    }

    // Role Management: Assign HOD (Admin Privilege)
    fun assignHod(facultyUserId: String, departmentName: String) {
        val currentProfile = UserProfileManager.userProfile.value
        if (!currentProfile.isAdmin) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only College Admin has the privilege to assign Head of Department (HOD)."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = adminHodDataSource.assignHod(facultyUserId, departmentName)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "HOD role successfully assigned to department $departmentName."
                    )
                    loadUserData()
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

    // Role Management: Revoke HOD (Admin Privilege)
    fun revokeHod(targetUserId: String) {
        val currentProfile = UserProfileManager.userProfile.value
        if (!currentProfile.isAdmin) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only College Admin has the privilege to revoke Head of Department (HOD)."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = adminHodDataSource.revokeHod(targetUserId)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "HOD role revoked. Reverted user to Faculty Teacher."
                    )
                    loadUserData()
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

    // Role Management: Create or Appoint HOD (One Department One HOD strictly enforced)
    fun createOrAppointHod(
        name: String,
        department: String,
        hodId: String,
        password: String = "00000"
    ) {
        val currentProfile = UserProfileManager.userProfile.value
        if (!currentProfile.isAdmin) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only College Admin has the privilege to appoint Head of Department (HOD)."
            )
            return
        }

        if (name.isBlank() || department.isBlank() || hodId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "HOD Name, Department, and HOD ID are required."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = adminHodDataSource.createOrAppointHod(
                name = name,
                department = department,
                hodId = hodId,
                password = password.ifBlank { "00000" }
            )
            when (result) {
                is AuthResult.Success -> {
                    // Update local faculty list state to immediately reflect the new HOD appointment
                    val currentList = _uiState.value.facultyList.toMutableList()
                    
                    // Enforce one HOD per department locally: demote any other HOD in this department
                    val updatedList = currentList.map { faculty ->
                        if (faculty.department.equals(department, ignoreCase = true) && 
                            faculty.designation.contains("HOD", ignoreCase = true) &&
                            faculty.facultyId != hodId.trim().uppercase()) {
                            faculty.copy(designation = "Associate Professor")
                        } else {
                            faculty
                        }
                    }.toMutableList()

                    val existingIndex = updatedList.indexOfFirst { it.facultyId.equals(hodId.trim(), ignoreCase = true) }
                    val newHodEntry = OfficialFacultyRegistryDto(
                        facultyId = hodId.trim().uppercase(),
                        fullName = name.trim(),
                        department = department.trim(),
                        designation = "Head of Department (HOD)",
                        qualification = "Ph.D / Head of Department",
                        institutionalEmail = "hod.${department.lowercase().replace(" ", "")}@ggcmbdin.edu.pk",
                        isClaimed = true,
                        isActive = true
                    )
                    if (existingIndex >= 0) {
                        updatedList[existingIndex] = newHodEntry
                    } else {
                        updatedList.add(0, newHodEntry)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        facultyList = updatedList,
                        successMessage = "HOD '${name.trim()}' ($hodId) appointed successfully for Department of ${department.trim()} (Default Password: ${password.ifBlank { "00000" }})."
                    )
                    loadUserData()
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

    // Role Management: Create or Register Teacher (Permissions: Post & CRUD only)
    fun createOrRegisterTeacher(
        name: String,
        department: String,
        designation: String = "Lecturer",
        teacherId: String,
        password: String = "00000"
    ) {
        val currentProfile = UserProfileManager.userProfile.value
        if (!currentProfile.isAdmin) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only College Admin has privilege to add Faculty Teachers."
            )
            return
        }

        if (name.isBlank() || department.isBlank() || teacherId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Teacher Name, Department, and Teacher ID are required."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = adminHodDataSource.createOrRegisterTeacher(
                name = name,
                department = department,
                designation = designation.ifBlank { "Lecturer" },
                teacherId = teacherId,
                password = password.ifBlank { "00000" }
            )
            when (result) {
                is AuthResult.Success -> {
                    val currentList = _uiState.value.facultyList.toMutableList()
                    val newTeacherEntry = OfficialFacultyRegistryDto(
                        facultyId = teacherId.trim().uppercase(),
                        fullName = name.trim(),
                        department = department.trim(),
                        designation = designation.trim().ifBlank { "Lecturer" },
                        qualification = "M.Phil / Lecturer",
                        institutionalEmail = "${teacherId.trim().lowercase()}@ggcmbdin.edu.pk",
                        isClaimed = true,
                        isActive = true
                    )
                    val existingIndex = currentList.indexOfFirst { it.facultyId.equals(teacherId.trim(), ignoreCase = true) }
                    if (existingIndex >= 0) {
                        currentList[existingIndex] = newTeacherEntry
                    } else {
                        currentList.add(0, newTeacherEntry)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        facultyList = currentList,
                        successMessage = "Teacher '${name.trim()}' ($teacherId) added successfully for ${department.trim()}. (Role: Teacher - Post & CRUD Permissions, Default Password: ${password.ifBlank { "00000" }})."
                    )
                    loadUserData()
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

    // Reset Claimed Registry Record (Super Admin Only)
    fun resetClaimedRecord(registryType: String, recordId: String, reason: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = registryDataSource.resetClaimedRecord(registryType, recordId, reason)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Record claim successfully reset for administrative re-registration."
                    )
                    when (registryType) {
                        "bs_student", "intermediate_student" -> loadStudentsData()
                        "faculty" -> loadFacultyData()
                    }
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

    // Toggle Active Status
    fun toggleRecordActive(registryType: String, recordId: String, currentActive: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = registryDataSource.setRegistryRecordActive(registryType, recordId, !currentActive)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = if (currentActive) "Record deactivated." else "Record activated."
                    )
                    when (registryType) {
                        "bs_student", "intermediate_student" -> loadStudentsData()
                        "faculty" -> loadFacultyData()
                    }
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

    // Broadcast Official Notification
    fun updateBroadcastTitle(value: String) {
        _uiState.value = _uiState.value.copy(broadcastTitle = value)
    }

    fun updateBroadcastMessage(value: String) {
        _uiState.value = _uiState.value.copy(broadcastMessage = value)
    }

    fun updateBroadcastType(value: String) {
        _uiState.value = _uiState.value.copy(broadcastType = value)
    }

    fun updateBroadcastTarget(value: String) {
        _uiState.value = _uiState.value.copy(broadcastTarget = value)
    }

    fun sendBroadcastNotification() {
        val title = _uiState.value.broadcastTitle.trim()
        val message = _uiState.value.broadcastMessage.trim()

        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter notification title.")
            return
        }
        if (message.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter notification message.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBroadcasting = true, errorMessage = null)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val nowStr = dateFormat.format(Date())

            val notification = AppNotificationDto(
                id = "admin_broadcast_${System.currentTimeMillis()}",
                notificationType = _uiState.value.broadcastType,
                title = title,
                message = message,
                contentType = "announcement",
                targetRole = _uiState.value.broadcastTarget,
                isPriority = true,
                createdAt = nowStr
            )

            val result = notificationDataSource.insertNotification(notification)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isBroadcasting = false,
                        broadcastTitle = "",
                        broadcastMessage = "",
                        successMessage = "Official notification successfully broadcasted to college network."
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isBroadcasting = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun openPostReaders(announcement: AnnouncementDto) {
        _uiState.value = _uiState.value.copy(
            selectedPostForReaders = announcement,
            showPostReadersDialog = true
        )
    }

    fun closePostReaders() {
        _uiState.value = _uiState.value.copy(
            selectedPostForReaders = null,
            showPostReadersDialog = false
        )
    }

    fun logoutAdmin(context: Context, onLoggedOut: () -> Unit) {
        UserProfileManager.clearProfile(context)
        onLoggedOut()
    }
}
