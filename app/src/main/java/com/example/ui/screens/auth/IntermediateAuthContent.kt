package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.GgcGoldTertiary

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandNavyLight = Color(0xFF132B68)
private val BrandFieldBorder = Color(0xFFDCE2EE)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IntermediateAuthContent(
    modifier: Modifier = Modifier,
    viewModel: IntermediateAuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("intermediate_auth_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandNavy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Intermediate Student",
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Intermediate Student Portal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector: Signup vs Login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F4F9))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tab: Register Account
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (state.selectedTab == IntermediateAuthTab.SIGNUP) BrandNavy else Color.Transparent)
                        .clickable { viewModel.switchTab(IntermediateAuthTab.SIGNUP) }
                        .padding(vertical = 10.dp)
                        .testTag("auth_tab_signup"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 13.sp,
                        fontWeight = if (state.selectedTab == IntermediateAuthTab.SIGNUP) FontWeight.Bold else FontWeight.Medium,
                        color = if (state.selectedTab == IntermediateAuthTab.SIGNUP) Color.White else Color(0xFF5A6B87)
                    )
                }

                // Tab: Student Login
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (state.selectedTab == IntermediateAuthTab.LOGIN) BrandNavy else Color.Transparent)
                        .clickable { viewModel.switchTab(IntermediateAuthTab.LOGIN) }
                        .padding(vertical = 10.dp)
                        .testTag("auth_tab_login"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Student Login",
                        fontSize = 13.sp,
                        fontWeight = if (state.selectedTab == IntermediateAuthTab.LOGIN) FontWeight.Bold else FontWeight.Medium,
                        color = if (state.selectedTab == IntermediateAuthTab.LOGIN) Color.White else Color(0xFF5A6B87)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.errorMessage?.let { errorText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorText,
                            fontSize = 12.sp,
                            color = Color(0xFFB71C1C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Success Banner
            AnimatedVisibility(
                visible = state.successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.successMessage?.let { successText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successText,
                            fontSize = 12.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            if (state.selectedTab == IntermediateAuthTab.SIGNUP) {
                // ==================== SIGNUP FORM ====================

                // First Name & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.regForm.firstName,
                        onValueChange = { viewModel.updateRegFirstName(it) },
                        label = { Text("First Name", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_first_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = state.regForm.lastName,
                        onValueChange = { viewModel.updateRegLastName(it) },
                        label = { Text("Last Name", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_last_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // College Roll Number
                OutlinedTextField(
                    value = state.regForm.rollNumber,
                    onValueChange = { viewModel.updateRegRollNumber(it) },
                    label = { Text("College Roll Number (e.g. 24-ICS-01)", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_roll_number"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Registration Number
                OutlinedTextField(
                    value = state.regForm.registrationNumber,
                    onValueChange = { viewModel.updateRegRegistrationNumber(it) },
                    label = { Text("Registration Number (e.g. 2024-GGC-1029)", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_registration_number"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Program / Class Selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select Program / Class",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.intermediatePrograms.forEach { prog ->
                            val isSelected = state.regForm.program == prog
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandNavy else Color(0xFFF3F5FA))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BrandNavy else Color(0xFFD6DFEC),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateRegProgram(prog) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("prog_chip_$prog"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prog,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF2C3E50)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Username
                OutlinedTextField(
                    value = state.regForm.username,
                    onValueChange = { viewModel.updateRegUsername(it) },
                    label = { Text("Choose Username", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_username"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password
                OutlinedTextField(
                    value = state.regForm.password,
                    onValueChange = { viewModel.updateRegPassword(it) },
                    label = { Text("Password (min 6 characters)", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password",
                                tint = Color(0xFF7A879D),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confirm Password
                OutlinedTextField(
                    value = state.regForm.confirmPassword,
                    onValueChange = { viewModel.updateRegConfirmPassword(it) },
                    label = { Text("Confirm Password", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password",
                                tint = Color(0xFF7A879D),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_confirm_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.registerStudent(context, onAuthSuccess)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Identity info card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandNavy.copy(alpha = 0.05f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Student Account Registration: Ensure your Roll Number, Registration Number, and Username are unique. You can log in immediately after creating your account.",
                        fontSize = 11.sp,
                        color = Color(0xFF4A5568),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register Button
                Button(
                    onClick = { viewModel.registerStudent(context, onAuthSuccess) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_verify_and_register"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create Student Account",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // ==================== LOGIN FORM ====================

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Log in with your chosen Username or College Roll Number.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username or Roll
                OutlinedTextField(
                    value = state.loginForm.usernameOrRoll,
                    onValueChange = { viewModel.updateLoginUsernameOrRoll(it) },
                    label = { Text("Username or College Roll Number", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_login_username_roll"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = state.loginForm.password,
                    onValueChange = { viewModel.updateLoginPassword(it) },
                    label = { Text("Password", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password",
                                tint = Color(0xFF7A879D),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_login_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BrandFieldBorder
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.loginStudent(context, onAuthSuccess)
                        }
                    )
                )

                // Quick Hint: Default Password for Imported Students
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandGold.copy(alpha = 0.12f))
                        .clickable { viewModel.updateLoginPassword("00000") }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = BrandGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Default Password: 00000",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                    Text(
                        text = "Auto-fill",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Login Button
                Button(
                    onClick = { viewModel.loginStudent(context, onAuthSuccess) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_login_intermediate"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Login to Portal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = { viewModel.switchTab(IntermediateAuthTab.SIGNUP) },
                    modifier = Modifier.testTag("btn_switch_to_signup")
                ) {
                    Text(
                        text = "Don't have an account? Create one",
                        fontSize = 12.sp,
                        color = BrandNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
