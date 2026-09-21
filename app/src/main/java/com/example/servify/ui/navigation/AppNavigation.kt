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
import com.example.servify.ui.modulos.modulos_compartidos.auth.OlvidePasswordScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.RegistroClienteScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.RegistroTrabajadorScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.SeleccionRolScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.TipoRol
import com.example.servify.ui.modulos.trabajador.muro.MuroSolicitudesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val rutasConBarraInferior = listOf(
        Rutas.Home.ruta,
        Rutas.MuroSolicitudes.ruta,
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
            composable(route = Rutas.Login.ruta) {
                LoginScreen(
                    onLoginExitoso = { usuario ->
                        val rutaDestino = when (usuario.rol.lowercase()) {
                            "tecnico", "trabajador" -> Rutas.MuroSolicitudes.ruta
                            else -> Rutas.Home.ruta
                        }
                        navController.navigate(rutaDestino) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    },
                    onIrARegistro = {
                        navController.navigate(Rutas.SeleccionRol.ruta)
                    },
                    onOlvidastePassword = {
                        navController.navigate(Rutas.RecuperarPassword.ruta)
                    }
                )
            }
            // Pantalla de Selección de Rol
            composable(Rutas.SeleccionRol.ruta) {
                SeleccionRolScreen(
                    onContinuar = { rol ->
                        when (rol) {
                            TipoRol.CLIENTE -> navController.navigate(Rutas.RegistroCliente.ruta)
                            TipoRol.TRABAJADOR -> navController.navigate(Rutas.RegistroTrabajador.ruta)
                        }
                    },
                    onIrALogin = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            //pantalla de olvidaste la contra
            composable(Rutas.RecuperarPassword.ruta) {
                OlvidePasswordScreen(
                    onPasswordRestablecido = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    },
                    onVolverALogin = {
                        navController.popBackStack()
                    }
                )
            }
            // Registro Cliente
            composable(Rutas.RegistroCliente.ruta) {
                RegistroClienteScreen(
                    onRegistroExitoso = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    },
                    onIrALogin = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            // Registro Trabajador
            composable(Rutas.RegistroTrabajador.ruta) {
                RegistroTrabajadorScreen(
                    onRegistroExitoso = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    },
                    onIrALogin = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.MuroSolicitudes.ruta) {
                MuroSolicitudesScreen(navController = navController)
            }

            composable(Rutas.Home.ruta) {
                HomeScreen(
                    navController = navController
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