package com.example.servify.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.servify.data.LlamadasManager
import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.navegacion.BarraNavegacion
import com.example.servify.ui.modulos.admin.AdminDashboardScreen
import com.example.servify.ui.modulos.admin.AdminSolicitudesScreen
import com.example.servify.ui.modulos.admin.AdminUsuariosScreen
import com.example.servify.ui.modulos.cliente.contratacion.CheckoutPagoScreen
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
import com.example.servify.ui.modulos.modulos_compartidos.messages.CallScreen
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatScreen
import com.example.servify.ui.modulos.modulos_compartidos.messages.ConversacionesScreen
import com.example.servify.ui.modulos.modulos_compartidos.pagos.MetodosPagoScreen
import com.example.servify.ui.modulos.modulos_compartidos.pagos.PagoExitosoScreen
import com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilScreen
import com.example.servify.ui.modulos.modulos_compartidos.perfil.PerfilViewModel
import com.example.servify.ui.modulos.modulos_compartidos.perfil.ResenasScreen
import com.example.servify.ui.modulos.trabajador.PlanesMembresiaScreen
import com.example.servify.ui.modulos.trabajador.gestion_perfil.EditarPerfilTrabajadorScreen
import com.example.servify.ui.modulos.trabajador.muro.DetalleAveriaScreen
import com.example.servify.ui.modulos.trabajador.muro.EnviarPropuestaScreen
import com.example.servify.ui.modulos.trabajador.muro.MapaSolicitudesScreen
import com.example.servify.ui.modulos.trabajador.muro.MuroSolicitudesScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTabScreen(navController: NavController, paginaInicial: Int = 0) {
    val pagerState = rememberPagerState(initialPage = paginaInicial, pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    // Único punto donde se conecta el socket (ya hay sesión al llegar aquí)
    LaunchedEffect(Unit) {
        SessionManager.token?.let { LlamadasManager.conectar(it) }
    }

    val rutaInicio = if (SessionManager.esTecnico) Rutas.MuroSolicitudes.ruta else Rutas.Home.ruta
    val listaRutas = listOf(rutaInicio, Rutas.Actividad.ruta, Rutas.Mensajes.ruta, Rutas.Perfil.ruta)
    val rutaActual = listaRutas.getOrElse(pagerState.currentPage) { rutaInicio }

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                rutaActual = rutaActual,
                onItemClick = { destino ->
                    val nuevoIndice = when (destino) {
                        Rutas.Home.ruta, Rutas.MuroSolicitudes.ruta -> 0
                        Rutas.Actividad.ruta -> 1
                        Rutas.Mensajes.ruta -> 2
                        Rutas.Perfil.ruta -> 3
                        else -> 0
                    }
                    coroutineScope.launch { pagerState.animateScrollToPage(nuevoIndice) }
                }
            )
        }
    ) { paddingValores ->
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().padding(paddingValores)) { pagina ->
            when (pagina) {
                0 -> {
                    if (SessionManager.esTecnico) MuroSolicitudesScreen(navController = navController)
                    else HomeScreen(navController = navController)
                }
                1 -> ActividadScreen(navController = navController)
                2 -> ConversacionesScreen(navController = navController)
                3 -> PerfilScreen(
                    token = SessionManager.token ?: "",
                    onIrAEditarCliente = { navController.navigate(Rutas.EditarPerfilCliente.ruta) },
                    onIrAEditarTrabajador = { navController.navigate(Rutas.EditarPerfilTrabajador.ruta) },
                    onCerrarSesion = {
                        LlamadasManager.desconectar()
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) { popUpTo(0) { inclusive = true } }
                    },
                    onVerTodasResenas = { navController.navigate(Rutas.Resenas.ruta) }
                )
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Rutas.Login.ruta) {
            composable(route = Rutas.Login.ruta) {
                LoginScreen(
                    onLoginExitoso = { usuario ->
                        SessionManager.guardarSesion(SessionManager.token, usuario)
                        val rutaDestino =
                            if (usuario.rol.equals("admin", ignoreCase = true)) Rutas.AdminDashboard.ruta
                            else Rutas.Home.ruta
                        navController.navigate(rutaDestino) { popUpTo(Rutas.Login.ruta) { inclusive = true } }
                    },
                    onIrARegistro = { navController.navigate(Rutas.SeleccionRol.ruta) },
                    onOlvidastePassword = { navController.navigate(Rutas.RecuperarPassword.ruta) }
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
                    onIrALogin = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } }
                )
            }

            composable(Rutas.RecuperarPassword.ruta) {
                OlvidePasswordScreen(
                    onPasswordRestablecido = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } },
                    onVolverALogin = { navController.popBackStack() }
                )
            }

            composable(Rutas.RegistroCliente.ruta) {
                RegistroClienteScreen(
                    onRegistroExitoso = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } },
                    onIrALogin = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } },
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.RegistroTrabajador.ruta) {
                RegistroTrabajadorScreen(
                    onRegistroExitoso = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } },
                    onIrALogin = { navController.navigate(Rutas.Login.ruta) { popUpTo(Rutas.Login.ruta) { inclusive = true } } },
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.Resenas.ruta) { backStackEntry ->
                val perfilViewModel: PerfilViewModel = viewModel()

                // Asegurarnos de recargar si está vacía al entrar
                LaunchedEffect(Unit) {
                    if (SessionManager.token != null) {
                        perfilViewModel.cargarResenas(SessionManager.token!!)
                    }
                }

                ResenasScreen(
                    onBackClick = { navController.popBackStack() },
                    viewModel = perfilViewModel
                )
            }

            composable(Rutas.Home.ruta) { MainTabScreen(navController = navController, paginaInicial = 0) }
            composable(Rutas.MuroSolicitudes.ruta) { MainTabScreen(navController = navController, paginaInicial = 0) }
            composable(Rutas.Actividad.ruta) { MainTabScreen(navController = navController, paginaInicial = 1) }
            composable(Rutas.Mensajes.ruta) { MainTabScreen(navController = navController, paginaInicial = 2) }
            composable(Rutas.Perfil.ruta) { MainTabScreen(navController = navController, paginaInicial = 3) }

            composable(Rutas.MapaSolicitudes.ruta) { MapaSolicitudesScreen(navController = navController) }

            composable(
                route = Rutas.Mapa.ruta,
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("lng") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("titulo") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0
                val tituloCrudo = backStackEntry.arguments?.getString("titulo") ?: ""
                val titulo = java.net.URLDecoder.decode(tituloCrudo, "UTF-8")

                MapaScreen(
                    navController = navController,
                    latDestino = if (lat != 0.0) lat else null,
                    lngDestino = if (lng != 0.0) lng else null,
                    tituloDestino = if (titulo.isNotBlank()) titulo else null
                )
            }

            composable(
                route = Rutas.DetalleTecnico.ruta,
                arguments = listOf(
                    navArgument("idTecnico") { type = NavType.IntType; defaultValue = 1 }
                )
            ) { backStackEntry ->
                val idTecnico = backStackEntry.arguments?.getInt("idTecnico") ?: 1
                DetalleTecnicoScreen(
                    idTecnico = idTecnico,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.CheckoutPago.ruta,
                arguments = listOf(
                    navArgument("solicitudId") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: 0
                CheckoutPagoScreen(
                    solicitudId = solicitudId,
                    navController = navController
                )
            }

            composable("detalle_averia/{idSolicitud}") { backStackEntry ->
                val idSolicitud = backStackEntry.arguments?.getString("idSolicitud")?.toIntOrNull() ?: 1
                DetalleAveriaScreen(
                    idSolicitud = idSolicitud,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "enviar_propuesta/{idSolicitud}/{titulo}",
                arguments = listOf(
                    navArgument("idSolicitud") { type = NavType.IntType },
                    navArgument("titulo") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val idSolicitud = backStackEntry.arguments?.getInt("idSolicitud") ?: 0
                val tituloTrabajo = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("titulo") ?: "", "UTF-8"
                )
                EnviarPropuestaScreen(
                    idSolicitud = idSolicitud,
                    tituloTrabajo = tituloTrabajo,
                    navController = navController
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

            composable(Rutas.PagoExitoso.ruta) { PagoExitosoScreen(navController = navController) }

            composable(Rutas.EditarPerfilCliente.ruta) {
                EditarPerfilClienteScreen(
                    token = SessionManager.token ?: "",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Rutas.EditarPerfilTrabajador.ruta) {
                EditarPerfilTrabajadorScreen(
                    token = SessionManager.token ?: "",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Rutas.NuevaSolicitud.ruta) {
                NuevaSolicitudScreen(
                    onBackClick = { navController.popBackStack() },
                    onContinuarClick = {
                        navController.navigate(Rutas.Actividad.ruta) { popUpTo(Rutas.Home.ruta) }
                    }
                )
            }

            composable(
                route = Rutas.PropuestasSolicitud.ruta,
                arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
            ) { backStackEntry ->
                PropuestasScreen(
                    solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: 0,
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
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Rutas.AdminDashboard.ruta) {
                AdminDashboardScreen(
                    onIrAUsuarios = { rolFiltro ->
                        navController.navigate(Rutas.AdminUsuarios.crearRuta(rolFiltro))
                    },
                    onIrASolicitudes = { navController.navigate(Rutas.AdminSolicitudes.ruta) },
                    onCerrarSesion = {
                        LlamadasManager.desconectar()
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(
                route = Rutas.AdminUsuarios.ruta,
                arguments = listOf(navArgument("rol") { type = NavType.StringType; defaultValue = "" })
            ) { backStackEntry ->
                AdminUsuariosScreen(
                    filtroInicial = backStackEntry.arguments?.getString("rol") ?: "",
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.AdminSolicitudes.ruta) {
                AdminSolicitudesScreen(onVolver = { navController.popBackStack() })
            }

            composable(
                route = "call_screen/{channelName}/{esVideo}",
                arguments = listOf(
                    navArgument("channelName") { type = NavType.StringType },
                    navArgument("esVideo") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                CallScreen(
                    navController = navController,
                    channelName = backStackEntry.arguments?.getString("channelName") ?: "default_channel",
                    esVideo = backStackEntry.arguments?.getBoolean("esVideo") ?: false
                )
            }
        }

        // Diálogo global de llamada entrante
        LlamadasManager.llamadaEntrante?.let { ll ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text(if (ll.esVideo) "Videollamada entrante" else "Llamada entrante") },
                text = { Text("${ll.deNombre} te está llamando") },
                confirmButton = {
                    Button(onClick = {
                        LlamadasManager.responder(ll.conversacionId, true)
                        navController.navigate(Rutas.Call.crearRuta("chat_${ll.conversacionId}", ll.esVideo))
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        LlamadasManager.responder(ll.conversacionId, false)
                    }) { Text("Rechazar") }
                }
            )
        }
    }
}