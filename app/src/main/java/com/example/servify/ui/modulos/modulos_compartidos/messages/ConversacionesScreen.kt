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
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.componentes.navegacion.TopBarPrincipal
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

// Modelo para el registro de llamadas
data class RegistroLlamadaItem(
    val id: Int,
    val nombreUsuario: String,
    val tituloSolicitud: String,
    val esVideo: Boolean,
    val duracion: String,
    val fecha: String,
    val entrante: Boolean
)

@Composable
fun ConversacionesScreen(
    navController: NavController,
    viewModel: ConversacionesViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado para alternar entre Chats (0) y Llamadas (1)
    var pestañaActiva by remember { mutableStateOf(0) }
    var textoBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            TopBarConMenu(navController = navController,titulo = "Mensajería")
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
                    onClick = { pestañaActiva = 1 },
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
                // Seccion de Chats con barra de búsqueda
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
                // Sección de Registro de Llamadas
                val llamadasSimuladas = listOf(
                    RegistroLlamadaItem(1, "Ana Martínez", "Reparación de Computadora", true, "4 min 12 seg", "Hoy, 10:30 AM", false),
                    RegistroLlamadaItem(2, "Carlos Gómez", "Instalación de tomacorrientes", false, "1 min 45 seg", "Ayer, 03:15 PM", true),
                    RegistroLlamadaItem(3, "María López", "Limpieza Profunda Casa", true, "8 min 30 seg", "26 Sep", false)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(llamadasSimuladas, key = { it.id }) { llamada ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(ServifyCianSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (llamada.esVideo) Icons.Default.Videocam else Icons.Default.Call,
                                        contentDescription = null,
                                        tint = ServifyBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = llamada.nombreUsuario,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = ServifyTextTitle
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = llamada.tituloSolicitud,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ServifyBluePrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Duración: ${llamada.duracion} • ${llamada.fecha}",
                                        fontSize = 11.sp,
                                        color = ServifyTextMuted
                                    )
                                }

                                Surface(
                                    color = ServifyInputBackground,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (llamada.entrante) "Recibida" else "Realizada",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyTextMuted
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