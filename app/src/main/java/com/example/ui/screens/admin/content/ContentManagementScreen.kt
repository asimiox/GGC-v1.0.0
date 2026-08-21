package com.example.ui.screens.admin.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserProfileManager
import com.example.data.model.AnnouncementDto
import com.example.data.model.AppRole
import com.example.data.model.CollegeEventDto
import com.example.data.model.CourseOutlineDto
import com.example.data.model.OfficialDocumentDto
import com.example.data.model.ProspectusDto

private val BrandNavy = Color(0xFF061B52)
private val BrandNavyDark = Color(0xFF030D2B)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandSuccess = Color(0xFF1B873F)
private val BrandSuccessContainer = Color(0xFFE8F5E9)
private val BrandError = Color(0xFFBA1A1A)
private val BrandErrorContainer = Color(0xFFFFDAD6)
private val BrandGold = Color(0xFFC59B27)
private val BrandGoldContainer = Color(0xFFFFF8E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentManagementScreen(
    onBack: () -> Unit,
    initialTab: ContentSectionTab = ContentSectionTab.ANNOUNCEMENTS,
    showTopBar: Boolean = true,
    viewModel: ContentManagementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by UserProfileManager.userProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Initial Tab trigger
    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    // Dialog States
    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var selectedAnnouncementForEdit by remember { mutableStateOf<AnnouncementDto?>(null) }

    var showEventDialog by remember { mutableStateOf(false) }
    var selectedEventForEdit by remember { mutableStateOf<CollegeEventDto?>(null) }

    var showDocumentDialog by remember { mutableStateOf(false) }
    var selectedDocumentForEdit by remember { mutableStateOf<OfficialDocumentDto?>(null) }

    var showOutlineDialog by remember { mutableStateOf(false) }
    var selectedOutlineForEdit by remember { mutableStateOf<CourseOutlineDto?>(null) }

    var showProspectusDialog by remember { mutableStateOf(false) }
    var selectedProspectusForEdit by remember { mutableStateOf<ProspectusDto?>(null) }

    var deleteConfirmationItem by remember { mutableStateOf<DeleteTarget?>(null) }

    // Snackbar effect
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Role verification (Admin, HOD, and Faculty/Teacher)
    val isAuthorized = userProfile.isVerified && (userProfile.isAdmin || userProfile.isHod || userProfile.isFaculty)

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Content Portal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    userProfile.isAdmin -> "College Administrator • Full Access"
                                    userProfile.isHod -> "HOD Portal • ${userProfile.department ?: "Department"} Scope"
                                    userProfile.isFaculty -> "Faculty Portal • ${userProfile.department ?: "Academic Management"}"
                                    else -> "Unauthorized"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = BrandGold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("btn_content_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.loadCurrentTab() },
                            modifier = Modifier.testTag("btn_content_refresh")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Content",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavyDark)
                )
            }
        },
        floatingActionButton = {
            if (isAuthorized) {
                FloatingActionButton(
                    onClick = {
                        when (uiState.selectedTab) {
                            ContentSectionTab.ANNOUNCEMENTS -> {
                                selectedAnnouncementForEdit = null
                                showAnnouncementDialog = true
                            }
                            ContentSectionTab.EVENTS -> {
                                selectedEventForEdit = null
                                showEventDialog = true
                            }
                            ContentSectionTab.DOCUMENTS -> {
                                selectedDocumentForEdit = null
                                showDocumentDialog = true
                            }
                            ContentSectionTab.COURSE_OUTLINES -> {
                                selectedOutlineForEdit = null
                                showOutlineDialog = true
                            }
                            ContentSectionTab.PROSPECTUS -> {
                                selectedProspectusForEdit = null
                                showProspectusDialog = true
                            }
                        }
                    },
                    containerColor = BrandNavy,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_content")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Content"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .testTag("content_management_screen")
    ) { innerPadding ->
        if (!isAuthorized) {
            // Unauthorized Banner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BrandBackground),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandError,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Access Restricted",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Official content management requires verified Administrator or Head of Department (HOD) credentials. Student profiles do not have write access.",
                            fontSize = 14.sp,
                            color = BrandTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Return to Dashboard")
                        }
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandBackground)
        ) {
            // 1. Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = Color.White,
                contentColor = BrandNavy,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                        color = BrandNavy,
                        height = 3.dp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                ContentSectionTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (uiState.selectedTab == tab) BrandNavy else BrandTextMuted
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

            // 2. Search & Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search Box
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search ${uiState.selectedTab.title.lowercase()}...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BrandTextMuted
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BrandTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("input_content_search"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandNavy,
                        unfocusedBorderColor = Color(0xFFD6DCE6),
                        focusedContainerColor = Color(0xFFFBFBFB),
                        unfocusedContainerColor = Color(0xFFFBFBFB)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status filter: All / Published / Draft
                    FilterChip(
                        selected = uiState.filterPublished == null,
                        onClick = { viewModel.setPublishedFilter(null) },
                        label = { Text("All", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandNavy,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_all")
                    )

                    FilterChip(
                        selected = uiState.filterPublished == true,
                        onClick = { viewModel.setPublishedFilter(true) },
                        label = { Text("Published", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandSuccess,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_published")
                    )

                    FilterChip(
                        selected = uiState.filterPublished == false,
                        onClick = { viewModel.setPublishedFilter(false) },
                        label = { Text("Drafts", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7A879D),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_drafts")
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

            // Progress Banner during file upload
            if (uiState.isUploadingFile) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandGoldContainer)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = BrandGold,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.uploadProgressMessage ?: "Uploading file to storage...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandNavy
                        )
                    }
                }
            }

            // Error Banner
            if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandErrorContainer)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = BrandError, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.errorMessage ?: "", fontSize = 13.sp, color = BrandError)
                        }
                        IconButton(onClick = { viewModel.clearMessages() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = BrandError, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 3. Tab Contents
            if (uiState.isLoading && !uiState.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BrandNavy)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Loading ${uiState.selectedTab.title.lowercase()}...", fontSize = 14.sp, color = BrandTextMuted)
                    }
                }
            } else {
                when (uiState.selectedTab) {
                    ContentSectionTab.ANNOUNCEMENTS -> AnnouncementsListSection(
                        announcements = uiState.announcements.filter { item ->
                            (uiState.filterPublished == null || item.isPublished == uiState.filterPublished) &&
                            (uiState.searchQuery.isBlank() || item.title.contains(uiState.searchQuery, ignoreCase = true) || item.content.contains(uiState.searchQuery, ignoreCase = true))
                        },
                        departments = uiState.departments,
                        onEdit = {
                            selectedAnnouncementForEdit = it
                            showAnnouncementDialog = true
                        },
                        onTogglePublish = { viewModel.toggleAnnouncementPublish(it.id ?: "", it.isPublished) },
                        onDelete = { deleteConfirmationItem = DeleteTarget.Announcement(it) }
                    )
                    ContentSectionTab.EVENTS -> EventsListSection(
                        events = uiState.events.filter { item ->
                            (uiState.filterPublished == null || item.isPublished == uiState.filterPublished) &&
                            (uiState.searchQuery.isBlank() || item.title.contains(uiState.searchQuery, ignoreCase = true) || item.description.contains(uiState.searchQuery, ignoreCase = true))
                        },
                        departments = uiState.departments,
                        onEdit = {
                            selectedEventForEdit = it
                            showEventDialog = true
                        },
                        onTogglePublish = { viewModel.toggleEventPublish(it.id ?: "", it.isPublished) },
                        onDelete = { deleteConfirmationItem = DeleteTarget.Event(it) }
                    )
                    ContentSectionTab.DOCUMENTS -> DocumentsListSection(
                        documents = uiState.documents.filter { item ->
                            (uiState.filterPublished == null || item.isPublished == uiState.filterPublished) &&
                            (uiState.searchQuery.isBlank() || item.title.contains(uiState.searchQuery, ignoreCase = true) || item.fileName.contains(uiState.searchQuery, ignoreCase = true))
                        },
                        departments = uiState.departments,
                        onEdit = {
                            selectedDocumentForEdit = it
                            showDocumentDialog = true
                        },
                        onTogglePublish = { viewModel.toggleDocumentPublish(it.id ?: "", it.isPublished) },
                        onDelete = { deleteConfirmationItem = DeleteTarget.Document(it) }
                    )
                    ContentSectionTab.COURSE_OUTLINES -> CourseOutlinesListSection(
                        outlines = uiState.courseOutlines.filter { item ->
                            (uiState.filterPublished == null || item.isPublished == uiState.filterPublished) &&
                            (uiState.searchQuery.isBlank() || item.title.contains(uiState.searchQuery, ignoreCase = true))
                        },
                        courses = uiState.courses,
                        departments = uiState.departments,
                        programs = uiState.programs,
                        onEdit = {
                            selectedOutlineForEdit = it
                            showOutlineDialog = true
                        },
                        onTogglePublish = { viewModel.toggleCourseOutlinePublish(it.id ?: "", it.isPublished) },
                        onDelete = { deleteConfirmationItem = DeleteTarget.CourseOutline(it) }
                    )
                    ContentSectionTab.PROSPECTUS -> ProspectusListSection(
                        prospectusList = uiState.prospectusList.filter { item ->
                            (uiState.filterPublished == null || item.isPublished == uiState.filterPublished) &&
                            (uiState.searchQuery.isBlank() || item.title.contains(uiState.searchQuery, ignoreCase = true) || item.academicSession.contains(uiState.searchQuery, ignoreCase = true))
                        },
                        onEdit = {
                            selectedProspectusForEdit = it
                            showProspectusDialog = true
                        },
                        onToggleCurrent = { viewModel.toggleProspectusCurrent(it.id ?: "", !it.isCurrent) },
                        onTogglePublish = { viewModel.toggleProspectusPublish(it.id ?: "", it.isPublished) },
                        onDelete = { deleteConfirmationItem = DeleteTarget.Prospectus(it) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAnnouncementDialog) {
        AnnouncementManageDialog(
            announcement = selectedAnnouncementForEdit,
            departments = uiState.departments,
            userRole = userProfile.appRole,
            userDepartmentId = uiState.userDepartmentId,
            onDismiss = { showAnnouncementDialog = false },
            onSave = { id, title, content, category, deptId, isPinned, isPublished, attachBytes, attachFileName ->
                viewModel.saveAnnouncement(
                    id = id,
                    title = title,
                    content = content,
                    category = category,
                    departmentId = deptId,
                    isPinned = isPinned,
                    isPublished = isPublished,
                    attachmentBytes = attachBytes,
                    attachmentFileName = attachFileName,
                    onSuccess = { showAnnouncementDialog = false }
                )
            }
        )
    }

    if (showEventDialog) {
        EventManageDialog(
            event = selectedEventForEdit,
            departments = uiState.departments,
            userRole = userProfile.appRole,
            userDepartmentId = uiState.userDepartmentId,
            onDismiss = { showEventDialog = false },
            onSave = { id, title, desc, date, time, venue, cat, deptId, isUpcoming, isPublished, bannerBytes, bannerFileName ->
                viewModel.saveEvent(
                    id = id,
                    title = title,
                    description = desc,
                    eventDate = date,
                    eventTime = time,
                    venue = venue,
                    category = cat,
                    departmentId = deptId,
                    isUpcoming = isUpcoming,
                    isPublished = isPublished,
                    bannerBytes = bannerBytes,
                    bannerFileName = bannerFileName,
                    onSuccess = { showEventDialog = false }
                )
            }
        )
    }

    if (showDocumentDialog) {
        DocumentManageDialog(
            document = selectedDocumentForEdit,
            departments = uiState.departments,
            userRole = userProfile.appRole,
            userDepartmentId = uiState.userDepartmentId,
            onDismiss = { showDocumentDialog = false },
            onSave = { id, title, desc, docType, deptId, session, isPublished, fileBytes, fileName ->
                viewModel.saveOfficialDocument(
                    id = id,
                    title = title,
                    description = desc,
                    documentType = docType,
                    departmentId = deptId,
                    academicSession = session,
                    isPublished = isPublished,
                    fileBytes = fileBytes,
                    fileName = fileName,
                    onSuccess = { showDocumentDialog = false }
                )
            }
        )
    }

    if (showOutlineDialog) {
        CourseOutlineManageDialog(
            outline = selectedOutlineForEdit,
            departments = uiState.departments,
            programs = uiState.programs,
            courses = uiState.courses,
            userRole = userProfile.appRole,
            userDepartmentId = uiState.userDepartmentId,
            onDismiss = { showOutlineDialog = false },
            onSave = { id, courseId, progId, deptId, title, session, sem, content, isPublished, fileBytes, fileName ->
                viewModel.saveCourseOutline(
                    id = id,
                    courseId = courseId,
                    programId = progId,
                    departmentId = deptId,
                    title = title,
                    sessionYear = session,
                    semesterNumber = sem,
                    outlineContent = content,
                    isPublished = isPublished,
                    fileBytes = fileBytes,
                    fileName = fileName,
                    onSuccess = { showOutlineDialog = false }
                )
            }
        )
    }

    if (showProspectusDialog) {
        ProspectusManageDialog(
            prospectus = selectedProspectusForEdit,
            onDismiss = { showProspectusDialog = false },
            onSave = { id, title, session, level, desc, isCurrent, isPublished, fileBytes, fileName ->
                viewModel.saveProspectus(
                    id = id,
                    title = title,
                    academicSession = session,
                    programLevel = level,
                    description = desc,
                    isCurrent = isCurrent,
                    isPublished = isPublished,
                    fileBytes = fileBytes,
                    fileName = fileName,
                    onSuccess = { showProspectusDialog = false }
                )
            }
        )
    }

    // Delete Confirmation Modal
    deleteConfirmationItem?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteConfirmationItem = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = BrandError) },
            title = { Text("Confirm Deletion", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${target.displayTitle}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        when (target) {
                            is DeleteTarget.Announcement -> viewModel.deleteAnnouncement(target.item.id ?: "", target.item.attachmentStoragePath)
                            is DeleteTarget.Event -> viewModel.deleteEvent(target.item.id ?: "", target.item.bannerStoragePath)
                            is DeleteTarget.Document -> viewModel.deleteOfficialDocument(target.item.id ?: "", target.item.storagePath)
                            is DeleteTarget.CourseOutline -> viewModel.deleteCourseOutline(target.item.id ?: "", target.item.storagePath)
                            is DeleteTarget.Prospectus -> viewModel.deleteProspectus(target.item.id ?: "", target.item.storagePath)
                        }
                        deleteConfirmationItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandError)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmationItem = null }) {
                    Text("Cancel", color = BrandTextMuted)
                }
            }
        )
    }
}

// Sealed class for deletion confirmation
sealed class DeleteTarget(val displayTitle: String) {
    class Announcement(val item: AnnouncementDto) : DeleteTarget(item.title)
    class Event(val item: CollegeEventDto) : DeleteTarget(item.title)
    class Document(val item: OfficialDocumentDto) : DeleteTarget(item.title)
    class CourseOutline(val item: CourseOutlineDto) : DeleteTarget(item.title)
    class Prospectus(val item: ProspectusDto) : DeleteTarget(item.title)
}

// =============================================================================
// SUB-SECTIONS
// =============================================================================

@Composable
private fun AnnouncementsListSection(
    announcements: List<AnnouncementDto>,
    departments: List<com.example.data.model.DepartmentDto>,
    onEdit: (AnnouncementDto) -> Unit,
    onTogglePublish: (AnnouncementDto) -> Unit,
    onDelete: (AnnouncementDto) -> Unit
) {
    if (announcements.isEmpty()) {
        EmptyContentView(icon = Icons.Default.Campaign, message = "No announcements found")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(announcements, key = { it.id ?: it.title }) { item ->
            val deptName = departments.firstOrNull { it.id == item.departmentId }?.name ?: "College-Wide"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_announcement_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.isPinned) {
                                Box(
                                    modifier = Modifier
                                        .background(BrandGoldContainer, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PushPin, contentDescription = null, tint = BrandGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("PINNED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .background(BrandNavy.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(item.category, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF2F4F7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(deptName, fontSize = 11.sp, color = BrandTextMuted)
                            }
                        }

                        StatusBadge(isPublished = item.isPublished)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.content,
                        fontSize = 13.sp,
                        color = BrandTextMuted,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    if (!item.attachmentName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item.attachmentName, fontSize = 11.sp, color = BrandNavy, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF2F4F7))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.authorName,
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )

                        Row {
                            IconButton(onClick = { onTogglePublish(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (item.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Publish",
                                    tint = BrandTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandNavy, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsListSection(
    events: List<CollegeEventDto>,
    departments: List<com.example.data.model.DepartmentDto>,
    onEdit: (CollegeEventDto) -> Unit,
    onTogglePublish: (CollegeEventDto) -> Unit,
    onDelete: (CollegeEventDto) -> Unit
) {
    if (events.isEmpty()) {
        EmptyContentView(icon = Icons.Default.Event, message = "No events registered")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events, key = { it.id ?: it.title }) { item ->
            val deptName = departments.firstOrNull { it.id == item.departmentId }?.name ?: "College-Wide"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_event_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(BrandNavy.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(item.category, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF2F4F7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(deptName, fontSize = 11.sp, color = BrandTextMuted)
                            }
                        }

                        StatusBadge(isPublished = item.isPublished)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        color = BrandTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${item.eventDate} ${item.eventTime ?: ""}".trim(),
                                fontSize = 12.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        item.venue?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = BrandTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = BrandTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF2F4F7))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onTogglePublish(item) }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = if (item.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Publish",
                                tint = BrandTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandNavy, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsListSection(
    documents: List<OfficialDocumentDto>,
    departments: List<com.example.data.model.DepartmentDto>,
    onEdit: (OfficialDocumentDto) -> Unit,
    onTogglePublish: (OfficialDocumentDto) -> Unit,
    onDelete: (OfficialDocumentDto) -> Unit
) {
    if (documents.isEmpty()) {
        EmptyContentView(icon = Icons.Default.Description, message = "No official documents uploaded")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(documents, key = { it.id ?: it.fileName }) { item ->
            val deptName = departments.firstOrNull { it.id == item.departmentId }?.name ?: "All College"
            val typeLabel = DOCUMENT_TYPE_LABELS[item.documentType] ?: item.documentType

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_doc_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(BrandNavy.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(typeLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                        }

                        StatusBadge(isPublished = item.isPublished)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    if (!item.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = BrandTextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.fileName,
                                fontSize = 12.sp,
                                color = BrandNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        item.academicSession?.let {
                            Text(
                                text = "Session: $it",
                                fontSize = 11.sp,
                                color = BrandTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF2F4F7))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Scope: $deptName",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )

                        Row {
                            IconButton(onClick = { onTogglePublish(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (item.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Publish",
                                    tint = BrandTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandNavy, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseOutlinesListSection(
    outlines: List<CourseOutlineDto>,
    courses: List<com.example.data.model.CourseDto>,
    departments: List<com.example.data.model.DepartmentDto>,
    programs: List<com.example.data.model.AcademicProgramDto>,
    onEdit: (CourseOutlineDto) -> Unit,
    onTogglePublish: (CourseOutlineDto) -> Unit,
    onDelete: (CourseOutlineDto) -> Unit
) {
    if (outlines.isEmpty()) {
        EmptyContentView(icon = Icons.AutoMirrored.Outlined.MenuBook, message = "No course outlines uploaded")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(outlines, key = { it.id ?: it.title }) { item ->
            val course = courses.firstOrNull { it.id == item.courseId }
            val deptName = departments.firstOrNull { it.id == item.departmentId }?.name ?: ""
            val progTitle = programs.firstOrNull { it.id == item.programId }?.title ?: ""

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_outline_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(BrandNavy.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("Sem ${item.semesterNumber}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                            }
                            if (course != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFF2F4F7), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(course.code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                }
                            }
                        }

                        StatusBadge(isPublished = item.isPublished)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    if (progTitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$progTitle • $deptName",
                            fontSize = 12.sp,
                            color = BrandTextMuted
                        )
                    }

                    if (!item.fileName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item.fileName, fontSize = 11.sp, color = BrandNavy, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF2F4F7))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Session: ${item.sessionYear ?: "Active"}",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )

                        Row {
                            IconButton(onClick = { onTogglePublish(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (item.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Publish",
                                    tint = BrandTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandNavy, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProspectusListSection(
    prospectusList: List<ProspectusDto>,
    onEdit: (ProspectusDto) -> Unit,
    onToggleCurrent: (ProspectusDto) -> Unit,
    onTogglePublish: (ProspectusDto) -> Unit,
    onDelete: (ProspectusDto) -> Unit
) {
    if (prospectusList.isEmpty()) {
        EmptyContentView(icon = Icons.Default.Description, message = "No prospectus entries found")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prospectusList, key = { it.id ?: it.fileName }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_prospectus_${item.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .background(BrandGoldContainer, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = BrandGold, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("CURRENT ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .background(BrandNavy.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(item.academicSession, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                            }
                        }

                        StatusBadge(isPublished = item.isPublished)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    if (!item.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = BrandTextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item.fileName, fontSize = 12.sp, color = BrandNavy, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF2F4F7))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onToggleCurrent(item) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (item.isCurrent) BrandGold else BrandTextMuted.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (item.isCurrent) "Active Prospectus" else "Make Current",
                                    fontSize = 12.sp,
                                    color = if (item.isCurrent) BrandGold else BrandTextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = { onTogglePublish(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (item.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Publish",
                                    tint = BrandTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandNavy, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// REUSABLE HELPER VIEWS
// =============================================================================

@Composable
private fun StatusBadge(isPublished: Boolean) {
    if (isPublished) {
        Box(
            modifier = Modifier
                .background(BrandSuccessContainer, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text("PUBLISHED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandSuccess)
        }
    } else {
        Box(
            modifier = Modifier
                .background(Color(0xFFECEFF1), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text("DRAFT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandTextMuted)
        }
    }
}

@Composable
private fun EmptyContentView(icon: ImageVector, message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BrandNavy.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandNavy.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = BrandTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Use the + button below to create new official content.",
                fontSize = 12.sp,
                color = BrandTextMuted.copy(alpha = 0.8f)
            )
        }
    }
}
