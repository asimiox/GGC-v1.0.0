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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
 * Modern icon-only bottom navigation bar for Govt Graduate College Mandi Bahauddin.
 *
 * Design:
 * - Transparent / seamless container without any bulky "white plate" background.
 * - Pure icon navigation (no text labels cluttering the bar).
 * - 5 evenly spaced destinations:
 *   [Notices] [Academics/Content] [Center: HOME] [Events] [Profile]
 * - Standard accessible touch targets (48dp x 48dp) with smooth active indicators.
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

    val homeScale by animateFloatAsState(
        targetValue = if (isHomeSelected) 1.08f else 1.0f,
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(56.dp)
            .testTag("ggc_bottom_nav_container"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Notices
            NavIconItem(
                item = noticesItem,
                isSelected = currentRoute == noticesItem.route,
                onNavigate = onNavigateToRoute
            )

            // 2. Academics / Content Hub
            NavIconItem(
                item = academicsItem,
                isSelected = currentRoute == academicsItem.route,
                onNavigate = onNavigateToRoute
            )

            // 3. Center Circular Home Button (no outer white collar plate)
            val homeBgColor by animateColorAsState(
                targetValue = if (isHomeSelected) BrandNavy else NeutralInactiveBg,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "home_bg_color"
            )
            val homeIconTint by animateColorAsState(
                targetValue = if (isHomeSelected) Color.White else BrandNavy,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "home_icon_tint"
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
                        indication = ripple(bounded = true, color = if (isHomeSelected) Color.White else BrandNavy)
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
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Events
            NavIconItem(
                item = eventsItem,
                isSelected = currentRoute == eventsItem.route,
                onNavigate = onNavigateToRoute
            )

            // 5. User Profile
            NavIconItem(
                item = profileItem,
                isSelected = currentRoute == profileItem.route,
                onNavigate = onNavigateToRoute
            )
        }
    }
}

/**
 * Individual icon-only navigation item with 48dp minimum touch target and smooth active indicator.
 */
@Composable
private fun NavIconItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onNavigate: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) BrandNavy else NeutralUnselected,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_icon_color"
    )

    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "nav_item_scale"
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
                    onNavigate(item.route)
                }
            }
            .testTag(item.testTag),
        contentAlignment = Alignment.Center
    ) {
        // Subtle circular highlight for active tab
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandNavy.copy(alpha = 0.10f))
            )
        }

        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
