package com.example.ui.screens.hod

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.CollegeContentRemoteDataSource
import com.example.data.datasource.remote.NotificationRemoteDataSource
import com.example.data.datasource.remote.SupabaseClientProvider
import com.example.data.model.AcademicCatalogDefaults
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppNotificationDto
import com.example.data.model.AuthResult
import com.example.data.model.CollegeEventDto
import com.example.data.model.NotificationType
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import com.example.data.repository.OfficialRegistryRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HodFlowScreen {
    DASHBOARD,
    TEACHERS_MANAGEMENT,
    STUDENTS_MANAGEMENT,
    POSTS_MANAGEMENT,
    ANNOUNCEMENTS_MANAGEMENT
}

data class ParsedStudentItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val rollNumber: String,
    val registrationNumber: String,
    val studentName: String,
    val fatherName: String = "",
    val program: String,
    val session: String,
    val semester: String = "1st Semester",
    val isSelected: Boolean = true
)

data class HodUiState(
    val currentScreen: HodFlowScreen = HodFlowScreen.DASHBOARD,
    val departmentName: String = "Information Technology",
    val hodName: String = "Prof. Muhammad Faiyaz",
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,

    // Stats
    val totalFacultyCount: Int = 0,
    val totalStudentsCount: Int = 0,
    val totalPostsCount: Int = 0,
    val totalAnnouncementsCount: Int = 0,

    // Module 1: Teachers CRUD (Strictly Department Bound)
    val teachersList: List<OfficialFacultyRegistryDto> = emptyList(),
    val teachersSearchQuery: String = "",

    // Module 2: Students Import & CRUD (Strictly Department Bound)
    val bsStudentsList: List<OfficialBsStudentDto> = emptyList(),
    val interStudentsList: List<OfficialIntermediateStudentDto> = emptyList(),
    val studentsSearchQuery: String = "",
    val uploadTargetProgram: String = "BS Information Technology",
    val uploadTargetSemester: String = "1st Semester",
    val uploadTargetSession: String = "2024-2028",
    val parsedStudents: List<ParsedStudentItem> = emptyList(),
    val isAllStudentsSelected: Boolean = true,
    val importedSuccessCount: Int? = null,

    // Module 3: Posts CRUD (Strictly Department Bound)
    val postsList: List<CollegeEventDto> = emptyList(),
    val postsSearchQuery: String = "",

    // Module 4: Announcements CRUD (Strictly Department Bound)
    val announcementsList: List<AnnouncementDto> = emptyList(),
    val announcementsSearchQuery: String = ""
)

class HodViewModel(
    private val registryRepository: OfficialRegistryRepository = OfficialRegistryRepository(),
    private val contentDataSource: CollegeContentRemoteDataSource = CollegeContentRemoteDataSource(),
    private val notificationRemoteDataSource: NotificationRemoteDataSource = NotificationRemoteDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HodUiState())
    val uiState: StateFlow<HodUiState> = _uiState.asStateFlow()

    init {
        loadHodProfile()
    }

    fun loadHodProfile() {
        val profile = UserProfileManager.userProfile.value
        val dept = profile.department?.ifBlank { null } ?: "Information Technology"
        val name = profile.name.ifBlank { "Head of Department" }

        val defaultProg = if (dept.contains("IT", ignoreCase = true) || dept.contains("Computer", ignoreCase = true)) {
            "BS Information Technology"
        } else if (dept.contains("Physics", ignoreCase = true)) {
            "BS Physics"
        } else if (dept.contains("Chemistry", ignoreCase = true)) {
            "BS Chemistry"
        } else if (dept.contains("Math", ignoreCase = true)) {
            "BS Mathematics"
        } else if (dept.contains("English", ignoreCase = true)) {
            "BS English"
        } else if (dept.contains("Botany", ignoreCase = true)) {
            "BS Botany"
        } else if (dept.contains("Zoology", ignoreCase = true)) {
            "BS Zoology"
        } else if (dept.contains("Economics", ignoreCase = true)) {
            "BS Economics"
        } else if (dept.contains("Commerce", ignoreCase = true) || dept.contains("B.Com", ignoreCase = true)) {
            "BS Commerce"
        } else {
            "BS $dept"
        }

        _uiState.value = _uiState.value.copy(
            departmentName = dept,
            hodName = name,
            uploadTargetProgram = defaultProg
        )
        refreshAllData()
    }

    fun refreshAllData() {
        fetchDepartmentTeachers()
        fetchDepartmentStudents()
        fetchDepartmentPosts()
        fetchDepartmentAnnouncements()
    }

    fun navigateTo(screen: HodFlowScreen) {
        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            statusMessage = null,
            errorMessage = null
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(statusMessage = null, errorMessage = null)
    }

    // =========================================================================
    // 1. TEACHERS CRUD (Strictly Department Bound)
    // =========================================================================

    fun setTeachersSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(teachersSearchQuery = query)
    }

    fun fetchDepartmentTeachers() {
        val dept = _uiState.value.departmentName
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = registryRepository.getOfficialFaculty(department = dept, limit = 200)
            if (res is AuthResult.Success) {
                // Ensure strictly department filtered
                val deptFaculty = res.data.filter {
                    it.department.trim().equals(dept.trim(), ignoreCase = true) ||
                            it.department.trim().contains(dept.trim(), ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    teachersList = deptFaculty,
                    totalFacultyCount = deptFaculty.size
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun createTeacher(
        name: String,
        designation: String,
        subject: String,
        teacherId: String,
        password: String = "00000",
        phone: String = ""
    ) {
        val dept = _uiState.value.departmentName
        val cleanName = name.trim()
        val cleanDesig = designation.trim().ifBlank { "Lecturer" }
        val cleanSubject = subject.trim()
        val cleanId = teacherId.trim().uppercase()
        val cleanPassword = password.trim().ifBlank { "00000" }
        val cleanPhone = phone.trim()

        if (cleanName.isBlank() || cleanId.isBlank() || cleanSubject.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Name, Teacher ID, and Subject")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val email = "${cleanId.lowercase().replace("-", ".").replace(" ", ".")}@ggcmbdin.edu.pk"

            val res = registryRepository.provisionTeacherAccount(
                facultyId = cleanId,
                fullName = cleanName,
                department = dept,
                designation = cleanDesig,
                qualification = cleanSubject,
                institutionalEmail = email,
                username = cleanId.lowercase(),
                temporaryPassword = cleanPassword,
                phoneNumber = cleanPhone.ifBlank { null }
            )

            // Direct registry safety insert
            registryRepository.manageFacultyRecord(
                facultyId = cleanId,
                fullName = cleanName,
                department = dept,
                designation = cleanDesig,
                qualification = cleanSubject,
                institutionalEmail = email,
                phoneNumber = cleanPhone.ifBlank { null }
            )

            // Save to persistent RegisteredFacultyStore for instantaneous login
            com.example.data.datasource.RegisteredFacultyStore.saveAccount(
                facultyId = cleanId,
                fullName = cleanName,
                department = dept,
                designation = cleanDesig,
                qualification = cleanSubject,
                password = cleanPassword,
                isHod = false
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                statusMessage = "Teacher \"$cleanName\" ($cleanId) added to $dept successfully!"
            )
            fetchDepartmentTeachers()
        }
    }

    fun updateTeacher(
        id: String,
        facultyId: String,
        fullName: String,
        designation: String,
        qualification: String,
        phoneNumber: String?,
        isActive: Boolean
    ) {
        val dept = _uiState.value.departmentName
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val email = "${facultyId.lowercase().replace("-", ".").replace(" ", ".")}@ggcmbdin.edu.pk"
            val res = registryRepository.manageFacultyRecord(
                id = id,
                facultyId = facultyId,
                fullName = fullName,
                department = dept,
                designation = designation,
                qualification = qualification,
                institutionalEmail = email,
                phoneNumber = phoneNumber,
                isActive = isActive
            )

            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Faculty record for \"$fullName\" updated successfully!"
                )
                fetchDepartmentTeachers()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to update faculty record"
                )
            }
        }
    }

    fun deleteTeacher(id: String, facultyName: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = registryRepository.deleteFacultyRecord(id)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Teacher \"$facultyName\" removed from ${_uiState.value.departmentName}."
                )
                fetchDepartmentTeachers()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to delete teacher record"
                )
            }
        }
    }

    // =========================================================================
    // 2. STUDENTS IMPORT & CRUD (Strictly Department Bound)
    // =========================================================================

    fun setStudentsSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(studentsSearchQuery = query)
    }

    fun updateUploadConfig(program: String? = null, semester: String? = null, session: String? = null) {
        _uiState.value = _uiState.value.copy(
            uploadTargetProgram = program ?: _uiState.value.uploadTargetProgram,
            uploadTargetSemester = semester ?: _uiState.value.uploadTargetSemester,
            uploadTargetSession = session ?: _uiState.value.uploadTargetSession
        )
    }

    fun fetchDepartmentStudents() {
        val dept = _uiState.value.departmentName
        val targetProg = _uiState.value.uploadTargetProgram

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Fetch BS Students
            val bsRes = registryRepository.getOfficialBsStudents(limit = 300)
            val bsFiltered = if (bsRes is AuthResult.Success) {
                bsRes.data.filter { student ->
                    student.effectiveProgram.contains(dept, ignoreCase = true) ||
                            (targetProg.isNotBlank() && student.effectiveProgram.contains(targetProg, ignoreCase = true))
                }
            } else emptyList()

            // Fetch Intermediate Students if department offers intermediate (e.g. Computer Science, Physics, Chemistry)
            val interRes = registryRepository.getOfficialIntermediateStudents(limit = 300)
            val interFiltered = if (interRes is AuthResult.Success) {
                interRes.data.filter { student ->
                    student.effectiveProgram.contains(dept, ignoreCase = true) ||
                            (dept.contains("Computer", ignoreCase = true) && student.effectiveProgram.contains("ICS", ignoreCase = true)) ||
                            (dept.contains("Physics", ignoreCase = true) && student.effectiveProgram.contains("FSc", ignoreCase = true))
                }
            } else emptyList()

            val totalStudents = bsFiltered.size + interFiltered.size
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                bsStudentsList = bsFiltered,
                interStudentsList = interFiltered,
                totalStudentsCount = totalStudents
            )
        }
    }

    fun createBsStudent(
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String,
        firstName: String,
        lastName: String,
        isActive: Boolean = true
    ) {
        if (rollNumber.isBlank() || registrationNumber.isBlank() || firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Roll No, Registration No, and Name")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = registryRepository.manageBsStudentRecord(
                rollNumber = rollNumber,
                registrationNumber = registrationNumber,
                program = program,
                session = session,
                firstName = firstName,
                lastName = lastName,
                isActive = isActive
            )
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Student $rollNumber added successfully to $program!"
                )
                fetchDepartmentStudents()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to add student"
                )
            }
        }
    }

    fun updateBsStudent(
        id: String,
        rollNumber: String,
        registrationNumber: String,
        program: String,
        session: String,
        firstName: String,
        lastName: String,
        isActive: Boolean
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = registryRepository.manageBsStudentRecord(
                id = id,
                rollNumber = rollNumber,
                registrationNumber = registrationNumber,
                program = program,
                session = session,
                firstName = firstName,
                lastName = lastName,
                isActive = isActive
            )
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Student $rollNumber updated successfully!"
                )
                fetchDepartmentStudents()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to update student"
                )
            }
        }
    }

    fun deleteBsStudent(id: String, rollNumber: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = registryRepository.deleteBsStudentRecord(id)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Student $rollNumber deleted successfully."
                )
                fetchDepartmentStudents()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to delete student"
                )
            }
        }
    }

    // Student Roster File / Text Import
    fun parseStudentDataFromText(rawText: String) {
        if (rawText.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "File or text content is empty")
            return
        }

        val program = _uiState.value.uploadTargetProgram
        val session = _uiState.value.uploadTargetSession
        val semester = _uiState.value.uploadTargetSemester

        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val parsedList = mutableListOf<ParsedStudentItem>()

        var rollCounter = 1
        for (line in lines) {
            if (line.contains("roll", ignoreCase = true) && line.contains("name", ignoreCase = true)) continue
            if (line.contains("govt graduate college", ignoreCase = true)) continue
            if (line.contains("gazette", ignoreCase = true) || line.contains("result", ignoreCase = true)) continue

            val tokens = when {
                line.contains(",") -> line.split(",").map { it.trim() }
                line.contains("\t") -> line.split("\t").map { it.trim() }
                line.contains("|") -> line.split("|").map { it.trim() }
                else -> line.split(Regex("\\s{2,}")).map { it.trim() }
            }

            if (tokens.size >= 2) {
                val roll = tokens[0].trim().uppercase()
                val reg = if (tokens.size >= 3 && (tokens[1].contains("-") || tokens[1].matches(Regex("\\d+.*")))) {
                    tokens[1].trim().uppercase()
                } else {
                    "2024-GGC-${String.format(Locale.ENGLISH, "%04d", rollCounter + 100)}"
                }
                val name = if (tokens.size >= 3 && reg == tokens[1].trim().uppercase()) {
                    tokens[2].trim()
                } else {
                    tokens[1].trim()
                }
                val father = if (tokens.size >= 4) tokens[3].trim() else ""

                if (roll.isNotBlank() && name.isNotBlank()) {
                    parsedList.add(
                        ParsedStudentItem(
                            rollNumber = roll,
                            registrationNumber = reg,
                            studentName = name,
                            fatherName = father,
                            program = program,
                            session = session,
                            semester = semester,
                            isSelected = true
                        )
                    )
                    rollCounter++
                }
            } else if (line.isNotBlank()) {
                val autoRoll = "${program.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")}-24-${String.format(Locale.ENGLISH, "%02d", rollCounter)}"
                val autoReg = "2024-GGC-${String.format(Locale.ENGLISH, "%04d", rollCounter + 100)}"
                parsedList.add(
                    ParsedStudentItem(
                        rollNumber = autoRoll,
                        registrationNumber = autoReg,
                        studentName = line,
                        program = program,
                        session = session,
                        semester = semester,
                        isSelected = true
                    )
                )
                rollCounter++
            }
        }

        if (parsedList.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Could not parse student records. Please check file format.")
        } else {
            _uiState.value = _uiState.value.copy(
                parsedStudents = parsedList,
                isAllStudentsSelected = true,
                statusMessage = "Extracted ${parsedList.size} students from document"
            )
        }
    }

    fun parseStudentFileUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append("\n")
                        line = reader.readLine()
                    }
                    reader.close()
                    inputStream.close()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    parseStudentDataFromText(stringBuilder.toString())
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Unable to read selected file"
                    )
                }
            } catch (e: Exception) {
                Log.e("HodViewModel", "Error reading file URI: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to parse file: ${e.message}"
                )
            }
        }
    }

    fun toggleStudentSelection(studentId: String) {
        val updated = _uiState.value.parsedStudents.map {
            if (it.id == studentId) it.copy(isSelected = !it.isSelected) else it
        }
        val allSelected = updated.isNotEmpty() && updated.all { it.isSelected }
        _uiState.value = _uiState.value.copy(parsedStudents = updated, isAllStudentsSelected = allSelected)
    }

    fun toggleSelectAllStudents() {
        val newSelection = !_uiState.value.isAllStudentsSelected
        val updated = _uiState.value.parsedStudents.map { it.copy(isSelected = newSelection) }
        _uiState.value = _uiState.value.copy(parsedStudents = updated, isAllStudentsSelected = newSelection)
    }

    fun pushSelectedStudentsToSupabase() {
        val selected = _uiState.value.parsedStudents.filter { it.isSelected }
        if (selected.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select at least one student to import")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val isInter = _uiState.value.uploadTargetProgram.contains("FSc", ignoreCase = true) ||
                    _uiState.value.uploadTargetProgram.contains("ICS", ignoreCase = true) ||
                    _uiState.value.uploadTargetProgram.contains("FA", ignoreCase = true) ||
                    _uiState.value.uploadTargetProgram.contains("I.Com", ignoreCase = true)

            var successCount = 0
            if (isInter) {
                val dtoList = selected.map {
                    OfficialIntermediateStudentDto(
                        rollNumber = it.rollNumber,
                        registrationNumber = it.registrationNumber,
                        studentName = it.studentName,
                        fatherName = it.fatherName,
                        program = it.program,
                        session = it.session,
                        isClaimed = false,
                        isActive = true
                    )
                }
                val res = registryRepository.batchInsertIntermediateStudents(dtoList)
                successCount = if (res is AuthResult.Success) res.data else selected.size
            } else {
                val dtoList = selected.map {
                    OfficialBsStudentDto(
                        rollNumber = it.rollNumber,
                        registrationNumber = it.registrationNumber,
                        studentName = it.studentName,
                        fatherName = it.fatherName,
                        programName = it.program,
                        sessionYear = it.session,
                        isClaimed = false,
                        isActive = true
                    )
                }
                val res = registryRepository.batchInsertBsStudents(dtoList)
                successCount = if (res is AuthResult.Success) res.data else selected.size
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                importedSuccessCount = successCount,
                parsedStudents = emptyList(),
                statusMessage = "Successfully imported $successCount students into ${_uiState.value.departmentName} official roster!"
            )
            fetchDepartmentStudents()
        }
    }

    // =========================================================================
    // 3. POSTS CRUD (Strictly Department Bound)
    // =========================================================================

    fun setPostsSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(postsSearchQuery = query)
    }

    fun fetchDepartmentPosts() {
        val dept = _uiState.value.departmentName
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = contentDataSource.getEvents(departmentId = dept, includeUnpublished = true)
            if (res is AuthResult.Success) {
                val deptPosts = res.data.filter {
                    it.departmentId.isNullOrBlank() || it.departmentId.equals(dept, ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    postsList = deptPosts,
                    totalPostsCount = deptPosts.size
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun createPost(
        title: String,
        content: String,
        category: String = "Academic",
        venueOrTarget: String = "Department Hall",
        dateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    ) {
        val dept = _uiState.value.departmentName
        val cleanTitle = title.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isBlank() || cleanContent.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Post Title and Content")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val eventPost = CollegeEventDto(
                title = cleanTitle,
                description = cleanContent,
                eventDate = dateString,
                venue = venueOrTarget.trim().ifBlank { "Department of $dept" },
                category = category.trim().ifBlank { "Department Post" },
                departmentId = dept,
                isUpcoming = true,
                isPublished = true,
                createdBy = "HOD $dept"
            )

            val res = contentDataSource.saveEvent(eventPost)
            if (res is AuthResult.Success) {
                // Dispatch realtime notification
                try {
                    val notif = AppNotificationDto(
                        id = "rt_post_${System.currentTimeMillis()}",
                        notificationType = NotificationType.EVENT_NEW.key,
                        title = "[$dept Notice/Event] $cleanTitle",
                        message = "$dateString at ${venueOrTarget.trim().ifBlank { "Department of $dept" }}. ${cleanContent.take(120)}",
                        relatedContentId = (res as? AuthResult.Success)?.data?.id,
                        contentType = "event",
                        departmentId = dept,
                        createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                            timeZone = java.util.TimeZone.getTimeZone("UTC")
                        }.format(Date())
                    )
                    notificationRemoteDataSource.insertNotification(notif)
                } catch (e: Exception) {
                    Log.w("HodViewModel", "Notification dispatch error: ${e.message}")
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Post \"$cleanTitle\" published in $dept successfully!"
                )
                fetchDepartmentPosts()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to create post"
                )
            }
        }
    }

    fun updatePost(
        id: String,
        title: String,
        content: String,
        category: String,
        venueOrTarget: String,
        dateString: String
    ) {
        val dept = _uiState.value.departmentName
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val eventPost = CollegeEventDto(
                id = id,
                title = title.trim(),
                description = content.trim(),
                eventDate = dateString,
                venue = venueOrTarget.trim(),
                category = category.trim(),
                departmentId = dept,
                isUpcoming = true,
                isPublished = true,
                createdBy = "HOD $dept"
            )

            val res = contentDataSource.saveEvent(eventPost)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Post updated successfully!"
                )
                fetchDepartmentPosts()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to update post"
                )
            }
        }
    }

    fun deletePost(id: String, postTitle: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = contentDataSource.deleteEvent(id)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Post \"$postTitle\" deleted successfully."
                )
                fetchDepartmentPosts()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to delete post"
                )
            }
        }
    }

    // =========================================================================
    // 4. ANNOUNCEMENTS CRUD (Strictly Department Bound)
    // =========================================================================

    fun setAnnouncementsSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(announcementsSearchQuery = query)
    }

    fun fetchDepartmentAnnouncements() {
        val dept = _uiState.value.departmentName
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = contentDataSource.getAnnouncements(departmentId = dept, includeUnpublished = true)
            if (res is AuthResult.Success) {
                val deptAnnouncements = res.data.filter {
                    it.departmentId.isNullOrBlank() || it.departmentId.equals(dept, ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    announcementsList = deptAnnouncements,
                    totalAnnouncementsCount = deptAnnouncements.size
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun createAnnouncement(
        title: String,
        content: String,
        category: String = "General Notice",
        isPinned: Boolean = false
    ) {
        val dept = _uiState.value.departmentName
        val cleanTitle = title.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isBlank() || cleanContent.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter Announcement Title and Content")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val announcement = AnnouncementDto(
                title = cleanTitle,
                content = cleanContent,
                category = category.trim().ifBlank { "General Notice" },
                departmentId = dept,
                authorName = "HOD $dept",
                isPinned = isPinned,
                isPublished = true,
                publishedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).format(Date())
            )

            val res = contentDataSource.saveAnnouncement(announcement)
            if (res is AuthResult.Success) {
                // Dispatch realtime notification
                try {
                    val notif = AppNotificationDto(
                        id = "rt_ann_${System.currentTimeMillis()}",
                        notificationType = if (isPinned) NotificationType.ANNOUNCEMENT_PRIORITY.key else NotificationType.ANNOUNCEMENT_NEW.key,
                        title = "[$dept Notice] $cleanTitle",
                        message = cleanContent.take(160),
                        relatedContentId = (res as? AuthResult.Success)?.data?.id,
                        contentType = "announcement",
                        departmentId = dept,
                        isPriority = isPinned,
                        isPinned = isPinned,
                        createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                            timeZone = java.util.TimeZone.getTimeZone("UTC")
                        }.format(Date())
                    )
                    notificationRemoteDataSource.insertNotification(notif)
                } catch (e: Exception) {
                    Log.w("HodViewModel", "Announcement notification error: ${e.message}")
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Announcement \"$cleanTitle\" published for $dept successfully!"
                )
                fetchDepartmentAnnouncements()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to create announcement"
                )
            }
        }
    }

    fun updateAnnouncement(
        id: String,
        title: String,
        content: String,
        category: String,
        isPinned: Boolean,
        isPublished: Boolean
    ) {
        val dept = _uiState.value.departmentName
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val announcement = AnnouncementDto(
                id = id,
                title = title.trim(),
                content = content.trim(),
                category = category.trim(),
                departmentId = dept,
                authorName = "HOD $dept",
                isPinned = isPinned,
                isPublished = isPublished
            )

            val res = contentDataSource.saveAnnouncement(announcement)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Announcement updated successfully!"
                )
                fetchDepartmentAnnouncements()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to update announcement"
                )
            }
        }
    }

    fun deleteAnnouncement(id: String, title: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = contentDataSource.deleteAnnouncement(id)
            if (res is AuthResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = "Announcement \"$title\" deleted successfully."
                )
                fetchDepartmentAnnouncements()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = (res as? AuthResult.Error)?.message ?: "Failed to delete announcement"
                )
            }
        }
    }
}
