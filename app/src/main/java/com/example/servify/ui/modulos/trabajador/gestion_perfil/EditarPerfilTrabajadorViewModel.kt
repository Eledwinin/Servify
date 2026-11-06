package com.example.servify.ui.modulos.trabajador.gestion_perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditarPerfilTrabajadorUiState(
    val correo: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val biografia: String = "",
    val anosExperiencia: String = "",
    val isLoading: Boolean = false,
    val actualizadoConExito: Boolean = false,
    val error: String? = null
)

class EditarPerfilTrabajadorViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    companion object { const val MAX_BIO = 500 }

    private val _uiState = MutableStateFlow(EditarPerfilTrabajadorUiState())
    val uiState: StateFlow<EditarPerfilTrabajadorUiState> = _uiState.asStateFlow()

    fun cargarPerfil(token: String) {
        if (token.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.obtenerPerfil(token).onSuccess { u ->
                _uiState.value = _uiState.value.copy(
                    correo = u.correo,
                    nombre = u.nombre,
                    telefono = u.telefono ?: "",
                    direccion = u.direccionTexto ?: "",
                    biografia = u.biografia ?: "",
                    anosExperiencia = u.anosExperiencia?.takeIf { it > 0 }?.toString() ?: "",
                    isLoading = false
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Error al obtener perfil"
                )
            }
        }
    }

    fun onNombreChange(v: String) { _uiState.value = _uiState.value.copy(nombre = v) }
    fun onTelefonoChange(v: String) { _uiState.value = _uiState.value.copy(telefono = v) }
    fun onDireccionChange(v: String) { _uiState.value = _uiState.value.copy(direccion = v) }
    fun onBiografiaChange(v: String) {
        if (v.length <= MAX_BIO) _uiState.value = _uiState.value.copy(biografia = v)
    }
    fun onAnosChange(v: String) {
        if (v.length <= 2 && v.all { it.isDigit() }) _uiState.value = _uiState.value.copy(anosExperiencia = v)
    }

    fun limpiarError() { _uiState.value = _uiState.value.copy(error = null) }

    fun guardarCambios(token: String) {
        val s = _uiState.value
        if (s.nombre.trim().isEmpty()) {
            _uiState.value = s.copy(error = "El nombre no puede estar vacío")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val bio = s.biografia.trim()
            val anos = s.anosExperiencia.toIntOrNull()

            repository.actualizarPerfil(
                token = token,
                nombre = s.nombre.trim(),
                telefono = s.telefono.trim().ifEmpty { null },
                direccion = s.direccion.trim().ifEmpty { null },
                biografia = bio,
                anosExperiencia = anos
            ).onSuccess {
                val previo = SessionManager.usuarioActual
                val modificado = previo?.copy(
                    nombre = s.nombre.trim(),
                    telefono = s.telefono.trim(),
                    direccionTexto = s.direccion.trim().ifEmpty { null },
                    biografia = bio,
                    anosExperiencia = anos
                )
                SessionManager.guardarSesion(token, modificado)
                _uiState.value = _uiState.value.copy(isLoading = false, actualizadoConExito = true)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "No se pudieron guardar los cambios"
                )
            }
        }
    }

    fun limpiarEstadoExito() {
        _uiState.value = _uiState.value.copy(actualizadoConExito = false)
    }
}