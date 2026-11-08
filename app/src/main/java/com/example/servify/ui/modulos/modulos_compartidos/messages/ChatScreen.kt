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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.LlamadasManager
import com.example.servify.ui.componentes.cards.TarjetaOfertaItem
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
    viewModel: ChatViewModel = viewModel(),
    onVerPerfil: () -> Unit = {},
    onReportarUsuario: (motivo: String) -> Unit = {},
    onVaciarChat: () -> Unit = {}
) {
    val verdePrincipal = Color(0xFF0F2C59)
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var texto by remember { mutableStateOf("") }
    var mostrarModalOferta by remember { mutableStateOf(false) }

    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarDialogoReporte by remember { mutableStateOf(false) }
    var mostrarDialogoVaciar by remember { mutableStateOf(false) }

    var mostrarDialogoLLamada by remember { mutableStateOf(false) }
    var tipoVideoLlamadaEntrante by remember { mutableStateOf(false) }

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
                                .background(Color(0xFFE0F2FE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                nombreOtro.split(" ").filter { it.isNotBlank() }.take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                                color = Color(0xFF0284C7), fontWeight = FontWeight.Bold, fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(nombreOtro, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(7.dp).background(Color(0xFF22C55E), CircleShape))
                                Spacer(Modifier.width(4.dp))
                                Text("En línea", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF334155))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        LlamadasManager.llamar(conversacionId, false)
                        navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", false))
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Llamar", tint = Color(0xFF334155))
                    }

                    IconButton(onClick = {
                        LlamadasManager.llamar(conversacionId, true)
                        navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", true))
                    }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Videollamada", tint = Color(0xFF334155))
                    }

                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color(0xFF334155))
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ver perfil", color = Color(0xFF1E293B)) },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = verdePrincipal) },
                                onClick = { menuAbierto = false; onVerPerfil() }
                            )
                            DropdownMenuItem(
                                text = { Text("Reportar usuario", color = Color(0xFF1E293B)) },
                                leadingIcon = { Icon(Icons.Default.Flag, null, tint = verdePrincipal) },
                                onClick = { menuAbierto = false; mostrarDialogoReporte = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Vaciar chat", color = Color(0xFFEF4444)) },
                                leadingIcon = { Icon(Icons.Default.DeleteSweep, null, tint = Color(0xFFEF4444)) },
                                onClick = { menuAbierto = false; mostrarDialogoVaciar = true }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
        ) {
            if (hayAcuerdoCerrado) {
                Surface(color = Color(0xFFCCFBF1), modifier = Modifier.fillMaxWidth()) {
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
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(viewModel.mensajes, key = { it.id }) { m ->
                            val tipoMensaje = m.tipo ?: "texto"
                            val esOfertaReal = tipoMensaje == "oferta" || m.contenido.startsWith("Propuesta de oferta") || (m.monto != null && m.monto > 0.0)

                            when {
                                tipoMensaje == "sistema" -> {
                                    Box(Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Surface(color = Color(0xFFE2E8F0), shape = RoundedCornerShape(10.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                val esLlamada = m.contenido.contains("llamada", ignoreCase = true) || m.contenido.contains("videollamada", ignoreCase = true)
                                                if (esLlamada) {
                                                    Icon(
                                                        if (m.contenido.contains("video", ignoreCase = true)) Icons.Default.Videocam else Icons.Default.Call,
                                                        contentDescription = null,
                                                        tint = Color(0xFF475569),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Text(
                                                    text = m.contenido,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                                esOfertaReal -> {
                                    val montoExtraido = m.monto ?: m.contenido.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                                    TarjetaOfertaItem(
                                        mensaje = m.copy(monto = montoExtraido),
                                        esMio = m.emisorId == viewModel.miId,
                                        onAceptar = { viewModel.responderOferta(m.id, "aceptar") },
                                        onRechazar = { viewModel.responderOferta(m.id, "rechazar") },
                                        horaFormateada = horaDe(m.enviadoEn)
                                    )
                                }
                                else -> {
                                    val mio = m.emisorId == viewModel.miId
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mio) Arrangement.End else Arrangement.Start) {
                                        Surface(
                                            color = if (mio) verdePrincipal else Color.White,
                                            shape = RoundedCornerShape(
                                                topStart = 18.dp, topEnd = 18.dp,
                                                bottomStart = if (mio) 18.dp else 4.dp,
                                                bottomEnd = if (mio) 4.dp else 18.dp
                                            ),
                                            shadowElevation = 1.5.dp,
                                            border = if (mio) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.widthIn(max = 290.dp)
                                        ) {
                                            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                                Text(m.contenido, fontSize = 15.sp, color = if (mio) Color.White else Color(0xFF1E293B))
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
                Surface(color = Color.White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            placeholder = { Text("Escribe un mensaje...", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            trailingIcon = {
                                if (!hayAcuerdoCerrado) {
                                    IconButton(onClick = { mostrarModalOferta = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Proponer precio", tint = Color(0xFF64748B), modifier = Modifier.size(22.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFCBD5E1),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.enviar(texto); texto = "" },
                            enabled = texto.isNotBlank(),
                            modifier = Modifier.size(48.dp).background(if (texto.isNotBlank()) verdePrincipal else Color(0xFFE2E8F0), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (mostrarModalOferta && !hayAcuerdoCerrado) {
        ModalProponerPrecio(
            onDismiss = { mostrarModalOferta = false },
            onEnviar = { monto -> viewModel.enviarOferta(monto); mostrarModalOferta = false }
        )
    }

    if (mostrarDialogoReporte) {
        var motivo by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoReporte = false },
            title = { Text("Reportar usuario", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Cuéntanos qué pasó con $nombreOtro para que podamos revisarlo.", fontSize = 13.sp, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        placeholder = { Text("Motivo del reporte", color = Color(0xFF94A3B8)) },
                        shape = RoundedCornerShape(14.dp),
                        minLines = 3, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdePrincipal, unfocusedBorderColor = Color(0xFFCBD5E1), cursorColor = verdePrincipal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onReportarUsuario(motivo.trim()); mostrarDialogoReporte = false; Toast.makeText(context, "Reporte enviado", Toast.LENGTH_SHORT).show() },
                    enabled = motivo.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Reportar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoReporte = false }) { Text("Cancelar", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    if (mostrarDialogoVaciar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoVaciar = false },
            title = { Text("Vaciar chat", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
            text = { Text("Se borrarán todos los mensajes de esta conversación. Esta acción no se puede deshacer.", fontSize = 14.sp, color = Color(0xFF64748B)) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoVaciar = false; onVaciarChat() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Vaciar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoVaciar = false }) { Text("Cancelar", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    if (mostrarDialogoLLamada) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(if (tipoVideoLlamadaEntrante) Icons.Default.Videocam else Icons.Default.Call, null, tint = verdePrincipal, modifier = Modifier.size(36.dp)) },
            title = { Text(if (tipoVideoLlamadaEntrante) "Videollamada entrante" else "Llamada de voz entrante", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text("$nombreOtro te está llamando...", fontSize = 14.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center) },
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
                ) { Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        mostrarDialogoLLamada = false
                        LlamadasManager.responder(conversacionId, false)
                        viewModel.limpiarLlamadaEntrante()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Rechazar", fontWeight = FontWeight.Bold) }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}