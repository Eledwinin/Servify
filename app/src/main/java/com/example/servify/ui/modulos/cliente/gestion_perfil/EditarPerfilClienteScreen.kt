package com.example.servify.ui.modulos.cliente.gestion_perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilClienteScreen(
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: EditarPerfilClienteViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Colores corporativos Servify
    val verdePrincipal = Color(0xFF168067)
    val fondoPantalla = Color.White
    val textoPrincipal = Color(0xFF1E293B)
    val textoSecundario = Color(0xFF64748B)
    val bordeInput = Color(0xFFCBD5E1)
    val fondoDeshabilitado = Color(0xFFF1F5F9)

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            viewModel.cargarPerfil(token)
        }
    }

    LaunchedEffect(uiState.actualizadoConExito) {
        if (uiState.actualizadoConExito) {
            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            viewModel.limpiarEstadoExito()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        containerColor = fondoPantalla,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = textoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondoPantalla
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.nombre.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = verdePrincipal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Campo Correo electrónico (Bloqueado / Solo lectura)
                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Correo electrónico") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Campo no editable",
                            tint = textoSecundario,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = textoSecundario,
                        disabledBorderColor = Color(0xFFE2E8F0),
                        disabledLabelColor = textoSecundario,
                        disabledContainerColor = fondoDeshabilitado
                    ),
                    supportingText = {
                        Text(
                            text = "El correo no se puede cambiar directamente por seguridad",
                            fontSize = 11.sp,
                            color = textoSecundario
                        )
                    }
                )

                // Nombre completo
                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdePrincipal,
                        unfocusedBorderColor = bordeInput,
                        focusedLabelColor = verdePrincipal,
                        unfocusedLabelColor = textoSecundario,
                        focusedTextColor = textoPrincipal,
                        unfocusedTextColor = textoPrincipal,
                        focusedContainerColor = fondoPantalla,
                        unfocusedContainerColor = fondoPantalla
                    )
                )

                // Teléfono de contacto
                OutlinedTextField(
                    value = uiState.telefono,
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono de contacto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdePrincipal,
                        unfocusedBorderColor = bordeInput,
                        focusedLabelColor = verdePrincipal,
                        unfocusedLabelColor = textoSecundario,
                        focusedTextColor = textoPrincipal,
                        unfocusedTextColor = textoPrincipal,
                        focusedContainerColor = fondoPantalla,
                        unfocusedContainerColor = fondoPantalla
                    )
                )

                // Dirección
                OutlinedTextField(
                    value = uiState.direccion,
                    onValueChange = { viewModel.onDireccionChange(it) },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdePrincipal,
                        unfocusedBorderColor = bordeInput,
                        focusedLabelColor = verdePrincipal,
                        unfocusedLabelColor = textoSecundario,
                        focusedTextColor = textoPrincipal,
                        unfocusedTextColor = textoPrincipal,
                        focusedContainerColor = fondoPantalla,
                        unfocusedContainerColor = fondoPantalla
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botón Guardar Cambios
                Button(
                    onClick = { viewModel.guardarCambios(token) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verdePrincipal,
                        contentColor = Color.White
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Guardar Cambios",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Cancelar
                OutlinedButton(
                    onClick = onNavigateBack,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textoSecundario
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}