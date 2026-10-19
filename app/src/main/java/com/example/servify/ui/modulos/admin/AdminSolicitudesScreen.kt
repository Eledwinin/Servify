package com.example.servify.ui.modulos.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager
import com.example.servify.data.model.AdminSolicitudItem
import com.example.servify.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminSolicitudesViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {
    private val _solicitudes = MutableStateFlow<List<AdminSolicitudItem>>(emptyList())
    val solicitudes = _solicitudes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    fun cargarSolicitudes() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.obtenerSolicitudes(token)
            _cargando.value = false
            res.onSuccess { _solicitudes.value = it }
        }
    }

    fun cancelarSolicitud(id: Int, onExito: () -> Unit) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.cancelarSolicitud(token, id)
            _cargando.value = false
            res.onSuccess {
                cargarSolicitudes()
                onExito()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSolicitudesScreen(
    onVolver: () -> Unit,
    viewModel: AdminSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current
    val solicitudes by viewModel.solicitudes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val bordeSuave = Color(0xFFE2E8F0)

    LaunchedEffect(Unit) {
        viewModel.cargarSolicitudes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderación de Solicitudes", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (cargando && solicitudes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF168067))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudes) { sol ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, bordeSuave)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sol.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (sol.estado.lowercase()) {
                                        "pendiente" -> Color(0xFFFFF7ED)
                                        "en_proceso" -> Color(0xFFEFF6FF)
                                        "completada" -> Color(0xFFECFDF5)
                                        else -> Color(0xFFFEF2F2)
                                    }
                                ) {
                                    Text(
                                        text = sol.estado.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (sol.estado.lowercase()) {
                                            "pendiente" -> Color(0xFFF97316)
                                            "en_proceso" -> Color(0xFF3B82F6)
                                            "completada" -> Color(0xFF10B981)
                                            else -> Color(0xFFEF4444)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = sol.descripcion ?: "Sin descripción detallada.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )

                            Text(
                                text = "Publicado por: ${sol.clienteNombre ?: "Usuario"} (${sol.clienteCorreo ?: ""})",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )

                            if (sol.estado.lowercase() != "cancelada") {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.cancelarSolicitud(sol.id) {
                                            Toast.makeText(context, "Solicitud dada de baja", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dar de baja", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}