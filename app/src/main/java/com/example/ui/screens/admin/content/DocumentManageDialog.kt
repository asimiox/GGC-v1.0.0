package com.example.ui.screens.admin.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDeep = Color(0xFF030D29)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF7E7A9)
private val BrandTextMuted = Color(0xFF5A6A85)

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
                        fileName = realName
                        fileBytes = bytes
                        fileError = null
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
                .testTag("document_manage_dialog")
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
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
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
                                        .size(44.dp)
                                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                                        .border(BorderStroke(1.5.dp, BrandGold.copy(alpha = 0.7f)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                                        contentDescription = "College Seal",
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.1.sp,
                                            color = BrandGoldLight
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Official",
                                            tint = BrandGold,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                    Text(
                                        text = if (document == null) "Official Document Dispatch" else "Edit Regulatory Document",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Directorate of Official Records & Institutional Archives",
                                        fontSize = 10.5.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.10f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 2. SCROLLABLE FORM BODY
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFC))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Authority Notice Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "OFFICIAL RECORD REPOSITORY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavy,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Documents published here are made accessible across the official college portal for faculty, students, and institutional review.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = if (it.isBlank()) "Title is required" else null
                        },
                        label = { Text("Document Title *") },
                        placeholder = { Text("e.g., Code of Conduct & Regulations") },
                        isError = titleError != null,
                        supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_doc_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Document Type
                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = DOCUMENT_TYPE_LABELS[documentType] ?: documentType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Document Classification") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_doc_type"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                focusedLabelColor = BrandNavy
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            DOCUMENT_TYPE_LABELS.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        documentType = key
                                        typeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Academic Session
                    OutlinedTextField(
                        value = academicSession,
                        onValueChange = { academicSession = it },
                        label = { Text("Academic Session") },
                        placeholder = { Text("2024-2025") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_doc_session"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Department
                    val isHod = userRole == AppRole.HOD
                    val selectedDeptName = departments.firstOrNull { it.id == selectedDeptId }?.name ?: "All College (General Document)"

                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded && !isHod,
                        onExpandedChange = { if (!isHod) deptDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDeptName,
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isHod,
                            label = { Text(if (isHod) "Department (Locked)" else "Department") },
                            trailingIcon = {
                                if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_doc_dept"),
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
                                    text = { Text("All College (General Document)") },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Brief summary of what this document contains...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_doc_desc"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // File Upload Section (Stored in Supabase Storage)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFD6DFEB))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = BrandNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Official PDF / Circular *",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandNavy
                                    )
                                }

                                Button(
                                    onClick = {
                                        filePickerLauncher.launch("*/*")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_select_doc_file")
                                ) {
                                    Text(
                                        text = if (isReadingFile) "Reading..." else if (fileName == null) "Select PDF" else "Change",
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (!fileName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "File: $fileName",
                                            fontSize = 12.sp,
                                            color = BrandNavy,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (fileBytes != null) {
                                            Text(
                                                text = "Size: ${FileUtils.formatFileSize(fileBytes?.size?.toLong() ?: 0L)}",
                                                fontSize = 10.sp,
                                                color = BrandTextMuted
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            fileName = null
                                            fileBytes = null
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove File",
                                            tint = Color(0xFFBA1A1A),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            } else if (fileError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = fileError ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBA1A1A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Publish Document", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                                Text(if (isPublished) "Publicly accessible & downloadable" else "Draft mode (Internal archival only)", fontSize = 11.sp, color = BrandTextMuted)
                            }
                            Switch(
                                checked = isPublished,
                                onCheckedChange = { isPublished = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                                modifier = Modifier.testTag("switch_doc_published")
                            )
                        }
                    }
                }

                // 3. STICKY EXECUTIVE FOOTER
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("Dismiss", color = BrandTextMuted, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                var valid = true
                                if (title.isBlank()) {
                                    titleError = "Title is required"
                                    valid = false
                                }
                                if (fileName.isNullOrBlank() && fileBytes == null) {
                                    fileError = "Please attach a document file"
                                    valid = false
                                }
                                if (valid) {
                                    onSave(
                                        document?.id,
                                        title,
                                        description,
                                        documentType,
                                        selectedDeptId,
                                        academicSession,
                                        isPublished,
                                        fileBytes,
                                        fileName
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("btn_save_doc"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (document == null) "Upload Document" else "Save Changes",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
