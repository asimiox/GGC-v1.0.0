package com.example.ui.screens.hod

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

/**
 * FEATURE 1: ADD TEACHER
 * Screen 1 — Add Teacher Form
 * Fields: 1. Name, 2. Designation, 3. Subject, 4. Teacher ID, 5. Password (default: 00000)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodAddTeacherFormScreen(
    state: HodUiState,
    onUpdateForm: (name: String?, desig: String?, subj: String?, id: String?, pass: String?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var desigExpanded by remember { mutableStateOf(false) }
    val designations = listOf("Lecturer", "Assistant Professor", "Associate Professor", "Professor", "Visiting Lecturer")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Add New Teacher",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.departmentName} Department",
                            fontSize = 12.sp,
                            color = BrandGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BrandNavy.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Faculty Onboarding Portal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "Add teacher credentials to your department roster.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (state.errorMessage != null) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // 1. Name
            Column {
                Text(
                    text = "1. Teacher Full Name *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.teacherFormName,
                    onValueChange = { onUpdateForm(it, null, null, null, null) },
                    placeholder = { Text("e.g. Prof. Shahid Imran") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BrandNavy)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    ),
                    singleLine = true
                )
            }

            // 2. Designation
            Column {
                Text(
                    text = "2. Designation *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = desigExpanded,
                    onExpandedChange = { desigExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.teacherFormDesignation,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desigExpanded) },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = BrandNavy)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("teacher_designation_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = desigExpanded,
                        onDismissRequest = { desigExpanded = false }
                    ) {
                        designations.forEach { desig ->
                            DropdownMenuItem(
                                text = { Text(desig) },
                                onClick = {
                                    onUpdateForm(null, desig, null, null, null)
                                    desigExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. Subject / Qualification
            Column {
                Text(
                    text = "3. Subject / Specialization *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.teacherFormSubject,
                    onValueChange = { onUpdateForm(null, null, it, null, null) },
                    placeholder = { Text("e.g. MSc Mathematics / Data Structures") },
                    leadingIcon = {
                        Icon(Icons.Default.School, contentDescription = null, tint = BrandNavy)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_subject_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    ),
                    singleLine = true
                )
            }

            // 4. Teacher ID
            Column {
                Text(
                    text = "4. Teacher ID (Unique Identifier) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.teacherFormId,
                    onValueChange = { onUpdateForm(null, null, null, it.uppercase(), null) },
                    placeholder = { Text("e.g. FAC01 or MATH-FAC-01") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = BrandNavy)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_id_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    ),
                    singleLine = true
                )
            }

            // 5. Password (default: 00000)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5. Default Password *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                    Text(
                        text = "Pre-filled default: 00000",
                        fontSize = 11.sp,
                        color = BrandGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.teacherFormPassword,
                    onValueChange = { onUpdateForm(null, null, null, null, it) },
                    placeholder = { Text("00000") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandNavy
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("create_teacher_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Provisioning Account...")
                } else {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Teacher Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Screen 2 — Teacher Dashboard / Created Summary (after creation)
 * Example data shown:
 * - ID = FAC01
 * - Name: Prof. Shahid Imran
 * - Qualification: MSc Mathematics
 * - Department: Math Dept
 * Buttons on this screen:
 * - Notice
 * - New Talk
 * - Profile
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodTeacherCreatedSummaryScreen(
    createdTeacher: CreatedTeacherSummary?,
    onNavigateToNotice: () -> Unit,
    onNavigateToNewTalk: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddAnotherTeacher: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val teacher = createdTeacher ?: CreatedTeacherSummary(
        teacherId = "FAC01",
        name = "Prof. Shahid Imran",
        designation = "Assistant Professor",
        subject = "MSc Mathematics",
        department = "Mathematics Department",
        defaultPassword = "00000"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Teacher Created",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Account Provisioned Successfully!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Teacher can now sign in using Teacher ID and default password.",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Teacher Profile Card (As Specified in Screen 2)
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = BrandGoldLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = teacher.name,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = teacher.designation,
                                fontSize = 13.sp,
                                color = BrandGoldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Key fields as requested
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "ID", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(text = teacher.teacherId, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text(text = "Qualification / Subject", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(text = teacher.subject, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Department", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(text = teacher.department, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        }
                        Column {
                            Text(text = "Default Password", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(text = teacher.defaultPassword, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandGoldLight)
                        }
                    }
                }
            }

            // Share Credentials Action
            OutlinedButton(
                onClick = {
                    val shareText = """
                        *Govt Graduate College Mandi Bahauddin*
                        Teacher Portal Credentials:
                        • Name: ${teacher.name}
                        • Teacher ID: ${teacher.teacherId}
                        • Department: ${teacher.department}
                        • Default Password: ${teacher.defaultPassword}
                        • App Download: Download the Official GGC App and login under Faculty Portal.
                    """.trimIndent()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Teacher Credentials")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandNavy)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Credentials with Teacher", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quick Actions (Teacher Dashboard Features):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            // The 3 Requested Buttons: Notice, New Talk, Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Notice
                Button(
                    onClick = onNavigateToNotice,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("teacher_action_notice"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 2: New Talk
                Button(
                    onClick = {
                        val shareText = "Hello ${teacher.name}, welcome to the GGC Mandi Bahauddin Department Portal!"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "New Talk"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("teacher_action_new_talk"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Talk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 3: Profile
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(teacher.teacherId))
                        Toast.makeText(context, "Teacher ID copied: ${teacher.teacherId}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("teacher_action_profile"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add Another Teacher Button
            OutlinedButton(
                onClick = onAddAnotherTeacher,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Add Another Teacher")
            }

            // Return to HOD Dashboard
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGold)
            ) {
                Text(
                    text = "Return to HOD Dashboard",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }
        }
    }
}
