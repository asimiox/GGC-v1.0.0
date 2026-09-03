package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.UserProfileManager
import com.example.data.datasource.PasswordRegistryStore
import com.example.data.model.AppRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)

/**
 * Universal Dialog for updating account passwords across BS Students,
 * Intermediate Students, Faculty members, HODs, and Super Administrators.
 */
@Composable
fun ChangePasswordDialog(
    onDismissRequest: () -> Unit,
    onPasswordChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val profile = UserProfileManager.userProfile.value

    // Determine primary identifier for validation
    val primaryIdentifier = when {
        profile.appRole == AppRole.ADMIN -> profile.username ?: "admin"
        profile.appRole.isTeacherLevel || profile.appRole == AppRole.HOD -> profile.facultyId ?: profile.username ?: ""
        else -> profile.rollNumber ?: profile.username ?: ""
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isCurrentVisible by remember { mutableStateOf(false) }
    var isNewVisible by remember { mutableStateOf(false) }
    var isConfirmVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val hasCustom = remember(primaryIdentifier) {
        PasswordRegistryStore.hasCustomPassword(primaryIdentifier)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) onDismissRequest()
        },
        properties = DialogProperties(dismissOnBackPress = !isSubmitting, dismissOnClickOutside = !isSubmitting),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BrandNavy.copy(alpha = 0.08f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Key Icon",
                        tint = BrandNavy,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Change Account Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                val roleLabel = when (profile.appRole) {
                    AppRole.ADMIN -> "Super Administrator"
                    AppRole.HOD -> "Head of Department (HOD)"
                    AppRole.TEACHER -> "Faculty Member"
                    AppRole.STUDENT_INTERMEDIATE -> "Intermediate Student"
                    AppRole.STUDENT_BS -> "BS Student"
                }
                Text(
                    text = "$roleLabel • ${profile.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF718096)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info banner explaining the default password if applicable
                if (!hasCustom) {
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.appRole == AppRole.ADMIN)
                                    "You are using the default admin password. Set your personal password below."
                                else
                                    "Your current default password is '00000'. Please set a secure personal password.",
                                fontSize = 11.sp,
                                color = Color(0xFF795548),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Error message banner
                AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                    errorMessage?.let { error ->
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error,
                                    color = Color(0xFFC62828),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Success message banner
                AnimatedVisibility(visible = successMessage != null, enter = fadeIn(), exit = fadeOut()) {
                    successMessage?.let { success ->
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = success,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 1. Current Password Field
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        errorMessage = null
                    },
                    label = { Text("Current Password") },
                    placeholder = {
                        Text(if (hasCustom) "Enter current password" else "Default is 00000")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy)
                    },
                    trailingIcon = {
                        IconButton(onClick = { isCurrentVisible = !isCurrentVisible }) {
                            Icon(
                                imageVector = if (isCurrentVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isCurrentVisible) "Hide" else "Show"
                            )
                        }
                    },
                    visualTransformation = if (isCurrentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isSubmitting && successMessage == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_current_password_input")
                )

                // 2. New Password Field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("New Password") },
                    placeholder = { Text("At least 4 characters") },
                    leadingIcon = {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrandNavy)
                    },
                    trailingIcon = {
                        IconButton(onClick = { isNewVisible = !isNewVisible }) {
                            Icon(
                                imageVector = if (isNewVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isNewVisible) "Hide" else "Show"
                            )
                        }
                    },
                    visualTransformation = if (isNewVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isSubmitting && successMessage == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_new_password_input")
                )

                // 3. Confirm New Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirm New Password") },
                    placeholder = { Text("Re-enter new password") },
                    leadingIcon = {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrandNavy)
                    },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmVisible = !isConfirmVisible }) {
                            Icon(
                                imageVector = if (isConfirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isConfirmVisible) "Hide" else "Show"
                            )
                        }
                    },
                    visualTransformation = if (isConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isSubmitting && successMessage == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        performPasswordUpdate(
                            currentPass = currentPassword,
                            newPass = newPassword,
                            confirmPass = confirmPassword,
                            primaryIdentifier = primaryIdentifier,
                            profile = profile,
                            context = context,
                            onError = { errorMessage = it },
                            onSuccess = { msg ->
                                successMessage = msg
                                isSubmitting = false
                                coroutineScope.launch {
                                    delay(1200)
                                    onPasswordChanged(newPassword)
                                    onDismissRequest()
                                }
                            },
                            setSubmitting = { isSubmitting = it }
                        )
                    }),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_confirm_password_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    performPasswordUpdate(
                        currentPass = currentPassword,
                        newPass = newPassword,
                        confirmPass = confirmPassword,
                        primaryIdentifier = primaryIdentifier,
                        profile = profile,
                        context = context,
                        onError = { errorMessage = it },
                        onSuccess = { msg ->
                            successMessage = msg
                            isSubmitting = false
                            coroutineScope.launch {
                                delay(1200)
                                onPasswordChanged(newPassword)
                                onDismissRequest()
                            }
                        },
                        setSubmitting = { isSubmitting = it }
                    )
                },
                enabled = !isSubmitting && successMessage == null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                modifier = Modifier.testTag("dialog_submit_password_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Saving...")
                } else {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !isSubmitting
            ) {
                Text(
                    text = if (successMessage != null) "Close" else "Cancel",
                    color = Color(0xFF718096)
                )
            }
        }
    )
}

private fun performPasswordUpdate(
    currentPass: String,
    newPass: String,
    confirmPass: String,
    primaryIdentifier: String,
    profile: com.example.data.model.UserProfile,
    context: android.content.Context,
    onError: (String) -> Unit,
    onSuccess: (String) -> Unit,
    setSubmitting: (Boolean) -> Unit
) {
    val cleanCurrent = currentPass.trim()
    val cleanNew = newPass.trim()
    val cleanConfirm = confirmPass.trim()

    if (cleanCurrent.isBlank()) {
        onError("Please enter your current password.")
        return
    }

    // Verify current password
    val isCurrentValid = PasswordRegistryStore.verifyPassword(primaryIdentifier, cleanCurrent) ||
            (cleanCurrent == UserProfileManager.getPassword(context))
    if (!isCurrentValid) {
        onError("Current password is incorrect. Please verify your current credentials.")
        return
    }

    if (cleanNew.length < 4) {
        onError("New password must be at least 4 characters long.")
        return
    }

    if (cleanNew == "00000") {
        onError("Please choose a personalized password rather than the default '00000'.")
        return
    }

    if (cleanNew != cleanConfirm) {
        onError("New password and confirmation do not match.")
        return
    }

    setSubmitting(true)

    // Gather all linked aliases
    val linkedIdentifiers = mutableListOf<String>()
    profile.rollNumber?.let { linkedIdentifiers.add(it) }
    profile.registrationNumber?.let { linkedIdentifiers.add(it) }
    profile.username?.let { linkedIdentifiers.add(it) }
    profile.facultyId?.let { linkedIdentifiers.add(it) }
    profile.institutionalEmail?.let { linkedIdentifiers.add(it) }
    profile.userId?.let { linkedIdentifiers.add(it) }
    if (primaryIdentifier.isNotBlank()) linkedIdentifiers.add(primaryIdentifier)

    PasswordRegistryStore.updatePasswordForUser(
        context = context,
        identifiers = linkedIdentifiers.distinct(),
        newPassword = cleanNew,
        appRole = profile.appRole,
        fullName = profile.name
    )

    onSuccess("Password updated successfully! Next time you log in, please use this new password.")
}

/**
 * Compact security recommendation banner displayed at the top of screens when user is
 * using the initial default password.
 */
@Composable
fun DefaultPasswordSecurityNotice(
    onOpenChangePassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = UserProfileManager.userProfile.value
    val primaryId = profile.rollNumber ?: profile.facultyId ?: profile.username ?: ""
    val hasChanged = PasswordRegistryStore.hasCustomPassword(primaryId)

    if (!hasChanged) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("default_password_security_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFBC02D).copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Security Notice",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = "You are currently using the default password. Update now to secure your account.",
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037).copy(alpha = 0.85f),
                        lineHeight = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOpenChangePassword,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061B52)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("banner_change_password_button")
                ) {
                    Text("Change", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Modal prompt that appears upon logging in if the account is still using the default password,
 * prompting students, teachers, and admins to change their password right away.
 */
@Composable
fun FirstLoginPasswordPromptDialog(
    onDismissRequest: () -> Unit,
    onOpenChangePassword: () -> Unit
) {
    val profile = UserProfileManager.userProfile.value
    val isStudent = profile.appRole == AppRole.STUDENT_BS || profile.appRole == AppRole.STUDENT_INTERMEDIATE

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BrandNavy.copy(alpha = 0.08f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BrandGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Welcome to GGC Portal! 🔐",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Important Security Recommendation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF57F17)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isStudent)
                        "Hello ${profile.name}! Your account is currently using the default college password '00000'. To protect your academic records and portal access, please create your own secure password now."
                    else
                        "Welcome ${profile.name}! To maintain security compliance across college records, please update your account password from the initial default.",
                    fontSize = 13.sp,
                    color = Color(0xFF4A5568),
                    lineHeight = 18.sp
                )

                Surface(
                    color = Color(0xFFF7FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Next Time You Login:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = BrandNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You will be able to log in securely with your newly updated password.",
                            fontSize = 11.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    onOpenChangePassword()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                modifier = Modifier.testTag("prompt_change_password_now_button")
            ) {
                Text("Change Password Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("prompt_change_password_later_button")
            ) {
                Text("Later", color = Color(0xFF718096))
            }
        }
    )
}
