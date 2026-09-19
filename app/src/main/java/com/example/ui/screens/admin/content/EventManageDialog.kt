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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import java.util.Date
import java.util.Locale

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDeep = Color(0xFF030F30)
private val BrandNavySurface = Color(0xFFF4F7FC)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandTextDark = Color(0xFF0B1938)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFFFF7E6)
private val BrandGoldBorder = Color(0xFFE5C067)
private val BrandGreen = Color(0xFF1E7E34)

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
                .testTag("event_manage_dialog")
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
                            .padding(horizontal = 18.dp, vertical = 14.dp)
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
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, BrandGold.copy(alpha = 0.6f)),
                                    shadowElevation = 3.dp,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_ggc_logo),
                                            contentDescription = "GGC Official Crest",
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = Color(0xFFFFD56B)
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = if (event == null) "Schedule Official Event" else "Edit Event Particulars",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Higher Education Department • Institutional Calendar Wing",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD56B),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    val eventRef = event?.id?.take(8)?.uppercase() ?: "EVT-2026"
                                    Text(
                                        text = "CALENDAR DOCKET: GGC/MB-$eventRef",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.4.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            Surface(
                                color = if (isPublished) BrandGreen.copy(alpha = 0.35f) else Color(0xFF6C757D).copy(alpha = 0.35f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, if (isPublished) Color(0xFF81C784) else Color(0xFFADB5BD))
                            ) {
                                Text(
                                    text = if (isPublished) "STATUS: CALENDAR LIVE" else "STATUS: DRAFT SCHEDULE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // 2. SCROLLABLE FORM BODY
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    FormSectionHeader(
                        number = "01",
                        title = "EVENT TITLE & BRIEF PARTICULARS",
                        subtitle = "Official program name, purpose, and detailed schedule"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = if (it.isBlank()) "Event title is mandatory" else null
                        },
                        label = { Text("Event / Program Title *") },
                        placeholder = { Text("e.g. Annual Convocation Ceremony 2026") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Title,
                                contentDescription = null,
                                tint = if (titleError != null) Color(0xFFBA1A1A) else BrandNavy,
                                modifier = Modifier.size(19.dp)
                            )
                        },
                        isError = titleError != null,
                        supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_event_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy,
                            unfocusedBorderColor = Color(0xFFC7D2E2),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFBFE)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Event Program Description / Circular") },
                        placeholder = { Text("Details regarding schedule, eligibility, chief guests, instructions for students & staff...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("input_event_desc"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy,
                            unfocusedBorderColor = Color(0xFFC7D2E2),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFBFE)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FormSectionHeader(
                        number = "02",
                        title = "SCHEDULE, TIMING & VENUE",
                        subtitle = "Official date, timing and campus assembly venue"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = {
                                eventDate = it
                                dateError = if (it.isBlank()) "Date is mandatory" else null
                            },
                            label = { Text("Date (YYYY-MM-DD) *") },
                            placeholder = { Text("2026-10-15") },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                            },
                            isError = dateError != null,
                            supportingText = dateError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_event_date"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                focusedLabelColor = BrandNavy,
                                unfocusedBorderColor = Color(0xFFC7D2E2),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFFAFBFE)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = eventTime,
                            onValueChange = { eventTime = it },
                            label = { Text("Time") },
                            placeholder = { Text("10:00 AM") },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_event_time"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                focusedLabelColor = BrandNavy,
                                unfocusedBorderColor = Color(0xFFC7D2E2),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFFAFBFE)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Campus Venue / Hall") },
                        placeholder = { Text("College Main Auditorium, Sports Ground, etc.") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_event_venue"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy,
                            unfocusedBorderColor = Color(0xFFC7D2E2),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFBFE)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FormSectionHeader(
                        number = "03",
                        title = "CATEGORY & JURISDICTION",
                        subtitle = "Classification category and departmental host wing"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Event Classification") },
                            leadingIcon = {
                                Icon(Icons.Default.Event, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .testTag("dropdown_event_category"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                focusedLabelColor = BrandNavy,
                                unfocusedBorderColor = Color(0xFFC7D2E2),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFFAFBFE)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            EVENT_CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val isHod = userRole == AppRole.HOD
                    val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "All College (General Activity)"

                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded && !isHod,
                        onExpandedChange = { if (!isHod) deptDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDeptName,
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isHod,
                            label = { Text(if (isHod) "Organizing Wing (Your Department)" else "Organizing Department Scope") },
                            leadingIcon = {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = if (isHod) BrandGold else BrandNavy, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .testTag("dropdown_event_dept"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                focusedLabelColor = BrandNavy,
                                unfocusedBorderColor = Color(0xFFC7D2E2),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = if (isHod) Color(0xFFF7F8FA) else Color(0xFFFAFBFE)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        if (!isHod) {
                            ExposedDropdownMenu(
                                expanded = deptDropdownExpanded,
                                onDismissRequest = { deptDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All College (General Activity)") },
                                    onClick = {
                                        selectedDeptId = null
                                        deptDropdownExpanded = false
                                    }
                                )
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept.name) },
                                        onClick = {
                                            selectedDeptId = dept.id
                                            deptDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    FormSectionHeader(
                        number = "04",
                        title = "OFFICIAL BANNER / POSTER",
                        subtitle = "Attach official event flyer, brochure, or ceremony banner"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandNavySurface,
                        border = BorderStroke(1.dp, Color(0xFFD3DFEF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BrandNavy.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = BrandNavy,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Official Event Banner / Poster",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandTextDark
                                        )
                                        Text(
                                            text = "JPG, PNG brochure image",
                                            fontSize = 11.sp,
                                            color = BrandTextMuted
                                        )
                                    }
                                }

                                Button(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_attach_event_banner")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isReadingFile) "Reading..." else if (bannerFileName == null) "Select Image" else "Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (!bannerFileName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFDCE5F2), thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected Banner",
                                                tint = BrandGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = bannerFileName ?: "",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = BrandNavy,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val sizeText = if (bannerBytes != null) {
                                                    FileUtils.formatFileSize(bannerBytes?.size?.toLong() ?: 0L)
                                                } else "Attached Image"
                                                Text(
                                                    text = "Official Media • $sizeText",
                                                    fontSize = 10.sp,
                                                    color = BrandTextMuted
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                bannerFileName = null
                                                bannerBytes = null
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Banner",
                                                tint = Color(0xFFBA1A1A),
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    FormSectionHeader(
                        number = "05",
                        title = "CALENDAR DISPATCH POLICIES",
                        subtitle = "Upcoming status and public student/staff visibility"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isUpcoming) BrandGoldLight else Color(0xFFFAFBFE),
                        border = BorderStroke(1.dp, if (isUpcoming) BrandGoldBorder else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isUpcoming = !isUpcoming }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUpcoming) "Upcoming / Active Calendar Event" else "Past / Archived Event",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUpcoming) Color(0xFF745100) else BrandTextDark
                                )
                                Text(
                                    text = if (isUpcoming) "Displayed in active countdown & upcoming events cards" else "Preserved in institutional activity archive",
                                    fontSize = 11.sp,
                                    color = BrandTextMuted
                                )
                            }
                            Switch(
                                checked = isUpcoming,
                                onCheckedChange = { isUpcoming = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BrandGold,
                                    checkedTrackColor = BrandNavy,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("switch_event_upcoming")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPublished) Color(0xFFF1F8F3) else Color(0xFFFAFBFE),
                        border = BorderStroke(1.dp, if (isPublished) Color(0xFFA5D6A7) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPublished = !isPublished }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isPublished) "Public Calendar Release (Live)" else "Draft Schedule (Hidden)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPublished) BrandGreen else BrandTextDark
                                )
                                Text(
                                    text = if (isPublished) "Visible across student and faculty calendar feeds" else "Retained internally for administrative review",
                                    fontSize = 11.sp,
                                    color = BrandTextMuted
                                )
                            }
                            Switch(
                                checked = isPublished,
                                onCheckedChange = { isPublished = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrandGreen,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("switch_event_published")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3. STICKY FOOTER
                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                Surface(
                    color = Color(0xFFF9FBFE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFBAC7D5)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard Draft", color = BrandTextMuted, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                var valid = true
                                if (title.isBlank()) {
                                    titleError = "Title is required"
                                    valid = false
                                }
                                if (eventDate.isBlank()) {
                                    dateError = "Date is required"
                                    valid = false
                                }
                                if (valid) {
                                    onSave(
                                        event?.id,
                                        title,
                                        description,
                                        eventDate,
                                        eventTime,
                                        venue,
                                        category,
                                        selectedDeptId,
                                        isUpcoming,
                                        isPublished,
                                        bannerBytes,
                                        bannerFileName
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("btn_save_event"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandGold.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = if (isPublished) Icons.AutoMirrored.Filled.Send else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFFFFD56B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (event == null) "Schedule Event" else "Save Changes",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSectionHeader(
    number: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = BrandNavy,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = number,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD56B),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp,
                color = BrandNavy
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = BrandTextMuted
            )
        }
    }
}
