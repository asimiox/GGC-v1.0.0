package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.datasource.remote.ActiveSessionRemoteManager
import com.example.data.model.AppRole
import com.example.ui.theme.GgcPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Data needed to prompt the user on Device B when an active session exists on Device A.
 */
data class SessionTransferPromptData(
    val activeDeviceName: String,
    val activeDeviceId: String = "",
    val userIdentifier: String,
    val role: AppRole
)

enum class TransferDialogState {
    PROMPT,
    WAITING_APPROVAL,
    APPROVED,
    REJECTED,
    EXPIRED
}

/**
 * Dialog shown on Device B when an account is active on Device A.
 * Allows user to send a login transfer request to Device A.
 */
@Composable
fun SessionTransferPromptDialog(
    data: SessionTransferPromptData,
    onDismiss: () -> Unit,
    onApproved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dialogState by remember { mutableStateOf(TransferDialogState.PROMPT) }
    var currentRequestId by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableIntStateOf(90) }

    // Polling effect while waiting for approval from Device A
    LaunchedEffect(dialogState, currentRequestId) {
        if (dialogState == TransferDialogState.WAITING_APPROVAL && currentRequestId.isNotBlank()) {
            while (dialogState == TransferDialogState.WAITING_APPROVAL && remainingSeconds > 0) {
                delay(2000L)
                remainingSeconds -= 2
                val status = ActiveSessionRemoteManager.checkTransferRequestStatus(
                    userIdentifier = data.userIdentifier,
                    requestId = currentRequestId
                )
                when (status) {
                    "APPROVED" -> {
                        dialogState = TransferDialogState.APPROVED
                        delay(1200L)
                        onApproved()
                        break
                    }
                    "REJECTED" -> {
                        dialogState = TransferDialogState.REJECTED
                        break
                    }
                    "EXPIRED" -> {
                        dialogState = TransferDialogState.EXPIRED
                        break
                    }
                }
            }
            if (remainingSeconds <= 0 && dialogState == TransferDialogState.WAITING_APPROVAL) {
                dialogState = TransferDialogState.EXPIRED
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (dialogState != TransferDialogState.WAITING_APPROVAL) {
                onDismiss()
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (dialogState) {
                    TransferDialogState.PROMPT -> {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(GgcPrimary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Device",
                                tint = GgcPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Account Active on Other Device",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This account is currently logged in on:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = data.activeDeviceName.ifBlank { "Another Device" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "To sign in on this phone, tap 'Request Approval'. A prompt will appear on ${data.activeDeviceName} where you can tap Approve to log out there and transfer login here.",
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
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    dialogState = TransferDialogState.WAITING_APPROVAL
                                    remainingSeconds = 90
                                    scope.launch {
                                        val req = ActiveSessionRemoteManager.createTransferRequest(
                                            context = context,
                                            userIdentifier = data.userIdentifier,
                                            role = data.role,
                                            activeDeviceName = data.activeDeviceName,
                                            activeDeviceId = data.activeDeviceId
                                        )
                                        currentRequestId = req.requestId
                                    }
                                },
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GgcPrimary)
                            ) {
                                Text("Request Approval", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    TransferDialogState.WAITING_APPROVAL -> {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(GgcPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 3.5.dp,
                                color = GgcPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Waiting for Approval...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "A notification has been sent to ${data.activeDeviceName}. Please tap 'Approve' on that device to continue.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Time remaining: ${remainingSeconds}s",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remainingSeconds < 20) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(
                            onClick = {
                                dialogState = TransferDialogState.PROMPT
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel Request")
                        }
                    }

                    TransferDialogState.APPROVED -> {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Approved",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Login Approved!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Session successfully transferred. Signing in...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    TransferDialogState.REJECTED -> {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Rejected",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Request Rejected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "The login request was denied on ${data.activeDeviceName}. You cannot log in without approval.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close")
                        }
                    }

                    TransferDialogState.EXPIRED -> {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFF57C00).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Expired",
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Request Timed Out",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF57C00),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "No response was received from ${data.activeDeviceName}. Please make sure that device is online, or try again.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Close")
                            }

                            Button(
                                onClick = {
                                    dialogState = TransferDialogState.PROMPT
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
        }
    }
}
