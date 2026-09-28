package com.example.servify.ui.modulos.cliente.profesional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.botones.BotonContratar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTecnicoScreen(
    idTecnico: Int,
    onBackClick: () -> Unit = {},
    onContratarClick: (Int) -> Unit,
    viewModel: DetalleTecnicoViewModel = viewModel()
) {
    // cuando entra a la pantalla pide los datos al bacekdn
    LaunchedEffect(idTecnico) {
        viewModel.cargarDetalleTecnico(idTecnico)
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Profesional") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            state.tecnico?.let { prof ->
                BotonContratar(
                    tarifa = "%.2f".format(prof.tarifaHora),
                    onContratarClick = { onContratarClick(prof.id) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {

                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                state.tecnico != null -> {
                    val tecnico = state.tecnico!!

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = tecnico.nombreCompleto,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${tecnico.oficio} • ${tecnico.anosExperiencia} años de exp.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "★ ${tecnico.calificacionPromedio} (${tecnico.totalResenas} reseñas)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        item {
                            HorizontalDivider()
                        }

                        item {
                            Text(
                                text = "Sobre mí",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tecnico.biografia.ifEmpty { "Sin descripción disponible." },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}