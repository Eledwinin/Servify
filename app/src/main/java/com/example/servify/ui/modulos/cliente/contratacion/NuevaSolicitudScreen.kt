package com.example.servify.ui.modulos.cliente.contratacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.cards.TarjetaFormulario
import com.example.servify.ui.componentes.cards.TarjetaFotos
import com.example.servify.ui.theme.*
import com.example.servify.data.SessionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    onBackClick: () -> Unit = {},
    onContinuarClick: () -> Unit = {},
    viewModel: NuevaSolicitudViewModel = viewModel()
) {
    val context = LocalContext.current

    val selectorFotos = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(NuevaSolicitudViewModel.MAX_FOTOS)
    ) { uris -> viewModel.agregarFotos(uris) }

    // Escucha si la solicitud fue un éxito
    LaunchedEffect(viewModel.solicitudExitosa) {
        if (viewModel.solicitudExitosa) {
            Toast.makeText(context, "Solicitud creada correctamente", Toast.LENGTH_SHORT).show()
            viewModel.limpiarEstadoExito() // Asegúrate de tener este método o similar en tu ViewModel
            onContinuarClick()
        }
    }

    // Escucha si hay errores
    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Servify",
                        color = ServifyBluePrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    Surface(
                        color = ServifyInputBackground,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = "MODO CLIENTE",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ServifySurface)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val token = SessionManager.token ?: ""
                        viewModel.enviarSolicitud(token, context.contentResolver)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Publicar Solicitud", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Cabecera interna con el botón de retroceso y el título de la vista
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(ServifySurface, CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nueva Solicitud",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyTextTitle
                    )
                }
            }

            item {
                // Tarjeta de formulario de la solicitud (Categoría, Título, Descripción, Presupuesto)
                TarjetaFormulario(
                    titulo = viewModel.titulo,
                    onTituloChange = { viewModel.titulo = it },
                    descripcion = viewModel.descripcion,
                    onDescripcionChange = { viewModel.descripcion = it },
                    categoriaSeleccionada = viewModel.categoria,
                    onCategoriaSelect = { viewModel.categoria = it },
                    presupuesto = viewModel.presupuesto,
                    onPresupuestoChange = { viewModel.presupuesto = it }
                )
            }

            item {
                // Tarjeta de selección de fotos de referencia
                TarjetaFotos(
                    fotos = viewModel.fotos,
                    maxFotos = NuevaSolicitudViewModel.MAX_FOTOS,
                    onAñadirFotoClick = {
                        selectorFotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onQuitarFoto = { viewModel.quitarFoto(it) }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}