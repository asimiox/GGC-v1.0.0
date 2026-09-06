package com.example.ui.components

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.UserProfileManager
import com.example.data.datasource.remote.ActiveSessionRemoteManager
import com.example.util.DeviceIdentifierHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Runs inside the authenticated app experience (Device A).
 * Continuously listens for login transfer requests from other devices (Device B),
 * and presents an Approve/Reject notification modal.
 */
@Composable
fun SessionTransferApprovalHost(
    onLoggedOut: (reason: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingRequest by remember { mutableStateOf<ActiveSessionRemoteManager.SessionTransferRequest?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val deviceId = DeviceIdentifierHelper.getDeviceId(context)
        while (true) {
            val user = UserProfileManager.getUserProfile(context)
            if (user != null && user.isLoggedIn) {
                val userIdentifier = user.studentRollNumber.takeIf { !it.isNullOrBlank() } ?: user.name
                try {
                    val req = ActiveSessionRemoteManager.getPendingTransferRequest(
                        userIdentifier = userIdentifier,
                        currentDeviceId = deviceId
                    )
                    if (req != null && pendingRequest?.requestId != req.requestId) {
                        pendingRequest = req
                    } else if (req == null && pendingRequest != null) {
                        pendingRequest = null
                    }
                } catch (_: Exception) {}
            } else {
                pendingRequest = null
            }
            delay(3000L)
        }
    }

    val request = pendingRequest
    if (request != null) {
        Dialog(
            onDismissRequest = { /* Non-dismissible without explicit action */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Alert",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Login Request From Other Device",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A new device is attempting to log in to your account:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = request.toDeviceName.ifBlank { "New Android Device" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Account: ${request.userIdentifier}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "If this is you, tap 'Approve & Log Out' to allow that phone to log in and sign out here.\n\nIf this was not you, tap 'Reject' to protect your account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (isProcessing) return@OutlinedButton
                                isProcessing = true
                                scope.launch {
                                    ActiveSessionRemoteManager.rejectTransferRequest(request)
                                    pendingRequest = null
                                    isProcessing = false
                                    Toast.makeText(context, "Login request rejected.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isProcessing
                        ) {
                            Text("Reject", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                if (isProcessing) return@Button
                                isProcessing = true
                                scope.launch {
                                    ActiveSessionRemoteManager.approveTransferRequest(context, request)
                                    UserProfileManager.logoutUser(context)
                                    pendingRequest = null
                                    isProcessing = false
                                    Toast.makeText(
                                        context,
                                        "Session transferred to ${request.toDeviceName}. You have been logged out.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onLoggedOut("Session transferred to ${request.toDeviceName}")
                                }
                            },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            enabled = !isProcessing
                        ) {
                            Text("Approve & Log Out", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
