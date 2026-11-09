package com.example.servify.ui.modulos.modulos_compartidos.pagos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

data class MetodoPagoOpcion(
    val id: String,
    val etiqueta: String,
    val ultimosDigitos: String,
    val tipoIcono: String,
    val colorBadge: Color,
    val colorTextoBadge: Color
)

@Composable
fun MetodosPagoScreen(
    monto: Double,
    tipoPlan: String,
    onVolver: () -> Unit,
    onConfirmarPago: (metodoSeleccionado: MetodoPagoOpcion) -> Unit
) {
    val metodos = listOf(
        MetodoPagoOpcion("1", "VISA", "•••• 4242", "VISA", Color(0xFFE0E7FF), Color(0xFF1D4ED8)),
        MetodoPagoOpcion("2", "MC", "•••• 1234", "MC", Color(0xFFFFE4E6), Color(0xFFE11D48)),
        MetodoPagoOpcion("3", "Pay", "Apple Pay", "ApplePay", Color(0xFFF3F4F6), Color(0xFF111827))
    )

    var metodoSeleccionado by remember { mutableStateOf(metodos[0]) }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servify",
                    color = Color(0xFF0F5132),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Surface(
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MODO TRABAJADOR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCBD5E1))
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = ServifyTextTitle,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Métodos de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )
            }

            TextButton(
                onClick = { /* Acción para agregar nueva tarjeta */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Nuevo método de pago",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
            }

            Spacer(Modifier.height(16.dp))

            metodos.forEach { metodo ->
                val esSeleccionado = metodoSeleccionado.id == metodo.id
                TarjetaMetodoPagoItem(
                    metodo = metodo,
                    seleccionado = esSeleccionado,
                    onSelect = { metodoSeleccionado = metodo }
                )
                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onConfirmarPago(metodoSeleccionado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
            ) {
                Text(
                    text = "Confirmar y Pagar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TarjetaMetodoPagoItem(
    metodo: MetodoPagoOpcion,
    seleccionado: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (seleccionado) 1.5.dp else 1.dp,
            color = if (seleccionado) Color(0xFF059669) else ServifyBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = metodo.colorBadge,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(width = 46.dp, height = 28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = metodo.etiqueta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = metodo.colorTextoBadge
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = metodo.ultimosDigitos,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ServifyTextTitle
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (seleccionado) Color(0xFF059669) else Color.Transparent)
                    .then(
                        if (!seleccionado) Modifier.background(
                            Color.Transparent,
                            CircleShape
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (seleccionado) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(20.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.5.dp, ServifyBorder)
                    ) {}
                }
            }
        }
    }
}