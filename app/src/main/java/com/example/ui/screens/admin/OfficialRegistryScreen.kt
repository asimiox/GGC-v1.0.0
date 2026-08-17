package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserProfileManager
import com.example.data.model.AppRole
import com.example.data.model.OfficialBsStudentDto
import com.example.data.model.OfficialFacultyRegistryDto
import com.example.data.model.OfficialIntermediateStudentDto
import com.example.ui.theme.GgcGoldTertiary
import kotlinx.coroutines.launch

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDark = Color(0xFF030D2B)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandSuccess = Color(0xFF1B873F)
private val BrandSuccessContainer = Color(0xFFE8F5E9)
private val BrandError = Color(0xFFBA1A1A)
private val BrandErrorContainer = Color(0xFFFFDAD6)
private val BrandClaimed = Color(0xFF1E6091)
private val BrandUnclaimed = Color(0xFF7A879D)

val BS_PROGRAM_OPTIONS = listOf(
    "BS Computer Science",
    "BS Information Technology",
    "BS Physics",
    "BS Chemistry",
    "BS Mathematics",
    "BS English",
    "BS Botany",
    "BS Zoology",
    "BS Economics",
    "BS Political Science",
    "BS Statistics",
    "BS Commerce",
    "BS BBA"
)

val INTER_PROGRAM_OPTIONS = listOf(
    "FSc Pre-Medical",
    "FSc Pre-Engineering",
    "ICS (Computer Science)",
    "I.Com (Commerce)",
    "FA (Humanities)"
)

val DEPARTMENT_OPTIONS = listOf(
    "Computer Science & IT",
    "Physics",
    "Chemistry",
    "Mathematics",
    "English",
    "Botany",
    "Zoology",
    "Economics",
    "Urdu",
    "Islamiat",
    "Statistics",
    "Commerce & Business Administration",
    "Political Science"
)

val DESIGNATION_OPTIONS = listOf(
    "Principal",
    "Vice Principal",
    "Head of Department (HOD)",
    "Associate Professor",
    "Assistant Professor",
    "Lecturer",
    "Visiting Lecturer",
    "Director of Physical Education",
    "Librarian",
    "Administrative Officer"
)

val SESSION_OPTIONS = listOf(
    "2021-2025",
    "2022-2026",
    "2023-2027",
    "2024-2028",
    "2025-2029"
)

val INTER_SESSION_OPTIONS = listOf(
    "2023-2025",
    "2024-2026",
    "2025-2027"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialRegistryScreen(
    onBack: () -> Unit,
    viewModel: OfficialRegistryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by UserProfileManager.userProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Dialog state holders
    var showAddBsDialog by remember { mutableStateOf(false) }
    var editingBsStudent by remember { mutableStateOf<OfficialBsStudentDto?>(null) }

    var showAddInterDialog by remember { mutableStateOf(false) }
    var editingInterStudent by remember { mutableStateOf<OfficialIntermediateStudentDto?>(null) }

    var showAddFacultyDialog by remember { mutableStateOf(false) }
    var editingFaculty by remember { mutableStateOf<OfficialFacultyRegistryDto?>(null) }

    // Confirmation dialog states
    var confirmToggleActive by remember {
        mutableStateOf<Triple<String, String, Boolean>?>(null) // type, id, currentActive
    }
    var confirmResetClaim by remember {
        mutableStateOf<Pair<String, String>?>(null) // type, id
    }
    var confirmDeleteRecord by remember {
        mutableStateOf<Pair<String, String>?>(null) // type, id
    }

    // Load initial data on mount
    LaunchedEffect(Unit) {
        viewModel.loadCurrentTab()
    }

    // Handle messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearMessages()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            scope.launch {
                snackbarHostState.showSnackbar("Error: $err")
                viewModel.clearMessages()
            }
        }
    }

    // Check authorization: User must be verified with HOD or ADMIN role
    val isAuthorized = userProfile.isVerified && (userProfile.appRole == AppRole.ADMIN || userProfile.appRole == AppRole.HOD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Registry Management",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = if (userProfile.isAdmin) "Official System Admin Portal" else "Departmental Registry (${userProfile.department ?: "HOD"})",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_registry_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadCurrentTab() },
                        modifier = Modifier.testTag("admin_registry_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = BrandNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BrandNavy
                )
            )
        },
        floatingActionButton = {
            if (isAuthorized) {
                FloatingActionButton(
                    onClick = {
                        when (uiState.selectedTab) {
                            OfficialRegistryTab.BS_STUDENTS -> showAddBsDialog = true
                            OfficialRegistryTab.INTERMEDIATE_STUDENTS -> showAddInterDialog = true
                            OfficialRegistryTab.FACULTY -> showAddFacultyDialog = true
                        }
                    },
                    containerColor = BrandNavy,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("admin_registry_add_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Record")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (uiState.selectedTab) {
                                OfficialRegistryTab.BS_STUDENTS -> "Add BS Student"
                                OfficialRegistryTab.INTERMEDIATE_STUDENTS -> "Add Inter Student"
                                OfficialRegistryTab.FACULTY -> "Add Faculty"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.testTag("admin_registry_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandBackground)
        ) {
            if (!isAuthorized) {
                UnauthorizedAccessView(onBack = onBack)
            } else {
                // Tab Navigation Header
                TabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    containerColor = Color.White,
                    contentColor = BrandNavy,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                            color = GgcGoldTertiary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = uiState.selectedTab == OfficialRegistryTab.BS_STUDENTS,
                        onClick = { viewModel.selectTab(OfficialRegistryTab.BS_STUDENTS) },
                        text = {
                            Text(
                                text = "BS Students",
                                fontWeight = if (uiState.selectedTab == OfficialRegistryTab.BS_STUDENTS) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_bs_students")
                    )

                    Tab(
                        selected = uiState.selectedTab == OfficialRegistryTab.INTERMEDIATE_STUDENTS,
                        onClick = { viewModel.selectTab(OfficialRegistryTab.INTERMEDIATE_STUDENTS) },
                        text = {
                            Text(
                                text = "Intermediate",
                                fontWeight = if (uiState.selectedTab == OfficialRegistryTab.INTERMEDIATE_STUDENTS) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_inter_students")
                    )

                    Tab(
                        selected = uiState.selectedTab == OfficialRegistryTab.FACULTY,
                        onClick = { viewModel.selectTab(OfficialRegistryTab.FACULTY) },
                        text = {
                            Text(
                                text = "Faculty",
                                fontWeight = if (uiState.selectedTab == OfficialRegistryTab.FACULTY) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_faculty")
                    )
                }

                HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

                // Search & Filter Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Live Search Input
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = when (uiState.selectedTab) {
                                        OfficialRegistryTab.BS_STUDENTS -> "Search Name, Roll No, Reg No..."
                                        OfficialRegistryTab.INTERMEDIATE_STUDENTS -> "Search Name, Roll No, Reg No..."
                                        OfficialRegistryTab.FACULTY -> "Search Name, Faculty ID, Email..."
                                    },
                                    fontSize = 13.sp,
                                    color = BrandTextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = BrandNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Search",
                                            tint = BrandTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("registry_search_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                unfocusedBorderColor = Color(0xFFD0D7E2),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filters:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandTextMuted
                            )

                            FilterChip(
                                selected = uiState.filterClaimed == null && uiState.filterActive == null,
                                onClick = {
                                    viewModel.setClaimedFilter(null)
                                    viewModel.setActiveFilter(null)
                                },
                                label = { Text("All", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandNavy,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_all")
                            )

                            FilterChip(
                                selected = uiState.filterClaimed == true,
                                onClick = {
                                    val next = if (uiState.filterClaimed == true) null else true
                                    viewModel.setClaimedFilter(next)
                                },
                                label = { Text("Claimed", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandClaimed,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_claimed")
                            )

                            FilterChip(
                                selected = uiState.filterClaimed == false,
                                onClick = {
                                    val next = if (uiState.filterClaimed == false) null else false
                                    viewModel.setClaimedFilter(next)
                                },
                                label = { Text("Unclaimed", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GgcGoldTertiary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_unclaimed")
                            )

                            FilterChip(
                                selected = uiState.filterActive == false,
                                onClick = {
                                    val next = if (uiState.filterActive == false) null else false
                                    viewModel.setActiveFilter(next)
                                },
                                label = { Text("Inactive", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF757575),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_inactive")
                            )
                        }
                    }
                }

                // Main Registry Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (uiState.isLoading && uiState.bsStudents.isEmpty() && uiState.intermediateStudents.isEmpty() && uiState.facultyList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = BrandNavy, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Loading official records...",
                                    fontSize = 13.sp,
                                    color = BrandTextMuted
                                )
                            }
                        }
                    } else {
                        when (uiState.selectedTab) {
                            OfficialRegistryTab.BS_STUDENTS -> {
                                if (uiState.bsStudents.isEmpty()) {
                                    EmptyStateView(
                                        title = if (uiState.searchQuery.isNotBlank()) "No BS records matching '${uiState.searchQuery}'" else "No BS Student Records in Registry",
                                        subtitle = "Click '+ Add BS Student' button to insert official students authorized for portal registration."
                                    )
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.testTag("bs_students_list")
                                    ) {
                                        item {
                                            RecordsCountHeader(count = uiState.bsStudents.size, tabName = "BS Student Records")
                                        }
                                        items(uiState.bsStudents, key = { it.id }) { student ->
                                            BsStudentCard(
                                                student = student,
                                                onEdit = { editingBsStudent = student },
                                                onToggleActive = {
                                                    confirmToggleActive = Triple("bs", student.id, student.isActive)
                                                },
                                                onResetClaim = {
                                                    confirmResetClaim = Pair("bs", student.id)
                                                },
                                                onDelete = {
                                                    confirmDeleteRecord = Pair("bs", student.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OfficialRegistryTab.INTERMEDIATE_STUDENTS -> {
                                if (uiState.intermediateStudents.isEmpty()) {
                                    EmptyStateView(
                                        title = if (uiState.searchQuery.isNotBlank()) "No Intermediate records matching '${uiState.searchQuery}'" else "No Intermediate Records in Registry",
                                        subtitle = "Click '+ Add Inter Student' button to insert official students authorized for portal registration."
                                    )
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.testTag("inter_students_list")
                                    ) {
                                        item {
                                            RecordsCountHeader(count = uiState.intermediateStudents.size, tabName = "Intermediate Records")
                                        }
                                        items(uiState.intermediateStudents, key = { it.id }) { student ->
                                            IntermediateStudentCard(
                                                student = student,
                                                onEdit = { editingInterStudent = student },
                                                onToggleActive = {
                                                    confirmToggleActive = Triple("intermediate", student.id, student.isActive)
                                                },
                                                onResetClaim = {
                                                    confirmResetClaim = Pair("intermediate", student.id)
                                                },
                                                onDelete = {
                                                    confirmDeleteRecord = Pair("intermediate", student.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OfficialRegistryTab.FACULTY -> {
                                if (uiState.facultyList.isEmpty()) {
                                    EmptyStateView(
                                        title = if (uiState.searchQuery.isNotBlank()) "No Faculty records matching '${uiState.searchQuery}'" else "No Faculty Records in Registry",
                                        subtitle = "Click '+ Add Faculty' button to insert official college professors and instructors."
                                    )
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.testTag("faculty_list")
                                    ) {
                                        item {
                                            RecordsCountHeader(count = uiState.facultyList.size, tabName = "Faculty Records")
                                        }
                                        items(uiState.facultyList, key = { it.id ?: it.facultyId }) { faculty ->
                                            FacultyRegistryCard(
                                                faculty = faculty,
                                                onEdit = { editingFaculty = faculty },
                                                onToggleActive = {
                                                    faculty.id?.let { id ->
                                                        confirmToggleActive = Triple("faculty", id, faculty.isActive)
                                                    }
                                                },
                                                onResetClaim = {
                                                    faculty.id?.let { id ->
                                                        confirmResetClaim = Pair("faculty", id)
                                                    }
                                                },
                                                onDelete = {
                                                    faculty.id?.let { id ->
                                                        confirmDeleteRecord = Pair("faculty", id)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // ADD / EDIT DIALOGS
    // =========================================================================

    if (showAddBsDialog || editingBsStudent != null) {
        BsStudentFormDialog(
            initial = editingBsStudent,
            isSaving = uiState.isSaving,
            onDismiss = {
                showAddBsDialog = false
                editingBsStudent = null
            },
            onSave = { roll, reg, program, session, first, last, active ->
                viewModel.saveBsStudent(
                    id = editingBsStudent?.id,
                    rollNumber = roll,
                    registrationNumber = reg,
                    program = program,
                    session = session,
                    firstName = first,
                    lastName = last,
                    isActive = active,
                    onSuccess = {
                        showAddBsDialog = false
                        editingBsStudent = null
                    }
                )
            }
        )
    }

    if (showAddInterDialog || editingInterStudent != null) {
        IntermediateStudentFormDialog(
            initial = editingInterStudent,
            isSaving = uiState.isSaving,
            onDismiss = {
                showAddInterDialog = false
                editingInterStudent = null
            },
            onSave = { roll, reg, program, session, first, last, active ->
                viewModel.saveIntermediateStudent(
                    id = editingInterStudent?.id,
                    rollNumber = roll,
                    registrationNumber = reg,
                    program = program,
                    session = session,
                    firstName = first,
                    lastName = last,
                    isActive = active,
                    onSuccess = {
                        showAddInterDialog = false
                        editingInterStudent = null
                    }
                )
            }
        )
    }

    if (showAddFacultyDialog || editingFaculty != null) {
        FacultyFormDialog(
            initial = editingFaculty,
            isSaving = uiState.isSaving,
            onDismiss = {
                showAddFacultyDialog = false
                editingFaculty = null
            },
            onSave = { facultyId, fullName, dept, desig, qual, email, phone, active ->
                viewModel.saveFaculty(
                    id = editingFaculty?.id,
                    facultyId = facultyId,
                    fullName = fullName,
                    department = dept,
                    designation = desig,
                    qualification = qual,
                    institutionalEmail = email,
                    phoneNumber = phone,
                    isActive = active,
                    onSuccess = {
                        showAddFacultyDialog = false
                        editingFaculty = null
                    }
                )
            }
        )
    }

    // =========================================================================
    // CONFIRMATION DIALOGS
    // =========================================================================

    confirmToggleActive?.let { (type, id, currentActive) ->
        val actionText = if (currentActive) "Deactivate" else "Activate"
        AlertDialog(
            onDismissRequest = { confirmToggleActive = null },
            icon = {
                Icon(
                    imageVector = if (currentActive) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (currentActive) BrandError else BrandSuccess
                )
            },
            title = {
                Text(
                    text = "$actionText Record?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            },
            text = {
                Text(
                    text = if (currentActive)
                        "Deactivating this record prevents students or teachers from verifying or signing in with these credentials. Existing claimed sessions will be locked."
                    else
                        "Activating this record will allow the holder to verify and access the college portal.",
                    fontSize = 13.sp,
                    color = BrandTextMuted,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleRecordActive(type, id, currentActive)
                        confirmToggleActive = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentActive) BrandError else BrandNavy
                    ),
                    modifier = Modifier.testTag("confirm_toggle_active_button")
                ) {
                    Text(actionText)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmToggleActive = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    confirmResetClaim?.let { (type, id) ->
        AlertDialog(
            onDismissRequest = { confirmResetClaim = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = GgcGoldTertiary
                )
            },
            title = {
                Text(
                    text = "Reset Account Claim?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            },
            text = {
                Text(
                    text = "Resetting the claim clears the linked user ID and frees up this official record. The user will need to re-verify or register again. This action is recorded in the audit logs.",
                    fontSize = 13.sp,
                    color = BrandTextMuted,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetClaimedRecord(type, id, "Administrative reset by authorized Admin/HOD")
                        confirmResetClaim = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GgcGoldTertiary),
                    modifier = Modifier.testTag("confirm_reset_claim_button")
                ) {
                    Text("Reset Claim", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmResetClaim = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    confirmDeleteRecord?.let { (type, id) ->
        AlertDialog(
            onDismissRequest = { confirmDeleteRecord = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = BrandError
                )
            },
            title = {
                Text(
                    text = "Delete Unclaimed Record?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete this official record? Claimed records cannot be deleted without first resetting them.",
                    fontSize = 13.sp,
                    color = BrandTextMuted,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (type) {
                            "bs" -> viewModel.deleteBsStudent(id)
                            "intermediate" -> viewModel.deleteIntermediateStudent(id)
                            "faculty" -> viewModel.deleteFaculty(id)
                        }
                        confirmDeleteRecord = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandError),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteRecord = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// =============================================================================
// LIST CARDS
// =============================================================================

@Composable
private fun RecordsCountHeader(count: Int, tabName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Showing $count $tabName",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandTextMuted
        )
        Text(
            text = "Real-time Verified Database",
            fontSize = 11.sp,
            color = GgcGoldTertiary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BsStudentCard(
    student: OfficialBsStudentDto,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onResetClaim: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bs_student_card_${student.rollNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Student Name & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = listOfNotNull(student.firstName, student.lastName)
                        .joinToString(" ")
                        .ifBlank { "BS Student Record" }

                    Text(
                        text = displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = student.program,
                        fontSize = 12.sp,
                        color = BrandTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = if (student.isActive) "Active" else "Inactive",
                        backgroundColor = if (student.isActive) BrandSuccessContainer else BrandErrorContainer,
                        textColor = if (student.isActive) BrandSuccess else BrandError
                    )

                    StatusBadge(
                        text = if (student.isClaimed) "Claimed" else "Available",
                        backgroundColor = if (student.isClaimed) Color(0xFFE0EDFF) else Color(0xFFFFF3D6),
                        textColor = if (student.isClaimed) BrandClaimed else GgcGoldTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(10.dp))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailInfoItem(label = "Roll Number", value = student.rollNumber)
                DetailInfoItem(label = "Reg Number", value = student.registrationNumber)
                DetailInfoItem(label = "Session", value = student.session ?: "N/A")
            }

            if (student.isClaimed && student.claimedByUserId != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandClaimed,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Linked User ID: ${student.claimedByUserId.take(8)}... (Claimed)",
                        fontSize = 11.sp,
                        color = BrandClaimed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (student.isClaimed) {
                    TextButton(
                        onClick = onResetClaim,
                        colors = ButtonDefaults.textButtonColors(contentColor = GgcGoldTertiary),
                        modifier = Modifier.testTag("reset_claim_btn_${student.rollNumber}")
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandError),
                        modifier = Modifier.testTag("delete_btn_${student.rollNumber}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 12.sp)
                    }
                }

                TextButton(
                    onClick = onToggleActive,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (student.isActive) BrandError else BrandSuccess
                    ),
                    modifier = Modifier.testTag("toggle_active_btn_${student.rollNumber}")
                ) {
                    Icon(
                        imageVector = if (student.isActive) Icons.Default.Block else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (student.isActive) "Deactivate" else "Activate", fontSize = 12.sp)
                }

                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandNavy),
                    modifier = Modifier.testTag("edit_btn_${student.rollNumber}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IntermediateStudentCard(
    student: OfficialIntermediateStudentDto,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onResetClaim: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inter_student_card_${student.rollNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = listOfNotNull(student.firstName, student.lastName)
                        .joinToString(" ")
                        .ifBlank { "Intermediate Student Record" }

                    Text(
                        text = displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = student.program,
                        fontSize = 12.sp,
                        color = BrandTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = if (student.isActive) "Active" else "Inactive",
                        backgroundColor = if (student.isActive) BrandSuccessContainer else BrandErrorContainer,
                        textColor = if (student.isActive) BrandSuccess else BrandError
                    )

                    StatusBadge(
                        text = if (student.isClaimed) "Claimed" else "Available",
                        backgroundColor = if (student.isClaimed) Color(0xFFE0EDFF) else Color(0xFFFFF3D6),
                        textColor = if (student.isClaimed) BrandClaimed else GgcGoldTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailInfoItem(label = "Roll Number", value = student.rollNumber)
                DetailInfoItem(label = "Reg Number", value = student.registrationNumber)
                DetailInfoItem(label = "Session", value = student.session)
            }

            if (student.isClaimed && student.claimedByUserId != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandClaimed,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Linked User ID: ${student.claimedByUserId.take(8)}... (Claimed)",
                        fontSize = 11.sp,
                        color = BrandClaimed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (student.isClaimed) {
                    TextButton(
                        onClick = onResetClaim,
                        colors = ButtonDefaults.textButtonColors(contentColor = GgcGoldTertiary),
                        modifier = Modifier.testTag("reset_claim_btn_${student.rollNumber}")
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandError),
                        modifier = Modifier.testTag("delete_btn_${student.rollNumber}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 12.sp)
                    }
                }

                TextButton(
                    onClick = onToggleActive,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (student.isActive) BrandError else BrandSuccess
                    ),
                    modifier = Modifier.testTag("toggle_active_btn_${student.rollNumber}")
                ) {
                    Icon(
                        imageVector = if (student.isActive) Icons.Default.Block else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (student.isActive) "Deactivate" else "Activate", fontSize = 12.sp)
                }

                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandNavy),
                    modifier = Modifier.testTag("edit_btn_${student.rollNumber}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FacultyRegistryCard(
    faculty: OfficialFacultyRegistryDto,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onResetClaim: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("faculty_card_${faculty.facultyId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = faculty.fullName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${faculty.designation} • ${faculty.department}",
                        fontSize = 12.sp,
                        color = BrandTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = if (faculty.isActive) "Active" else "Inactive",
                        backgroundColor = if (faculty.isActive) BrandSuccessContainer else BrandErrorContainer,
                        textColor = if (faculty.isActive) BrandSuccess else BrandError
                    )

                    StatusBadge(
                        text = if (faculty.isClaimed) "Claimed" else "Available",
                        backgroundColor = if (faculty.isClaimed) Color(0xFFE0EDFF) else Color(0xFFFFF3D6),
                        textColor = if (faculty.isClaimed) BrandClaimed else GgcGoldTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailInfoItem(label = "Faculty ID", value = faculty.facultyId)
                DetailInfoItem(label = "Qualification", value = faculty.qualification)
                DetailInfoItem(label = "Email", value = faculty.institutionalEmail ?: "N/A")
            }

            if (faculty.phoneNumber != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = BrandTextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = faculty.phoneNumber, fontSize = 11.sp, color = BrandTextMuted)
                }
            }

            if (faculty.isClaimed && faculty.claimedByUserId != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandClaimed,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Linked User ID: ${faculty.claimedByUserId.take(8)}... (Claimed)",
                        fontSize = 11.sp,
                        color = BrandClaimed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF2F4F7))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (faculty.isClaimed) {
                    TextButton(
                        onClick = onResetClaim,
                        colors = ButtonDefaults.textButtonColors(contentColor = GgcGoldTertiary),
                        modifier = Modifier.testTag("reset_claim_btn_${faculty.facultyId}")
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandError),
                        modifier = Modifier.testTag("delete_btn_${faculty.facultyId}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 12.sp)
                    }
                }

                TextButton(
                    onClick = onToggleActive,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (faculty.isActive) BrandError else BrandSuccess
                    ),
                    modifier = Modifier.testTag("toggle_active_btn_${faculty.facultyId}")
                ) {
                    Icon(
                        imageVector = if (faculty.isActive) Icons.Default.Block else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (faculty.isActive) "Deactivate" else "Activate", fontSize = 12.sp)
                }

                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandNavy),
                    modifier = Modifier.testTag("edit_btn_${faculty.facultyId}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun DetailInfoItem(label: String, value: String) {
    Column(modifier = Modifier.width(100.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = BrandTextMuted,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyStateView(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = BrandTextMuted,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun UnauthorizedAccessView(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrandErrorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Access Restricted",
                        tint = BrandError,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Administrator Access Required",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Official Registry Management is strictly restricted to authorized College Administrators and Department Heads. Your current session does not hold administrative privileges.",
                    fontSize = 13.sp,
                    color = BrandTextMuted,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("unauthorized_return_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to App")
                }
            }
        }
    }
}

// =============================================================================
// FORMS / DIALOGS
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BsStudentFormDialog(
    initial: OfficialBsStudentDto? = null,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (roll: String, reg: String, program: String, session: String, first: String?, last: String?, active: Boolean) -> Unit
) {
    var rollNumber by remember { mutableStateOf(initial?.rollNumber ?: "") }
    var regNumber by remember { mutableStateOf(initial?.registrationNumber ?: "") }
    var program by remember { mutableStateOf(initial?.program ?: BS_PROGRAM_OPTIONS.first()) }
    var session by remember { mutableStateOf(initial?.session ?: SESSION_OPTIONS.last()) }
    var firstName by remember { mutableStateOf(initial?.firstName ?: "") }
    var lastName by remember { mutableStateOf(initial?.lastName ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    var programExpanded by remember { mutableStateOf(false) }
    var sessionExpanded by remember { mutableStateOf(false) }

    var rollError by remember { mutableStateOf<String?>(null) }
    var regError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initial == null) "Add BS Student Record" else "Edit BS Student Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BrandTextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF2F4F7))

                // Roll Number
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = {
                        rollNumber = it.uppercase()
                        rollError = null
                    },
                    label = { Text("Roll Number * (e.g. 2024-BS-IT-042)") },
                    isError = rollError != null,
                    supportingText = rollError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_roll_number_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Registration Number
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = {
                        regNumber = it.uppercase()
                        regError = null
                    },
                    label = { Text("Registration Number * (e.g. 2024-GGC-042)") },
                    isError = regError != null,
                    supportingText = regError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_reg_number_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Program Dropdown
                ExposedDropdownMenuBox(
                    expanded = programExpanded,
                    onExpandedChange = { programExpanded = !programExpanded }
                ) {
                    OutlinedTextField(
                        value = program,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Degree Program *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("form_program_dropdown"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = programExpanded,
                        onDismissRequest = { programExpanded = false }
                    ) {
                        BS_PROGRAM_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    program = opt
                                    programExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Session Dropdown
                ExposedDropdownMenuBox(
                    expanded = sessionExpanded,
                    onExpandedChange = { sessionExpanded = !sessionExpanded }
                ) {
                    OutlinedTextField(
                        value = session,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Session *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sessionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("form_session_dropdown"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sessionExpanded,
                        onDismissRequest = { sessionExpanded = false }
                    ) {
                        SESSION_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    session = opt
                                    sessionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Optional First & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_first_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("form_last_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Record Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Text("Allow this record for student verification", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandNavy
                        ),
                        modifier = Modifier.testTag("form_active_switch")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        var hasError = false
                        if (rollNumber.isBlank()) {
                            rollError = "Roll Number is required"
                            hasError = true
                        }
                        if (regNumber.isBlank()) {
                            regError = "Registration Number is required"
                            hasError = true
                        }
                        if (!hasError) {
                            onSave(
                                rollNumber.trim().uppercase(),
                                regNumber.trim().uppercase(),
                                program.trim(),
                                session.trim(),
                                firstName.trim().ifBlank { null },
                                lastName.trim().ifBlank { null },
                                isActive
                            )
                        }
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("form_save_bs_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(if (initial == null) "Save BS Record" else "Update BS Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntermediateStudentFormDialog(
    initial: OfficialIntermediateStudentDto? = null,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (roll: String, reg: String, program: String, session: String, first: String?, last: String?, active: Boolean) -> Unit
) {
    var rollNumber by remember { mutableStateOf(initial?.rollNumber ?: "") }
    var regNumber by remember { mutableStateOf(initial?.registrationNumber ?: "") }
    var program by remember { mutableStateOf(initial?.program ?: INTER_PROGRAM_OPTIONS.first()) }
    var session by remember { mutableStateOf(initial?.session ?: INTER_SESSION_OPTIONS.last()) }
    var firstName by remember { mutableStateOf(initial?.firstName ?: "") }
    var lastName by remember { mutableStateOf(initial?.lastName ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    var programExpanded by remember { mutableStateOf(false) }
    var sessionExpanded by remember { mutableStateOf(false) }

    var rollError by remember { mutableStateOf<String?>(null) }
    var regError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initial == null) "Add Inter Record" else "Edit Inter Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BrandTextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF2F4F7))

                // Roll Number
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = {
                        rollNumber = it.uppercase()
                        rollError = null
                    },
                    label = { Text("Roll Number * (e.g. 24-ICS-101)") },
                    isError = rollError != null,
                    supportingText = rollError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_inter_roll_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Registration Number
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = {
                        regNumber = it.uppercase()
                        regError = null
                    },
                    label = { Text("BISE Registration No * (e.g. 2024-BISE-101)") },
                    isError = regError != null,
                    supportingText = regError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_inter_reg_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Program Dropdown
                ExposedDropdownMenuBox(
                    expanded = programExpanded,
                    onExpandedChange = { programExpanded = !programExpanded }
                ) {
                    OutlinedTextField(
                        value = program,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Intermediate Program *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("form_inter_program_dropdown"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = programExpanded,
                        onDismissRequest = { programExpanded = false }
                    ) {
                        INTER_PROGRAM_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    program = opt
                                    programExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Session Dropdown
                ExposedDropdownMenuBox(
                    expanded = sessionExpanded,
                    onExpandedChange = { sessionExpanded = !sessionExpanded }
                ) {
                    OutlinedTextField(
                        value = session,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Session *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sessionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sessionExpanded,
                        onDismissRequest = { sessionExpanded = false }
                    ) {
                        INTER_SESSION_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    session = opt
                                    sessionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // First & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Record Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Text("Allow this record for student verification", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        var hasError = false
                        if (rollNumber.isBlank()) {
                            rollError = "Roll Number is required"
                            hasError = true
                        }
                        if (regNumber.isBlank()) {
                            regError = "Registration Number is required"
                            hasError = true
                        }
                        if (!hasError) {
                            onSave(
                                rollNumber.trim().uppercase(),
                                regNumber.trim().uppercase(),
                                program.trim(),
                                session.trim(),
                                firstName.trim().ifBlank { null },
                                lastName.trim().ifBlank { null },
                                isActive
                            )
                        }
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("form_save_inter_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(if (initial == null) "Save Inter Record" else "Update Inter Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyFormDialog(
    initial: OfficialFacultyRegistryDto? = null,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (facultyId: String, fullName: String, dept: String, desig: String, qual: String, email: String?, phone: String?, active: Boolean) -> Unit
) {
    var facultyId by remember { mutableStateOf(initial?.facultyId ?: "") }
    var fullName by remember { mutableStateOf(initial?.fullName ?: "") }
    var department by remember { mutableStateOf(initial?.department ?: DEPARTMENT_OPTIONS.first()) }
    var designation by remember { mutableStateOf(initial?.designation ?: DESIGNATION_OPTIONS[3]) }
    var qualification by remember { mutableStateOf(initial?.qualification ?: "M.Phil.") }
    var institutionalEmail by remember { mutableStateOf(initial?.institutionalEmail ?: "") }
    var phoneNumber by remember { mutableStateOf(initial?.phoneNumber ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    var deptExpanded by remember { mutableStateOf(false) }
    var desigExpanded by remember { mutableStateOf(false) }

    var facultyIdError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var qualError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initial == null) "Add Faculty Member" else "Edit Faculty Member",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BrandTextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF2F4F7))

                // Faculty ID
                OutlinedTextField(
                    value = facultyId,
                    onValueChange = {
                        facultyId = it.uppercase()
                        facultyIdError = null
                    },
                    label = { Text("Faculty ID * (e.g. FAC-CS-001)") },
                    isError = facultyIdError != null,
                    supportingText = facultyIdError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_faculty_id_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        nameError = null
                    },
                    label = { Text("Full Name * (e.g. Dr. Muhammad Ahmad)") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_faculty_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Department Dropdown
                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = !deptExpanded }
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Academic Department *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("form_faculty_dept_dropdown"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        DEPARTMENT_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    department = opt
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Designation Dropdown
                ExposedDropdownMenuBox(
                    expanded = desigExpanded,
                    onExpandedChange = { desigExpanded = !desigExpanded }
                ) {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Designation *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desigExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("form_faculty_desig_dropdown"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = desigExpanded,
                        onDismissRequest = { desigExpanded = false }
                    ) {
                        DESIGNATION_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 13.sp) },
                                onClick = {
                                    designation = opt
                                    desigExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Qualification
                OutlinedTextField(
                    value = qualification,
                    onValueChange = {
                        qualification = it
                        qualError = null
                    },
                    label = { Text("Qualification * (e.g. Ph.D. / M.Phil.)") },
                    isError = qualError != null,
                    supportingText = qualError?.let { { Text(it, color = BrandError) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_faculty_qual_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Institutional Email
                OutlinedTextField(
                    value = institutionalEmail,
                    onValueChange = { institutionalEmail = it.lowercase() },
                    label = { Text("Institutional Email (e.g. name@ggcmbdin.edu.pk)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_faculty_email_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone / Contact Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_faculty_phone_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Record Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Text("Allow this record for faculty verification", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        var hasError = false
                        if (facultyId.isBlank()) {
                            facultyIdError = "Faculty ID is required"
                            hasError = true
                        }
                        if (fullName.isBlank()) {
                            nameError = "Full Name is required"
                            hasError = true
                        }
                        if (qualification.isBlank()) {
                            qualError = "Qualification is required"
                            hasError = true
                        }
                        if (!hasError) {
                            onSave(
                                facultyId.trim().uppercase(),
                                fullName.trim(),
                                department.trim(),
                                designation.trim(),
                                qualification.trim(),
                                institutionalEmail.trim().lowercase().ifBlank { null },
                                phoneNumber.trim().ifBlank { null },
                                isActive
                            )
                        }
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("form_save_faculty_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(if (initial == null) "Save Faculty Member" else "Update Faculty Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
