package com.example.ui.screens.programs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.People
import com.example.data.datasource.OfficialFacultyData
import com.example.data.model.FacultyMember

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)
private val BrandLeadershipBadgeBg = Color(0xFFE8EDFA)

fun getProgramIcon(id: String): ImageVector {
    return when (id) {
        "ics", "bs_it" -> Icons.Default.Computer
        "icom", "bs_bba" -> Icons.Default.Business
        "bs_english" -> Icons.Default.AutoStories
        "bs_islamic_studies" -> Icons.Default.MenuBook
        "fsc_pre_eng" -> Icons.Default.Engineering
        "fsc_pre_med" -> Icons.Default.Biotech
        "fa" -> Icons.Default.MenuBook
        "bs_physics" -> Icons.Default.Science
        "bs_mathematics" -> Icons.Default.Calculate
        "bs_political_science" -> Icons.Default.AccountBalance
        "bs_urdu" -> Icons.Default.Translate
        "bs_chemistry" -> Icons.Default.Science
        "bs_zoology" -> Icons.Default.Pets
        else -> Icons.Default.MenuBook
    }
}

fun getProgramSummary(program: CollegeProgram): String {
    return when (program.id) {
        "bs_it" -> "Dive into computing, programming, and emerging technologies with access to modern IT labs."
        "bs_bba" -> "Develop critical management, marketing, financial, and organizational leadership capabilities."
        "bs_english" -> "Comprehensive study of English literature, linguistics, communication, and critical theory."
        "bs_islamic_studies" -> "In-depth study of Quranic sciences, Hadith, Islamic jurisprudence, and ethical history."
        "bs_physics" -> "Rigorous curriculum covering classical mechanics, electromagnetism, optics, and modern physics."
        "bs_mathematics" -> "Advanced exploration of pure and applied mathematics, calculus, and computational modeling."
        "bs_political_science" -> "Study of political systems, international relations, governance, and constitutional law."
        "bs_urdu" -> "Classical and modern Urdu literature, poetry, linguistics, and literary criticism."
        "bs_chemistry" -> "Study of organic, inorganic, physical, and analytical chemistry with lab investigations."
        "bs_zoology" -> "Comprehensive study of animal biology, genetics, ecology, physiology, and biodiversity."
        "ics" -> "Core foundations in computer science, physics, mathematics, and statistics."
        "fsc_pre_eng" -> "Preparation for engineering disciplines with physics, chemistry, and mathematics."
        "fsc_pre_med" -> "Preparation for medical and health sciences with biology, physics, and chemistry."
        "fa" -> "Humanities, arts, languages, and social science subjects."
        "icom" -> "Commerce, accounting, banking, economics, and business statistics."
        else -> "Official program offered at Govt Graduate College Mandi Bahauddin."
    }
}

@Composable
fun ProgramsScreen(
    onBack: () -> Unit,
    onNavigateToFaculty: (() -> Unit)? = null,
    onNavigateToCoursesOutline: (() -> Unit)? = null
) {
    var selectedProgram by remember { mutableStateOf<CollegeProgram?>(null) }
    var selectedLevel by remember { mutableStateOf(ProgramLevel.BS) }

    AnimatedContent(
        targetState = selectedProgram,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "programs_flow"
    ) { program ->
        if (program != null) {
            ProgramDetailView(
                program = program,
                onBack = { selectedProgram = null },
                onNavigateToFaculty = onNavigateToFaculty,
                onNavigateToCoursesOutline = onNavigateToCoursesOutline
            )
        } else {
            ProgramsCatalogView(
                selectedLevel = selectedLevel,
                onSelectLevel = { selectedLevel = it },
                onBack = onBack,
                onSelectProgram = { selectedProgram = it }
            )
        }
    }
}

// -------------------------------------------------------------
// PROGRAMS CATALOG VIEW (Reference Image: Screen 9)
// -------------------------------------------------------------
@Composable
private fun ProgramsCatalogView(
    selectedLevel: ProgramLevel,
    onSelectLevel: (ProgramLevel) -> Unit,
    onBack: () -> Unit,
    onSelectProgram: (CollegeProgram) -> Unit
) {
    val programs = OfficialProgramsData.getProgramsByLevel(selectedLevel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("programs_catalog_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("programs_main_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Programs",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Segmented Button / Tabs (BS Programs | Intermediate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                // BS Programs Tab
                val isBsSelected = selectedLevel == ProgramLevel.BS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isBsSelected) BrandNavy else Color.Transparent)
                        .clickable { onSelectLevel(ProgramLevel.BS) }
                        .padding(vertical = 10.dp)
                        .testTag("programs_tab_bs"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BS Programs",
                        fontSize = 13.sp,
                        fontWeight = if (isBsSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isBsSelected) Color.White else BrandNavy
                    )
                }

                // Intermediate Tab
                val isInterSelected = selectedLevel == ProgramLevel.INTERMEDIATE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isInterSelected) BrandNavy else Color.Transparent)
                        .clickable { onSelectLevel(ProgramLevel.INTERMEDIATE) }
                        .padding(vertical = 10.dp)
                        .testTag("programs_tab_intermediate"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Intermediate",
                        fontSize = 13.sp,
                        fontWeight = if (isInterSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isInterSelected) Color.White else BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section Title
            Text(
                text = if (selectedLevel == ProgramLevel.BS) "BS Degree Programs" else "Intermediate Programs",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List of Programs
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(programs) { program ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectProgram(program) }
                            .testTag("program_item_${program.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandIconBadgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getProgramIcon(program.id),
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = program.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = BrandNavy,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = BrandTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PROGRAM DETAIL VIEW (Reference Image: Screen 10)
// -------------------------------------------------------------
@Composable
private fun ProgramDetailView(
    program: CollegeProgram,
    onBack: () -> Unit,
    onNavigateToFaculty: (() -> Unit)? = null,
    onNavigateToCoursesOutline: (() -> Unit)? = null
) {
    val departmentFaculty = remember(program.departmentName) {
        OfficialFacultyData.getFacultyByDepartment(program.departmentName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("program_detail_screen_${program.id}")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("program_detail_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = program.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Center Hero Icon Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(BrandIconBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getProgramIcon(program.id),
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Program Description
            Text(
                text = getProgramSummary(program),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Key Program Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Duration
                    InfoItemRow(
                        icon = Icons.Default.AccessTime,
                        label = "Duration",
                        value = program.duration
                    )

                    HorizontalDivider(color = Color(0xFFF2F4F7))

                    // Eligibility
                    InfoItemRow(
                        icon = Icons.Default.Person,
                        label = "Eligibility",
                        value = program.eligibility
                    )

                    HorizontalDivider(color = Color(0xFFF2F4F7))

                    // Head of Department (HOD)
                    InfoItemRow(
                        icon = Icons.Default.Verified,
                        label = "Head of Department (HOD)",
                        value = program.hod
                    )
                }
            }

            if (onNavigateToCoursesOutline != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToCoursesOutline() }
                        .testTag("program_view_courses_outline_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "View Courses Outline",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Semester-wise courses and credits",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Department Faculty Section
            if (departmentFaculty.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Department Faculty (${departmentFaculty.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    if (onNavigateToFaculty != null) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandNavy,
                            modifier = Modifier
                                .clickable { onNavigateToFaculty() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    departmentFaculty.forEach { member ->
                        DepartmentFacultyMiniCard(member = member)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DepartmentFacultyMiniCard(member: FacultyMember) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dept_faculty_${member.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconVector = when {
                member.isPrincipal -> Icons.Default.AccountBalance
                member.isVicePrincipal -> Icons.Default.School
                member.isHod -> Icons.Default.Verified
                else -> Icons.Default.Person
            }
            val iconBg = if (member.isLeadership) BrandLeadershipBadgeBg else BrandIconBadgeBg

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = member.designation,
                    fontSize = 12.sp,
                    fontWeight = if (member.isLeadership) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (member.isLeadership) BrandNavy else Color(0xFF2B3A55)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = member.qualification,
                    fontSize = 11.sp,
                    color = BrandTextMuted
                )
            }
        }
    }
}

@Composable
private fun InfoItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BrandIconBadgeBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandNavy,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = BrandTextMuted,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = BrandNavy
            )
        }
    }
}
