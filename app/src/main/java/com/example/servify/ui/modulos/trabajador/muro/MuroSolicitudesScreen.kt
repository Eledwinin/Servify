package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.ui.componentes.navegacion.TopBarConMenu
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuroSolicitudesScreen(
    navController: NavController,
    viewModel: MuroSolicitudesViewModel = viewModel()
) {
    val esVip = SessionManager.esVip
    val context = LocalContext.current
    val listaSolicitudes = viewModel.solicitudes
    val contratacionesDirectas = viewModel.contratacionesDirectas

    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var estaDisponible by remember { mutableStateOf(true) }
    var mostrarModalDirectas by remember { mutableStateOf(false) }

    // 📷 LAUNCHER PARA ESCANEO DE CÓDIGO QR CON CÁMARA (ZXING)
    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            // Se leyó exitosamente el QR, enviamos al backend para validar
            viewModel.validarQrCliente(result.contents)
        } else {
            Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { viewModel.cargarSolicitudes() }

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

    val solicitudesFiltradas = remember(listaSolicitudes, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") {
            listaSolicitudes
        } else {
            listaSolicitudes.filter {
                it.categoriaNombre.equals(categoriaSeleccionada, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarConMenu(
                navController = navController,
                titulo = "Servify",
                accionesDerecha = {
                    // Botón de Sobrecito (Contrataciones Directas) ✉️
                    BadgedBox(
                        badge = {
                            if (contratacionesDirectas.isNotEmpty()) {
                                Badge(
                                    containerColor = ServifyDanger,
                                    contentColor = Color.White
                                ) {
                                    Text(contratacionesDirectas.size.toString())
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { mostrarModalDirectas = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Solicitudes Directas",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Botón de Membresía VIP
                    Surface(
                        onClick = {
                            if (!esVip) {
                                navController.navigate(Rutas.PlanesMembresia.ruta)
                            } else {
                                navController.navigate(Rutas.Perfil.ruta)
                            }
                        },
                        color = if (esVip) ServifyPendingBg else Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (esVip) "¡Eres VIP! ⭐" else "PLAN PRO 👑",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (esVip) ServifyPendingText else Color(0xFF4338CA)
                        )
                    }
                }
            )
        },
        containerColor = ServifyBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TARJETA DE GANANCIAS
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GANANCIAS (SEMANA ACTUAL)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyBlueLight,
                                letterSpacing = 0.5.sp
                            )

                            Surface(
                                color = if (estaDisponible) ServifyOnlineGreen.copy(alpha = 0.2f) else Color(0xFF334155),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { estaDisponible = !estaDisponible }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (estaDisponible) ServifyOnlineGreen else ServifyTextMuted)
                                    )
                                    Text(
                                        text = if (estaDisponible) "Disponible" else "Ocupado",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$340.00",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = ServifyOnlineGreen, modifier = Modifier.size(14.dp))
                                    Text("14 Trabajos", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                }
                            }

                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Star, null, tint = ServifyStarYellow, modifier = Modifier.size(14.dp))
                                    Text("4.9 (48)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // 2. ACCESOS DIRECTOS RÁPIDOS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BotonAccesoRapido("Mi Agenda", Icons.Default.ViewAgenda, Modifier.weight(1f)) { }

                    // 📷 BOTÓN ESCANEAR QR ACTIVADO
                    BotonAccesoRapido("Escanear QR", Icons.Default.QrCodeScanner, Modifier.weight(1f)) {
                        val options = ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Escanea el código QR del cliente para confirmar el cierre")
                            setCameraId(0) // Usar cámara trasera
                            setBeepEnabled(true)
                            setBarcodeImageEnabled(false)
                            setOrientationLocked(false)
                        }
                        qrScanLauncher.launch(options)
                    }

                    BotonAccesoRapido("Reportes", Icons.Default.LocalOffer, Modifier.weight(1f)) { }
                }
            }

            // 3. ENCABEZADO Y BOTÓN MAPA
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Solicitudes Disponibles",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextTitle
                        )
                        Text(
                            text = "Nuevos trabajos en tu zona",
                            fontSize = 12.sp,
                            color = ServifyTextMuted
                        )
                    }

                    FilterChip(
                        selected = false,
                        onClick = { navController.navigate(Rutas.MapaSolicitudes.ruta) },
                        label = { Text("Ver Mapa", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ver Mapa",
                                tint = ServifyBluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ServifyCianSoft,
                            labelColor = ServifyBluePrimary
                        ),
                        border = null,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // 4. CHIPS DE FILTRADO POR CATEGORÍA
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categorias = listOf("Todas", "PC/Tech", "Electricidad", "Plomería", "Aire Acondicionado")
                    categorias.forEach { cat ->
                        FilterChip(
                            selected = categoriaSeleccionada == cat,
                            onClick = { categoriaSeleccionada = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = ServifySurface,
                                selectedContainerColor = ServifyBluePrimary,
                                labelColor = ServifyTextMuted,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = categoriaSeleccionada == cat,
                                borderColor = ServifyBorder,
                                selectedBorderColor = ServifyBluePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 5. LISTA DE SOLICITUDES PÚBLICAS
            if (viewModel.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ServifyBluePrimary)
                    }
                }
            } else if (solicitudesFiltradas.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes disponibles en esta categoría", color = ServifyTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(solicitudesFiltradas, key = { it.id }) { solicitud ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("detalle_averia/${solicitud.id}") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(CircleShape).background(ServifyCianSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = solicitud.clienteNombre.firstOrNull()?.uppercase() ?: "C",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = ServifyBluePrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = solicitud.clienteNombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = ServifyTextTitle
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = ServifyBluePrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = solicitud.distanciaKm?.let { "A %.1f km de ti".format(it) } ?: "Cercano",
                                            fontSize = 11.sp,
                                            color = ServifyTextMuted
                                        )
                                    }
                                }

                                Surface(color = ServifyInputBackground, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = solicitud.categoriaNombre.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = solicitud.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = solicitud.descripcion,
                                fontSize = 13.sp,
                                color = ServifyTextMuted,
                                lineHeight = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Presupuesto estimado:", fontSize = 12.sp, color = ServifyTextMuted)
                                Text("$45.00", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ServifyTextTitle)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.ignorar(solicitud.id) },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = ServifyInputBackground,
                                        contentColor = ServifyTextMuted
                                    ),
                                    border = null
                                ) {
                                    Text("Ignorar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = {
                                        val tituloCodificado = java.net.URLEncoder.encode(solicitud.titulo, "UTF-8")
                                        navController.navigate("enviar_propuesta/${solicitud.id}/$tituloCodificado")
                                    },
                                    modifier = Modifier.weight(1.3f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                                ) {
                                    Text(
                                        text = "Enviar Postulación ›",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal para revisar Solicitudes Directas (Sobrecito ✉️ con SCROLL)
    if (mostrarModalDirectas) {
        AlertDialog(
            onDismissRequest = { mostrarModalDirectas = false },
            title = {
                Text(
                    text = "Contrataciones Directas Recibidas ✉️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                if (contratacionesDirectas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No tienes solicitudes directas registradas.", fontSize = 13.sp, color = ServifyTextMuted)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                    ) {
                        items(contratacionesDirectas, key = { it.id }) { item ->
                            ItemContratacionDirecta(
                                contratacion = item,
                                onAceptar = { viewModel.responderContratacion(item.id, "aceptado") },
                                onRechazar = { viewModel.responderContratacion(item.id, "rechazado") },
                                onEliminar = { viewModel.eliminarContratacionDirectaLocal(item.id) },
                                onIrAlChat = {
                                    mostrarModalDirectas = false
                                    navController.navigate("chat/${item.id}")
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarModalDirectas = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

// Componentes Auxiliares
@Composable
fun BotonAccesoRapido(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = ServifySurface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ServifyCianSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = ServifyBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = titulo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ItemContratacionDirecta(
    contratacion: ContratacionDirectaResponse,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onEliminar: () -> Unit,
    onIrAlChat: () -> Unit
) {
    val esAceptada = contratacion.estado.equals("aceptado", ignoreCase = true) ||
            contratacion.estado.equals("aceptada", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(ServifyCianSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contratacion.cliente_nombre.firstOrNull()?.uppercase() ?: "C",
                            fontWeight = FontWeight.Bold,
                            color = ServifyBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = contratacion.cliente_nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = contratacion.categoria_nombre, fontSize = 11.sp, color = ServifyTextMuted)
                    }
                }

                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar solicitud",
                        tint = ServifyDanger,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = contratacion.descripcion, fontSize = 13.sp, color = ServifyTextTitle)
            Spacer(modifier = Modifier.height(10.dp))

            if (esAceptada) {
                Button(
                    onClick = onIrAlChat,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                ) {
                    Text("Abrir Chat con el Cliente 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRechazar,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rechazar", fontSize = 12.sp, color = ServifyDanger)
                    }
                    Button(
                        onClick = onAceptar,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                    ) {
                        Text("Aceptar", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}