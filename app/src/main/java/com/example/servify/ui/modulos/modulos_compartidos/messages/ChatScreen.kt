package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: ChatViewModel = viewModel()
) {
    val verde = Color(0xFF1B7B61)
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var texto by remember { mutableStateOf("") }

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
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
                        "Escribe el primer mensaje",
                        color = Color.Gray, fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(viewModel.mensajes, key = { it.id }) { m ->
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
}