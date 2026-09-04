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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.NavRoutes

// Official GGC Primary Brand Color
private val BrandNavy = Color(0xFF061B52)
// Neutral Gray for unselected items
private val NeutralUnselected = Color(0xFF788292)
// Neutral soft surface for unselected center button
private val NeutralInactiveBg = Color(0xFFF1F4F9)
private val NeutralInactiveBorder = Color(0xFFD6DFEB)

/**
 * Modern floating bottom navigation bar for Govt Graduate College Mandi Bahauddin.
 *
 * Structure:
 * - Clean white floating surface with rounded corners and subtle drop shadow.
 * - Sits comfortably above the bottom edge with horizontal margins.
 * - Exactly 5 destinations:
 *   [Notices] [Academic Departments] [HOME (Center circle)] [Events] [User Profile]
 * - Center Home button is a circular floating button rising slightly above the bar with
 *   an integrated white outer ring collar.
 * - Active Home button is highlighted in official GGC Brand Navy (#061B52) with subtle scaling.
 * - When any other destination is active, Home returns to its inactive appearance while the selected
 *   item highlights in Brand Navy (#061B52).
 */
@Composable
fun GgcBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = BottomNavItem.studentItems
) {
    // Resolve the 5 destinations accurately regardless of list ordering
    val homeItem = items.firstOrNull { it.route == NavRoutes.HOME } ?: BottomNavItem.Home
    val noticesItem = items.firstOrNull { it.route == NavRoutes.NOTICES } ?: BottomNavItem.Notices
    val academicsItem = items.firstOrNull { it.route == NavRoutes.ACADEMICS || it.route == NavRoutes.CONTENT_MANAGEMENT }
        ?: items.firstOrNull { it.route != NavRoutes.HOME && it.route != NavRoutes.NOTICES && it.route != NavRoutes.EVENTS && it.route != NavRoutes.PROFILE }
        ?: BottomNavItem.Academics
    val eventsItem = items.firstOrNull { it.route == NavRoutes.EVENTS } ?: BottomNavItem.Events
    val profileItem = items.firstOrNull { it.route == NavRoutes.PROFILE } ?: BottomNavItem.Profile

    val isHomeSelected = currentRoute == homeItem.route

    // Scale animation for the center circular Home button
    val homeScale by animateFloatAsState(
        targetValue = if (isHomeSelected) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "home_button_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .height(78.dp)
            .testTag("ggc_bottom_nav_container"),
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
                // Left 2 items: Notices, Academic Departments
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        item = noticesItem,
                        isSelected = currentRoute == noticesItem.route,
                        onNavigate = onNavigateToRoute
                    )
                    NavItem(
                        item = academicsItem,
                        isSelected = currentRoute == academicsItem.route,
                        onNavigate = onNavigateToRoute
                    )
                }

                // Dedicated center gap reserved for the circular Home button
                Spacer(modifier = Modifier.width(66.dp))

                // Right 2 items: Events, User Profile
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        item = eventsItem,
                        isSelected = currentRoute == eventsItem.route,
                        onNavigate = onNavigateToRoute
                    )
                    NavItem(
                        item = profileItem,
                        isSelected = currentRoute == profileItem.route,
                        onNavigate = onNavigateToRoute
                    )
                }
            }
        }

        // 2. Center Circular Home Button with integrated outer white ring collar
        // Overlaps and rises above the top edge of the navigation bar
        Box(
            modifier = Modifier
                .size(66.dp)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            // Integrated white outer ring collar matching the bar surface
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
                    // Inner Circular Home Action Button
                    val homeBgColor by animateColorAsState(
                        targetValue = if (isHomeSelected) BrandNavy else NeutralInactiveBg,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "home_bg_color"
                    )

                    val homeIconTint by animateColorAsState(
                        targetValue = if (isHomeSelected) Color.White else BrandNavy.copy(alpha = 0.70f),
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "home_icon_tint"
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
                                if (!isHomeSelected) {
                                    onNavigateToRoute(homeItem.route)
                                }
                            }
                            .testTag(homeItem.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = homeItem.title,
                            tint = homeIconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual navigation item for the 4 flanking destinations (Notices, Academics, Events, Profile).
 */
@Composable
private fun RowScope.NavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onNavigate: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_item_icon_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_item_text_color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 24.dp, color = BrandNavy)
            ) {
                if (!isSelected) {
                    onNavigate(item.route)
                }
            }
            .testTag(item.testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.title,
                color = textColor,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                lineHeight = 13.sp
            )

            // Subtle active dot indicator matching the reference design
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(3.5.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) BrandNavy else Color.Transparent)
            )
        }
    }
}
