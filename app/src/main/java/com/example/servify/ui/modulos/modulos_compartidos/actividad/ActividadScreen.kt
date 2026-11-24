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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.data.model.MiPostulacion
import com.example.servify.ui.componentes.feedback.ServifyPullToRefreshBox
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.modulos.cliente.contratacion.MisSolicitudesContenido
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@Composable
fun ActividadScreen(
    navController: NavController,
    viewModel: ActividadViewModel = viewModel()
) {
    val esTecnico = SessionManager.esTecnico
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    DisposableEffect(lifecycleOwner, esTecnico) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && esTecnico) {
                viewModel.cargarPostulaciones(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
            TopBarConMenu(
                navController = navController,
                titulo = if (esTecnico) "Mis Trabajos" else "Mis Solicitudes"
            )
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

private val ESTADOS_HISTORIAL_POSTULACION = setOf(
    "rechazada", "finalizada", "cancelada", "completada", "completado"
)

private val ESTADOS_HISTORIAL_SOLICITUD = setOf(
    "completado", "completada", "finalizada", "cancelada", "cancelado"
)

private val ESTADOS_COMPLETADO = setOf(
    "completado", "completada", "finalizada"
)

private val ESTADOS_CANCELADO = setOf(
    "cancelado", "cancelada"
)

private fun esPostulacionDeHistorial(p: MiPostulacion): Boolean {
    val estPost = p.estado.lowercase()
    val estSol = p.estadoSolicitud?.lowercase().orEmpty()
    return estPost in ESTADOS_HISTORIAL_POSTULACION || estSol in ESTADOS_HISTORIAL_SOLICITUD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostulacionesTecnico(
    navController: NavController,
    viewModel: ActividadViewModel
) {
    val context = LocalContext.current
    var pestañaSeleccionada by remember { mutableStateOf(0) } // 0 = En Curso, 1 = Historial

    val postulacionesHistorial = remember(viewModel.postulaciones) {
        viewModel.postulaciones.filter { esPostulacionDeHistorial(it) }
    }

    val postulacionesEnCurso = remember(viewModel.postulaciones) {
        viewModel.postulaciones.filter { !esPostulacionDeHistorial(it) }
    }

    val listaActual = if (pestañaSeleccionada == 0) postulacionesEnCurso else postulacionesHistorial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                containerColor = ServifyInputBackground,
                contentColor = ServifyBluePrimary,
                indicator = {},
                divider = {},
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Tab(
                    selected = pestañaSeleccionada == 0,
                    onClick = {
                        pestañaSeleccionada = 0
                        viewModel.cargarPostulaciones(context)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (pestañaSeleccionada == 0) ServifySurface else Color.Transparent),
                    text = {
                        Text(
                            "En Curso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (pestañaSeleccionada == 0) ServifyBlueLight else ServifyTextMuted
                        )
                    }
                )
                Tab(
                    selected = pestañaSeleccionada == 1,
                    onClick = {
                        pestañaSeleccionada = 1
                        viewModel.cargarPostulaciones(context)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (pestañaSeleccionada == 1) ServifySurface else Color.Transparent),
                    text = {
                        Text(
                            "Historial",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (pestañaSeleccionada == 1) ServifyBlueLight else ServifyTextMuted
                        )
                    }
                )
            }

            IconButton(
                onClick = { viewModel.cargarPostulaciones(context) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ServifySurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar lista",
                    tint = ServifyBlueLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ServifyPullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refrescarPostulaciones(context) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
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
                    val estPost = p.estado.lowercase()
                    val estSol = p.estadoSolicitud?.lowercase().orEmpty()
                    val estaCompletado = estSol in ESTADOS_COMPLETADO || estPost in ESTADOS_COMPLETADO
                    val estaCancelado = estSol in ESTADOS_CANCELADO || estPost == "cancelada"

                    val (textoEstado, fondoEstado, colorTextoEstado) = when {
                        estaCompletado -> Triple("COMPLETADO", ServifyCianSoft, ServifyBluePrimary)
                        estaCancelado -> Triple("CANCELADA", ServifyDangerBg, ServifyDanger)
                        estPost == "rechazada" -> Triple("NO SELECCIONADA", ServifyDangerBg, ServifyDanger)
                        estSol == "esperando_pago" -> Triple("ESPERANDO PAGO", ServifyPendingBg, ServifyPendingText)
                        estPost == "aceptada" -> Triple("EN PROGRESO", ServifyCianSoft, ServifyBluePrimary)
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (estPost == "aceptada" && !estaCompletado && !estaCancelado && p.conversacionId != null) {
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
                                        contentColor = ServifyBlueLight
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
}