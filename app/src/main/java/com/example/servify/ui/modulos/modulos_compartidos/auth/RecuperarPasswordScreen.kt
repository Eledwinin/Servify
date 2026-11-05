package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.feedback.MensajeFeedback
import com.example.servify.ui.componentes.indicadores.ProgresoPasos
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.*

@Composable
fun OlvidePasswordScreen(
    onPasswordRestablecido: () -> Unit = {},
    onVolverALogin: () -> Unit = {},
    viewModel: RecuperarPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.cambioExitoso) {
        if (viewModel.cambioExitoso) {
            Toast.makeText(
                context,
                "¡Contraseña actualizada con éxito! Ya puedes iniciar sesión.",
                Toast.LENGTH_LONG
            ).show()
            onPasswordRestablecido()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground) // Fondo celeste pastel luminoso
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
           
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { viewModel.retrocederPaso(onVolverALogin) },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ServifySurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(22.dp)
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

                Spacer(modifier = Modifier.height(24.dp))

                ProgresoPasos(
                    pasoActual = viewModel.pasoActual,
                    totalPasos = 3,
                    etiquetas = listOf("Correo", "Código", "Nueva clave")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            MensajeFeedback(
                mensaje = viewModel.mensajeError,
                esError = true
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ServifySurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    AnimatedContent(
                        targetState = viewModel.pasoActual,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "cambioPasoAnimado"
                    ) { paso ->
                        when (paso) {
                            1 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "¿Olvidaste tu contraseña?",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ServifyTextTitle
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ingresa tu correo electrónico registrado y te enviaremos un código de seguridad para verificar tu identidad.",
                                        fontSize = 13.sp,
                                        color = ServifyTextMuted,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))

                                    CampoTexto(
                                        etiqueta = "CORREO ELECTRÓNICO",
                                        valor = viewModel.correo,
                                        onValorCambiado = { viewModel.onCorreoChange(it) },
                                        placeholder = "ejemplo@correo.com",
                                        iconoInicio = Icons.Outlined.Email,
                                        tipoTeclado = KeyboardType.Email
                                    )
                                }
                            }

                            2 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Código de Verificación",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ServifyTextTitle
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Hemos enviado un código a ${viewModel.correo}. Ingrésalo para confirmar que eres tú.",
                                        fontSize = 13.sp,
                                        color = ServifyTextMuted,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))

                                    CampoTexto(
                                        etiqueta = "CÓDIGO DE 6 DÍGITOS",
                                        valor = viewModel.codigoOtp,
                                        onValorCambiado = { viewModel.onCodigoOtpChange(it) },
                                        placeholder = "123456",
                                        iconoInicio = Icons.Outlined.Pin,
                                        tipoTeclado = KeyboardType.Number
                                    )
                                }
                            }

                            3 -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Nueva Contraseña",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ServifyTextTitle
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Asegúrate de que sea segura y tenga al menos 6 caracteres.",
                                        fontSize = 13.sp,
                                        color = ServifyTextMuted,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))

                                    CampoTexto(
                                        etiqueta = "NUEVA CONTRASEÑA",
                                        valor = viewModel.nuevaPassword,
                                        onValorCambiado = { viewModel.onNuevaPasswordChange(it) },
                                        placeholder = "••••••••",
                                        iconoInicio = Icons.Outlined.Lock,
                                        esPassword = true,
                                        tipoTeclado = KeyboardType.Password
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    CampoTexto(
                                        etiqueta = "CONFIRMAR NUEVA CONTRASEÑA",
                                        valor = viewModel.confirmarPassword,
                                        onValorCambiado = { viewModel.onConfirmarPasswordChange(it) },
                                        placeholder = "••••••••",
                                        iconoInicio = Icons.Outlined.Lock,
                                        esPassword = true,
                                        tipoTeclado = KeyboardType.Password
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = ServifyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val botonHabilitado = !viewModel.cargando && when (viewModel.pasoActual) {
                    1 -> viewModel.correo.isNotBlank() && viewModel.correo.contains("@")
                    2 -> viewModel.codigoOtp.length == 6
                    3 -> viewModel.nuevaPassword.isNotBlank() && viewModel.nuevaPassword == viewModel.confirmarPassword
                    else -> false
                }

                val textoBoton = when (viewModel.pasoActual) {
                    1 -> "Enviar Código de Verificación"
                    2 -> "Verificar Código"
                    3 -> "Restablecer Contraseña"
                    else -> "Continuar"
                }

                Button(
                    onClick = {
                        when (viewModel.pasoActual) {
                            1 -> viewModel.solicitarCodigo()
                            2 -> viewModel.verificarCodigoPaso()
                            3 -> viewModel.restablecerPassword { onPasswordRestablecido() }
                        }
                    },
                    enabled = botonHabilitado,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ServifyBluePrimary,
                        contentColor = Color.White,
                        disabledContainerColor = ServifyBluePrimary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = textoBoton,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                        color = ServifyBluePrimary,
                        modifier = Modifier.clickable(enabled = !viewModel.cargando) {
                            onVolverALogin()
                        }
                    )
                }
            }
        }
    }
}