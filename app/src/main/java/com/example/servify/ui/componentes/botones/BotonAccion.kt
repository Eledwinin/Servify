package com.example.servify.ui.componentes.botones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonAccion(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit,
    colorFondo: Color = Color.White,
    colorContenido: Color = Color(0xFF1E293B),
    conBorde: Boolean = true,
    mostrarFlecha: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = colorFondo,
        border = if (conBorde) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = colorContenido, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(texto, color = colorContenido, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (mostrarFlecha) Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
        }
    }
}