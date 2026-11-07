package com.example.servify.ui.modulos.cliente.profesional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.repository.SolicitudesRepository
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleTecnicoUiState(
    val isLoading: Boolean = false,
    val tecnico: ProfesionalPerfil? = null,
    val error: String? = null,
    val enviandoSolicitud: Boolean = false,
    val enviadoExitoso: Boolean = false
)

class DetalleTecnicoViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val solicitudesRepository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleTecnicoUiState())
    val uiState: StateFlow<DetalleTecnicoUiState> = _uiState.asStateFlow()

    fun cargarDetalleTecnico(idTecnico: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val resultado = usuarioRepository.obtenerPerfilProfesional(idTecnico.toString())

            resultado.onSuccess { tecnicoData ->
                _uiState.update {
                    it.copy(isLoading = false, tecnico = tecnicoData)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.localizedMessage)
                }
            }
        }
    }

    fun enviarSolicitudDirecta(categoriaId: Int, descripcion: String) {
        val tecnico = uiState.value.tecnico ?: return
        val token = SessionManager.token ?: run {
            _uiState.update { it.copy(error = "Sesión no válida o expirada") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(enviandoSolicitud = true, error = null) }

            val resultado = solicitudesRepository.crearContratacionDirecta(
                token = token,
                tecnicoId = tecnico.id,
                categoriaId = categoriaId,
                descripcion = descripcion
            )

            resultado.onSuccess {
                _uiState.update { it.copy(enviandoSolicitud = false, enviadoExitoso = true) }
            }.onFailure { err ->
                _uiState.update { it.copy(enviandoSolicitud = false, error = err.localizedMessage) }
            }
        }
    }

    fun resetEstadoEnvio() {
        _uiState.update { it.copy(enviadoExitoso = false) }
    }
}