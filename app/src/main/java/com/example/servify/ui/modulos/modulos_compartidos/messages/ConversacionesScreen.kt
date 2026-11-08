package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
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
import com.example.servify.data.model.LlamadaItem
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@Composable
fun ConversacionesScreen(
    navController: NavController,
    viewModel: ConversacionesViewModel = viewModel(),
    llamadasViewModel: LlamadasHistorialViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado para alternar entre Chats (0) y Llamadas (1)
    var pestañaActiva by remember { mutableStateOf(0) }
    var textoBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargar()
        llamadasViewModel.cargar()
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            TopBarConMenu(navController = navController, titulo = "Mensajería")
        },
        containerColor = ServifyBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Pestañas superiores estilo píldora corporativa
            TabRow(
                selectedTabIndex = pestañaActiva,
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
                    selected = pestañaActiva == 0,
                    onClick = { pestañaActiva = 0 },
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (pestañaActiva == 0) ServifySurface else Color.Transparent),
                    text = {
                        Text(
                            "Chats",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (pestañaActiva == 0) ServifyBluePrimary else ServifyTextMuted
                        )
                    }
                )
                Tab(
                    selected = pestañaActiva == 1,
                    onClick = {
                        pestañaActiva = 1
                        llamadasViewModel.cargar() // Recargar historial al abrir pestaña
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (pestañaActiva == 1) ServifySurface else Color.Transparent),
                    text = {
                        Text(
                            "Llamadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (pestañaActiva == 1) ServifyBluePrimary else ServifyTextMuted
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido dinámico según la pestaña
            if (pestañaActiva == 0) {
                // Sección de Chats con barra de búsqueda
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Buscar conversación...", color = ServifyTextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ServifyTextMuted) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ServifySurface,
                        unfocusedContainerColor = ServifySurface,
                        disabledContainerColor = ServifySurface,
                        focusedBorderColor = ServifyBluePrimary,
                        unfocusedBorderColor = ServifyBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                val conversacionesFiltradas = viewModel.conversaciones.filter {
                    it.otroNombre.contains(textoBusqueda, ignoreCase = true) ||
                            it.solicitudTitulo.contains(textoBusqueda, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (viewModel.isLoading && viewModel.conversaciones.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = ServifyBluePrimary)
                            }
                        }
                    } else if (conversacionesFiltradas.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "Aún no tienes conversaciones activas",
                                    color = ServifyTextMuted,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(conversacionesFiltradas, key = { it.id }) { c ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(Rutas.Chat.crearRuta(c.id, c.otroNombre))
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ServifySurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(ServifyCianSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = c.otroNombre.split(" ").filter { it.isNotBlank() }.take(2)
                                                .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                                            color = ServifyBluePrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = c.otroNombre,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = ServifyTextTitle
                                            )
                                            Text(
                                                text = horaDe(c.ultimoMensajeEn),
                                                fontSize = 11.sp,
                                                color = ServifyTextMuted
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = c.solicitudTitulo,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ServifyBluePrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = c.ultimoMensaje ?: "Sin mensajes todavía",
                                            fontSize = 13.sp,
                                            color = ServifyTextMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (c.noLeidos > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(ServifyBluePrimary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${c.noLeidos}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Sección de Registro de Llamadas reales obtenidas del Backend
                val miId = SessionManager.usuarioActual?.id ?: 0

                if (llamadasViewModel.isLoading && llamadasViewModel.llamadas.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ServifyBluePrimary)
                    }
                } else if (llamadasViewModel.llamadas.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay registros de llamadas recientes",
                            color = ServifyTextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(llamadasViewModel.llamadas, key = { it.id }) { llamada ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ServifySurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                FilaLlamada(l = llamada, miId = miId) {
                                    navController.navigate(Rutas.Chat.crearRuta(llamada.conversacionId, llamada.otroNombre))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente reutilizable para cada fila del historial de llamadas
@Composable
fun FilaLlamada(l: LlamadaItem, miId: Int, onClick: () -> Unit) {
    val saliente = l.emisorId == miId
    val fallida = l.estado in listOf("perdida", "rechazada", "cancelada")

    // Colores e iconos dinámicos según el estado
    val colorEstado = when (l.estado) {
        "completada" -> Color(0xFF0D9488) // Esmeralda si fue exitosa
        "rechazada" -> Color(0xFFEF4444)  // Rojo si fue rechazada
        "cancelada" -> Color(0xFFF59E0B)  // Naranja si fue cancelada
        else -> Color(0xFFEF4444)         // Rojo para perdidas
    }

    val estadoTexto = when (l.estado) {
        "completada" -> "Duración: %02d:%02d".format((l.duracionSeg ?: 0) / 60, (l.duracionSeg ?: 0) % 60)
        "rechazada" -> "Llamada rechazada"
        "cancelada" -> "Llamada cancelada"
        else -> "Llamada perdida"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con iniciales o icono flotante moderno
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (fallida && !saliente) Color(0xFFFEE2E2) else ServifyCianSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = l.otroNombre.split(" ").filter { it.isNotBlank() }.take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                color = if (fallida && !saliente) Color(0xFFEF4444) else ServifyBluePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Textos descriptivos principales
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = l.otroNombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ServifyTextTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Icono chiquito indicando si fue entrante/saliente y voz/video
                Icon(
                    imageVector = if (l.esVideo == 1) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = null,
                    tint = colorEstado,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${if (saliente) "Saliente" else "Entrante"} · $estadoTexto",
                    fontSize = 12.sp,
                    color = colorEstado,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Hora alineada a la derecha
        Text(
            text = horaDe(l.iniciadaEn),
            fontSize = 11.sp,
            color = ServifyTextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}