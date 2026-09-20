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
import androidx.compose.material.icons.filled.Description
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
import com.example.data.model.DepartmentDto
import com.example.data.model.OfficialDocumentDto
import com.example.ui.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ScreenBg = Color(0xFFF6F6F6)
private val BrandNavy = Color(0xFF061B52)
private val BorderColor = Color(0xFFD5DDE7)
private val FieldLabelColor = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val AsteriskColor = Color(0xFFDC2626)
private val ErrorRed = Color(0xFFDC2626)
private val CardBg = Color.White

val DOCUMENT_TYPE_LABELS = mapOf(
    "academic_notice" to "Academic Notice",
    "admission" to "Admission Guide / Form",
    "rules_regulations" to "Rules & Regulations",
    "fee_structure" to "Official Fee Structure",
    "form" to "Application / Affiliation Form",
    "examination" to "Examination Date Sheet / Guidelines",
    "other" to "General Institutional Document"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentManageDialog(
    document: OfficialDocumentDto? = null,
    departments: List<DepartmentDto>,
    userRole: AppRole,
    userDepartmentId: String?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        description: String?,
        documentType: String,
        departmentId: String?,
        academicSession: String?,
        isPublished: Boolean,
        fileBytes: ByteArray?,
        fileName: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf(document?.title ?: "") }
    var description by remember { mutableStateOf(document?.description ?: "") }
    var documentType by remember { mutableStateOf(document?.documentType ?: "academic_notice") }
    var academicSession by remember { mutableStateOf(document?.academicSession ?: "2024-2025") }
    var selectedDeptId by remember {
        mutableStateOf(
            if (userRole == AppRole.HOD && userDepartmentId != null) userDepartmentId
            else document?.departmentId
        )
    }
    var isPublished by remember { mutableStateOf(document?.isPublished ?: true) }

    var fileName by remember { mutableStateOf(document?.fileName) }
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }

    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var fileError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isReadingFile by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            fileError = null
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
                .testTag("document_manage_dialog"),
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
                            modifier = Modifier.testTag("btn_close_document")
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
                        text = if (document == null) "Upload Official Document" else "Edit Official Document",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Publish verified institutional policies, forms, rules, and circulars.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. DOCUMENT TITLE
                    FieldLabel(label = "Document Title", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        placeholder = { Text("e.g., BS Fall 2025 Official Fee Schedule", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_document_title"),
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

                    // 2. CATEGORY / DOCUMENT TYPE
                    FieldLabel(label = "Document Category", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { typeDropdownExpanded = true }
                                .testTag("select_document_category"),
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
                                    text = DOCUMENT_TYPE_LABELS[documentType] ?: documentType,
                                    fontSize = 15.sp,
                                    color = BrandNavy,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Category",
                                    tint = TextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            DOCUMENT_TYPE_LABELS.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            fontWeight = if (documentType == key) FontWeight.Bold else FontWeight.Normal,
                                            color = if (documentType == key) BrandNavy else Color(0xFF1E293B)
                                        )
                                    },
                                    onClick = {
                                        documentType = key
                                        typeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. DESCRIPTION
                    FieldLabel(label = "Description & Purpose", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Briefly explain the contents or directives of this document...", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_document_description"),
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
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4. ACADEMIC SESSION
                    FieldLabel(label = "Academic Session", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = academicSession,
                        onValueChange = { academicSession = it },
                        placeholder = { Text("e.g., 2024-2025", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_document_session"),
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

                    // 5. AUDIENCE / DEPARTMENT
                    FieldLabel(label = "Audience", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "All College (Everyone)"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = userRole != AppRole.HOD) { deptDropdownExpanded = true }
                                .testTag("select_document_audience"),
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

                    // 6. DOCUMENT FILE ATTACHMENT
                    FieldLabel(label = "Document File (PDF / Document)", isRequired = true)
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
                                            text = fileName ?: "Selected Document",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (fileBytes != null) "${fileBytes!!.size / 1024} KB" else "Attached file",
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
                                .testTag("btn_pick_document_file"),
                            shape = RoundedCornerShape(14.dp),
                            color = CardBg,
                            border = BorderStroke(1.dp, if (fileError != null) ErrorRed else BorderColor)
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
                                    text = "Select official document file",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "PDF, DOC, DOCX up to 25MB",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        if (fileError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = fileError!!, color = ErrorRed, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // PRIMARY ACTION: Publish Document
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Please enter a document title"
                                valid = false
                            }
                            if (fileName.isNullOrBlank() && fileBytes == null) {
                                fileError = "Please select a document file to attach"
                                valid = false
                            }

                            if (valid) {
                                onSave(
                                    document?.id,
                                    title.trim(),
                                    description.trim().ifBlank { null },
                                    documentType,
                                    selectedDeptId,
                                    academicSession.trim().ifBlank { null },
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
                            .testTag("btn_save_document"),
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
                                text = "Saving Document...",
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
                                text = if (document == null) "Publish Document" else "Update Document",
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
