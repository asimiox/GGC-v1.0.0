package com.example.ui.screens.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

private val BrandNavy = Color(0xFF061B52)
private val BrandGold = Color(0xFFC5A059)
private val BrandBackground = Color(0xFFF6F6F6)
private val BrandTextMuted = Color(0xFF7A879D)

private enum class SplashStep {
    BISMILLAH,
    OFFICIAL_BRAND
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var currentStep by remember { mutableStateOf(SplashStep.BISMILLAH) }

    LaunchedEffect(Unit) {
        // Step 1: In the name of Almighty Allah (850ms)
        delay(850)
        currentStep = SplashStep.OFFICIAL_BRAND
        // Step 2: Official GGC Logo & App Identity (1400ms)
        delay(1400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing))
                    .togetherWith(
                        fadeOut(animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing))
                    )
            },
            label = "splash_sequence_transition"
        ) { step ->
            when (step) {
                SplashStep.BISMILLAH -> {
                    BismillahSplashView()
                }
                SplashStep.OFFICIAL_BRAND -> {
                    BrandSplashView()
                }
            }
        }
    }
}

@Composable
private fun BismillahSplashView() {
    var animateIn by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "bismillah_alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.94f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "bismillah_scale"
    )

    LaunchedEffect(Unit) {
        animateIn = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .alpha(alphaAnim)
            .scale(scaleAnim)
    ) {
        // Subtle ornamental gold accent circle
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(BrandGold)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Arabic Calligraphy Typography
        Text(
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Serif,
            color = BrandNavy,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // English translation
        Text(
            text = "In the name of Almighty Allah",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Subtle divider
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(1.5.dp)
                .background(BrandGold.copy(alpha = 0.6f))
        )
    }
}

@Composable
private fun BrandSplashView() {
    var animateIn by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "brand_alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.92f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "brand_scale"
    )

    LaunchedEffect(Unit) {
        animateIn = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .alpha(alphaAnim)
            .scale(scaleAnim)
    ) {
        // Large official GGC App Logo
        Image(
            painter = painterResource(id = R.drawable.ic_ggc_logo),
            contentDescription = "GGC M.B.Din Official Logo",
            modifier = Modifier
                .size(165.dp)
                .testTag("splash_official_logo")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App Name
        Text(
            text = "GGC M.B.Din",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            textAlign = TextAlign.Center,
            letterSpacing = 0.3.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // App Identity Subtitle
        Text(
            text = "Official App",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = BrandTextMuted,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Official College Designation
        Text(
            text = "Govt Graduate College Mandi Bahauddin",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = BrandTextMuted.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}
