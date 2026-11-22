package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AvisoModeracion(
    val solicitudId: Int,
    val titulo: String,
    val categoria: String,
    val motivo: String
)

private data class PreguntaFrecuente(
    val pregunta: String,
    val respuesta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyudaSoporteScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var cargandoAvisos by remember { mutableStateOf(true) }
    var avisosModeracion by remember { mutableStateOf<List<AvisoModeracion>>(emptyList()) }
    var faqExpandida by remember { mutableStateOf<Int?>(0) }

    val preguntasFrecuentes = remember {
        listOf(
            PreguntaFrecuente(
                pregunta = "¿Por qué mi solicitud puede ser dada de baja por Administración?",
                respuesta = "El equipo de moderación revisa las solicitudes publicadas para garantizar la seguridad de la comunidad. Una solicitud puede darse de baja si contiene información falsa, lenguaje ofensivo, spam, datos personales sensibles en el título o descripción, o servicios no permitidos en Servify."
            ),
            PreguntaFrecuente(
                pregunta = "¿Cómo apelo una decisión de moderación?",
                respuesta = "Si consideras que tu solicitud fue dada de baja por error, revisa el motivo detallado en la sección superior y presiona el botón 'Contactar a Soporte' indicando el ID de tu solicitud para que un administrador la reevalúe."
            ),
            PreguntaFrecuente(
                pregunta = "¿Cómo funciona el pago y cierre con código QR?",
                respuesta = "Al finalizar el servicio, el cliente selecciona el método de pago (efectivo o tarjeta) en 'Mis Solicitudes' para generar un código QR único. El técnico escanea ese código desde su dispositivo para confirmar la finalización segura del trabajo."
            ),
            PreguntaFrecuente(
                pregunta = "¿Cómo contacto a un técnico o cliente?",
                respuesta = "Una vez que aceptas una propuesta o contratación directa, se habilita automáticamente un chat privado en la pestaña 'Mensajes' desde donde también puedes iniciar llamadas de voz o videollamadas."
            )
        )
    }

    LaunchedEffect(Unit) {
        val token = SessionManager.token
        if (token.isNullOrBlank()) {
            cargandoAvisos = false
            return@LaunchedEffect
        }

        try {
            val lista = withContext(Dispatchers.IO) {
                val res = RetrofitClient.instance.obtenerMisSolicitudes("Bearer $token")
                if (res.isSuccessful) {
                    val body = res.body() ?: emptyList()
                    body.mapNotNull { sol ->
                        val partes = sol.descripcion.split("[Dada de baja por Administración — Motivo:")
                        if (partes.size > 1) {
                            AvisoModeracion(
                                solicitudId = sol.id,
                                titulo = sol.titulo,
                                categoria = sol.categoriaNombre,
                                motivo = partes[1].removeSuffix("]").trim()
                            )
                        } else {
                            null
                        }
                    }
                } else {
                    emptyList()
                }
            }
            avisosModeracion = lista
        } catch (_: Exception) {
            avisosModeracion = emptyList()
        } finally {
            cargandoAvisos = false
        }
    }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ayuda y Soporte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(ServifySurface, CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp)
        ) {
            // ==========================================
            // SECCIÓN 1: AVISOS DE ADMINISTRACIÓN / MODERACIÓN
            // ==========================================
            item {
                Text(
                    text = "Avisos de Administración",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Historial de notificaciones y resoluciones sobre tus publicaciones.",
                    fontSize = 12.sp,
                    color = ServifyTextMuted
                )
            }

            if (cargandoAvisos) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ServifyBluePrimary,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }
            } else if (avisosModeracion.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = ServifyCianSoft,
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = ServifyBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sin avisos de moderación",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyTextTitle
                                )
                                Text(
                                    text = "Tu cuenta no tiene solicitudes dadas de baja ni sanciones activas.",
                                    fontSize = 12.sp,
                                    color = ServifyTextMuted,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            } else {
                items(avisosModeracion, key = { it.solicitudId }) { aviso ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GppBad,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Solicitud dada de baja",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ID #${aviso.solicitudId}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }

                            Text(
                                text = "${aviso.titulo} (${aviso.categoria})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7F1D1D)
                            )

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Motivo registrado por el administrador:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyTextMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = aviso.motivo,
                                        fontSize = 13.sp,
                                        color = ServifyTextTitle,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN 2: PREGUNTAS FRECUENTES (FAQ)
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Preguntas Frecuentes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )
            }

            items(preguntasFrecuentes.size) { index ->
                val item = preguntasFrecuentes[index]
                val expandido = faqExpandida == index

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            faqExpandida = if (expandido) null else index
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.pregunta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextTitle,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = ServifyBluePrimary
                            )
                        }

                        if (expandido) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.respuesta,
                                fontSize = 12.sp,
                                color = ServifyTextMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN 3: CONTACTO DIRECTO CON SOPORTE
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "¿Necesitas ayuda adicional?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextTitle
                        )
                        Text(
                            text = "Si tienes dudas sobre una moderación o un problema con un servicio, escríbenos directamente.",
                            fontSize = 12.sp,
                            color = ServifyTextMuted,
                            lineHeight = 17.sp
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:soporte@servify.app")
                                    putExtra(Intent.EXTRA_SUBJECT, "Soporte / Apelación Servify")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Correo de soporte: soporte@servify.app",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Contactar a Soporte",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}