package com.example.servify.ui.modulos.cliente.contratacion

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.componentes.QrGenerator

@Composable
fun DialogoCierreCliente(
    solicitudId: Int,
    tecnicoId: Int?,
    onDismissRequest: () -> Unit,
    onConfirmarPago: (metodoPago: String) -> Unit,
    onCalificarClick: () -> Unit, // 👈 🎯 NUEVO CALLBACK DIRECTO
    qrToken: String?,
    cargando: Boolean = false
) {
    var metodoSeleccionado by remember { mutableStateOf("efectivo") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(qrToken) {
        if (!qrToken.isNullOrEmpty()) {
            qrBitmap = QrGenerator.generarBitmapQr(qrToken)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (qrToken.isNullOrEmpty()) {
                Button(
                    onClick = { onConfirmarPago(metodoSeleccionado) },
                    enabled = !cargando,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Generar QR de Cierre")
                    }
                }
            } else {
                // 🚀 ESTRATEGIA NUEVA: BOTÓN DIRECTO PARA NAVEGAR A RESEÑAS
                Button(
                    onClick = onCalificarClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Calificar al Técnico ⭐")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(if (qrToken.isNullOrEmpty()) "Cancelar" else "Cerrar")
            }
        },
        title = {
            Text(
                text = if (qrToken.isNullOrEmpty()) "Finalizar Servicio" else "Muestra este QR al Técnico",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (qrToken.isNullOrEmpty()) {
                    Text(
                        text = "Selecciona el método con el que acordaste pagar la solicitud:",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (metodoSeleccionado == "efectivo"),
                            onClick = { metodoSeleccionado = "efectivo" }
                        )
                        Text(text = "💵 Efectivo")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (metodoSeleccionado == "tarjeta"),
                            onClick = { metodoSeleccionado = "tarjeta" }
                        )
                        Text(text = "💳 Tarjeta (Simulada / Stripe)")
                    }
                } else {
                    Text(
                        text = "El técnico deberá escanear este código desde su app para validar el servicio. Una vez escaneado, presiona para calificarlo.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    qrBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Código QR de Cierre",
                            modifier = Modifier
                                .size(220.dp)
                                .padding(8.dp)
                        )
                    } ?: CircularProgressIndicator()
                }
            }
        }
    )
}