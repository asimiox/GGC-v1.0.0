package com.example.ui.screens.admin.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.AcademicCatalogDefaults
import com.example.data.model.AcademicProgramDto
import com.example.data.model.AppRole
import com.example.data.model.CourseDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.DepartmentDto
import com.example.ui.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDeep = Color(0xFF030D29)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF7E7A9)
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
    // Ensure lists are never empty
    val effectiveDepartments = remember(departments) {
        if (departments.isNotEmpty()) departments else AcademicCatalogDefaults.defaultDepartments
    }
    val effectiveAllPrograms = remember(programs) {
        if (programs.isNotEmpty()) programs else AcademicCatalogDefaults.defaultPrograms
    }
    val effectiveAllCourses = remember(courses) {
        if (courses.isNotEmpty()) courses else AcademicCatalogDefaults.defaultCourses
    }

    val isHod = userRole == AppRole.HOD
    var selectedDeptId by remember {
        mutableStateOf<String?>(
            if (isHod && userDepartmentId != null) userDepartmentId
            else outline?.departmentId ?: effectiveDepartments.firstOrNull()?.id ?: AcademicCatalogDefaults.DEPT_ID_IT
        )
    }

    val filteredPrograms = remember(selectedDeptId, effectiveAllPrograms) {
        val list = if (selectedDeptId != null) {
            effectiveAllPrograms.filter { it.departmentId == selectedDeptId }
        } else {
            effectiveAllPrograms
        }
        if (list.isNotEmpty()) list else AcademicCatalogDefaults.getProgramsForDepartment(selectedDeptId)
    }

    var selectedProgramId by remember {
        mutableStateOf<String?>(
            outline?.programId ?: filteredPrograms.firstOrNull()?.id ?: AcademicCatalogDefaults.PROG_ID_BSIT
        )
    }

    var semesterNumber by remember { mutableIntStateOf(outline?.semesterNumber ?: 1) }

    val filteredCourses = remember(selectedProgramId, semesterNumber, effectiveAllCourses) {
        val byProg = effectiveAllCourses.filter { it.programId == selectedProgramId }
        val bySem = byProg.filter { it.semesterNumber == semesterNumber }
        if (bySem.isNotEmpty()) bySem
        else if (byProg.isNotEmpty()) byProg
        else AcademicCatalogDefaults.getCoursesForProgram(selectedProgramId, semesterNumber)
    }

    var isCustomCourseMode by remember { mutableStateOf(false) }
    var customCourseCode by remember { mutableStateOf("") }
    var customCourseTitle by remember { mutableStateOf("") }
    var customCreditHours by remember { mutableStateOf("3 (3-0)") }

    var selectedCourseId by remember {
        mutableStateOf(
            outline?.courseId ?: filteredCourses.firstOrNull()?.id ?: AcademicCatalogDefaults.defaultCourses.first().id ?: ""
        )
    }

    var title by remember {
        mutableStateOf(
            outline?.title ?: run {
                val c = filteredCourses.firstOrNull { it.id == selectedCourseId }
                if (c != null) "${c.code} - ${c.title} Outline" else "Course Outline"
            }
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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isReadingFile by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            coroutineScope.launch(Dispatchers.IO) {
                val realName = FileUtils.getFileName(context, uri)
                val bytes = FileUtils.getFileBytes(context, uri)
                withContext(Dispatchers.Main) {
                    isReadingFile = false
                    if (bytes != null && bytes.isNotEmpty()) {
                        fileName = realName
                        fileBytes = bytes
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFD6DFEB)),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("course_outline_manage_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // 1. EXECUTIVE GOVERNMENT HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BrandNavyDeep, BrandNavy)
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(BrandGold, Color(0xFFFFE082), BrandGold)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                                        .border(BorderStroke(1.5.dp, BrandGold.copy(alpha = 0.7f)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                                        contentDescription = "College Seal",
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.1.sp,
                                            color = BrandGoldLight
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Official",
                                            tint = BrandGold,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                    Text(
                                        text = if (outline == null) "Curriculum & Course Syllabus Dispatch" else "Edit Academic Outline",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Academic Council • Departmental Syllabus Management",
                                        fontSize = 10.5.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.10f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 2. SCROLLABLE FORM BODY
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFC))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Authority Notice Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "OFFICIAL CURRICULUM DISPATCH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavy,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Ensure correct Department, Academic Program, and Semester alignment according to University of Gujrat / BISE affiliation guidelines.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                // 1. Department Selection
                val currentDept = effectiveDepartments.firstOrNull { it.id == selectedDeptId }
                val selectedDeptName = currentDept?.name ?: "Select Department"

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
                            effectiveDepartments.forEach { dept ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(dept.name, fontWeight = FontWeight.Medium)
                                            if (dept.code.isNotBlank()) {
                                                Text("Code: ${dept.code}", fontSize = 11.sp, color = BrandTextMuted)
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedDeptId = dept.id
                                        val newProgList = effectiveAllPrograms.filter { it.departmentId == dept.id }.ifEmpty {
                                            AcademicCatalogDefaults.getProgramsForDepartment(dept.id, dept.code)
                                        }
                                        val firstProg = newProgList.firstOrNull()
                                        selectedProgramId = firstProg?.id ?: AcademicCatalogDefaults.PROG_ID_BSIT

                                        val newCourses = effectiveAllCourses.filter { it.programId == selectedProgramId }.ifEmpty {
                                            AcademicCatalogDefaults.getCoursesForProgram(selectedProgramId, semesterNumber)
                                        }
                                        val firstCourse = newCourses.firstOrNull()
                                        selectedCourseId = firstCourse?.id ?: ""
                                        if (firstCourse != null) {
                                            title = "${firstCourse.code} - ${firstCourse.title} Outline"
                                        }
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Academic Program Selection
                val currentProgram = effectiveAllPrograms.firstOrNull { it.id == selectedProgramId }
                    ?: filteredPrograms.firstOrNull { it.id == selectedProgramId }
                val selectedProgramTitle = currentProgram?.title ?: "Select Academic Program"

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
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(prog.title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            Text("${prog.code} • ${prog.degreeType}", fontSize = 11.sp, color = BrandTextMuted)
                                        }
                                        if (prog.id == selectedProgramId) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    selectedProgramId = prog.id ?: ""
                                    val matchCourses = effectiveAllCourses.filter { it.programId == prog.id }.ifEmpty {
                                        AcademicCatalogDefaults.getCoursesForProgram(prog.id, semesterNumber)
                                    }
                                    val firstMatch = matchCourses.firstOrNull { it.semesterNumber == semesterNumber } ?: matchCourses.firstOrNull()
                                    selectedCourseId = firstMatch?.id ?: ""
                                    if (firstMatch != null) {
                                        title = "${firstMatch.code} - ${firstMatch.title} Outline"
                                    }
                                    progDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Semester Selection
                val isInter = currentProgram?.isIntermediate == true
                val semLabel = if (isInter) "Part / Year $semesterNumber" else "Semester $semesterNumber"

                ExposedDropdownMenuBox(
                    expanded = semDropdownExpanded,
                    onExpandedChange = { semDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = semLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isInter) "Class / Part" else "Semester") },
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
                        val maxSem = if (isInter) 2 else (currentProgram?.totalSemesters ?: 8)
                        (1..maxSem).forEach { sem ->
                            DropdownMenuItem(
                                text = { Text(if (isInter) "Part $sem" else "Semester $sem") },
                                onClick = {
                                    semesterNumber = sem
                                    val matched = filteredCourses.firstOrNull { it.semesterNumber == sem }
                                    if (matched != null) {
                                        selectedCourseId = matched.id ?: ""
                                        title = "${matched.code} - ${matched.title} Outline"
                                    }
                                    semDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Course Selection & Custom Course Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCustomCourseMode) "Custom Subject Details" else "Select Course / Subject *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                    Text(
                        text = if (isCustomCourseMode) "← Choose from Catalog" else "+ Type Custom Subject",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGold,
                        modifier = Modifier
                            .clickable {
                                isCustomCourseMode = !isCustomCourseMode
                                courseError = null
                            }
                            .padding(4.dp)
                            .testTag("btn_toggle_custom_course")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (!isCustomCourseMode) {
                    val currentCourse = effectiveAllCourses.firstOrNull { it.id == selectedCourseId }
                        ?: filteredCourses.firstOrNull { it.id == selectedCourseId }
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
                                    text = {
                                        Column {
                                            Text("${c.code}: ${c.title}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            Text("Credits: ${c.creditHours} • Sem ${c.semesterNumber}", fontSize = 11.sp, color = BrandTextMuted)
                                        }
                                    },
                                    onClick = {
                                        selectedCourseId = c.id ?: ""
                                        semesterNumber = c.semesterNumber
                                        title = "${c.code} - ${c.title} Outline"
                                        courseError = null
                                        courseDropdownExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Add Custom / New Subject...", color = BrandNavy, fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    isCustomCourseMode = true
                                    courseDropdownExpanded = false
                                }
                            )
                        }
                    }
                } else {
                    // Custom course input card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F8FB))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customCourseCode,
                                    onValueChange = {
                                        customCourseCode = it
                                        if (it.isNotBlank() && customCourseTitle.isNotBlank()) {
                                            title = "$it - $customCourseTitle Outline"
                                        }
                                    },
                                    label = { Text("Course Code *") },
                                    placeholder = { Text("e.g., CS-201") },
                                    modifier = Modifier
                                        .weight(0.45f)
                                        .testTag("input_custom_course_code"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = customCreditHours,
                                    onValueChange = { customCreditHours = it },
                                    label = { Text("Credits") },
                                    placeholder = { Text("3 (3-0)") },
                                    modifier = Modifier
                                        .weight(0.55f)
                                        .testTag("input_custom_credit_hours"),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customCourseTitle,
                                onValueChange = {
                                    customCourseTitle = it
                                    if (customCourseCode.isNotBlank() && it.isNotBlank()) {
                                        title = "$customCourseCode - $it Outline"
                                    }
                                },
                                label = { Text("Subject / Course Name *") },
                                placeholder = { Text("e.g., Data Structures & Algorithms") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_custom_course_title"),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Title
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

                // 6. Session Year
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

                // 7. Content / Topics Notes
                OutlinedTextField(
                    value = outlineContent,
                    onValueChange = { outlineContent = it },
                    label = { Text("Weekly Breakdown & Key Topics") },
                    placeholder = { Text("Week 1: Introduction...\nWeek 2: Core Fundamentals...\nWeek 3: Practical...") },
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

                // 8. Outline PDF File Upload Section
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
                                    filePickerLauncher.launch("*/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_select_outline_pdf")
                            ) {
                                Text(
                                    text = if (isReadingFile) "Reading..." else if (fileName == null) "Attach PDF" else "Change",
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!fileName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "File: $fileName",
                                        fontSize = 12.sp,
                                        color = BrandNavy,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (fileBytes != null) {
                                        Text(
                                            text = "Size: ${FileUtils.formatFileSize(fileBytes?.size?.toLong() ?: 0L)}",
                                            fontSize = 10.sp,
                                            color = BrandTextMuted
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        fileName = null
                                        fileBytes = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove PDF",
                                        tint = Color(0xFFBA1A1A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switch Publish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish Outline", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Visible to Students & Faculty" else "Draft mode", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_outline_published")
                    )
                }

                }

                // 3. STICKY EXECUTIVE FOOTER
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("Dismiss", color = BrandTextMuted, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                var valid = true
                                var finalCourseId = selectedCourseId

                                if (isCustomCourseMode) {
                                    if (customCourseCode.isBlank() || customCourseTitle.isBlank()) {
                                        courseError = "Please enter both Course Code and Title"
                                        valid = false
                                    } else {
                                        finalCourseId = UUID.randomUUID().toString()
                                    }
                                } else {
                                    if (finalCourseId.isBlank()) {
                                        courseError = "Please select a course"
                                        valid = false
                                    }
                                }

                                if (title.isBlank()) {
                                    titleError = "Title is required"
                                    valid = false
                                }

                                if (valid) {
                                    onSave(
                                        outline?.id,
                                        finalCourseId,
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
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("btn_save_outline"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (outline == null) "Dispatch Outline" else "Save Changes",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
