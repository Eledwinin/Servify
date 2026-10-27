package com.example.servify.ui.componentes.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    textoCancelar: String,
    cargando: Boolean = false,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val rojo = Color(0xFFDC2626)
    AlertDialog(
        onDismissRequest = { if (!cargando) onCancelar() },
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(mensaje, fontSize = 14.sp)
                if (cargando) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = rojo)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirmar, enabled = !cargando, colors = ButtonDefaults.buttonColors(containerColor = rojo)) {
                Text(textoConfirmar, color = Color.White)
            }
        },
        dismissButton = { OutlinedButton(onClick = onCancelar, enabled = !cargando) { Text(textoCancelar) } }
    )
}