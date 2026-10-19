package com.example.servify.ui.modulos.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager
import com.example.servify.data.model.AdminUsuarioItem
import com.example.servify.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUsuariosViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {
    private val _usuarios = MutableStateFlow<List<AdminUsuarioItem>>(emptyList())
    val usuarios = _usuarios.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    var busqueda by mutableStateOf("")
    var filtroRol by mutableStateOf("")

    fun cargarUsuarios() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.obtenerUsuarios(token, busqueda.ifBlank { null }, filtroRol.ifBlank { null })
            _cargando.value = false
            res.onSuccess { _usuarios.value = it }
        }
    }

    fun cambiarRol(usuarioId: Int, nuevoRol: String, onCompletado: () -> Unit) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _cargando.value = true
            val res = repository.cambiarRol(token, usuarioId, nuevoRol)
            _cargando.value = false
            res.onSuccess {
                cargarUsuarios()
                onCompletado()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    filtroInicial: String = "",
    onVolver: () -> Unit,
    viewModel: AdminUsuariosViewModel = viewModel()
) {
    val context = LocalContext.current
    val usuarios by viewModel.usuarios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var usuarioSeleccionado by remember { mutableStateOf<AdminUsuarioItem?>(null) }
    var mostrarDialogoRol by remember { mutableStateOf(false) }

    val verdePrincipal = Color(0xFF168067)
    val bordeSuave = Color(0xFFE2E8F0)

    LaunchedEffect(filtroInicial) {
        viewModel.filtroRol = filtroInicial
        viewModel.cargarUsuarios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.busqueda,
                onValueChange = {
                    viewModel.busqueda = it
                    viewModel.cargarUsuarios()
                },
                placeholder = { Text("Buscar por nombre o correo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    unfocusedBorderColor = bordeSuave,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("" to "Todos", "cliente" to "Clientes", "tecnico" to "Técnicos", "admin" to "Admins").forEach { (key, label) ->
                    val seleccionado = viewModel.filtroRol == key
                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            viewModel.filtroRol = key
                            viewModel.cargarUsuarios()
                        },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = verdePrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (cargando && usuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verdePrincipal)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(usuarios) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    usuarioSeleccionado = user
                                    mostrarDialogoRol = true
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, bordeSuave)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(verdePrincipal.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.nombre.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = verdePrincipal,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                    Text(text = user.correo, fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = "Rol: ${user.rol.uppercase()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (user.rol.equals("admin", true)) Color(0xFFDC2626) else verdePrincipal
                                    )
                                }
                                Icon(Icons.Default.Edit, contentDescription = "Modificar rol", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoRol && usuarioSeleccionado != null) {
        val user = usuarioSeleccionado!!
        AlertDialog(
            onDismissRequest = { mostrarDialogoRol = false },
            title = { Text("Cambiar Rol de Usuario", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona el nuevo rol para ${user.nombre}:", fontSize = 13.sp)
                    listOf("cliente", "tecnico", "admin").forEach { rolOpcion ->
                        Button(
                            onClick = {
                                viewModel.cambiarRol(user.id, rolOpcion) {
                                    Toast.makeText(context, "Rol actualizado a $rolOpcion", Toast.LENGTH_SHORT).show()
                                    mostrarDialogoRol = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.rol.equals(rolOpcion, true)) verdePrincipal else Color(0xFFF1F5F9),
                                contentColor = if (user.rol.equals(rolOpcion, true)) Color.White else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(rolOpcion.uppercase(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRol = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}