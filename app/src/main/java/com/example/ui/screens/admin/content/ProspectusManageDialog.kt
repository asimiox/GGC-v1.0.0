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
import com.example.data.model.ProspectusDto
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
    var title by remember { mutableStateOf(prospectus?.title ?: "") }
    var academicSession by remember { mutableStateOf(prospectus?.academicSession ?: "2024-2025") }
    var programLevel by remember { mutableStateOf(prospectus?.programLevel ?: PROSPECTUS_LEVELS[0]) }
    var description by remember { mutableStateOf(prospectus?.description ?: "") }
    var isCurrent by remember { mutableStateOf(prospectus?.isCurrent ?: true) }
    var isPublished by remember { mutableStateOf(prospectus?.isPublished ?: true) }

    var fileName by remember { mutableStateOf(prospectus?.fileName) }
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }

    var levelDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var sessionError by remember { mutableStateOf<String?>(null) }
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
                .testTag("prospectus_manage_dialog"),
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
                            modifier = Modifier.testTag("btn_close_prospectus")
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
                        text = if (prospectus == null) "Upload Prospectus" else "Edit Prospectus",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Share official college prospectus, admissions criteria, and program guides.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. PROSPECTUS TITLE
                    FieldLabel(label = "Prospectus Title", isRequired = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        placeholder = { Text("e.g., GGC Official Prospectus & Admission Guide", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_prospectus_title"),
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

                    // 2. ACADEMIC SESSION & PROGRAM LEVEL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel(label = "Session", isRequired = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = academicSession,
                                onValueChange = {
                                    academicSession = it
                                    if (sessionError != null) sessionError = null
                                },
                                placeholder = { Text("2024-2025", color = TextSecondary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_prospectus_session"),
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
                                isError = sessionError != null
                            )
                            if (sessionError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = sessionError!!, color = ErrorRed, fontSize = 12.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1.3f)) {
                            FieldLabel(label = "Program Level", isRequired = false)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { levelDropdownExpanded = true }
                                        .testTag("select_prospectus_level"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardBg,
                                    border = BorderStroke(1.dp, BorderColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = programLevel,
                                            fontSize = 13.sp,
                                            color = BrandNavy,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Level",
                                            tint = TextSecondary
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = levelDropdownExpanded,
                                    onDismissRequest = { levelDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(Color.White)
                                ) {
                                    PROSPECTUS_LEVELS.forEach { level ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = level,
                                                    fontWeight = if (programLevel == level) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (programLevel == level) BrandNavy else Color(0xFF1E293B)
                                                )
                                            },
                                            onClick = {
                                                programLevel = level
                                                levelDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. DESCRIPTION
                    FieldLabel(label = "Description & Summary", isRequired = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Summary of eligibility criteria, admission timeline, or department highlights...", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_prospectus_description"),
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

                    // 4. PROSPECTUS FILE (PDF)
                    FieldLabel(label = "Prospectus Document (PDF)", isRequired = true)
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
                                            text = fileName ?: "Selected Prospectus",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (fileBytes != null) "${fileBytes!!.size / 1024} KB" else "Attached PDF",
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
                                .clickable { filePickerLauncher.launch("application/pdf") }
                                .testTag("btn_pick_prospectus_file"),
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
                                    text = "Select prospectus PDF document",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "PDF up to 50MB",
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

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. EDITION STATUS
                    FieldLabel(label = "Edition Status", isRequired = false)
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
                                    .clickable { isCurrent = true },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isCurrent) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Current Active Edition",
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isCurrent) Color.White else TextSecondary
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isCurrent = false },
                                shape = RoundedCornerShape(20.dp),
                                color = if (!isCurrent) BrandNavy else Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Archive / Previous",
                                        fontSize = 13.sp,
                                        fontWeight = if (!isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (!isCurrent) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // PRIMARY ACTION: Publish Prospectus
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Please enter a prospectus title"
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
                                    title.trim(),
                                    academicSession.trim(),
                                    programLevel,
                                    description.trim().ifBlank { null },
                                    isCurrent,
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
                            .testTag("btn_save_prospectus"),
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
                                text = "Saving Prospectus...",
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
                                text = if (prospectus == null) "Publish Prospectus" else "Update Prospectus",
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
