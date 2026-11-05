package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.*

@Composable
fun RegistroClienteScreen(
    onRegistroExitoso: () -> Unit = {},
    onIrALogin: () -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val estadoRegistro by viewModel.estadoRegistro.collectAsState()

    LaunchedEffect(estadoRegistro) {
        when (val estado = estadoRegistro) {
            is RegistroState.Error -> {
                Toast.makeText(context, estado.mensaje, Toast.LENGTH_LONG).show()
            }
            is RegistroState.Success -> {
                Toast.makeText(context, estado.mensaje, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    val scrollState = rememberScrollState()
    val formularioValido = nombre.isNotBlank() &&
            email.isNotBlank() &&
            telefono.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmarPassword &&
            estadoRegistro !is RegistroState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground) // Fondo celeste pastel luminoso (#E5FCFF)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Retroceder a la izquierda
                IconButton(
                    onClick = { onVolver() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ServifySurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar",
                        tint = ServifyBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Icono / Emoji a la derecha
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ServifyBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Icono Cliente",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Cuenta de Cliente",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Crea tu cuenta para solicitar servicios cerca de ti.",
                fontSize = 13.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                etiqueta = "NOMBRE COMPLETO",
                valor = nombre,
                onValorCambiado = { nombre = it },
                placeholder = "Ej. Carlos Morales",
                iconoInicio = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "CORREO ELECTRÓNICO",
                valor = email,
                onValorCambiado = { email = it },
                placeholder = "tu@correo.com",
                iconoInicio = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "NÚMERO DE TELÉFONO",
                valor = telefono,
                onValorCambiado = { telefono = it },
                placeholder = "7000-0000",
                iconoInicio = Icons.Outlined.Phone,
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "CONTRASEÑA",
                valor = password,
                onValorCambiado = { password = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "CONFIRMAR CONTRASEÑA",
                valor = confirmarPassword,
                onValorCambiado = { confirmarPassword = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (estadoRegistro is RegistroState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ServifyBluePrimary)
                }
            } else {
                BotonPrincipal(
                    texto = "Crear Cuenta de Cliente",
                    onClick = {
                        viewModel.registrarUsuario(
                            nombre = nombre,
                            correo = email,
                            password = password,
                            telefono = telefono,
                            rol = "cliente",
                            onSuccess = onRegistroExitoso
                        )
                    },
                    habilitado = formularioValido
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes cuenta? ", fontSize = 13.sp, color = ServifyTextMuted)
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