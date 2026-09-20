package com.example.ui.screens.hod

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.AnnouncementDto
import com.example.ui.screens.admin.PostReadersDialog

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDeep = Color(0xFF030D29)
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
    var viewingReadersAnnouncement by remember { mutableStateOf<AnnouncementDto?>(null) }

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
                            onViewReaders = { viewingReadersAnnouncement = notice },
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

    // Dialog 4: View Student Readers
    viewingReadersAnnouncement?.let { notice ->
        PostReadersDialog(
            announcement = notice,
            onDismiss = { viewingReadersAnnouncement = null }
        )
    }
}

@Composable
fun HodAnnouncementCardItem(
    announcement: AnnouncementDto,
    onViewReaders: () -> Unit,
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
                        onClick = onViewReaders,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandNavy),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Readers", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

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
    var category by remember { mutableStateOf("General Notice") }
    var isPinned by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("General Notice", "College Event", "Fees Notice", "Date Sheet / Exam", "Holiday Notice", "Scholarship")
    val screenBg = Color(0xFFF6F6F6)
    val borderColor = Color(0xFFD5DDE7)
    val cardBg = Color.White
    val textSecondary = Color(0xFF64748B)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBg)
                .testTag("hod_compose_announcement_dialog"),
            color = screenBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top App Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = screenBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_hod_announcement")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(0.5.dp, borderColor),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(3.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                                    contentDescription = "GGC Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                color = BrandNavy,
                                maxLines = 1
                            )
                            Text(
                                text = "Department of ",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // Scrollable form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "New Announcement",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Publish updates, notices, and notifications for  students and faculty.",
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text("Announcement Title *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        placeholder = { Text("e.g., Mid-Term Examination Schedule", color = textSecondary) },
                        modifier = Modifier.fillMaxWidth().testTag("hod_input_announcement_title"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        singleLine = true,
                        isError = titleError != null
                    )
                    if (titleError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = titleError!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Category
                    Text("Category *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryExpanded = true }
                                .testTag("hod_select_announcement_category"),
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 15.sp,
                                    color = BrandNavy,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Select Category",
                                    tint = textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal,
                                            color = if (category == cat) BrandNavy else Color(0xFF1E293B)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Message Content
                    Text("Message Content *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            if (contentError != null) contentError = null
                        },
                        placeholder = { Text("Write your full announcement details here...", color = textSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("hod_input_announcement_content"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        maxLines = 8,
                        isError = contentError != null
                    )
                    if (contentError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = contentError!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pin toggle card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pin to top of feed",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "Highlights this notice at the top of the department announcements list.",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                            Switch(
                                checked = isPinned,
                                onCheckedChange = { isPinned = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrandNavy
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Primary button
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Title is required"
                                valid = false
                            }
                            if (content.isBlank()) {
                                contentError = "Content is required"
                                valid = false
                            }
                            if (valid) {
                                onConfirm(title.trim(), content.trim(), category, isPinned)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("hod_btn_publish_announcement"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Publish Announcement",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
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
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("General Notice", "College Event", "Fees Notice", "Date Sheet / Exam", "Holiday Notice", "Scholarship")
    val screenBg = Color(0xFFF6F6F6)
    val borderColor = Color(0xFFD5DDE7)
    val cardBg = Color.White
    val textSecondary = Color(0xFF64748B)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBg)
                .testTag("hod_edit_announcement_dialog"),
            color = screenBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top App Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = screenBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_edit_hod_announcement")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(0.5.dp, borderColor),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(3.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                                    contentDescription = "GGC Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                color = BrandNavy,
                                maxLines = 1
                            )
                            Text(
                                text = "Department of ",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // Scrollable form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Edit Announcement",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Modify announcement details, category, or publication status.",
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text("Announcement Title *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (titleError != null) titleError = null
                        },
                        modifier = Modifier.fillMaxWidth().testTag("hod_edit_input_title"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        singleLine = true,
                        isError = titleError != null
                    )
                    if (titleError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = titleError!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Category
                    Text("Category *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryExpanded = true }
                                .testTag("hod_edit_select_category"),
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 15.sp,
                                    color = BrandNavy,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Select Category",
                                    tint = textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal,
                                            color = if (category == cat) BrandNavy else Color(0xFF1E293B)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Message Content
                    Text("Message Content *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            if (contentError != null) contentError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("hod_edit_input_content"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = BrandNavy,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = BrandNavy,
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        maxLines = 8,
                        isError = contentError != null
                    )
                    if (contentError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = contentError!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pin & Published toggles
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Pin to top of feed",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandNavy
                                    )
                                    Text(
                                        text = "Keep at top of department feed",
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                                Switch(
                                    checked = isPinned,
                                    onCheckedChange = { isPinned = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = BrandNavy
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = borderColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Published to Students",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandNavy
                                    )
                                    Text(
                                        text = if (isPublished) "Visible to all department students" else "Saved as draft (hidden)",
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                                Switch(
                                    checked = isPublished,
                                    onCheckedChange = { isPublished = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = BrandNavy
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Primary button
                    Button(
                        onClick = {
                            var valid = true
                            if (title.isBlank()) {
                                titleError = "Title is required"
                                valid = false
                            }
                            if (content.isBlank()) {
                                contentError = "Content is required"
                                valid = false
                            }
                            if (valid) {
                                announcement.id?.let {
                                    onConfirm(it, title.trim(), content.trim(), category, isPinned, isPublished)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("hod_btn_save_changes"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Save Changes",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
