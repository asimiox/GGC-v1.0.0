package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserProfileManager
import com.example.ui.screens.courses.CoursesOutlineScreen

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)

enum class HomeSubScreen {
    NONE,
    FEE_STRUCTURE,
    ANNOUNCEMENT,
    PROSPECTUS,
    COURSES_OUTLINE
}

@Composable
fun HomeScreen(
    onNavigateToPrograms: () -> Unit = {},
    onNavigateToCoursesOutline: () -> Unit = {}
) {
    val userProfile by UserProfileManager.userProfile.collectAsState()
    var activeSubScreen by remember { mutableStateOf(HomeSubScreen.NONE) }
    val scrollState = rememberScrollState()

    if (activeSubScreen != HomeSubScreen.NONE) {
        when (activeSubScreen) {
            HomeSubScreen.FEE_STRUCTURE -> FeeStructureView(onBack = { activeSubScreen = HomeSubScreen.NONE })
            HomeSubScreen.ANNOUNCEMENT -> AnnouncementView(onBack = { activeSubScreen = HomeSubScreen.NONE })
            HomeSubScreen.PROSPECTUS -> ProspectusView(onBack = { activeSubScreen = HomeSubScreen.NONE })
            HomeSubScreen.COURSES_OUTLINE -> CoursesOutlineScreen(onBack = { activeSubScreen = HomeSubScreen.NONE })
            HomeSubScreen.NONE -> {}
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .testTag("home_screen_container")
    ) {
        // 1. Official App Header (Reference Image Screen 8)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                    contentDescription = "GGC Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("home_official_logo")
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "GGC M.B.Din",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Text(
                        text = "Official App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted
                    )
                }
            }

            IconButton(
                onClick = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT },
                modifier = Modifier.testTag("home_notifications_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = BrandNavy,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Personalized Student Bento Card (Solid Navy background)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_user_bento_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = userProfile.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.testTag("home_user_name")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = userProfile.programName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.testTag("home_user_program")
                    )

                    // Semester is shown ONLY for BS students, strictly omitted for Intermediate
                    if (userProfile.programLevel == "BS" && !userProfile.semester.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userProfile.semester ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.70f),
                            modifier = Modifier.testTag("home_user_semester")
                        )
                    }
                }
            }

            // 3. Bento Grid: Exactly 4 Items (Programs, Fee Structure, Announcement, Prospectus)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Item 1: Programs
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Programs",
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    testTag = "bento_btn_programs",
                    onClick = onNavigateToPrograms
                )

                // Item 2: Fee Structure
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Fee Structure",
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    testTag = "bento_btn_fee_structure",
                    onClick = { activeSubScreen = HomeSubScreen.FEE_STRUCTURE }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Item 3: Announcement
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Announcement",
                    icon = Icons.Outlined.Campaign,
                    testTag = "bento_btn_announcement",
                    onClick = { activeSubScreen = HomeSubScreen.ANNOUNCEMENT }
                )

                // Item 4: Prospectus
                BentoActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Prospectus",
                    icon = Icons.Outlined.Description,
                    testTag = "bento_btn_prospectus",
                    onClick = { activeSubScreen = HomeSubScreen.PROSPECTUS }
                )
            }

            // New Bento Card: Courses Outline (Full-width dark navy blue rectangular Bento card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        onNavigateToCoursesOutline()
                        activeSubScreen = HomeSubScreen.COURSES_OUTLINE
                    }
                    .testTag("bento_btn_courses_outline"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Layers,
                                contentDescription = "Courses Outline",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Courses Outline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Semester-wise syllabi & course codes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Courses Outline",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = BrandNavy,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// FEE STRUCTURE VIEW (Reference Image: Screen 11)
// -------------------------------------------------------------
@Composable
private fun FeeStructureView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("fee_structure_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("fee_back_btn")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Fee Structure",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BrandIconBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Fee Structure",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Official fee details will be available here.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = BrandTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Government Subsidized Rates",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All fees are regulated in accordance with the Government of the Punjab Higher Education Department policies and University of the Punjab affiliations.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ANNOUNCEMENT VIEW
// -------------------------------------------------------------
@Composable
private fun AnnouncementView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("announcement_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("announcement_back_btn")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Announcements",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Fall Admissions Open",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Admissions for BS and Intermediate morning/evening programs are currently open. Visit the Admission desk for application submission.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Merit Lists Schedule",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The 1st Merit List will be published on the college notice board and updated in the official app as per the academic calendar.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PROSPECTUS VIEW
// -------------------------------------------------------------
@Composable
private fun ProspectusView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("prospectus_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("prospectus_back_btn")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandNavy
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Prospectus",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Academic Prospectus & Code of Conduct",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Contains complete institutional guidelines, academic regulations, examination criteria, attendance policies, and code of conduct for all enrolled students.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextMuted,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
