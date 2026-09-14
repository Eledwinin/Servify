package com.example.servify.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.servify.ui.componentes.navegacion.BarraNavegacion
import com.example.servify.ui.modulos.cliente.home.HomeScreen
import com.example.servify.ui.modulos.modulos_compartidos.actividad.ActividadScreen
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatScreen
import com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val rutasConBarraInferior = listOf(
        Rutas.Home.ruta,
        Rutas.Actividad.ruta,
        Rutas.Mensajes.ruta,
        Rutas.Perfil.ruta
    )

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarraInferior) {
                BarraNavegacion(
                    rutaActual = rutaActual,
                    onItemClick = { destino ->
                        navController.navigate(destino) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValores ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta,
            modifier = Modifier.padding(paddingValores)
        ) {
            composable(Rutas.Login.ruta) {
                LoginScreen(
                    onLoginExitoso = {
                        navController.navigate(Rutas.Home.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.Home.ruta) {
                HomeScreen(
                    onIrADetalleTecnico = { id ->
                        navController.navigate(Rutas.DetalleTecnico.crearRuta(id))
                    }
                )
            }

            composable(Rutas.Actividad.ruta) {
                ActividadScreen()
            }

            composable(Rutas.Mensajes.ruta) {
                ChatScreen()
            }

            composable(Rutas.Perfil.ruta) {
                PerfilScreen(
                    onCerrarSesion = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}