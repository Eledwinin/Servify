package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.botones.BotonAccion
import com.example.servify.ui.componentes.cards.TarjetaBase
import com.example.servify.ui.componentes.feedback.DialogoConfirmacion
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    token: String,
    onIrAEditarCliente: () -> Unit,
    onIrAEditarTrabajador: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val c = PerfilColores
    val context = LocalContext.current
    val ui by viewModel.uiState.collectAsState()

    var dialogoVip by remember { mutableStateOf(false) }
    var dialogoEliminar by remember { mutableStateOf(false) }
    var dialogoCerrarSesion by remember { mutableStateOf(false) }

    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.subirFotoPortafolio(context, it) }
    }
    val abrirSelector = {
        selectorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            viewModel.cargarPerfil(token)
            viewModel.cargarPortafolio()
        }
    }
    LaunchedEffect(ui.error) {
        ui.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    val user = ui.usuario
    val esTecnico = SessionManager.esTecnico || (user?.rol?.lowercase() in listOf("tecnico", "trabajador", "ambos"))
    val esVip = SessionManager.esVip || (user?.esVip == true)
    val (porcentaje, faltantes) = remember(user, ui.fotosPortafolio.size, esTecnico) {
        calcularCompletitud(user, ui.fotosPortafolio.size, esTecnico)
    }

    // Diálogo de confirmación para Cerrar Sesión
    if (dialogoCerrarSesion) {
        DialogoConfirmacion(
            titulo = "¿Cerrar sesión?",
            mensaje = "¿Estás seguro de que deseas salir de tu cuenta?",
            textoConfirmar = "Sí, salir",
            textoCancelar = "Cancelar",
            onConfirmar = {
                dialogoCerrarSesion = false
                onCerrarSesion()
            },
            onCancelar = { dialogoCerrarSesion = false }
        )
    }

    if (dialogoVip) {
        DialogoConfirmacion(
            titulo = "Cancelar Membresía PRO",
            mensaje = "¿Seguro que deseas cancelar tu suscripción? Perderás el acceso prioritario y tu insignia de verificación.",
            textoConfirmar = "Sí, cancelar plan", textoCancelar = "Mantener plan",
            onConfirmar = {
                viewModel.cambiarMembresiaVip(false) { exito ->
                    SessionManager.cancelarSuscripcionVip()
                    dialogoVip = false
                    Toast.makeText(context,
                        if (exito) "Suscripción cancelada" else "Suscripción cancelada localmente",
                        Toast.LENGTH_SHORT).show()
                }
            },
            onCancelar = { dialogoVip = false }
        )
    }

    if (dialogoEliminar) {
        DialogoConfirmacion(
            titulo = "¿Eliminar tu cuenta?",
            mensaje = "Esta acción es definitiva. Se eliminarán tus datos, fotos, historial y servicios asociados.",
            textoConfirmar = "Sí, eliminar cuenta", textoCancelar = "Cancelar",
            cargando = ui.isLoading,
            onConfirmar = {
                viewModel.eliminarCuenta {
                    dialogoEliminar = false
                    Toast.makeText(context, "Cuenta eliminada permanentemente", Toast.LENGTH_LONG).show()
                    onCerrarSesion()
                }
            },
            onCancelar = { dialogoEliminar = false }
        )
    }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Servify",
                            color = ServifyBluePrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    Surface(shape = RoundedCornerShape(12.dp), color = ServifyInputBackground,
                        modifier = Modifier.padding(end = 16.dp)) {
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        if (ui.isLoading && user == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ServifyBluePrimary)
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CabeceraPerfil(user, esTecnico, esVip)

                if (porcentaje < 100 && esTecnico) {
                    TarjetaCompletitud(porcentaje, faltantes, onIrAEditarTrabajador)
                }

                if (esTecnico && esVip) TarjetaVip(onCancelar = { dialogoVip = true })
                if (esTecnico) TarjetaSobreMi(user?.biografia, user?.anosExperiencia, onIrAEditarTrabajador)

                TarjetaContacto(user)

                if (esTecnico) {
                    TarjetaPortafolio(ui.fotosPortafolio, ui.isUploadingFoto, abrirSelector) {
                        viewModel.eliminarFotoPortafolio(it)
                    }
                    TarjetaBase {
                        Text("Mis Reseñas", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Aún no tienes reseñas registradas.", color = c.gris, fontSize = 13.sp)
                    }
                } else {
                    SeccionResenasCliente()
                }

                // Aquí validamos a qué pantalla de edición navegar dependiendo si es técnico o cliente
                BotonAccion(
                    texto = "Editar Perfil",
                    icono = Icons.Default.Settings,
                    onClick = {
                        if (esTecnico) onIrAEditarTrabajador() else onIrAEditarCliente()
                    }
                )

                BotonAccion("Cerrar Sesión", Icons.AutoMirrored.Filled.ExitToApp, { dialogoCerrarSesion = true },
                    colorFondo = c.rojoClaro, colorContenido = c.rojo, conBorde = false)

                TextButton(onClick = { dialogoEliminar = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.DeleteForever, null, tint = c.rojo.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar cuenta permanentemente", color = c.rojo.copy(alpha = 0.8f),
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}