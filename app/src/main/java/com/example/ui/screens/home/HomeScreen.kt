package com.example.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GgcGoldTertiary

data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tag: String,
    val onClick: () -> Unit
)

data class NoticeHighlight(
    val id: String,
    val title: String,
    val date: String,
    val category: String,
    val isUrgent: Boolean = false
)

@Composable
fun HomeScreen(
    onNavigateToAcademics: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToCollege: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDepartments: () -> Unit = onNavigateToAcademics,
    onNavigateToFaculty: () -> Unit = {},
    onNavigateToPrograms: () -> Unit = onNavigateToAcademics,
    onNavigateToEvents: () -> Unit = {},
    onNavigateToStudentSection: () -> Unit = onNavigateToProfile,
    onNavigateToResources: () -> Unit = onNavigateToAcademics
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val featuredNotices = listOf(
        NoticeHighlight(
            id = "1",
            title = "Admissions Open for BS 4-Year Programs (Session 2026-2030)",
            date = "10 Aug 2026",
            category = "Admissions",
            isUrgent = true
        ),
        NoticeHighlight(
            id = "2",
            title = "BS Spring Semester Mid-Term Examination Datesheet",
            date = "08 Aug 2026",
            category = "Exams",
            isUrgent = false
        ),
        NoticeHighlight(
            id = "3",
            title = "Annual College Sports & Cultural Week Announcement",
            date = "05 Aug 2026",
            category = "Events",
            isUrgent = false
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .testTag("home_screen_container")
    ) {
        // Hero Banner Box with Campus Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ggc_banner),
                contentDescription = "GGC M.B.DIN Campus",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark Overlay for Readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ggc_logo),
                            contentDescription = "GGC Crest",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Govt Graduate College",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Official",
                                tint = GgcGoldTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Mandi Bahauddin • Est. 1959",
                            color = GgcGoldTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Affiliated with University of the Punjab • Punjab Higher Education",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Official Announcements Ticker / Highlight Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onNavigateToNotices() }
                .testTag("notice_ticker_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Latest Official Announcement",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Admissions Open for BS 4-Year Programs (2026-2030)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View All",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Major College Sections Grid (8 Core Areas)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "College Sections & Services",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            val coreSectionsRow1 = listOf(
                QuickActionItem(
                    title = "College Info",
                    subtitle = "History & Campus",
                    icon = Icons.Default.School,
                    tag = "qa_college_info",
                    onClick = onNavigateToCollege
                ),
                QuickActionItem(
                    title = "Departments",
                    subtitle = "BS Faculties",
                    icon = Icons.Default.Business,
                    tag = "qa_departments",
                    onClick = onNavigateToDepartments
                ),
                QuickActionItem(
                    title = "Faculty",
                    subtitle = "Staff Directory",
                    icon = Icons.Default.People,
                    tag = "qa_faculty",
                    onClick = onNavigateToFaculty
                ),
                QuickActionItem(
                    title = "Programs",
                    subtitle = "BS Honors",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    tag = "qa_programs",
                    onClick = onNavigateToPrograms
                )
            )

            val coreSectionsRow2 = listOf(
                QuickActionItem(
                    title = "Notices",
                    subtitle = "Circulars & Exams",
                    icon = Icons.AutoMirrored.Filled.Announcement,
                    tag = "qa_notices",
                    onClick = onNavigateToNotices
                ),
                QuickActionItem(
                    title = "Events",
                    subtitle = "Sports & Seminars",
                    icon = Icons.Default.Event,
                    tag = "qa_events",
                    onClick = onNavigateToEvents
                ),
                QuickActionItem(
                    title = "Student Section",
                    subtitle = "Portal & Rules",
                    icon = Icons.Default.AccountCircle,
                    tag = "qa_student_section",
                    onClick = onNavigateToStudentSection
                ),
                QuickActionItem(
                    title = "Resources",
                    subtitle = "Notes & Papers",
                    icon = Icons.Default.FolderSpecial,
                    tag = "qa_resources",
                    onClick = onNavigateToResources
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                coreSectionsRow1.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                coreSectionsRow2.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Utility Shortcut Row (Timetable, GPA Calculator, Web Portal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    action = QuickActionItem(
                        title = "Timetable",
                        subtitle = "Class Schedule",
                        icon = Icons.Default.DateRange,
                        tag = "qa_timetable",
                        onClick = onNavigateToAcademics
                    ),
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    action = QuickActionItem(
                        title = "GPA Calc",
                        subtitle = "CGPA Tool",
                        icon = Icons.Default.Calculate,
                        tag = "qa_gpa_calc",
                        onClick = onNavigateToProfile
                    ),
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    action = QuickActionItem(
                        title = "Web Portal",
                        subtitle = "ggcmbdin.edu.pk",
                        icon = Icons.Default.Language,
                        tag = "qa_website",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ggcmbdin.edu.pk/"))
                            context.startActivity(intent)
                        }
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Notices Horizontal Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToNotices() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredNotices) { notice ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .clickable { onNavigateToNotices() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (notice.isUrgent) Color(0xFFDC2626)
                                            else MaterialTheme.colorScheme.secondaryContainer
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = notice.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (notice.isUrgent) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }

                                Text(
                                    text = notice.date,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = notice.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Principal Message Highlight Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onNavigateToCollege() }
                .testTag("principal_message_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Principal Message",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Principal's Welcome",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Government Graduate College Mandi Bahauddin",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"Welcome to GGC M.B.DIN. Our institution stands dedicated to academic excellence, character building, and empowering youth with contemporary higher education since 1959.\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Read Full College History & Vision →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // College Stats Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(number = "1959", label = "Established")
                StatItem(number = "12+", label = "BS Programs")
                StatItem(number = "100+", label = "Faculty")
                StatItem(number = "5000+", label = "Students")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickActionCard(
    action: QuickActionItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { action.onClick() }
            .testTag(action.tag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = action.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = action.subtitle,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
        )
    }
}

