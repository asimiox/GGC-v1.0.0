package com.example.ui.screens.faculty

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.datasource.OfficialFacultyData
import com.example.data.model.FacultyMember

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)
private val BrandLeadershipBg = Color(0xFF061B52)
private val BrandLeadershipBadgeBg = Color(0xFFE8EDFA)
private val BrandLeadershipText = Color(0xFF061B52)

@Composable
fun FacultyTabScreen(
    onBack: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All") }
    var staffTypeFilter by remember { mutableStateOf(0) } // 0: All, 1: Teaching Faculty, 2: Administrative Staff

    val allDepartments = remember {
        listOf("All") + OfficialFacultyData.getAllDepartments()
    }

    val filteredFaculty = remember(searchQuery, selectedDepartment, staffTypeFilter) {
        val baseList = if (selectedDepartment == "All") {
            OfficialFacultyData.getAllFaculty()
        } else {
            OfficialFacultyData.getFacultyByDepartment(selectedDepartment)
        }

        val typeFiltered = when (staffTypeFilter) {
            1 -> baseList.filter { !it.isStaff }
            2 -> baseList.filter { it.isStaff }
            else -> baseList
        }

        if (searchQuery.isBlank()) {
            typeFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            typeFiltered.filter { member ->
                member.name.lowercase().contains(q) ||
                        member.designation.lowercase().contains(q) ||
                        member.qualification.lowercase().contains(q) ||
                        member.department.lowercase().contains(q) ||
                        member.email.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("faculty_directory_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("faculty_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandNavy
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Faculty & Staff Directory",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Govt Graduate College Mandi Bahauddin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted
                )
            }

            // Count Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandIconBadgeBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${filteredFaculty.size} Members",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        // Type Switcher (All / Teaching Faculty / Administrative Staff)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val typeTabs = listOf("All (46)", "Teaching (41)", "Staff (5)")
            typeTabs.forEachIndexed { index, title ->
                val isSelected = staffTypeFilter == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandNavy else Color(0xFFF0F3F8))
                        .clickable { staffTypeFilter = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else BrandNavy
                    )
                }
            }
        }

        // Search Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search by name, designation, department...",
                        fontSize = 13.sp,
                        color = BrandTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = BrandTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandNavy,
                    unfocusedBorderColor = Color(0xFFE2E6EC),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = BrandNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("faculty_search_input")
            )
        }

        // Horizontal Department Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allDepartments) { dept ->
                val isSelected = selectedDepartment == dept
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) BrandNavy else Color.White)
                        .clickable { selectedDepartment = dept }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("dept_filter_$dept"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dept,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else BrandNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Faculty Items List
        if (filteredFaculty.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(BrandIconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No Members Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "No faculty or staff matched your search query. Try clearing filters.",
                            fontSize = 13.sp,
                            color = BrandTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = filteredFaculty,
                    key = { it.id }
                ) { faculty ->
                    FacultyMemberCard(faculty = faculty)
                }
            }
        }
    }
}

@Composable
fun FacultyMemberCard(
    faculty: FacultyMember,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("faculty_card_${faculty.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconVector: ImageVector = when {
                faculty.isPrincipal -> Icons.Default.AccountBalance
                faculty.isVicePrincipal -> Icons.Default.MilitaryTech
                faculty.isHod -> Icons.Default.Verified
                else -> Icons.Default.Person
            }

            val iconBoxSize = if (faculty.isLeadership) 52.dp else 46.dp
            val iconSize = if (faculty.isLeadership) 24.dp else 20.dp
            val iconBg = if (faculty.isLeadership) BrandLeadershipBadgeBg else BrandIconBadgeBg

            Box(
                modifier = Modifier
                    .size(iconBoxSize)
                    .clip(CircleShape)
                    .background(iconBg)
                    .border(1.dp, BrandNavy.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (faculty.imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(faculty.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = faculty.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        loading = {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = BrandNavy.copy(alpha = 0.5f),
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        error = {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = BrandNavy,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name & Staff/Leadership Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = faculty.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        modifier = Modifier.weight(1f)
                    )

                    if (faculty.isStaff) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Staff",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    } else if (faculty.isLeadership) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandLeadershipBadgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (faculty.isPrincipal) "Principal" else if (faculty.isVicePrincipal) "Vice Principal" else "HOD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandLeadershipText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Designation
                Text(
                    text = faculty.designation,
                    fontSize = 13.sp,
                    fontWeight = if (faculty.isLeadership) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (faculty.isLeadership) BrandNavy else Color(0xFF2B3A55)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Qualification
                Text(
                    text = faculty.qualification,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = BrandTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Department Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF0F3F8))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (faculty.isStaff) "Administrative & Office Staff" else "Department of ${faculty.department}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandTextMuted
                        )
                    }

                    if (faculty.email.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandIconBadgeBg)
                                .clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:${faculty.email}")
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, faculty.email, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = BrandNavy,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Email",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }
        }
    }
}
