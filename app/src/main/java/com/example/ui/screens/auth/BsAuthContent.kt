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
import androidx.compose.runtime.LaunchedEffect
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
fun BsAuthContent(
    modifier: Modifier = Modifier,
    initialProgram: String? = null,
    initialSemester: String? = null,
    viewModel: BsAuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(initialProgram, initialSemester) {
        viewModel.initialize(initialProgram, initialSemester)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bs_auth_card"),
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
                        contentDescription = "BS Degree Student",
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BS Student Portal",
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
                        .background(if (state.selectedTab == BsAuthTab.SIGNUP) BrandNavy else Color.Transparent)
                        .clickable { viewModel.switchTab(BsAuthTab.SIGNUP) }
                        .padding(vertical = 10.dp)
                        .testTag("bs_auth_tab_signup"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 13.sp,
                        fontWeight = if (state.selectedTab == BsAuthTab.SIGNUP) FontWeight.Bold else FontWeight.Medium,
                        color = if (state.selectedTab == BsAuthTab.SIGNUP) Color.White else Color(0xFF5A6B87)
                    )
                }

                // Tab: Student Login
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (state.selectedTab == BsAuthTab.LOGIN) BrandNavy else Color.Transparent)
                        .clickable { viewModel.switchTab(BsAuthTab.LOGIN) }
                        .padding(vertical = 10.dp)
                        .testTag("bs_auth_tab_login"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Student Login",
                        fontSize = 13.sp,
                        fontWeight = if (state.selectedTab == BsAuthTab.LOGIN) FontWeight.Bold else FontWeight.Medium,
                        color = if (state.selectedTab == BsAuthTab.LOGIN) Color.White else Color(0xFF5A6B87)
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
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
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
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // CONTENT: SIGNUP FORM
            if (state.selectedTab == BsAuthTab.SIGNUP) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Name row (First Name + Last Name)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.regForm.firstName,
                            onValueChange = { viewModel.updateRegFirstName(it) },
                            label = { Text("First Name *", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                unfocusedBorderColor = BrandFieldBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bs_input_first_name")
                        )

                        OutlinedTextField(
                            value = state.regForm.lastName,
                            onValueChange = { viewModel.updateRegLastName(it) },
                            label = { Text("Last Name *", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandNavy,
                                unfocusedBorderColor = BrandFieldBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bs_input_last_name")
                        )
                    }

                    // College Roll Number Field
                    OutlinedTextField(
                        value = state.regForm.rollNumber,
                        onValueChange = { viewModel.updateRegRollNumber(it) },
                        label = { Text("College/BS Roll Number *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. BSIT-2022-01", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = BrandNavy)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_roll_number")
                    )

                    // University Registration Number Field
                    OutlinedTextField(
                        value = state.regForm.registrationNumber,
                        onValueChange = { viewModel.updateRegRegistrationNumber(it) },
                        label = { Text("University Registration Number *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. UOG-2022-IT-001", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = BrandNavy)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_reg_number")
                    )

                    // Program Selection Chips
                    Text(
                        text = "Select BS Program *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.bsPrograms.forEach { prog ->
                            val isSelected = state.regForm.program == prog
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandNavy else Color(0xFFF1F4F9))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BrandNavy else Color(0xFFE2E6EE),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateRegProgram(prog) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("bs_chip_${prog.replace(" ", "_").lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prog,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else BrandNavy
                                )
                            }
                        }
                    }

                    // Semester Selection Chips
                    Text(
                        text = "Current Semester",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.semesters.forEach { sem ->
                            val isSelected = state.regForm.semester == sem
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandNavy else Color(0xFFF1F4F9))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BrandNavy else Color(0xFFE2E6EE),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateRegSemester(sem) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("bs_sem_chip_${sem.replace(" ", "_").lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sem,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else BrandNavy
                                )
                            }
                        }
                    }

                    // Username Field
                    OutlinedTextField(
                        value = state.regForm.username,
                        onValueChange = { viewModel.updateRegUsername(it) },
                        label = { Text("Choose Username *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. asim.nawaz", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandNavy)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_username")
                    )

                    // Password Field
                    OutlinedTextField(
                        value = state.regForm.password,
                        onValueChange = { viewModel.updateRegPassword(it) },
                        label = { Text("Password (min 6 chars) *", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy)
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = BrandNavy
                                )
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_password")
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = state.regForm.confirmPassword,
                        onValueChange = { viewModel.updateRegConfirmPassword(it) },
                        label = { Text("Confirm Password *", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy)
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = BrandNavy
                                )
                            }
                        },
                        visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_confirm_password")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Notice info
                    Text(
                        text = "Note: Your Roll Number and University Registration Number will be verified against official college & university admission records.",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit Registration Button
                    Button(
                        onClick = { viewModel.registerStudent(context, onAuthSuccess) },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("bs_btn_register_submit"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandNavy,
                            disabledContainerColor = BrandNavy.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Verify & Register BS Account",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // CONTENT: LOGIN FORM
            if (state.selectedTab == BsAuthTab.LOGIN) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Login to your verified BS student profile",
                        fontSize = 13.sp,
                        color = Color(0xFF5A6B87),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Username or Roll Number Field
                    OutlinedTextField(
                        value = state.loginForm.usernameOrRoll,
                        onValueChange = { viewModel.updateLoginUsernameOrRoll(it) },
                        label = { Text("Roll No, Reg No, or Username", fontSize = 12.sp) },
                        placeholder = { Text("e.g. BSIT-2022-01 or asim.nawaz", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandNavy)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_login_user")
                    )

                    // Password Field
                    OutlinedTextField(
                        value = state.loginForm.password,
                        onValueChange = { viewModel.updateLoginPassword(it) },
                        label = { Text("Password", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BrandNavy)
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = BrandNavy
                                )
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BrandFieldBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bs_input_login_password")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Login Button
                    Button(
                        onClick = { viewModel.loginStudent(context, onAuthSuccess) },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("bs_btn_login_submit"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandNavy,
                            disabledContainerColor = BrandNavy.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign In as BS Student",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
