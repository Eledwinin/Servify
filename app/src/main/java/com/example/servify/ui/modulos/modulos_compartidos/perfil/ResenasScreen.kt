package com.example.servify.ui.modulos.modulos_compartidos.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenasScreen(
    onBackClick: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val ui by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Todas las Reseñas",
                        color = ServifyTextTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ServifyTextTitle)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifyBackground)
            )
        }
    ) { padding ->
        if (ui.resenas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay reseñas registradas aún.", color = ServifyTextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(ui.resenas) { resena ->
                    Surface(
                        color = ServifySurface,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 1.5.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServifyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = resena.nombreCliente ?: "Usuario",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ServifyTextTitle
                                )
                                Text(
                                    text = "★ ${resena.calificacion}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                            Text(
                                text = resena.comentario ?: "Sin comentarios",
                                fontSize = 13.sp,
                                color = ServifyTextBody
                            )
                        }
                    }
                }
            }
        }
    }
}