package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.indicadores.ProgresoPasos
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.ServifyBackground
import com.example.servify.ui.theme.ServifyGreenLight
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifySurface
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun OlvidePasswordScreen(
    onPasswordRestablecido: () -> Unit = {},
    onVolverALogin: () -> Unit = {}
) {
    var pasoActual by remember { mutableIntStateOf(1) }

    // Estados de los campos
    var email by remember { mutableStateOf("") }
    var codigoOtp by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground)
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // header
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (pasoActual > 1) pasoActual-- else onVolverALogin()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyTextTitle,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Recuperar Acceso",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // componente visual del progreso
                ProgresoPasos(
                    pasoActual = pasoActual,
                    totalPasos = 3,
                    etiquetas = listOf("Ingresa tu correo", "Verifica el código", "Nueva contraseña")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // componente dinamico para pasos
            AnimatedContent(
                targetState = pasoActual,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "cambioPasoAnimado"
            ) { paso ->
                when (paso) {
                    1 -> {
                        // 1- enviar Correo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ingresa tu correo electrónico registrado y te enviaremos un código de seguridad para verificar tu identidad.",
                                fontSize = 14.sp,
                                color = ServifyTextMuted,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            CampoTexto(
                                etiqueta = "Correo Electrónico",
                                valor = email,
                                onValorCambiado = { email = it },
                                placeholder = "ejemplo@correo.com",
                                iconoInicio = Icons.Outlined.Email,
                                tipoTeclado = KeyboardType.Email
                            )
                        }
                    }

                    2 -> {
                        // 2- ingresar Código OTP
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Código de Verificación",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hemos enviado un código a $email. Ingrésalo para confirmar que eres tú.",
                                fontSize = 14.sp,
                                color = ServifyTextMuted,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            CampoTexto(
                                etiqueta = "Código de 4 dígitos",
                                valor = codigoOtp,
                                onValorCambiado = { if (it.length <= 4) codigoOtp = it },
                                placeholder = "1234",
                                iconoInicio = Icons.Outlined.Pin,
                                tipoTeclado = KeyboardType.Number
                            )
                        }
                    }

                    3 -> {
                        // 3- restablecer Contraseña
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Crea una nueva contraseña",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Asegúrate de que sea segura y que tenga al menos 6 caracteres.",
                                fontSize = 14.sp,
                                color = ServifyTextMuted,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            CampoTexto(
                                etiqueta = "Nueva Contraseña",
                                valor = nuevaPassword,
                                onValorCambiado = { nuevaPassword = it },
                                placeholder = "••••••••",
                                iconoInicio = Icons.Outlined.Lock,
                                esPassword = true,
                                tipoTeclado = KeyboardType.Password
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CampoTexto(
                                etiqueta = "Confirmar Nueva Contraseña",
                                valor = confirmarPassword,
                                onValorCambiado = { confirmarPassword = it },
                                placeholder = "••••••••",
                                iconoInicio = Icons.Outlined.Lock,
                                esPassword = true,
                                tipoTeclado = KeyboardType.Password
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // boton principal
            Column(modifier = Modifier.fillMaxWidth()) {
                val botonHabilitado = when (pasoActual) {
                    1 -> email.isNotBlank() && email.contains("@")
                    2 -> codigoOtp.length >= 4
                    3 -> nuevaPassword.isNotBlank() && nuevaPassword == confirmarPassword
                    else -> false
                }

                val textoBoton = when (pasoActual) {
                    1 -> "Enviar Código"
                    2 -> "Verificar Código"
                    3 -> "Restablecer Contraseña"
                    else -> "Continuar"
                }

                BotonPrincipal(
                    texto = textoBoton,
                    habilitado = botonHabilitado,
                    onClick = {
                        when (pasoActual) {
                            1 -> pasoActual = 2
                            2 -> pasoActual = 3
                            3 -> onPasswordRestablecido()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Recordaste tu clave? ",
                        fontSize = 13.sp,
                        color = ServifyTextMuted
                    )
                    Text(
                        text = "Inicia Sesión",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyGreenPrimary,
                        modifier = Modifier.clickable { onVolverALogin() }
                    )
                }
            }
        }
    }
}