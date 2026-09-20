package com.example.ui.screens.admin.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

private val ScreenBg = Color(0xFFF6F6F6)
private val BrandNavy = Color(0xFF061B52)
private val BorderColor = Color(0xFFD5DDE7)
private val FieldLabelColor = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val AsteriskColor = Color(0xFFDC2626)
private val ErrorRed = Color(0xFFDC2626)
private val CardBg = Color.White

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
    var isSaving by remember { mutableStateOf(false) }

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
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .testTag("course_outline_manage_dialog"),
            color = ScreenBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // ----------------------------------------------------
                // TOP APP BAR (HEADER)
                // ----------------------------------------------------
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ScreenBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            enabled = !isSaving && !isReadingFile,
                            modifier = Modifier.testTag("btn_close_outline")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Official GGC Crest / Logo
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(0.5.dp, BorderColor),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(3.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                                    contentDescription = "GGC Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                color = BrandNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Official App",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (isReadingFile || isSaving) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = BrandNavy,
                        trackColor = BorderColor
                    )
                }

                // ----------------------------------------------------
                // SCROLLABLE FORM
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // Page Title & Subtitle
                    Text(
                        text = if (outline == null) "New Course Outline" else "Edit Course Outline",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Publish approved syllabi, course outlines, and academic learning objectives.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. DEPARTMENT
                    FieldLabel(label = "Department", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    val selectedDeptName = effectiveDepartments.firstOrNull { it.id == selectedDeptId }?.name ?: "Select Department"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isHod) { deptDropdownExpanded = true }
                                .testTag("select_outline_department"),
                            shape = RoundedCornerShape(14.dp),
                            color = CardBg,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedDeptName,
                                    fontSize = 15.sp,
                                    color = BrandNavy,
                                    fontWeight = FontWeight.Medium
                                )
                                if (!isHod) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select Department",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        }

                        if (!isHod) {
                            DropdownMenu(
                                expanded = deptDropdownExpanded,
                                onDismissRequest = { deptDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(Color.White)
                            ) {
                                effectiveDepartments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = dept.name,
                                                fontWeight = if (selectedDeptId == dept.id) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedDeptId == dept.id) BrandNavy else Color(0xFF1E293B)
                                            )
                                        },
                                        onClick = {
                                            selectedDeptId = dept.id
                                            val newPrograms = effectiveAllPrograms.filter { it.departmentId == dept.id }
                                            selectedProgramId = newPrograms.firstOrNull()?.id
                                            deptDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. PROGRAM & SEMESTER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Degree Program
                        Column(modifier = Modifier.weight(1.3f)) {
                            FieldLabel(label = "Degree Program", isRequired = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            val selectedProgName = filteredPrograms.firstOrNull { it.id == selectedProgramId }?.title ?: "Select Program"
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { progDropdownExpanded = true }
                                        .testTag("select_outline_program"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardBg,
                                    border = BorderStroke(1.dp, BorderColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = selectedProgName,
                                            fontSize = 13.sp,
                                            color = BrandNavy,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Program",
                                            tint = TextSecondary
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = progDropdownExpanded,
                                    onDismissRequest = { progDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(Color.White)
                                ) {
                                    filteredPrograms.forEach { prog ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = prog.title,
                                                    fontWeight = if (selectedProgramId == prog.id) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedProgramId == prog.id) BrandNavy else Color(0xFF1E293B)
                                                )
                                            },
                                            onClick = {
                                                selectedProgramId = prog.id
                                                progDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Semester
                        Column(modifier = Modifier.weight(0.7f)) {
                            FieldLabel(label = "Semester", isRequired = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { semDropdownExpanded = true }
                                        .testTag("select_outline_semester"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardBg,
                                    border = BorderStroke(1.dp, BorderColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Sem $semesterNumber",
                                            fontSize = 13.sp,
                                            color = BrandNavy,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Semester",
                                            tint = TextSecondary
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = semDropdownExpanded,
                                    onDismissRequest = { semDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .background(Color.White)
                                ) {
                                    (1..8).forEach { sem ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Semester $sem",
                                                    fontWeight = if (semesterNumber == sem) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (semesterNumber == sem) BrandNavy else Color(0xFF1E293B)
                                                )
                                            },
                                            onClick = {
                                                semesterNumber = sem
                                                semDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. COURSE
                    FieldLabel(label = "Course Subject", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isCustomCourseMode) {
                        val currentCourse = filteredCourses.firstOrNull { it.id == selectedCourseId }
                        val courseDisplayText = if (currentCourse != null) "${currentCourse.code}: ${currentCourse.title}" else "Select Course"
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { courseDropdownExpanded = true }
                                    .testTag("select_outline_course"),
                                shape = RoundedCornerShape(14.dp),
                                color = CardBg,
                                border = BorderStroke(1.dp, if (courseError != null) ErrorRed else BorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = courseDisplayText,
                                        fontSize = 14.sp,
                                        color = BrandNavy,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select Course",
                                        tint = TextSecondary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = courseDropdownExpanded,
                                onDismissRequest = { courseDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(Color.White)
                            ) {
                                filteredCourses.forEach { c ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "${c.code} - ${c.title}",
                                                    fontWeight = if (selectedCourseId == c.id) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedCourseId == c.id) BrandNavy else Color(0xFF1E293B)
                                                )
                                                Text(
                                                    text = "${c.creditHours} Credit Hours",
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCourseId = c.id ?: ""
                                            title = "${c.code} - ${c.title} Outline"
                                            courseError = null
                                            courseDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = customCourseCode,
                                onValueChange = { customCourseCode = it },
                                placeholder = { Text("Code (e.g., CS-201)", color = TextSecondary) },
                                modifier = Modifier.weight(0.8f),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CardBg,
                                    unfocusedContainerColor = CardBg,
                                    focusedBorderColor = BrandNavy,
                                    unfocusedBorderColor = BorderColor
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = customCourseTitle,
                                onValueChange = {
                                    customCourseTitle = it
                                    title = "$customCourseCode - $customCourseTitle Outline"
                                },
                                placeholder = { Text("Course Title", color = TextSecondary) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CardBg,
                                    unfocusedContainerColor = CardBg,
                                    focusedBorderColor = BrandNavy,
                                    unfocusedBorderColor = BorderColor
                                ),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (!isCustomCourseMode) "Can't find your course? Enter custom course name" else "Return to standard catalog courses",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavy,
                        modifier = Modifier
                            .clickable { isCustomCourseMode = !isCustomCourseMode }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. OUTLINE TITLE
                    FieldLabel(label = "Outline Document Title", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        placeholder = { Text("e.g., CS-101 - Introduction to Computing Outline", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_outline_title"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg,
                            disabledContainerColor = CardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor,
                            errorBorderColor = ErrorRed,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        singleLine = true,
                        isError = titleError != null
                    )
                    if (titleError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = titleError!!, color = ErrorRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. ACADEMIC SESSION
                    FieldLabel(label = "Session / Academic Year", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sessionYear,
                        onValueChange = { sessionYear = it },
                        placeholder = { Text("e.g., 2024-2028", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_outline_session"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg,
                            disabledContainerColor = CardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6. SYLLABUS / DESCRIPTION
                    FieldLabel(label = "Learning Objectives & Weekly Syllabus", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = outlineContent,
                        onValueChange = { outlineContent = it },
                        placeholder = { Text("Enter course objectives, recommended textbooks, or weekly breakdown...", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("input_outline_content"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg,
                            disabledContainerColor = CardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7. SYLLABUS FILE ATTACHMENT
                    FieldLabel(label = "Syllabus / Outline PDF Document", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!fileName.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFEBEE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = fileName ?: "Attached Syllabus",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (fileBytes != null) "${fileBytes!!.size / 1024} KB" else "Syllabus document",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        fileName = null
                                        fileBytes = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove file",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filePickerLauncher.launch("*/*") }
                                .testTag("btn_pick_outline_file"),
                            shape = RoundedCornerShape(14.dp),
                            color = CardBg,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEEF2F7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = BrandNavy,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Attach syllabus PDF file",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "PDF or DOCX document",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // PRIMARY ACTION: Publish Course Outline
                    Button(
                        onClick = {
                            var valid = true
                            var finalCourseId = selectedCourseId
                            if (isCustomCourseMode) {
                                if (customCourseCode.isBlank() || customCourseTitle.isBlank()) {
                                    courseError = "Please enter both course code and title"
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
                                titleError = "Please enter an outline title"
                                valid = false
                            }

                            if (valid) {
                                onSave(
                                    outline?.id,
                                    finalCourseId,
                                    selectedProgramId,
                                    selectedDeptId,
                                    title.trim(),
                                    sessionYear.trim().ifBlank { null },
                                    semesterNumber,
                                    outlineContent.trim().ifBlank { null },
                                    isPublished,
                                    fileBytes,
                                    fileName
                                )
                            }
                        },
                        enabled = !isSaving && !isReadingFile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_outline"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandNavy,
                            disabledContainerColor = BrandNavy.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSaving || isReadingFile) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Saving Outline...",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (outline == null) "Publish Course Outline" else "Update Course Outline",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(label: String, isRequired: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = FieldLabelColor
        )
        if (isRequired) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "*",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AsteriskColor
            )
        } else {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(Optional)",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}
