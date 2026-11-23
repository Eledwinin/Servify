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
import java.net.URLEncoder
import java.util.Locale

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

    // Estados para el diálogo de rechazo/eliminación con motivo
    var contratacionARechazar by remember { mutableStateOf<ContratacionDirectaResponse?>(null) }
    var motivoRechazoTexto by remember { mutableStateOf("") }

    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            viewModel.validarQrCliente(result.contents)
        } else {
            Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    val tipoPlanActual = SessionManager.usuarioActual?.tipoPlan ?: ""
    val esSemanal = tipoPlanActual.contains("Semanal", ignoreCase = true)
    val esPro = tipoPlanActual.contains("PRO", ignoreCase = true) || (esVip && !esSemanal)

    val textoBoton = when {
        esSemanal -> "Plan Semanal"
        esPro -> "¡Eres Plan Pro! ⭐"
        else -> "VER PLANES"
    }

    var periodo by remember { mutableStateOf("mes") }

    LaunchedEffect(Unit) {
        viewModel.cargarGanancias()
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

    LaunchedEffect(viewModel.irACalificar) {
        if (viewModel.irACalificar) {
            val solId = viewModel.solicitudIdParaCalificar ?: 0
            val cliId = viewModel.clienteIdParaCalificar ?: 0

            viewModel.cargarGanancias()
            viewModel.resenaNavegada()

            navController.navigate(Rutas.CrearResena.crearRuta(solId, cliId))
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
                            tint = ServifyBlueLight,
                            modifier = Modifier.size(26.dp)
                        )
                    }

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
                            text = textoBoton,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                esSemanal -> Color(0xFF0284C7)
                                esPro -> ServifyPendingText
                                else -> Color(0xFF4338CA)
                            }
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
                val g = viewModel.ganancias
                val (monto, trabajos) = when (periodo) {
                    "semana" -> g.totalSemana to g.trabajosSemana
                    "mes" -> g.totalMes to g.trabajosMes
                    else -> g.totalHistorico to g.trabajosHistorico
                }
                val tituloPeriodo = when (periodo) {
                    "semana" -> "GANANCIAS (SEMANA ACTUAL)"
                    "mes" -> "GANANCIAS DE " + java.time.LocalDate.now().month
                        .getDisplayName(java.time.format.TextStyle.FULL, Locale("es", "SV")).uppercase()
                    else -> "GANANCIAS TOTALES"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                tituloPeriodo,
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
                                    Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (estaDisponible) ServifyOnlineGreen else ServifyTextMuted)
                                    )
                                    Text(
                                        if (estaDisponible) "Disponible" else "Ocupado",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("semana" to "Semana", "mes" to "Mes", "total" to "Total").forEach { (clave, etiqueta) ->
                                Surface(
                                    color = if (periodo == clave) Color.White else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.clickable { periodo = clave }
                                ) {
                                    Text(
                                        etiqueta,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (periodo == clave) ServifyDarkNavy else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$%.2f".format(Locale.US, monto),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        if (periodo == "mes") {
                            val anterior = g.totalMesAnterior
                            val texto = when {
                                anterior > 0 -> {
                                    val pct = (g.totalMes - anterior) / anterior * 100
                                    (if (pct >= 0) "▲ " else "▼ ") + "%.0f%% vs mes anterior".format(
                                        Locale.US,
                                        kotlin.math.abs(pct)
                                    )
                                }
                                g.totalMes > 0 -> "Primer mes con ganancias"
                                else -> "Aún sin trabajos pagados este mes"
                            }
                            Text(
                                texto,
                                fontSize = 12.sp,
                                color = if (anterior > 0 && g.totalMes < anterior) Color(0xFFFCA5A5) else ServifyOnlineGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)) {
                                Row(
                                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.TrendingUp,
                                        null,
                                        tint = ServifyOnlineGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        if (trabajos == 1) "1 Trabajo" else "$trabajos Trabajos",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)) {
                                Row(
                                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        tint = ServifyStarYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        "%.1f (%d)".format(
                                            Locale.US,
                                            SessionManager.usuarioActual?.calificacionPromedio ?: 0.0,
                                            viewModel.totalResenasTecnico
                                        ),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
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
                    BotonAccesoRapido("Mi Agenda", Icons.Default.ViewAgenda, Modifier.weight(1f)) {
                        navController.navigate(Rutas.AgendaTecnico.ruta)
                    }

                    BotonAccesoRapido("Escanear QR", Icons.Default.QrCodeScanner, Modifier.weight(1f)) {
                        val options = ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Escanea el código QR del cliente para confirmar el cierre")
                            setCameraId(0)
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
                                            text = solicitud.distanciaKm?.let { "A %.1f km de ti".format(Locale.US, it) } ?: "Cercano",
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

                            val presupuesto = solicitud.presupuestoEstimado
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Presupuesto estimado:", fontSize = 12.sp, color = ServifyTextMuted)
                                Text(
                                    text = if (presupuesto != null && presupuesto > 0.0) {
                                        "$%.2f".format(Locale.US, presupuesto)
                                    } else {
                                        "A convenir"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ServifyTextTitle
                                )
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
                                        val tituloCodificado = URLEncoder.encode(solicitud.titulo, "UTF-8")
                                        val montoQuery = presupuesto ?: 0.0
                                        navController.navigate("enviar_propuesta/${solicitud.id}/$tituloCodificado?presupuesto=$montoQuery")
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

    // Modal para revisar Solicitudes Directas (Sobrecito ✉️)
    if (mostrarModalDirectas) {
        AlertDialog(
            onDismissRequest = { mostrarModalDirectas = false },
            containerColor = ServifySurface,
            title = {
                Text(
                    text = "Contrataciones Directas Recibidas ✉️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ServifyTextTitle
                )
            },
            text = {
                if (contratacionesDirectas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No tienes solicitudes directas pendientes.", fontSize = 13.sp, color = ServifyTextMuted)
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
                                onRechazar = {
                                    motivoRechazoTexto = ""
                                    contratacionARechazar = item
                                },
                                onEliminar = {
                                    motivoRechazoTexto = ""
                                    contratacionARechazar = item
                                },
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
                    Text("Cerrar", color = ServifyBlueLight, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Diálogo obligatorio para ingresar el motivo al rechazar / eliminar una solicitud directa
    contratacionARechazar?.let { seleccionada ->
        AlertDialog(
            onDismissRequest = { contratacionARechazar = null },
            containerColor = ServifySurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Rechazar solicitud directa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ServifyTextTitle
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Indícale a ${seleccionada.cliente_nombre} el motivo por el cual no puedes tomar este trabajo:",
                        fontSize = 13.sp,
                        color = ServifyTextMuted,
                        lineHeight = 18.sp
                    )
                    OutlinedTextField(
                        value = motivoRechazoTexto,
                        onValueChange = { motivoRechazoTexto = it },
                        placeholder = { Text("Ej. No tengo disponibilidad esta semana...", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val motivoLimpio = motivoRechazoTexto.trim()
                        if (motivoLimpio.isEmpty()) {
                            Toast.makeText(context, "Por favor escribe el motivo", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.responderContratacion(
                                idContratacion = seleccionada.id,
                                estado = "rechazado",
                                motivo = motivoLimpio
                            )
                            contratacionARechazar = null
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyDanger)
                ) {
                    Text("Confirmar rechazo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { contratacionARechazar = null }) {
                    Text("Cancelar", color = ServifyTextMuted)
                }
            }
        )
    }
}

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
                        contentDescription = "Rechazar y eliminar solicitud",
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