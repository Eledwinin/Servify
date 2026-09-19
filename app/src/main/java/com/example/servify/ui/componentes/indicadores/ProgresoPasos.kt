package com.example.servify.ui.componentes.indicadores

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.ServifyBorder
import com.example.servify.ui.theme.ServifyGreenLight
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun ProgresoPasos(
    pasoActual: Int,
    totalPasos: Int = 3,
    etiquetas: List<String> = listOf("Correo", "Código", "Nueva clave")
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Fila de círculos y conectores
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 1..totalPasos) {
                val estaCompletado = i < pasoActual
                val esActual = i == pasoActual

                val colorFondo by animateColorAsState(
                    targetValue = when {
                        estaCompletado || esActual -> ServifyGreenPrimary
                        else -> ServifyBorder.copy(alpha = 0.4f)
                    },
                    animationSpec = tween(350),
                    label = "colorFondoPaso"
                )

                // circulo del paso
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colorFondo),
                    contentAlignment = Alignment.Center
                ) {
                    if (estaCompletado) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Completado",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "$i",
                            color = if (esActual) Color.White else ServifyTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Línea conectora entre círculos (excepto el último)
                if (i < totalPasos) {
                    val conexionLlena = i < pasoActual
                    val progresoLinea by animateFloatAsState(
                        targetValue = if (conexionLlena) 1f else 0f,
                        animationSpec = tween(400),
                        label = "progresoLinea"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ServifyBorder.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progresoLinea)
                                .height(4.dp)
                                .background(ServifyGreenPrimary)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Etiqueta informativa del paso actual
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASO $pasoActual DE $totalPasos",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyGreenPrimary,
                letterSpacing = 1.sp
            )
            Text(
                text = etiquetas.getOrElse(pasoActual - 1) { "" },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ServifyTextTitle
            )
        }
    }
}