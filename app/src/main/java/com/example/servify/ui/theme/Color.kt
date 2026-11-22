package com.example.servify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Paleta Corporativa Principal (Azules y Celestes) ---
val ServifyDarkNavy = Color(0xFF052659)       // Azul marino profundo
val ServifyBluePrimary = Color(0xFF052659)    // Azul principal
val ServifyTealAccent = Color(0xFF548383)     // Acento teal
val ServifyBlueLight = Color(0xFF7DA0CA)      // Azul claro (ideal para textos/íconos en oscuro)
val ServifyCianSoft = Color(0xFFC1E8FF)       // Cian suave

// --- Superficies y Fondos (Adaptables a Claro / Oscuro) ---
val ServifyBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF021024) else Color(0xFFEBF8FC)

val ServifySurface: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF071A38) else Color(0xFFFFFFFF)

val ServifyInputBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0B254E) else Color(0xFFF0F4F8)

val ServifyBorder: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E3A66) else Color(0xFFCBD5E1)

// --- Tipografía y Textos (Adaptables a Claro / Oscuro) ---
val ServifyTextTitle: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF1F5F9) else Color(0xFF021024)

val ServifyTextBody: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFCBD5E1) else Color(0xFF052659)

val ServifyTextMuted: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF475569)

val ServifyTextPlaceholder: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF7DA0CA)

// --- Estados y Tags (Badges) ---
val ServifyPendingBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF422006) else Color(0xFFFEF3C7)

val ServifyPendingText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFD97706)

val ServifyStarYellow = Color(0xFFF59E0B)     // Estrellas de calificación en reseñas
val ServifyOnlineGreen = Color(0xFF10B981)    // Textos "En línea" / Disponible

// --- Acciones Críticas ---
val ServifyDanger = Color(0xFFEF4444)         // Textos / ícono "Cerrar Sesión" / error
val ServifyRed = Color(0xFFEF4444)            // Alias para cancelación y errores
val ServifyDangerBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3B0D11) else Color(0xFFFEF2F2)

// --- Elementos Flotantes / Botones Secundarios ---
val ServifyFabDark = Color(0xFF052659)

// Alias de compatibilidad
val ServifyGreenPrimary = ServifyBluePrimary
val ServifyGreenDark = ServifyDarkNavy
val ServifyGreenLight = ServifyCianSoft
val ServifyTealAvatar: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0E3266) else ServifyCianSoft
val ServifyTealAvatarText: Color
    @Composable get() = if (isSystemInDarkTheme()) ServifyCianSoft else ServifyBluePrimary