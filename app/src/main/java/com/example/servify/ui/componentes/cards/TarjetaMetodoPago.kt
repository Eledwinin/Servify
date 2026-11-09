package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

@Composable
fun TarjetaMetodoPago(
    etiquetaBadge: String,
    ultimosDigitosOTexto: String,
    colorFondoBadge: Color,
    colorTextoBadge: Color,
    seleccionado: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (seleccionado) 1.5.dp else 1.dp,
            color = if (seleccionado) Color(0xFF059669) else ServifyBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = colorFondoBadge,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(width = 46.dp, height = 28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = etiquetaBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorTextoBadge
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = ultimosDigitosOTexto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ServifyTextTitle
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (seleccionado) Color(0xFF059669) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (seleccionado) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(20.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.5.dp, ServifyBorder)
                    ) {}
                }
            }
        }
    }
}