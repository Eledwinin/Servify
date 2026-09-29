package com.example.servify.ui.modulos.cliente.profesional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// esto es el estado de la pantalla
data class DetalleTecnicoUiState(
    val isLoading: Boolean = false,
    val tecnico: ProfesionalPerfil? = null,
    val error: String? = null
)

// el view model se conecta al repo
class DetalleTecnicoViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleTecnicoUiState())
    val uiState: StateFlow<DetalleTecnicoUiState> = _uiState.asStateFlow()

    fun cargarDetalleTecnico(idTecnico: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val resultado = repository.obtenerPerfilProfesional(idTecnico.toString())

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
}