package com.example.servify.ui.modulos.cliente.contratacion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.model.DetalleSolicitud
import com.example.servify.data.model.EstadoPago
import com.example.servify.data.model.PaymentSheetResponse
import com.example.servify.data.repository.SolicitudesRepository
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch

class CheckoutPagoViewModel(
    private val repository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {

    // Estado del flujo de pago con Stripe
    var estadoPago by mutableStateOf<EstadoPago>(EstadoPago.Idle)
        private set

    // Datos de la solicitud a pagar
    var solicitud by mutableStateOf<DetalleSolicitud?>(null)
        private set

    var cargandoSolicitud by mutableStateOf(false)
        private set

    // Monto a pagar editable si no viene prefijado
    var montoTexto by mutableStateOf("0.00")

    // QR generado tras el pago exitoso para validar el servicio
    var qrTokenGenerado by mutableStateOf<String?>(null)
        private set

    var mensajeNotificacion by mutableStateOf<String?>(null)

    fun cargarDetalle(solicitudId: Int) {
        if (solicitudId <= 0) return
        val token = SessionManager.token ?: return

        cargandoSolicitud = true
        viewModelScope.launch {
            try {
                val res = repository.obtenerDetalleSolicitud(token, solicitudId)
                res.onSuccess { det ->
                    solicitud = det
                    val montoInicial = det.presupuestoEstimado ?: 0.0
                    if (montoInicial > 0.0 && montoTexto == "0.00") {
                        montoTexto = String.format(java.util.Locale.US, "%.2f", montoInicial)
                    }
                }.onFailure { err ->
                    Log.e("CHECKOUT_VM", "Error al cargar solicitud: ${err.message}")
                }
            } catch (e: Exception) {
                Log.e("CHECKOUT_VM", "Excepción al cargar detalle: ${e.message}", e)
            } finally {
                cargandoSolicitud = false
            }
        }
    }

    /**
     * Paso 1: Solicita al backend de Servify los parámetros del PaymentSheet (POST /api/pagos/crear-payment-sheet).
     */
    fun iniciarPagoStripe(solicitudId: Int) {
        val token = SessionManager.token
        if (token.isNullOrEmpty()) {
            estadoPago = EstadoPago.Error("Sesión no válida o expirada. Por favor inicia sesión nuevamente.")
            return
        }

        val montoDouble = montoTexto.toDoubleOrNull()
        if (montoDouble == null || montoDouble <= 0.0) {
            estadoPago = EstadoPago.Error("Ingresa un monto válido mayor a 0 para realizar el pago.")
            return
        }

        estadoPago = EstadoPago.Cargando
        mensajeNotificacion = null

        viewModelScope.launch {
            try {
                Log.d("CHECKOUT_VM", "Llamando a crear-payment-sheet para solicitud $solicitudId, monto $montoDouble")
                val resultado = repository.crearPaymentSheet(
                    token = token,
                    solicitudId = solicitudId,
                    monto = montoDouble
                )

                resultado.onSuccess { response ->
                    Log.d("CHECKOUT_VM", "PaymentSheet listo con Customer: ${response.customer}")
                    estadoPago = EstadoPago.ListoParaPresentar(response)
                }.onFailure { err ->
                    val errorMsg = err.message ?: "No se pudo preparar la pasarela de pagos"
                    Log.e("CHECKOUT_VM", "Fallo al crear PaymentSheet: $errorMsg")
                    estadoPago = EstadoPago.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("CHECKOUT_VM", "Excepción inesperada en iniciarPagoStripe: ${e.message}", e)
                estadoPago = EstadoPago.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Paso 2: Procesa el resultado devuelto por Stripe PaymentSheet.
     */
    fun procesarResultadoStripe(result: PaymentSheetResult, solicitudId: Int) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                Log.d("CHECKOUT_VM", "¡Pago de Stripe COMPLETADO exitosamente!")
                estadoPago = EstadoPago.Exitoso("¡Pago completado con éxito! Generando QR de cierre...")
                mensajeNotificacion = "¡Pago exitoso!"

                // Refrescamos la solicitud y obtenemos el QR de cierre para el técnico
                finalizarPagoYCierre(solicitudId)
            }
            is PaymentSheetResult.Canceled -> {
                Log.d("CHECKOUT_VM", "El usuario canceló el PaymentSheet")
                estadoPago = EstadoPago.Idle
                mensajeNotificacion = "Pago cancelado"
            }
            is PaymentSheetResult.Failed -> {
                val errorMsg = result.error.localizedMessage ?: "Fallo al procesar la tarjeta"
                Log.e("CHECKOUT_VM", "Error en Stripe PaymentSheet: $errorMsg", result.error)
                estadoPago = EstadoPago.Error("Error en el pago: $errorMsg")
            }
        }
    }

    private fun finalizarPagoYCierre(solicitudId: Int) {
        val token = SessionManager.token ?: return
        val montoDouble = montoTexto.toDoubleOrNull()

        viewModelScope.launch {
            try {
                // Inicia el cierre en el backend registrando método tarjeta y monto
                val resCierre = repository.iniciarCierre(
                    token = token,
                    solicitudId = solicitudId,
                    metodoPago = "tarjeta",
                    tecnicoId = null,
                    monto = montoDouble
                )
                if (resCierre.isSuccess) {
                    qrTokenGenerado = resCierre.getOrNull()?.qrToken
                }

                // Recargamos el estado actualizado de la solicitud
                cargarDetalle(solicitudId)
            } catch (e: Exception) {
                Log.e("CHECKOUT_VM", "Error tras completar pago: ${e.message}", e)
            }
        }
    }

    fun restablecerEstado() {
        estadoPago = EstadoPago.Idle
    }
}
