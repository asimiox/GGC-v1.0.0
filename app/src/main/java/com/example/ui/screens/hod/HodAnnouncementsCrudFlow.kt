package com.example.ui.screens.hod

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AnnouncementDto

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

/**
 * 4. ANNOUNCEMENTS CRUD (Strictly Department Bound)
 * Full management: List, Compose / Broadcast, Edit, and Delete department-specific announcements & notices.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodAnnouncementsCrudScreen(
    state: HodUiState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onCreateAnnouncement: (title: String, content: String, category: String, isPinned: Boolean) -> Unit,
    onUpdateAnnouncement: (id: String, title: String, content: String, category: String, isPinned: Boolean, isPublished: Boolean) -> Unit,
    onDeleteAnnouncement: (id: String, title: String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<AnnouncementDto?>(null) }
    var deletingAnnouncement by remember { mutableStateOf<AnnouncementDto?>(null) }

    val filteredAnnouncements = state.announcementsList.filter { notice ->
        val q = state.announcementsSearchQuery.trim().lowercase()
        q.isBlank() ||
                notice.title.lowercase().contains(q) ||
                notice.content.lowercase().contains(q) ||
                notice.category.lowercase().contains(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Announcements & Notices",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.departmentName} Notice Board",
                            fontSize = 12.sp,
                            color = BrandGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = BrandNavy,
                contentColor = BrandGoldLight,
                modifier = Modifier.testTag("hod_fab_create_announcement")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Notice")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Notice", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        containerColor = BrandBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Department Lock Banner
            Surface(
                color = BrandNavy.copy(alpha = 0.06f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Announcements Bound to ${state.departmentName} Department",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = state.announcementsSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hod_announcements_search_input"),
                placeholder = { Text("Search department notices...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandNavy) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandNavy)
                }
            } else if (filteredAnnouncements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No announcements in ${state.departmentName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap \"New Notice\" to broadcast official notices to your department",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAnnouncements, key = { it.id ?: it.title }) { notice ->
                        HodAnnouncementCardItem(
                            announcement = notice,
                            onEdit = { editingAnnouncement = notice },
                            onDelete = { deletingAnnouncement = notice }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Dialog 1: Compose Announcement
    if (showCreateDialog) {
        HodComposeAnnouncementDialog(
            departmentName = state.departmentName,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, content, category, isPinned ->
                onCreateAnnouncement(title, content, category, isPinned)
                showCreateDialog = false
            }
        )
    }

    // Dialog 2: Edit Announcement
    editingAnnouncement?.let { notice ->
        HodEditAnnouncementDialog(
            announcement = notice,
            departmentName = state.departmentName,
            onDismiss = { editingAnnouncement = null },
            onConfirm = { id, title, content, category, isPinned, isPublished ->
                onUpdateAnnouncement(id, title, content, category, isPinned, isPublished)
                editingAnnouncement = null
            }
        )
    }

    // Dialog 3: Delete Announcement
    deletingAnnouncement?.let { notice ->
        AlertDialog(
            onDismissRequest = { deletingAnnouncement = null },
            title = { Text("Delete Announcement?", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Text(
                    "Are you sure you want to delete the notice \"${notice.title}\" from ${state.departmentName}? This cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        notice.id?.let { onDeleteAnnouncement(it, notice.title) }
                        deletingAnnouncement = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingAnnouncement = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HodAnnouncementCardItem(
    announcement: AnnouncementDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandNavy.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = announcement.category.ifBlank { "General Notice" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (announcement.isPinned) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = BrandGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Pinned",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = if (announcement.isPublished) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (announcement.isPublished) "Published" else "Draft",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (announcement.isPublished) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = announcement.content,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${announcement.authorName}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onDelete,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodComposeAnnouncementDialog(
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("College Event") }
    var isPinned by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("College Event", "Fees Notice", "Date Sheet / Exam", "General Notice", "Holiday Notice", "Scholarship")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Compose Announcement", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Department: $departmentName (Locked)", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category / Notice Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    category = c
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Content / Message *") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pin to Top of Notice Board", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, content, category, isPinned) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Broadcast Notice", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodEditAnnouncementDialog(
    announcement: AnnouncementDto,
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (id: String, title: String, content: String, category: String, isPinned: Boolean, isPublished: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(announcement.title) }
    var content by remember { mutableStateOf(announcement.content) }
    var category by remember { mutableStateOf(announcement.category) }
    var isPinned by remember { mutableStateOf(announcement.isPinned) }
    var isPublished by remember { mutableStateOf(announcement.isPublished) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("College Event", "Fees Notice", "Date Sheet / Exam", "General Notice", "Holiday Notice", "Scholarship")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Announcement", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Department: $departmentName", fontSize = 12.sp, color = BrandGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category / Notice Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    category = c
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Content / Message") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pin to Top", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Published Status", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    announcement.id?.let {
                        onConfirm(it, title, content, category, isPinned, isPublished)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
