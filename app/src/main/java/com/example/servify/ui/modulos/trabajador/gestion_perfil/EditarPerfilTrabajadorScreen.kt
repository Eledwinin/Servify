package com.example.servify.ui.modulos.trabajador.gestion_perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilTrabajadorScreen(
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: EditarPerfilTrabajadorViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val iniciales = uiState.nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "TR" }

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            viewModel.cargarPerfil(token)
        }
    }

    LaunchedEffect(uiState.actualizadoConExito) {
        if (uiState.actualizadoConExito) {
            Toast.makeText(context, "Perfil de trabajador actualizado correctamente", Toast.LENGTH_SHORT).show()
            viewModel.limpiarEstadoExito()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil (Trabajador)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(ServifySurface, CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.nombre.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ServifyBluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(ServifyBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iniciales, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Correo Electrónico (Bloqueado)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("CORREO ELECTRÓNICO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.correo,
                                onValueChange = {},
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = ServifyTextMuted, modifier = Modifier.size(20.dp)) },
                                trailingIcon = { Icon(Icons.Default.Lock, null, tint = ServifyTextMuted, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = ServifyTextTitle,
                                    disabledBorderColor = ServifyBorder,
                                    disabledContainerColor = ServifyInputBackground
                                ),
                                supportingText = {
                                    Text("El correo no se puede cambiar directamente por seguridad", fontSize = 11.sp, color = ServifyTextMuted)
                                }
                            )
                        }

                        // Nombre
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("NOMBRE COMPLETO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.nombre,
                                onValueChange = { viewModel.onNombreChange(it) },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = ServifyBluePrimary, modifier = Modifier.size(20.dp)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ServifyBluePrimary,
                                    unfocusedBorderColor = ServifyBorder,
                                    focusedContainerColor = ServifyInputBackground,
                                    unfocusedContainerColor = ServifyInputBackground
                                )
                            )
                        }

                        // Teléfono
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("TELÉFONO DE CONTACTO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.telefono,
                                onValueChange = { viewModel.onTelefonoChange(it) },
                                leadingIcon = { Icon(Icons.Default.Phone, null, tint = ServifyBluePrimary, modifier = Modifier.size(20.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ServifyBluePrimary,
                                    unfocusedBorderColor = ServifyBorder,
                                    focusedContainerColor = ServifyInputBackground,
                                    unfocusedContainerColor = ServifyInputBackground
                                )
                            )
                        }

                        // Dirección
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("DIRECCIÓN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.direccion,
                                onValueChange = { viewModel.onDireccionChange(it) },
                                placeholder = { Text("Ingresa tu dirección...", color = ServifyTextMuted, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = ServifyBluePrimary, modifier = Modifier.size(20.dp)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ServifyBluePrimary,
                                    unfocusedBorderColor = ServifyBorder,
                                    focusedContainerColor = ServifyInputBackground,
                                    unfocusedContainerColor = ServifyInputBackground
                                )
                            )
                        }

                        // Años de Experiencia (Exclusivo Trabajador)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("AÑOS DE EXPERIENCIA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.anosExperiencia,
                                onValueChange = { viewModel.onAnosChange(it) },
                                leadingIcon = { Icon(Icons.Default.Work, null, tint = ServifyBluePrimary, modifier = Modifier.size(20.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ServifyBluePrimary,
                                    unfocusedBorderColor = ServifyBorder,
                                    focusedContainerColor = ServifyInputBackground,
                                    unfocusedContainerColor = ServifyInputBackground
                                )
                            )
                        }

                        // Sobre Mí (Exclusivo Trabajador)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("SOBRE MÍ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                            OutlinedTextField(
                                value = uiState.biografia,
                                onValueChange = { viewModel.onBiografiaChange(it) },
                                placeholder = { Text("Cuéntales a los clientes quién eres y qué tipo de trabajos haces...", color = ServifyTextMuted, fontSize = 13.sp) },
                                minLines = 3,
                                maxLines = 6,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ServifyBluePrimary,
                                    unfocusedBorderColor = ServifyBorder,
                                    focusedContainerColor = ServifyInputBackground,
                                    unfocusedContainerColor = ServifyInputBackground
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.guardarCambios(token) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary, contentColor = Color.White)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onNavigateBack,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ServifyTextTitle, containerColor = ServifySurface),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ServifyBorder))
                ) {
                    Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}