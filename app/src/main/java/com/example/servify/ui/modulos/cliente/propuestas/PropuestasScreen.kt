package com.example.servify.ui.modulos.cliente.propuestas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.example.servify.data.model.PropuestaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropuestasScreen(
    solicitudId: Int,
    onBackClick: () -> Unit,
    viewModel: PropuestasViewModel = viewModel()
) {
    val verdeServify = Color(0xFF1B7B61)
    val context = LocalContext.current
    var propuestaACancelar by remember { mutableStateOf<PropuestaItem?>(null) }

    LaunchedEffect(solicitudId) { viewModel.cargar(solicitudId) }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }
    LaunchedEffect(viewModel.mensajeExito) {
        viewModel.mensajeExito?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeExito = null
        }
    }

    val hayAcuerdo = viewModel.propuestas.any { it.estado == "aceptada" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Propuestas recibidas", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (viewModel.isLoading && viewModel.propuestas.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = verdeServify)
                    }
                }
            } else if (viewModel.propuestas.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text("Todavía no hay propuestas para esta solicitud", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(viewModel.propuestas, key = { it.id }) { p ->
                    val esAceptada = p.estado == "aceptada"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esAceptada) Color(0xFFF0FAF6) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(p.tecnicoNombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                p.tecnicoCalificacion?.let {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                        Text("%.1f".format(it), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB800))
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Propone $%.2f".format(p.montoPropuesto),
                                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = verdeServify
                            )
                            val presupuesto = p.presupuestoEstimado
                            if (presupuesto != null && presupuesto != p.montoPropuesto) {
                                Text(
                                    "Tú pusiste $%.2f".format(presupuesto),
                                    fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (!p.mensaje.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(p.mensaje, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                            }

                            Spacer(Modifier.height(14.dp))

                            if (esAceptada) {
                                Surface(color = Color(0xFFE2F3EE), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        "Acuerdo activo",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = verdeServify
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { propuestaACancelar = p },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Cancelar acuerdo", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.rechazar(p.id) },
                                        enabled = !hayAcuerdo,
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) { Text("Rechazar", fontWeight = FontWeight.SemiBold) }

                                    Button(
                                        onClick = { viewModel.aceptar(p.id) },
                                        enabled = !hayAcuerdo,
                                        modifier = Modifier.weight(1.3f).height(44.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
                                    ) { Text("Aceptar", fontWeight = FontWeight.Bold, color = Color.White) }
                                }
                                if (hayAcuerdo) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Cancela el acuerdo actual para elegir a otro técnico",
                                        fontSize = 11.sp, color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    propuestaACancelar?.let { p ->
        AlertDialog(
            onDismissRequest = { propuestaACancelar = null },
            title = { Text("¿Cancelar el acuerdo?") },
            text = { Text("Se cerrará el chat con ${p.tecnicoNombre} y tu solicitud volverá a estar disponible para otros técnicos.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelarAcuerdo(p.id)
                    propuestaACancelar = null
                }) { Text("Sí, cancelar", color = Color(0xFFDC2626)) }
            },
            dismissButton = {
                TextButton(onClick = { propuestaACancelar = null }) { Text("Volver") }
            }
        )
    }
}