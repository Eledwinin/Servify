package com.example.servify.ui.modulos.cliente.home

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.servify.ui.theme.*
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
    latDestino: Double? = null,
    lngDestino: Double? = null,
    tituloDestino: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    remember {
        MapLibre.getInstance(context)
    }

    val listaTecnicos by homeViewModel.listaProfesionales.collectAsState()

    // La tarjeta inicia OCULTA (null) hasta presionar un pin de técnico (si estamos en modo normal)
    var tecnicoSeleccionado by remember { mutableStateOf<ProfesionalPerfil?>(null) }

    // Definición de coordenadas iniciales (Si viene de una solicitud, usa eso; si no, usa el ViewModel o por defecto)
    val initialLat = latDestino ?: if (homeViewModel.latCliente != 0.0) homeViewModel.latCliente else 13.6929
    val initialLng = lngDestino ?: if (homeViewModel.lngCliente != 0.0) homeViewModel.lngCliente else -89.2182

    var userLat by remember { mutableStateOf(initialLat) }
    var userLng by remember { mutableStateOf(initialLng) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido && latDestino == null) {
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
        if (latDestino == null) {
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
    }

    val ubicacionPrincipal = LatLng(userLat, userLng)

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var mapLibreInstance by remember { mutableStateOf<MapLibreMap?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef?.onStop()
            mapViewRef?.onDestroy()
        }
    }

    LaunchedEffect(listaTecnicos, mapLibreInstance, userLat, userLng, latDestino, lngDestino) {
        val map = mapLibreInstance ?: return@LaunchedEffect
        map.clear()

        if (latDestino != null && lngDestino != null) {
            // CASO A: Venimos desde los detalles de una Solicitud -> Mostrar pin de la avería
            // Cambia esta línea en tu MapaScreen.kt:
            val iconoRojo = crearIconoTinte(context, android.R.drawable.ic_menu_mapmode, android.graphics.Color.RED)
            map.addMarker(
                MarkerOptions()
                    .position(ubicacionPrincipal)
                    .title(tituloDestino ?: "Ubicación del Trabajo")
                    .icon(iconoRojo)
                    .snippet("Punto exacto de la solicitud")
            )
        } else {
            // CASO B: Modo normal (Home) -> Mostrar ubicación del usuario y técnicos cercanos
            val iconoAzul = crearIconoTinte(context, android.R.drawable.ic_menu_myplaces, Color.Blue.toArgb())

            map.addMarker(
                MarkerOptions()
                    .position(ubicacionPrincipal)
                    .title("CLIENTE_MI_UBICACION")
                    .icon(iconoAzul)
                    .snippet("Tu posición actual")
            )

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
                            .snippet("${tecnico.nombreCompleto}\n${tecnico.oficio}")
                    )
                }
            }

            map.setOnMarkerClickListener { markerClicked ->
                val titleMarker = markerClicked.title ?: ""

                if (titleMarker.startsWith("TECNICO_")) {
                    val idExtraido = titleMarker.removePrefix("TECNICO_")
                    mapaTecnicosPorId[idExtraido]?.let { tecnicoEncontrado ->
                        tecnicoSeleccionado = tecnicoEncontrado
                    }
                } else if (titleMarker == "CLIENTE_MI_UBICACION") {
                    tecnicoSeleccionado = null
                }
                true
            }
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionPrincipal, 15.0))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (latDestino != null) "Ubicación de la Solicitud" else "Técnicos en el Mapa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = ServifyBluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
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

            // Tarjeta flotante del técnico (Solo si estamos en modo normal y se seleccionó un pin)
            val tecnicoAMostrar = tecnicoSeleccionado
            if (latDestino == null && tecnicoAMostrar != null) {
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
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ServifyCianSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iniciales,
                                    color = ServifyBluePrimary,
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
                                    color = ServifyTextTitle
                                )
                                Text(
                                    text = tecnicoAMostrar.oficio,
                                    fontSize = 12.sp,
                                    color = ServifyTextMuted
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = ServifyStarYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "%.1f".format(tecnicoAMostrar.calificacionPromedio),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ServifyTextTitle
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
                                    color = ServifyBluePrimary
                                )
                            }

                            Button(
                                onClick = {
                                    navController.navigate(Rutas.DetalleTecnico.crearRuta(tecnicoAMostrar.id.toString()))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ver Perfil", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Funciones auxiliares para crear iconos en MapLibre
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