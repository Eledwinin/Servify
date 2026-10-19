package com.example.servify.ui.modulos.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.model.DashboardData
import com.example.servify.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val data: DashboardData? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    fun cargarMetricas() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, error = null)
            val resultado = repository.obtenerDashboardStats(token)
            resultado.onSuccess { data ->
                _uiState.value = _uiState.value.copy(data = data, cargando = false)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(cargando = false, error = err.message)
            }
        }
    }
}