package com.example.servify.ui.modulos.cliente.contratacion

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager
import com.example.servify.ui.componentes.cards.TarjetaFormulario
import com.example.servify.ui.componentes.cards.TarjetaFotos
import com.example.servify.ui.theme.*

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

    LaunchedEffect(viewModel.solicitudExitosa) {
        if (viewModel.solicitudExitosa) {
            Toast.makeText(context, "Solicitud creada correctamente", Toast.LENGTH_SHORT).show()
            viewModel.limpiarEstadoExito()
            onContinuarClick()
        }
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    Scaffold(
        containerColor = ServifyBackground,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.ime),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva Solicitud",
                        color = ServifyTextTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = ServifyBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground)
            )
        },
        bottomBar = {
            Surface(
                color = ServifySurface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Publicar Solicitud",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
                TarjetaFotos(
                    fotos = viewModel.fotos,
                    maxFotos = NuevaSolicitudViewModel.MAX_FOTOS,
                    onAñadirFotoClick = {
                        selectorFotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onQuitarFoto = { viewModel.quitarFoto(it) }
                )
            }
        }
    }
}