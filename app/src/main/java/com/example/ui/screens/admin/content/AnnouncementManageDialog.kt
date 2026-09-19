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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppRole
import com.example.data.model.DepartmentDto
import com.example.ui.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDeep = Color(0xFF030F30)
private val BrandNavySurface = Color(0xFFF4F7FC)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandTextDark = Color(0xFF0B1938)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFFFF7E6)
private val BrandGoldBorder = Color(0xFFE5C067)
private val BrandGreen = Color(0xFF1E7E34)

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
    var category by remember { mutableStateOf(announcement?.category ?: "General") }
    var selectedDeptId by remember {
        mutableStateOf(
            if (userRole == AppRole.HOD && userDepartmentId != null) userDepartmentId
            else announcement?.departmentId
        )
    }
    var isPinned by remember { mutableStateOf(announcement?.isPinned ?: false) }
    var isPublished by remember { mutableStateOf(announcement?.isPublished ?: true) }

    var attachmentFileName by remember { mutableStateOf(announcement?.attachmentName) }
    var attachmentBytes by remember { mutableStateOf<ByteArray?>(null) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

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
                .testTag("announcement_manage_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ==========================================
                // 1. OFFICIAL INSTITUTIONAL GOVERNMENT HEADER
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BrandNavyDeep, BrandNavy)
                            )
                        )
                ) {
                    // Subtle Top Gold Bar
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
                                // Official Crest Emblem
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
                                        text = if (announcement == null) "New Official Announcement" else "Edit Official Announcement",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Higher Education Department • Official Gazette Desk",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // Dismiss Icon Button
                            IconButton(
                                onClick = onDismiss,
                                enabled = !isSaving && !isUploadingFile,
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

                        // Official Gazette Docket Pill
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
                                    val docketRef = announcement?.id?.take(8)?.uppercase() ?: "DRAFT-2026"
                                    Text(
                                        text = "DISPATCH DOCKET: GGC/MB-$docketRef",
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
                                    text = if (isPublished) "STATUS: GAZETTE DISPATCH" else "STATUS: INTERNAL DRAFT",
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

                // Progress Banner (Saving / Uploading)
                if (isUploadingFile || isSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEBF3FF))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(15.dp),
                                    strokeWidth = 2.dp,
                                    color = BrandNavy
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isUploadingFile) {
                                        uploadProgressMessage ?: "Uploading signed circular scan to official repository..."
                                    } else {
                                        "Synchronizing and publishing gazette notice..."
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = BrandNavy,
                                trackColor = Color(0xFFCDDEFF)
                            )
                        }
                    }
                }

                // ==========================================
                // 2. SCROLLABLE FORM BODY (OFFICIAL GAZETTE SECTIONS)
                // ==========================================
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {

                    // ----------------------------------------------------
                    // SECTION 01: NOTIFICATION SUBJECT & CIRCULAR TEXT
                    // ----------------------------------------------------
                    FormSectionHeader(
                        number = "01",
                        title = "NOTIFICATION PARTICULARS",
                        subtitle = "Subject and directive order issued under official authority"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notice Subject Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = if (it.isBlank()) "Subject / Title is mandatory" else null
                        },
                        label = { Text("Subject / Title of Notification *") },
                        placeholder = { Text("e.g. Schedule of BS / Intermediate Annual Examination 2026") },
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
                            .testTag("input_announcement_title"),
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

                    // Notice Content Field (Editorial Style)
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            contentError = if (it.isBlank()) "Notification body text is required" else null
                        },
                        label = { Text("Official Circular / Order Text *") },
                        placeholder = { Text("Pursuant to the approval of the Academic Council / Principal, it is hereby notified for information of all concerned that...") },
                        isError = contentError != null,
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = contentError ?: "Official administrative text or student directive",
                                    color = if (contentError != null) Color(0xFFBA1A1A) else BrandTextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${content.length} chars",
                                    color = BrandTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("input_announcement_content"),
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

                    // ----------------------------------------------------
                    // SECTION 02: CLASSIFICATION & JURISDICTION
                    // ----------------------------------------------------
                    FormSectionHeader(
                        number = "02",
                        title = "CLASSIFICATION & JURISDICTION",
                        subtitle = "Select regulatory wing and institutional jurisdiction"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gazette Category / Branch") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .testTag("dropdown_announcement_category"),
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
                            ANNOUNCEMENT_CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(if (cat == category) BrandNavy else Color.LightGray)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = cat,
                                                fontWeight = if (cat == category) FontWeight.Bold else FontWeight.Normal,
                                                color = if (cat == category) BrandNavy else Color.Unspecified
                                            )
                                        }
                                    },
                                    onClick = {
                                        category = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Department Jurisdiction Scope Selector
                    val isHod = userRole == AppRole.HOD
                    val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "All College (General Notice)"

                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded && !isHod,
                        onExpandedChange = { if (!isHod) deptDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDeptName,
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isHod,
                            label = { Text(if (isHod) "Department Jurisdiction (Locked to Your Wing)" else "Administrative Scope / Jurisdiction") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = if (isHod) BrandGold else BrandNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            trailingIcon = {
                                if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .testTag("dropdown_announcement_dept"),
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
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = BrandNavy,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "All College (General Notice)",
                                                fontWeight = if (selectedDeptId == null) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedDeptId == null) BrandNavy else Color.Unspecified
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedDeptId = null
                                        deptDropdownExpanded = false
                                    }
                                )
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(if (selectedDeptId == dept.id) BrandNavy else Color.LightGray)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = dept.name,
                                                    fontWeight = if (selectedDeptId == dept.id) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedDeptId == dept.id) BrandNavy else Color.Unspecified
                                                )
                                            }
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // ----------------------------------------------------
                    // SECTION 03: OFFICIAL SIGNED ATTACHMENT / ANNEXURE
                    // ----------------------------------------------------
                    FormSectionHeader(
                        number = "03",
                        title = "ANNEXURE & ATTACHED DISPATCH",
                        subtitle = "Attach signed circular copy, gazette notification, or document scan"
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
                                            imageVector = Icons.Default.AttachFile,
                                            contentDescription = null,
                                            tint = BrandNavy,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Official Signed Copy (PDF / Scan)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandTextDark
                                        )
                                        Text(
                                            text = "Verified Annexure / Principal Office Circular",
                                            fontSize = 11.sp,
                                            color = BrandTextMuted
                                        )
                                    }
                                }

                                Button(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_attach_announcement_file")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isReadingFile) "Reading..." else if (attachmentFileName == null) "Attach Scan" else "Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (!attachmentFileName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
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
                                                contentDescription = "Verified File",
                                                tint = BrandGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = attachmentFileName ?: "",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = BrandNavy,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val sizeText = if (attachmentBytes != null) {
                                                    FileUtils.formatFileSize(attachmentBytes?.size?.toLong() ?: 0L)
                                                } else "Attached Document"
                                                Text(
                                                    text = "Official Annexure • $sizeText",
                                                    fontSize = 10.sp,
                                                    color = BrandTextMuted
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                attachmentFileName = null
                                                attachmentBytes = null
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Annexure",
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

                    // ----------------------------------------------------
                    // SECTION 04: DISPATCH DIRECTIVES & VISIBILITY
                    // ----------------------------------------------------
                    FormSectionHeader(
                        number = "04",
                        title = "DISPATCH POLICIES & CONTROLS",
                        subtitle = "Configure notice board priority and student/faculty publication"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Directive 1: Pinned / Priority Notice Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPinned) BrandGoldLight else Color(0xFFFAFBFE),
                        border = BorderStroke(1.dp, if (isPinned) BrandGoldBorder else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPinned = !isPinned }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isPinned) BrandGold.copy(alpha = 0.2f) else Color(0xFFECEFF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = if (isPinned) BrandGold else BrandTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Pin to College Gazette Header",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPinned) Color(0xFF745100) else BrandTextDark
                                    )
                                    Text(
                                        text = "Feature as urgent sticky notice at top of student and faculty feeds",
                                        fontSize = 11.sp,
                                        color = BrandTextMuted
                                    )
                                }
                            }

                            Switch(
                                checked = isPinned,
                                onCheckedChange = { isPinned = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BrandGold,
                                    checkedTrackColor = BrandNavy,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("switch_announcement_pinned")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Directive 2: Public Dispatch Card
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isPublished) BrandGreen.copy(alpha = 0.15f) else Color(0xFFECEFF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPublished) Icons.Default.NotificationsActive else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (isPublished) BrandGreen else BrandTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isPublished) "Public Gazette Release (Live)" else "Draft Mode (Administrative Review)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPublished) BrandGreen else BrandTextDark
                                    )
                                    Text(
                                        text = if (isPublished) "Instantly dispatched & accessible across all student and faculty apps" else "Hidden from public boards; retained for office clearance",
                                        fontSize = 11.sp,
                                        color = BrandTextMuted
                                    )
                                }
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
                                modifier = Modifier.testTag("switch_announcement_published")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ==========================================
                // 3. INSTITUTIONAL STICKY FOOTER ACTION BAR
                // ==========================================
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
                            enabled = !isSaving && !isUploadingFile,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFBAC7D5)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Discard Draft",
                                color = BrandTextMuted,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                var valid = true
                                if (title.isBlank()) {
                                    titleError = "Subject / Title is required"
                                    valid = false
                                }
                                if (content.isBlank()) {
                                    contentError = "Notification body text is required"
                                    valid = false
                                }
                                if (valid) {
                                    onSave(
                                        announcement?.id,
                                        title,
                                        content,
                                        category,
                                        selectedDeptId,
                                        isPinned,
                                        isPublished,
                                        attachmentBytes,
                                        attachmentFileName
                                    )
                                }
                            },
                            enabled = !isSaving && !isUploadingFile,
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("btn_save_announcement"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandNavy,
                                disabledContainerColor = BrandNavy.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandGold.copy(alpha = 0.5f))
                        ) {
                            if (isSaving || isUploadingFile) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isUploadingFile) "Uploading..." else "Dispatching...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = if (isPublished) Icons.AutoMirrored.Filled.Send else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD56B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (announcement == null) {
                                        if (isPublished) "Issue & Dispatch" else "Save as Draft"
                                    } else "Update Gazette Notice",
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

