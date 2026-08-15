package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.BottomNavItem
import kotlin.math.roundToInt

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFE5A93C)

@Composable
fun CurvedBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Admission,
        BottomNavItem.Alumni,
        BottomNavItem.About
    )

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.let {
        if (it == -1) 0 else it
    }

    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "curved_fab_index"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(84.dp)
            .testTag("curved_bottom_bar_container")
    ) {
        // 1. Curved Background Canvas with dynamic scoop cutout
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("curved_bottom_bar_canvas")
        ) {
            val barHeight = size.height
            val barWidth = size.width
            val itemCount = items.size
            val itemWidth = barWidth / itemCount

            // Calculate center X of the selected curved scoop
            val centerX = (animatedIndex + 0.5f) * itemWidth
            val topY = 24.dp.toPx() // The flat top baseline of the bottom bar
            val cutoutRadius = 34.dp.toPx()
            val curveSpread = 50.dp.toPx()
            val depth = 16.dp.toPx()

            val path = Path().apply {
                moveTo(0f, topY)

                // Left segment before curve scoop
                val startX = centerX - curveSpread
                val endX = centerX + curveSpread

                lineTo(startX, topY)

                // Smooth cubic Bezier down into the scoop
                cubicTo(
                    startX + 16.dp.toPx(), topY,
                    centerX - cutoutRadius, topY + depth,
                    centerX, topY + depth
                )

                // Smooth cubic Bezier back up out of the scoop
                cubicTo(
                    centerX + cutoutRadius, topY + depth,
                    endX - 16.dp.toPx(), topY,
                    endX, topY
                )

                // Right segment after curve scoop
                lineTo(barWidth, topY)
                lineTo(barWidth, barHeight)
                lineTo(0f, barHeight)
                close()
            }

            // Draw shadow/elevation ambient stroke for sleek depth
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.05f)
            )

            // Draw solid brand bar
            drawPath(
                path = path,
                color = BrandNavy
            )
        }

        // 2. Interactive Navigation Items (Icons in the bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (!isSelected) {
                                onNavigateToRoute(item.route)
                            }
                        }
                        .testTag(item.testTag),
                    contentAlignment = Alignment.Center
                ) {
                    // Only display icon if not the active floating item
                    if (!isSelected) {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = item.title,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 3. Elevated Floating Active Bubble / Circle Icon (Round FAB style)
        val itemCount = items.size
        val selectedItem = items[selectedIndex]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        ) {
            // Using BoxWithConstraints or simple layout offset
            androidx.compose.foundation.layout.BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val totalWidthPx = maxWidth
                val singleTabWidth = totalWidthPx / itemCount
                val fabSize = 52.dp
                val fabCenterXPx = (singleTabWidth * animatedIndex) + (singleTabWidth / 2f) - (fabSize / 2f)

                Box(
                    modifier = Modifier
                        .offset(x = fabCenterXPx, y = 4.dp)
                        .size(fabSize)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = BrandGold.copy(alpha = 0.4f),
                            ambientColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(
                            color = BrandNavy,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            // Already selected
                        }
                        .testTag("floating_active_nav_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = selectedItem.selectedIcon,
                        contentDescription = selectedItem.title,
                        tint = BrandGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
