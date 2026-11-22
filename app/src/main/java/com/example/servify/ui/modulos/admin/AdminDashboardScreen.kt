package com.example.servify.ui.modulos.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val AdminDarkBlue = Color(0xFF0B1F3A)
private val AdminBg = Color(0xFFF2F5F9)
private val AdminBorder = Color(0xFFE3E9F0)
private val AdminDivider = Color(0xFFEEF2F6)
private val AdminTextMuted = Color(0xFF6B7C93)
private val AdminTeal = Color(0xFF34D1BF)
private val AdminBlueLight = Color(0xFF7DB7FF)
private val AdminOrange = Color(0xFFF5A524)
private val AdminBlue = Color(0xFF4C8DF6)
private val AdminGreen = Color(0xFF22B07D)
private val AdminRed = Color(0xFFE5484D)

@Composable
fun AdminDashboardScreen(
    onIrAUsuarios: (rolFiltro: String) -> Unit = {},
    onIrASolicitudes: (categoriaFiltro: String) -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.data
    val cargando = uiState.cargando
    var tabReporteSeleccionada by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.cargarMetricas()
    }

    val totalUsuarios = stats?.usuarios?.totalUsuarios ?: 0
    val totalTecnicos = stats?.usuarios?.totalTecnicos ?: 0
    val totalClientes = stats?.usuarios?.totalClientes ?: 0

    val totalSolicitudes = stats?.solicitudes?.totalSolicitudes ?: 0
    val pendientes = stats?.solicitudes?.pendientes ?: 0
    val enProceso = stats?.solicitudes?.enProceso ?: 0
    val completadas = stats?.solicitudes?.completadas ?: 0
    val canceladas = stats?.solicitudes?.canceladas ?: 0

    val categorias = listOf(
        Pair("Carpintería", Color(0xFFF5A524)),
        Pair("Cerrajería", Color(0xFF64748B)),
        Pair("Electricidad", Color(0xFFEAB308)),
        Pair("Mant. de Cómputo", Color(0xFF4C8DF6)),
        Pair("Plomería", Color(0xFF34D1BF)),
        Pair("Refrigeración", Color(0xFF38BDF8))
    )

    Scaffold(containerColor = AdminBg) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. ENCABEZADO Y COMUNIDAD REAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(AdminDarkBlue)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Servify Admin", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Panel de control y métricas", fontSize = 13.sp, color = Color(0xFF9FB1C9))
                        }

                        IconButton(
                            onClick = onCerrarSesion,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    Text("Comunidad", fontSize = 13.sp, color = Color(0xFF9FB1C9))
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.clickable { onIrAUsuarios("") }
                    ) {
                        Text(
                            text = if (cargando && stats == null) "..." else totalUsuarios.toString(),
                            fontSize = 62.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "usuarios registrados",
                            fontSize = 17.sp,
                            color = Color(0xFFC9D6E8),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val pesoTec = if (totalTecnicos > 0) totalTecnicos.toFloat() else 1f
                    val pesoCli = if (totalClientes > 0) totalClientes.toFloat() else 1f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(pesoTec)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AdminTeal)
                        )
                        Box(
                            modifier = Modifier
                                .weight(pesoCli)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AdminBlueLight)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onIrAUsuarios("tecnico") }
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AdminTeal))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$totalTecnicos técnicos", fontSize = 14.sp, color = Color.White)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onIrAUsuarios("cliente") }
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AdminBlueLight))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$totalClientes clientes", fontSize = 14.sp, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. SECCIÓN SOLICITUDES REALES
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Solicitudes", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AdminDarkBlue)
                    Text("$totalSolicitudes en total", fontSize = 13.sp, color = AdminTextMuted)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AdminBorder))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEDF1F5))
                        ) {
                            if (pendientes > 0) Box(modifier = Modifier.weight(pendientes.toFloat()).fillMaxHeight().background(AdminOrange))
                            if (enProceso > 0) Box(modifier = Modifier.weight(enProceso.toFloat()).fillMaxHeight().background(AdminBlue))
                            if (completadas > 0) Box(modifier = Modifier.weight(completadas.toFloat()).fillMaxHeight().background(AdminGreen))
                            if (canceladas > 0) Box(modifier = Modifier.weight(canceladas.toFloat()).fillMaxHeight().background(AdminRed))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ItemSolicitudRow("Pendientes", pendientes, AdminOrange, esCero = pendientes == 0)
                        HorizontalDivider(color = AdminDivider, modifier = Modifier.padding(vertical = 10.dp))
                        ItemSolicitudRow("En proceso", enProceso, AdminBlue, esCero = enProceso == 0)
                        HorizontalDivider(color = AdminDivider, modifier = Modifier.padding(vertical = 10.dp))
                        ItemSolicitudRow("Completadas", completadas, AdminGreen, esCero = completadas == 0)
                        HorizontalDivider(color = AdminDivider, modifier = Modifier.padding(vertical = 10.dp))
                        ItemSolicitudRow("Canceladas", canceladas, AdminRed, esCero = canceladas == 0)

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onIrASolicitudes("") },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AdminDarkBlue)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Text(
                                    text = if (pendientes > 0) "Revisar $pendientes pendientes" else "Ver todas las solicitudes",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SECCIÓN REPORTES (EN 0)
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reportes", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AdminDarkBlue)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("0 nuevos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AdminBorder))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PillTab("Nuevos (0)", tabReporteSeleccionada == 0) { tabReporteSeleccionada = 0 }
                            PillTab("En revisión (0)", tabReporteSeleccionada == 1) { tabReporteSeleccionada = 1 }
                            PillTab("Resueltos (0)", tabReporteSeleccionada == 2) { tabReporteSeleccionada = 2 }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(AdminBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AdminGreen)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Sin reportes pendientes", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminDarkBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "La comunidad se encuentra al día y sin incidencias.",
                                fontSize = 12.sp,
                                color = AdminTextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. SECCIÓN CATEGORÍAS (CLICABLES PARA FILTRAR SOLICITUDES)
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Categorías", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AdminDarkBlue)
                    Text("${categorias.size} en total", fontSize = 13.sp, color = AdminTextMuted)
                }

                Spacer(modifier = Modifier.height(14.dp))

                categorias.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        fila.forEach { categoria ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable { onIrASolicitudes(categoria.first) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AdminBorder))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(categoria.second))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(categoria.first, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminDarkBlue)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun ItemSolicitudRow(nombre: String, cantidad: Int, colorPunto: Color, esCero: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(colorPunto))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = nombre, fontSize = 15.sp, color = AdminDarkBlue)
        }
        Text(
            text = cantidad.toString(),
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = if (esCero) Color(0xFF9AA8BA) else AdminDarkBlue
        )
    }
}

@Composable
private fun PillTab(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(if (seleccionada) AdminDarkBlue else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionada) Color.White else AdminTextMuted
        )
    }
}