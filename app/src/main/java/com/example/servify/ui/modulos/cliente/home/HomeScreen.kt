package com.example.servify.ui.modulos.cliente.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.navigation.Rutas
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val verdeServify = Color(0xFF1B7B61)

    var busqueda by remember { mutableStateOf("") }

    val listaProfesionales by viewModel.listaProfesionales.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    // Ubicación GPS
    var tienePermisoUbicacion by remember { mutableStateOf(false) }
    var textoUbicacion by remember { mutableStateOf("Obteniendo ubicación...") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    @SuppressLint("MissingPermission")
    fun obtenerCoordenadas() {
        textoUbicacion = "Obteniendo GPS..."
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.actualizarUbicacionCliente(location.latitude, location.longitude)
                    textoUbicacion = "GPS: %.2f, %.2f".format(location.latitude, location.longitude)
                } else {
                    textoUbicacion = "GPS sin señal"
                }
            }
            .addOnFailureListener { textoUbicacion = "Error GPS" }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val concedido = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        tienePermisoUbicacion = concedido
        if (concedido) obtenerCoordenadas() else textoUbicacion = "Permiso denegado"
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            tienePermisoUbicacion = true
            obtenerCoordenadas()
        } else {
            delay(300)
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
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
                Text(
                    text = "Servify",
                    color = verdeServify,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "CLIENTE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { navController.navigate(Rutas.Perfil.ruta) }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ubicación GPS
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = {
                            if (!tienePermisoUbicacion) {
                                permissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                )
                            } else {
                                obtenerCoordenadas()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tienePermisoUbicacion) Color(0xFF1E1E1E) else Color(0xFFD32F2F)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = if (tienePermisoUbicacion) Icons.Default.LocationOn else Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (tienePermisoUbicacion) textoUbicacion else "Activar Permiso",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = verdeServify)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Encuentra a tu\nexperto ideal",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Miles de profesionales locales listos para ayudarte.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            placeholder = { Text("¿Qué servicio buscas hoy?", fontSize = 13.sp, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Sección Técnicos
            item {
                Text("Más Cercanos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (estaCargando) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = verdeServify)
                    }
                }
            } else if (mensajeError != null) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("No se pudieron cargar los técnicos", color = Color.Red, fontSize = 13.sp)
                        TextButton(onClick = { viewModel.cargarTecnicos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            } else if (listaProfesionales.isEmpty()) {
                item {
                    Text(
                        text = "No hay técnicos registrados aún.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(listaProfesionales) { prof ->
                    val iniciales = prof.nombreCompleto
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.uppercase() }
                        .joinToString("")
                        .ifEmpty { "PR" }

                    val distanciaMetros = viewModel.calcularDistanciaMetros(prof.latitud, prof.longitud)
                    val textoDistancia = if (distanciaMetros != Float.MAX_VALUE) {
                        if (distanciaMetros >= 1000) "%.1f km".format(distanciaMetros / 1000) else "%.0f m".format(distanciaMetros)
                    } else null

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Rutas.DetalleTecnico.crearRuta(prof.id.toString()))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE2F3EE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iniciales,
                                    color = Color(0xFF1B7B61),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(prof.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${prof.anosExperiencia} años de experiencia", fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = Color(0xFFEEEEEE),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = prof.oficio,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    if (textoDistancia != null) {
                                        Surface(
                                            color = Color(0xFFE2F3EE),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "📍 $textoDistancia",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = verdeServify
                                            )
                                        }
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "%.1f".format(prof.calificacionPromedio),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFFB800)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}