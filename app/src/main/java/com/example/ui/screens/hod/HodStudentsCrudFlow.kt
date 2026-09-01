package com.example.ui.screens.hod

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfficialBsStudentDto

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

/**
 * 2. STUDENTS IMPORT & CRUD (Strictly Department Bound)
 * Full management: Registry List, Add Single Student, Edit, Delete, and Batch Import (File / Text Gazette).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodStudentsCrudScreen(
    state: HodUiState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onCreateStudent: (roll: String, reg: String, prog: String, sess: String, first: String, last: String, active: Boolean) -> Unit,
    onUpdateStudent: (id: String, roll: String, reg: String, prog: String, sess: String, first: String, last: String, active: Boolean) -> Unit,
    onDeleteStudent: (id: String, roll: String) -> Unit,
    onUpdateUploadConfig: (prog: String?, sem: String?, sess: String?) -> Unit,
    onParseText: (String) -> Unit,
    onParseFileUri: (android.content.Context, Uri) -> Unit,
    onToggleSelectStudent: (String) -> Unit,
    onToggleSelectAll: () -> Unit,
    onPushSelectedToSupabase: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<OfficialBsStudentDto?>(null) }
    var deletingStudent by remember { mutableStateOf<OfficialBsStudentDto?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onParseFileUri(context, uri)
        }
    }

    val filteredBsStudents = state.bsStudentsList.filter { s ->
        val q = state.studentsSearchQuery.trim().lowercase()
        q.isBlank() ||
                s.rollNumber.lowercase().contains(q) ||
                s.registrationNumber.lowercase().contains(q) ||
                s.effectiveDisplayName.lowercase().contains(q) ||
                s.effectiveProgram.lowercase().contains(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Students Management",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.departmentName} Roster",
                            fontSize = 12.sp,
                            color = BrandGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = BrandNavy,
                    contentColor = BrandGoldLight,
                    modifier = Modifier.testTag("hod_fab_add_student")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Student")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Student", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row: 1. Department Registry (CRUD) | 2. Import Roster (File/Gazette)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = BrandNavy,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandNavy
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Department Students (${state.bsStudentsList.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) BrandNavy else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Import Roster",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) BrandNavy else Color.Gray
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // TAB 1: Department Students Registry (CRUD)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Department Lock Notice
                    Surface(
                        color = BrandNavy.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Restricted to ${state.departmentName} Department Students",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = BrandNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    OutlinedTextField(
                        value = state.studentsSearchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hod_students_search_input"),
                        placeholder = { Text("Search by roll no, reg no, or student name...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandNavy)
                        }
                    } else if (filteredBsStudents.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No students found in ${state.departmentName}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Use \"Import Roster\" tab to upload class lists or tap \"Add Student\"",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredBsStudents, key = { it.id ?: it.rollNumber }) { student ->
                                HodStudentCardItem(
                                    student = student,
                                    onEdit = { editingStudent = student },
                                    onDelete = { deletingStudent = student }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }
                }
            } else {
                // TAB 2: Import Roster (File / Gazette Text)
                HodImportRosterTab(
                    state = state,
                    onUpdateConfig = onUpdateUploadConfig,
                    onPickFile = { filePickerLauncher.launch("*/*") },
                    onParseText = onParseText,
                    onToggleSelect = onToggleSelectStudent,
                    onToggleSelectAll = onToggleSelectAll,
                    onPushToDatabase = onPushSelectedToSupabase
                )
            }
        }
    }

    // Dialog 1: Add Student
    if (showAddDialog) {
        HodAddStudentDialog(
            defaultProgram = state.uploadTargetProgram,
            departmentName = state.departmentName,
            onDismiss = { showAddDialog = false },
            onConfirm = { roll, reg, prog, sess, first, last, active ->
                onCreateStudent(roll, reg, prog, sess, first, last, active)
                showAddDialog = false
            }
        )
    }

    // Dialog 2: Edit Student
    editingStudent?.let { student ->
        HodEditStudentDialog(
            student = student,
            departmentName = state.departmentName,
            onDismiss = { editingStudent = null },
            onConfirm = { id, roll, reg, prog, sess, first, last, active ->
                onUpdateStudent(id, roll, reg, prog, sess, first, last, active)
                editingStudent = null
            }
        )
    }

    // Dialog 3: Delete Student
    deletingStudent?.let { student ->
        AlertDialog(
            onDismissRequest = { deletingStudent = null },
            title = { Text("Delete Student Record?", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Text(
                    "Are you sure you want to remove student \"${student.effectiveDisplayName}\" (Roll: ${student.rollNumber}) from the official ${state.departmentName} registry?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        student.id?.let { onDeleteStudent(it, student.rollNumber) }
                        deletingStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HodStudentCardItem(
    student: OfficialBsStudentDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BrandNavy.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.effectiveDisplayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Roll No: ${student.rollNumber} • ${student.effectiveProgram}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Surface(
                    color = if (student.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (student.isActive) "Active" else "Inactive",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (student.isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reg: ${student.registrationNumber}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Session: ${student.effectiveSession}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodImportRosterTab(
    state: HodUiState,
    onUpdateConfig: (prog: String?, sem: String?, sess: String?) -> Unit,
    onPickFile: () -> Unit,
    onParseText: (String) -> Unit,
    onToggleSelect: (String) -> Unit,
    onToggleSelectAll: () -> Unit,
    onPushToDatabase: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Upload Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Import Parameters (${state.departmentName})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.uploadTargetProgram,
                    onValueChange = { onUpdateConfig(it, null, null) },
                    label = { Text("Target Academic Program") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.uploadTargetSemester,
                        onValueChange = { onUpdateConfig(null, it, null) },
                        label = { Text("Semester") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.uploadTargetSession,
                        onValueChange = { onUpdateConfig(null, null, it) },
                        label = { Text("Session (e.g. 2024-2028)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Document Upload / Text Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Upload Roster File or Paste List",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Accepted formats: CSV, TXT, Gazette lists, or multi-line student text.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onPickFile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select File from Device (.csv, .txt)", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "— OR PASTE ROSTER TEXT DIRECTLY —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Paste Roll No, Registration No, and Student Names here...") },
                    maxLines = 6,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onParseText(rawText) },
                    enabled = rawText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Extract & Preview Students", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Extracted Students Preview (Full List, up to 1000+ students)
        if (state.parsedStudents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))

            var previewSearchQuery by remember { mutableStateOf("") }
            val filteredStudents = remember(state.parsedStudents, previewSearchQuery) {
                if (previewSearchQuery.isBlank()) {
                    state.parsedStudents
                } else {
                    val q = previewSearchQuery.trim().lowercase()
                    state.parsedStudents.filter {
                        it.studentName.lowercase().contains(q) ||
                                it.rollNumber.lowercase().contains(q) ||
                                it.registrationNumber.lowercase().contains(q) ||
                                it.fatherName.lowercase().contains(q)
                    }
                }
            }

            val selectedCount = state.parsedStudents.count { it.isSelected }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Extracted Roster (${state.parsedStudents.size} Students)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "$selectedCount of ${state.parsedStudents.size} selected for import",
                                fontSize = 12.sp,
                                color = if (selectedCount > 0) Color(0xFF2E7D32) else Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleSelectAll() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = state.isAllStudentsSelected,
                                onCheckedChange = { onToggleSelectAll() },
                                colors = CheckboxDefaults.colors(checkedColor = BrandNavy)
                            )
                            Text("Select All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search within preview
                    if (state.parsedStudents.size > 5) {
                        OutlinedTextField(
                            value = previewSearchQuery,
                            onValueChange = { previewSearchQuery = it },
                            placeholder = { Text("Filter extracted list (e.g. roll no, name)...", fontSize = 12.sp) },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            trailingIcon = {
                                if (previewSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { previewSearchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Full Scrollable Student List container
                    val previewScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(previewScrollState)
                    ) {
                        filteredStudents.forEachIndexed { index, student ->
                            val globalIndex = state.parsedStudents.indexOf(student) + 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleSelect(student.id) }
                                    .padding(vertical = 5.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = student.isSelected,
                                    onCheckedChange = { onToggleSelect(student.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = BrandNavy)
                                )
                                Text(
                                    text = "#$globalIndex",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.width(36.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.studentName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandNavy
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Roll: ${student.rollNumber}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandNavy
                                        )
                                        if (student.registrationNumber.isNotBlank() && student.registrationNumber != student.rollNumber) {
                                            Text(
                                                text = " • Reg: ${student.registrationNumber}",
                                                fontSize = 11.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                        if (student.fatherName.isNotBlank()) {
                                            Text(
                                                text = " • S/D/O: ${student.fatherName}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.8.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onPushToDatabase,
                        enabled = !state.isLoading && selectedCount > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Pushing $selectedCount Students to Database...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Push All $selectedCount Students to Database (1-Click)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodAddStudentDialog(
    defaultProgram: String,
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (roll: String, reg: String, prog: String, sess: String, first: String, last: String, active: Boolean) -> Unit
) {
    var rollNumber by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var program by remember { mutableStateOf(defaultProgram) }
    var session by remember { mutableStateOf("2024-2028") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Add Student Record", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Department: $departmentName", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it.uppercase() },
                    label = { Text("Roll Number * (e.g. BS-IT-01)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { regNumber = it.uppercase() },
                    label = { Text("Registration Number * (e.g. 2024-GGC-0101)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father Name (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = program,
                    onValueChange = { program = it },
                    label = { Text("Program") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = session,
                    onValueChange = { session = it },
                    label = { Text("Session (e.g. 2024-2028)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(rollNumber, regNumber, program, session, studentName, fatherName, true)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Add Student", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodEditStudentDialog(
    student: OfficialBsStudentDto,
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (id: String, roll: String, reg: String, prog: String, sess: String, first: String, last: String, active: Boolean) -> Unit
) {
    var rollNumber by remember { mutableStateOf(student.rollNumber) }
    var regNumber by remember { mutableStateOf(student.registrationNumber) }
    var studentName by remember { mutableStateOf(student.effectiveDisplayName) }
    var fatherName by remember { mutableStateOf(student.fatherName ?: "") }
    var program by remember { mutableStateOf(student.effectiveProgram) }
    var session by remember { mutableStateOf(student.effectiveSession) }
    var isActive by remember { mutableStateOf(student.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Student Record", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Roll: ${student.rollNumber} • $departmentName", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it.uppercase() },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { regNumber = it.uppercase() },
                    label = { Text("Registration Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = program,
                    onValueChange = { program = it },
                    label = { Text("Program") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = session,
                    onValueChange = { session = it },
                    label = { Text("Session") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Student Active Status", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    student.id?.let {
                        onConfirm(it, rollNumber, regNumber, program, session, studentName, fatherName, isActive)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
