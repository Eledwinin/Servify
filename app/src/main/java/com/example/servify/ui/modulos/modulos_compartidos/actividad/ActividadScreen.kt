package com.example.servify.ui.modulos.modulos_compartidos.actividad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Módulo de seguimiento de servicios (Mis Trabajos). Organizado en pestañas 'En Curso'
// (con tarjeta de estado del servicio) e 'Historial' (trabajos completados o cancelados).
@Composable
fun ActividadScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla Actividad")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Te lleva directo al perfil del técnico con ID 1
                navController.navigate("detalle_tecnico/1")
            }
        ) {
            Text("Probar Perfil Técnico (ID: 1)")
        }
    }
}