package com.example.servify.ui.modulos.modulos_compartidos.perfil

//centro de cuenta del usuario. Muestra información personal, ajustes de configuración,
//enlace al switch 'Cambiar a Modo Trabajador/Cliente' y botón de cierre de sesión

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PerfilScreen(onCerrarSesion: () -> Unit = {}) {
    Text(text = "Pantalla Perfil")
}