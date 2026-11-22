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
import androidx.compose.material.icons.filled.Close
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

    fun cargarSolicitudes(categoria: String? = null) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.obtenerSolicitudes(token, categoria)
            _cargando.value = false
            res.onSuccess { _solicitudes.value = it }
        }
    }

    fun cancelarSolicitud(
        id: Int,
        motivo: String,
        categoriaActual: String?,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = SessionManager.token
        if (token.isNullOrBlank()) {
            onError("Sesión inválida: token vacío")
            return
        }
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.cancelarSolicitud(token, id, motivo)
            _cargando.value = false
            res.onSuccess { msg ->
                _solicitudes.value = _solicitudes.value.map { item ->
                    if (item.id == id) item.copy(estado = "cancelado") else item
                }
                cargarSolicitudes(categoriaActual)
                onExito(msg)
            }.onFailure { err ->
                onError(err.message ?: "Error desconocido al dar de baja")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSolicitudesScreen(
    onVolver: () -> Unit,
    categoriaInicial: String = "",
    viewModel: AdminSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current
    val solicitudes by viewModel.solicitudes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val bordeSuave = Color(0xFFE2E8F0)

    var categoriaActiva by remember(categoriaInicial) { mutableStateOf(categoriaInicial) }
    var solicitudParaBaja by remember { mutableStateOf<AdminSolicitudItem?>(null) }
    var motivoBaja by remember { mutableStateOf("") }

    LaunchedEffect(categoriaActiva) {
        viewModel.cargarSolicitudes(categoriaActiva.ifBlank { null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (categoriaActiva.isNotBlank()) "Solicitudes: $categoriaActiva" else "Moderación de Solicitudes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (categoriaActiva.isNotBlank()) {
                            Text(
                                text = "Filtrando por categoría",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (categoriaActiva.isNotBlank()) {
                        TextButton(onClick = { categoriaActiva = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver todas", fontSize = 12.sp)
                        }
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
        } else if (solicitudes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (categoriaActiva.isNotBlank())
                        "No hay solicitudes registradas en la categoría \"$categoriaActiva\"."
                    else
                        "No hay solicitudes para mostrar.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudes, key = { it.id }) { sol ->
                    val estadoLower = sol.estado.lowercase()
                    val estaCancelada = estadoLower in listOf("cancelada", "cancelado")

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
                                    color = when (estadoLower) {
                                        "pendiente" -> Color(0xFFFFF7ED)
                                        "en_proceso" -> Color(0xFFEFF6FF)
                                        "completada", "completado" -> Color(0xFFECFDF5)
                                        else -> Color(0xFFFEF2F2)
                                    }
                                ) {
                                    Text(
                                        text = sol.estado.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (estadoLower) {
                                            "pendiente" -> Color(0xFFF97316)
                                            "en_proceso" -> Color(0xFF3B82F6)
                                            "completada", "completado" -> Color(0xFF10B981)
                                            else -> Color(0xFFEF4444)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (!sol.categoriaNombre.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = "Categoría: ${sol.categoriaNombre}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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

                            if (!estaCancelada) {
                                OutlinedButton(
                                    onClick = {
                                        solicitudParaBaja = sol
                                        motivoBaja = ""
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

    solicitudParaBaja?.let { solSeleccionada ->
        AlertDialog(
            onDismissRequest = { solicitudParaBaja = null },
            title = {
                Text(
                    text = "Dar de baja solicitud",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Especifica el motivo por el cual se dará de baja \"${solSeleccionada.titulo}\". Este mensaje se enviará al usuario.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    OutlinedTextField(
                        value = motivoBaja,
                        onValueChange = { motivoBaja = it },
                        label = { Text("Motivo de la baja *") },
                        placeholder = { Text("Ej. Solicitud de prueba, datos falsos o contenido inapropiado") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val razonFinal = motivoBaja.trim()
                        val idSol = solSeleccionada.id
                        solicitudParaBaja = null
                        viewModel.cancelarSolicitud(
                            id = idSol,
                            motivo = razonFinal,
                            categoriaActual = categoriaActiva.ifBlank { null },
                            onExito = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onError = { errMsg ->
                                Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = motivoBaja.trim().length >= 5,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirmar baja", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudParaBaja = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}