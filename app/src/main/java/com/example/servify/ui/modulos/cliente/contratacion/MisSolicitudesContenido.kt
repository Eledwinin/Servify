package com.example.servify.ui.modulos.cliente.contratacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@Composable
fun MisSolicitudesContenido(
    navController: NavController,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado para manejar las pestañas superiores
    var pestañaSeleccionada by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    // Clasificación de las solicitudes del cliente según el estado
    val solicitudesEnCurso = viewModel.solicitudes.filter {
        it.estado.equals("pendiente", ignoreCase = true) || it.estado.equals("en_proceso", ignoreCase = true)
    }

    val solicitudesHistorial = viewModel.solicitudes.filter {
        it.estado.equals("completado", ignoreCase = true) || it.estado.equals("cancelado", ignoreCase = true) || it.estado.equals("rechazado", ignoreCase = true)
    }

    val listaActual = if (pestañaSeleccionada == 0) solicitudesEnCurso else solicitudesHistorial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mis Trabajos",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ServifyTextTitle
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pestañas superiores tipo píldora corporativa
        TabRow(
            selectedTabIndex = pestañaSeleccionada,
            containerColor = ServifyInputBackground,
            contentColor = ServifyBluePrimary,
            indicator = {},
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Tab(
                selected = pestañaSeleccionada == 0,
                onClick = { pestañaSeleccionada = 0 },
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (pestañaSeleccionada == 0) ServifySurface else Color.Transparent),
                text = {
                    Text(
                        "En Curso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (pestañaSeleccionada == 0) ServifyBluePrimary else ServifyTextMuted
                    )
                }
            )
            Tab(
                selected = pestañaSeleccionada == 1,
                onClick = { pestañaSeleccionada = 1 },
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (pestañaSeleccionada == 1) ServifySurface else Color.Transparent),
                text = {
                    Text(
                        "Historial",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (pestañaSeleccionada == 1) ServifyBluePrimary else ServifyTextMuted
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (viewModel.isLoading && viewModel.solicitudes.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ServifyBluePrimary)
                    }
                }
            } else if (listaActual.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (pestañaSeleccionada == 0) "No tienes solicitudes en curso" else "No hay historial de solicitudes",
                            color = ServifyTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(listaActual, key = { it.id }) { s ->
                    val (textoEstado, colorFondo, colorTexto) = when (s.estado.lowercase()) {
                        "pendiente" -> Triple("PUBLICADA", ServifyCianSoft, ServifyBluePrimary)
                        "en_proceso" -> Triple("EN PROCESO", ServifyPendingBg, ServifyPendingText)
                        "completado" -> Triple("COMPLETADA", ServifyCianSoft, ServifyBluePrimary)
                        else -> Triple(s.estado.uppercase(), ServifyInputBackground, ServifyTextMuted)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Rutas.PropuestasSolicitud.crearRuta(s.id))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ServifyInputBackground,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = s.categoriaNombre.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyTextMuted
                                    )
                                }
                                Surface(
                                    color = colorFondo,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = textoEstado,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorTexto
                                    )
                                }
                            }

                            Text(
                                text = s.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )

                            Text(
                                text = s.descripcion,
                                fontSize = 13.sp,
                                color = ServifyTextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = when (s.totalPropuestas) {
                                    0 -> "Sin propuestas todavía"
                                    1 -> "1 propuesta recibida"
                                    else -> "${s.totalPropuestas} propuestas recibidas"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (s.totalPropuestas > 0) ServifyBluePrimary else ServifyTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}