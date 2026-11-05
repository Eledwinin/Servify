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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.model.Mensaje
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.ServifyBluePrimary
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
    val verde = Color(0xFF1B7B61)
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var texto by remember { mutableStateOf("") }
    var mostrarModalOferta by remember { mutableStateOf(false) }

    LaunchedEffect(conversacionId) { viewModel.iniciar(conversacionId) }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    LaunchedEffect(viewModel.mensajes.size) {
        if (viewModel.mensajes.isNotEmpty()) listState.animateScrollToItem(viewModel.mensajes.lastIndex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(38.dp).background(Color(0xFFE2F3EE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                nombreOtro.split(" ").filter { it.isNotBlank() }.take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" },
                                color = verde, fontWeight = FontWeight.Bold, fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(nombreOtro, fontWeight = FontWeight.Bold, fontSize = 17.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Botón de Llamada de Voz
                            IconButton(onClick = {
                                navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", false))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Llamar",
                                    tint = ServifyBluePrimary
                                )
                            }

                            // Botón de Videollamada
                            IconButton(onClick = {
                                navController.navigate(Rutas.Call.crearRuta("chat_${conversacionId}", true))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Videollamada",
                                    tint = ServifyBluePrimary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Botón para abrir el modal de Proponer Precio
                    IconButton(onClick = { mostrarModalOferta = true }) {
                        Icon(Icons.Default.LocalOffer, contentDescription = "Proponer Precio", tint = verde)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (viewModel.isLoading && viewModel.mensajes.isEmpty()) {
                    CircularProgressIndicator(color = verde, modifier = Modifier.align(Alignment.Center))
                } else if (viewModel.mensajes.isEmpty()) {
                    Text(
                        "Escribe el primer mensaje o haz una propuesta económica",
                        color = Color.Gray, fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(viewModel.mensajes, key = { it.id }) { m ->
                            // Evitamos nulos para prevenir cualquier cierre inesperado
                            val tipoMensaje = m.tipo ?: "texto"
                            val esOfertaReal = tipoMensaje == "oferta" || m.contenido.startsWith("Propuesta de oferta") || (m.monto != null && m.monto > 0.0)

                            when {
                                tipoMensaje == "sistema" -> {
                                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Surface(
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = m.contenido,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF475569)
                                            )
                                        }
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
                                            color = if (mio) verde else Color.White,
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp, topEnd = 16.dp,
                                                bottomStart = if (mio) 16.dp else 4.dp,
                                                bottomEnd = if (mio) 4.dp else 16.dp
                                            ),
                                            shadowElevation = 1.dp,
                                            modifier = Modifier.widthIn(max = 280.dp)
                                        ) {
                                            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                                Text(
                                                    m.contenido, fontSize = 15.sp,
                                                    color = if (mio) Color.White else Color(0xFF1E293B)
                                                )
                                                Text(
                                                    horaDe(m.enviadoEn), fontSize = 10.sp,
                                                    color = if (mio) Color.White.copy(alpha = 0.7f) else Color.Gray,
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
                        fontSize = 13.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { texto = it },
                        placeholder = { Text("Escribe un mensaje...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verde)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            viewModel.enviar(texto)
                            texto = ""
                        },
                        enabled = texto.isNotBlank(),
                        modifier = Modifier.size(48.dp).background(
                            if (texto.isNotBlank()) verde else Color(0xFFCBD5E1), CircleShape
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    }

    if (mostrarModalOferta) {
        ModalProponerPrecio(
            onDismiss = { mostrarModalOferta = false },
            onEnviar = { monto ->
                viewModel.enviarOferta(monto)
                mostrarModalOferta = false
            }
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
    val verde = Color(0xFF1B7B61)
    val estado = mensaje.estadoOferta ?: "pendiente"

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(min = 240.dp, max = 300.dp)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Propuesta Económica", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = verde)
                    Text(horaDe(mensaje.enviadoEn), fontSize = 10.sp, color = Color.Gray)
                }

                Text(
                    text = "$${String.format(Locale.US, "%.2f", mensaje.monto ?: 0.0)}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                when (estado) {
                    "pendiente" -> {
                        if (esMio) {
                            Text("Esperando respuesta...", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onRechazar,
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                                ) {
                                    Text("Rechazar", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = onAceptar,
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verde)
                                ) {
                                    Text("Aceptar", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                    "aceptada" -> {
                        Surface(color = Color(0xFFE2F3EE), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✓ Oferta Aceptada (Acuerdo Cerrado)", modifier = Modifier.padding(6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = verde, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    "rechazada" -> {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✕ Oferta Rechazada", modifier = Modifier.padding(6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    else -> {
                        Text("Oferta cancelada", fontSize = 11.sp, color = Color.Gray)
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
    val verde = Color(0xFF1B7B61)
    var montoStr by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Proponer Precio / Regatear", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Ingresa la cantidad monetaria que propones para este trabajo.", fontSize = 13.sp, color = Color.Gray)

            OutlinedTextField(
                value = montoStr,
                onValueChange = { montoStr = it; error = null },
                label = { Text("Monto en USD ($)") },
                placeholder = { Text("Ej: 45.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verde, focusedLabelColor = verde),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(error!!, color = Color(0xFFDC2626), fontSize = 12.sp)
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
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verde)
            ) {
                Text("Enviar Propuesta Económica", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}