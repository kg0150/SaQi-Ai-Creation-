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

private val DarkColorScheme = darkColorScheme(
    primary = CineGoldPrimary,
    onPrimary = CineObsidian,
    primaryContainer = CineSurfaceElevated,
    onPrimaryContainer = CineGoldSecondary,
    secondary = CineCyan,
    onSecondary = CineObsidian,
    secondaryContainer = CineSurfaceCard,
    onSecondaryContainer = CineCyan,
    tertiary = CineElectricViolet,
    onTertiary = Color.White,
    background = CineObsidian,
    onBackground = CineTextPrimary,
    surface = CineSurfaceDark,
    onSurface = CineTextPrimary,
    surfaceVariant = CineSurfaceCard,
    onSurfaceVariant = CineTextSecondary,
    outline = CineBorder,
    error = CineNeonRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CineLightPrimary,
    onPrimary = Color.White,
    primaryContainer = CineLightSurfaceElevated,
    onPrimaryContainer = CineLightPrimary,
    secondary = CineCyan,
    onSecondary = Color.White,
    tertiary = CineGoldDark,
    onTertiary = Color.White,
    background = CineLightBg,
    onBackground = Color(0xFF0F172A),
    surface = CineLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = CineLightSurfaceElevated,
    onSurfaceVariant = Color(0xFF475569),
    outline = CineLightBorder,
    error = CineNeonRose,
    onError = Color.White
)

@Composable
fun CineCraftTheme(
    darkTheme: Boolean = true, // Default to dark cinema aesthetic
    dynamicColor: Boolean = false, // Keep signature cinematic styling consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
