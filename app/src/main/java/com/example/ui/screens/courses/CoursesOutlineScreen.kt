package com.example.ui.screens.courses

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.OfficialCoursesOutlineData
import com.example.data.model.CourseItem
import com.example.data.model.ProgramCourseOutline

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandTextDark = Color(0xFF2B3A55)
private val BrandBadgeBg = Color(0xFFEEF3FF)
private val BrandAccent = Color(0xFF1E3A8A)

@Composable
fun CoursesOutlineScreen(
    initialProgramId: String? = null,
    onBack: () -> Unit
) {
    val allPrograms = remember { OfficialCoursesOutlineData.allPrograms }
    var selectedProgram by remember {
        mutableStateOf(
            if (initialProgramId != null) OfficialCoursesOutlineData.getProgramById(initialProgramId)
            else null
        )
    }

    if (selectedProgram != null) {
        ProgramSemestersOutlineView(
            program = selectedProgram!!,
            onBackToPrograms = { selectedProgram = null },
            onExit = onBack
        )
    } else {
        ProgramsOutlineListView(
            programs = allPrograms,
            onSelectProgram = { program -> selectedProgram = program },
            onBack = onBack
        )
    }
}

// -------------------------------------------------------------
// 1. PROGRAMS LIST VIEW
// -------------------------------------------------------------
@Composable
private fun ProgramsOutlineListView(
    programs: List<ProgramCourseOutline>,
    onSelectProgram: (ProgramCourseOutline) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val bsPrograms = remember(searchQuery) {
        OfficialCoursesOutlineData.bsPrograms.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.headOfDepartment.contains(searchQuery, ignoreCase = true)
        }
    }
    val interPrograms = remember(searchQuery) {
        OfficialCoursesOutlineData.intermediatePrograms.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("courses_outline_programs_list")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("courses_outline_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Courses Outline",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Government Graduate College Mandi Bahauddin",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search program or subject...",
                        fontSize = 13.sp,
                        color = BrandTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = BrandTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("courses_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFD),
                    unfocusedContainerColor = Color(0xFFF8FAFD)
                ),
                singleLine = true
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        // Program Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "BS Programs (4 Years / 8 Semesters)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            items(bsPrograms, key = { it.id }) { program ->
                BsProgramCard(
                    program = program,
                    onClick = { onSelectProgram(program) }
                )
            }

            if (interPrograms.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Intermediate Programs (2 Years)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                items(interPrograms, key = { it.id }) { program ->
                    IntermediateProgramCard(
                        program = program,
                        onClick = { onSelectProgram(program) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// BS PROGRAM CARD
// -------------------------------------------------------------
@Composable
private fun BsProgramCard(
    program: ProgramCourseOutline,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("program_outline_card_${program.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BrandBadgeBg),
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

                    Column {
                        Text(
                            text = program.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "HOD: ${program.headOfDepartment}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandTextDark
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Outline",
                    tint = BrandNavy,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (!program.about.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = program.about,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandBadgeBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "8 Semesters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${program.totalCourses} Courses",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${program.totalCredits} Cr Total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextDark
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// INTERMEDIATE PROGRAM CARD
// -------------------------------------------------------------
@Composable
private fun IntermediateProgramCard(
    program: ProgramCourseOutline,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("program_outline_card_${program.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = program.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Duration: ${program.duration} • ${program.eligibility}",
                        fontSize = 11.sp,
                        color = BrandTextMuted
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = BrandNavy,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 2. PROGRAM SEMESTERS & COURSES VIEW
// -------------------------------------------------------------
@Composable
private fun ProgramSemestersOutlineView(
    program: ProgramCourseOutline,
    onBackToPrograms: () -> Unit,
    onExit: () -> Unit
) {
    var selectedSemesterIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("program_semesters_outline_view")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToPrograms,
                modifier = Modifier.testTag("program_outline_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Programs",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Head: ${program.headOfDepartment}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        if (program.isIntermediate || program.semesters.isEmpty()) {
            // Intermediate note from PDF
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = program.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "Duration: ${program.duration}",
                            fontSize = 13.sp,
                            color = BrandTextDark
                        )
                        Text(
                            text = "Eligibility: ${program.eligibility}",
                            fontSize = 13.sp,
                            color = BrandTextDark
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandBadgeBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = BrandNavy,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = program.note ?: "Course-wise/semester-wise outlines for Intermediate programs are not yet published on the college website.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = BrandNavy,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        } else {
            // BS Program with 8 Semesters
            val currentSemester = program.semesters.getOrNull(selectedSemesterIndex) ?: program.semesters.first()

            // Horizontal Scrollable Tabs for Semesters
            ScrollableTabRow(
                selectedTabIndex = selectedSemesterIndex,
                containerColor = Color.White,
                contentColor = BrandNavy,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSemesterIndex]),
                        color = BrandNavy,
                        height = 3.dp
                    )
                },
                divider = {
                    HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
                }
            ) {
                program.semesters.forEachIndexed { index, semester ->
                    Tab(
                        selected = selectedSemesterIndex == index,
                        onClick = { selectedSemesterIndex = index },
                        text = {
                            Text(
                                text = "Sem ${semester.semesterNumber}",
                                fontSize = 13.sp,
                                fontWeight = if (selectedSemesterIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSemesterIndex == index) BrandNavy else BrandTextMuted
                            )
                        }
                    )
                }
            }

            // Semester Summary Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Semester ${currentSemester.semesterNumber} Outlines",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${currentSemester.courseCount} Courses",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandNavy
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandBadgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${currentSemester.totalCreditHours} Cr Total",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                }
            }

            // Course List for the Selected Semester
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("semester_courses_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(currentSemester.courses) { index, course ->
                    CourseCardItem(course = course, index = index + 1)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COURSE CARD ITEM
// -------------------------------------------------------------
@Composable
private fun CourseCardItem(
    course: CourseItem,
    index: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("course_item_${course.code}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Row 1: Code and Credit Hours Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Code badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = course.code,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }

                // Credit hours
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (course.creditHours > 0) Color(0xFFF1F5F9) else Color(0xFFFFF7ED))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (course.creditHours > 0) "${course.creditHours} Credit Hours" else "Non-Credit (0 Cr)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (course.creditHours > 0) BrandTextDark else Color(0xFFC2410C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Course Title
            Text(
                text = course.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandNavy,
                lineHeight = 20.sp
            )

            // Row 3: Description / Classification (if non-empty)
            if (course.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandTextMuted)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }
            }
        }
    }
}
