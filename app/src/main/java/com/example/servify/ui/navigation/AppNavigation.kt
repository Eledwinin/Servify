package com.example.servify.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.navegacion.BarraNavegacion
import com.example.servify.ui.modulos.admin.AdminDashboardScreen
import com.example.servify.ui.modulos.admin.AdminSolicitudesScreen
import com.example.servify.ui.modulos.admin.AdminUsuariosScreen
import com.example.servify.ui.modulos.cliente.contratacion.NuevaSolicitudScreen
import com.example.servify.ui.modulos.cliente.gestion_perfil.EditarPerfilClienteScreen
import com.example.servify.ui.modulos.cliente.home.HomeScreen
import com.example.servify.ui.modulos.cliente.home.MapaScreen
import com.example.servify.ui.modulos.cliente.profesional.DetalleTecnicoScreen
import com.example.servify.ui.modulos.cliente.propuestas.PropuestasScreen
import com.example.servify.ui.modulos.modulos_compartidos.actividad.ActividadScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.LoginScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.OlvidePasswordScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.RegistroClienteScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.RegistroTrabajadorScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.SeleccionRolScreen
import com.example.servify.ui.modulos.modulos_compartidos.auth.TipoRol
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatScreen
import com.example.servify.ui.modulos.modulos_compartidos.messages.ConversacionesScreen
import com.example.servify.ui.modulos.modulos_compartidos.pagos.MetodosPagoScreen
import com.example.servify.ui.modulos.modulos_compartidos.pagos.PagoExitosoScreen
import com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilScreen
import com.example.servify.ui.modulos.trabajador.PlanesMembresiaScreen
import com.example.servify.ui.modulos.trabajador.muro.DetalleAveriaScreen
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
                        val destinoFinal = if (destino == Rutas.Home.ruta || destino == Rutas.MuroSolicitudes.ruta) {
                            if (SessionManager.esTecnico) Rutas.MuroSolicitudes.ruta else Rutas.Home.ruta
                        } else {
                            destino
                        }

                        navController.navigate(destinoFinal) {
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
                        SessionManager.guardarSesion(SessionManager.token, usuario)

                        val rutaDestino = when {
                            usuario.rol.equals("admin", ignoreCase = true) -> Rutas.AdminDashboard.ruta
                            SessionManager.esTecnico -> Rutas.MuroSolicitudes.ruta
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

            composable("detalle_tecnico/{idTecnico}") { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("idTecnico") ?: "1"
                val idTecnico = idString.toIntOrNull() ?: 1

                DetalleTecnicoScreen(
                    idTecnico = idTecnico,
                    onBackClick = { navController.popBackStack() },
                    onContratarClick = { idContratado ->
                        navController.navigate("contratacion/$idContratado")
                    }
                )
            }

            composable(Rutas.MuroSolicitudes.ruta) {
                MuroSolicitudesScreen(navController = navController)
            }

            composable(Rutas.Home.ruta) {
                HomeScreen(navController = navController)
            }

            // Destino del Mapa de Mapbox
            composable(Rutas.Mapa.ruta) {
                MapaScreen(navController = navController)
            }

            composable(Rutas.Actividad.ruta) {
                ActividadScreen(navController = navController)
            }

            composable("detalle_averia/{idSolicitud}") { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("idSolicitud") ?: "1"
                val idSolicitud = idString.toIntOrNull() ?: 1

                DetalleAveriaScreen(
                    idSolicitud = idSolicitud,
                    navController = navController,
                    onBackClick = { navController.popBackStack() },
                    onPostularseClick = {}
                )
            }

            composable(Rutas.PlanesMembresia.ruta) {
                val perfilViewModel: com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilViewModel = viewModel()
                PlanesMembresiaScreen(
                    navController = navController,
                    onPlanAdquirido = {
                        perfilViewModel.cambiarMembresiaVip(esVip = true) {
                            SessionManager.activarSuscripcionVip()
                            navController.navigate(Rutas.PagoExitoso.ruta)
                        }
                    }
                )
            }

            composable(Rutas.MetodosPago.ruta) {
                val perfilViewModel: com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilViewModel = viewModel()
                MetodosPagoScreen(
                    navController = navController,
                    onPagoConfirmado = {
                        perfilViewModel.cambiarMembresiaVip(esVip = true) {
                            SessionManager.activarSuscripcionVip()
                            navController.navigate(Rutas.PagoExitoso.ruta) {
                                popUpTo(Rutas.PlanesMembresia.ruta) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Rutas.PagoExitoso.ruta) {
                PagoExitosoScreen(navController = navController)
            }

            composable(Rutas.Perfil.ruta) {
                PerfilScreen(
                    token = SessionManager.token ?: "",
                    onIrAEditarPerfil = {
                        navController.navigate(Rutas.EditarPerfil.ruta)
                    },
                    onCerrarSesion = {
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.EditarPerfil.ruta) {
                EditarPerfilClienteScreen(
                    token = SessionManager.token ?: "",
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Rutas.NuevaSolicitud.ruta) {
                NuevaSolicitudScreen(
                    onBackClick = { navController.popBackStack() },
                    onContinuarClick = {
                        navController.navigate(Rutas.Actividad.ruta) {
                            popUpTo(Rutas.Home.ruta)
                        }
                    }
                )
            }

            composable(
                route = Rutas.PropuestasSolicitud.ruta,
                arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
            ) { backStackEntry ->
                val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: 0
                PropuestasScreen(
                    solicitudId = solicitudId,
                    onBackClick = { navController.popBackStack() },
                    onChatClick = { convId, nombre ->
                        navController.navigate(Rutas.Chat.crearRuta(convId, nombre))
                    }
                )
            }

            composable(
                route = Rutas.Chat.ruta,
                arguments = listOf(
                    navArgument("conversacionId") { type = NavType.IntType },
                    navArgument("nombre") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                ChatScreen(
                    conversacionId = backStackEntry.arguments?.getInt("conversacionId") ?: 0,
                    nombreOtro = backStackEntry.arguments?.getString("nombre") ?: "",
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Rutas.Mensajes.ruta) {
                ConversacionesScreen(navController = navController)
            }

            // Administrador destinos
            composable(Rutas.AdminDashboard.ruta) {
                AdminDashboardScreen(
                    onIrAUsuarios = { rolFiltro ->
                        navController.navigate(Rutas.AdminUsuarios.crearRuta(rolFiltro))
                    },
                    onIrASolicitudes = {
                        navController.navigate(Rutas.AdminSolicitudes.ruta)
                    },
                    onCerrarSesion = {
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Rutas.AdminUsuarios.ruta,
                arguments = listOf(navArgument("rol") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { backStackEntry ->
                val rol = backStackEntry.arguments?.getString("rol") ?: ""
                AdminUsuariosScreen(
                    filtroInicial = rol,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.AdminSolicitudes.ruta) {
                AdminSolicitudesScreen(
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}