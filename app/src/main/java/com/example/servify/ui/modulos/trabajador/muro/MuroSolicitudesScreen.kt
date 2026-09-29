package com.example.servify.ui.modulos.trabajador.muro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.data.model.SolicitudItemTecnico
import com.example.servify.ui.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuroSolicitudesScreen(
    navController: NavController
) {
    val verdeServify = Color(0xFF1B7B61)

    // Leemos el estado global de la sesión VIP
    val esVip = SessionManager.esVip

    var solicitudSeleccionadaParaPostular by remember { mutableStateOf<SolicitudItemTecnico?>(null) }

    var listaSolicitudes by remember {
        mutableStateOf(
            listOf(
                SolicitudItemTecnico(
                    id = 1,
                    clienteId = 101,
                    clienteNombre = "Ana M.",
                    categoriaNombre = "REPARACIÓN DE PC",
                    titulo = "Pantalla rota en Laptop",
                    descripcion = "Se me cayó la laptop (HP Pavilion) y la pantalla está estrellada pero el equipo enciende y se escuchan...",
                    distanciaKm = 3.5
                ),
                SolicitudItemTecnico(
                    id = 2,
                    clienteId = 102,
                    clienteNombre = "Carlos J.",
                    categoriaNombre = "REPARACIÓN DE PC",
                    titulo = "Pantalla rota en Laptop",
                    descripcion = "Se me cayó la laptop (HP Pavilion) y la pantalla está estrellada pero el equipo enciende y se escuchan...",
                    distanciaKm = 3.5
                ),
                SolicitudItemTecnico(
                    id = 3,
                    clienteId = 103,
                    clienteNombre = "Rosa P.",
                    categoriaNombre = "REPARACIÓN DE PC",
                    titulo = "Pantalla rota en Laptop",
                    descripcion = "Se me cayó la laptop (HP Pavilion) y la pantalla está estrellada pero el equipo enciende y se escuchan...",
                    distanciaKm = 3.5
                )
            )
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servify",
                    color = verdeServify,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón superior condicionado al estado VIP real
                    Surface(
                        onClick = {
                            if (!esVip) {
                                navController.navigate(Rutas.PlanesMembresia.ruta)
                            } else {
                                navController.navigate(Rutas.Perfil.ruta)
                            }
                        },
                        color = if (esVip) Color(0xFFFEF3C7) else Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (esVip) "¡Eres VIP! ⭐" else "PLAN PRO 👑",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (esVip) Color(0xFFD97706) else Color(0xFF4338CA)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "TRABAJADOR",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { navController.navigate(Rutas.Perfil.ruta) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                    Text(
                        text = "Solicitudes de Clientes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Nuevos trabajos en tu zona",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            if (listaSolicitudes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay solicitudes pendientes en tu zona",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(listaSolicitudes, key = { it.id }) { solicitud ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("detalle_averia/${solicitud.id}")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F1F1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = solicitud.clienteNombre.firstOrNull()?.uppercase() ?: "C",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = solicitud.clienteNombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = verdeServify,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "A ${solicitud.distanciaKm} km de ti",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFE2F3EE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = solicitud.categoriaNombre,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = verdeServify
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = solicitud.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = solicitud.descripcion,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        listaSolicitudes = listaSolicitudes.filter { it.id != solicitud.id }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color(0xFFF7F7F7),
                                        contentColor = Color.DarkGray
                                    ),
                                    border = null
                                ) {
                                    Text("Ignorar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = {
                                        solicitudSeleccionadaParaPostular = solicitud
                                    },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
                                ) {
                                    Text(
                                        text = "Postularme",
                                        fontSize = 13.sp,
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

        solicitudSeleccionadaParaPostular?.let { solicitud ->
            ModalPostularseSheet(
                tituloTrabajo = solicitud.titulo,
                onDismiss = { solicitudSeleccionadaParaPostular = null },
                onEnviarPostulacion = { precio, tiempo, mensaje ->
                    solicitudSeleccionadaParaPostular = null
                }
            )
        }
    }
}