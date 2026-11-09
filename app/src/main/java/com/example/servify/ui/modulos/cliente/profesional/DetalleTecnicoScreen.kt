package com.example.servify.ui.modulos.cliente.profesional

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTecnicoScreen(
    idTecnico: Int,
    mostrarBotonContratar: Boolean = false, // 👈 Se agrega para controlar la visibilidad del botón
    onBackClick: () -> Unit = {},
    viewModel: DetalleTecnicoViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }
    var descripcionTexto by remember { mutableStateOf("") }

    LaunchedEffect(idTecnico) {
        viewModel.cargarDetalleTecnico(idTecnico)
    }

    LaunchedEffect(state.enviadoExitoso) {
        if (state.enviadoExitoso) {
            Toast.makeText(context, "Solicitud directa enviada al técnico", Toast.LENGTH_SHORT).show()
            viewModel.resetEstadoEnvio()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Perfil del Profesional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
            )
        },
        // 🚀 SI MOSTRARBOTONCONTRATAR ES FALSE, LA BARRA INFERIOR CON EL BOTÓN NO SE RENDERIZA
        bottomBar = {
            if (mostrarBotonContratar && state.tecnico != null) {
                Surface(
                    shadowElevation = 8.dp,
                    color = ServifySurface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { mostrarDialogo = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ServifyBluePrimary
                            ),
                            enabled = !state.enviandoSolicitud
                        ) {
                            if (state.enviandoSolicitud) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Solicitar Servicio",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = ServifyBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = ServifyBluePrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Error desconocido",
                        color = ServifyDanger,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                state.tecnico != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        PerfilTecnicoContenido(state.tecnico!!)
                    }
                }
            }
        }
    }

    // Modal para ingresar la descripción del trabajo y realizar la contratación directa
    if (mostrarDialogo && state.tecnico != null) {
        val tecnico = state.tecnico!!
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Solicitar Servicio a ${tecnico.nombreCompleto}") },
            text = {
                Column {
                    Text("Describe brevemente el trabajo que necesitas:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcionTexto,
                        onValueChange = { descripcionTexto = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Necesito reparar la tubería del baño") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (descripcionTexto.isNotBlank()) {
                            val categoriaIdReal = tecnico.categoriaId ?: 1
                            viewModel.enviarSolicitudDirecta(categoriaIdReal, descripcionTexto)
                            mostrarDialogo = false
                        }
                    }
                ) {
                    Text("Enviar Solicitud")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}