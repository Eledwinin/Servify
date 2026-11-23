package com.example.servify.modulos_compartidos.pagos

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.api.SuscripcionRequest
import com.example.servify.data.model.EstadoPago
import com.example.servify.data.repository.PagosRepository
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class PagoUiState {
    object Idle : PagoUiState()
    object Loading : PagoUiState()
    data class Success(val mensaje: String, val esVip: Int) : PagoUiState()
    data class Error(val mensaje: String) : PagoUiState()
}

class PagosViewModel(
    private val apiService: ApiService = RetrofitClient.instance,
    private val pagosRepository: PagosRepository = PagosRepository(apiService)
) : ViewModel() {

    // Estado reactivo para Stripe PaymentSheet
    var estadoPago by mutableStateOf<EstadoPago>(EstadoPago.Idle)
        private set

    var planEnProceso by mutableStateOf<String?>(null)
        private set

    var mensajeNotificacion by mutableStateOf<String?>(null)

    // Estado retrocompatible para flujos alternativos
    private val _uiState = MutableStateFlow<PagoUiState>(PagoUiState.Idle)
    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    /**
     * Paso 1 (Stripe): Solicita los parámetros de PaymentSheet para suscribirse a un plan VIP / PRO.
     * Endpoint: POST /api/pagos/crear-payment-sheet con body { "tipo": "vip", "monto": <monto> }
     */
    fun iniciarPagoSuscripcionStripe(monto: Double, tipoPlan: String) {
        val token = SessionManager.token
        if (token.isNullOrEmpty() || !SessionManager.estaAutenticado()) {
            estadoPago = EstadoPago.Error("No se encontró una sesión activa. Inicia sesión nuevamente.")
            return
        }

        if (monto <= 0.0) {
            estadoPago = EstadoPago.Error("El monto del plan debe ser mayor a 0.")
            return
        }

        planEnProceso = tipoPlan
        estadoPago = EstadoPago.Cargando
        mensajeNotificacion = null

        viewModelScope.launch {
            try {
                Log.d("PAGOS_VM", "Solicitando PaymentSheet para suscripción: $tipoPlan ($$monto)")
                val resultado = pagosRepository.crearPaymentSheetSuscripcion(
                    token = token,
                    monto = monto,
                    tipo = "vip"
                )

                resultado.onSuccess { response ->
                    Log.d("PAGOS_VM", "PaymentSheet listo (Customer: ${response.customer})")
                    estadoPago = EstadoPago.ListoParaPresentar(response, tipoPlan)
                }.onFailure { err ->
                    val errorMsg = err.message ?: "No se pudo preparar la pasarela de pagos"
                    Log.e("PAGOS_VM", "Error al crear PaymentSheet: $errorMsg")
                    estadoPago = EstadoPago.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("PAGOS_VM", "Excepción al iniciar pago Stripe: ${e.message}", e)
                estadoPago = EstadoPago.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Paso 2 (Stripe): Procesa el resultado devuelto por Stripe PaymentSheet.
     */
    fun procesarResultadoStripe(
        result: PaymentSheetResult,
        tipoPlan: String,
        onExito: () -> Unit = {}
    ) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                Log.d("PAGOS_VM", "¡Pago de Stripe para $tipoPlan COMPLETADO exitosamente!")

                // 🌟 Activa inmediatamente el estado VIP reactivo en SessionManager
                SessionManager.activarSuscripcionVip(tipoPlan)

                estadoPago = EstadoPago.Exitoso("¡Suscripción a $tipoPlan activada con éxito!", tipoPlan)
                mensajeNotificacion = "¡Felicidades! Tu $tipoPlan ya está activo."
                onExito()
            }
            is PaymentSheetResult.Canceled -> {
                Log.d("PAGOS_VM", "El usuario canceló el PaymentSheet de Stripe")
                estadoPago = EstadoPago.Cancelado("Operación de suscripción cancelada")
                mensajeNotificacion = "Pago cancelado"
            }
            is PaymentSheetResult.Failed -> {
                val errorMsg = result.error.localizedMessage ?: "Fallo al procesar la tarjeta con Stripe"
                Log.e("PAGOS_VM", "Error en Stripe PaymentSheet: $errorMsg", result.error)
                estadoPago = EstadoPago.Error("Error en el pago: $errorMsg")
            }
        }
    }

    fun restablecerEstado() {
        estadoPago = EstadoPago.Idle
        mensajeNotificacion = null
        planEnProceso = null
    }

    // --- Flujo alternativo existente de suscripción ---
    fun procesarPagoSuscripcion(
        monto: Double,
        tipoPlan: String,
        metodoPago: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = PagoUiState.Loading

            val token = SessionManager.token
            if (token.isNullOrEmpty() || !SessionManager.estaAutenticado()) {
                _uiState.value = PagoUiState.Error("No se encontró sesión activa")
                return@launch
            }

            try {
                val request = SuscripcionRequest(
                    monto = monto,
                    tipoPlan = tipoPlan,
                    metodoPago = metodoPago
                )

                val (data, errorMsg) = withContext(Dispatchers.IO) {
                    val response = apiService.suscribirsePlan("Bearer $token", request)
                    if (response.isSuccessful && response.body() != null) {
                        Pair(response.body(), null)
                    } else {
                        val msg = response.errorBody()?.string() ?: "Error al procesar la suscripción"
                        Pair(null, msg)
                    }
                }

                if (data != null) {
                    SessionManager.activarSuscripcionVip(tipoPlan)
                    _uiState.value = PagoUiState.Success(data.mensaje, data.esVip)
                    onExito()
                } else {
                    _uiState.value = PagoUiState.Error(errorMsg ?: "Error al procesar la suscripción")
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
