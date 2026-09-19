package com.example.ui.screens.admin.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppRole
import com.example.data.model.DepartmentDto
import com.example.ui.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Minimal, light, professional color system matching the design direction
private val ScreenBg = Color(0xFFF6F6F6)
private val BrandNavy = Color(0xFF061B52)
private val TextMain = Color(0xFF061B52)
private val TextSecondary = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFFFFFFF)
private val FieldLabelColor = Color(0xFF111827)
private val AsteriskColor = Color(0xFFDC2626)
private val GoldAccent = Color(0xFFC59B27)

val ANNOUNCEMENT_CATEGORIES = listOf(
    "General",
    "Academic",
    "Admissions",
    "Examinations",
    "Fee & Scholarships",
    "Events",
    "Sports",
    "Emergency"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementManageDialog(
    announcement: AnnouncementDto? = null,
    departments: List<DepartmentDto>,
    userRole: AppRole,
    userDepartmentId: String?,
    isSaving: Boolean = false,
    isUploadingFile: Boolean = false,
    uploadProgressMessage: String? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        content: String,
        category: String,
        departmentId: String?,
        isPinned: Boolean,
        isPublished: Boolean,
        attachmentBytes: ByteArray?,
        attachmentFileName: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf(announcement?.title ?: "") }
    var content by remember { mutableStateOf(announcement?.content ?: "") }
    var category by remember { mutableStateOf(announcement?.category ?: "") }
    
    val isHod = userRole == AppRole.HOD
    var selectedDeptId by remember {
        mutableStateOf(
            if (isHod && userDepartmentId != null) userDepartmentId
            else announcement?.departmentId
        )
    }
    
    // Priority: Normal vs Important (maps to isPinned for UI clarity)
    var isImportant by remember { mutableStateOf(announcement?.isPinned ?: false) }
    // All announcements created through here are published by default
    val isPublished = true

    var attachmentFileName by remember { mutableStateOf(announcement?.attachmentName) }
    var attachmentBytes by remember { mutableStateOf<ByteArray?>(null) }

    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var audienceMenuExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

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
                        attachmentFileName = realName
                        attachmentBytes = bytes
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
                .testTag("announcement_manage_dialog"),
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
                            enabled = !isSaving && !isUploadingFile,
                            modifier = Modifier.testTag("btn_close_announcement")
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

                // Progress Indicator when uploading or saving
                if (isUploadingFile || isSaving || isReadingFile) {
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
                    // SCREEN TITLE & SUBTITLE
                    Text(
                        text = if (announcement == null) "New Announcement" else "Edit Announcement",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Share important updates with your college community.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. TITLE *
                    FieldLabel(label = "Title", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (it.isNotBlank()) titleError = null
                        },
                        placeholder = {
                            Text(
                                text = "Enter announcement title",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        },
                        isError = titleError != null,
                        supportingText = titleError?.let { { Text(it, color = AsteriskColor, fontSize = 12.sp) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_announcement_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            errorBorderColor = AsteriskColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. MESSAGE *
                    FieldLabel(label = "Message", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = {
                                if (it.length <= 5000) {
                                    content = it
                                    if (it.isNotBlank()) contentError = null
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Write your announcement message here...",
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            isError = contentError != null,
                            supportingText = contentError?.let { { Text(it, color = AsteriskColor, fontSize = 12.sp) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("input_announcement_content"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                errorBorderColor = AsteriskColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Text(
                            text = "${content.length}/5000",
                            fontSize = 11.sp,
                            color = TextSecondary.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. CATEGORY *
                    FieldLabel(label = "Category", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryMenuExpanded = true }
                                .testTag("dropdown_announcement_category"),
                            shape = RoundedCornerShape(12.dp),
                            color = InputBg,
                            border = BorderStroke(1.dp, if (categoryError != null) AsteriskColor else BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (category.isNotBlank()) category else "Select category",
                                    fontSize = 14.sp,
                                    color = if (category.isNotBlank()) TextMain else TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (categoryMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            ANNOUNCEMENT_CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = if (cat == category) FontWeight.Bold else FontWeight.Normal,
                                            color = if (cat == category) BrandNavy else TextMain
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        categoryError = null
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (categoryError != null) {
                        Text(
                            text = categoryError ?: "",
                            color = AsteriskColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4. AUDIENCE *
                    FieldLabel(label = "Audience", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))

                    val selectedDept = departments.firstOrNull { it.id == selectedDeptId }
                    val audienceDisplay = if (selectedDeptId == null) {
                        "All College (Students & Faculty)"
                    } else {
                        selectedDept?.name ?: "Selected Department"
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isHod) { audienceMenuExpanded = true }
                                .testTag("dropdown_announcement_dept"),
                            shape = RoundedCornerShape(12.dp),
                            color = InputBg,
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = audienceDisplay,
                                    fontSize = 14.sp,
                                    color = TextMain,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!isHod) {
                                    Icon(
                                        imageVector = if (audienceMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (!isHod) {
                            DropdownMenu(
                                expanded = audienceMenuExpanded,
                                onDismissRequest = { audienceMenuExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "All College (Students & Faculty)",
                                            fontWeight = if (selectedDeptId == null) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedDeptId == null) BrandNavy else TextMain
                                        )
                                    },
                                    onClick = {
                                        selectedDeptId = null
                                        audienceMenuExpanded = false
                                    }
                                )
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = dept.name,
                                                fontWeight = if (selectedDeptId == dept.id) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedDeptId == dept.id) BrandNavy else TextMain
                                            )
                                        },
                                        onClick = {
                                            selectedDeptId = dept.id
                                            audienceMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5. ATTACHMENT (OPTIONAL)
                    FieldLabel(label = "Attachment", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { filePickerLauncher.launch("*/*") }
                            .testTag("btn_attach_announcement_file"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rounded attachment icon badge
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE5EDF8),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = BrandNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (attachmentFileName.isNullOrBlank()) "Add attachment" else attachmentFileName ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = if (attachmentFileName.isNullOrBlank()) FontWeight.Medium else FontWeight.SemiBold,
                                    color = TextMain,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val subText = if (attachmentFileName.isNullOrBlank()) {
                                    "PDF or image"
                                } else {
                                    val sizeText = if (attachmentBytes != null) {
                                        FileUtils.formatFileSize(attachmentBytes?.size?.toLong() ?: 0L)
                                    } else "Document attached"
                                    sizeText
                                }
                                Text(
                                    text = subText,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (!attachmentFileName.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        attachmentFileName = null
                                        attachmentBytes = null
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove attachment",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 6. PRIORITY (Normal | Important segmented control)
                    Text(
                        text = "Priority",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = FieldLabelColor
                    )
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
                            // Normal tab
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isImportant = false },
                                shape = RoundedCornerShape(20.dp),
                                color = if (!isImportant) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Normal",
                                        fontSize = 13.sp,
                                        fontWeight = if (!isImportant) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (!isImportant) Color.White else TextSecondary
                                    )
                                }
                            }

                            // Important tab
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isImportant = true },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isImportant) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Important",
                                        fontSize = 13.sp,
                                        fontWeight = if (isImportant) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isImportant) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // PRIMARY ACTION: Publish Announcement
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Please enter an announcement title"
                                valid = false
                            }
                            if (content.isBlank()) {
                                contentError = "Please write an announcement message"
                                valid = false
                            }
                            if (category.isBlank()) {
                                categoryError = "Please select a category"
                                valid = false
                            }

                            if (valid) {
                                onSave(
                                    announcement?.id,
                                    title.trim(),
                                    content.trim(),
                                    category,
                                    selectedDeptId,
                                    isImportant,
                                    isPublished,
                                    attachmentBytes,
                                    attachmentFileName
                                )
                            }
                        },
                        enabled = !isSaving && !isUploadingFile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_announcement"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandNavy,
                            disabledContainerColor = BrandNavy.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSaving || isUploadingFile) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isUploadingFile) "Uploading..." else "Publishing...",
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
                                text = if (announcement == null) "Publish Announcement" else "Update Announcement",
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
