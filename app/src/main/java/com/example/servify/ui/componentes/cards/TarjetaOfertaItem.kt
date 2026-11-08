package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.model.Mensaje
import java.util.Locale

@Composable
fun TarjetaOfertaItem(
    mensaje: Mensaje,
    esMio: Boolean,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    horaFormateada: String
) {
    val estado = mensaje.estadoOferta ?: "pendiente"

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.widthIn(min = 260.dp, max = 310.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF0F2C59), modifier = Modifier.size(16.dp))
                        Text("Propuesta Económica", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59))
                    }
                    Text(horaFormateada, fontSize = 10.sp, color = Color(0xFF94A3B8))
                }

                Text(
                    text = "$${String.format(Locale.US, "%.2f", mensaje.monto ?: 0.0)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )

                when (estado) {
                    "pendiente" -> {
                        if (esMio) {
                            Text("Esperando respuesta del usuario...", fontSize = 12.sp, color = Color(0xFF64748B), fontStyle = FontStyle.Italic)
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onRechazar,
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Text("Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onAceptar,
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
                                ) {
                                    Text("Aceptar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                    "aceptada" -> {
                        Surface(color = Color(0xFFCCFBF1), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✓ Oferta Aceptada (Acuerdo Cerrado)", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E), textAlign = TextAlign.Center)
                        }
                    }
                    "rechazada" -> {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✕ Oferta Rechazada", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C), textAlign = TextAlign.Center)
                        }
                    }
                    else -> {
                        Text("Oferta cancelada", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}