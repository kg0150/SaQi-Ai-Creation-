package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OmniCyan,
    onPrimary = OmniObsidian,
    primaryContainer = OmniSurfaceElevated,
    onPrimaryContainer = OmniCyan,
    secondary = OmniViolet,
    onSecondary = Color.White,
    secondaryContainer = OmniSurfaceCard,
    onSecondaryContainer = OmniViolet,
    tertiary = OmniEmerald,
    onTertiary = OmniObsidian,
    background = OmniObsidian,
    onBackground = OmniTextPrimary,
    surface = OmniSurfaceDark,
    onSurface = OmniTextPrimary,
    surfaceVariant = OmniSurfaceCard,
    onSurfaceVariant = OmniTextSecondary,
    outline = OmniBorder,
    error = OmniRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OmniLightPrimary,
    onPrimary = Color.White,
    primaryContainer = OmniLightSurfaceElevated,
    onPrimaryContainer = OmniLightPrimary,
    secondary = OmniViolet,
    onSecondary = Color.White,
    tertiary = OmniEmerald,
    onTertiary = Color.White,
    background = OmniLightBg,
    onBackground = Color(0xFF0F172A),
    surface = OmniLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = OmniLightSurfaceElevated,
    onSurfaceVariant = Color(0xFF475569),
    outline = OmniLightBorder,
    error = OmniRose,
    onError = Color.White
)

@Composable
fun OmniPlayTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
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

// Backward compatibility alias
@Composable
fun CineCraftTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) = OmniPlayTheme(darkTheme, dynamicColor, content)
