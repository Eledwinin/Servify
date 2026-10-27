package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAveriaScreen(
    idSolicitud: Int,
    navController: NavController,
    onBackClick: () -> Unit,
    onPostularseClick: () -> Unit = {},
    viewModel: DetalleAveriaViewModel = viewModel()
) {
    val verdeServify = Color(0xFF1B7B61)
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var mostrarModal by remember { mutableStateOf(false) }

    // Estado para controlar la foto seleccionada que se abrirá en pantalla completa
    var fotoExpandidaUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idSolicitud) { viewModel.cargar(idSolicitud) }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    LaunchedEffect(viewModel.postulacionExitosa) {
        if (viewModel.postulacionExitosa) {
            Toast.makeText(context, "Postulación enviada", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    val detalle = viewModel.detalle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Solicitud", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { mostrarModal = true },
                        enabled = detalle != null && !detalle.yaPostulado,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
                    ) {
                        Text(
                            text = if (detalle?.yaPostulado == true) "Ya te postulaste" else "Postularme a este trabajo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (viewModel.isLoading || detalle == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (viewModel.isLoading) CircularProgressIndicator(color = verdeServify)
                else Text("No se pudo cargar la solicitud", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(46.dp).clip(CircleShape).background(Color(0xFFE2F3EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = verdeServify)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(detalle.clienteNombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        detalle.clienteCalificacion?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("%.1f".format(it), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = detalle.distanciaKm?.let { "A %.1f km de ti".format(it) } ?: "Distancia no disponible",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = Color(0xFFE2F3EE), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = detalle.categoriaNombre.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = verdeServify
                                )
                            }
                            detalle.presupuestoEstimado?.let {
                                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "Presupuesto: $%.2f".format(it),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }

                        Text(detalle.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(detalle.descripcion, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)

                        if (!detalle.direccionTexto.isNullOrBlank()) {
                            Text("📍 ${detalle.direccionTexto}", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                // Galería de fotos con soporte de clic para ampliar
                if (detalle.fotos.isNotEmpty()) {
                    Text("Fotos del problema (${detalle.fotos.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(detalle.fotos) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Foto del problema",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        // Asignamos la URL para abrir el visor en grande
                                        fotoExpandidaUrl = url
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarModal && detalle != null) {
        ModalPostularseSheet(
            tituloTrabajo = detalle.titulo,
            onDismiss = { mostrarModal = false },
            onEnviarPostulacion = { precio, tiempo, mensaje ->
                mostrarModal = false
                viewModel.postular(precio, tiempo, mensaje)
            }
        )
    }

    // Visor de imagen en pantalla completa (Dialog emergente)
    fotoExpandidaUrl?.let { url ->
        Dialog(
            onDismissRequest = { fotoExpandidaUrl = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { fotoExpandidaUrl = null }, // Tocar el fondo oscuro cierra el visor
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                // Botón superior derecho para cerrar con comodidad
                IconButton(
                    onClick = { fotoExpandidaUrl = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}