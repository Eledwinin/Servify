package com.example.servify.data.repository

import android.util.Log
import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.CrearPaymentSheetRequest
import com.example.servify.data.model.PaymentSheetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio encargado de gestionar las pasarelas de pago (Stripe PaymentSheet)
 * tanto para servicios como para suscripciones VIP/PRO de técnicos.
 */
class PagosRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    /**
     * Solicita al backend de Servify los parámetros para inicializar el PaymentSheet de Stripe
     * para una suscripción VIP / PRO de técnico.
     * Endpoint: POST /api/pagos/crear-payment-sheet
     *
     * @param token JWT del usuario autenticado en SessionManager.
     * @param monto Precio numérico del plan (ej. 3.99, 6.99, 9.99 o 19.99).
     * @param tipo Tipo de pago; para suscripciones se envía "vip".
     */
    suspend fun crearPaymentSheetSuscripcion(
        token: String,
        monto: Double,
        tipo: String = "vip"
    ): Result<PaymentSheetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PAGOS_REPO", "Iniciando creación de PaymentSheet para suscripción tipo=$tipo, monto=$$monto")
                val request = CrearPaymentSheetRequest(
                    solicitudId = null,
                    monto = monto,
                    tipo = tipo
                )

                val respuesta = apiService.crearPaymentSheet("Bearer $token", request)
                val codigo = respuesta.code()

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val body = respuesta.body()!!
                    Log.d("PAGOS_REPO", "PaymentSheet suscripción creado con éxito (Customer: ${body.customer})")
                    Result.success(body)
                } else {
                    val errBody = respuesta.errorBody()?.string() ?: "Sin detalle de error"
                    Log.e("PAGOS_REPO", "Error HTTP al crear PaymentSheet suscripción ($codigo): $errBody")
                    val mensaje = when (codigo) {
                        400 -> "Parámetros de suscripción inválidos: $errBody"
                        401 -> "Sesión expirada. Por favor vuelve a iniciar sesión."
                        500 -> "Error en el servidor de pagos. Intenta nuevamente más tarde."
                        else -> "Error ($codigo) al preparar la pasarela de pagos: $errBody"
                    }
                    Result.failure(Exception(mensaje))
                }
            } catch (e: Exception) {
                Log.e("PAGOS_REPO", "Excepción al conectar con pasarela de pagos: ${e.message}", e)
                Result.failure(Exception("Fallo de conexión con la pasarela de pagos: ${e.localizedMessage}"))
            }
        }
    }
}
