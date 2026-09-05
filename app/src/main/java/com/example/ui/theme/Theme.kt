package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
  lightColorScheme(
    primary = GgcNavyPrimary,
    onPrimary = GgcSurfaceLight,
    primaryContainer = GgcNavyContainer,
    onPrimaryContainer = GgcNavyOnContainer,
    secondary = GgcNavyPrimary,
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

/**
 * Universal College Theme: Strictly locked to Light Mode across all devices and OS settings.
 * Dark Mode is completely disabled to ensure consistent institutional branding and legibility.
 */
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun GgcTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) = MyApplicationTheme(
  darkTheme = false,
  dynamicColor = false,
  content = content
)


