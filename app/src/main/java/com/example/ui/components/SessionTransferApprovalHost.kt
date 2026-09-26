package com.example.ui.components

import androidx.compose.runtime.Composable

/**
 * Multi-device concurrency allowed.
 * Single-device session conflict checks and termination modals are removed.
 */
@Composable
fun SessionTransferApprovalHost(
    onLoggedOut: (reason: String) -> Unit
) {
    // Multi-device login is enabled across devices. No session conflict or termination modals.
}
