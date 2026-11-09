package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

@Composable
fun TarjetaPlan(
    titulo: String,
    precio: String,
    badge: String? = null,
    beneficios: List<String>,
    seleccionado: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (seleccionado) 2.dp else 1.dp,
            color = if (seleccionado) ServifyBluePrimary else ServifyBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )

                if (badge != null) {
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBluePrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = precio,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ServifyBluePrimary
                )
                Text(
                    text = "/mes",
                    fontSize = 13.sp,
                    color = ServifyTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = ServifyBorder,
                thickness = 0.5.dp
            )

            beneficios.forEach { beneficio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = beneficio,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ServifyTextBody
                    )
                }
            }
        }
    }
}