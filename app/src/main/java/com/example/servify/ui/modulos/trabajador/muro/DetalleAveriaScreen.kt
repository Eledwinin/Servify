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
import com.example.servify.ui.theme.* // Importamos la paleta de colores oficial azul de Servify

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAveriaScreen(
    idSolicitud: Int,
    navController: NavController,
    onBackClick: () -> Unit,
    onPostularseClick: () -> Unit = {},
    viewModel: DetalleAveriaViewModel = viewModel()
) {
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
            Toast.makeText(context, "Postulación enviada con éxito", Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    val detalle = viewModel.detalle

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles de la Solicitud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = ServifyBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = ServifySurface) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { mostrarModal = true },
                        enabled = detalle != null && !detalle.yaPostulado,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ServifyBluePrimary,
                            disabledContainerColor = ServifyBorder
                        )
                    ) {
                        Text(
                            text = if (detalle?.yaPostulado == true) "Ya te postulaste" else "Postularme a este trabajo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        containerColor = ServifyBackground // Fondo azulado oficial #EBF8FC
    ) { padding ->
        if (viewModel.isLoading || detalle == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (viewModel.isLoading) CircularProgressIndicator(color = ServifyBluePrimary)
                else Text("No se pudo cargar la solicitud", color = ServifyTextMuted)
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
                // Etiqueta Solicitud Por
                Text(
                    text = "SOLICITUD POR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextMuted,
                    letterSpacing = 0.5.sp
                )

                // Tarjeta de información del cliente (Inspirada en Figma)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ServifySurface, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ServifyCianSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = detalle.clienteNombre.firstOrNull()?.uppercase() ?: "C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ServifyBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = detalle.clienteNombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ServifyTextTitle
                        )

                        // Calificación y reseñas del cliente
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = ServifyStarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val calificacionStr = detalle.clienteCalificacion?.let { "%.1f".format(it) } ?: "4.8"
                            Text(
                                text = "$calificacionStr (12 reseñas de trabajadores)",
                                fontSize = 12.sp,
                                color = ServifyTextMuted
                            )
                        }

                        Text(
                            text = "📅 Cliente desde 2023",
                            fontSize = 11.sp,
                            color = ServifyTextMuted
                        )
                    }
                }

                // Categoría y tiempo de publicación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ServifyCianSoft,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = detalle.categoriaNombre.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBluePrimary
                        )
                    }

                    Text(
                        text = "⏱ Publicado hace 2h",
                        fontSize = 12.sp,
                        color = ServifyTextMuted
                    )
                }

                // Tarjeta Principal del Problema (Título, Ubicación y Descripción)
                Card(
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = detalle.titulo,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ServifyTextTitle
                        )

                        // Caja de ubicación
                        Surface(
                            color = ServifyInputBackground,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ServifyBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = detalle.distanciaKm?.let { "Ciudad Central, a %.1f km".format(it) } ?: (detalle.direccionTexto ?: "Ubicación cercana"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ServifyTextTitle
                                )
                            }
                        }

                        Text(
                            text = detalle.descripcion,
                            fontSize = 14.sp,
                            color = ServifyTextMuted,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Galería de fotos con soporte de clic para ampliar
                if (detalle.fotos.isNotEmpty()) {
                    Text(
                        text = "Fotos del problema (${detalle.fotos.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ServifyTextTitle
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(detalle.fotos) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Foto del problema",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp, 120.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
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
                    .clickable { fotoExpandidaUrl = null },
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