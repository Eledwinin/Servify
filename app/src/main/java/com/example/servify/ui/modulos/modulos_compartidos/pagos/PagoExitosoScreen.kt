package com.example.servify.ui.modulos.modulos_compartidos.pagos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.servify.ui.navigation.Rutas

@Composable
fun PagoExitosoScreen(
    navController: NavController,
    monto: Double = 9.99,
    onVolverInicio: () -> Unit = {}
) {
    val verdeServify = Color(0xFF1B7B61)
    val verdeFondoIcono = Color(0xFFE2F3EE)
    val textoOscuro = Color(0xFF1E293B)
    val textoGris = Color(0xFF64748B)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "MODO TRABAJADOR",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.6f))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(verdeFondoIcono)
                    .border(2.dp, verdeServify, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Éxito",
                    tint = verdeServify,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pago exitoso",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textoOscuro
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tu suscripción ha sido activada correctamente.",
                fontSize = 13.sp,
                color = textoGris
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Tarjeta de Resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monto pagado",
                            fontSize = 13.sp,
                            color = textoGris,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = String.format("$%.2f", monto),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textoOscuro
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                    FilaDetalle(titulo = "Fecha", valor = "Hoy")
                    FilaDetalle(titulo = "Estado", valor = "Activo")
                    FilaDetalle(titulo = "Método de pago", valor = "Visa •••• 4242")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    navController.navigate(Rutas.MuroSolicitudes.ruta) {
                        popUpTo(Rutas.MuroSolicitudes.ruta) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
            ) {
                Text(
                    text = "Ir al Muro de Solicitudes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(Rutas.Perfil.ruta) {
                        popUpTo(Rutas.Perfil.ruta) { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = "Ver mi perfil",
                    color = verdeServify,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FilaDetalle(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = titulo, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text = valor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
    }
}