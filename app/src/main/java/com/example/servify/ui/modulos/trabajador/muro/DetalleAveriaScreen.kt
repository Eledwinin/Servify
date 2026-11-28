package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.servify.ui.componentes.indicadores.AvatarUsuario
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAveriaScreen(
    idSolicitud: Int,
    navController: NavController,
    onBackClick: () -> Unit,
    viewModel: DetalleAveriaViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
                            tint = ServifyBlueLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = ServifySurface) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            detalle?.let { item ->
                                val tituloCodificado = java.net.URLEncoder.encode(item.titulo, "UTF-8")
                                val montoQuery = item.presupuestoEstimado ?: 0.0
                                navController.navigate("enviar_propuesta/${item.id}/$tituloCodificado?presupuesto=$montoQuery")
                            }
                        },
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
        containerColor = ServifyBackground
    ) { padding ->
        if (viewModel.isLoading || detalle == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (viewModel.isLoading) CircularProgressIndicator(color = ServifyBlueLight)
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
                Text(
                    text = "SOLICITUD POR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextMuted,
                    letterSpacing = 0.5.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ServifySurface)
                        .clickable {
                            navController.navigate(Rutas.PerfilCliente.crearRuta(detalle.clienteId))
                        }
                        .padding(16.dp)
                ) {
                    AvatarUsuario(
                        nombre = detalle.clienteNombre,
                        fotoUrl = detalle.clienteFoto,
                        tamano = 50.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = detalle.clienteNombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ServifyTextTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = ServifyStarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            val calificacion = detalle.clienteCalificacion ?: 0.0
                            val totalResenas = detalle.clienteTotalResenas ?: 0

                            val textoResenas = if (calificacion > 0.0) {
                                "%.1f ($totalResenas reseñas)".format(calificacion)
                            } else {
                                "Sin calificaciones"
                            }

                            Text(
                                text = textoResenas,
                                fontSize = 12.sp,
                                color = ServifyTextMuted
                            )
                        }

                        // Días siendo cliente
                        val diasCliente = viewModel.calcularDiasCliente(detalle.clienteCreadoEn)
                        val textoCliente = if (diasCliente <= 0) "Nuevo en Servify" else "$diasCliente días siendo cliente de Servify"

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = ServifyTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = textoCliente,
                                fontSize = 12.sp,
                                color = ServifyTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver perfil ›",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ServifyCianSoft.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = detalle.categoriaNombre.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBlueLight
                        )
                    }

                    Text(
                        text = viewModel.tiempoRelativo(detalle.creadoEn),
                        fontSize = 12.sp,
                        color = ServifyTextMuted
                    )
                }

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

                        // Caja de ubicación con el botón interactivo hacia el Mapa interno
                        Surface(
                            color = ServifyInputBackground,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = ServifyBlueLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    val textoUbicacion = when {
                                        !detalle.direccionTexto.isNullOrBlank() -> detalle.direccionTexto
                                        detalle.distanciaKm != null -> "Ubicación a %.1f km".format(detalle.distanciaKm)
                                        else -> "Ubicación exacta registrada"
                                    }

                                    Text(
                                        text = textoUbicacion!!,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ServifyTextTitle,
                                        maxLines = 2
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        val lat = detalle.latitud ?: 13.6929
                                        val lng = detalle.longitud ?: -89.2182
                                        val titulo = detalle.titulo ?: "Solicitud"
                                        navController.navigate(Rutas.Mapa.crearRuta(lat, lng, titulo))
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = null,
                                        tint = ServifyBlueLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ver mapa",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyBlueLight
                                    )
                                }
                            }
                        }
                        Text(
                            text = detalle.descripcion,
                            fontSize = 14.sp,
                            color = ServifyTextMuted,
                            lineHeight = 22.sp
                        )
                        val presupuesto = detalle.presupuestoEstimado
                        HorizontalDivider(color = ServifyBorder.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Presupuesto estimado:",
                                fontSize = 13.sp,
                                color = ServifyTextMuted
                            )
                            Text(
                                text = if (presupuesto != null && presupuesto > 0.0) {
                                    "$%.2f".format(java.util.Locale.US, presupuesto)
                                } else {
                                    "A convenir"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ServifyTextTitle
                            )
                        }
                    }
                }

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
                                    .clickable { fotoExpandidaUrl = url }
                            )
                        }
                    }
                }
            }
        }
    }

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
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = { fotoExpandidaUrl = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }
}