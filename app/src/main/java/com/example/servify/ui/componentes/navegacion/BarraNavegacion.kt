package com.example.servify.ui.componentes.navegacion


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.servify.ui.navigation.Rutas

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

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            val seleccionado = rutaActual == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onItemClick(item.ruta) },
                icon = { Icon(imageVector = item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF168067),
                    selectedTextColor = Color(0xFF168067),
                    indicatorColor = Color(0xFFE8F3F1),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}