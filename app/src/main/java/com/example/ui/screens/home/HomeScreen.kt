package com.example.ui.screens.home

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GgcGoldTertiary
import kotlinx.coroutines.delay

data class CoreServiceItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val testTag: String,
    val onClick: () -> Unit
)

data class HeroSlide(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val badge: String
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
    val scrollState = rememberScrollState()

    val heroSlides = listOf(
        HeroSlide(
            imageRes = R.drawable.img_hero_01,
            title = "Main Academic Complex",
            subtitle = "Govt Graduate College Mandi Bahauddin • Est. 1959",
            badge = "MAIN CAMPUS"
        ),
        HeroSlide(
            imageRes = R.drawable.img_hero_02,
            title = "Post Graduate Block",
            subtitle = "Dept. of English Language & Literature • Islamic Studies",
            badge = "POST GRADUATE"
        ),
        HeroSlide(
            imageRes = R.drawable.img_hero_03,
            title = "Historic Campus Courtyard",
            subtitle = "Sprawling Green Quadrangle & Academic Corridors",
            badge = "CAMPUS GROUNDS"
        )
    )

    val pagerState = rememberPagerState(pageCount = { heroSlides.size })

    // Auto-advance hero slides smoothly
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % heroSlides.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    val coreServices = listOf(
        CoreServiceItem(
            title = "College Information",
            subtitle = "History, Administration & Vision",
            icon = Icons.Default.Info,
            testTag = "home_service_college_info",
            onClick = onNavigateToCollege
        ),
        CoreServiceItem(
            title = "Academic Departments",
            subtitle = "Faculties & Department Details",
            icon = Icons.Default.Business,
            testTag = "home_service_departments",
            onClick = onNavigateToDepartments
        ),
        CoreServiceItem(
            title = "Faculty & Staff",
            subtitle = "Academic Directory & Staff Profiles",
            icon = Icons.Default.People,
            testTag = "home_service_faculty",
            onClick = onNavigateToFaculty
        ),
        CoreServiceItem(
            title = "Programs & Courses",
            subtitle = "4-Year BS Honors Degree Programs",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            testTag = "home_service_programs",
            onClick = onNavigateToPrograms
        ),
        CoreServiceItem(
            title = "Events & Activities",
            subtitle = "Academic Seminars, Workshops & Sports",
            icon = Icons.Default.Event,
            testTag = "home_service_events",
            onClick = onNavigateToEvents
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .testTag("home_screen_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Official College Banner Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .testTag("home_hero_slider")
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val slide = heroSlides[page]
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = slide.imageRes),
                        contentDescription = slide.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Institutional Dark Navy Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x66061B52),
                                        Color(0xCC061B52),
                                        Color(0xF0030D2B)
                                    )
                                )
                            )
                    )

                    // Slide Information Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Badge Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GgcGoldTertiary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = slide.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF061B52),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ggc_logo),
                                    contentDescription = "GGC Crest",
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = slide.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Institution",
                                        tint = GgcGoldTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = slide.subtitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Slider Indicator Dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(heroSlides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(if (isSelected) 18.dp else 6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) GgcGoldTertiary else Color.White.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // Primary Sections Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Latest Official Notice Highlight Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToNotices() }
                    .testTag("home_latest_notice_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NewReleases,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Latest Notice & Circulars",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Official Academic & Administrative Announcements",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Notices",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Academic Sections Title
            Text(
                text = "Academic & Institutional Services",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Primary Services Column
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                coreServices.forEach { service ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { service.onClick() }
                            .testTag(service.testTag),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = service.icon,
                                    contentDescription = service.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = service.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
