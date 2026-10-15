package com.example.servify.ui.modulos.modulos_compartidos.actividad

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.modulos.cliente.contratacion.MisSolicitudesContenido

@Composable
fun ActividadScreen(
    navController: NavController,
    viewModel: ActividadViewModel = viewModel()
) {
    val verdeServify = Color(0xFF1B7B61)
    val esTecnico = SessionManager.esTecnico
    val context = LocalContext.current

    LaunchedEffect(esTecnico) {
        if (esTecnico) viewModel.cargarPostulaciones()
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
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
                Text("Servify", color = verdeServify, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = if (esTecnico) "TRABAJADOR" else "CLIENTE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
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
    val verdeServify = Color(0xFF1B7B61)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Text("Mis postulaciones", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("El estado de tus propuestas", fontSize = 13.sp, color = Color.Gray)
            }
        }

        if (viewModel.isLoading && viewModel.postulaciones.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verdeServify)
                }
            }
        } else if (viewModel.postulaciones.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no te has postulado a ningún trabajo", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(viewModel.postulaciones, key = { it.id }) { p ->
                val (texto, fondo, colorTexto) = when (p.estado) {
                    "aceptada" -> Triple("ACEPTADA", Color(0xFFE2F3EE), verdeServify)
                    "rechazada" -> Triple("NO SELECCIONADA", Color(0xFFFEE2E2), Color(0xFFDC2626))
                    else -> Triple("EN REVISIÓN", Color(0xFFE0E7FF), Color(0xFF4338CA))
                }

                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate("detalle_averia/${p.solicitudId}")
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
                            Text(
                                p.categoriaNombre.uppercase(),
                                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = verdeServify
                            )
                            Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    texto,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colorTexto
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(p.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tu propuesta: $%.2f".format(p.montoPropuesto),
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray
                        )
                        if (p.estado == "aceptada" && p.conversacionId != null) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "El cliente te aceptó. El chat estará disponible pronto.",
                                fontSize = 12.sp, color = verdeServify
                            )
                        }
                    }
                }
            }
        }
    }
}