package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.servify.data.SessionManager
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.UsuarioModel
import com.example.servify.ui.componentes.feedback.MensajeFeedback
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground) // Fondo celeste pastel (#DAFBFF)
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
                horizontalAlignment = Alignment.CenterHorizontally // <-- Aquí estaba el error (Decía CenterHost)
            ) {
                // Ícono Superior dentro de contenedor celeste suave
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

                // Títulos con jerarquía fuerte
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

                // Mensaje de feedback de error (si existe)
                MensajeFeedback(
                    mensaje = viewModel.mensajeError,
                    esError = true
                )

                // Campo de Correo Electrónico
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

                // Campo de Contraseña
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

                // ¿Olvidaste tu contraseña?
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

                // Botón Iniciar Sesión
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

                // Enlace de Registro
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

    if (viewModel.mostrarDialogoCategorias) {
        DialogoSeleccionCategoria(
            categorias = viewModel.categoriasDisponibles,
            cargando = viewModel.cargandoCategorias,
            onCategoriaSeleccionada = { categoria ->
                viewModel.confirmarCategoriaTecnico(categoria) { usuario, _ ->
                    onLoginExitoso(usuario)
                }
            },
            onDismiss = {
                viewModel.cerrarDialogoCategorias()
            }
        )
    }
}

@Composable
fun DialogoSeleccionCategoria(
    categorias: List<Categoria>,
    cargando: Boolean,
    onCategoriaSeleccionada: (Categoria) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Selecciona tu Especialidad",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ServifyTextTitle
            )
        },
        text = {
            if (cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ServifyBluePrimary)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Elige la categoría con la que trabajarás:",
                        fontSize = 13.sp,
                        color = ServifyTextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categorias) { cat ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCategoriaSeleccionada(cat) },
                                shape = RoundedCornerShape(12.dp),
                                color = ServifySurface,
                                border = BorderStroke(1.dp, ServifyBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = cat.nombre,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ServifyTextBody
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = ServifyRed)
            }
        },
        containerColor = ServifySurface,
        shape = RoundedCornerShape(20.dp)
    )
}