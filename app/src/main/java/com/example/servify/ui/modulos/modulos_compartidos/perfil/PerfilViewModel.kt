package com.example.servify.ui.modulos.modulos_compartidos.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.model.UsuarioModel
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilUiState(
    val usuario: UsuarioModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun cargarPerfil(token: String) {
        if (token.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val resultado = repository.obtenerPerfil(token)
            resultado.onSuccess { usuario ->
                SessionManager.guardarSesion(token, usuario)
                _uiState.value = _uiState.value.copy(
                    usuario = usuario,
                    isLoading = false
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message
                )
            }
        }
    }

    fun cambiarMembresiaVip(esVip: Boolean, onResultado: (Boolean) -> Unit) {
        val usuario = SessionManager.usuarioActual ?: return
        viewModelScope.launch {
            val resultado = repository.actualizarEstadoVip(usuario.id, esVip)
            resultado.onSuccess { nuevoEstado ->
                SessionManager.esVip = nuevoEstado
                val usuarioActualizado = usuario.copy(esVip = nuevoEstado)
                SessionManager.guardarSesion(SessionManager.token, usuarioActualizado)
                _uiState.value = _uiState.value.copy(usuario = usuarioActualizado)
                onResultado(true)
            }.onFailure {
                onResultado(false)
            }
        }
    }
}