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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.example.ui.screens.admin.AdminNavSection

private val BrandNavy = Color(0xFF061B52)
private val BrandGoldLight = Color(0xFFE5C058)
private val NeutralUnselected = Color(0xFF788292)
private val NeutralInactiveBg = Color(0xFFF1F4F9)
private val NeutralInactiveBorder = Color(0xFFD6DFEB)

/**
 * Modern icon-only navigation bar for the Administrator Control Center.
 * Seamless transparent layout without the bulky white plate background.
 *
 * 5 dedicated destinations:
 * - Notices / Circulars (AdminNavSection.CONTENT)
 * - Course Outlines / Academics (AdminNavSection.ACADEMICS)
 * - Center: Admin Dashboard Hub (AdminNavSection.DASHBOARD)
 * - College Events & Seminars (AdminNavSection.EVENTS)
 * - Official Documents & Prospectus (AdminNavSection.DOCUMENTS)
 */
@Composable
fun GgcAdminBottomBar(
    activeSection: AdminNavSection,
    onSelectSection: (AdminNavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHomeSelected = activeSection == AdminNavSection.DASHBOARD

    val homeScale by animateFloatAsState(
        targetValue = if (isHomeSelected) 1.08f else 1.0f,
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(56.dp)
            .testTag("ggc_admin_bottom_nav_container"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Notices / Content
            AdminNavIconItem(
                title = "Notices",
                selectedIcon = Icons.Filled.Campaign,
                unselectedIcon = Icons.Outlined.Campaign,
                isSelected = activeSection == AdminNavSection.CONTENT,
                testTag = "admin_nav_notices",
                onClick = { onSelectSection(AdminNavSection.CONTENT) }
            )

            // 2. Outlines / Academics
            AdminNavIconItem(
                title = "Outlines",
                selectedIcon = Icons.Filled.AutoStories,
                unselectedIcon = Icons.Outlined.AutoStories,
                isSelected = activeSection == AdminNavSection.ACADEMICS,
                testTag = "admin_nav_outlines",
                onClick = { onSelectSection(AdminNavSection.ACADEMICS) }
            )

            // 3. Center Circular Admin Dashboard Button
            val homeBgColor by animateColorAsState(
                targetValue = if (isHomeSelected) BrandNavy else NeutralInactiveBg,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "admin_home_bg"
            )
            val homeIconTint by animateColorAsState(
                targetValue = if (isHomeSelected) BrandGoldLight else BrandNavy,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "admin_home_tint"
            )
            val homeBorder = if (isHomeSelected) null else BorderStroke(1.dp, NeutralInactiveBorder)

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .scale(homeScale)
                    .shadow(
                        elevation = if (isHomeSelected) 4.dp else 1.dp,
                        shape = CircleShape,
                        spotColor = if (isHomeSelected) BrandNavy.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.08f)
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
                        indication = ripple(bounded = true, color = if (isHomeSelected) BrandGoldLight else BrandNavy)
                    ) {
                        if (!isHomeSelected) {
                            onSelectSection(AdminNavSection.DASHBOARD)
                        }
                    }
                    .testTag("admin_nav_home"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isHomeSelected) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                    contentDescription = "Admin Dashboard",
                    tint = homeIconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Events
            AdminNavIconItem(
                title = "Events",
                selectedIcon = Icons.Filled.Event,
                unselectedIcon = Icons.Outlined.Event,
                isSelected = activeSection == AdminNavSection.EVENTS,
                testTag = "admin_nav_events",
                onClick = { onSelectSection(AdminNavSection.EVENTS) }
            )

            // 5. Official Documents
            AdminNavIconItem(
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

/**
 * Individual icon-only navigation item with 48dp minimum touch target.
 */
@Composable
private fun AdminNavIconItem(
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "admin_nav_icon_color"
    )

    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "admin_nav_item_scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(itemScale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = BrandNavy)
            ) {
                if (!isSelected) {
                    onClick()
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandNavy.copy(alpha = 0.10f))
            )
        }

        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
