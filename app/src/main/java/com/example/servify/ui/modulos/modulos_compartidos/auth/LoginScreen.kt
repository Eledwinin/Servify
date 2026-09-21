package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.model.UsuarioModel
import com.example.servify.ui.theme.*

@Composable
fun LoginScreen(
    onLoginExitoso: (UsuarioModel) -> Unit,
    onIrARegistro: () -> Unit,
    onOlvidastePassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ServifyBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Servify",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = ServifyGreenPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyLarge,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            viewModel.mensajeError?.let { error ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = ServifyDangerBg
                ) {
                    Text(
                        text = error,
                        color = ServifyDanger,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo electrónico", color = ServifyTextPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = ServifyTextMuted
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ServifySurface,
                    unfocusedContainerColor = ServifySurface,
                    focusedBorderColor = ServifyGreenPrimary,
                    unfocusedBorderColor = ServifyBorder,
                    focusedTextColor = ServifyTextTitle,
                    unfocusedTextColor = ServifyTextBody
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.cargando
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña", color = ServifyTextPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ServifyTextMuted
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = ServifyTextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.iniciarSesion { usuario ->
                            onLoginExitoso(usuario)
                        }
                    }
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ServifySurface,
                    unfocusedContainerColor = ServifySurface,
                    focusedBorderColor = ServifyGreenPrimary,
                    unfocusedBorderColor = ServifyBorder,
                    focusedTextColor = ServifyTextTitle,
                    unfocusedTextColor = ServifyTextBody
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.cargando
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = ServifyGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(enabled = !viewModel.cargando) {
                        onOlvidastePassword()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.iniciarSesion { usuario ->
                        onLoginExitoso(usuario)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !viewModel.cargando,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ServifyGreenPrimary,
                    contentColor = ServifySurface,
                    disabledContainerColor = ServifyGreenPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (viewModel.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ServifySurface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ServifyTextMuted
                )
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ServifyGreenPrimary,
                    modifier = Modifier.clickable(enabled = !viewModel.cargando) {
                        onIrARegistro()
                    }
                )
            }
        }
    }
}