package com.example.ui.screens.admin.content

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AppRole
import com.example.data.model.CollegeEventDto
import com.example.data.model.DepartmentDto
import com.example.ui.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFFF6F6F6)
private val BrandNavy = Color(0xFF061B52)
private val BorderColor = Color(0xFFD5DDE7)
private val FieldLabelColor = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val AsteriskColor = Color(0xFFDC2626)
private val ErrorRed = Color(0xFFDC2626)
private val BrandGold = Color(0xFFC59B27)
private val CardBg = Color.White

val EVENT_CATEGORIES = listOf(
    "College",
    "Academic",
    "Seminar",
    "Workshop",
    "Sports",
    "Cultural",
    "Convocation",
    "Competitions",
    "Exhibition"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManageDialog(
    event: CollegeEventDto? = null,
    departments: List<DepartmentDto>,
    userRole: AppRole,
    userDepartmentId: String?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        description: String,
        eventDate: String,
        eventTime: String?,
        venue: String?,
        category: String,
        departmentId: String?,
        isUpcoming: Boolean,
        isPublished: Boolean,
        bannerBytes: ByteArray?,
        bannerFileName: String?
    ) -> Unit
) {
    val todayFormatted = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) }

    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var eventDate by remember { mutableStateOf(event?.eventDate ?: todayFormatted) }
    var eventTime by remember { mutableStateOf(event?.eventTime ?: "10:00 AM") }
    var venue by remember { mutableStateOf(event?.venue ?: "College Auditorium") }
    var category by remember { mutableStateOf(event?.category ?: "College") }
    var selectedDeptId by remember {
        mutableStateOf(
            if (userRole == AppRole.HOD && userDepartmentId != null) userDepartmentId
            else event?.departmentId
        )
    }
    var isUpcoming by remember { mutableStateOf(event?.isUpcoming ?: true) }
    var isPublished by remember { mutableStateOf(event?.isPublished ?: true) }

    var bannerFileName by remember { mutableStateOf(event?.attachmentName) }
    var bannerBytes by remember { mutableStateOf<ByteArray?>(null) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isReadingFile by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
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
                        bannerFileName = realName
                        bannerBytes = bytes
                    }
                }
            }
        }
    }

    // Native Android Date Picker
    fun showDatePicker() {
        val cal = Calendar.getInstance()
        val parts = eventDate.split("-")
        if (parts.size == 3) {
            val y = parts[0].toIntOrNull() ?: cal.get(Calendar.YEAR)
            val m = (parts[1].toIntOrNull() ?: 1) - 1
            val d = parts[2].toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
            cal.set(y, m, d)
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                eventDate = formatted
                dateError = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Native Android Time Picker
    fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                eventTime = String.format(Locale.ENGLISH, "%02d:%02d %s", hour12, minute, amPm)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
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
                .testTag("event_manage_dialog"),
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
                            modifier = Modifier.testTag("btn_close_event")
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
                        text = if (event == null) "New Event" else "Edit Event",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Schedule seminars, workshops, sports, and college activities.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. EVENT TITLE
                    FieldLabel(label = "Event Title", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        placeholder = { Text("e.g., Annual Sports Gala 2025", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_event_title"),
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

                    // 2. DESCRIPTION
                    FieldLabel(label = "Description & Details", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Write schedule, guest speakers, eligibility, or event details...", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("input_event_description"),
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

                    // 3. DATE & TIME (Side by side)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel(label = "Date", isRequired = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker() }
                                    .testTag("input_event_date"),
                                shape = RoundedCornerShape(14.dp),
                                color = CardBg,
                                border = BorderStroke(1.dp, if (dateError != null) ErrorRed else BorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = eventDate.ifBlank { "Select Date" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (eventDate.isNotBlank()) BrandNavy else TextSecondary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pick Date",
                                        tint = BrandNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (dateError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = dateError!!, color = ErrorRed, fontSize = 12.sp)
                            }
                        }

                        // Time
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel(label = "Time", isRequired = false)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTimePicker() }
                                    .testTag("input_event_time"),
                                shape = RoundedCornerShape(14.dp),
                                color = CardBg,
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = eventTime.ifBlank { "Set Time" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (eventTime.isNotBlank()) BrandNavy else TextSecondary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick Time",
                                        tint = BrandNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4. VENUE / LOCATION
                    FieldLabel(label = "Location / Venue", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        placeholder = { Text("e.g., College Main Auditorium", color = TextSecondary) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_event_venue"),
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

                    // 5. CATEGORY
                    FieldLabel(label = "Category", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryDropdownExpanded = true }
                                .testTag("select_event_category"),
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
                                    text = category,
                                    fontSize = 15.sp,
                                    color = BrandNavy,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Open Categories",
                                    tint = TextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            EVENT_CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal,
                                            color = if (category == cat) BrandNavy else Color(0xFF1E293B)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6. AUDIENCE / DEPARTMENT
                    FieldLabel(label = "Audience", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "All College (Everyone)"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = userRole != AppRole.HOD) { deptDropdownExpanded = true }
                                .testTag("select_event_audience"),
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
                                if (userRole != AppRole.HOD) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select Audience",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        }

                        if (userRole != AppRole.HOD) {
                            DropdownMenu(
                                expanded = deptDropdownExpanded,
                                onDismissRequest = { deptDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "All College (Everyone)",
                                            fontWeight = if (selectedDeptId == null) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedDeptId == null) BrandNavy else Color(0xFF1E293B)
                                        )
                                    },
                                    onClick = {
                                        selectedDeptId = null
                                        deptDropdownExpanded = false
                                    }
                                )
                                departments.forEach { dept ->
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
                                            deptDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7. EVENT BANNER / IMAGE
                    FieldLabel(label = "Event Banner / Poster", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!bannerFileName.isNullOrBlank()) {
                        // Selected image card
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
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = bannerFileName ?: "Selected Poster",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (bannerBytes != null) "${bannerBytes!!.size / 1024} KB" else "Attached banner",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        bannerFileName = null
                                        bannerBytes = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove banner",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Picker button
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { imagePickerLauncher.launch("image/*") }
                                .testTag("btn_pick_event_banner"),
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
                                    text = "Add event poster or banner",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "PNG, JPG up to 10MB",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 8. EVENT TIMELINE STATUS
                    FieldLabel(label = "Timeline Status", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFEFF2F7),
                        border = BorderStroke(0.5.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isUpcoming = true },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isUpcoming) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Upcoming Event",
                                        fontSize = 13.sp,
                                        fontWeight = if (isUpcoming) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isUpcoming) Color.White else TextSecondary
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isUpcoming = false },
                                shape = RoundedCornerShape(20.dp),
                                color = if (!isUpcoming) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Past / Completed",
                                        fontSize = 13.sp,
                                        fontWeight = if (!isUpcoming) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (!isUpcoming) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // PRIMARY ACTION: Publish Event
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Please enter an event title"
                                valid = false
                            }
                            if (eventDate.isBlank()) {
                                dateError = "Please specify the event date"
                                valid = false
                            }

                            if (valid) {
                                onSave(
                                    event?.id,
                                    title.trim(),
                                    description.trim(),
                                    eventDate.trim(),
                                    eventTime.trim().ifBlank { null },
                                    venue.trim().ifBlank { null },
                                    category,
                                    selectedDeptId,
                                    isUpcoming,
                                    isPublished,
                                    bannerBytes,
                                    bannerFileName
                                )
                            }
                        },
                        enabled = !isSaving && !isReadingFile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_event"),
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
                                text = "Saving Event...",
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
                                text = if (event == null) "Publish Event" else "Update Event",
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
