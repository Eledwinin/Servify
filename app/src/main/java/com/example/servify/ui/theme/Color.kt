package com.example.servify.ui.theme

import androidx.compose.ui.graphics.Color

// --- Paleta Corporativa Principal (Azules y Celestes) ---
val ServifyDarkNavy = Color(0xFF021024)       // Títulos principales, textos oscuros
val ServifyBluePrimary = Color(0xFF052659)    // Botones principales, elementos pesados
val ServifyTealAccent = Color(0xFF548383)     // Elementos secundarios, acentos
val ServifyBlueLight = Color(0xFF7DA0CA)      // Bordes, subtítulos, elementos de apoyo
val ServifyCianSoft = Color(0xFFC1E8FF)       // Badges, chips activos, fondos sutiles

// --- Superficies y Fondos (Con el nuevo fondo #E5FCFF) ---
val ServifyBackground = Color(0xFFEBF8FC)     // Fondo general actualizado de la app
val ServifySurface = Color(0xFFFFFFFF)        // Fondo de cards, modales y contenedores
val ServifyInputBackground = Color(0xFFF0F4F8)// Fondo de inputs de texto y barras
val ServifyBorder = Color(0xFFCBD5E1)         // Bordes de tarjetas y separadores

// --- Tipografía y Textos ---
val ServifyTextTitle = Color(0xFF021024)      // Títulos principales en negrita
val ServifyTextBody = Color(0xFF052659)       // Textos estándar
val ServifyTextMuted = Color(0xFF548383)      // Subtítulos, fechas
val ServifyTextPlaceholder = Color(0xFF7DA0CA)// Placeholder en inputs

// --- Estados y Tags (Badges) ---
val ServifyPendingBg = Color(0xFFFEF3C7)      // Fondo tag "PENDIENTE"
val ServifyPendingText = Color(0xFFD97706)    // Textos tag "PENDIENTE"
val ServifyStarYellow = Color(0xFFF59E0B)     // Estrellas de calificación en reseñas
val ServifyOnlineGreen = Color(0xFF10B981)    // Textos "En línea"

// --- Acciones Críticas ---
val ServifyDanger = Color(0xFFEF4444)         // Textos / ícono "Cerrar Sesión"
val ServifyRed = Color(0xFFEF4444)            // Alias para cancelación y errores
val ServifyDangerBg = Color(0xFFFEF2F2)       // Fondo botón de alerta / peligro

// --- Elementos Flotantes / Botones Secundarios ---
val ServifyFabDark = Color(0xFF052659)        // Botón flotante / acciones rápidas

// Alias de compatibilidad
val ServifyGreenPrimary = ServifyBluePrimary
val ServifyGreenDark = ServifyDarkNavy
val ServifyGreenLight = ServifyCianSoft
val ServifyTealAvatar = ServifyCianSoft
val ServifyTealAvatarText = ServifyBluePrimary