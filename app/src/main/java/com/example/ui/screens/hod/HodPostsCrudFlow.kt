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
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PostAdd
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
import com.example.data.model.CollegeEventDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldLight = Color(0xFFE5C058)
private val BrandBg = Color(0xFFF6F8FB)

/**
 * 3. POSTS CRUD (Strictly Department Bound)
 * Full management: List, Compose / Post, Edit, and Delete department-specific posts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodPostsCrudScreen(
    state: HodUiState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onCreatePost: (title: String, content: String, category: String, venueOrTarget: String, date: String) -> Unit,
    onUpdatePost: (id: String, title: String, content: String, category: String, venueOrTarget: String, date: String) -> Unit,
    onDeletePost: (id: String, title: String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingPost by remember { mutableStateOf<CollegeEventDto?>(null) }
    var deletingPost by remember { mutableStateOf<CollegeEventDto?>(null) }

    val filteredPosts = state.postsList.filter { post ->
        val q = state.postsSearchQuery.trim().lowercase()
        q.isBlank() ||
                post.title.lowercase().contains(q) ||
                post.description.lowercase().contains(q) ||
                post.category.lowercase().contains(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Department Posts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.departmentName} Feed",
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
                modifier = Modifier.testTag("hod_fab_create_post")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Post", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                        text = "Posts Bound to ${state.departmentName} Department",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = state.postsSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hod_posts_search_input"),
                placeholder = { Text("Search department posts...") },
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
            } else if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No posts published yet in ${state.departmentName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap \"Create Post\" to publish department updates & articles",
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
                    items(filteredPosts, key = { it.id ?: it.title }) { post ->
                        HodPostCardItem(
                            post = post,
                            onEdit = { editingPost = post },
                            onDelete = { deletingPost = post }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Dialog 1: Create Post
    if (showCreateDialog) {
        HodComposePostDialog(
            departmentName = state.departmentName,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, content, category, venue, date ->
                onCreatePost(title, content, category, venue, date)
                showCreateDialog = false
            }
        )
    }

    // Dialog 2: Edit Post
    editingPost?.let { post ->
        HodEditPostDialog(
            post = post,
            departmentName = state.departmentName,
            onDismiss = { editingPost = null },
            onConfirm = { id, title, content, category, venue, date ->
                onUpdatePost(id, title, content, category, venue, date)
                editingPost = null
            }
        )
    }

    // Dialog 3: Delete Post
    deletingPost?.let { post ->
        AlertDialog(
            onDismissRequest = { deletingPost = null },
            title = { Text("Delete Post?", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Text(
                    "Are you sure you want to delete the post \"${post.title}\" from ${state.departmentName}? This action cannot be reversed."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        post.id?.let { onDeletePost(it, post.title) }
                        deletingPost = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPost = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HodPostCardItem(
    post: CollegeEventDto,
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
                    color = BrandGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = post.category.ifBlank { "Academic" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.eventDate,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.description,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            if (!post.venue.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.venue,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HodComposePostDialog(
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String, venueOrTarget: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic Notice") }
    var venueOrTarget by remember { mutableStateOf("Department Hall") }
    var dateString by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()))
    }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Academic Notice", "Workshop / Seminar", "Project Showcase", "Exam Guidelines", "Department Activity", "Class Schedule")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Create Department Post", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("Target: $departmentName (Locked)", fontSize = 12.sp, color = BrandGold)
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
                    label = { Text("Post Title *") },
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
                        label = { Text("Category / Topic") },
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
                    label = { Text("Post Content / Body *") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venueOrTarget,
                    onValueChange = { venueOrTarget = it },
                    label = { Text("Target Audience / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, content, category, venueOrTarget, dateString) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
            ) {
                Text("Publish Post", color = Color.White)
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
fun HodEditPostDialog(
    post: CollegeEventDto,
    departmentName: String,
    onDismiss: () -> Unit,
    onConfirm: (id: String, title: String, content: String, category: String, venueOrTarget: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf(post.title) }
    var content by remember { mutableStateOf(post.description) }
    var category by remember { mutableStateOf(post.category) }
    var venueOrTarget by remember { mutableStateOf(post.venue ?: "Department Hall") }
    var dateString by remember { mutableStateOf(post.eventDate) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Academic Notice", "Workshop / Seminar", "Project Showcase", "Exam Guidelines", "Department Activity", "Class Schedule")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Department Post", fontWeight = FontWeight.Bold, color = BrandNavy)
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
                    label = { Text("Post Title") },
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
                        label = { Text("Category / Topic") },
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
                    label = { Text("Post Content / Body") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venueOrTarget,
                    onValueChange = { venueOrTarget = it },
                    label = { Text("Target Audience / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    post.id?.let {
                        onConfirm(it, title, content, category, venueOrTarget, dateString)
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
