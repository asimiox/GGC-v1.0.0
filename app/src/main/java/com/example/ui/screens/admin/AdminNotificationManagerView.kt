package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationType

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F8FB)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationManagerView(
    uiState: AdminControlCenterUiState,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onTargetChange: (String) -> Unit,
    onSendBroadcast: () -> Unit
) {
    var expandedTypeDropdown by remember { mutableStateOf(false) }
    var expandedTargetDropdown by remember { mutableStateOf(false) }

    val notificationTypes: List<Pair<String, String>> = listOf(
        NotificationType.ANNOUNCEMENT_PRIORITY.key to "Urgent Notice / Announcement",
        NotificationType.ANNOUNCEMENT_NEW.key to "General College Notice",
        NotificationType.EVENT_NEW.key to "College Event Notice",
        NotificationType.DOCUMENT_NEW.key to "Official Document Notification"
    )

    val targetAudiences = listOf(
        "All College Members",
        "Students Only (BS & Intermediate)",
        "Faculty & Staff Only",
        "BS IT Students",
        "Computer Science Department"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("admin_notification_manager_view")
    ) {
        // Header
        Text(
            text = "Official Broadcast Controller",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy
        )
        Text(
            text = "Transmit instant push alerts to Android devices across the campus network",
            fontSize = 12.sp,
            color = Color(0xFF718096)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Broadcast Composer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = BrandGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compose Push Alert",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Field
                OutlinedTextField(
                    value = uiState.broadcastTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Alert Title") },
                    placeholder = { Text("e.g. Urgent: College Closure / Exam Schedule Published") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("broadcast_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BorderColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target Audience Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTargetDropdown,
                    onExpandedChange = { expandedTargetDropdown = !expandedTargetDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.broadcastTarget,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Audience") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTargetDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTargetDropdown,
                        onDismissRequest = { expandedTargetDropdown = false }
                    ) {
                        targetAudiences.forEach { target ->
                            DropdownMenuItem(
                                text = { Text(target) },
                                onClick = {
                                    onTargetChange(target)
                                    expandedTargetDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notification Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTypeDropdown,
                    onExpandedChange = { expandedTypeDropdown = !expandedTypeDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = notificationTypes.find { it.first == uiState.broadcastType }?.second ?: "System Broadcast",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Alert Priority & Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTypeDropdown,
                        onDismissRequest = { expandedTypeDropdown = false }
                    ) {
                        notificationTypes.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    onTypeChange(key)
                                    expandedTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Message Field
                OutlinedTextField(
                    value = uiState.broadcastMessage,
                    onValueChange = onMessageChange,
                    label = { Text("Notification Message Body") },
                    placeholder = { Text("Provide complete details for students and faculty...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("broadcast_message_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = BorderColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Send Button
                Button(
                    onClick = onSendBroadcast,
                    enabled = !uiState.isBroadcasting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_broadcast_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    if (uiState.isBroadcasting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcasting Alert...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast Immediately to All Active Devices")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Realtime Delivery Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Broadcasted alerts leverage Supabase Realtime Channels + Android in-app notification banners. Connected devices receive the alert instantly without app restart.",
                    fontSize = 11.sp,
                    color = BrandNavy,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
