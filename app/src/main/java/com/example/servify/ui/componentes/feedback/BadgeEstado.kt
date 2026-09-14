package com.example.servify.ui.componentes.feedback

// Etiqueta de color según el estado ("En camino", "Pendiente", "Completado")
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BadgeEstado(estado: String, modifier: Modifier = Modifier) {
    val (fondo, textoColor) = when (estado.lowercase()) {
        "completado" -> Pair(Color(0xFFD1FAE5), Color(0xFF065F46))
        "en camino", "en progreso" -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        "cancelado" -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = modifier
            .background(color = fondo, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textoColor
        )
    }
}