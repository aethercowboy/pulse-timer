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

enum class AppThemeMode {
  SYSTEM,
  GYM_DARK,
  LIGHT
}

private val GymDarkColorScheme = darkColorScheme(
  primary = NeonWorkGreen,
  onPrimary = GymBlack,
  primaryContainer = NeonWorkGreenDark,
  onPrimaryContainer = GymTextPrimary,
  secondary = NeonRestCyan,
  onSecondary = GymBlack,
  secondaryContainer = NeonRestCyanDark,
  onSecondaryContainer = GymTextPrimary,
  tertiary = NeonWarmupAmber,
  onTertiary = GymBlack,
  background = GymBlack,
  onBackground = GymTextPrimary,
  surface = GymDarkSurface,
  onSurface = GymTextPrimary,
  surfaceVariant = GymDarkSurfaceVariant,
  onSurfaceVariant = GymTextSecondary,
  outline = GymDarkBorder,
  surfaceContainer = GymDarkCard
)

private val GymLightColorScheme = lightColorScheme(
  primary = GymLightPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFBAE6FD),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = GymLightAccent,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFA7F3D0),
  onSecondaryContainer = Color(0xFF065F46),
  tertiary = NeonWarmupAmber,
  onTertiary = Color.White,
  background = GymLightBackground,
  onBackground = GymLightTextPrimary,
  surface = GymLightSurface,
  onSurface = GymLightTextPrimary,
  surfaceVariant = GymLightSurfaceVariant,
  onSurfaceVariant = GymLightTextSecondary,
  outline = Color(0xFFCBD5E1)
)

@Composable
fun PulseTimerTheme(
  themeMode: AppThemeMode = AppThemeMode.GYM_DARK,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val isDark = when (themeMode) {
    AppThemeMode.GYM_DARK -> true
    AppThemeMode.LIGHT -> false
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
  }

  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    isDark -> GymDarkColorScheme
    else -> GymLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
