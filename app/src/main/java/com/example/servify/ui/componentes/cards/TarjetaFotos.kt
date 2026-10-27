package com.example.servify.ui.componentes.cards

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun TarjetaFotos(
    fotos: List<Uri>,
    maxFotos: Int,
    onAñadirFotoClick: () -> Unit,
    onQuitarFoto: (Uri) -> Unit
) {
    val verde = Color(0xFF1B7B61)



    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(24.dp).background(Color(0xFFE2F3EE), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("2", fontSize = 12.sp, color = verde, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(8.dp))
                Text("Fotos de Referencia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                Text("${fotos.size}/$maxFotos", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(fotos, key = { it.toString() }) { uri ->
                    Box(Modifier.size(80.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Foto seleccionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                        IconButton(
                            onClick = { onQuitarFoto(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(22.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                if (fotos.size < maxFotos) {
                    item {
                        Column(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { onAñadirFotoClick() },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                            Text("Añadir", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}