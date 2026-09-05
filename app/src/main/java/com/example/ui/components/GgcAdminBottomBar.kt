package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.admin.AdminNavSection

private val BrandNavy = Color(0xFF061B52)
private val BrandGoldLight = Color(0xFFE5C058)
private val NeutralUnselected = Color(0xFF788292)
private val NeutralInactiveBg = Color(0xFFF1F4F9)
private val NeutralInactiveBorder = Color(0xFFD6DFEB)

/**
 * Modern floating bottom navigation bar for the Administrator Control Center.
 * Mirrors the Student Dashboard navigation bar structure with 5 dedicated destinations:
 * - Left 1: Notices / Circulars (AdminNavSection.CONTENT)
 * - Left 2: Course Outlines / Academics (AdminNavSection.ACADEMICS)
 * - Center: Admin Dashboard Hub (AdminNavSection.DASHBOARD) - Elevated Circular Button
 * - Right 1: College Events & Seminars (AdminNavSection.EVENTS)
 * - Right 2: Official Documents & Prospectus (AdminNavSection.DOCUMENTS)
 */
@Composable
fun GgcAdminBottomBar(
    activeSection: AdminNavSection,
    onSelectSection: (AdminNavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHomeSelected = activeSection == AdminNavSection.DASHBOARD

    val homeScale by animateFloatAsState(
        targetValue = if (isHomeSelected) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "admin_home_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .height(78.dp)
            .testTag("ggc_admin_bottom_nav_container"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Floating White Main Bar Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.14f),
                    ambientColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 2 items: Notices, Course Outlines
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminNavItem(
                        title = "Notices",
                        selectedIcon = Icons.Filled.Campaign,
                        unselectedIcon = Icons.Outlined.Campaign,
                        isSelected = activeSection == AdminNavSection.CONTENT,
                        testTag = "admin_nav_notices",
                        onClick = { onSelectSection(AdminNavSection.CONTENT) }
                    )
                    AdminNavItem(
                        title = "Outlines",
                        selectedIcon = Icons.Filled.AutoStories,
                        unselectedIcon = Icons.Outlined.AutoStories,
                        isSelected = activeSection == AdminNavSection.ACADEMICS,
                        testTag = "admin_nav_outlines",
                        onClick = { onSelectSection(AdminNavSection.ACADEMICS) }
                    )
                }

                // Dedicated center gap reserved for circular Admin Home button
                Spacer(modifier = Modifier.width(66.dp))

                // Right 2 items: Events, Official Documents
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminNavItem(
                        title = "Events",
                        selectedIcon = Icons.Filled.Event,
                        unselectedIcon = Icons.Outlined.Event,
                        isSelected = activeSection == AdminNavSection.EVENTS,
                        testTag = "admin_nav_events",
                        onClick = { onSelectSection(AdminNavSection.EVENTS) }
                    )
                    AdminNavItem(
                        title = "Docs",
                        selectedIcon = Icons.Filled.Description,
                        unselectedIcon = Icons.Outlined.Description,
                        isSelected = activeSection == AdminNavSection.DOCUMENTS,
                        testTag = "admin_nav_docs",
                        onClick = { onSelectSection(AdminNavSection.DOCUMENTS) }
                    )
                }
            }
        }

        // 2. Center Circular Admin Dashboard Button with integrated outer white ring collar
        Box(
            modifier = Modifier
                .size(66.dp)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(66.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.12f),
                        ambientColor = Color.Black.copy(alpha = 0.06f)
                    ),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val homeBgColor by animateColorAsState(
                        targetValue = if (isHomeSelected) BrandNavy else NeutralInactiveBg,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "admin_home_bg"
                    )

                    val homeIconTint by animateColorAsState(
                        targetValue = if (isHomeSelected) BrandGoldLight else BrandNavy.copy(alpha = 0.70f),
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "admin_home_tint"
                    )

                    val homeBorder = if (isHomeSelected) {
                        null
                    } else {
                        BorderStroke(1.dp, NeutralInactiveBorder)
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(homeScale)
                            .shadow(
                                elevation = if (isHomeSelected) 6.dp else 1.dp,
                                shape = CircleShape,
                                spotColor = if (isHomeSelected) BrandNavy.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.06f)
                            )
                            .clip(CircleShape)
                            .background(homeBgColor)
                            .then(
                                if (homeBorder != null) {
                                    Modifier.border(homeBorder.width, homeBorder.brush, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = true,
                                    color = if (isHomeSelected) Color.White else BrandNavy
                                )
                            ) {
                                onSelectSection(AdminNavSection.DASHBOARD)
                            }
                            .testTag("admin_nav_home_fab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHomeSelected) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Control Center",
                            tint = homeIconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.AdminNavItem(
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "admin_icon_tint"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "admin_text_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "admin_item_scale"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = BrandNavy.copy(alpha = 0.12f))
            ) {
                onClick()
            }
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier
                .size(23.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            maxLines = 1
        )
    }
}
