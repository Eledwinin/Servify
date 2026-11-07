package com.example.servify.ui.modulos.cliente.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.componentes.navegacion.TopBarPrincipal
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*
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

    val busqueda by viewModel.queryBusqueda.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val listaProfesionales by viewModel.listaProfesionales.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

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
                    SessionManager.actualizarUbicacion(location.latitude, location.longitude)
                    textoUbicacion = "Ubicación activa"
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
        containerColor = ServifyBackground,
        topBar = {
            TopBarConMenu(navController = navController, titulo = "Servify")
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Rutas.NuevaSolicitud.ruta) },
                containerColor = ServifyBluePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Solicitud")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Solicitud", fontWeight = FontWeight.Bold)
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
            // Estado Ubicación GPS
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
                            containerColor = if (tienePermisoUbicacion) ServifyDarkNavy else ServifyDanger
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

            // Banner + Buscador Dinámico (Azul Profundo Corporativo)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Encuentra a tu\nexperto ideal",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Miles de profesionales locales listos para ayudarte.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = { viewModel.onQueryBusquedaChange(it) },
                            placeholder = { Text("¿Qué servicio buscas hoy?", fontSize = 13.sp, color = ServifyTextPlaceholder) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ServifyBluePrimary) },
                            trailingIcon = {
                                if (busqueda.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onQueryBusquedaChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = ServifyTextMuted)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ServifySurface,
                                unfocusedContainerColor = ServifySurface,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = ServifyTextTitle,
                                unfocusedTextColor = ServifyTextBody
                            )
                        )
                    }
                }
            }

            // Carrusel Horizontal de Categorías
            item {
                val categoriasDisponibles = remember {
                    listOf("Plomería", "Electricidad", "Carpintería", "Jardinería", "Pintura", "Climatización")
                }

                Column {
                    Text("Categorías", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = categoriaSeleccionada == null,
                                onClick = { viewModel.seleccionarCategoria(null) },
                                label = { Text("Todas", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = ServifySurface,
                                    selectedContainerColor = ServifyBluePrimary,
                                    labelColor = ServifyTextBody,
                                    selectedLabelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = categoriaSeleccionada == null,
                                    borderColor = ServifyBorder,
                                    selectedBorderColor = ServifyBluePrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        items(categoriasDisponibles) { cat ->
                            val estaSeleccionada = categoriaSeleccionada == cat
                            FilterChip(
                                selected = estaSeleccionada,
                                onClick = { viewModel.seleccionarCategoria(cat) },
                                label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = ServifySurface,
                                    selectedContainerColor = ServifyBluePrimary,
                                    labelColor = ServifyTextBody,
                                    selectedLabelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = estaSeleccionada,
                                    borderColor = ServifyBorder,
                                    selectedBorderColor = ServifyBluePrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            // Encabezado "Más Cercanos" con Botón al Mapa
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Más Cercanos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)

                    FilterChip(
                        selected = false,
                        onClick = { navController.navigate(Rutas.Mapa.ruta) },
                        label = { Text("Ver en Mapa", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ver Mapa",
                                tint = ServifyBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ServifyCianSoft.copy(alpha = 0.5f),
                            labelColor = ServifyBluePrimary
                        ),
                        border = null,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Lista de Técnicos / Estados de Carga
            if (estaCargando) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ServifyBluePrimary)
                    }
                }
            } else if (mensajeError != null) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("No se pudieron cargar los técnicos", color = ServifyDanger, fontSize = 13.sp)
                        TextButton(onClick = { viewModel.cargarTecnicos() }) {
                            Text("Reintentar", color = ServifyBluePrimary)
                        }
                    }
                }
            } else if (listaProfesionales.isEmpty()) {
                item {
                    Text(
                        text = if (busqueda.isNotEmpty() || categoriaSeleccionada != null)
                            "No se encontraron técnicos para esta búsqueda."
                        else
                            "No hay técnicos registrados aún.",
                        color = ServifyTextMuted,
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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, ServifyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
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

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(prof.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ServifyTextTitle)
                                Text("${prof.anosExperiencia} años de experiencia", fontSize = 12.sp, color = ServifyTextMuted)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = ServifyInputBackground,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = prof.oficio,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ServifyTextBody
                                        )
                                    }

                                    if (textoDistancia != null) {
                                        Surface(
                                            color = ServifyCianSoft,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "📍 $textoDistancia",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ServifyBluePrimary
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
                                        tint = ServifyStarYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "%.1f".format(prof.calificacionPromedio),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ServifyTextTitle
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