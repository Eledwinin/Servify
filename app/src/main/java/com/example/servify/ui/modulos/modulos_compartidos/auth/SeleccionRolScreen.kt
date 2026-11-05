package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

enum class TipoRol {
    CLIENTE,
    TRABAJADOR
}

@Composable
fun SeleccionRolScreen(
    onContinuar: (TipoRol) -> Unit = {},
    onIrALogin: () -> Unit = {}
) {
    var rolElegido by remember { mutableStateOf<TipoRol?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Cabecera superior
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(ServifyBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkOutline,
                        contentDescription = "Logo Servify",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Elige tu camino",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ServifyTextTitle
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Selecciona el tipo de cuenta con el que deseas comenzar en Servify.",
                    fontSize = 14.sp,
                    color = ServifyTextMuted,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjetas de Roles
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta del Cliente
                TarjetaRolMejorada(
                    titulo = "Quiero Contratar",
                    subtitulo = "Busco técnicos calificados para resolver necesidades de mi hogar u oficina.",
                    badgeTexto = "MODO CLIENTE",
                    icono = Icons.Outlined.Person,
                    estaSeleccionada = rolElegido == TipoRol.CLIENTE,
                    onClick = { rolElegido = TipoRol.CLIENTE }
                )

                // Tarjeta del Trabajador
                TarjetaRolMejorada(
                    titulo = "Quiero Trabajar",
                    subtitulo = "Ofrezco mis servicios profesionales, recibo clientes y gano dinero.",
                    badgeTexto = "MODO PROFESIONAL",
                    icono = Icons.Outlined.Handyman,
                    estaSeleccionada = rolElegido == TipoRol.TRABAJADOR,
                    onClick = { rolElegido = TipoRol.TRABAJADOR }
                )

                // Tarjeta de información / detalles dinámicos según la selección
                AnimatedVisibility(
                    visible = rolElegido != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (rolElegido == TipoRol.CLIENTE) "Beneficios Modo Cliente" else "Beneficios Modo Profesional",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyCianSoft
                                )
                                Text(
                                    text = "Seleccionado",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (rolElegido == TipoRol.CLIENTE)
                                    "Podrás buscar profesionales cercanos, cotizar reparaciones en tiempo real, coordinar citas y pagar con total seguridad desde la app."
                                else
                                    "Podrás crear tu perfil de servicios, recibir solicitudes directas y soliicitudes de clientes en tu zona, y contactarte de manera direta con los clientes.",
                                fontSize = 13.sp,
                                color = Color.White,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Acción y Enlace inferior
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { rolElegido?.let { onContinuar(it) } },
                    enabled = rolElegido != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ServifyBluePrimary,
                        contentColor = Color.White,
                        disabledContainerColor = ServifyBluePrimary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Continuar con el Registro",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        fontSize = 13.sp,
                        color = ServifyTextMuted
                    )
                    Text(
                        text = "Inicia Sesión",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary,
                        modifier = Modifier.clickable { onIrALogin() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRolMejorada(
    titulo: String,
    subtitulo: String,
    badgeTexto: String,
    icono: ImageVector,
    estaSeleccionada: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionada) ServifySurface else ServifySurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (estaSeleccionada) 8.dp else 2.dp
        ),
        border = BorderStroke(
            width = if (estaSeleccionada) 2.5.dp else 1.dp,
            color = if (estaSeleccionada) ServifyBluePrimary else ServifyBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (estaSeleccionada) ServifyCianSoft else ServifyInputBackground)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = badgeTexto,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (estaSeleccionada) ServifyBluePrimary else ServifyTextMuted
                    )
                }

                // Indicador de selección visual nítido
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (estaSeleccionada) ServifyBluePrimary else ServifyBorder,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (estaSeleccionada) ServifyBluePrimary else ServifyInputBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = if (estaSeleccionada) Color.White else ServifyBluePrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitulo,
                        fontSize = 12.sp,
                        color = ServifyTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}