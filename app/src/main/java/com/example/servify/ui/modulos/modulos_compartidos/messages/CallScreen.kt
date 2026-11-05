package com.example.servify.ui.modulos.modulos_compartidos.messages


import android.Manifest
import com.example.servify.BuildConfig
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
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
import com.example.servify.data.agora.AgoraManager
import com.example.servify.ui.theme.*
import io.agora.rtc2.IRtcEngineEventHandler


@Composable
fun CallScreen(
    navController: NavController,
    channelName: String,
    esVideo: Boolean
) {
    val context = LocalContext.current
    var estaSilenciado by remember { mutableStateOf(false) }
    var estaVideoApagado by remember { mutableStateOf(!esVideo) }
    var estadoConexion by remember { mutableStateOf("Conectando a la llamada...") }


    val agoraAppId = BuildConfig.AGORA_APP_ID

    val agoraManager = remember {
        val eventHandler = object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                estadoConexion = "En llamada"
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                estadoConexion = "Conectado con el experto"
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                estadoConexion = "El usuario colgó"
            }
        }
        AgoraManager(context, agoraAppId, eventHandler)
    }

    // Permisos necesarios de cámara y audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            agoraManager.unirseCanal(channelName, null)
        } else {
            Toast.makeText(context, "Se requieren permisos de audio y cámara", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        val micPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val camPermission = if (esVideo) ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED else true

        if (micPermission && camPermission) {
            agoraManager.unirseCanal(channelName, null)
        } else {
            val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
            if (esVideo) permissionsToRequest.add(Manifest.permission.CAMERA)
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            agoraManager.salirCanal()
            agoraManager.destruir()
        }
    }

    // Interfaz de la Llamada con la paleta de colores corporativos
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyDarkNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Avatar o indicador central
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(ServifyBluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esVideo) Icons.Default.Videocam else Icons.Default.CallEnd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (esVideo) "Videollamada Servify" else "Llamada de Voz",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = estadoConexion,
                color = ServifyCianSoft,
                fontSize = 14.sp
            )
        }

        // Botones de control inferiores (Silenciar, Video, Colgar)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Micrófono
            IconButton(
                onClick = {
                    estaSilenciado = !estaSilenciado
                    agoraManager.alternarAudio(estaSilenciado)
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (estaSilenciado) ServifyDangerBg else ServifySurface)
            ) {
                Icon(
                    imageVector = if (estaSilenciado) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Silenciar",
                    tint = if (estaSilenciado) ServifyDanger else ServifyBluePrimary
                )
            }

            // Botón Colgar
            IconButton(
                onClick = {
                    agoraManager.salirCanal()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ServifyDanger)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "Colgar",
                    tint = Color.White
                )
            }

            // Botón Video (Solo si es videollamada)
            if (esVideo) {
                IconButton(
                    onClick = {
                        estaVideoApagado = !estaVideoApagado
                        agoraManager.alternarVideo(estaVideoApagado)
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (estaVideoApagado) ServifyDangerBg else ServifySurface)
                ) {
                    Icon(
                        imageVector = if (estaVideoApagado) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Video",
                        tint = if (estaVideoApagado) ServifyDanger else ServifyBluePrimary
                    )
                }
            }
        }
    }
}