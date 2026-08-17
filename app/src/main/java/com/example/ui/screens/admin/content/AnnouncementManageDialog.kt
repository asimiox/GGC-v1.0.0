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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
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
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppRole
import com.example.data.model.DepartmentDto

private val BrandNavy = Color(0xFF061B52)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandGold = Color(0xFFC59B27)

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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("announcement_manage_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Dialog Header
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
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (announcement == null) "New Announcement" else "Edit Announcement",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Text(
                                text = "Official College Notice",
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

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = if (it.isBlank()) "Title is required" else null
                    },
                    label = { Text("Title *") },
                    placeholder = { Text("e.g., Fall 2025 Admissions Open") },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_announcement_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Content Input (multiline)
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        contentError = if (it.isBlank()) "Content is required" else null
                    },
                    label = { Text("Notice Content *") },
                    placeholder = { Text("Enter the full details of this announcement...") },
                    isError = contentError != null,
                    supportingText = contentError?.let { { Text(it, color = Color(0xFFBA1A1A)) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("input_announcement_content"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        focusedLabelColor = BrandNavy
                    ),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
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
                            .testTag("dropdown_announcement_category"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            focusedLabelColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ANNOUNCEMENT_CATEGORIES.forEach { cat ->
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

                // Department Selector (Only enabled for Admin; locked for HOD)
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
                        label = { Text(if (isHod) "Department (Locked to your Dept)" else "Department Scope") },
                        trailingIcon = {
                            if (!isHod) ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_announcement_dept"),
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
                                text = { Text("All College (General Notice)") },
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

                // Attachment Section
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
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = null,
                                    tint = BrandNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PDF Attachment",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                            }

                            Button(
                                onClick = {
                                    val sampleName = "notice_${System.currentTimeMillis() % 1000}.pdf"
                                    attachmentFileName = sampleName
                                    attachmentBytes = "GGC M.B.Din Official Announcement Attachment".toByteArray()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_attach_announcement_file")
                            ) {
                                Text(if (attachmentFileName == null) "Attach File" else "Change", fontSize = 11.sp)
                            }
                        }

                        if (!attachmentFileName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Attached: $attachmentFileName",
                                fontSize = 12.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switches for Pinned & Published
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = if (isPinned) BrandGold else BrandTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pin to Top of Notice Board", fontSize = 13.sp, color = BrandNavy)
                    }
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandGold, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_announcement_pinned")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Publish to Students & Faculty", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BrandNavy)
                        Text(if (isPublished) "Visible immediately" else "Draft mode (hidden)", fontSize = 11.sp, color = BrandTextMuted)
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandNavy),
                        modifier = Modifier.testTag("switch_announcement_published")
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
                            if (content.isBlank()) {
                                contentError = "Content is required"
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_announcement"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (announcement == null) "Create" else "Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
