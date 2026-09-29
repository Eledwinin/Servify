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
import androidx.navigation.NavController
import com.example.servify.ui.navigation.Rutas
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

data class Categoria(val nombre: String, val icono: String)
data class Trabajador(
    val id: String,
    val iniciales: String,
    val nombre: String,
    val rating: Double,
    val distancia: String,
    val categoria: String,
    val precioHora: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val verdeServify = Color(0xFF1B7B61)
    var busqueda by remember { mutableStateOf("") }

    // Estados para la ubicación
    var tienePermisoUbicacion by remember { mutableStateOf(false) }
    var textoUbicacion by remember { mutableStateOf("Obteniendo ubicación...") }

    // Cliente para obtener el GPS
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Función para obtener las coordenadas en tiempo real
    @SuppressLint("MissingPermission")
    fun obtenerCoordenadas() {
        textoUbicacion = "Obteniendo GPS..."
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    textoUbicacion = "GPS: %.2f, %.2f".format(lat, lng)
                } else {
                    textoUbicacion = "GPS sin señal"
                }
            }
            .addOnFailureListener {
                textoUbicacion = "Error GPS"
            }
    }

    // Launcher para pedir permisos en pantalla
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val concedido = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        tienePermisoUbicacion = concedido
        if (concedido) {
            obtenerCoordenadas()
        } else {
            textoUbicacion = "Permiso denegado"
        }
    }

    // Efecto inicial: solicita permiso automáticamente al entrar a la pantalla
    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            tienePermisoUbicacion = true
            obtenerCoordenadas()
        } else {
            // Esperamos un instante breve para asegurar que la vista cargó antes de pedir el permiso
            delay(300)
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val categorias = listOf(
        Categoria("Reparación PC", "💻"),
        Categoria("Electricidad", "⚡"),
        Categoria("Plomería", "🔧"),
        Categoria("Diseño", "🎨"),
        Categoria("Limpieza", "🧹"),
        Categoria("Mudanzas", "📦")
    )

    val trabajadores = listOf(
        Trabajador("1", "JP", "Juan Pérez", 4.9, "A 2.5 km de ti", "Electricidad", "$25/h"),
        Trabajador("2", "MG", "María Gómez", 4.7, "A 1.2 km de ti", "Plomería", "$30/h"),
        Trabajador("3", "CR", "Carlos Ruiz", 5.0, "A 3.0 km de ti", "Reparación de PC", "$40/h"),
        Trabajador("4", "AL", "Ana López", 4.8, "A 0.8 km de ti", "Limpieza", "$15/h"),
        Trabajador("5", "PS", "Pedro Sánchez", 4.1, "A 4.1 km de ti", "Mudanzas", "$20/h")
    )

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
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.Gray
                            )
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
            // Botón de Ubicación / Mapa
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = {
                            if (!tienePermisoUbicacion) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
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

            // Banner Verde + Buscador
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

            // Categorías
            item {
                Text("Categorías", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        categorias.take(3).forEach { cat ->
                            CategoriaItem(cat, Modifier.weight(1f))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        categorias.drop(3).take(3).forEach { cat ->
                            CategoriaItem(cat, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Mejor Valorados
            item {
                Text("Mejor Valorados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            items(trabajadores) { trabajador ->
                TrabajadorCard(
                    trabajador = trabajador,
                    onClick = {
                        navController.navigate(Rutas.DetalleTecnico.crearRuta(trabajador.id))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoriaItem(categoria: Categoria, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = categoria.icono, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = categoria.nombre,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun TrabajadorCard(trabajador: Trabajador, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    text = trabajador.iniciales,
                    color = Color(0xFF1B7B61),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(trabajador.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(trabajador.distancia, fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = trabajador.categoria,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
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
                        text = trabajador.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFFFB800)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = trabajador.precioHora,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}