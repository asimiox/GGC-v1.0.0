package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.example.R

private val BrandBackground = Color(0xFFF6F6F6)
private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandFieldBorder = Color(0xFFE2E8F0)
private val BrandTextMuted = Color(0xFF7A879D)

@Composable
fun BsAuthContent(
    modifier: Modifier = Modifier,
    initialProgram: String? = null,
    initialSemester: String? = null,
    viewModel: BsAuthViewModel = viewModel(),
    onBack: (() -> Unit)? = null,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(initialProgram, initialSemester) {
        viewModel.initialize(initialProgram, initialSemester)
    }

    state.transferPromptData?.let { promptData ->
        com.example.ui.components.SessionTransferPromptDialog(
            data = promptData,
            onDismiss = { viewModel.dismissTransferPrompt() },
            onApproved = {
                viewModel.completeTransferLogin(context, onAuthSuccess)
            }
        )
    }

    Column(
        modifier = modifier
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 12.dp)
            .testTag("bs_auth_card"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // TOP HEADER: Navy back arrow + "BS Student Login"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bs_auth_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandNavy
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = "BS Student Login",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }

            // OFFICIAL GGC BRANDING: Logo + GGC M.B.Din + Official App
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                    contentDescription = "GGC Logo",
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "GGC M.B.Din",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        lineHeight = 19.sp
                    )
                    Text(
                        text = "Official App",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            // PORTAL INTRO: "BS Student Portal" with vertical gold accent + Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.5.dp)
                        .height(24.dp)
                        .background(BrandGold, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BS Student Portal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }

            Text(
                text = "Sign in using your College Roll Number / Registration Number and Password",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                lineHeight = 19.sp,
                modifier = Modifier.padding(bottom = 22.dp)
            )

            // Alerts (Error Banner & Success Banner)
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.errorMessage?.let { errorText ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("bs_error_card"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEECEB),
                        border = BorderStroke(1.dp, Color(0xFFF5C6CB))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
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
                                color = Color(0xFF9A0007),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.successMessage?.let { successText ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("bs_success_card"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFFC3E6CB))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
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
                                color = Color(0xFF1B5E20),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // LOGIN FIELDS
            // FIELD 1: Roll Number or Registration Number
            OutlinedTextField(
                value = state.loginForm.usernameOrRoll,
                onValueChange = { viewModel.updateLoginUsernameOrRoll(it) },
                label = { Text("Roll Number or Registration Number") },
                placeholder = { Text("Enter your roll number or registration number", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrandNavy
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = BrandFieldBorder,
                    focusedLabelColor = BrandNavy,
                    unfocusedLabelColor = BrandTextMuted,
                    cursorColor = BrandNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bs_input_login_user")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // FIELD 2: Password
            OutlinedTextField(
                value = state.loginForm.password,
                onValueChange = { viewModel.updateLoginPassword(it) },
                label = { Text("Password") },
                placeholder = { Text("Enter your password", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandNavy
                    )
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
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.loginStudent(context, onAuthSuccess) }
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = BrandFieldBorder,
                    focusedLabelColor = BrandNavy,
                    unfocusedLabelColor = BrandTextMuted,
                    cursorColor = BrandNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bs_input_login_password")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECURITY MESSAGE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Use your correct credentials to access your account.",
                    fontSize = 12.sp,
                    color = BrandTextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PRIMARY BUTTON: "Sign In as BS Student" with right arrow
            Button(
                onClick = { viewModel.loginStudent(context, onAuthSuccess) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("bs_btn_login_submit"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandNavy,
                    disabledContainerColor = BrandNavy.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sign In as BS Student",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // SUBTLE DIVIDER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // HELP SECTION: Information row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = BrandTextMuted,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Official student accounts are pre-registered by College Admin / HOD. If your roll number is not found, please contact your department.",
                    fontSize = 12.sp,
                    color = BrandTextMuted,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 40.dp))

        // FOOTER: "GGC M.B.Din · Official App"
        Text(
            text = "GGC M.B.Din · Official App",
            fontSize = 12.sp,
            color = BrandTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
    }
}

