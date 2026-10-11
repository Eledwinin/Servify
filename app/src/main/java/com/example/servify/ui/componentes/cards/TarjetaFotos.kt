package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

@Composable
fun TarjetaFotos(
    onAñadirFotoClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(ServifyTealAvatar),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = ServifyGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fotos de Referencia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ServifyTextTitle)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Botón para añadir foto
                Surface(
                    onClick = onAñadirFotoClick,
                    shape = RoundedCornerShape(12.dp),
                    color = ServifyInputBackground,
                    modifier = Modifier.size(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir foto", tint = ServifyGreenPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Añadir", fontSize = 11.sp, color = ServifyGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}