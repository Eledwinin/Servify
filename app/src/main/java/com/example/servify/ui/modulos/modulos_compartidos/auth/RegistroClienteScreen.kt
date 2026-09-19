package com.example.servify.ui.modulos.modulos_compartidos.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifySurface
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun RegistroClienteScreen(
    onRegistroExitoso: () -> Unit = {},
    onIrALogin: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val formularioValido = nombre.isNotBlank() &&
            email.isNotBlank() &&
            telefono.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmarPassword

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(color = ServifyGreenPrimary, shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkOutline,
                    contentDescription = "Logo Servify",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Cuenta de Cliente",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Crea tu cuenta para solicitar servicios cerca de ti.",
                fontSize = 14.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                etiqueta = "Nombre Completo",
                valor = nombre,
                onValorCambiado = { nombre = it },
                placeholder = "Ej. Carlos Morales",
                iconoInicio = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Correo Electrónico",
                valor = email,
                onValorCambiado = { email = it },
                placeholder = "tu@correo.com",
                iconoInicio = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Número de Teléfono",
                valor = telefono,
                onValorCambiado = { telefono = it },
                placeholder = "7000-0000",
                iconoInicio = Icons.Outlined.Phone,
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Contraseña",
                valor = password,
                onValorCambiado = { password = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Confirmar Contraseña",
                valor = confirmarPassword,
                onValorCambiado = { confirmarPassword = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            BotonPrincipal(
                texto = "Crear Cuenta de Cliente",
                onClick = onRegistroExitoso,
                habilitado = formularioValido
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                    color = ServifyGreenPrimary,
                    modifier = Modifier.clickable { onIrALogin() }
                )
            }
        }
    }
}