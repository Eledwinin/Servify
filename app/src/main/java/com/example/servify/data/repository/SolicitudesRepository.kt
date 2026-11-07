package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ContratacionDirectaRequest
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.data.model.MensajeResponse
import com.example.servify.data.model.ResponderContratacionRequest
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
}