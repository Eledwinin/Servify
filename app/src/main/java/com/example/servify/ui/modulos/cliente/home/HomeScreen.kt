package com.example.servify.ui.modulos.cliente.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

//Pantalla principal del cliente. Muestra el encabezado de bienvenida, barra de búsqueda en tiempo real,
// cuadrícula interactiva de categorías de servicios y lista de técnicos mejor valorados
@Composable
fun HomeScreen(onIrADetalleTecnico: (String) -> Unit = {}) {
    Text(text = "Pantalla Home Cliente")
}