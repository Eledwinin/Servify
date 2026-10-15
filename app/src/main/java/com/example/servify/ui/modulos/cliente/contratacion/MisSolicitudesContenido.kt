package com.example.servify.ui.modulos.cliente.contratacion

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.navigation.Rutas

@Composable
fun MisSolicitudesContenido(
    navController: NavController,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val verdeServify = Color(0xFF1B7B61)
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Text("Mis solicitudes", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Toca una para ver las propuestas", fontSize = 13.sp, color = Color.Gray)
            }
        }

        if (viewModel.isLoading && viewModel.solicitudes.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verdeServify)
                }
            }
        } else if (viewModel.solicitudes.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no has publicado solicitudes", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(viewModel.solicitudes, key = { it.id }) { s ->
                val (textoEstado, colorFondo, colorTexto) = when (s.estado) {
                    "pendiente" -> Triple("PUBLICADA", Color(0xFFE0E7FF), Color(0xFF4338CA))
                    "en_proceso" -> Triple("EN PROCESO", Color(0xFFFEF3C7), Color(0xFFD97706))
                    "completado" -> Triple("COMPLETADA", Color(0xFFE2F3EE), verdeServify)
                    else -> Triple(s.estado.uppercase(), Color(0xFFEEEEEE), Color.DarkGray)
                }

                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Rutas.PropuestasSolicitud.crearRuta(s.id))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(s.categoriaNombre.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = verdeServify)
                            Surface(color = colorFondo, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    textoEstado,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colorTexto
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(s.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            s.descripcion, fontSize = 12.sp, color = Color.DarkGray,
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = when (s.totalPropuestas) {
                                0 -> "Sin propuestas todavía"
                                1 -> "1 propuesta recibida"
                                else -> "${s.totalPropuestas} propuestas recibidas"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (s.totalPropuestas > 0) verdeServify else Color.Gray
                        )
                    }
                }
            }
        }
    }
}