package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.ui.theme.*

@Composable
fun TarjetaTecnicoDestacado(
    profesional: ProfesionalPerfil,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nombre = profesional.nombreCompleto.ifBlank { "Técnico" }
    val oficio = profesional.oficio.ifBlank { "Servicio" }

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        border = BorderStroke(1.2.dp, Color(0xFFFDE68A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = oficio,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ServifyStarYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(profesional.calificacionPromedio),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ServifyTextTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${profesional.anosExperiencia} años de exp.",
                fontSize = 11.sp,
                color = ServifyTextMuted
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verificado",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "Verificado",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF047857)
                    )
                }

                Text(
                    text = "Contactar ›",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyBluePrimary
                )
            }
        }
    }
}