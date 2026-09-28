package com.example.servify.ui.modulos.cliente.gestion_perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditarPerfilUiState(
    val correo: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val actualizadoConExito: Boolean = false,
    val error: String? = null
)

class EditarPerfilClienteViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUiState())
    val uiState: StateFlow<EditarPerfilUiState> = _uiState.asStateFlow()

    fun cargarPerfil(token: String) {
        if (token.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val resultado = repository.obtenerPerfil(token)
            resultado.onSuccess { usuario ->
                _uiState.value = _uiState.value.copy(
                    correo = usuario.correo,
                    nombre = usuario.nombre,
                    telefono = usuario.telefono ?: "",
                    direccion = usuario.direccionTexto ?: "", // Leemos direccionTexto
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

    fun onNombreChange(nuevoNombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nuevoNombre)
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        _uiState.value = _uiState.value.copy(telefono = nuevoTelefono)
    }

    fun onDireccionChange(nuevaDireccion: String) {
        _uiState.value = _uiState.value.copy(direccion = nuevaDireccion)
    }

    fun guardarCambios(token: String) {
        val currentState = _uiState.value
        if (currentState.nombre.trim().isEmpty()) {
            _uiState.value = currentState.copy(error = "El nombre no puede estar vacío")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val resultado = repository.actualizarPerfil(
                token = token,
                nombre = currentState.nombre.trim(),
                telefono = currentState.telefono.trim().ifEmpty { null },
                direccion = currentState.direccion.trim().ifEmpty { null }
            )

            resultado.onSuccess {
                // Actualizamos SessionManager con direccionTexto
                val usuarioPrevio = SessionManager.usuarioActual
                val usuarioModificado = usuarioPrevio?.copy(
                    nombre = currentState.nombre.trim(),
                    telefono = currentState.telefono.trim(),
                    direccionTexto = currentState.direccion.trim().ifEmpty { null }
                )
                SessionManager.guardarSesion(token, usuarioModificado)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    actualizadoConExito = true
                )
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