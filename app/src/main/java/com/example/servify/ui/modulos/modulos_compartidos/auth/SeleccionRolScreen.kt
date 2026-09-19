package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer

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
    //es para saber que tarjeta esta volteada
    var tarjetaVolteada by remember { mutableStateOf<TipoRol?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground)
            .padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 36.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ServifyGreenPrimary),
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
                    fontWeight = FontWeight.Bold,
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

            // tarjeta de roles
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // tarjeta del cliente
                TarjetaRolModerna(
                    titulo = "Quiero Contratar",
                    subtitulo = "Busco técnicos calificados para resolver necesidades de mi hogar u oficina.",
                    badgeTexto = "MODO CLIENTE",
                    descripcionAtras = "Podrás buscar profesionales cercanos, cotizar reparaciones en tiempo real, coordinar citas y pagar con total seguridad desde la app.",
                    icono = Icons.Outlined.Person,
                    estaSeleccionada = rolElegido == TipoRol.CLIENTE,
                    mostrarAtras = tarjetaVolteada == TipoRol.CLIENTE,
                    onClick = {
                        rolElegido = TipoRol.CLIENTE
                        tarjetaVolteada = if (tarjetaVolteada == TipoRol.CLIENTE) null else TipoRol.CLIENTE
                    }
                )

                // tarjeta del trabajador
                TarjetaRolModerna(
                    titulo = "Quiero Trabajar",
                    subtitulo = "Ofrezco mis servicios profesionales, recibo clientes y gano dinero.",
                    badgeTexto = "PROFESIONAL",
                    descripcionAtras = "Podrás crear tu catálogo de servicios, recibir solicitudes directas de clientes en tu zona, definir tus horarios y maximizar tus ingresos semanales.",
                    icono = Icons.Outlined.Handyman,
                    estaSeleccionada = rolElegido == TipoRol.TRABAJADOR,
                    mostrarAtras = tarjetaVolteada == TipoRol.TRABAJADOR,
                    onClick = {
                        rolElegido = TipoRol.TRABAJADOR
                        tarjetaVolteada = if (tarjetaVolteada == TipoRol.TRABAJADOR) null else TipoRol.TRABAJADOR
                    }
                )
            }

            // boton accion
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { rolElegido?.let { onContinuar(it) } },
                    enabled = rolElegido != null,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ServifyGreenPrimary,
                        disabledContainerColor = ServifyGreenPrimary.copy(alpha = 0.35f)
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
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                        color = ServifyGreenPrimary,
                        modifier = Modifier.clickable { onIrALogin() }
                    )
                }
            }
        }

    }
}

@Composable
private fun TarjetaRolModerna(
    titulo: String,
    subtitulo: String,
    badgeTexto: String,
    descripcionAtras: String,
    icono: ImageVector,
    estaSeleccionada: Boolean,
    mostrarAtras: Boolean,
    onClick: () -> Unit
) {
    val rotacion by animateFloatAsState(
        targetValue = if (mostrarAtras) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "rotacionTarjeta"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotacion
                cameraDistance = 12f * density
            }
            .clickable {
                onClick()
                //esto es para mostrar con cada click la parte de frente o trasera
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (estaSeleccionada) 4.dp else 1.dp
        ),
        border = BorderStroke(
            width = if (estaSeleccionada) 2.dp else 1.dp,
            color = if (estaSeleccionada) ServifyGreenPrimary else ServifyBorder
        )
    ) {
        // cuando pasa la mitad del giro se muestra la cara de atrás
        if (rotacion > 90f) {
            // cara trasera
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { rotationY = 180f }
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalles del perfil",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyGreenPrimary
                        )
                        Text(
                            text = "Toca para voltear ↺",
                            fontSize = 11.sp,
                            color = ServifyTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = descripcionAtras,
                        fontSize = 13.sp,
                        color = ServifyTextTitle,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            // cara del frente
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
                            .background(if (estaSeleccionada) ServifyGreenLight else ServifyBackground)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = badgeTexto,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (estaSeleccionada) ServifyGreenPrimary else ServifyTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (estaSeleccionada) ServifyGreenPrimary else ServifyBorder.copy(
                                    alpha = 0.5f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (estaSeleccionada) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (estaSeleccionada) ServifyTealAvatar else ServifyBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icono,
                            contentDescription = null,
                            tint = if (estaSeleccionada) ServifyTealAvatarText else ServifyTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextTitle
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitulo,
                            fontSize = 12.sp,
                            color = ServifyTextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}