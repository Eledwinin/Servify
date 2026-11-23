package com.example.servify.data.repository

import android.util.Log
import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ContratacionDirectaRequest
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.data.model.CrearPaymentSheetRequest
import com.example.servify.data.model.DetalleSolicitud
import com.example.servify.data.model.GenericResponse
import com.example.servify.data.model.IniciarCierreRequest
import com.example.servify.data.model.IniciarCierreResponse
import com.example.servify.data.model.MensajeResponse
import com.example.servify.data.model.PaymentSheetResponse
import com.example.servify.data.model.ResenaRequest
import com.example.servify.data.model.ResponderContratacionRequest
import com.example.servify.data.model.ValidarQrRequest
import com.example.servify.data.model.ValidarQrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SolicitudesRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {

    // 1. Crear Contratación Directa (Cliente -> Técnico)
    suspend fun crearContratacionDirecta(
        token: String,
        tecnicoId: Int,
        categoriaId: Int,
        descripcion: String,
        montoAcordado: Double? = null
    ): Result<MensajeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ContratacionDirectaRequest(
                    tecnico_id = tecnicoId,
                    categoria_id = categoriaId,
                    descripcion = descripcion,
                    monto_acordado = montoAcordado
                )
                val respuesta = apiService.crearContratacionDirecta("Bearer $token", req)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al enviar solicitud (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // 2. Obtener Contrataciones del Técnico (Sobrecito ✉️)
    suspend fun obtenerContratacionesTecnico(token: String): Result<List<ContratacionDirectaResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.obtenerContratacionesTecnico("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar solicitudes (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // 3. Responder a la Contratación Directa (Aceptar / Rechazar con motivo)
    suspend fun responderContratacionDirecta(
        token: String,
        idContratacion: Int,
        estado: String,
        motivo: String? = null
    ): Result<MensajeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ResponderContratacionRequest(estado = estado, motivo = motivo)
                val respuesta = apiService.responderContratacionDirecta("Bearer $token", idContratacion, req)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar estado (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // 4. Iniciar Cierre de Servicio (Cliente)
    // ==========================================
    suspend fun iniciarCierre(
        token: String,
        solicitudId: Int,
        metodoPago: String,
        tecnicoId: Int?,
        monto: Double? = null
    ): Result<IniciarCierreResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = IniciarCierreRequest(
                    metodoPago = metodoPago,
                    tecnicoId = tecnicoId,
                    monto = monto
                )
                val respuesta = apiService.iniciarCierre("Bearer $token", solicitudId, req)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al iniciar el cierre (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // 5. Validar QR (Técnico)
    // ==========================================
    suspend fun validarQr(
        token: String,
        qrToken: String,
        tecnicoId: Int
    ): Result<ValidarQrResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ValidarQrRequest(
                    qrToken = qrToken,
                    tecnicoId = tecnicoId
                )
                val respuesta = apiService.validarQr("Bearer $token", req)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Código QR inválido o expirado (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // 6. Crear Reseña y Calificación
    // ==========================================
    suspend fun crearResena(
        token: String,
        resena: ResenaRequest
    ): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.crearResena("Bearer $token", resena)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al guardar la reseña (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // 7. Pagos con Stripe (PaymentSheet)
    // ==========================================
    suspend fun crearPaymentSheet(
        token: String,
        solicitudId: Int,
        monto: Double
    ): Result<PaymentSheetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PAGOS_REPO", "Iniciando creación de PaymentSheet para solicitud $solicitudId, monto: $$monto")
                val req = CrearPaymentSheetRequest(solicitudId = solicitudId, monto = monto)
                val respuesta = apiService.crearPaymentSheet("Bearer $token", req)
                val codigo = respuesta.code()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val body = respuesta.body()!!
                    Log.d("PAGOS_REPO", "PaymentSheet creado exitosamente (Customer: ${body.customer})")
                    Result.success(body)
                } else {
                    val errBody = respuesta.errorBody()?.string() ?: "Sin detalle"
                    Log.e("PAGOS_REPO", "Error HTTP al crear PaymentSheet ($codigo): $errBody")
                    val mensaje = when (codigo) {
                        400 -> "Parámetros de pago inválidos: $errBody"
                        401 -> "Sesión expirada. Por favor vuelve a iniciar sesión."
                        404 -> "Solicitud de servicio no encontrada para pago."
                        500 -> "Error interno del servidor de pagos. Intenta nuevamente."
                        else -> "Error ($codigo) al preparar el pago: $errBody"
                    }
                    Result.failure(Exception(mensaje))
                }
            } catch (e: Exception) {
                Log.e("PAGOS_REPO", "Excepción al crear PaymentSheet: ${e.message}", e)
                Result.failure(Exception("Fallo de conexión con la pasarela de pagos: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // 8. Obtener Detalle de Solicitud
    // ==========================================
    suspend fun obtenerDetalleSolicitud(
        token: String,
        solicitudId: Int
    ): Result<DetalleSolicitud> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.obtenerDetalleSolicitud("Bearer $token", solicitudId)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar detalle de solicitud (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }
}