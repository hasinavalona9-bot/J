package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = HayGoldSecondary,
    onPrimary = HayNavyDark,
    primaryContainer = HayNavySurface,
    onPrimaryContainer = HayGoldLight,
    secondary = HayGoldLight,
    onSecondary = HayNavyDark,
    background = HayNavyDark,
    onBackground = Color.White,
    surface = HayNavyPrimary,
    onSurface = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HayNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3EAFF),
    onPrimaryContainer = HayNavyDark,
    secondary = HayGoldSecondary,
    onSecondary = HayNavyDark,
    background = HayBackgroundLight,
    onBackground = HayNavyDark,
    surface = HaySurfaceLight,
    onSurface = HayNavyDark,
  )

@Composable
fun HayInfoTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
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

