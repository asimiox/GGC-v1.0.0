package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfficialFacultyRegistryDto

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFF3D372)
private val CardBg = Color(0xFFFFFFFF)
private val BackgroundSurface = Color(0xFFF6F8FB)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementView(
    uiState: AdminControlCenterUiState,
    onAssignHod: (facultyUserId: String, departmentName: String) -> Unit,
    onRevokeHod: (targetUserId: String) -> Unit,
    onResetClaim: (registryType: String, recordId: String, reason: String) -> Unit,
    onToggleActive: (registryType: String, recordId: String, currentActive: Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var claimFilter by remember { mutableStateOf<Boolean?>(null) } // null = all, true = claimed, false = unclaimed

    // Dialog state for Claim Reset
    var showResetDialog by remember { mutableStateOf(false) }
    var resetTargetRecordId by remember { mutableStateOf("") }
    var resetTargetName by remember { mutableStateOf("") }
    var resetReason by remember { mutableStateOf("Administrative identity re-verification") }

    // Dialog state for HOD Assignment
    var showHodDialog by remember { mutableStateOf(false) }
    var hodTargetFaculty by remember { mutableStateOf<OfficialFacultyRegistryDto?>(null) }
    var selectedHodDepartment by remember { mutableStateOf("") }

    val filteredFaculty = uiState.facultyList.filter { faculty ->
        val matchesQuery = searchQuery.isBlank() ||
                faculty.fullName.contains(searchQuery, ignoreCase = true) ||
                faculty.facultyId.contains(searchQuery, ignoreCase = true) ||
                faculty.department.contains(searchQuery, ignoreCase = true) ||
                (faculty.institutionalEmail?.contains(searchQuery, ignoreCase = true) == true)
        val matchesClaim = claimFilter == null || faculty.isClaimed == claimFilter
        matchesQuery && matchesClaim
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(16.dp)
            .testTag("admin_user_mgmt_view")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "User & Role Administration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Manage registered staff, faculty privileges, HODs and account claims",
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, ID, department...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = BrandNavy
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_user_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandNavy,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips (All, Claimed, Unclaimed)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = claimFilter == null,
                onClick = { claimFilter = null },
                label = { Text("All Records") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandNavy,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = claimFilter == true,
                onClick = { claimFilter = true },
                label = { Text("Claimed Accounts") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandNavy,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = claimFilter == false,
                onClick = { claimFilter = false },
                label = { Text("Unclaimed") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandNavy,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandNavy)
            }
        } else if (filteredFaculty.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No user records found matching criteria.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFaculty, key = { it.id ?: it.facultyId }) { faculty ->
                    val recordId = faculty.id.orEmpty()
                    FacultyUserAdminCard(
                        faculty = faculty,
                        onAssignHodClick = {
                            hodTargetFaculty = faculty
                            selectedHodDepartment = faculty.department
                            showHodDialog = true
                        },
                        onToggleActiveClick = {
                            onToggleActive("faculty", recordId, faculty.isActive)
                        },
                        onResetClaimClick = {
                            resetTargetRecordId = recordId
                            resetTargetName = faculty.fullName
                            showResetDialog = true
                        }
                    )
                }
            }
        }
    }

    // Reset Claim Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Reset Claimed Account") },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to reset the claimed account for $resetTargetName?",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "This unbinds the Supabase user profile from this official record, allowing re-registration if credentials were lost.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetReason,
                        onValueChange = { resetReason = it },
                        label = { Text("Audit Reason") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetClaim("faculty", resetTargetRecordId, resetReason)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Confirm Reset Claim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // HOD Assignment Dialog
    if (showHodDialog && hodTargetFaculty != null) {
        AlertDialog(
            onDismissRequest = { showHodDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BrandGold,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Assign Head of Department (HOD)") },
            text = {
                Column {
                    Text(
                        text = "Promote ${hodTargetFaculty?.fullName} (${hodTargetFaculty?.facultyId}) to Head of Department?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = selectedHodDepartment,
                        onValueChange = { selectedHodDepartment = it },
                        label = { Text("Department") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        hodTargetFaculty?.claimedByUserId?.let { userId ->
                            onAssignHod(userId, selectedHodDepartment)
                        }
                        showHodDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("Assign HOD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHodDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FacultyUserAdminCard(
    faculty: OfficialFacultyRegistryDto,
    onAssignHodClick: () -> Unit,
    onToggleActiveClick: () -> Unit,
    onResetClaimClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = faculty.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Text(
                            text = "${faculty.designation} • Dept. of ${faculty.department}",
                            fontSize = 11.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }

                // Claimed / Unclaimed Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (faculty.isClaimed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (faculty.isClaimed) "Claimed" else "Unclaimed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (faculty.isClaimed) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info rows: Faculty ID, Email, Active state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${faculty.facultyId}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Text(
                    text = if (faculty.isActive) "● Active" else "○ Deactivated",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (faculty.isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (faculty.isClaimed && !faculty.claimedByUserId.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = onAssignHodClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BrandGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Assign HOD", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onResetClaimClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Claim", fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onToggleActiveClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (faculty.isActive) "Deactivate" else "Activate",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
