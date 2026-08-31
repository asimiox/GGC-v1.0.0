package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileManager
import com.example.data.model.GgcOfficialDepartments
import com.example.data.model.OfficialFacultyRegistryDto

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF3D372)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F8FB)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementView(
    uiState: AdminControlCenterUiState,
    onAssignHod: (facultyUserId: String, departmentName: String) -> Unit,
    onCreateHod: (name: String, department: String, hodId: String, password: String) -> Unit = { _, _, _, _ -> },
    onCreateTeacher: (name: String, department: String, designation: String, teacherId: String, password: String) -> Unit = { _, _, _, _, _ -> },
    onRevokeHod: (targetUserId: String) -> Unit,
    onResetClaim: (registryType: String, recordId: String, reason: String) -> Unit,
    onToggleActive: (registryType: String, recordId: String, currentActive: Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Department HODs, 1 = Faculty Registry
    var searchQuery by remember { mutableStateOf("") }
    var claimFilter by remember { mutableStateOf<Boolean?>(null) } // null = all, true = claimed, false = unclaimed

    // Dialog state for Claim Reset
    var showResetDialog by remember { mutableStateOf(false) }
    var resetTargetRecordId by remember { mutableStateOf("") }
    var resetTargetName by remember { mutableStateOf("") }
    var resetReason by remember { mutableStateOf("Administrative identity re-verification") }

    // Dialog state for HOD Creation (Strict Department Dropdown & 00000 default password)
    var showCreateHodDialog by remember { mutableStateOf(false) }
    var newHodName by remember { mutableStateOf("") }
    var newHodDepartment by remember { mutableStateOf(GgcOfficialDepartments.LIST.first()) }
    var newHodId by remember { mutableStateOf(GgcOfficialDepartments.generateDefaultHodId(GgcOfficialDepartments.LIST.first())) }
    var newHodPassword by remember { mutableStateOf("00000") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    // Dialog state for Teacher Creation (Post & CRUD permissions)
    var showCreateTeacherDialog by remember { mutableStateOf(false) }
    var newTeacherName by remember { mutableStateOf("") }
    var newTeacherDepartment by remember { mutableStateOf(GgcOfficialDepartments.LIST.first()) }
    var newTeacherDesignation by remember { mutableStateOf("Lecturer") }
    var newTeacherId by remember { mutableStateOf(GgcOfficialDepartments.generateDefaultTeacherId(GgcOfficialDepartments.LIST.first())) }
    var newTeacherPassword by remember { mutableStateOf("00000") }
    var isTeacherPasswordVisible by remember { mutableStateOf(false) }
    var teacherDeptDropdownExpanded by remember { mutableStateOf(false) }
    var teacherDesignationDropdownExpanded by remember { mutableStateOf(false) }

    val teacherDesignationOptions = listOf(
        "Lecturer",
        "Assistant Professor",
        "Associate Professor",
        "Professor",
        "Visiting Faculty",
        "Faculty Member"
    )

    // Dialog state for HOD Promotion
    var showPromoteDialog by remember { mutableStateOf(false) }
    var hodTargetFaculty by remember { mutableStateOf<OfficialFacultyRegistryDto?>(null) }
    var selectedPromoteDepartment by remember { mutableStateOf(GgcOfficialDepartments.LIST.first()) }
    var promoteDeptDropdownExpanded by remember { mutableStateOf(false) }

    val userProfile by UserProfileManager.userProfile.collectAsState()
    val canManageHod = userProfile.isAdmin

    val filteredFaculty = uiState.facultyList.filter { faculty ->
        val matchesQuery = searchQuery.isBlank() ||
                faculty.fullName.contains(searchQuery, ignoreCase = true) ||
                faculty.facultyId.contains(searchQuery, ignoreCase = true) ||
                faculty.department.contains(searchQuery, ignoreCase = true) ||
                (faculty.institutionalEmail?.contains(searchQuery, ignoreCase = true) == true)
        val matchesClaim = claimFilter == null || faculty.isClaimed == claimFilter
        matchesQuery && matchesClaim
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(16.dp)
            .testTag("admin_user_mgmt_view")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HOD & Faculty Administration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Official Academic Departments (https://www.ggcmbdin.edu.pk)",
                    fontSize = 11.sp,
                    color = Color(0xFF718096)
                )
            }

            if (canManageHod) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Appoint HOD Button
                    Button(
                        onClick = {
                            newHodName = ""
                            newHodDepartment = GgcOfficialDepartments.LIST.first()
                            newHodId = GgcOfficialDepartments.generateDefaultHodId(newHodDepartment)
                            newHodPassword = "00000"
                            showCreateHodDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("admin_create_hod_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Appoint HOD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector: 0 = Department HODs, 1 = Faculty Registry
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = BrandNavy,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Department HODs", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Faculty List (${uiState.facultyList.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // -------------------------------------------------------------
            // TAB 0: Official Department HODs (One Department One HOD)
            // -------------------------------------------------------------
            Text(
                text = "One Department One HOD Governance",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Text(
                text = "Only official departments from ggcmbdin.edu.pk are allowed. Each department has strictly one active HOD.",
                fontSize = 11.sp,
                color = Color(0xFF718096)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(GgcOfficialDepartments.LIST) { deptName ->
                    // Find active HOD for this department in facultyList
                    val currentHod = uiState.facultyList.firstOrNull { f ->
                        f.department.equals(deptName, ignoreCase = true) &&
                                (f.designation.contains("HOD", ignoreCase = true) ||
                                        f.designation.contains("Head of Department", ignoreCase = true))
                    }

                    DepartmentHodStatusCard(
                        departmentName = deptName,
                        currentHod = currentHod,
                        canManage = canManageHod,
                        onAppointClick = {
                            newHodDepartment = deptName
                            newHodId = GgcOfficialDepartments.generateDefaultHodId(deptName)
                            newHodName = currentHod?.fullName.orEmpty()
                            newHodPassword = "00000"
                            showCreateHodDialog = true
                        }
                    )
                }
            }
        } else {
            // -------------------------------------------------------------
            // TAB 1: Faculty List & Registry
            // -------------------------------------------------------------
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, ID, department...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandNavy
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_user_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = claimFilter == null,
                    onClick = { claimFilter = null },
                    label = { Text("All Records") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandNavy,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = claimFilter == true,
                    onClick = { claimFilter = true },
                    label = { Text("Claimed Accounts") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandNavy,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = claimFilter == false,
                    onClick = { claimFilter = false },
                    label = { Text("Unclaimed") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandNavy,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandNavy)
                }
            } else if (filteredFaculty.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No faculty records found matching criteria.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFaculty, key = { it.id ?: it.facultyId }) { faculty ->
                        val recordId = faculty.id.orEmpty()
                        FacultyUserAdminCard(
                            faculty = faculty,
                            canAssignHod = canManageHod,
                            onAssignHodClick = {
                                hodTargetFaculty = faculty
                                selectedPromoteDepartment = faculty.department.ifBlank { GgcOfficialDepartments.LIST.first() }
                                showPromoteDialog = true
                            },
                            onToggleActiveClick = {
                                onToggleActive("faculty", recordId, faculty.isActive)
                            },
                            onResetClaimClick = {
                                resetTargetRecordId = recordId
                                resetTargetName = faculty.fullName
                                showResetDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // CREATE / APPOINT NEW HOD DIALOG (NAME, DEPARTMENT, HOD ID, PASSWORD=00000)
    // -------------------------------------------------------------
    if (showCreateHodDialog) {
        val existingHodForDept = uiState.facultyList.firstOrNull {
            it.department.equals(newHodDepartment, ignoreCase = true) &&
                    (it.designation.contains("HOD", ignoreCase = true) || it.designation.contains("Head of Department", ignoreCase = true))
        }

        AlertDialog(
            onDismissRequest = { showCreateHodDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Appoint Department HOD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = BrandNavy
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Appoint Head of Department with official credentials. Password defaults to 00000.",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. NAME
                    OutlinedTextField(
                        value = newHodName,
                        onValueChange = { newHodName = it },
                        label = { Text("HOD Full Name") },
                        placeholder = { Text("e.g. Prof. Dr. Abdul Manan") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_new_hod_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. DEPARTMENT (Dropdown populated strictly from GGCMBDIN official website)
                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded,
                        onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newHodDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Designated Department") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = BrandNavy) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("admin_new_hod_dept_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = deptDropdownExpanded,
                            onDismissRequest = { deptDropdownExpanded = false }
                        ) {
                            GgcOfficialDepartments.LIST.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept, fontSize = 13.sp) },
                                    onClick = {
                                        newHodDepartment = dept
                                        newHodId = GgcOfficialDepartments.generateDefaultHodId(dept)
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. HOD ID
                    OutlinedTextField(
                        value = newHodId,
                        onValueChange = { newHodId = it },
                        label = { Text("HOD ID / Username") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BrandNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_new_hod_id_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. PASSWORD (Default 00000)
                    OutlinedTextField(
                        value = newHodPassword,
                        onValueChange = { newHodPassword = it },
                        label = { Text("Password (Default: 00000)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_new_hod_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // One Department One HOD Notice
                    if (existingHodForDept != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Current HOD is ${existingHodForDept.fullName}. Appointing will reassign this department.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Department is currently vacant. Will appoint as the sole HOD.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHodName.isNotBlank() && newHodDepartment.isNotBlank() && newHodId.isNotBlank()) {
                            onCreateHod(
                                newHodName.trim(),
                                newHodDepartment.trim(),
                                newHodId.trim(),
                                newHodPassword.trim().ifBlank { "00000" }
                            )
                            showCreateHodDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_confirm_create_hod_btn")
                ) {
                    Text("Save & Appoint HOD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateHodDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------------------------------------------------------------
    // CREATE / REGISTER TEACHER DIALOG (Posting & CRUD Permissions)
    // -------------------------------------------------------------
    if (showCreateTeacherDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTeacherDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Add Faculty Teacher",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandNavy
                    )
                    Text(
                        text = "Teacher will be granted full content posting & CRUD privileges",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Full Name
                    OutlinedTextField(
                        value = newTeacherName,
                        onValueChange = { newTeacherName = it },
                        label = { Text("Teacher Full Name *") },
                        placeholder = { Text("e.g. Prof. Tariq Mahmood") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_teacher_name")
                    )

                    // Department Dropdown (Strict GGCMBDIN Official List)
                    ExposedDropdownMenuBox(
                        expanded = teacherDeptDropdownExpanded,
                        onExpandedChange = { teacherDeptDropdownExpanded = !teacherDeptDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newTeacherDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Academic Department *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teacherDeptDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_new_teacher_dept")
                        )
                        ExposedDropdownMenu(
                            expanded = teacherDeptDropdownExpanded,
                            onDismissRequest = { teacherDeptDropdownExpanded = false }
                        ) {
                            GgcOfficialDepartments.LIST.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept, fontSize = 13.sp) },
                                    onClick = {
                                        newTeacherDepartment = dept
                                        newTeacherId = GgcOfficialDepartments.generateDefaultTeacherId(dept)
                                        teacherDeptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Designation Dropdown
                    ExposedDropdownMenuBox(
                        expanded = teacherDesignationDropdownExpanded,
                        onExpandedChange = { teacherDesignationDropdownExpanded = !teacherDesignationDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newTeacherDesignation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Designation") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teacherDesignationDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = teacherDesignationDropdownExpanded,
                            onDismissRequest = { teacherDesignationDropdownExpanded = false }
                        ) {
                            teacherDesignationOptions.forEach { desig ->
                                DropdownMenuItem(
                                    text = { Text(desig, fontSize = 13.sp) },
                                    onClick = {
                                        newTeacherDesignation = desig
                                        teacherDesignationDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Teacher / Faculty ID
                    OutlinedTextField(
                        value = newTeacherId,
                        onValueChange = { newTeacherId = it.uppercase() },
                        label = { Text("Teacher / Faculty ID *") },
                        placeholder = { Text("e.g. IT-T-01") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_teacher_id")
                    )

                    // Default Password
                    OutlinedTextField(
                        value = newTeacherPassword,
                        onValueChange = { newTeacherPassword = it },
                        label = { Text("Password (Default: 00000)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = if (isTeacherPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isTeacherPasswordVisible = !isTeacherPasswordVisible }) {
                                Icon(
                                    imageVector = if (isTeacherPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_teacher_password")
                    )

                    // Role Permission Info Badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Role: Faculty Teacher",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Permissions: Post notices, manage course outlines, lecture materials, sem events & CRUD operations.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTeacherName.isNotBlank() && newTeacherDepartment.isNotBlank() && newTeacherId.isNotBlank()) {
                            onCreateTeacher(
                                newTeacherName.trim(),
                                newTeacherDepartment.trim(),
                                newTeacherDesignation.trim(),
                                newTeacherId.trim(),
                                newTeacherPassword.trim().ifBlank { "00000" }
                            )
                            showCreateTeacherDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_confirm_create_teacher_btn")
                ) {
                    Text("Save & Add Teacher")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTeacherDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------------------------------------------------------------
    // PROMOTE FACULTY TO HOD DIALOG
    // -------------------------------------------------------------
    if (showPromoteDialog && hodTargetFaculty != null) {
        AlertDialog(
            onDismissRequest = { showPromoteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Assign HOD to Department") },
            text = {
                Column {
                    Text(
                        text = "Assign ${hodTargetFaculty?.fullName} (${hodTargetFaculty?.facultyId}) as Head of Department?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = promoteDeptDropdownExpanded,
                        onExpandedChange = { promoteDeptDropdownExpanded = !promoteDeptDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPromoteDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = promoteDeptDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = promoteDeptDropdownExpanded,
                            onDismissRequest = { promoteDeptDropdownExpanded = false }
                        ) {
                            GgcOfficialDepartments.LIST.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept, fontSize = 13.sp) },
                                    onClick = {
                                        selectedPromoteDepartment = dept
                                        promoteDeptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = hodTargetFaculty?.claimedByUserId ?: hodTargetFaculty?.facultyId.orEmpty()
                        onAssignHod(userId, selectedPromoteDepartment)
                        showPromoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("Assign HOD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------------------------------------------------------------
    // RESET CLAIM DIALOG
    // -------------------------------------------------------------
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Reset Claimed Account") },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to reset the claimed account for $resetTargetName?",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "This unbinds the user profile from this official record, allowing re-registration if credentials were lost.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetReason,
                        onValueChange = { resetReason = it },
                        label = { Text("Audit Reason") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetClaim("faculty", resetTargetRecordId, resetReason)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Confirm Reset Claim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DepartmentHodStatusCard(
    departmentName: String,
    currentHod: OfficialFacultyRegistryDto?,
    canManage: Boolean,
    onAppointClick: () -> Unit
) {
    val isAppointed = currentHod != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isAppointed) BrandNavy.copy(alpha = 0.2f) else BorderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isAppointed) BrandNavy.copy(alpha = 0.1f) else Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (isAppointed) BrandNavy else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = departmentName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "Code: ${GgcOfficialDepartments.getDepartmentCode(departmentName)}",
                            fontSize = 11.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }

                // Appointed vs Vacant Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAppointed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isAppointed) "● Appointed" else "○ Vacant",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAppointed) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isAppointed && currentHod != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentHod.fullName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "HOD ID: ${currentHod.facultyId} • Designation: ${currentHod.designation}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (canManage) {
                        OutlinedButton(
                            onClick = onAppointClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Reassign", fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No HOD currently assigned to this department.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    if (canManage) {
                        Button(
                            onClick = onAppointClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Appoint HOD", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FacultyUserAdminCard(
    faculty: OfficialFacultyRegistryDto,
    canAssignHod: Boolean = true,
    onAssignHodClick: () -> Unit,
    onToggleActiveClick: () -> Unit,
    onResetClaimClick: () -> Unit
) {
    val isHod = faculty.designation.contains("HOD", ignoreCase = true) || faculty.designation.contains("Head of Department", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHod) BrandGold.copy(alpha = 0.5f) else BorderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isHod) BrandGold.copy(alpha = 0.15f) else BrandNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHod) Icons.Default.Star else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isHod) BrandGold else BrandNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = faculty.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "${faculty.designation} • Dept. of ${faculty.department}",
                            fontSize = 11.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }

                // Claimed / Unclaimed Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (faculty.isClaimed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (faculty.isClaimed) "Claimed" else "Unclaimed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (faculty.isClaimed) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info rows: Faculty ID, Email, Active state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${faculty.facultyId}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Text(
                    text = if (faculty.isActive) "● Active" else "○ Deactivated",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (faculty.isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canAssignHod && !isHod) {
                    OutlinedButton(
                        onClick = onAssignHodClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BrandGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Appoint HOD", fontSize = 11.sp)
                    }
                }

                if (faculty.isClaimed && !faculty.claimedByUserId.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = onResetClaimClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Claim", fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onToggleActiveClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (faculty.isActive) "Deactivate" else "Activate",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
