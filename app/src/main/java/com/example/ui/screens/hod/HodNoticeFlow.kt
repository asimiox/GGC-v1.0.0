package com.example.ui.screens.hod

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

data class NoticeTypeItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color
)

/**
 * FEATURE 3: NOTICE+ / ANNOUNCEMENTS+
 * Screen 1 — New Notice (Type of Notice Selection)
 * Options: College Event, Fees, Date Sheet, General Notice, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodNoticeCategorySelectScreen(
    state: HodUiState,
    onSelectCategory: (String) -> Unit,
    onProceedToCompose: () -> Unit,
    onBack: () -> Unit
) {
    val categories = listOf(
        NoticeTypeItem(
            title = "College Event",
            description = "Sports Gala, Seminars, Workshops & Cultural Activities",
            icon = Icons.Default.Event,
            iconBgColor = Color(0xFFE3F2FD),
            iconTint = Color(0xFF1976D2)
        ),
        NoticeTypeItem(
            title = "Fees",
            description = "Challan deadlines, installment plans & scholarship alerts",
            icon = Icons.Default.Paid,
            iconBgColor = Color(0xFFE8F5E9),
            iconTint = Color(0xFF2E7D32)
        ),
        NoticeTypeItem(
            title = "Date Sheet",
            description = "Mid-term & Final exam schedules, practical timings",
            icon = Icons.Default.MenuBook,
            iconBgColor = Color(0xFFFFF3E0),
            iconTint = Color(0xFFE65100)
        ),
        NoticeTypeItem(
            title = "General Notice",
            description = "Holidays circular, timetable updates & departmental instructions",
            icon = Icons.Default.Campaign,
            iconBgColor = Color(0xFFEDE7F6),
            iconTint = Color(0xFF512DA8)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notice+ / Announcements",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Step 1: Select Type of Notice",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose Notice Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Text(
                text = "Select the type of announcement you wish to broadcast to your students and faculty.",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            categories.forEach { item ->
                val isSelected = state.noticeCategory == item.title
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onSelectCategory(item.title)
                            onProceedToCompose()
                        }
                        .then(
                            if (isSelected) Modifier.border(2.dp, BrandNavy, RoundedCornerShape(16.dp))
                            else Modifier
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(item.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.iconTint,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BrandNavy.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Screen 2 — Compose & Send
 * - To: Whole IT
 * - → Semester Selection (dropdown/filter)
 * - Content: [text box]
 * - Button: [ Post ]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodNoticeComposeSendScreen(
    state: HodUiState,
    onUpdateNotice: (targetDept: String?, targetSem: String?, title: String?, content: String?) -> Unit,
    onPost: () -> Unit,
    onBack: () -> Unit
) {
    var semesterDropdownExpanded by remember { mutableStateOf(false) }

    val semesterOptions = listOf(
        "All Semesters",
        "1st Semester",
        "2nd Semester",
        "3rd Semester",
        "4th Semester",
        "5th Semester",
        "6th Semester",
        "7th Semester",
        "8th Semester",
        "Faculty Members Only"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Compose Notice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Category: ${state.noticeCategory}",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target Recipient Card (To: Whole IT / Semester Selection)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Broadcast Target",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 1: To: Whole IT (Locked to HOD's Department)
                    OutlinedTextField(
                        value = "To: Whole ${state.departmentName}",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = BrandNavy)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9FAFC),
                            unfocusedContainerColor = Color(0xFFF9FAFC)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 2: Semester Selection (dropdown/filter)
                    Text(
                        text = "→ Semester Filter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = semesterDropdownExpanded,
                        onExpandedChange = { semesterDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.noticeTargetSemester,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterDropdownExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("notice_semester_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BrandNavy
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = semesterDropdownExpanded,
                            onDismissRequest = { semesterDropdownExpanded = false }
                        ) {
                            semesterOptions.forEach { sem ->
                                DropdownMenuItem(
                                    text = { Text(sem) },
                                    onClick = {
                                        onUpdateNotice(null, sem, null, null)
                                        semesterDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (state.errorMessage != null) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Notice Title
            Column {
                Text(
                    text = "Notice Title *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.noticeTitle,
                    onValueChange = { onUpdateNotice(null, null, it, null) },
                    placeholder = { Text("e.g. 1st Semester Mid-Term Examination Schedule") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notice_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    ),
                    singleLine = true
                )
            }

            // Content [text box]
            Column {
                Text(
                    text = "Notice Content / Description *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.noticeContent,
                    onValueChange = { onUpdateNotice(null, null, null, it) },
                    placeholder = { Text("Enter the complete announcement details, dates, instructions...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("notice_content_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Button
            Button(
                onClick = onPost,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("post_notice_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Publishing Notice...")
                } else {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post Notice to ${state.noticeTargetSemester}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
