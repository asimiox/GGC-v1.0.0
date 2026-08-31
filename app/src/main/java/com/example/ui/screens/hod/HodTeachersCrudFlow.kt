package com.example.ui.screens.hod

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfficialFacultyRegistryDto

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

/**
 * 1. TEACHERS CRUD (Strictly Department Bound)
 * Full management: List, Search, Add/Provision, Edit, and Delete teachers in HOD's department.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodTeachersCrudScreen(
    state: HodUiState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onCreateTeacher: (name: String, desig: String, subj: String, id: String, pass: String, phone: String) -> Unit,
    onUpdateTeacher: (id: String, facultyId: String, name: String, desig: String, subj: String, phone: String?, isActive: Boolean) -> Unit,
    onDeleteTeacher: (id: String, name: String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTeacher by remember { mutableStateOf<OfficialFacultyRegistryDto?>(null) }
    var deletingTeacher by remember { mutableStateOf<OfficialFacultyRegistryDto?>(null) }

    val filteredTeachers = state.teachersList.filter { teacher ->
        val q = state.teachersSearchQuery.trim().lowercase()
        q.isBlank() ||
                teacher.fullName.lowercase().contains(q) ||
                teacher.facultyId.lowercase().contains(q) ||
                (teacher.qualification?.lowercase()?.contains(q) == true) ||
                teacher.designation.lowercase().contains(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Teachers Management",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.departmentName} Department",
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
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandNavy,
                contentColor = BrandGoldLight,
                modifier = Modifier.testTag("hod_fab_add_teacher")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Teacher")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Teacher", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Department Lock Banner
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
                        text = "Department Locked: ${state.departmentName} (${filteredTeachers.size} Faculty Members)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = state.teachersSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hod_teachers_search_input"),
                placeholder = { Text("Search by name, ID, or subject...") },
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
            } else if (filteredTeachers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No teachers found in ${state.departmentName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap \"Add Teacher\" to provision a new faculty account",
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
                    items(filteredTeachers, key = { it.id ?: it.facultyId }) { teacher ->
                        HodTeacherCardItem(
                            teacher = teacher,
                            onEdit = { editingTeacher = teacher },
                            onDelete = { deletingTeacher = teacher }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Dialog 1: Add New Teacher (Provision)
    if (showAddDialog) {
        HodAddTeacherDialog(
            departmentName = state.departmentName,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desig, subj, id, pass, phone ->
                onCreateTeacher(name, desig, subj, id, pass, phone)
                showAddDialog = false
            }
        )
    }

    // Dialog 2: Edit Teacher
    editingTeacher?.let { teacher ->
        HodEditTeacherDialog(
            teacher = teacher,
            departmentName = state.departmentName,
            onDismiss = { editingTeacher = null },
            onConfirm = { id, fId, name, desig, subj, phone, isActive ->
                onUpdateTeacher(id, fId, name, desig, subj, phone, isActive)
                editingTeacher = null
            }
        )
    }

    // Dialog 3: Delete Teacher Confirmation
    deletingTeacher?.let { teacher ->
        AlertDialog(
            onDismissRequest = { deletingTeacher = null },
            title = { Text("Delete Teacher Record?", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Text(
                    "Are you sure you want to remove \"${teacher.fullName}\" (${teacher.facultyId}) from ${state.departmentName} faculty registry? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        teacher.id?.let { onDeleteTeacher(it, teacher.fullName) }
                        deletingTeacher = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTeacher = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HodTeacherCardItem(
    teacher: OfficialFacultyRegistryDto,
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrandNavy.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = teacher.fullName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${teacher.designation} • ${teacher.qualification ?: "Faculty"}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Surface(
                    color = if (teacher.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (teacher.isActive) "Active" else "Inactive",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (teacher.isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = BrandGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ID: ${teacher.facultyId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                }

                if (!teacher.institutionalEmail.isNullOrBlank()) {
                    Text(
                        text = teacher.institutionalEmail,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Edit & Delete
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
fun HodAddTeacherDialog(
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, desig: String, subj: String, id: String, pass: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desig by remember { mutableStateOf("Lecturer") }
    var subject by remember { mutableStateOf("") }
    var facultyId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("00000") }
    var phone by remember { mutableStateOf("") }
    var desigExpanded by remember { mutableStateOf(false) }

    val designations = listOf("Lecturer", "Assistant Professor", "Associate Professor", "Professor", "Visiting Lecturer")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Add Faculty Member", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Department: $departmentName (Locked)", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Teacher Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Designation Dropdown
                ExposedDropdownMenuBox(
                    expanded = desigExpanded,
                    onExpandedChange = { desigExpanded = !desigExpanded }
                ) {
                    OutlinedTextField(
                        value = desig,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Designation *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desigExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = desigExpanded,
                        onDismissRequest = { desigExpanded = false }
                    ) {
                        designations.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    desig = d
                                    desigExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Specialization *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = facultyId,
                    onValueChange = { facultyId = it.uppercase() },
                    label = { Text("Faculty ID * (e.g. IT-04, PHY-02)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Temporary Password (Default: 00000)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desig, subject, facultyId, password, phone) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Provision Account", color = Color.White)
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
fun HodEditTeacherDialog(
    teacher: OfficialFacultyRegistryDto,
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (id: String, facultyId: String, name: String, desig: String, subj: String, phone: String?, isActive: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(teacher.fullName) }
    var desig by remember { mutableStateOf(teacher.designation) }
    var subject by remember { mutableStateOf(teacher.qualification ?: "") }
    var phone by remember { mutableStateOf(teacher.phoneNumber ?: "") }
    var isActive by remember { mutableStateOf(teacher.isActive) }
    var desigExpanded by remember { mutableStateOf(false) }

    val designations = listOf("Lecturer", "Assistant Professor", "Associate Professor", "Professor", "Visiting Lecturer")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Faculty Details", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Faculty ID: ${teacher.facultyId} • $departmentName", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Teacher Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = desigExpanded,
                    onExpandedChange = { desigExpanded = !desigExpanded }
                ) {
                    OutlinedTextField(
                        value = desig,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Designation") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desigExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = desigExpanded,
                        onDismissRequest = { desigExpanded = false }
                    ) {
                        designations.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    desig = d
                                    desigExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Specialization") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Active Status", fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
                    teacher.id?.let {
                        onConfirm(it, teacher.facultyId, name, desig, subject, phone.ifBlank { null }, isActive)
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
