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
import com.example.servify.data.model.MiSolicitud
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@Composable
fun MisSolicitudesContenido(
    navController: NavController,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current

    var pestañaSeleccionada by remember { mutableStateOf(0) }
    var solicitudParaCerrar by remember { mutableStateOf<MiSolicitud?>(null) }

    LaunchedEffect(Unit) { viewModel.cargar() }

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

    val solicitudesDirectas = viewModel.solicitudes.filter { esSolicitudDirecta(it) }

    val solicitudesEnCurso = viewModel.solicitudes.filter { s ->
        !esSolicitudDirecta(s) && (
                s.estado.equals("pendiente", ignoreCase = true) ||
                        s.estado.equals("en_proceso", ignoreCase = true) ||
                        s.estado.equals("esperando_pago", ignoreCase = true) ||
                        s.estado.equals("pendiente_pago", ignoreCase = true) ||
                        s.estado.equals("en_cierre", ignoreCase = true)
                )
    }

    val solicitudesHistorial = viewModel.solicitudes.filter { s ->
        !esSolicitudDirecta(s) && (
                s.estado.equals("completado", ignoreCase = true) ||
                        s.estado.equals("completada", ignoreCase = true) ||
                        s.estado.equals("cancelado", ignoreCase = true) ||
                        s.estado.equals("cancelada", ignoreCase = true) ||
                        s.estado.equals("rechazado", ignoreCase = true) ||
                        s.estado.equals("rechazada", ignoreCase = true)
                )
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
                    Text(
                        "Directas ✉️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (pestañaSeleccionada == 2) ServifyBluePrimary else ServifyTextMuted
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val listaActual = when (pestañaSeleccionada) {
            0 -> solicitudesEnCurso
            1 -> solicitudesHistorial
            else -> solicitudesDirectas
        }

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
                            text = when (pestañaSeleccionada) {
                                0 -> "No tienes solicitudes en curso"
                                1 -> "No hay historial de solicitudes"
                                else -> "No has enviado contrataciones directas"
                            },
                            color = ServifyTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(listaActual, key = { it.id }) { s ->
                    val (textoEstado, colorFondo, colorTexto) = when (s.estado.lowercase()) {
                        "aceptada", "aceptado" -> Triple("ACEPTADA", ServifyCianSoft, ServifyBluePrimary)
                        "rechazada", "rechazado" -> Triple("RECHAZADA", ServifyDangerBg, ServifyDanger)
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
                                // 🌟 NAVEGACIÓN INTELIGENTE AL PERFIL DEL TÉCNICO O A SUS PROPUESTAS:
                                val idTecnico = s.tecnicoId
                                if (idTecnico != null && idTecnico > 0) {
                                    navController.navigate(Rutas.DetalleTecnico.crearRuta(idTecnico))
                                } else {
                                    navController.navigate(Rutas.PropuestasSolicitud.crearRuta(s.id))
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

                            Text(
                                text = s.descripcion,
                                fontSize = 13.sp,
                                color = ServifyTextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (pestañaSeleccionada == 2) {
                                        when (s.estado.lowercase()) {
                                            "aceptada", "aceptado" -> "¡El técnico aceptó la contratación!"
                                            "rechazada", "rechazado" -> "El técnico rechazó la solicitud"
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
                                    color = if (pestañaSeleccionada == 2 && s.estado.lowercase() in listOf("aceptada", "aceptado")) ServifyBluePrimary else ServifyTextMuted,
                                    modifier = Modifier.weight(1f)
                                )

                                if (s.estado.lowercase() in listOf("en_proceso", "aceptada", "aceptado", "esperando_pago", "pendiente_pago", "en_cierre")) {
                                    Button(
                                        onClick = { solicitudParaCerrar = s },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            if (s.estado.lowercase() in listOf("esperando_pago", "pendiente_pago", "en_cierre")) "Mostrar QR" else "Finalizar Trabajo",
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

    // 🚀 MODAL DE CIERRE CON RESCATE DE ID DE TÉCNICO Y NAVEGACIÓN GARANTIZADA
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

                // Rescatamos el técnicoId desde la solicitud o el ViewModel (si viniera 0/null asigna 1 de salvavidas)
                val idTecnicoBruto = solicitud.tecnicoId ?: viewModel.tecnicoIdParaCalificar ?: 0
                val idTecnicoFinal = if (idTecnicoBruto > 0) idTecnicoBruto else 1

                // 1. Ocultar modal y limpiar
                solicitudParaCerrar = null
                viewModel.limpiarEstadoCierre()

                // 2. Navegar directamente sin bloqueos ni errores
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