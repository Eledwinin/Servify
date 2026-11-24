package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.modulos.modulos_compartidos.messages.CallViewModel.Evento
import com.example.servify.ui.modulos.modulos_compartidos.messages.CallViewModel.Fase
import com.example.servify.data.LlamadasManager
import com.example.servify.ui.theme.*

private val FondoOscuro = Color(0xFF021024)
private val FondoPip = Color(0xFF0B1B3A)

@Composable
fun CallScreen(
    navController: NavController,
    channelName: String,
    esVideo: Boolean,
    nombre: String = "",
    vm: CallViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val conversacionId = remember(channelName) { channelName.removePrefix("chat_").toIntOrNull() ?: 0 }

    // La pantalla no se apaga durante la llamada
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // El botón atrás cuelga (y avisa al servidor)
    BackHandler { vm.colgar() }

    // ---- Permisos ----
    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) vm.unirse() else vm.terminar(true, "Se requiere el permiso del micrófono")
    }
    val camLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) vm.alternarCamara()
        else Toast.makeText(context, "Activa el permiso de cámara para usar el video", Toast.LENGTH_LONG).show()
    }
    val alPulsarCamara = {
        val tiene = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (vm.camaraEncendida || tiene) vm.alternarCamara() else camLauncher.launch(Manifest.permission.CAMERA)
    }

    // ---- Arranque: primero el motor, después el permiso y la unión al canal ----
    LaunchedEffect(conversacionId) {
        vm.iniciar(conversacionId, esVideo)
        val micOk = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (micOk) vm.unirse() else micLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    // ---- Eventos de una sola vez: avisos y cierre (un único popBackStack) ----
    LaunchedEffect(Unit) {
        vm.eventos.collect { e ->
            when (e) {
                is Evento.Aviso -> Toast.makeText(context, e.texto, Toast.LENGTH_SHORT).show()
                Evento.Cerrar -> navController.popBackStack()
            }
        }
    }

    // ---- El otro rechazó, o el servidor cerró por falta de respuesta ----
    LaunchedEffect(LlamadasManager.respuestaLlamada) {
        if (LlamadasManager.respuestaLlamada == false) {
            LlamadasManager.respuestaLlamada = null
            vm.alRechazar()
        }
    }

    val tiempo = formatoTiempo(vm.segundos)
    val videoRemotoVisible = esVideo && vm.uidRemoto != null && vm.remotoConVideo

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ServifyDarkNavy, FondoOscuro)))) {

        // Capa 1: video remoto (si lo hay). Se mantiene viva aunque el otro apague su cámara.
        if (esVideo && vm.motorListo) {
            vm.uidRemoto?.let { uid ->
                key(uid) {
                    AndroidView(
                        factory = { ctx -> vm.crearVistaRemota(ctx, uid) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Capa 2: panel con avatar. Es opaco: tapa el video remoto cuando el otro tiene la cámara apagada.
        if (!videoRemotoVisible) {
            PanelLlamada(
                esVideo = esVideo,
                nombre = nombre,
                fase = vm.fase,
                tiempo = tiempo,
                remotoSinVideo = esVideo && vm.uidRemoto != null
            )
        } else {
            // Degradado inferior para que se lean los controles sobre el video
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(240.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
            )
            Box(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 14.dp)) {
                PildoraEstado(vm.fase, tiempo)
            }
        }

        // Capa 3: mi cámara en una ventana flotante. La vista se crea UNA vez y no se destruye al apagar la cámara.
        if (esVideo && vm.motorListo) {
            val formaPip = RoundedCornerShape(18.dp)
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 12.dp, end = 16.dp)
                    .size(width = 104.dp, height = 148.dp)
                    .shadow(10.dp, formaPip)
                    .clip(formaPip)
                    .background(FondoPip)
            ) {
                AndroidView(
                    factory = { ctx -> vm.crearVistaLocal(ctx) },
                    modifier = Modifier.fillMaxSize()
                )
                if (!vm.camaraEncendida) {
                    Column(
                        Modifier.fillMaxSize().background(FondoPip),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.VideocamOff, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(26.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Cámara apagada", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }
                Box(Modifier.matchParentSize().border(1.5.dp, Color.White.copy(alpha = 0.35f), formaPip))
            }
        }

        // Capa 4: controles
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            color = Color.White.copy(alpha = 0.10f),
            shape = RoundedCornerShape(36.dp)
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotonControl(
                    icono = if (vm.microApagado) Icons.Default.MicOff else Icons.Default.Mic,
                    etiqueta = if (vm.microApagado) "Silenciado" else "Micrófono",
                    activo = vm.microApagado,
                    onClick = { vm.alternarMicro() }
                )

                if (esVideo) {
                    BotonControl(
                        icono = if (vm.camaraEncendida) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        etiqueta = "Cámara",
                        activo = vm.camaraEncendida,
                        onClick = alPulsarCamara
                    )
                    BotonControl(
                        icono = Icons.Default.Cameraswitch,
                        etiqueta = "Girar",
                        habilitado = vm.camaraEncendida,
                        onClick = { vm.girarCamara() }
                    )
                } else {
                    BotonControl(
                        icono = if (vm.altavozActivo) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        etiqueta = "Altavoz",
                        activo = vm.altavozActivo,
                        onClick = { vm.alternarAltavoz() }
                    )
                }

                BotonControl(
                    icono = Icons.Default.CallEnd,
                    etiqueta = "Colgar",
                    fondoFijo = ServifyDanger,
                    colorIconoFijo = Color.White,
                    tamano = 62.dp,
                    onClick = { vm.colgar() }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Panel de espera / llamada de voz
// ---------------------------------------------------------------------------------------------

@Composable
private fun PanelLlamada(
    esVideo: Boolean,
    nombre: String,
    fase: Fase,
    tiempo: String,
    remotoSinVideo: Boolean
) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ServifyDarkNavy, FondoOscuro)))) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AvatarPulso(nombre = nombre, esVideo = esVideo, pulsando = fase != Fase.EN_LLAMADA)
            Spacer(Modifier.height(28.dp))

            Text(
                text = nombre.ifBlank { if (esVideo) "Videollamada" else "Llamada de voz" },
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (nombre.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    if (esVideo) "Videollamada Servify" else "Llamada de voz Servify",
                    color = ServifyBlueLight, fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(18.dp))
            PildoraEstado(fase, tiempo)

            if (fase == Fase.EN_LLAMADA && remotoSinVideo) {
                Spacer(Modifier.height(14.dp))
                Text("Su cámara está apagada", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AvatarPulso(nombre: String, esVideo: Boolean, pulsando: Boolean) {
    val transicion = rememberInfiniteTransition(label = "pulso")
    val spec = infiniteRepeatable<Float>(tween(2000, easing = LinearEasing), RepeatMode.Restart)
    val specDesfasada = infiniteRepeatable<Float>(
        tween(2000, easing = LinearEasing), RepeatMode.Restart, initialStartOffset = StartOffset(1000)
    )
    val escala1 by transicion.animateFloat(1f, 1.9f, spec, label = "e1")
    val alfa1 by transicion.animateFloat(0.35f, 0f, spec, label = "a1")
    val escala2 by transicion.animateFloat(1f, 1.9f, specDesfasada, label = "e2")
    val alfa2 by transicion.animateFloat(0.35f, 0f, specDesfasada, label = "a2")

    Box(Modifier.size(190.dp), contentAlignment = Alignment.Center) {
        if (pulsando) {
            Box(Modifier.size(112.dp).graphicsLayer { scaleX = escala1; scaleY = escala1; alpha = alfa1 }.background(ServifyCianSoft, CircleShape))
            Box(Modifier.size(112.dp).graphicsLayer { scaleX = escala2; scaleY = escala2; alpha = alfa2 }.background(ServifyCianSoft, CircleShape))
        } else {
            Box(Modifier.size(148.dp).border(2.dp, ServifyCianSoft.copy(alpha = 0.35f), CircleShape))
        }
        Box(
            Modifier.size(112.dp).clip(CircleShape).background(ServifyBluePrimary)
                .border(3.dp, ServifyBlueLight.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val iniciales = inicialesDe(nombre)
            if (iniciales.isNotEmpty()) {
                Text(iniciales, color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(if (esVideo) Icons.Default.Videocam else Icons.Default.Call, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Composable
private fun PildoraEstado(fase: Fase, tiempo: String) {
    val (texto, color) = when (fase) {
        Fase.CONECTANDO -> "Conectando…" to Color(0xFFFBBF24)
        Fase.LLAMANDO -> "Llamando…" to ServifyCianSoft
        Fase.EN_LLAMADA -> "En llamada · $tiempo" to ServifyOnlineGreen
    }
    Surface(color = Color.Black.copy(alpha = 0.28f), shape = CircleShape) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Text(texto, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Controles
// ---------------------------------------------------------------------------------------------

@Composable
private fun BotonControl(
    icono: ImageVector,
    etiqueta: String,
    onClick: () -> Unit,
    activo: Boolean = false,
    habilitado: Boolean = true,
    fondoFijo: Color? = null,
    colorIconoFijo: Color? = null,
    tamano: Dp = 56.dp
) {
    val fondo by animateColorAsState(
        fondoFijo ?: if (activo) Color.White else Color.White.copy(alpha = 0.16f), label = "fondo"
    )
    val colorIcono = colorIconoFijo ?: if (activo) ServifyDarkNavy else Color.White

    Column(
        Modifier.alpha(if (habilitado) 1f else 0.4f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier.size(tamano).clip(CircleShape).background(fondo)
                .clickable(enabled = habilitado, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = etiqueta, tint = colorIcono)
        }
        Text(etiqueta, color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, maxLines = 1)
    }
}

// ---------------------------------------------------------------------------------------------
// Utilidades
// ---------------------------------------------------------------------------------------------

private fun formatoTiempo(s: Int): String =
    if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s % 3600 / 60, s % 60)
    else "%02d:%02d".format(s / 60, s % 60)

private fun inicialesDe(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")