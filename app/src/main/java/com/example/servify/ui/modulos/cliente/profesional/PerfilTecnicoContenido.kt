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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilTecnicoContenido(perfil: ProfesionalPerfil) {
    val verde = Color(0xFF1B7B61)
    val iniciales = perfil.nombreCompleto.split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" }

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

        // Cabecera
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(72.dp).background(Color(0xFFE2F3EE), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(iniciales, color = verde, fontWeight = FontWeight.Bold, fontSize = 24.sp) }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(perfil.nombreCompleto, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(perfil.oficio, color = verde, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "%.1f (${perfil.totalResenas} reseñas)".format(perfil.calificacionPromedio),
                        fontSize = 13.sp, color = Color.DarkGray
                    )
                }
            }
        }

        // Insignias
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Insignia("${perfil.anosExperiencia} años de experiencia", Color(0xFFE2F3EE), verde)
            if (perfil.trabajosCompletados > 0)
                Insignia("${perfil.trabajosCompletados} trabajos completados", Color(0xFFE0E7FF), Color(0xFF4338CA))
            if (perfil.esVip == 1)
                Insignia("⭐ VIP", Color(0xFFFEF3C7), Color(0xFFD97706))
        }

        // Sobre mí
        Seccion("Sobre mí") {
            Text(
                perfil.biografia?.takeIf { it.isNotBlank() }
                    ?: "Este profesional aún no ha escrito su descripción.",
                fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp
            )
        }

        // Especialidades
        if (perfil.especialidades.isNotEmpty()) {
            Seccion("Especialidades") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    perfil.especialidades.forEach { Insignia(it, Color(0xFFF1F5F9), Color(0xFF334155)) }
                }
            }
        }

        // Reseñas
        Seccion("Reseñas y comentarios") {
            if (perfil.resenas.isEmpty()) {
                Surface(
                    color = Color(0xFFF8F9FA), shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Aún sin reseñas. Las opiniones aparecerán aquí cuando termine sus primeros trabajos.",
                        modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Color.Gray
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    perfil.resenas.forEach { r ->
                        Surface(
                            color = Color.White, shape = RoundedCornerShape(12.dp),
                            shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(r.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "★".repeat(r.calificacion.coerceIn(0, 5)),
                                        color = Color(0xFFFFB800), fontSize = 14.sp
                                    )
                                }
                                if (!r.comentario.isNullOrBlank()) {
                                    Text(r.comentario, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
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
        Text(titulo, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        contenido()
    }
}

@Composable
private fun Insignia(texto: String, fondo: Color, colorTexto: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(10.dp)) {
        Text(
            texto, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colorTexto
        )
    }
}