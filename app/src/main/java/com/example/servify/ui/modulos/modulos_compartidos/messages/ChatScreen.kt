package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.LlamadasManager
import com.example.servify.data.model.Mensaje
import com.example.servify.ui.navigation.Rutas
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun horaDe(iso: String?): String {
    if (iso.isNullOrBlank() || iso.length < 19) return ""
    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(entrada.parse(iso.take(19))!!)
    } catch (e: Exception) { "" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversacionId: Int,
    nombreOtro: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val verdePrincipal = Color(0xFF0D9488) // Esmeralda / teal moderno
    val verdeClaroBg = Color(0xFFF0FDFA)
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var texto by remember { mutableStateOf("") }
    var mostrarModalOferta by remember { mutableStateOf(false) }

    // Estados para el diálogo de llamada entrante dentro del chat
    var mostrarDialogoLLamada by remember { mutableStateOf(false) }
    var tipoVideoLlamadaEntrante by remember { mutableStateOf(false) }

    // Detectar si ya hay alguna oferta aceptada en la conversación
    val hayAcuerdoCerrado by remember {
        derivedStateOf {
            viewModel.mensajes.any { m ->
                m.estadoOferta.equals("aceptada", ignoreCase = true) ||
                        (m.tipo == "sistema" && m.contenido.contains("acuerdo cerrado", ignoreCase = true))
            }
        }
    }

    LaunchedEffect(conversacionId) { viewModel.iniciar(conversacionId) }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    LaunchedEffect(viewModel.mensajes.size) {
        if (viewModel.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.mensajes.lastIndex)
        }
    }

    LaunchedEffect(LlamadasManager.llamadaEntrante) {
        if (LlamadasManager.llamadaEntrante == null) {
            mostrarDialogoLLamada = false
        }
    }

    // Escuchar cuando el ViewModel detecte una llamada entrante por el socket
    LaunchedEffect(viewModel.llamadaEntrante) {
        viewModel.llamadaEntrante?.let { (_, esVideo) ->
            tipoVideoLlamadaEntrante = esVideo
            mostrarDialogoLLamada = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(verdeClaroBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                nombreOtro.split(" ").filter { it.isNotBlank() }.take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                                color = verdePrincipal, fontWeight = FontWeight.Bold, fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(nombreOtro, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                            Text("En línea", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF334155))
                    }
                },
                actions = {
                    // Botón de Llamada de Voz (Limpio, el servidor registra el evento)
                    IconButton(onClick = {
                        LlamadasManager.llamar(conversacionId, false)
                        navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", false))
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Llamar", tint = verdePrincipal)
                    }

                    // Botón de Videollamada (Limpio, el servidor registra el evento)
                    IconButton(onClick = {
                        LlamadasManager.llamar(conversacionId, true)
                        navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", true))
                    }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Videollamada", tint = verdePrincipal)
                    }

                    // Si hay acuerdo, el botón literal desaparece de la barra
                    if (!hayAcuerdoCerrado) {
                        IconButton(onClick = { mostrarModalOferta = true }) {
                            Icon(Icons.Default.LocalOffer, contentDescription = "Proponer Precio", tint = verdePrincipal)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            // Banner superior indicador si ya llegaron a un acuerdo
            if (hayAcuerdoCerrado) {
                Surface(
                    color = Color(0xFFCCFBF1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ya han llegado a un acuerdo económico",
                            fontSize = 13.sp,
                            color = Color(0xFF0F766E),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (viewModel.isLoading && viewModel.mensajes.isEmpty()) {
                    CircularProgressIndicator(color = verdePrincipal, modifier = Modifier.align(Alignment.Center))
                } else if (viewModel.mensajes.isEmpty()) {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("¡Empieza la conversación!", fontWeight = FontWeight.Bold, color = Color(0xFF475569), fontSize = 15.sp)
                        Text(
                            "Escribe el primer mensaje o haz una propuesta económica",
                            color = Color(0xFF94A3B8), fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(viewModel.mensajes, key = { it.id }) { m ->
                            val tipoMensaje = m.tipo ?: "texto"
                            val esOfertaReal = tipoMensaje == "oferta" || m.contenido.startsWith("Propuesta de oferta") || (m.monto != null && m.monto > 0.0)

                            when {
                                tipoMensaje == "sistema" -> {
                                    val esLlamada = m.contenido.contains("llamada", ignoreCase = true) ||
                                            m.contenido.contains("videollamada", ignoreCase = true)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                                        if (esLlamada) Icon(
                                            if (m.contenido.contains("video", ignoreCase = true)) Icons.Default.Videocam else Icons.Default.Call,
                                            null, tint = Color(0xFF475569), modifier = Modifier.size(14.dp)
                                        )
                                        Text(m.contenido, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                                    }
                                }
                                esOfertaReal -> {
                                    val montoExtraido = m.monto ?: m.contenido.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                                    val mensajeParaTarjeta = m.copy(monto = montoExtraido)

                                    TarjetaOfertaItem(
                                        mensaje = mensajeParaTarjeta,
                                        esMio = m.emisorId == viewModel.miId,
                                        onAceptar = { viewModel.responderOferta(m.id, "aceptar") },
                                        onRechazar = { viewModel.responderOferta(m.id, "rechazar") }
                                    )
                                }
                                else -> {
                                    val mio = m.emisorId == viewModel.miId
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (mio) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            color = if (mio) verdePrincipal else Color.White,
                                            shape = RoundedCornerShape(
                                                topStart = 18.dp, topEnd = 18.dp,
                                                bottomStart = if (mio) 18.dp else 4.dp,
                                                bottomEnd = if (mio) 4.dp else 18.dp
                                            ),
                                            shadowElevation = 1.5.dp,
                                            modifier = Modifier.widthIn(max = 290.dp)
                                        ) {
                                            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                                Text(
                                                    m.contenido, fontSize = 15.sp,
                                                    color = if (mio) Color.White else Color(0xFF1E293B)
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    horaDe(m.enviadoEn), fontSize = 10.sp,
                                                    color = if (mio) Color.White.copy(alpha = 0.75f) else Color(0xFF94A3B8),
                                                    modifier = Modifier.align(Alignment.End)
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

            if (!viewModel.activa) {
                Surface(color = Color(0xFFFEF3C7), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Esta conversación está cerrada. No se pueden enviar mensajes.",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            placeholder = { Text("Escribe un mensaje...", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verdePrincipal,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewModel.enviar(texto)
                                texto = ""
                            },
                            enabled = texto.isNotBlank(),
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (texto.isNotBlank()) verdePrincipal else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal para proponer precio (Solo si no hay acuerdo)
    if (mostrarModalOferta && !hayAcuerdoCerrado) {
        ModalProponerPrecio(
            onDismiss = { mostrarModalOferta = false },
            onEnviar = { monto ->
                viewModel.enviarOferta(monto)
                mostrarModalOferta = false
            }
        )
    }

    // Diálogo flotante de llamada entrante dentro del chat (Aceptar / Rechazar)
    if (mostrarDialogoLLamada) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = if (tipoVideoLlamadaEntrante) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = null,
                    tint = verdePrincipal,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (tipoVideoLlamadaEntrante) "Videollamada entrante" else "Llamada de voz entrante",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "$nombreOtro te está llamando...",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoLLamada = false
                        LlamadasManager.responder(conversacionId, true)
                        viewModel.limpiarLlamadaEntrante()
                        navController.navigate(Rutas.Call.crearRuta("chat_$conversacionId", tipoVideoLlamadaEntrante))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        mostrarDialogoLLamada = false
                        LlamadasManager.responder(conversacionId, false) // 👈 Avisa al servidor que rechazaste
                        viewModel.limpiarLlamadaEntrante()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Rechazar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun TarjetaOfertaItem(
    mensaje: Mensaje,
    esMio: Boolean,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val verdePrincipal = Color(0xFF0D9488)
    val estado = mensaje.estadoOferta ?: "pendiente"

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            modifier = Modifier.widthIn(min = 260.dp, max = 310.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LocalOffer, null, tint = verdePrincipal, modifier = Modifier.size(16.dp))
                        Text("Propuesta Económica", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = verdePrincipal)
                    }
                    Text(horaDe(mensaje.enviadoEn), fontSize = 10.sp, color = Color(0xFF94A3B8))
                }

                Text(
                    text = "$${String.format(Locale.US, "%.2f", mensaje.monto ?: 0.0)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )

                when (estado) {
                    "pendiente" -> {
                        if (esMio) {
                            Text("Esperando respuesta del usuario...", fontSize = 12.sp, color = Color(0xFF64748B), fontStyle = FontStyle.Italic)
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onRechazar,
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Text("Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onAceptar,
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal)
                                ) {
                                    Text("Aceptar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                    "aceptada" -> {
                        Surface(color = Color(0xFFCCFBF1), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✓ Oferta Aceptada (Acuerdo Cerrado)", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E), textAlign = TextAlign.Center)
                        }
                    }
                    "rechazada" -> {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✕ Oferta Rechazada", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C), textAlign = TextAlign.Center)
                        }
                    }
                    else -> {
                        Text("Oferta cancelada", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalProponerPrecio(
    onDismiss: () -> Unit,
    onEnviar: (Double) -> Unit
) {
    val verdePrincipal = Color(0xFF0D9488)
    var montoStr by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Proponer Precio / Regatear", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("Ingresa la cantidad monetaria en dólares que propones para este servicio.", fontSize = 13.sp, color = Color(0xFF64748B))

            OutlinedTextField(
                value = montoStr,
                onValueChange = { montoStr = it; error = null },
                label = { Text("Monto en USD ($)") },
                placeholder = { Text("Ej: 45.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    focusedLabelColor = verdePrincipal,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(error!!, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = {
                    val montoLimpio = montoStr.replace(",", ".").trim()
                    val monto = montoLimpio.toDoubleOrNull()

                    if (monto == null || monto <= 0) {
                        error = "Ingresa un monto válido mayor a 0"
                        return@Button
                    }
                    onEnviar(monto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal)
            ) {
                Text("Enviar Propuesta Económica", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}