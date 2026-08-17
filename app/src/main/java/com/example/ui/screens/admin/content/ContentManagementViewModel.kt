package com.example.ui.screens.admin.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.CollegeStorageRemoteDataSource
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppRole
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto
import com.example.data.repository.CollegeContentRepository
import com.example.data.repository.CollegeStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ContentSectionTab(val title: String) {
    ANNOUNCEMENTS("Announcements"),
    EVENTS("Events"),
    DOCUMENTS("Official Documents"),
    COURSE_OUTLINES("Course Outlines"),
    PROSPECTUS("Prospectus")
}

data class ContentManagementUiState(
    val selectedTab: ContentSectionTab = ContentSectionTab.ANNOUNCEMENTS,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingFile: Boolean = false,
    val uploadProgressMessage: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val userRole: AppRole = AppRole.STUDENT_BS,
    val userDepartment: String? = null,
    val userDepartmentId: String? = null,
    val departments: List<DepartmentDto> = emptyList(),
    val programs: List<AcademicProgramDto> = emptyList(),
    val courses: List<CourseDto> = emptyList(),
    // Content lists
    val announcements: List<AnnouncementDto> = emptyList(),
    val events: List<CollegeEventDto> = emptyList(),
    val documents: List<OfficialDocumentDto> = emptyList(),
    val courseOutlines: List<CourseOutlineDto> = emptyList(),
    val prospectusList: List<ProspectusDto> = emptyList(),
    // Filters
    val searchQuery: String = "",
    val selectedDepartmentFilter: String? = null,
    val selectedProgramFilter: String? = null,
    val selectedCourseFilter: String? = null,
    val selectedDocumentTypeFilter: String? = null,
    val filterPublished: Boolean? = null
)

class ContentManagementViewModel(
    private val contentRepository: CollegeContentRepository = CollegeContentRepository(),
    private val storageRepository: CollegeStorageRepository = CollegeStorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentManagementUiState())
    val uiState: StateFlow<ContentManagementUiState> = _uiState.asStateFlow()

    init {
        val profile = UserProfileManager.userProfile.value
        _uiState.update {
            it.copy(
                userRole = profile.appRole,
                userDepartment = profile.department
            )
        }
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val deptResult = contentRepository.getDepartments()
            if (deptResult is AuthResult.Success) {
                val depts = deptResult.data
                val profile = UserProfileManager.userProfile.value
                val matchedDept = if (!profile.department.isNullOrBlank()) {
                    depts.firstOrNull { it.name.equals(profile.department, ignoreCase = true) || it.code.equals(profile.department, ignoreCase = true) }
                } else null

                _uiState.update {
                    it.copy(
                        departments = depts,
                        userDepartmentId = matchedDept?.id,
                        selectedDepartmentFilter = if (profile.appRole == AppRole.HOD && matchedDept != null) matchedDept.id else null
                    )
                }
            }

            loadPrograms()
            loadCourses()
            loadCurrentTab()
        }
    }

    fun selectTab(tab: ContentSectionTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null, successMessage = null) }
        loadCurrentTab()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setDepartmentFilter(departmentId: String?) {
        _uiState.update { it.copy(selectedDepartmentFilter = departmentId) }
        loadPrograms()
        loadCourses()
        loadCurrentTab()
    }

    fun setProgramFilter(programId: String?) {
        _uiState.update { it.copy(selectedProgramFilter = programId) }
        loadCourses()
        if (_uiState.value.selectedTab == ContentSectionTab.COURSE_OUTLINES) {
            loadCourseOutlines()
        }
    }

    fun setCourseFilter(courseId: String?) {
        _uiState.update { it.copy(selectedCourseFilter = courseId) }
        if (_uiState.value.selectedTab == ContentSectionTab.COURSE_OUTLINES) {
            loadCourseOutlines()
        }
    }

    fun setDocumentTypeFilter(type: String?) {
        _uiState.update { it.copy(selectedDocumentTypeFilter = type) }
        if (_uiState.value.selectedTab == ContentSectionTab.DOCUMENTS) {
            loadDocuments()
        }
    }

    fun setPublishedFilter(published: Boolean?) {
        _uiState.update { it.copy(filterPublished = published) }
    }

    fun loadPrograms() {
        viewModelScope.launch {
            val deptId = _uiState.value.selectedDepartmentFilter
            val result = contentRepository.getPrograms(departmentId = deptId, includeUnpublished = true)
            if (result is AuthResult.Success) {
                _uiState.update { it.copy(programs = result.data) }
            }
        }
    }

    fun loadCourses() {
        viewModelScope.launch {
            val progId = _uiState.value.selectedProgramFilter
            val deptId = _uiState.value.selectedDepartmentFilter
            val result = contentRepository.getCourses(programId = progId, departmentId = deptId, includeUnpublished = true)
            if (result is AuthResult.Success) {
                _uiState.update { it.copy(courses = result.data) }
            }
        }
    }

    fun loadCurrentTab() {
        when (_uiState.value.selectedTab) {
            ContentSectionTab.ANNOUNCEMENTS -> loadAnnouncements()
            ContentSectionTab.EVENTS -> loadEvents()
            ContentSectionTab.DOCUMENTS -> loadDocuments()
            ContentSectionTab.COURSE_OUTLINES -> loadCourseOutlines()
            ContentSectionTab.PROSPECTUS -> loadProspectus()
        }
    }

    // =========================================================================
    // 1. ANNOUNCEMENTS
    // =========================================================================

    fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val deptId = _uiState.value.selectedDepartmentFilter
            val result = contentRepository.getAnnouncements(departmentId = deptId, includeUnpublished = true)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, announcements = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveAnnouncement(
        id: String? = null,
        title: String,
        content: String,
        category: String,
        departmentId: String?,
        isPinned: Boolean,
        isPublished: Boolean,
        attachmentBytes: ByteArray? = null,
        attachmentFileName: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            var storagePath: String? = null
            var finalFileName: String? = null
            var finalSize: Long? = null

            if (attachmentBytes != null && !attachmentFileName.isNullOrBlank()) {
                _uiState.update { it.copy(isUploadingFile = true, uploadProgressMessage = "Uploading announcement attachment...") }
                val deptCode = _uiState.value.departments.firstOrNull { it.id == departmentId }?.code ?: "general"
                val uploadRes = storageRepository.uploadAnnouncementAttachment(deptCode, attachmentFileName, attachmentBytes)
                _uiState.update { it.copy(isUploadingFile = false, uploadProgressMessage = null) }
                when (uploadRes) {
                    is AuthResult.Success -> {
                        storagePath = uploadRes.data
                        finalFileName = attachmentFileName
                        finalSize = attachmentBytes.size.toLong()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "Attachment upload failed: ${uploadRes.message}") }
                        return@launch
                    }
                }
            }

            val profile = UserProfileManager.userProfile.value
            val existing = _uiState.value.announcements.firstOrNull { it.id == id }

            val announcement = AnnouncementDto(
                id = id,
                title = title.trim(),
                content = content.trim(),
                category = category.trim(),
                departmentId = departmentId,
                authorId = profile.userId,
                authorName = profile.name,
                isPinned = isPinned,
                isPublished = isPublished,
                attachmentStoragePath = storagePath ?: existing?.attachmentStoragePath,
                attachmentName = finalFileName ?: existing?.attachmentName,
                attachmentSizeBytes = finalSize ?: existing?.attachmentSizeBytes
            )

            val result = contentRepository.saveAnnouncement(announcement)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Announcement saved successfully."
                        )
                    }
                    loadAnnouncements()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteAnnouncement(id: String, storagePath: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            if (!storagePath.isNullOrBlank()) {
                storageRepository.deleteFile(CollegeStorageRemoteDataSource.BUCKET_ANNOUNCEMENTS, storagePath)
            }
            val result = contentRepository.deleteAnnouncement(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Announcement deleted.") }
                    loadAnnouncements()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleAnnouncementPublish(id: String, currentPublished: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setAnnouncementPublished(id, !currentPublished)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (!currentPublished) "Published" else "Unpublished") }
                    loadAnnouncements()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // 2. EVENTS
    // =========================================================================

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val deptId = _uiState.value.selectedDepartmentFilter
            val result = contentRepository.getEvents(departmentId = deptId, includeUnpublished = true)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, events = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveEvent(
        id: String? = null,
        title: String,
        description: String,
        eventDate: String,
        eventTime: String?,
        venue: String?,
        category: String,
        departmentId: String?,
        isUpcoming: Boolean,
        isPublished: Boolean,
        bannerBytes: ByteArray? = null,
        bannerFileName: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            var bannerPath: String? = null
            var finalFileName: String? = null

            if (bannerBytes != null && !bannerFileName.isNullOrBlank()) {
                _uiState.update { it.copy(isUploadingFile = true, uploadProgressMessage = "Uploading event banner...") }
                val uploadRes = storageRepository.uploadCollegeMedia(bannerFileName, bannerBytes)
                _uiState.update { it.copy(isUploadingFile = false, uploadProgressMessage = null) }
                when (uploadRes) {
                    is AuthResult.Success -> {
                        bannerPath = uploadRes.data
                        finalFileName = bannerFileName
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "Banner upload failed: ${uploadRes.message}") }
                        return@launch
                    }
                }
            }

            val profile = UserProfileManager.userProfile.value
            val existing = _uiState.value.events.firstOrNull { it.id == id }

            val event = CollegeEventDto(
                id = id,
                title = title.trim(),
                description = description.trim(),
                eventDate = eventDate.trim(),
                eventTime = eventTime?.trim()?.ifBlank { null },
                venue = venue?.trim()?.ifBlank { "College Auditorium" },
                category = category.trim(),
                departmentId = departmentId,
                isUpcoming = isUpcoming,
                isPublished = isPublished,
                bannerStoragePath = bannerPath ?: existing?.bannerStoragePath,
                attachmentName = finalFileName ?: existing?.attachmentName,
                createdBy = profile.userId
            )

            val result = contentRepository.saveEvent(event)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Event saved successfully."
                        )
                    }
                    loadEvents()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteEvent(id: String, bannerPath: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            if (!bannerPath.isNullOrBlank()) {
                storageRepository.deleteFile(CollegeStorageRemoteDataSource.BUCKET_COLLEGE_MEDIA, bannerPath)
            }
            val result = contentRepository.deleteEvent(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Event deleted.") }
                    loadEvents()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleEventPublish(id: String, currentPublished: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setEventPublished(id, !currentPublished)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (!currentPublished) "Published" else "Unpublished") }
                    loadEvents()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // 3. OFFICIAL DOCUMENTS
    // =========================================================================

    fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = contentRepository.getOfficialDocuments(
                type = state.selectedDocumentTypeFilter,
                departmentId = state.selectedDepartmentFilter,
                includeUnpublished = true
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, documents = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveOfficialDocument(
        id: String? = null,
        title: String,
        description: String?,
        documentType: String,
        departmentId: String?,
        academicSession: String?,
        isPublished: Boolean,
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            var storagePath: String? = null
            var finalFileName = fileName
            var finalSize: Long? = null

            val existing = _uiState.value.documents.firstOrNull { it.id == id }

            if (fileBytes != null && !fileName.isNullOrBlank()) {
                _uiState.update { it.copy(isUploadingFile = true, uploadProgressMessage = "Uploading document file to secure storage...") }
                val deptCode = _uiState.value.departments.firstOrNull { it.id == departmentId }?.code
                val uploadRes = storageRepository.uploadOfficialDocument(documentType, deptCode, fileName, fileBytes)
                _uiState.update { it.copy(isUploadingFile = false, uploadProgressMessage = null) }
                when (uploadRes) {
                    is AuthResult.Success -> {
                        storagePath = uploadRes.data
                        finalSize = fileBytes.size.toLong()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "File upload failed: ${uploadRes.message}") }
                        return@launch
                    }
                }
            } else if (existing != null) {
                storagePath = existing.storagePath
                finalFileName = existing.fileName
                finalSize = existing.fileSizeBytes
            } else {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Please select a file to upload.") }
                return@launch
            }

            val profile = UserProfileManager.userProfile.value
            val doc = OfficialDocumentDto(
                id = id,
                title = title.trim(),
                description = description?.trim()?.ifBlank { null },
                documentType = documentType.trim(),
                departmentId = departmentId,
                storagePath = storagePath ?: "",
                fileName = finalFileName ?: "document.pdf",
                fileSizeBytes = finalSize,
                mimeType = if ((finalFileName ?: "").endsWith(".docx", ignoreCase = true)) "application/vnd.openxmlformats-officedocument.wordprocessingml.document" else "application/pdf",
                academicSession = academicSession?.trim()?.ifBlank { null },
                isPublished = isPublished,
                uploadedBy = profile.userId
            )

            val result = contentRepository.saveOfficialDocument(doc)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Official Document saved successfully."
                        )
                    }
                    loadDocuments()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteOfficialDocument(id: String, storagePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            if (storagePath.isNotBlank()) {
                storageRepository.deleteFile(CollegeStorageRemoteDataSource.BUCKET_OFFICIAL_DOCUMENTS, storagePath)
            }
            val result = contentRepository.deleteOfficialDocument(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Document deleted.") }
                    loadDocuments()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleDocumentPublish(id: String, currentPublished: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setOfficialDocumentPublished(id, !currentPublished)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (!currentPublished) "Published" else "Unpublished") }
                    loadDocuments()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // 4. COURSE OUTLINES
    // =========================================================================

    fun loadCourseOutlines() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val result = contentRepository.getCourseOutlines(
                courseId = state.selectedCourseFilter,
                programId = state.selectedProgramFilter,
                departmentId = state.selectedDepartmentFilter,
                includeUnpublished = true
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, courseOutlines = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveCourseOutline(
        id: String? = null,
        courseId: String,
        programId: String?,
        departmentId: String?,
        title: String,
        sessionYear: String?,
        semesterNumber: Int,
        outlineContent: String?,
        isPublished: Boolean,
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            var storagePath: String? = null
            var finalFileName = fileName
            var finalSize: Long? = null

            val existing = _uiState.value.courseOutlines.firstOrNull { it.id == id }

            if (fileBytes != null && !fileName.isNullOrBlank()) {
                _uiState.update { it.copy(isUploadingFile = true, uploadProgressMessage = "Uploading course outline PDF...") }
                val deptCode = _uiState.value.departments.firstOrNull { it.id == departmentId }?.code ?: "dept"
                val progCode = _uiState.value.programs.firstOrNull { it.id == programId }?.code ?: "prog"
                val uploadRes = storageRepository.uploadCourseOutline(deptCode, progCode, fileName, fileBytes)
                _uiState.update { it.copy(isUploadingFile = false, uploadProgressMessage = null) }
                when (uploadRes) {
                    is AuthResult.Success -> {
                        storagePath = uploadRes.data
                        finalSize = fileBytes.size.toLong()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "Upload failed: ${uploadRes.message}") }
                        return@launch
                    }
                }
            } else if (existing != null) {
                storagePath = existing.storagePath
                finalFileName = existing.fileName
                finalSize = existing.fileSizeBytes
            }

            val profile = UserProfileManager.userProfile.value
            val outline = CourseOutlineDto(
                id = id,
                courseId = courseId,
                programId = programId,
                departmentId = departmentId,
                title = title.trim(),
                sessionYear = sessionYear?.trim()?.ifBlank { null },
                semesterNumber = semesterNumber,
                outlineContent = outlineContent?.trim()?.ifBlank { null },
                storagePath = storagePath,
                fileName = finalFileName,
                fileSizeBytes = finalSize,
                mimeType = "application/pdf",
                isPublished = isPublished,
                createdBy = profile.userId
            )

            val result = contentRepository.saveCourseOutline(outline)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Course Outline saved successfully."
                        )
                    }
                    loadCourseOutlines()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteCourseOutline(id: String, storagePath: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            if (!storagePath.isNullOrBlank()) {
                storageRepository.deleteFile(CollegeStorageRemoteDataSource.BUCKET_COURSE_OUTLINES, storagePath)
            }
            val result = contentRepository.deleteCourseOutline(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Course outline deleted.") }
                    loadCourseOutlines()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleCourseOutlinePublish(id: String, currentPublished: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setCourseOutlinePublished(id, !currentPublished)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (!currentPublished) "Published" else "Unpublished") }
                    loadCourseOutlines()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    // =========================================================================
    // 5. PROSPECTUS
    // =========================================================================

    fun loadProspectus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = contentRepository.getProspectusList(includeUnpublished = true)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, prospectusList = result.data) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveProspectus(
        id: String? = null,
        title: String,
        academicSession: String,
        programLevel: String?,
        description: String?,
        isCurrent: Boolean,
        isPublished: Boolean,
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            var storagePath: String? = null
            var finalFileName = fileName
            var finalSize: Long? = null

            val existing = _uiState.value.prospectusList.firstOrNull { it.id == id }

            if (fileBytes != null && !fileName.isNullOrBlank()) {
                _uiState.update { it.copy(isUploadingFile = true, uploadProgressMessage = "Uploading prospectus PDF...") }
                val uploadRes = storageRepository.uploadProspectus(academicSession, fileName, fileBytes)
                _uiState.update { it.copy(isUploadingFile = false, uploadProgressMessage = null) }
                when (uploadRes) {
                    is AuthResult.Success -> {
                        storagePath = uploadRes.data
                        finalSize = fileBytes.size.toLong()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "Upload failed: ${uploadRes.message}") }
                        return@launch
                    }
                }
            } else if (existing != null) {
                storagePath = existing.storagePath
                finalFileName = existing.fileName
                finalSize = existing.fileSizeBytes
            } else {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Please select a prospectus PDF file.") }
                return@launch
            }

            val profile = UserProfileManager.userProfile.value
            val prospectus = ProspectusDto(
                id = id,
                title = title.trim(),
                academicSession = academicSession.trim(),
                programLevel = programLevel?.trim()?.ifBlank { "Comprehensive" },
                description = description?.trim()?.ifBlank { null },
                storagePath = storagePath ?: "",
                fileName = finalFileName ?: "prospectus.pdf",
                fileSizeBytes = finalSize,
                mimeType = "application/pdf",
                isCurrent = isCurrent,
                isPublished = isPublished,
                uploadedBy = profile.userId
            )

            val result = contentRepository.saveProspectus(prospectus)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Prospectus saved successfully."
                        )
                    }
                    loadProspectus()
                    onSuccess?.invoke()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun deleteProspectus(id: String, storagePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            if (storagePath.isNotBlank()) {
                storageRepository.deleteFile(CollegeStorageRemoteDataSource.BUCKET_PROSPECTUS, storagePath)
            }
            val result = contentRepository.deleteProspectus(id)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Prospectus deleted.") }
                    loadProspectus()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleProspectusCurrent(id: String, isCurrent: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setProspectusCurrent(id, isCurrent)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (isCurrent) "Set as Current Active Prospectus" else "Removed as Current Prospectus") }
                    loadProspectus()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun toggleProspectusPublish(id: String, currentPublished: Boolean) {
        viewModelScope.launch {
            val result = contentRepository.setProspectusPublished(id, !currentPublished)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = if (!currentPublished) "Published" else "Unpublished") }
                    loadProspectus()
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
