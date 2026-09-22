package com.example.servify.ui.componentes.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MensajeFeedback(
    mensaje: String?,
    esError: Boolean = true,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !mensaje.isNullOrBlank(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val fondoColor = if (esError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
        val bordeColor = if (esError) Color(0xFFFFCDD2) else Color(0xFFC8E6C9)
        val contenidoColor = if (esError) Color(0xFFD32F2F) else Color(0xFF2E7D32)
        val icono = if (esError) Icons.Default.Warning else Icons.Default.CheckCircle

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(fondoColor, shape = RoundedCornerShape(10.dp))
                .border(1.dp, bordeColor, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = contenidoColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = mensaje ?: "",
                    color = contenidoColor,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}