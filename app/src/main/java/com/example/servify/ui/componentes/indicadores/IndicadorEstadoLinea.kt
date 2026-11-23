package com.example.servify.ui.componentes.indicadores

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.ServifyTextMuted

// Colores oficiales para el estado de presencia
val ServifyColorEnLinea = Color(0xFF22C55E)
val ServifyColorNoEnLinea = Color(0xFF9CA3AF)

/**
 * Indicador visual discreto y reactivo del estado de actividad de un usuario ("En línea" / "No está en línea").
 *
 * @param estaEnLinea Indica si el usuario remoto se encuentra actualmente activo.
 * @param modifier Modificador para el contenedor del indicador.
 * @param mostrarTexto Si es `true`, despliega el texto descriptivo junto al punto indicador.
 * @param tamanoPunto Diámetro del punto de estado (por defecto 8.dp según especificación).
 * @param tamanoTexto Tamaño tipográfico para el texto de estado.
 * @param colorTexto Color del texto de estado (por defecto el tono atenuado corporativo).
 * @param espaciado Separación horizontal entre el punto y la etiqueta de texto.
 */
@Composable
fun IndicadorEstadoLinea(
    estaEnLinea: Boolean,
    modifier: Modifier = Modifier,
    mostrarTexto: Boolean = true,
    tamanoPunto: Dp = 8.dp,
    tamanoTexto: TextUnit = 11.sp,
    colorTexto: Color = ServifyTextMuted,
    espaciado: Dp = 5.dp,
) {
    val colorPuntoAnimado by animateColorAsState(
        targetValue = if (estaEnLinea) ServifyColorEnLinea else ServifyColorNoEnLinea,
        animationSpec = tween(durationMillis = 300),
        label = "ColorIndicadorPresencia"
    )

    val textoEstado = if (estaEnLinea) "En línea" else "No está en línea"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(tamanoPunto)
                .background(colorPuntoAnimado, CircleShape)
        )
        if (mostrarTexto) {
            Spacer(modifier = Modifier.width(espaciado))
            Text(
                text = textoEstado,
                fontSize = tamanoTexto,
                fontWeight = if (estaEnLinea) FontWeight.Medium else FontWeight.Normal,
                color = colorTexto
            )
        }
    }
}
