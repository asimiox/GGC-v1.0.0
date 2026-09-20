package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF64748B)
private val BrandFieldBorder = Color(0xFFDCE2EE)
private val BrandInfoBg = Color(0xFFF0F4FA)
private val BrandInfoBorder = Color(0xFFDCE5F2)

@Composable
fun FacultyAuthContent(
    modifier: Modifier = Modifier,
    viewModel: FacultyAuthViewModel = viewModel(),
    onBack: (() -> Unit)? = null,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("faculty_auth_card")
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("teacher_auth_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.ic_ggc_logo),
                contentDescription = "GGC Logo",
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "GGC M.B.Din",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Official App",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PAGE TITLE
        Text(
            text = "Faculty Portal",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Official Teacher Login & Claim",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal gold accent line below heading with subtle grey track
        Box(
            modifier = Modifier
                .width(190.dp)
                .height(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .align(Alignment.CenterStart)
                    .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(1.dp))
            )
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(3.dp)
                    .align(Alignment.CenterStart)
                    .background(BrandGold, shape = RoundedCornerShape(2.dp))
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // FACULTY & STAFF SECTION
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FacultyStaffIcon()

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Faculty & Staff Portal",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Teachers · Department HODs · College Admin",
                    fontSize = 13.sp,
                    color = BrandTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // INFORMATION BOX
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("faculty_provisioning_notice"),
            shape = RoundedCornerShape(14.dp),
            color = BrandInfoBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandInfoBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Teacher accounts are provisioned exclusively by the College Administration. Sign in using your assigned Faculty ID / Username and Password.",
                    fontSize = 13.sp,
                    color = BrandNavy,
                    lineHeight = 18.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Alerts: Error Message
        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            state.errorMessage?.let { error ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("faculty_error_card"),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEECEB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C6CB))
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = error,
                            fontSize = 12.5.sp,
                            color = Color(0xFF9A0007),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Alerts: Success Message
        AnimatedVisibility(
            visible = state.successMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            state.successMessage?.let { success ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("faculty_success_card"),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC3E6CB))
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = success,
                            fontSize = 12.5.sp,
                            color = Color(0xFF1B5E20),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // LOGIN SECTION
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Faculty Sign In",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter your Faculty ID, Username, or Institutional Email",
                fontSize = 13.5.sp,
                color = BrandTextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Faculty ID / Username / Email
            OutlinedTextField(
                value = state.loginForm.usernameOrFacultyId,
                onValueChange = { viewModel.updateLoginUsernameOrFacultyId(it) },
                label = { Text("Faculty ID / Username / Email") },
                placeholder = { Text("Enter your Faculty ID / Username / Email", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrandNavy
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
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
                    .testTag("faculty_login_identity_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: Password
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
                            contentDescription = "Toggle Password",
                            tint = BrandTextMuted
                        )
                    }
                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.loginFaculty(context, onAuthSuccess)
                }),
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
                    .testTag("faculty_login_password_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PRIMARY ACTION BUTTON
            Button(
                onClick = { viewModel.loginFaculty(context, onAuthSuccess) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("faculty_submit_login_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandNavy,
                    disabledContainerColor = BrandNavy.copy(alpha = 0.6f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sign In to Faculty Portal",
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
        }

        Spacer(modifier = Modifier.height(36.dp))

        // BOTTOM HELP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF7A879D),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "For account access, contact College Administration.",
                fontSize = 11.5.sp,
                color = Color(0xFF7A879D)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }
    }
}

/**
 * Faculty and staff emblem with two navy silhouettes and a subtle gold accent silhouette
 * matching the official reference layout.
 */
@Composable
private fun FacultyStaffIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(34.dp)) {
            val navy = Color(0xFF061B52)
            val gold = Color(0xFFC59B27)
            val w = size.width
            val h = size.height

            // Left person (Navy)
            drawCircle(
                color = navy,
                radius = w * 0.12f,
                center = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.32f)
            )
            drawArc(
                color = navy,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.04f, h * 0.50f),
                size = androidx.compose.ui.geometry.Size(w * 0.36f, h * 0.42f)
            )

            // Right person (Subtle Gold Accent)
            drawCircle(
                color = gold,
                radius = w * 0.12f,
                center = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.32f)
            )
            drawArc(
                color = gold,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.60f, h * 0.50f),
                size = androidx.compose.ui.geometry.Size(w * 0.36f, h * 0.42f)
            )

            // Center person (Navy foreground)
            drawCircle(
                color = navy,
                radius = w * 0.15f,
                center = androidx.compose.ui.geometry.Offset(w * 0.49f, h * 0.26f)
            )
            drawArc(
                color = navy,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.46f),
                size = androidx.compose.ui.geometry.Size(w * 0.48f, h * 0.48f)
            )
        }
    }
}
