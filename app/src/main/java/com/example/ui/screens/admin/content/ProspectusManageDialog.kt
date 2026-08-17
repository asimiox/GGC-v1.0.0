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
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.ProspectusDto

private val BrandNavy = Color(0xFF061B52)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandGold = Color(0xFFC59B27)

val PROSPECTUS_LEVELS = listOf(
    "Comprehensive (BS & Intermediate)",
    "BS 4-Year Programs",
    "Intermediate Programs",
    "Postgraduate"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProspectusManageDialog(
    prospectus: ProspectusDto? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        academicSession: String,
        programLevel: String?,
        description: String?,
        isCurrent: Boolean,
        isPublished: Boolean,
        fileBytes: ByteArray?,
        fileName: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf(prospectus?.title ?: "GGC Official Prospectus 2024-2025") }
    var academicSession by remember { mutableStateOf(prospectus?.academicSession ?: "2024-2025") }
    var programLevel by remember { mutableStateOf(prospectus?.programLevel ?: "Comprehensive (BS & Intermediate)") }
    var description by remember {
        mutableStateOf(
            prospectus?.description ?: "Official academic prospectus, code of conduct, fee regulations and admission eligibility."
        )
    }
    var isCurrent by remember { mutableStateOf(prospectus?.isCurrent ?: true) }
    var isPublished by remember { mutableStateOf(prospectus?.isPublished ?: true) }

    var fileName by remember { mutableStateOf(prospectus?.fileName) }
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }

    var levelDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var sessionError by remember { mutableStateOf<String?>(null) }
    var fileError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("prospectus_manage_dialog")
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
                                text = if (prospectus == null) "Upload Prospectus" else "Edit Prospectus",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Institutional Academic Guidebook",
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
                    label = { Text("Prospectus Title *") },
                    placeholder = { Text("e.g., GGC Official Prospectus 2024-2025") },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_prospectus_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Academic Session
                OutlinedTextField(
                    value = academicSession,
                    onValueChange = {
                        academicSession = it
                        sessionError = if (it.isBlank()) "Academic session is required" else null
                    },
                    label = { Text("Academic Session *") },
                    placeholder = { Text("2024-2025") },
                    isError = sessionError != null,
                    supportingText = sessionError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_prospectus_session"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Program Level
                ExposedDropdownMenuBox(
                    expanded = levelDropdownExpanded,
                    onExpandedChange = { levelDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = programLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Program Scope") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_prospectus_level"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = levelDropdownExpanded,
                        onDismissRequest = { levelDropdownExpanded = false }
                    ) {
                        PROSPECTUS_LEVELS.forEach { lvl ->
                            DropdownMenuItem(
                                text = { Text(lvl) },
                                onClick = {
                                    programLevel = lvl
                                    levelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Highlights") },
                    placeholder = { Text("Contains rules, merit criteria, department info...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_prospectus_desc"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // PDF File Upload Section
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
                                    text = "Prospectus PDF File *",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }

                            Button(
                                onClick = {
                                    val safeSession = academicSession.replace(" ", "_").replace("/", "-")
                                    val sampleName = "ggc_prospectus_${safeSession}_${System.currentTimeMillis() % 1000}.pdf"
                                    fileName = sampleName
                                    fileBytes = "GGC Official Prospectus PDF Document Stream".toByteArray()
                                    fileError = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_select_prospectus_pdf")
                            ) {
                                Text(if (fileName == null) "Select PDF" else "Change", fontSize = 11.sp)
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

                // Switch for Is Current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isCurrent) BrandGold else BrandTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Set as Current Active Prospectus", fontSize = 13.sp, color = BrandNavy, fontWeight = FontWeight.Medium)
                            Text("Displayed on Home & Admission pages", fontSize = 11.sp, color = BrandTextMuted)
                        }
                    }
                    Switch(
                        checked = isCurrent,
                        onCheckedChange = { isCurrent = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandGold, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_prospectus_current")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Switch for Published
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish Prospectus", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Publicly accessible" else "Draft mode", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_prospectus_published")
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
                            if (academicSession.isBlank()) {
                                sessionError = "Academic session is required"
                                valid = false
                            }
                            if (fileName.isNullOrBlank() && fileBytes == null) {
                                fileError = "Please select a prospectus PDF file"
                                valid = false
                            }
                            if (valid) {
                                onSave(
                                    prospectus?.id,
                                    title,
                                    academicSession,
                                    programLevel,
                                    description,
                                    isCurrent,
                                    isPublished,
                                    fileBytes,
                                    fileName
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_prospectus"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (prospectus == null) "Upload" else "Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
