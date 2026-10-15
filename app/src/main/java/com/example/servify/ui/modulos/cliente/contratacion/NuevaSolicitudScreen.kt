package com.example.servify.ui.modulos.cliente.contratacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.servify.ui.navigation.Rutas

@Composable
fun NuevaSolicitudScreen(
    onBackClick: () -> Unit = {},
    onContinuarClick: () -> Unit = {},
    viewModel: NuevaSolicitudViewModel = viewModel()
) {
    val context = LocalContext.current

    // Escucha si la solicitud fue un éxito
    LaunchedEffect(viewModel.solicitudExitosa) {
        if (viewModel.solicitudExitosa) {
            Toast.makeText(context, "Solicitud creada correctamente", Toast.LENGTH_SHORT).show()
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
            Column(modifier = Modifier.background(ServifySurface)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Servify", color = ServifyGreenPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Surface(color = ServifyInputBackground, shape = RoundedCornerShape(12.dp)) {
                        Text("MODO CLIENTE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ServifyTextTitle)
                    }
                    Text("Nueva Solicitud", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(ServifySurface).padding(16.dp)) {
                Button(
                    onClick = {
                        val token = SessionManager.token ?: ""
                        viewModel.enviarSolicitud(token)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyGreenPrimary),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = ServifySurface, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Publicar Solicitud", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ServifySurface)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                // Tarjeta de formulario de la solicitud
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
                // Tarjeta de fotos de referencia
                TarjetaFotos(
                    onAñadirFotoClick = { }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}