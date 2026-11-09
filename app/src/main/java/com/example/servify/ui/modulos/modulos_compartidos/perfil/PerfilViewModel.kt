package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.FotoPortafolioResponse
import com.example.servify.data.model.Resena
import com.example.servify.data.model.UsuarioModel
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilUiState(
    val usuario: UsuarioModel? = null,
    val fotosPortafolio: List<FotoPortafolioResponse> = emptyList(),
    val resenas: List<Resena> = emptyList(),
    val isLoading: Boolean = false,
    val isUploadingFoto: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // ==========================================
    // PERFIL BÁSICO Y VIP
    // ==========================================



    fun cargarPerfil(token: String) {
        if (token.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val resultado = repository.obtenerPerfil(token)
            resultado.onSuccess { usuario ->
                Log.d("DEBUG_PERFIL", "Usuario recibido: $usuario")
                Log.d("DEBUG_PERFIL", "categoriaNombre recibido: ${usuario.categoriaNombre}")

                SessionManager.guardarSesion(token, usuario)

                _uiState.value = _uiState.value.copy(
                    usuario = usuario,
                    isLoading = false
                )
                cargarResenas(token)
            }.onFailure { err ->
                Log.e("DEBUG_PERFIL", "Error al cargar perfil: ${err.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message
                )
            }
        }
    }

    // ==========================================
    // RESEÑAS DEL TÉCNICO
    // ==========================================

    fun cargarResenas(token: String) {
        if (token.isBlank()) return
        viewModelScope.launch {
            val resultado = repository.obtenerResenasUsuario(token)
            resultado.onSuccess { listaResenas ->
                _uiState.value = _uiState.value.copy(resenas = listaResenas)
            }.onFailure { err ->
                Log.e("RESENAS", err.message ?: "error")
            }
        }
    }

    fun cambiarMembresiaVip(esVip: Boolean, onResultado: (Boolean) -> Unit) {
        val usuario = SessionManager.usuarioActual ?: return
        val token = SessionManager.token ?: return

        viewModelScope.launch {
            if (!esVip) {
                // Llamamos al servidor para borrar el VIP en la base de datos real
                val resultado = repository.cancelarPlan(token)
                resultado.onSuccess {
                    // Limpiamos la sesión localmente
                    SessionManager.cancelarSuscripcionVip()
                    val usuarioActualizado = usuario.copy(esVip = false, tipoPlan = null)
                    SessionManager.guardarSesion(token, usuarioActualizado)
                    _uiState.value = _uiState.value.copy(usuario = usuarioActualizado)
                    onResultado(true)
                }.onFailure { err ->
                    Log.e("VIP", "Error al cancelar plan en servidor: ${err.message}")
                    onResultado(false)
                }
            } else {
                // Si es para activar, lo manejas de acuerdo a tu flujo de pagos
                onResultado(true)
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // ==========================================
    // PORTAFOLIO DE TRABAJOS (TÉCNICOS)
    // ==========================================

    fun cargarPortafolio() {
        val token = SessionManager.token ?: return
        if (token.isBlank()) return

        viewModelScope.launch {
            val resultado = repository.obtenerPortafolio(token)
            resultado.onSuccess { fotos ->
                _uiState.value = _uiState.value.copy(fotosPortafolio = fotos)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(error = err.message)
            }
        }
    }

    fun subirFotoPortafolio(context: Context, uri: Uri) {
        val token = SessionManager.token ?: return
        if (token.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingFoto = true, error = null)
            val resultado = repository.subirFotoPortafolio(context, token, uri)
            resultado.onSuccess { respuesta ->
                val listaActualizada = listOf(respuesta.foto) + _uiState.value.fotosPortafolio
                _uiState.value = _uiState.value.copy(
                    fotosPortafolio = listaActualizada,
                    isUploadingFoto = false
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isUploadingFoto = false,
                    error = err.message
                )
            }
        }
    }

    fun eliminarFotoPortafolio(fotoId: Int) {
        val token = SessionManager.token ?: return
        if (token.isBlank()) return

        viewModelScope.launch {
            val resultado = repository.eliminarFotoPortafolio(token, fotoId)
            resultado.onSuccess {
                val listaActualizada = _uiState.value.fotosPortafolio.filter { it.id != fotoId }
                _uiState.value = _uiState.value.copy(fotosPortafolio = listaActualizada)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(error = err.message)
            }
        }
    }

    // ==========================================
    // ELIMINAR CUENTA (CLIENTE / TÉCNICO)
    // ==========================================

    fun eliminarCuenta(onCuentaEliminada: () -> Unit) {
        val token = SessionManager.token ?: return
        if (token.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val resultado = repository.eliminarCuenta(token)
            resultado.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                SessionManager.cerrarSesion()
                onCuentaEliminada()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message
                )
            }
        }
    }
}