package com.example.ui.screens.admin.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AppRole
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto

private val BrandNavy = Color(0xFF061B52)
private val BrandTextMuted = Color(0xFF5A6A85)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseOutlineManageDialog(
    outline: CourseOutlineDto? = null,
    departments: List<DepartmentDto>,
    programs: List<AcademicProgramDto>,
    courses: List<CourseDto>,
    userRole: AppRole,
    userDepartmentId: String?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        courseId: String,
        programId: String?,
        departmentId: String?,
        title: String,
        sessionYear: String?,
        semesterNumber: Int,
        outlineContent: String?,
        isPublished: Boolean,
        fileBytes: ByteArray?,
        fileName: String?
    ) -> Unit
) {
    val isHod = userRole == AppRole.HOD
    var selectedDeptId by remember {
        mutableStateOf(
            if (isHod && userDepartmentId != null) userDepartmentId
            else outline?.departmentId ?: departments.firstOrNull()?.id
        )
    }

    val filteredPrograms = remember(selectedDeptId, programs) {
        if (selectedDeptId != null) programs.filter { it.departmentId == selectedDeptId }
        else programs
    }

    var selectedProgramId by remember {
        mutableStateOf(
            outline?.programId ?: filteredPrograms.firstOrNull()?.id
        )
    }

    var semesterNumber by remember { mutableIntStateOf(outline?.semesterNumber ?: 1) }

    val filteredCourses = remember(selectedProgramId, courses) {
        if (selectedProgramId != null) courses.filter { it.programId == selectedProgramId }
        else courses
    }

    var selectedCourseId by remember {
        mutableStateOf(outline?.courseId ?: filteredCourses.firstOrNull()?.id ?: "")
    }

    var title by remember {
        mutableStateOf(
            outline?.title ?: (filteredCourses.firstOrNull { it.id == selectedCourseId }?.let { "${it.code} - ${it.title} Outline" } ?: "")
        )
    }
    var sessionYear by remember { mutableStateOf(outline?.sessionYear ?: "2024-2028") }
    var outlineContent by remember { mutableStateOf(outline?.outlineContent ?: "") }
    var isPublished by remember { mutableStateOf(outline?.isPublished ?: true) }

    var fileName by remember { mutableStateOf(outline?.fileName) }
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }

    var deptDropdownExpanded by remember { mutableStateOf(false) }
    var progDropdownExpanded by remember { mutableStateOf(false) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }
    var semDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var courseError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("course_outline_manage_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BrandNavy.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (outline == null) "New Course Outline" else "Edit Outline",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Syllabus, Topics & PDF",
                                fontSize = 12.sp,
                                color = BrandTextMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = BrandTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Department Selection
                val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "Select Department"

                ExposedDropdownMenuBox(
                    expanded = deptDropdownExpanded && !isHod,
                    onExpandedChange = { if (!isHod) deptDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDeptName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isHod,
                        label = { Text(if (isHod) "Department (Locked)" else "Department *") },
                        trailingIcon = {
                            if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_outline_dept"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    if (!isHod) {
                        ExposedDropdownMenu(
                            expanded = deptDropdownExpanded,
                            onDismissRequest = { deptDropdownExpanded = false }
                        ) {
                            departments.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept.name) },
                                    onClick = {
                                        selectedDeptId = dept.id
                                        selectedProgramId = programs.firstOrNull { it.departmentId == dept.id }?.id
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Program Selection
                val selectedProgramTitle = filteredPrograms.firstOrNull { it.id == selectedProgramId }?.title ?: "Select Academic Program"

                ExposedDropdownMenuBox(
                    expanded = progDropdownExpanded,
                    onExpandedChange = { progDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProgramTitle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Academic Program *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = progDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_outline_prog"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = progDropdownExpanded,
                        onDismissRequest = { progDropdownExpanded = false }
                    ) {
                        filteredPrograms.forEach { prog ->
                            DropdownMenuItem(
                                text = { Text(prog.title) },
                                onClick = {
                                    selectedProgramId = prog.id
                                    val matchCourse = courses.firstOrNull { it.programId == prog.id }
                                    selectedCourseId = matchCourse?.id ?: ""
                                    if (matchCourse != null) {
                                        title = "${matchCourse.code} - ${matchCourse.title} Outline"
                                    }
                                    progDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Semester Number
                ExposedDropdownMenuBox(
                    expanded = semDropdownExpanded,
                    onExpandedChange = { semDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "Semester $semesterNumber",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Semester") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_outline_sem"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = semDropdownExpanded,
                        onDismissRequest = { semDropdownExpanded = false }
                    ) {
                        (1..8).forEach { sem ->
                            DropdownMenuItem(
                                text = { Text("Semester $sem") },
                                onClick = {
                                    semesterNumber = sem
                                    semDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Course Selection
                val currentCourse = courses.firstOrNull { it.id == selectedCourseId }
                val selectedCourseName = if (currentCourse != null) "${currentCourse.code}: ${currentCourse.title}" else "Select Course / Subject"

                ExposedDropdownMenuBox(
                    expanded = courseDropdownExpanded,
                    onExpandedChange = { courseDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCourseName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course / Subject *") },
                        isError = courseError != null,
                        supportingText = courseError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_outline_course"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false }
                    ) {
                        filteredCourses.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.code}: ${c.title} (Sem ${c.semesterNumber})") },
                                onClick = {
                                    selectedCourseId = c.id ?: ""
                                    semesterNumber = c.semesterNumber
                                    title = "${c.code} - ${c.title} Outline"
                                    courseError = null
                                    courseDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = if (it.isBlank()) "Title is required" else null
                    },
                    label = { Text("Outline Title *") },
                    placeholder = { Text("e.g., CS-101 Introduction to Computing Syllabus") },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_outline_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Session Year
                OutlinedTextField(
                    value = sessionYear,
                    onValueChange = { sessionYear = it },
                    label = { Text("Session Year") },
                    placeholder = { Text("2024-2028") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_outline_session"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Content / Topics Notes
                OutlinedTextField(
                    value = outlineContent,
                    onValueChange = { outlineContent = it },
                    label = { Text("Weekly Breakdown & Key Topics") },
                    placeholder = { Text("Week 1: Introduction...\nWeek 2: Core Fundamentals...\nWeek 3: Laboratory Practical...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("input_outline_content"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Outline PDF File Upload Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F8FB))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.UploadFile,
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Full Syllabus PDF",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }

                            Button(
                                onClick = {
                                    val safeCode = currentCourse?.code?.replace(" ", "_") ?: "outline"
                                    val sampleName = "${safeCode}_syllabus_${System.currentTimeMillis() % 1000}.pdf"
                                    fileName = sampleName
                                    fileBytes = "GGC Official Course Outline PDF Stream".toByteArray()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_select_outline_pdf")
                            ) {
                                Text(if (fileName == null) "Attach PDF" else "Change", fontSize = 11.sp)
                            }
                        }

                        if (!fileName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "File: $fileName",
                                fontSize = 12.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish Outline", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Visible in Courses & Outlines" else "Draft mode", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_outline_published")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = BrandTextMuted)
                    }

                    Button(
                        onClick = {
                            var valid = true
                            if (selectedCourseId.isBlank()) {
                                courseError = "Please select a course"
                                valid = false
                            }
                            if (title.isBlank()) {
                                titleError = "Title is required"
                                valid = false
                            }
                            if (valid) {
                                onSave(
                                    outline?.id,
                                    selectedCourseId,
                                    selectedProgramId,
                                    selectedDeptId,
                                    title,
                                    sessionYear,
                                    semesterNumber,
                                    outlineContent,
                                    isPublished,
                                    fileBytes,
                                    fileName
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_outline"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (outline == null) "Create Outline" else "Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
