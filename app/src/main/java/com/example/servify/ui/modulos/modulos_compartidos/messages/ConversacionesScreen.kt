package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.servify.data.SessionManager
import com.example.servify.ui.navigation.Rutas

@Composable
fun ConversacionesScreen(
    navController: NavController,
    viewModel: ConversacionesViewModel = viewModel()
) {
    val verde = Color(0xFF1B7B61)
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.cargar() }
    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Servify", color = verde, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        if (SessionManager.esTecnico) "TRABAJADOR" else "CLIENTE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text("Mensajes", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp)) }

            if (viewModel.isLoading && viewModel.conversaciones.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = verde)
                    }
                }
            } else if (viewModel.conversaciones.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes conversaciones. Se abren cuando un cliente acepta una propuesta.",
                            color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(viewModel.conversaciones, key = { it.id }) { c ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            navController.navigate(Rutas.Chat.crearRuta(c.id, c.otroNombre))
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(48.dp).background(Color(0xFFE2F3EE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    c.otroNombre.split(" ").filter { it.isNotBlank() }.take(2)
                                        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                                    color = verde, fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(c.otroNombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(horaDe(c.ultimoMensajeEn), fontSize = 11.sp, color = verde)
                                }
                                Text(c.solicitudTitulo, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    c.ultimoMensaje ?: "Sin mensajes todavía",
                                    fontSize = 13.sp, color = Color.DarkGray,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (c.noLeidos > 0) {
                                Spacer(Modifier.width(8.dp))
                                Box(Modifier.size(22.dp).background(verde, CircleShape), contentAlignment = Alignment.Center) {
                                    Text("${c.noLeidos}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}