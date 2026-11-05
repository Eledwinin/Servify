package com.example.servify.ui.componentes.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

sealed class ItemNav(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Explorar : ItemNav(Rutas.Home.ruta, "Explorar", Icons.Default.Search)
    object Actividad : ItemNav(Rutas.Actividad.ruta, "Actividad", Icons.Default.Assignment)
    object Chat : ItemNav(Rutas.Mensajes.ruta, "Mensajes", Icons.Default.Chat)
    object Perfil : ItemNav(Rutas.Perfil.ruta, "Perfil", Icons.Default.Person)
}

@Composable
fun BarraNavegacion(
    rutaActual: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        ItemNav.Explorar,
        ItemNav.Actividad,
        ItemNav.Chat,
        ItemNav.Perfil
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ServifyBackground,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val seleccionado = rutaActual == item.ruta

                val colorIconoTexto = if (seleccionado) ServifyBluePrimary else ServifyTextMuted

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onItemClick(item.ruta) }
                        .background(if (seleccionado) ServifyCianSoft else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = item.titulo,
                        tint = colorIconoTexto,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.titulo,
                        fontSize = 11.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                        color = colorIconoTexto
                    )
                }
            }
        }
    }
}