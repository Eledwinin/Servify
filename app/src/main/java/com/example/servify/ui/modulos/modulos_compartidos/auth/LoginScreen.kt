package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.servify.ui.componentes.feedback.MensajeFeedback
import com.example.servify.ui.theme.ServifyBackground
import com.example.servify.ui.theme.ServifyBluePrimary
import com.example.servify.ui.theme.ServifyBorder
import com.example.servify.ui.theme.ServifyCianSoft
import com.example.servify.ui.theme.ServifyDarkNavy
import com.example.servify.ui.theme.ServifyInputBackground
import com.example.servify.ui.theme.ServifySurface
import com.example.servify.ui.theme.ServifyTextBody
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextPlaceholder
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun LoginScreen(
    onLoginExitoso: (UsuarioModel) -> Unit,
    onIrARegistro: () -> Unit,
    onOlvidastePassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(ServifyDarkNavy)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ServifySurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(ServifyCianSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏠",
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ServifyTextTitle
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Inicia sesión para continuar en Servify.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ServifyTextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                MensajeFeedback(
                    mensaje = viewModel.mensajeError,
                    esError = true
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = viewModel.correo,
                        onValueChange = { viewModel.onCorreoChange(it) },
                        placeholder = { Text("tu@correo.com", color = ServifyTextPlaceholder) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = ServifyBluePrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ServifyInputBackground,
                            unfocusedContainerColor = ServifyInputBackground,
                            focusedBorderColor = ServifyBluePrimary,
                            unfocusedBorderColor = ServifyBorder,
                            focusedTextColor = ServifyTextTitle,
                            unfocusedTextColor = ServifyTextBody
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.cargando
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CONTRASEÑA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = { Text("••••••••", color = ServifyTextPlaceholder) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = ServifyBluePrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = ServifyBluePrimary
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ServifyInputBackground,
                            unfocusedContainerColor = ServifyInputBackground,
                            focusedBorderColor = ServifyBluePrimary,
                            unfocusedBorderColor = ServifyBorder,
                            focusedTextColor = ServifyTextTitle,
                            unfocusedTextColor = ServifyTextBody
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.cargando
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = onOlvidastePassword,
                        enabled = !viewModel.cargando,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.bodySmall,
                            color = ServifyBluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.iniciarSesion { usuario ->
                            onLoginExitoso(usuario)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !viewModel.cargando,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ServifyBluePrimary,
                        contentColor = ServifySurface,
                        disabledContainerColor = ServifyBluePrimary.copy(alpha = 0.6f)
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
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
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
                        fontWeight = FontWeight.ExtraBold,
                        color = ServifyBluePrimary,
                        modifier = Modifier.clickable(enabled = !viewModel.cargando) {
                            onIrARegistro()
                        }
                    )
                }
            }
        }
    }
}