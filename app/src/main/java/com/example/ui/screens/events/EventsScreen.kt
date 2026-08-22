package com.example.ui.screens.events

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ZoomIn
import com.example.data.model.CollegeEventDto
import com.example.ui.components.FileAttachmentCard
import com.example.ui.components.ImageViewerDialog
import com.example.ui.util.FileUtils

private val BrandNavy = Color(0xFF061B52)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandBackground = Color(0xFFF6F6F6)

@Composable
fun EventsScreen(
    onBack: (() -> Unit)? = null,
    viewModel: EventsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredEvents = viewModel.getFilteredEvents()
    val categories = listOf("All", "College", "Sports", "Academic", "Seminar", "Workshop", "Cultural")

    if (uiState.selectedEvent != null) {
        val bannerUrl = viewModel.getBannerUrl(uiState.selectedEvent?.bannerStoragePath)
        CollegeEventDetailScreen(
            event = uiState.selectedEvent!!,
            bannerUrl = bannerUrl,
            attachmentUrl = bannerUrl,
            onBack = { viewModel.selectEvent(null) }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("events_screen_container")
    ) {
        // Top App Bar
        SurfaceHeader(
            title = "Events & Activities",
            subtitle = "Seminars, Workshops, Sports & Institutional Programs",
            onBack = onBack,
            onRefresh = { viewModel.loadPublishedEvents() }
        )

        // Minimal Tab Bar (Upcoming / Past)
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = Color.White,
            contentColor = BrandNavy,
            indicator = { tabPositions ->
                if (uiState.selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = BrandNavy
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.setSelectedTab(0) },
                text = {
                    Text(
                        text = "Upcoming Events (${uiState.upcomingEvents.size})",
                        fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (uiState.selectedTab == 0) BrandNavy else BrandTextMuted
                    )
                },
                modifier = Modifier.testTag("events_tab_upcoming")
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.setSelectedTab(1) },
                text = {
                    Text(
                        text = "Past Activities (${uiState.pastEvents.size})",
                        fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (uiState.selectedTab == 1) BrandNavy else BrandTextMuted
                    )
                },
                modifier = Modifier.testTag("events_tab_past")
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        // Search & Category Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search events by keyword, venue...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BrandTextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("events_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFD),
                    unfocusedContainerColor = Color(0xFFF8FAFD)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = uiState.selectedCategory.equals(category, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) BrandNavy else Color(0xFFEEF3FF))
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else BrandNavy
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("events_loading_indicator"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandNavy)
            }
        } else if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .testTag("events_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF3FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "No matching events found" else "No published events available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (uiState.selectedTab == 0) {
                            "Official schedule for upcoming academic seminars, workshops, sports week, and institutional ceremonies will be published here."
                        } else {
                            "No archived college activities recorded in this section."
                        },
                        fontSize = 12.sp,
                        color = BrandTextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("events_list_container"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents, key = { it.id ?: it.title }) { event ->
                    PublishedEventCard(
                        event = event,
                        onClick = { viewModel.selectEvent(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SurfaceHeader(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)?,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("events_header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }
            }
            Column(modifier = Modifier.padding(start = if (onBack != null) 4.dp else 8.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BrandTextMuted
                )
            }
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier.testTag("events_refresh_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = BrandNavy
            )
        }
    }
}

@Composable
fun PublishedEventCard(
    event: CollegeEventDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("event_item_${event.id ?: event.title.hashCode()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEEF3FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = event.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = BrandTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.eventDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!event.venue.isNullOrBlank() || !event.eventTime.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!event.venue.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BrandTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = event.venue,
                                fontSize = 11.sp,
                                color = BrandTextMuted
                            )
                        }
                    }

                    if (!event.eventTime.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = BrandTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = event.eventTime,
                                fontSize = 11.sp,
                                color = BrandTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = event.description,
                fontSize = 12.sp,
                color = BrandTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!event.attachmentName.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Attachment,
                            contentDescription = "Attachment",
                            tint = Color(0xFF1B873F),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Attachment available",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B873F)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Event Details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CollegeEventDetailScreen(
    event: CollegeEventDto,
    bannerUrl: String?,
    attachmentUrl: String? = null,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showFullBannerViewer by remember { mutableStateOf(false) }

    if (showFullBannerViewer && !bannerUrl.isNullOrBlank()) {
        ImageViewerDialog(
            imageUrl = bannerUrl,
            title = event.title,
            fileName = "Event_Banner.png",
            onDismiss = { showFullBannerViewer = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("event_detail_screen_container")
    ) {
        // Top Back Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("event_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Events",
                    tint = BrandNavy
                )
            }

            Text(
                text = "Event Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Event Main Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Event Banner Image with Tap to Zoom
                    if (!bannerUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showFullBannerViewer = true }
                                .testTag("event_banner_image_container"),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = bannerUrl,
                                contentDescription = "Event Banner Image",
                                loading = {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = BrandNavy, modifier = Modifier.size(28.dp))
                                    }
                                },
                                error = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = BrandTextMuted,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Tap to view full banner", fontSize = 11.sp, color = BrandTextMuted)
                                    }
                                },
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Tap to Zoom Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "View Banner",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEEF3FF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = event.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Event Schedule & Venue Information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Date: ${event.eventDate}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandNavy
                        )
                    }

                    if (!event.eventTime.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Time: ${event.eventTime}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandNavy
                            )
                        }
                    }

                    if (!event.venue.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Venue: ${event.venue}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Official Program Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.description,
                        fontSize = 13.sp,
                        color = BrandNavy,
                        lineHeight = 20.sp
                    )
                }
            }

            // Attached Documents or Event Flyer Section
            if (!event.attachmentName.isNullOrBlank() || !event.bannerStoragePath.isNullOrBlank()) {
                FileAttachmentCard(
                    fileUrl = attachmentUrl,
                    fileName = event.attachmentName ?: "Event_Flyer.png",
                    title = event.title
                )
            }
        }
    }
}
