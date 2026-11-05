package com.example.servify.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = ServifyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = ServifyCianSoft,
    onPrimaryContainer = ServifyDarkNavy,
    secondary = ServifyTealAccent,
    background = ServifyBackground,
    surface = ServifySurface,
    onBackground = ServifyTextTitle,
    onSurface = ServifyTextTitle,
    outline = ServifyBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = ServifyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = ServifyDarkNavy,
    onPrimaryContainer = ServifyCianSoft,
    secondary = ServifyTealAccent,
    background = Color(0xFF021024),
    surface = Color(0xFF052659),
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF548383)
)

@Composable
fun ServifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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