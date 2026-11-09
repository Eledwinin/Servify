package com.example.servify.modulos_compartidos.pagos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.api.SuscripcionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PagoUiState {
    object Idle : PagoUiState()
    object Loading : PagoUiState()
    data class Success(val mensaje: String, val esVip: Int) : PagoUiState()
    data class Error(val mensaje: String) : PagoUiState()
}

class PagosViewModel(
    private val apiService: ApiService = RetrofitClient.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow<PagoUiState>(PagoUiState.Idle)
    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    fun procesarPagoSuscripcion(
        monto: Double,
        tipoPlan: String,
        metodoPago: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = PagoUiState.Loading

            val token = SessionManager.token
            if (token.isNullOrEmpty()) {
                _uiState.value = PagoUiState.Error("No se encontró sesión activa")
                return@launch
            }

            try {
                val request = SuscripcionRequest(
                    monto = monto,
                    tipoPlan = tipoPlan,
                    metodoPago = metodoPago
                )

                val response = apiService.suscribirsePlan("Bearer $token", request)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // Activa el estado VIP reactivo en SessionManager
                    SessionManager.activarSuscripcionVip(tipoPlan)

                    _uiState.value = PagoUiState.Success(data.mensaje, data.esVip)
                    onExito()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al procesar la suscripción"
                    _uiState.value = PagoUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = PagoUiState.Error(e.localizedMessage ?: "Error de conexión con el servidor")
            }
        }
    }

    fun reiniciarEstado() {
        _uiState.value = PagoUiState.Idle
    }
}