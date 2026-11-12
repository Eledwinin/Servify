package com.example.servify.ui.modulos.trabajador.muro

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
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
import com.example.servify.data.SessionManager
import com.example.servify.data.model.SolicitudItemTecnico
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
fun MapaSolicitudesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    muroViewModel: MuroSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current

    remember {
        MapLibre.getInstance(context)
    }

    val listaSolicitudes = muroViewModel.solicitudes

    var solicitudSeleccionada by remember { mutableStateOf<SolicitudItemTecnico?>(null) }

    var userLat by remember { mutableStateOf(SessionManager.latitud ?: 13.6929) }
    var userLng by remember { mutableStateOf(SessionManager.longitud ?: -89.2182) }

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
                        SessionManager.actualizarUbicacion(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        muroViewModel.cargarSolicitudes()

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
                        SessionManager.actualizarUbicacion(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            launcherPermisos.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(muroViewModel.mensajeError) {
        muroViewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            muroViewModel.mensajeError = null
        }
    }
    LaunchedEffect(muroViewModel.mensajeExito) {
        muroViewModel.mensajeExito?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            muroViewModel.mensajeExito = null
        }
    }

    val ubicacionTecnico = LatLng(userLat, userLng)

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var mapLibreInstance by remember { mutableStateOf<MapLibreMap?>(null) }

    // Memorizar icono para evitar recrear Bitmaps en cada actualización de ubicación
    val iconoAzul = remember { crearIconoTinte(context, android.R.drawable.ic_menu_myplaces, ServifyBluePrimary.toArgb()) }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef?.onStop()
            mapViewRef?.onDestroy()
        }
    }

    LaunchedEffect(listaSolicitudes, mapLibreInstance, userLat, userLng) {
        val map = mapLibreInstance ?: return@LaunchedEffect
        map.clear()

        map.addMarker(
            MarkerOptions()
                .position(ubicacionTecnico)
                .title("TRABAJADOR_MI_UBICACION")
                .icon(iconoAzul)
                .snippet("Tu posición actual")
        )

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(ubicacionTecnico, 13.0)
        )

        val mapaSolicitudesPorId = mutableMapOf<String, SolicitudItemTecnico>()

        listaSolicitudes.forEach { solicitud ->
            val lat = solicitud.latitud ?: 0.0
            val lng = solicitud.longitud ?: 0.0

            if (lat != 0.0 && lng != 0.0) {
                val posSolicitud = LatLng(lat, lng)
                val idSolStr = solicitud.id.toString()
                mapaSolicitudesPorId[idSolStr] = solicitud

                map.addMarker(
                    MarkerOptions()
                        .position(posSolicitud)
                        .title("SOLICITUD_$idSolStr")
                        .snippet("${solicitud.titulo} - ${solicitud.categoriaNombre}")
                )
            }
        }

        map.setOnMarkerClickListener { markerClicked ->
            val titleMarker = markerClicked.title ?: ""

            if (titleMarker.startsWith("SOLICITUD_")) {
                val idExtraido = titleMarker.removePrefix("SOLICITUD_")
                val solicitudEncontrada = mapaSolicitudesPorId[idExtraido]
                if (solicitudEncontrada != null) {
                    solicitudSeleccionada = solicitudEncontrada
                }
            } else if (titleMarker == "TRABAJADOR_MI_UBICACION") {
                solicitudSeleccionada = null
            }
            true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Solicitudes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ServifyBluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
            )
        },
        containerColor = ServifyBackground
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
                                    solicitudSeleccionada = null
                                    true
                                }
                            }
                        }
                        mapViewRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            val solicitudAMostrar = solicitudSeleccionada

            if (solicitudAMostrar != null) {
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
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = ServifyBluePrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = solicitudAMostrar.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ServifyTextTitle
                                )
                                Text(
                                    text = "Cliente: ${solicitudAMostrar.clienteNombre}",
                                    fontSize = 12.sp,
                                    color = ServifyTextMuted
                                )
                            }

                            Surface(
                                color = ServifyInputBackground,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = solicitudAMostrar.categoriaNombre.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = solicitudAMostrar.descripcion,
                            fontSize = 12.sp,
                            color = ServifyTextMuted,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            solicitudAMostrar.distanciaKm?.let { dist ->
                                Text(
                                    text = "A %.1f km de ti".format(dist),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyBluePrimary
                                )
                            }

                            // Navegación directa a la pantalla completa de propuesta
                            Button(
                                onClick = {
                                    val tituloCodificado = java.net.URLEncoder.encode(solicitudAMostrar.titulo, "UTF-8")
                                    navController.navigate("enviar_propuesta/${solicitudAMostrar.id}/$tituloCodificado")
                                    solicitudSeleccionada = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Postularme ›")
                            }
                        }
                    }
                }
            }
        }
    }
}

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