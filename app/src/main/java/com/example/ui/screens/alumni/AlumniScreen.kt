package com.example.ui.screens.alumni

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.datasource.OfficialAlumniData
import com.example.data.model.AlumniMember

private val BrandNavy = Color(0xFF061B52)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)
private val BrandTextDark = Color(0xFF2B3A55)
private val BrandIconBadgeBg = Color(0xFFEEF3FF)
private val BrandQuoteBg = Color(0xFFF8FAFD)

@Composable
fun AlumniScreen() {
    val alumniList = remember { OfficialAlumniData.getAllAlumni() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("alumni_screen_container")
    ) {
        // Screen Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ggc_logo),
                contentDescription = "GGC Logo",
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alumni",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
                Text(
                    text = "Government Graduate College Mandi Bahauddin",
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
                    text = "${alumniList.size} Alumni",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            }
        }

        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

        // Vertical Alumni List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("alumni_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(alumniList, key = { it.id }) { alumni ->
                AlumniCard(alumni = alumni)
            }
        }
    }
}

@Composable
fun AlumniCard(
    alumni: AlumniMember,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alumni_card_${alumni.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Avatar & Name/Position
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BrandIconBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = alumni.name,
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alumni.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = alumni.position,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Details section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9FAFB))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Organization
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Organization",
                        tint = BrandNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Organization: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandTextDark
                    )
                    Text(
                        text = alumni.organization,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextDark
                    )
                }

                // Education
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Education",
                        tint = BrandNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Education: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandTextDark
                    )
                    Text(
                        text = alumni.education,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTextDark
                    )
                }
            }

            // Testimonial Section (only when testimonial is provided)
            if (!alumni.testimonial.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                // Toggle Button for Testimonial
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                        .testTag("alumni_testimonial_toggle_${alumni.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = BrandNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExpanded) "Hide Testimonial" else "Read Testimonial",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandNavy
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = BrandNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(animationSpec = tween(200)) + expandVertically(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(150))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandQuoteBg)
                            .padding(14.dp)
                            .testTag("alumni_testimonial_content_${alumni.id}")
                    ) {
                        Text(
                            text = "\"${alumni.testimonial}\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = BrandTextDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
