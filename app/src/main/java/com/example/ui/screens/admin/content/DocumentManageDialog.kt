package com.example.ui.screens.admin.content

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppRole
import com.example.data.model.DepartmentDto
import com.example.data.model.OfficialDocumentDto

private val BrandNavy = Color(0xFF061B52)
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("document_manage_dialog")
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
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (document == null) "Upload Document" else "Edit Document",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Official Institutional PDF / Form",
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
                                    imageVector = Icons.Default.UploadFile,
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PDF / Document File *",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }

                            Button(
                                onClick = {
                                    val safeTitle = title.trim().replace(" ", "_").lowercase().ifEmpty { "doc" }
                                    val sampleName = "${safeTitle}_${System.currentTimeMillis() % 1000}.pdf"
                                    fileName = sampleName
                                    fileBytes = "GGC M.B.Din Official Document PDF Content Stream".toByteArray()
                                    fileError = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_select_doc_file")
                            ) {
                                Text(if (fileName == null) "Select File" else "Change", fontSize = 11.sp)
                            }
                        }

                        if (!fileName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "File: $fileName",
                                fontSize = 12.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Medium
                            )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish Document", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Publicly downloadable" else "Draft mode", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_doc_published")
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
                            .weight(1f)
                            .testTag("btn_save_doc"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (document == null) "Upload" else "Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
