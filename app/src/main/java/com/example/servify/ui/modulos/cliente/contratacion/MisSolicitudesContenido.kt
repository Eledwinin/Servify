package com.example.servify.ui.modulos.cliente.contratacion

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.servify.data.SessionManager
import com.example.servify.data.model.MiSolicitud
import com.example.servify.ui.componentes.feedback.ServifyPullToRefreshBox
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

private data class OpcionFiltroSolicitud(
    val clave: String,
    val etiqueta: String
)

private val FILTROS_SOLICITUD_EN_CURSO = listOf(
    OpcionFiltroSolicitud("TODAS", "Todas"),
    OpcionFiltroSolicitud("PUBLICADA", "Publicadas"),
    OpcionFiltroSolicitud("EN_PROCESO", "En proceso"),
    OpcionFiltroSolicitud("POR_CERRAR", "Por cerrar")
)

private val FILTROS_SOLICITUD_HISTORIAL = listOf(
    OpcionFiltroSolicitud("TODAS", "Todas"),
    OpcionFiltroSolicitud("COMPLETADA", "Completadas"),
    OpcionFiltroSolicitud("CANCELADA", "Canceladas")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesContenido(
    navController: NavController,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current

    var filtroEstado by remember { mutableStateOf("TODAS") }
    var pestañaSeleccionada by remember { mutableStateOf(0) }
    var solicitudParaCerrar by remember { mutableStateOf<MiSolicitud?>(null) }

    val userId = SessionManager.usuarioActual?.id ?: 0
    val prefs = remember(userId) {
        context.getSharedPreferences("servify_directas_tab_vistas_$userId", Context.MODE_PRIVATE)
    }
    var clavesVistasTab by remember(userId) {
        mutableStateOf(prefs.getStringSet("claves_vistas", emptySet())?.toSet() ?: emptySet())
    }

    fun claveVista(s: MiSolicitud): String = "${s.id}_${s.estado.lowercase()}"

    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(pestañaSeleccionada) {
        filtroEstado = "TODAS"
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    fun esSolicitudDirecta(s: MiSolicitud): Boolean {
        return s.titulo.startsWith("Contratación Directa", ignoreCase = true) ||
                s.estado.equals("directa", ignoreCase = true)
    }

    val solicitudesDirectas = remember(viewModel.solicitudes) {
        viewModel.solicitudes.filter { esSolicitudDirecta(it) }
    }
    val directasRespondidas = remember(solicitudesDirectas) {
        solicitudesDirectas.filter {
            it.estado.lowercase() in listOf("rechazado", "rechazada", "aceptado", "aceptada")
        }
    }
    val directasNoVistasCount = remember(directasRespondidas, clavesVistasTab) {
        directasRespondidas.count { !clavesVistasTab.contains(claveVista(it)) }
    }

    // Al abrir la pestaña Directas (índice 2), marca todas las respondidas como vistas y limpia el badge
    LaunchedEffect(pestañaSeleccionada, directasRespondidas) {
        if (pestañaSeleccionada == 2 && directasRespondidas.isNotEmpty()) {
            val nuevoSet = HashSet(clavesVistasTab).apply {
                directasRespondidas.forEach { add(claveVista(it)) }
            }
            prefs.edit().putStringSet("claves_vistas", nuevoSet).commit()
            clavesVistasTab = nuevoSet
        }
    }

    val solicitudesEnCurso = remember(viewModel.solicitudes) {
        viewModel.solicitudes.filter { s ->
            !esSolicitudDirecta(s) && (
                    s.estado.equals("pendiente", ignoreCase = true) ||
                            s.estado.equals("en_proceso", ignoreCase = true) ||
                            s.estado.equals("esperando_pago", ignoreCase = true) ||
                            s.estado.equals("pendiente_pago", ignoreCase = true) ||
                            s.estado.equals("en_cierre", ignoreCase = true)
                    )
        }
    }

    val solicitudesHistorial = remember(viewModel.solicitudes) {
        viewModel.solicitudes.filter { s ->
            !esSolicitudDirecta(s) && (
                    s.estado.equals("completado", ignoreCase = true) ||
                            s.estado.equals("completada", ignoreCase = true) ||
                            s.estado.equals("cancelado", ignoreCase = true) ||
                            s.estado.equals("cancelada", ignoreCase = true) ||
                            s.estado.equals("rechazado", ignoreCase = true) ||
                            s.estado.equals("rechazada", ignoreCase = true)
                    )
        }
    }

    val basePestana = when (pestañaSeleccionada) {
        0 -> solicitudesEnCurso
        1 -> solicitudesHistorial
        else -> solicitudesDirectas
    }

    val listaActual = remember(basePestana, pestañaSeleccionada, filtroEstado) {
        if (pestañaSeleccionada == 2 || filtroEstado == "TODAS") {
            basePestana
        } else {
            basePestana.filter { s ->
                val est = s.estado.lowercase()
                when (filtroEstado) {
                    "PUBLICADA" -> est == "pendiente"
                    "EN_PROCESO" -> est == "en_proceso"
                    "POR_CERRAR" -> est in setOf("esperando_pago", "pendiente_pago", "en_cierre")
                    "COMPLETADA" -> est in setOf("completado", "completada")
                    "CANCELADA" -> est in setOf("cancelado", "cancelada", "rechazado", "rechazada")
                    else -> true
                }
            }
        }
    }

    val opcionesFiltroActual = when (pestañaSeleccionada) {
        0 -> FILTROS_SOLICITUD_EN_CURSO
        1 -> FILTROS_SOLICITUD_HISTORIAL
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

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
                        fontSize = 12.sp,
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
                        fontSize = 12.sp,
                        color = if (pestañaSeleccionada == 1) ServifyBluePrimary else ServifyTextMuted
                    )
                }
            )
            Tab(
                selected = pestañaSeleccionada == 2,
                onClick = { pestañaSeleccionada = 2 },
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (pestañaSeleccionada == 2) ServifySurface else Color.Transparent),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Directas ✉️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (pestañaSeleccionada == 2) ServifyBluePrimary else ServifyTextMuted
                        )
                        if (directasNoVistasCount > 0 && pestañaSeleccionada != 2) {
                            Badge(
                                containerColor = ServifyDanger,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = directasNoVistasCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }

        if (opcionesFiltroActual.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(opcionesFiltroActual, key = { it.clave }) { opcion ->
                    val seleccionado = filtroEstado == opcion.clave
                    FilterChip(
                        selected = seleccionado,
                        onClick = { filtroEstado = opcion.clave },
                        label = {
                            Text(
                                text = opcion.etiqueta,
                                fontSize = 12.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ServifyCianSoft,
                            selectedLabelColor = ServifyBluePrimary,
                            containerColor = ServifySurface,
                            labelColor = ServifyTextMuted
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ServifyPullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refrescar() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
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
                                text = when {
                                    filtroEstado != "TODAS" -> "No hay solicitudes con ese estado"
                                    pestañaSeleccionada == 0 -> "No tienes solicitudes en curso"
                                    pestañaSeleccionada == 1 -> "No hay historial de solicitudes"
                                    else -> "No has enviado contrataciones directas"
                                },
                                color = ServifyTextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(listaActual, key = { it.id }) { s ->
                        val estadoLower = s.estado.lowercase()
                        val esDirectaRechazada = pestañaSeleccionada == 2 && estadoLower in listOf("rechazada", "rechazado")

                        val (textoEstado, colorFondo, colorTexto) = when (estadoLower) {
                            "aceptada", "aceptado" -> Triple("ACEPTADA", ServifyCianSoft, ServifyBluePrimary)
                            "rechazada", "rechazado" -> Triple(
                                if (pestañaSeleccionada == 2) "NO ACEPTADA" else "RECHAZADA",
                                ServifyDangerBg,
                                ServifyDanger
                            )
                            "cancelado", "cancelada" -> Triple("CANCELADO", ServifyDangerBg, ServifyDanger)
                            "directa", "pendiente" -> Triple(if (pestañaSeleccionada == 2) "PENDIENTE" else "PUBLICADA", ServifyPendingBg, ServifyPendingText)
                            "en_proceso" -> Triple("EN PROCESO", ServifyCianSoft, ServifyBluePrimary)
                            "esperando_pago", "pendiente_pago", "en_cierre" -> Triple("ESPERANDO ESCANEO QR", ServifyPendingBg, ServifyPendingText)
                            "completado", "completada" -> Triple("COMPLETADA", ServifyCianSoft, ServifyBluePrimary)
                            else -> Triple(s.estado.uppercase(), ServifyInputBackground, ServifyTextMuted)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when {
                                        estadoLower in listOf("completado", "completada") -> {
                                            val idTecnico = s.tecnicoId ?: 1
                                            if (idTecnico > 0) {
                                                navController.navigate(Rutas.CrearResena.crearRuta(s.id, idTecnico))
                                            } else {
                                                Toast.makeText(context, "No se encontró el técnico asociado", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        estadoLower in listOf("en_proceso", "aceptada", "aceptado", "esperando_pago", "pendiente_pago", "en_cierre") -> {
                                            navController.navigate(Rutas.PropuestasSolicitud.crearRuta(s.id))
                                        }

                                        estadoLower in listOf("cancelado", "cancelada", "rechazado", "rechazada") -> {
                                            // Solicitud rechazada o dada de baja: no navega a propuestas
                                        }

                                        else -> {
                                            val idTecnico = s.tecnicoId
                                            if (idTecnico != null && idTecnico > 0) {
                                                navController.navigate(Rutas.DetalleTecnico.crearRuta(idTecnico))
                                            } else {
                                                navController.navigate(Rutas.PropuestasSolicitud.crearRuta(s.id))
                                            }
                                        }
                                    }
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

                                val partesDesc = s.descripcion.split("[Dada de baja por Administración — Motivo:")
                                val descripcionLimpia = partesDesc[0].trim()
                                val motivoBajaAdmin = if (partesDesc.size > 1) {
                                    partesDesc[1].removeSuffix("]").trim()
                                } else {
                                    null
                                }

                                if (descripcionLimpia.isNotBlank()) {
                                    Text(
                                        text = descripcionLimpia,
                                        fontSize = 13.sp,
                                        color = ServifyTextMuted,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 18.sp
                                    )
                                }

                                if (!motivoBajaAdmin.isNullOrBlank()) {
                                    Surface(
                                        color = Color(0xFFFEF2F2),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "⚠️ Dada de baja por Administración",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                            Text(
                                                text = "Motivo: $motivoBajaAdmin",
                                                fontSize = 12.sp,
                                                color = Color(0xFF7F1D1D),
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }

                                if (esDirectaRechazada) {
                                    Surface(
                                        color = Color(0xFFFEF2F2),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            val nombreTec = s.tecnicoNombre?.takeIf { it.isNotBlank() } ?: "El técnico"
                                            Text(
                                                text = "❌ $nombreTec no aceptó la solicitud",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                            if (!s.motivoRechazo.isNullOrBlank()) {
                                                Text(
                                                    text = "Motivo: ${s.motivoRechazo}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF7F1D1D),
                                                    lineHeight = 17.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (pestañaSeleccionada == 2) {
                                            when (estadoLower) {
                                                "aceptada", "aceptado" -> "¡El técnico aceptó la contratación!"
                                                "rechazada", "rechazado" -> ""
                                                else -> "Esperando respuesta del técnico..."
                                            }
                                        } else if (pestañaSeleccionada == 1) {
                                            ""
                                        } else {
                                            when (s.totalPropuestas) {
                                                0 -> "Sin propuestas todavía"
                                                1 -> "1 propuesta recibida"
                                                else -> "${s.totalPropuestas} propuestas recibidas"
                                            }
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (pestañaSeleccionada == 2 && estadoLower in listOf("aceptada", "aceptado")) ServifyBluePrimary else ServifyTextMuted,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (estadoLower in listOf("en_proceso", "aceptada", "aceptado", "esperando_pago", "pendiente_pago", "en_cierre")) {
                                        Button(
                                            onClick = { solicitudParaCerrar = s },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                if (estadoLower in listOf("esperando_pago", "pendiente_pago", "en_cierre")) "Mostrar QR" else "Finalizar Trabajo",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
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

    solicitudParaCerrar?.let { solicitud ->
        DialogoCierreCliente(
            solicitudId = solicitud.id,
            tecnicoId = solicitud.tecnicoId,
            onDismissRequest = {
                solicitudParaCerrar = null
                viewModel.limpiarEstadoCierre()
            },
            onConfirmarPago = { metodoPago ->
                if (metodoPago == "tarjeta") {
                    val idActual = solicitud.id
                    solicitudParaCerrar = null
                    viewModel.limpiarEstadoCierre()
                    navController.navigate(Rutas.CheckoutPago.crearRuta(idActual))
                } else {
                    viewModel.iniciarCierreSolicitud(solicitud.id, "efectivo", solicitud.tecnicoId)
                }
            },
            onCalificarClick = {
                val idSolFinal = solicitud.id
                val idTecnicoBruto = solicitud.tecnicoId ?: viewModel.tecnicoIdParaCalificar ?: 0
                val idTecnicoFinal = if (idTecnicoBruto > 0) idTecnicoBruto else 1

                solicitudParaCerrar = null
                viewModel.limpiarEstadoCierre()

                if (idSolFinal > 0) {
                    navController.navigate(Rutas.CrearResena.crearRuta(idSolFinal, idTecnicoFinal))
                } else {
                    Toast.makeText(context, "Error al identificar la solicitud", Toast.LENGTH_SHORT).show()
                }
            },
            qrToken = viewModel.qrTokenGenerado,
            cargando = viewModel.cargandoCierre
        )
    }
}