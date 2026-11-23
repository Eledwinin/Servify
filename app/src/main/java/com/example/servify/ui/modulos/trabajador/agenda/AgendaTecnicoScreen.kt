package com.example.servify.ui.modulos.trabajador.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.api.AgendaItemResponse
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val ColorVisita = Color(0xFF2563EB)
private val ColorEntrega = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaTecnicoScreen(
    navController: NavController,
    viewModel: AgendaTecnicoViewModel = viewModel()
) {
    val hoyCal = remember { Calendar.getInstance() }
    val hoyIso = remember {
        String.format(
            Locale.US,
            "%04d-%02d-%02d",
            hoyCal.get(Calendar.YEAR),
            hoyCal.get(Calendar.MONTH) + 1,
            hoyCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    var mesActual by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
        )
    }
    var fechaSeleccionadaIso by remember { mutableStateOf(hoyIso) }
    var mostrarTodos by remember { mutableStateOf(false) }

    val fechasVisitas = remember(viewModel.itemsAgenda) {
        viewModel.itemsAgenda.mapNotNull { it.fecha_visita }.toSet()
    }
    val fechasEntregas = remember(viewModel.itemsAgenda) {
        viewModel.itemsAgenda.mapNotNull { it.fecha_entrega }.toSet()
    }

    val fechaSeleccionadaBonita = remember(fechaSeleccionadaIso) {
        val partes = fechaSeleccionadaIso.split("-")
        if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fechaSeleccionadaIso
    }

    val eventosFiltrados = remember(viewModel.itemsAgenda, fechaSeleccionadaIso, mostrarTodos) {
        if (mostrarTodos) {
            viewModel.itemsAgenda
        } else {
            viewModel.itemsAgenda.filter {
                it.fecha_visita == fechaSeleccionadaIso || it.fecha_entrega == fechaSeleccionadaIso
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mi Agenda",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ServifyTextTitle
                        )
                        Text(
                            text = "${viewModel.itemsAgenda.size} trabajos programados",
                            fontSize = 12.sp,
                            color = ServifyTextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBlueLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarAgenda() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = ServifyBlueLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
            )
        },
        containerColor = ServifyBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendario mensual con indicadores visuales de Visita y Entrega
            item {
                CalendarioAgendaMarcado(
                    mesActual = mesActual,
                    fechaSeleccionadaIso = fechaSeleccionadaIso,
                    hoyIso = hoyIso,
                    fechasVisitas = fechasVisitas,
                    fechasEntregas = fechasEntregas,
                    onCambiarMes = { delta ->
                        mesActual = (mesActual.clone() as Calendar).apply {
                            add(Calendar.MONTH, delta)
                        }
                    },
                    onSeleccionarDia = { nuevaFechaIso ->
                        fechaSeleccionadaIso = nuevaFechaIso
                        mostrarTodos = false
                    }
                )
            }

            // Barra de filtro: Fecha seleccionada vs Ver todos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (mostrarTodos) "Todos los compromisos" else "Agenda del $fechaSeleccionadaBonita",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )

                    FilterChip(
                        selected = mostrarTodos,
                        onClick = { mostrarTodos = !mostrarTodos },
                        label = {
                            Text(
                                text = if (mostrarTodos) "Filtrar por día" else "Ver todos (${viewModel.itemsAgenda.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ServifySurface,
                            selectedContainerColor = ServifyBluePrimary,
                            labelColor = ServifyBlueLight,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            when {
                viewModel.cargando -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ServifyBluePrimary)
                        }
                    }
                }

                viewModel.errorMensaje != null -> {
                    item {
                        Surface(
                            color = ServifyDangerBg,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(viewModel.errorMensaje!!, color = ServifyDanger, fontSize = 13.sp)
                                TextButton(onClick = { viewModel.cargarAgenda() }) {
                                    Text("Reintentar", color = ServifyBlueLight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                eventosFiltrados.isEmpty() -> {
                    item {
                        Surface(
                            color = ServifySurface,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = ServifyTextMuted,
                                    modifier = Modifier.size(38.dp)
                                )
                                Text(
                                    text = if (mostrarTodos) "Aún no tienes trabajos agendados" else "Sin visitas ni entregas para este día",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ServifyTextTitle
                                )
                                Text(
                                    text = "Toca un día marcado en el calendario o presiona \"Ver todos\".",
                                    fontSize = 12.sp,
                                    color = ServifyTextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(eventosFiltrados, key = { it.solicitud_id }) { item ->
                        TarjetaEventoAgenda(
                            item = item,
                            fechaReferenciaIso = if (mostrarTodos) null else fechaSeleccionadaIso,
                            onAbrirChat = {
                                item.conversacion_id?.let { convId ->
                                    navController.navigate(Rutas.Chat.crearRuta(convId, item.cliente_nombre))
                                }
                            },
                            onAbrirMapa = {
                                val lat = item.latitud
                                val lng = item.longitud
                                if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                                    navController.navigate(Rutas.Mapa.crearRuta(lat, lng, item.titulo))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarioAgendaMarcado(
    mesActual: Calendar,
    fechaSeleccionadaIso: String,
    hoyIso: String,
    fechasVisitas: Set<String>,
    fechasEntregas: Set<String>,
    onCambiarMes: (Int) -> Unit,
    onSeleccionarDia: (String) -> Unit
) {
    val tituloMes = remember(mesActual) {
        val formato = SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES"))
        formato.format(mesActual.time).replaceFirstChar { it.uppercase() }
    }

    val anio = mesActual.get(Calendar.YEAR)
    val mes = mesActual.get(Calendar.MONTH)
    val primerDiaSemana = mesActual.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Domingo
    val diasEnMes = mesActual.getActualMaximum(Calendar.DAY_OF_MONTH)

    val celdas = remember(anio, mes) {
        buildList<Int?> {
            repeat(primerDiaSemana) { add(null) }
            for (d in 1..diasEnMes) add(d)
            while (size % 7 != 0) add(null)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabecera del mes y flechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tituloMes,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )
                Row {
                    IconButton(onClick = { onCambiarMes(-1) }, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior", tint = ServifyBlueLight)
                    }
                    IconButton(onClick = { onCambiarMes(1) }, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente", tint = ServifyBlueLight)
                    }
                }
            }

            // Encabezado D L M M J V S
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextMuted
                    )
                }
            }

            // Cuadrícula de días
            celdas.chunked(7).forEach { semana ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    semana.forEach { dia ->
                        if (dia == null) {
                            Spacer(modifier = Modifier.weight(1f).height(44.dp))
                        } else {
                            val diaIso = String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)
                            val seleccionado = diaIso == fechaSeleccionadaIso
                            val esHoy = diaIso == hoyIso
                            val tieneVisita = diaIso in fechasVisitas
                            val tieneEntrega = diaIso in fechasEntregas

                            val fondoDia = when {
                                seleccionado -> ServifyBluePrimary
                                tieneVisita || tieneEntrega -> ServifyBluePrimary.copy(alpha = 0.12f)
                                else -> Color.Transparent
                            }

                            val colorNumero = when {
                                seleccionado -> Color.White
                                esHoy -> ServifyBlueLight
                                else -> ServifyTextTitle
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(CircleShape)
                                    .background(fondoDia)
                                    .then(
                                        if (esHoy && !seleccionado) {
                                            Modifier.border(1.dp, ServifyBlueLight, CircleShape)
                                        } else Modifier
                                    )
                                    .clickable { onSeleccionarDia(diaIso) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dia.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (seleccionado || esHoy || tieneVisita || tieneEntrega) FontWeight.Bold else FontWeight.Normal,
                                    color = colorNumero
                                )

                                if (tieneVisita || tieneEntrega) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        if (tieneVisita) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(if (seleccionado) Color.White else ColorVisita)
                                            )
                                        }
                                        if (tieneEntrega) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(if (seleccionado) Color(0xFFA7F3D0) else ColorEntrega)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Leyenda de indicadores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorVisita))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Visita", fontSize = 11.sp, color = ServifyTextMuted, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.width(18.dp))

                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ColorEntrega))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Entrega", fontSize = 11.sp, color = ServifyTextMuted, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun TarjetaEventoAgenda(
    item: AgendaItemResponse,
    fechaReferenciaIso: String?,
    onAbrirChat: () -> Unit,
    onAbrirMapa: () -> Unit
) {
    val esVisitaHoy = fechaReferenciaIso != null && item.fecha_visita == fechaReferenciaIso
    val esEntregaHoy = fechaReferenciaIso != null && item.fecha_entrega == fechaReferenciaIso

    Card(
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (esVisitaHoy || fechaReferenciaIso == null) {
                        BadgeEvento(
                            texto = "Visita: ${item.fecha_visita ?: "Sin fecha"}",
                            colorFondo = ServifyBluePrimary.copy(alpha = 0.2f),
                            colorTexto = ServifyBlueLight
                        )
                    }
                    if (esEntregaHoy || fechaReferenciaIso == null) {
                        BadgeEvento(
                            texto = "Entrega: ${item.fecha_entrega ?: "Sin fecha"}",
                            colorFondo = Color(0xFF10B981).copy(alpha = 0.18f),
                            colorTexto = Color(0xFF34D399)
                        )
                    }
                }

                Text(
                    text = "$%.2f".format(Locale.US, item.monto),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = ServifyBlueLight
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.categoria_nombre} • Cliente: ${item.cliente_nombre}",
                    fontSize = 12.sp,
                    color = ServifyTextMuted
                )
            }

            if (!item.direccion_texto.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ServifyBlueLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = item.direccion_texto,
                        fontSize = 12.sp,
                        color = ServifyTextBody,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (item.conversacion_id != null) {
                    OutlinedButton(
                        onClick = onAbrirChat,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(16.dp), tint = ServifyBlueLight)
                        Spacer(Modifier.width(6.dp))
                        Text("Chat", color = ServifyBlueLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (item.latitud != null && item.longitud != null && item.latitud != 0.0) {
                    Button(
                        onClick = onAbrirMapa,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                    ) {
                        Icon(Icons.Default.Directions, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Cómo llegar", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeEvento(texto: String, colorFondo: Color, colorTexto: Color) {
    Surface(color = colorFondo, shape = CircleShape) {
        Text(
            text = texto,
            color = colorTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}