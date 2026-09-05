package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentLoginEntity
import com.example.data.repository.LoggedInStudentSummary
import com.example.data.repository.StudentAuditRepository
import kotlinx.coroutines.launch

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF5A6A85)
private val SuccessGreen = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudentLoginsView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auditRepo = remember { StudentAuditRepository.getInstance(context) }

    val allLogins by auditRepo.allLoginsFlow.collectAsState(initial = emptyList())
    val distinctStudents by auditRepo.distinctStudentsFlow.collectAsState(initial = emptyList())
    val totalLoginCount by auditRepo.totalLoginCountFlow.collectAsState(initial = 0)
    val distinctStudentCount by auditRepo.distinctStudentCountFlow.collectAsState(initial = 0)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Consolidated Student Directory, 1: All Login Sessions
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevelFilter by remember { mutableStateOf("All") } // "All", "BS", "Intermediate"
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var selectedStudentForHistory by remember { mutableStateOf<LoggedInStudentSummary?>(null) }

    // Filter logins
    val filteredLogins = remember(allLogins, searchQuery, selectedLevelFilter) {
        allLogins.filter { login ->
            val matchesLevel = when (selectedLevelFilter) {
                "BS" -> login.programLevel.equals("BS", ignoreCase = true)
                "Intermediate" -> login.programLevel.equals("Intermediate", ignoreCase = true)
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchesQuery = if (q.isEmpty()) true else {
                login.fullName.lowercase().contains(q) ||
                    login.rollNumber.lowercase().contains(q) ||
                    login.username.lowercase().contains(q) ||
                    login.programName.lowercase().contains(q)
            }
            matchesLevel && matchesQuery
        }
    }

    // Filter distinct students
    val filteredStudents = remember(distinctStudents, searchQuery, selectedLevelFilter) {
        distinctStudents.filter { student ->
            val matchesLevel = when (selectedLevelFilter) {
                "BS" -> student.programLevel.equals("BS", ignoreCase = true)
                "Intermediate" -> student.programLevel.equals("Intermediate", ignoreCase = true)
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchesQuery = if (q.isEmpty()) true else {
                student.fullName.lowercase().contains(q) ||
                    student.rollNumber.lowercase().contains(q) ||
                    student.username.lowercase().contains(q) ||
                    student.programName.lowercase().contains(q)
            }
            matchesLevel && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("admin_student_logins_screen")
    ) {
        // 1. Top Metrics Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BrandNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                                .background(BrandGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = BrandGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Student Logins & Sessions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Authorized audit trail of student credentials & logins",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("admin_clear_logins_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Unique Students",
                        value = "$distinctStudentCount",
                        sub = "Logged-in Students",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Total Logins",
                        value = "$totalLoginCount",
                        sub = "Recorded Sessions",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Active (24h)",
                        value = "${distinctStudents.count { it.isCurrentlyActive }}",
                        sub = "Recent Users",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = BrandNavy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BrandGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Verified Students (${filteredStudents.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Login Sessions (${filteredLogins.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
        }

        // 3. Search and Filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_logins_search_input"),
                placeholder = {
                    Text("Search by Name, Roll No, or Program...", fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = BrandTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All", "BS", "Intermediate").forEach { filterName ->
                    val isSelected = selectedLevelFilter == filterName
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLevelFilter = filterName },
                        label = {
                            Text(
                                text = if (filterName == "All") "All Programs" else "$filterName Students",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandNavy,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = BrandNavy
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

        // 4. Content List (Tab 0: Consolidated Student Directory; Tab 1: Login Sessions)
        if (selectedTab == 0) {
            // Logged-in Students Directory (One entry per student with aggregated stats)
            if (filteredStudents.isEmpty()) {
                EmptyStateView(
                    title = "No students found",
                    message = "Students who log in will be listed in this directory."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("admin_students_directory_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredStudents, key = { it.username }) { student ->
                        StudentDirectoryCard(
                            student = student,
                            onClick = { selectedStudentForHistory = student }
                        )
                    }
                }
            }
        } else {
            // All Login Sessions
            if (filteredLogins.isEmpty()) {
                EmptyStateView(
                    title = "No student logins found",
                    message = if (searchQuery.isNotEmpty()) "Try refining your search keyword."
                    else "Student login events will automatically be recorded here as students log in."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("admin_logins_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredLogins, key = { it.id }) { login ->
                        val matchingSummary = distinctStudents.firstOrNull { 
                            it.username.equals(login.username, ignoreCase = true) ||
                                it.rollNumber.equals(login.rollNumber, ignoreCase = true)
                        } ?: LoggedInStudentSummary(
                            username = login.username,
                            fullName = login.fullName,
                            rollNumber = login.rollNumber,
                            registrationNumber = login.registrationNumber,
                            programLevel = login.programLevel,
                            programName = login.programName,
                            semester = login.semester,
                            totalLogins = 1,
                            firstLoginFormatted = login.loginTimeFormatted,
                            lastLoginFormatted = login.loginTimeFormatted,
                            lastLoginTimestamp = login.loginTimestamp,
                            isCurrentlyActive = true
                        )
                        LoginSessionCard(
                            login = login,
                            onClick = { selectedStudentForHistory = matchingSummary }
                        )
                    }
                }
            }
        }
    }

    // Student Activity History Dialog
    selectedStudentForHistory?.let { student ->
        StudentActivityHistoryDialog(
            student = student,
            onDismiss = { selectedStudentForHistory = null }
        )
    }

    // Clear History Confirmation
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(30.dp)
                )
            },
            title = { Text("Clear Student Login History?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This action will remove all recorded login sessions from the local audit database. New logins will continue to be tracked.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        coroutineScope.launch {
                            auditRepo.clearAllAuditLogs()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandGoldLight
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = sub,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun LoginSessionCard(
    login: StudentLoginEntity,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("login_card_${login.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (login.programLevel.equals("BS", ignoreCase = true)) Color(0xFFE0E7FF) else Color(0xFFFEF3C7)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = login.fullName.take(2).uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (login.programLevel.equals("BS", ignoreCase = true)) BrandNavy else Color(0xFF92400E)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = login.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = login.rollNumber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandGold
                            )
                            if (!login.registrationNumber.isNullOrBlank()) {
                                Text(
                                    text = " • ${login.registrationNumber}",
                                    fontSize = 10.sp,
                                    color = BrandTextMuted
                                )
                            }
                        }
                    }
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (login.programLevel.equals("BS", ignoreCase = true)) Color(0xFFEEF2FF) else Color(0xFFFFFBEB)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = login.programLevel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (login.programLevel.equals("BS", ignoreCase = true)) BrandNavy else Color(0xFF92400E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Program & Semester info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = BrandTextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${login.programName}${if (!login.semester.isNullOrBlank()) " • ${login.semester}" else ""}",
                    fontSize = 11.sp,
                    color = BrandTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Timestamp and Device info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = login.loginTimeFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessGreen
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = BrandTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = login.deviceInfo.take(20),
                        fontSize = 10.sp,
                        color = BrandTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentDirectoryCard(
    student: LoggedInStudentSummary,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_dir_${student.username}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BrandNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.take(2).uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = student.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "${student.rollNumber} • ${student.programName}",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )
                    }
                }

                // Logins count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEF3FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${student.totalLogins} ${if (student.totalLogins == 1) "login" else "logins"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Last Active",
                        fontSize = 10.sp,
                        color = BrandTextMuted
                    )
                    Text(
                        text = student.lastLoginFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (student.isCurrentlyActive) SuccessGreen else BrandTextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "First Login",
                        fontSize = 10.sp,
                        color = BrandTextMuted
                    )
                    Text(
                        text = student.firstLoginFormatted,
                        fontSize = 11.sp,
                        color = BrandTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to view complete history",
                    fontSize = 11.sp,
                    color = BrandGold,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateView(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
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
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = BrandNavy,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            color = BrandTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
