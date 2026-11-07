package com.example.servify.ui.modulos.modulos_compartidos.actividad

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.componentes.navegacion.TopBarPrincipal
import com.example.servify.ui.modulos.cliente.contratacion.MisSolicitudesContenido
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.* // Importamos la paleta de colores azul corporativa

@Composable
fun ActividadScreen(
    navController: NavController,
    viewModel: ActividadViewModel = viewModel()
) {
    val esTecnico = SessionManager.esTecnico
    val context = LocalContext.current

    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permiso otorgado o denegado por el usuario */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!tienePermiso) {
                launcherPermisos.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(esTecnico) {
        if (esTecnico) viewModel.cargarPostulaciones(context)
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            TopBarConMenu(navController = navController,
                titulo = if (esTecnico) "Mis Trabajos" else "Mis Solicitudes")
        },
        containerColor = ServifyBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (esTecnico) {
                PostulacionesTecnico(navController, viewModel)
            } else {
                MisSolicitudesContenido(navController = navController)
            }
        }
    }
}

@Composable
private fun PostulacionesTecnico(
    navController: NavController,
    viewModel: ActividadViewModel
) {
    // Estado para controlar las pestañas superiores
    var pestañaSeleccionada by remember { mutableStateOf(0) } // 0 = En Curso, 1 = Historial

    // Clasificación de postulaciones según diseño Figma
    val postulacionesEnCurso = viewModel.postulaciones.filter {
        it.estado.equals("aceptada", ignoreCase = true) || it.estado.equals("revision", ignoreCase = true) || it.estado.equals("pendiente", ignoreCase = true)
    }

    val postulacionesHistorial = viewModel.postulaciones.filter {
        it.estado.equals("rechazada", ignoreCase = true) || it.estado.equals("finalizada", ignoreCase = true) || it.estado.equals("cancelada", ignoreCase = true)
    }

    val listaActual = if (pestañaSeleccionada == 0) postulacionesEnCurso else postulacionesHistorial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Pestañas superiores tipo píldora
        TabRow(
            selectedTabIndex = pestañaSeleccionada,
            containerColor = ServifyInputBackground,
            contentColor = ServifyBluePrimary,
            indicator = {}, // Sin indicador predeterminado para usar diseño de superficies
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

        // Contenido de la lista según la pestaña activa
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (viewModel.isLoading && viewModel.postulaciones.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ServifyBluePrimary)
                    }
                }
            } else if (listaActual.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (pestañaSeleccionada == 0) "No tienes trabajos en curso" else "No hay historial de trabajos",
                            color = ServifyTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(listaActual, key = { it.id }) { p ->
                    val (textoEstado, fondoEstado, colorTextoEstado) = when (p.estado.lowercase()) {
                        "aceptada" -> Triple("EN PROGRESO", ServifyCianSoft, ServifyBluePrimary)
                        "rechazada" -> Triple("NO SELECCIONADA", ServifyDangerBg, ServifyDanger)
                        else -> Triple("EN REVISIÓN", ServifyPendingBg, ServifyPendingText)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("detalle_averia/${p.solicitudId}")
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
                                        text = p.categoriaNombre.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyTextMuted
                                    )
                                }
                                Surface(
                                    color = fondoEstado,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = textoEstado,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorTextoEstado
                                    )
                                }
                            }

                            Text(
                                text = p.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )

                            Text(
                                text = "Tu propuesta: $%.2f".format(p.montoPropuesto),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ServifyTextMuted
                            )

                            // Botón de acción si está aceptada (Ir al Chat o Ver detalles)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (p.estado.lowercase() == "aceptada" && p.conversacionId != null) {
                                    Button(
                                        onClick = { navController.navigate(Rutas.Chat.crearRuta(p.conversacionId, "Cliente")) },
                                        modifier = Modifier.weight(1f).height(42.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                                    ) {
                                        Text("Ir al chat", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { navController.navigate("detalle_averia/${p.solicitudId}") },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = ServifyInputBackground,
                                        contentColor = ServifyBluePrimary
                                    ),
                                    border = null
                                ) {
                                    Text("Ver detalles ›", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}