package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearResenaScreen(
    solicitudId: Int,
    destinatarioId: Int,
    navController: NavController,
    viewModel: CrearResenaViewModel = viewModel()
) {
    val context = LocalContext.current
    val esTecnico = SessionManager.esTecnico

    // 🚀 Redirección limpia tras enviar la reseña con éxito
    LaunchedEffect(viewModel.resenaEnviada) {
        if (viewModel.resenaEnviada) {
            Toast.makeText(context, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show()

            val rutaInicio = if (esTecnico) Rutas.MuroSolicitudes.ruta else Rutas.Home.ruta
            navController.navigate(rutaInicio) {
                popUpTo(Rutas.CrearResena.ruta) { inclusive = true }
            }
        }
    }

    LaunchedEffect(solicitudId) { viewModel.cargarExistente(solicitudId) }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

            // 🌟 Si el backend dice que ya la calificó, podemos activar un estado local de bloqueo
            if (mensaje.contains("anteriormente", ignoreCase = true)) {
                viewModel.yaCalificado = true
            }

            viewModel.mensajeError = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (esTecnico) "Calificación del Cliente" else "Calificación del Técnico",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ServifySurface,
                    titleContentColor = ServifyTextTitle
                )
            )
        }
    ) { paddingValues ->
        if (viewModel.verificando) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ServifyBluePrimary)
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (viewModel.yaCalificado) "¡Servicio Calificado!" else "¿Cómo fue tu experiencia?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle
            )

            Text(
                text = if (viewModel.yaCalificado)
                    "Ya has enviado una valoración para este servicio anteriormente."
                else if (esTecnico)
                    "Tu opinión sobre el cliente ayuda a mantener una comunidad segura en Servify."
                else
                    "Tu valoración ayuda a mejorar la calidad del servicio de nuestros profesionales.",
                fontSize = 14.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de estrellas (Interactivo si no ha calificado, Fijo si ya calificó)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= viewModel.calificacion) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Estrella $i",
                        tint = if (i <= viewModel.calificacion) Color(0xFFFFB800) else ServifyTextMuted,
                        modifier = Modifier
                            .size(44.dp)
                            .then(
                                if (!viewModel.yaCalificado) {
                                    Modifier.clickable { viewModel.calificacion = i }
                                } else {
                                    Modifier // 🚫 Sin clic si ya está calificado
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.comentario,
                onValueChange = { if (!viewModel.yaCalificado) viewModel.comentario = it },
                label = { Text(if (viewModel.yaCalificado) "Tu comentario registrado" else "Escribe un comentario u opinión...") },
                readOnly = viewModel.yaCalificado, // 🚫 Bloqueado si ya calificó
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de envío (Se oculta o deshabilita si ya fue calificado)
            if (!viewModel.yaCalificado) {
                Button(
                    onClick = { viewModel.enviarResena(solicitudId, destinatarioId) },
                    enabled = !viewModel.cargando && viewModel.calificacion > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                ) {
                    if (viewModel.cargando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Enviar Calificación",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = ServifyInputBackground
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "⭐ Ya has calificado este trabajo",
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}