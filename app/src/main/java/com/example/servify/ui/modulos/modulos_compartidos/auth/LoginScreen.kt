package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.R.attr.password
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.model.Usuario
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifySurface
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextTitle
import com.example.servify.ui.theme.ServifyTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: (Usuario) -> Unit = {},
    onIrARegistro: () -> Unit = {},
    onOlvidastePassword: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Servify
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(color = ServifyGreenPrimary, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkOutline,
                    contentDescription = "Logo Servify",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Título y subtítulo
            Text(
                text = "Bienvenido de nuevo",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Inicia sesión para continuar en Servify.",
                fontSize = 14.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            CampoTexto(
                etiqueta = "Correo Electrónico",
                valor = viewModel.correo,
                onValorCambiado = { viewModel.onCorreoChange(it) },
                placeholder = "tu@correo.com",
                iconoInicio = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Contraseña
            CampoTexto(
                etiqueta = "Contraseña",
                valor = viewModel.password,
                onValorCambiado = { viewModel.onPasswordChange(it) },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ServifyGreenPrimary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onOlvidastePassword() }
            )

            // Mensaje de error si falla la autenticación
            AnimatedVisibility(visible = viewModel.mensajeError != null) {
                viewModel.mensajeError?.let { error ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))


            // Botón de inicio de sesión
            BotonPrincipal(
                texto = if (viewModel.cargando) "Iniciando Sesión..." else "Iniciar Sesión",
                onClick = {
                    viewModel.iniciarSesion{ usuario ->
                        onLoginExitoso(usuario)
                    }
                },
                habilitado = !viewModel.cargando && viewModel.correo.isNotBlank() && viewModel.password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Enlace a Registro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    fontSize = 13.sp,
                    color = ServifyTextMuted
                )
                Text(
                    text = "Regístrate",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyGreenPrimary,
                    modifier = Modifier.clickable { onIrARegistro() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ServifyTheme {
        LoginScreen()
    }
}