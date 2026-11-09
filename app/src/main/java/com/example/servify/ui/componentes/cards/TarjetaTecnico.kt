package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.ui.theme.*

@Composable
fun TarjetaTecnico(
    profesional: ProfesionalPerfil,
    textoDistancia: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 🌟 Distinguimos entre VIP real y Plan Semanal
    val esSemanal = profesional.tipoPlan?.contains("Semanal", ignoreCase = true) == true
    val esVipReal = profesional.esVip == 1 && !esSemanal

    val nombre = profesional.nombreCompleto.ifBlank { "Técnico" }
    val oficio = profesional.oficio.ifBlank { "Servicio" }

    val iniciales = nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "PR" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = if (esVipReal || esSemanal) 1.5.dp else 1.dp,
            color = when {
                esVipReal -> Color(0xFFFDE68A)  // Dorado para VIP PRO
                esSemanal -> Color(0xFF0284C7)  // Celeste/Azul para Plan Semanal
                else -> ServifyBorder
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            esVipReal -> Color(0xFFFEF3C7) // Fondo dorado claro
                            esSemanal -> Color(0xFFE0F2FE) // Fondo celeste claro
                            else -> ServifyCianSoft
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = when {
                        esVipReal -> Color(0xFFB45309)
                        esSemanal -> Color(0xFF0284C7)
                        else -> ServifyBluePrimary
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ServifyTextTitle
                    )

                    // 🌟 ETIQUETA DINÁMICA: VIP VS SEMANAL VS VERIFICADO NORMAL
                    when {
                        esVipReal -> {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VIP",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                        esSemanal -> {
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "SEMANAL",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verificado",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${profesional.anosExperiencia} años de experiencia",
                    fontSize = 12.sp,
                    color = ServifyTextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = ServifyInputBackground,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = oficio,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ServifyTextBody
                        )
                    }

                    if (textoDistancia != null) {
                        Surface(
                            color = ServifyCianSoft,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📍 $textoDistancia",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyBluePrimary
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ServifyStarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(profesional.calificacionPromedio),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ServifyTextTitle
                    )
                }
            }
        }
    }
}