package com.example.servify.ui.modulos.trabajador.muro

//muro de trabajos disponibles para técnicos, lista solicitudes de clientes en un radio
//cercano mostrando categoría, distancia en kilómetros y botón de acción 'Postularme'

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MuroSolicitudesScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Muro de Solicitudes - Técnico", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Aquí verás las solicitudes publicadas por los clientes.")
        }
    }
}