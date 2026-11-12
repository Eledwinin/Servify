package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.servify.BuildConfig
import com.example.servify.data.LlamadasManager
import com.example.servify.data.SessionManager
import com.example.servify.data.agora.AgoraManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.ui.theme.*
import io.agora.rtc2.IRtcEngineEventHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CallScreen(
    navController: NavController,
    channelName: String,
    esVideo: Boolean
) {
    val context = LocalContext.current
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val conversacionId = remember { channelName.removePrefix("chat_").toIntOrNull() ?: 0 }

    var estaSilenciado by remember { mutableStateOf(false) }
    var estaVideoApagado by remember { mutableStateOf(false) }
    var estadoConexion by remember { mutableStateOf("Conectando a la llamada...") }
    var uidRemoto by remember { mutableStateOf<Int?>(null) }
    var alguienSeUnio by remember { mutableStateOf(false) }
    var segundos by remember { mutableStateOf(0) }
    var finalizada by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val agoraManager = remember {
        val handler = object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                mainHandler.post { estadoConexion = "Llamando..." }
            }
            override fun onUserJoined(uid: Int, elapsed: Int) {
                mainHandler.post {
                    uidRemoto = uid
                    alguienSeUnio = true
                    estadoConexion = "En llamada"
                }
            }
            override fun onUserOffline(uid: Int, reason: Int) {
                mainHandler.post { finalizada = true }
            }
            override fun onError(err: Int) {
                android.util.Log.e("AGORA", "onError: $err")
            }
            override fun onConnectionStateChanged(state: Int, reason: Int) {
                android.util.Log.d("AGORA", "conexión: estado=$state razón=$reason")
            }
        }
        AgoraManager(context, BuildConfig.AGORA_APP_ID, handler)
    }

    suspend fun unirse() {
        try {
            val auth = "Bearer ${SessionManager.token}"
            val res = withContext(Dispatchers.IO) {
                RetrofitClient.instance.obtenerTokenLlamada(auth, conversacionId)
            }

            if (res.isSuccessful && res.body()?.token != null) {
                val body = res.body()!!
                val canal = body.canal ?: "chat_$conversacionId"

                withContext(Dispatchers.IO) {
                    agoraManager.unirseCanal(canal, body.token, esVideo)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No se pudo iniciar la llamada (${res.code()})", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }
    }

    fun colgar() {
        scope.launch(Dispatchers.IO) {
            if (alguienSeUnio) LlamadasManager.finalizar(conversacionId)
            else LlamadasManager.cancelar(conversacionId)
        }
        navController.popBackStack()
    }

    // Permisos
    val permisos = remember {
        buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (esVideo) add(Manifest.permission.CAMERA)
        }.toTypedArray()
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.all { it }) {
            scope.launch { unirse() }
        } else {
            Toast.makeText(context, "Se requieren permisos de audio y cámara", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        LlamadasManager.respuestaLlamada = null
        val concedidos = permisos.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (concedidos) {
            unirse()
        } else {
            lanzadorPermisos.launch(permisos)
        }
    }

    // El otro rechazó
    LaunchedEffect(LlamadasManager.respuestaLlamada) {
        if (LlamadasManager.respuestaLlamada == false) {
            LlamadasManager.respuestaLlamada = null
            Toast.makeText(context, "Llamada rechazada", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // El otro colgó
    LaunchedEffect(finalizada) {
        if (finalizada) {
            Toast.makeText(context, "La llamada terminó", Toast.LENGTH_SHORT).show()
            delay(800)
            navController.popBackStack()
        }
    }

    // Nadie contesta en 40 segundos
    LaunchedEffect(alguienSeUnio) {
        if (!alguienSeUnio) {
            delay(40_000)
            if (!alguienSeUnio) {
                Toast.makeText(context, "No contestó", Toast.LENGTH_SHORT).show()
                colgar()
            }
        }
    }

    // Cronómetro
    LaunchedEffect(alguienSeUnio) {
        while (alguienSeUnio) {
            delay(1000)
            segundos++
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                agoraManager.salirCanal()
                agoraManager.destruir()
            }
        }
    }

    val tiempo = "%02d:%02d".format(segundos / 60, segundos % 60)

    Box(Modifier.fillMaxSize().background(ServifyDarkNavy)) {

        // Video remoto a pantalla completa
        if (esVideo && uidRemoto != null) {
            AndroidView(
                factory = { ctx -> agoraManager.crearVistaRemota(ctx, uidRemoto!!) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.size(110.dp).clip(CircleShape).background(ServifyBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (esVideo) Icons.Default.Videocam else Icons.Default.Call,
                        null, tint = Color.White, modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    if (esVideo) "Videollamada Servify" else "Llamada de Voz",
                    color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (alguienSeUnio) "$estadoConexion · $tiempo" else estadoConexion,
                    color = ServifyCianSoft, fontSize = 14.sp
                )
            }
        }

        // Mi cámara en una ventana pequeña
        if (esVideo && !estaVideoApagado) {
            AndroidView(
                factory = { ctx -> agoraManager.crearVistaLocal(ctx) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .size(width = 110.dp, height = 150.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        }

        // Controles
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonLlamada(
                icono = if (estaSilenciado) Icons.Default.MicOff else Icons.Default.Mic,
                fondo = if (estaSilenciado) ServifyDangerBg else ServifySurface,
                color = if (estaSilenciado) ServifyDanger else ServifyBluePrimary,
                descripcion = "Silenciar"
            ) {
                estaSilenciado = !estaSilenciado
                agoraManager.alternarAudio(estaSilenciado)
            }

            BotonLlamada(
                icono = Icons.Default.CallEnd, fondo = ServifyDanger, color = Color.White,
                descripcion = "Colgar", tamano = 64
            ) { colgar() }

            if (esVideo) {
                BotonLlamada(
                    icono = if (estaVideoApagado) Icons.Default.VideocamOff else Icons.Default.Videocam,
                    fondo = if (estaVideoApagado) ServifyDangerBg else ServifySurface,
                    color = if (estaVideoApagado) ServifyDanger else ServifyBluePrimary,
                    descripcion = "Video"
                ) {
                    estaVideoApagado = !estaVideoApagado
                    agoraManager.alternarVideo(estaVideoApagado)
                }
                BotonLlamada(
                    icono = Icons.Default.Cameraswitch, fondo = ServifySurface,
                    color = ServifyBluePrimary, descripcion = "Girar cámara"
                ) { agoraManager.cambiarCamara() }
            }
        }
    }
}

@Composable
private fun BotonLlamada(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    fondo: Color,
    color: Color,
    descripcion: String,
    tamano: Int = 56,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(tamano.dp).clip(CircleShape).background(fondo)
    ) {
        Icon(icono, contentDescription = descripcion, tint = color)
    }
}
