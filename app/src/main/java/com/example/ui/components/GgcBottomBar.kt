package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.BottomNavItem

private val BrandNavy = Color(0xFF061B52)
private val InactiveIconColor = Color.White.copy(alpha = 0.70f)
private val BrandCanvasBg = Color(0xFFF6F6F6)

@Composable
fun GgcBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = BottomNavItem.studentItems
) {
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.let {
        if (it == -1) 0 else it
    }

    // Smooth spring animation for the active destination
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "curved_nav_index"
    )

    // Full-width bar container respecting navigation bar insets
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandCanvasBg)
            .navigationBarsPadding()
            .testTag("ggc_bottom_nav_container")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
        ) {
            val totalWidth = maxWidth
            val itemCount = items.size
            val itemWidth = totalWidth / itemCount
            val fabSize = 54.dp

            // Dynamic X position for the floating circular selection bubble
            val fabOffsetX = (itemWidth * animatedIndex) + (itemWidth / 2f) - (fabSize / 2f)

            // 1. Deep Navy Bottom Navigation Bar with Deep, Wide Concave Scoop
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ggc_curved_nav_canvas")
            ) {
                val w = size.width
                val h = size.height
                val topY = 30.dp.toPx() // Baseline top of the Navy bar

                val currentCenterX = (animatedIndex + 0.5f) * (w / itemCount)

                // Deep, wide curved notch geometry with generous white space around the bubble
                val notchHalfWidth = 48.dp.toPx()
                val notchDepth = 40.dp.toPx()

                val path = Path().apply {
                    // Start at top-left screen edge
                    moveTo(0f, topY)

                    val startNotchX = (currentCenterX - notchHalfWidth).coerceAtLeast(0f)
                    val endNotchX = (currentCenterX + notchHalfWidth).coerceAtMost(w)

                    // Line from left edge to start of scoop
                    lineTo(startNotchX, topY)

                    // Symmetrical smooth concave Bézier curve dipping deep under the active circle
                    cubicTo(
                        currentCenterX - notchHalfWidth * 0.58f, topY,
                        currentCenterX - notchHalfWidth * 0.40f, topY + notchDepth,
                        currentCenterX, topY + notchDepth
                    )

                    // Symmetrical smooth concave Bézier curve rising back up to baseline
                    cubicTo(
                        currentCenterX + notchHalfWidth * 0.40f, topY + notchDepth,
                        currentCenterX + notchHalfWidth * 0.58f, topY,
                        endNotchX, topY
                    )

                    // Line to top-right screen edge
                    lineTo(w, topY)

                    // Down right edge to bottom
                    lineTo(w, h)

                    // Bottom edge to bottom-left
                    lineTo(0f, h)

                    // Close back to start
                    close()
                }

                // Ambient drop shadow along the top contour
                drawIntoCanvas { canvas ->
                    val shadowPaint = Paint().apply {
                        color = Color.Black.copy(alpha = 0.12f)
                        asFrameworkPaint().apply {
                            maskFilter = android.graphics.BlurMaskFilter(
                                10.dp.toPx(),
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                    }
                    canvas.drawPath(path, shadowPaint)
                }

                // Draw solid GGC Navy curved navigation bar
                drawPath(
                    path = path,
                    color = BrandNavy
                )
            }

            // 2. Inactive Navigation Icons Row (Positioned comfortably in the navy bar body)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp),
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
                        // Render inactive icon
                        if (!isSelected) {
                            Icon(
                                imageVector = item.unselectedIcon,
                                contentDescription = item.title,
                                tint = InactiveIconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // 3. Floating Active Circular Selection Bubble with generous clear space (moat)
            val selectedItem = items[selectedIndex]

            Box(
                modifier = Modifier
                    .offset(x = fabOffsetX, y = 0.dp)
                    .size(fabSize)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.35f),
                        ambientColor = Color.Black.copy(alpha = 0.20f)
                    )
                    .background(
                        color = BrandNavy,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Current tab active
                    }
                    .testTag("bottom_nav_active_floating_bubble"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = selectedItem.selectedIcon,
                    contentDescription = selectedItem.title,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
