package com.example.servify.ui.modulos.cliente.profesional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilTecnicoContenido(perfil: ProfesionalPerfil) {
    val iniciales = perfil.nombreCompleto.split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" }

    val categoriaPrincipal = perfil.oficio.ifBlank {
        perfil.especialidades.firstOrNull() ?: "Técnico Profesional"
    }

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

        // Cabecera Principal con Avatar, Nombre, Badge de Categoría y Calificación
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(72.dp)
                    .background(ServifyCianSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = ServifyBluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = perfil.nombreCompleto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )

                Spacer(Modifier.height(4.dp))

                // Badge de la Categoría/Oficio
                Surface(
                    color = ServifyCianSoft.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = categoriaPrincipal,
                        color = ServifyBluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = ServifyStarYellow, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "%.1f (%d reseñas)".format(perfil.calificacionPromedio, perfil.totalResenas),
                        fontSize = 13.sp,
                        color = ServifyTextMuted
                    )
                }
            }
        }

        // Insignias destacadas de experiencia, trabajos y VIP
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Insignia("${perfil.anosExperiencia} años de experiencia", ServifyCianSoft, ServifyBluePrimary)
            if (perfil.trabajosCompletados > 0)
                Insignia("${perfil.trabajosCompletados} trabajos completados", Color(0xFFE0E7FF), Color(0xFF4338CA))
            if (perfil.esVip == 1)
                Insignia("⭐ VIP", ServifyPendingBg, ServifyPendingText)
        }

        Seccion("Sobre mí") {
            Text(
                text = perfil.biografia?.takeIf { it.isNotBlank() }
                    ?: "Este profesional aún no ha escrito su descripción.",
                fontSize = 14.sp,
                color = ServifyTextMuted,
                lineHeight = 20.sp
            )
        }

        if (perfil.especialidades.isNotEmpty()) {
            Seccion("Otras Especialidades") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    perfil.especialidades.forEach { Insignia(it, ServifyInputBackground, ServifyTextTitle) }
                }
            }
        }

        Seccion("Reseñas y comentarios") {
            if (perfil.resenas.isEmpty()) {
                Surface(
                    color = ServifySurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Aún sin reseñas. Las opiniones aparecerán aquí cuando termine sus primeros trabajos.",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp,
                        color = ServifyTextMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    perfil.resenas.forEach { r ->
                        Surface(
                            color = ServifySurface,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = r.autorNombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = ServifyTextTitle
                                    )
                                    Text(
                                        text = "★".repeat(r.calificacion.coerceIn(0, 5)),
                                        color = ServifyStarYellow,
                                        fontSize = 14.sp
                                    )
                                }
                                if (!r.comentario.isNullOrBlank()) {
                                    Text(
                                        text = r.comentario,
                                        fontSize = 13.sp,
                                        color = ServifyTextMuted,
                                        lineHeight = 18.sp
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

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)
        contenido()
    }
}

@Composable
private fun Insignia(texto: String, fondo: Color, colorTexto: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}