package com.example.servify.ui.modulos.cliente.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.ui.navigation.Rutas
import com.google.android.gms.location.LocationServices
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val verdeServify = Color(0xFF1B7B61)

    remember {
        MapLibre.getInstance(context)
    }

    val listaTecnicos by homeViewModel.listaProfesionales.collectAsState()

    // La tarjeta inicia OCULTA (null) hasta presionar un pin
    var tecnicoSeleccionado by remember { mutableStateOf<ProfesionalPerfil?>(null) }

    // Ubicación GPS
    var userLat by remember { mutableStateOf(if (homeViewModel.latCliente != 0.0) homeViewModel.latCliente else 13.6929) }
    var userLng by remember { mutableStateOf(if (homeViewModel.lngCliente != 0.0) homeViewModel.lngCliente else -89.2182) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLng = location.longitude
                        homeViewModel.actualizarUbicacionCliente(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.cargarTecnicos()

        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLng = location.longitude
                        homeViewModel.actualizarUbicacionCliente(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            launcherPermisos.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val ubicacionCliente = LatLng(userLat, userLng)

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var mapLibreInstance by remember { mutableStateOf<MapLibreMap?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef?.onStop()
            mapViewRef?.onDestroy()
        }
    }

    LaunchedEffect(listaTecnicos, mapLibreInstance, userLat, userLng) {
        val map = mapLibreInstance ?: return@LaunchedEffect
        map.clear()

        // 1. Icono AZUL para la ubicación del usuario
        val iconoAzul = crearIconoTinte(context, android.R.drawable.ic_menu_myplaces, Color.Blue.toArgb())

        map.addMarker(
            MarkerOptions()
                .position(ubicacionCliente)
                .title("CLIENTE_MI_UBICACION")
                .icon(iconoAzul)
                .snippet("Tu posición actual")
        )

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(ubicacionCliente, 13.0)
        )

        // 2. Marcadores para los técnicos
        val mapaTecnicosPorId = mutableMapOf<String, ProfesionalPerfil>()

        listaTecnicos.forEach { tecnico ->
            val lat = tecnico.latitud ?: 0.0
            val lng = tecnico.longitud ?: 0.0

            if (lat != 0.0 && lng != 0.0) {
                val posTecnico = LatLng(lat, lng)
                val idTecnicoStr = tecnico.id.toString()
                mapaTecnicosPorId[idTecnicoStr] = tecnico

                map.addMarker(
                    MarkerOptions()
                        .position(posTecnico)
                        .title("TECNICO_$idTecnicoStr")
                        .snippet("${tecnico.nombreCompleto} - ${tecnico.oficio}")
                )
            }
        }

        map.setOnMarkerClickListener { markerClicked ->
            val titleMarker = markerClicked.title ?: ""

            if (titleMarker.startsWith("TECNICO_")) {
                val idExtraido = titleMarker.removePrefix("TECNICO_")
                val tecnicoEncontrado = mapaTecnicosPorId[idExtraido]
                if (tecnicoEncontrado != null) {
                    tecnicoSeleccionado = tecnicoEncontrado
                }
            } else if (titleMarker == "CLIENTE_MI_UBICACION") {
                tecnicoSeleccionado = null
            }
            true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Técnicos en el Mapa", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val estiloConCalles = "https://tiles.openfreemap.org/styles/liberty"

            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        onCreate(null)
                        onStart()
                        getMapAsync { map ->
                            map.setStyle(estiloConCalles) {
                                mapLibreInstance = map
                                map.addOnMapClickListener {
                                    tecnicoSeleccionado = null
                                    true
                                }
                            }
                        }
                        mapViewRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Tarjeta del técnico (Solo se muestra cuando tecnicoSeleccionado NO es null)
            val tecnicoAMostrar = tecnicoSeleccionado

            if (tecnicoAMostrar != null) {
                val iniciales = tecnicoAMostrar.nombreCompleto
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .ifEmpty { "PR" }

                val latTecnico = tecnicoAMostrar.latitud ?: 0.0
                val lngTecnico = tecnicoAMostrar.longitud ?: 0.0

                val distanciaMetros = homeViewModel.calcularDistanciaMetros(latTecnico, lngTecnico)
                val textoDistancia = if (distanciaMetros != Float.MAX_VALUE) {
                    if (distanciaMetros >= 1000) "%.1f km".format(distanciaMetros / 1000) else "%.0f m".format(distanciaMetros)
                } else null

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE2F3EE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iniciales,
                                    color = verdeServify,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tecnicoAMostrar.nombreCompleto,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = tecnicoAMostrar.oficio,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "%.1f".format(tecnicoAMostrar.calificacionPromedio),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (textoDistancia != null) {
                                Text(
                                    text = "📍 A $textoDistancia de ti",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = verdeServify
                                )
                            }

                            Button(
                                onClick = {
                                    navController.navigate(Rutas.DetalleTecnico.crearRuta(tecnicoAMostrar.id.toString()))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = verdeServify),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ver Perfil")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Funciones auxiliares para crear el icono azul de MapLibre
private fun crearIconoTinte(context: Context, resourceId: Int, tintColorARGB: Int): org.maplibre.android.annotations.Icon {
    val drawable = ContextCompat.getDrawable(context, resourceId)
        ?: throw IllegalArgumentException("No se pudo cargar el drawable")

    val bitmap = drawableToBitmap(drawable, tintColorARGB)
    return IconFactory.getInstance(context).fromBitmap(bitmap)
}

private fun drawableToBitmap(drawable: Drawable, tintColor: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
    drawable.draw(canvas)
    return bitmap
}