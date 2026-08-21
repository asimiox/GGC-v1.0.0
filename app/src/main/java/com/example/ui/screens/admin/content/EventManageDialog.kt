package com.example.ui.screens.admin.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
private val BrandTextMuted = Color(0xFF5A6A85)

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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("event_manage_dialog")
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
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (event == null) "New Event" else "Edit Event",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "College Calendar & Activities",
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

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = if (it.isBlank()) "Title is required" else null
                    },
                    label = { Text("Event Title *") },
                    placeholder = { Text("e.g., Annual Sports Gala 2025") },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_event_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Event Description") },
                    placeholder = { Text("Details regarding schedule, eligibility, participants...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("input_event_desc"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Event Date and Time in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = {
                            eventDate = it
                            dateError = if (it.isBlank()) "Date is required" else null
                        },
                        label = { Text("Date (YYYY-MM-DD) *") },
                        isError = dateError != null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_event_date"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { eventTime = it },
                        label = { Text("Time") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_event_time"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Venue
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    placeholder = { Text("College Auditorium / Main Ground") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_event_venue"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_event_category"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
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

                // Department
                val isHod = userRole == AppRole.HOD
                val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "College-Wide Event"

                ExposedDropdownMenuBox(
                    expanded = deptDropdownExpanded && !isHod,
                    onExpandedChange = { if (!isHod) deptDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDeptName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isHod,
                        label = { Text(if (isHod) "Department (Locked)" else "Department Scope") },
                        trailingIcon = {
                            if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_event_dept"),
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
                            DropdownMenuItem(
                                text = { Text("College-Wide Event") },
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

                Spacer(modifier = Modifier.height(14.dp))

                // Banner section
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
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Event Poster / Banner",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }

                            Button(
                                onClick = {
                                    imagePickerLauncher.launch("image/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_attach_event_banner")
                            ) {
                                Text(
                                    text = if (isReadingFile) "Reading..." else if (bannerFileName == null) "Select Image" else "Change",
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!bannerFileName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Selected: $bannerFileName",
                                        fontSize = 12.sp,
                                        color = BrandNavy,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (bannerBytes != null) {
                                        Text(
                                            text = "Size: ${FileUtils.formatFileSize(bannerBytes?.size?.toLong() ?: 0L)}",
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
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Image",
                                        tint = Color(0xFFBA1A1A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Upcoming Status", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isUpcoming) "Upcoming event" else "Past / Completed", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isUpcoming,
                        onCheckedChange = { isUpcoming = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_event_upcoming")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish Status", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Publicly visible" else "Draft mode", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_event_published")
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
                            .weight(1f)
                            .testTag("btn_save_event"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (event == null) "Create Event" else "Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
