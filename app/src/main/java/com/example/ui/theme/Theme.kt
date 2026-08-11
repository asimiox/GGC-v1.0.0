package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GgcGreenPrimaryDarkTheme,
    onPrimary = GgcGreenPrimaryDark,
    primaryContainer = GgcGreenContainerDarkTheme,
    secondary = GgcNavySecondaryDarkTheme,
    tertiary = GgcGoldTertiary,
    background = GgcBackgroundDark,
    surface = GgcSurfaceDark,
    onBackground = GgcOnBackgroundDark,
    onSurface = GgcOnBackgroundDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GgcGreenPrimary,
    onPrimary = GgcSurfaceLight,
    primaryContainer = GgcGreenContainer,
    onPrimaryContainer = GgcGreenOnContainer,
    secondary = GgcNavySecondary,
    secondaryContainer = GgcNavyContainer,
    onSecondaryContainer = GgcNavyOnContainer,
    tertiary = GgcGoldTertiary,
    tertiaryContainer = GgcGoldContainer,
    onTertiaryContainer = GgcGoldOnContainer,
    background = GgcBackgroundLight,
    surface = GgcSurfaceLight,
    surfaceVariant = GgcSurfaceVariantLight,
    onBackground = GgcOnBackgroundLight,
    onSurface = GgcOnSurfaceLight,
    outline = GgcOutlineLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to strictly preserve official GGC branding colors
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
