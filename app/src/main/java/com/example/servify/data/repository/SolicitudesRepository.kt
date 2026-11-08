package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ContratacionDirectaRequest
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.data.model.GenericResponse
import com.example.servify.data.model.IniciarCierreRequest
import com.example.servify.data.model.IniciarCierreResponse
import com.example.servify.data.model.MensajeResponse
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

    // 3. Responder a la Contratación Directa (Aceptar / Rechazar)
    suspend fun responderContratacionDirecta(
        token: String,
        idContratacion: Int,
        estado: String
    ): Result<MensajeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ResponderContratacionRequest(estado)
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
}