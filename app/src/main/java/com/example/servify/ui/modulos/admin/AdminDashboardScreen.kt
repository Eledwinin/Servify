package com.example.servify.ui.modulos.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onIrAUsuarios: (rolFiltro: String) -> Unit,
    onIrASolicitudes: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val verdePrincipal = Color(0xFF168067)
    val fondoPantalla = Color(0xFFF8FAFC)
    val textoOscuro = Color(0xFF1E293B)
    val textoGris = Color(0xFF64748B)

    LaunchedEffect(Unit) {
        viewModel.cargarMetricas()
    }

    Scaffold(
        containerColor = fondoPantalla,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Servify Admin",
                            color = verdePrincipal,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Panel de Control y Métricas",
                            color = textoGris,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFFDC2626)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (uiState.cargando && uiState.data == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = verdePrincipal)
            }
        } else {
            val stats = uiState.data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Comunidad y Usuarios",
                    color = textoOscuro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Total Usuarios",
                        valor = "${stats?.usuarios?.totalUsuarios ?: 0}",
                        icono = Icons.Default.Group,
                        colorIcono = verdePrincipal,
                        onClick = { onIrAUsuarios("") }
                    )
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Técnicos",
                        valor = "${stats?.usuarios?.totalTecnicos ?: 0}",
                        icono = Icons.Default.Engineering,
                        colorIcono = Color(0xFF0284C7),
                        onClick = { onIrAUsuarios("tecnico") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Clientes",
                        valor = "${stats?.usuarios?.totalClientes ?: 0}",
                        icono = Icons.Default.Person,
                        colorIcono = Color(0xFF8B5CF6),
                        onClick = { onIrAUsuarios("cliente") }
                    )
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Categorías",
                        valor = "${stats?.totalCategorias ?: 0}",
                        icono = Icons.Default.Category,
                        colorIcono = Color(0xFFF59E0B),
                        onClick = { /* Categorías llamkay */ }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Estado de Solicitudes",
                    color = textoOscuro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Pendientes",
                        valor = "${stats?.solicitudes?.pendientes ?: 0}",
                        icono = Icons.Default.Pending,
                        colorIcono = Color(0xFFF97316),
                        onClick = onIrASolicitudes
                    )
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "En Proceso",
                        valor = "${stats?.solicitudes?.enProceso ?: 0}",
                        icono = Icons.Default.Assignment,
                        colorIcono = Color(0xFF3B82F6),
                        onClick = onIrASolicitudes
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Completadas",
                        valor = "${stats?.solicitudes?.completadas ?: 0}",
                        icono = Icons.Default.CheckCircle,
                        colorIcono = Color(0xFF10B981),
                        onClick = onIrASolicitudes
                    )
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Canceladas",
                        valor = "${stats?.solicitudes?.canceladas ?: 0}",
                        icono = Icons.Default.Assignment,
                        colorIcono = Color(0xFFEF4444),
                        onClick = onIrASolicitudes
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricaCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorIcono.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = valor,
                color = Color(0xFF1E293B),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = titulo,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}