package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.data.model.AppRole

/**
 * Data needed to prompt the user on Device B when an active session exists on Device A.
 */
data class SessionTransferPromptData(
    val activeDeviceName: String,
    val activeDeviceId: String = "",
    val userIdentifier: String,
    val role: AppRole
)

/**
 * Multi-device login: Automatically proceeds with approval immediately without blocking the user.
 */
@Composable
fun SessionTransferPromptDialog(
    data: SessionTransferPromptData,
    onDismiss: () -> Unit,
    onApproved: () -> Unit
) {
    LaunchedEffect(Unit) {
        onApproved()
    }
}
