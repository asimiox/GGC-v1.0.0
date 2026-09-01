package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserProfileManager
import com.example.ui.screens.auth.FacultyAuthContent
import com.example.ui.theme.GgcGoldTertiary
import kotlinx.coroutines.launch

private val BrandNavy = Color(0xFF061B52)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val userProfile by UserProfileManager.userProfile.collectAsState()
    var showFacultyAuthSheet by remember { mutableStateOf(false) }
    var showHodDashboard by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showChangeSemesterDialog by remember { mutableStateOf(false) }
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordErrorMsg by remember { mutableStateOf<String?>(null) }
    var passwordSuccessMsg by remember { mutableStateOf<String?>(null) }
    var isCurrentPassVisible by remember { mutableStateOf(false) }
    var isNewPassVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = true) {
        if (showHodDashboard) {
            showHodDashboard = false
        } else if (showChangeSemesterDialog) {
            showChangeSemesterDialog = false
        } else if (showChangePasswordDialog) {
            showChangePasswordDialog = false
        } else if (showFacultyAuthSheet) {
            showFacultyAuthSheet = false
        } else if (onBack != null) {
            onBack()
        }
    }

    if (showHodDashboard) {
        com.example.ui.screens.hod.HodDashboardScreen(
            onNavigateBack = { showHodDashboard = false }
        )
        return
    }

    if (showFacultyAuthSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFacultyAuthSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FacultyAuthContent(
                    onAuthSuccess = {
                        scope.launch {
                            sheetState.hide()
                            showFacultyAuthSheet = false
                        }
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .testTag("profile_screen_container")
    ) {
        if (onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "User Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }
            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
        }

        // Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, GgcGoldTertiary, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ggc_logo),
                        contentDescription = "User Avatar",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = userProfile.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (userProfile.isFaculty) {
                        "Faculty Member • ${userProfile.designation ?: userProfile.department ?: "Faculty Portal"}"
                    } else {
                        "${userProfile.programLevel} Student Portal • ${userProfile.programName}"
                    },
                    fontSize = 13.sp,
                    color = GgcGoldTertiary,
                    fontWeight = FontWeight.SemiBold
                )

                if (userProfile.isVerified) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Official Record",
                            tint = GgcGoldTertiary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (userProfile.isFaculty) "Verified Official Faculty Member" else "Verified Official Identity",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Faculty Identity Details (if logged in as Faculty)
        if (userProfile.isFaculty && userProfile.isVerified) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Official Faculty Credentials",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        userProfile.facultyId?.let { fid ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Official Faculty ID", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = fid, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.designation?.let { desig ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Designation", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = desig, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.department?.let { dept ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Department", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = dept, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.qualification?.let { qual ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Qualification", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = qual, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.institutionalEmail?.let { email ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Institutional Email", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = email, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.username?.let { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Portal Username", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = "@$user", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Faculty Options (Password change & HOD dashboard if applicable)
                val isHodOrAdmin = userProfile.isHod || userProfile.isAdmin || userProfile.designation?.contains("Principal", ignoreCase = true) == true
                if (isHodOrAdmin) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        ProfileMenuRow(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "HOD Command Center",
                            subtitle = "Department Management & Student Roster Ingestion",
                            tag = "menu_hod_dashboard",
                            onClick = { showHodDashboard = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ProfileMenuRow(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your faculty account password (default: 00000)",
                        tag = "menu_change_password",
                        onClick = {
                            currentPasswordInput = ""
                            newPasswordInput = ""
                            confirmPasswordInput = ""
                            passwordErrorMsg = null
                            passwordSuccessMsg = null
                            showChangePasswordDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Student Identity Details (if available)
        if (!userProfile.isFaculty && (userProfile.rollNumber != null || userProfile.registrationNumber != null)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Official Student Credentials",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        userProfile.rollNumber?.let { roll ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "College Roll Number", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = roll, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        userProfile.registrationNumber?.let { reg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (userProfile.programLevel == "BS") "University Reg Number" else "Registration Number",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(text = reg, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }

                        // Official Program / Department (Strictly locked to HOD / Admin Import)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Official Department", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(
                                    text = "Assigned via Official Import",
                                    fontSize = 10.sp,
                                    color = BrandNavy.copy(alpha = 0.6f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandNavy.copy(alpha = 0.08f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = userProfile.programName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandNavy
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )

                        userProfile.semester?.let { sem ->
                            if (userProfile.programLevel == "BS") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showChangeSemesterDialog = true },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Current Semester", fontSize = 13.sp, color = Color(0xFF6B7280))
                                        Text(
                                            text = "Tap to update active semester",
                                            fontSize = 10.sp,
                                            color = BrandNavy.copy(alpha = 0.6f)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(BrandNavy)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = sem,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "Change Semester",
                                            tint = BrandNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                )
                            }
                        }

                        userProfile.username?.let { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Portal Username", fontSize = 13.sp, color = Color(0xFF6B7280))
                                Text(text = "@$user", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Change Account Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = BrandNavy
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Update your password. New HOD accounts are created with default password '00000'.",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (passwordErrorMsg != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = passwordErrorMsg.orEmpty(),
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if (passwordSuccessMsg != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = passwordSuccessMsg.orEmpty(),
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = currentPasswordInput,
                        onValueChange = {
                            currentPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Current Password") },
                        placeholder = { Text("Default is 00000") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy) },
                        trailingIcon = {
                            IconButton(onClick = { isCurrentPassVisible = !isCurrentPassVisible }) {
                                Icon(
                                    imageVector = if (isCurrentPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isCurrentPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("current_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = {
                            newPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = BrandNavy) },
                        trailingIcon = {
                            IconButton(onClick = { isNewPassVisible = !isNewPassVisible }) {
                                Icon(
                                    imageVector = if (isNewPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isNewPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = {
                            confirmPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Confirm New Password") },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = BrandNavy) },
                        visualTransformation = if (isNewPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_password_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val savedPass = UserProfileManager.getPassword(context)
                        if (currentPasswordInput.trim() != savedPass && currentPasswordInput.trim() != "00000") {
                            passwordErrorMsg = "Incorrect current password. Default is '00000'."
                            return@Button
                        }
                        if (newPasswordInput.trim().length < 4) {
                            passwordErrorMsg = "New password must be at least 4 characters."
                            return@Button
                        }
                        if (newPasswordInput.trim() != confirmPasswordInput.trim()) {
                            passwordErrorMsg = "New passwords do not match."
                            return@Button
                        }

                        UserProfileManager.updatePassword(context, newPasswordInput.trim())
                        passwordSuccessMsg = "Password updated successfully!"
                        scope.launch {
                            kotlinx.coroutines.delay(1200)
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_password_btn")
                ) {
                    Text("Update Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChangeSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showChangeSemesterDialog = false },
            title = {
                Column {
                    Text(
                        text = "Update Active Semester",
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Department: ${userProfile.programName}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..8).map { "Semester $it" }.chunked(2).forEach { rowPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPair.forEach { sem ->
                                val isSelected = userProfile.semester == sem
                                Button(
                                    onClick = {
                                        UserProfileManager.updateSemester(context, sem)
                                        showChangeSemesterDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) BrandNavy else Color(0xFFE8EEFA),
                                        contentColor = if (isSelected) Color.White else BrandNavy
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("dialog_sem_${sem.removePrefix("Semester ")}")
                                ) {
                                    Text(
                                        text = sem,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showChangeSemesterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
