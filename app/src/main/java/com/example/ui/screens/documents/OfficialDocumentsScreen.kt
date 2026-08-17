package com.example.ui.screens.documents

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.OfficialDocumentDto
import com.example.ui.theme.GgcGoldTertiary

private val BrandNavy = Color(0xFF061B52)
private val BrandTextMuted = Color(0xFF5A6A85)
private val BrandBackground = Color(0xFFF6F6F6)

@Composable
fun OfficialDocumentsScreen(
    initialCategory: String? = null,
    onBack: (() -> Unit)? = null,
    viewModel: OfficialDocumentsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.setSelectedType(initialCategory)
        }
    }

    val docCategories = listOf("All", "Fee Structure", "Rules", "Academic", "Examinations", "Forms", "Admissions")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("official_documents_screen_container")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("documents_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandNavy
                            )
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Official Documents & Rules",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified College Documents",
                                tint = GgcGoldTertiary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Text(
                            text = "Institutional rules, fee schedules, forms & guidelines",
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.loadPublishedDocuments() },
                    modifier = Modifier.testTag("documents_refresh_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by title, session, or keyword...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BrandTextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("documents_search_input"),
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

            // Category Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(docCategories) { category ->
                    val isSelected = uiState.selectedType.equals(category, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) BrandNavy
                                else Color(0xFFEEF3FF)
                            )
                            .clickable { viewModel.setSelectedType(category) }
                            .padding(horizontal = 13.dp, vertical = 6.dp)
                            .testTag("doc_filter_$category")
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
                    .testTag("documents_loading_indicator"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandNavy)
            }
        } else if (uiState.filteredDocuments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .testTag("documents_empty_state"),
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
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "No matching documents found" else "No published documents in this category",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Official documents uploaded and published by the administration will appear here for student & faculty access.",
                        fontSize = 12.sp,
                        color = BrandTextMuted,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("documents_list_container"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredDocuments, key = { it.id ?: it.fileName }) { doc ->
                    PublishedDocumentCard(
                        doc = doc,
                        onOpen = {
                            val url = viewModel.getDocumentUrl(doc.storagePath)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening document in browser: $url", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PublishedDocumentCard(
    doc: OfficialDocumentDto,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doc_item_${doc.id ?: doc.fileName.hashCode()}"),
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
                    val displayType = when (doc.documentType.lowercase()) {
                        "fee_structure" -> "Fee Schedule"
                        "rules_regulations" -> "Rules & Regulations"
                        "academic_notice" -> "Academic Circular"
                        "examination" -> "Examination Policy"
                        "form" -> "Official Form"
                        "admission" -> "Admission Guide"
                        else -> doc.documentType.replace("_", " ").uppercase()
                    }
                    Text(
                        text = displayType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }

                if (!doc.academicSession.isNullOrBlank()) {
                    Text(
                        text = "Session ${doc.academicSession}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GgcGoldTertiary
                    )
                } else {
                    val dateFormatted = (doc.createdAt ?: "").take(10)
                    if (dateFormatted.isNotBlank()) {
                        Text(
                            text = dateFormatted,
                            fontSize = 11.sp,
                            color = BrandTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Document",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!doc.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = doc.description,
                            fontSize = 12.sp,
                            color = BrandTextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val sizeText = doc.fileSizeBytes?.let {
                        "${it / (1024 * 1024)} MB (${it / 1024} KB)"
                    } ?: "Verified Official PDF"

                    Text(
                        text = "${doc.fileName} • $sizeText",
                        fontSize = 11.sp,
                        color = BrandTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.testTag("btn_download_doc_${doc.id ?: doc.fileName.hashCode()}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View / Download PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
