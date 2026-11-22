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


@Composable
fun ServifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val lightColorScheme = lightColorScheme(
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

    val darkColorScheme = darkColorScheme(
        primary = ServifyBlueLight,
        onPrimary = ServifyDarkNavy,
        primaryContainer = ServifyBluePrimary,
        onPrimaryContainer = ServifyCianSoft,
        secondary = ServifyTealAccent,
        background = Color(0xFF021024),
        surface = Color(0xFF071A38),
        onBackground = Color(0xFFF1F5F9),
        onSurface = Color(0xFFF1F5F9),
        outline = Color(0xFF1E3A66)
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}