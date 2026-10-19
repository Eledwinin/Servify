package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.AdminSolicitudItem
import com.example.servify.data.model.AdminUsuarioItem
import com.example.servify.data.model.CambiarRolRequest
import com.example.servify.data.model.DashboardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun obtenerDashboardStats(token: String): Result<DashboardData> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getDashboardStats("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    Result.success(respuesta.body()!!.data)
                } else {
                    Result.failure(Exception("Error al cargar métricas (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de red: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun obtenerUsuarios(
        token: String,
        busqueda: String? = null,
        rol: String? = null
    ): Result<List<AdminUsuarioItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getUsuariosAdmin("Bearer $token", busqueda, rol)
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    Result.success(respuesta.body()!!.usuarios)
                } else {
                    Result.failure(Exception("Error al obtener usuarios (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun cambiarRol(
        token: String,
        usuarioId: Int,
        nuevoRol: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.cambiarRolUsuario(
                    "Bearer $token",
                    usuarioId,
                    CambiarRolRequest(nuevoRol)
                )
                if (respuesta.isSuccessful) {
                    Result.success(respuesta.body()?.mensaje ?: "Rol actualizado correctamente")
                } else {
                    Result.failure(Exception("No se pudo actualizar el rol (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun obtenerSolicitudes(token: String): Result<List<AdminSolicitudItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getSolicitudesAdmin("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    Result.success(respuesta.body()!!.solicitudes)
                } else {
                    Result.failure(Exception("Error al obtener solicitudes (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun cancelarSolicitud(token: String, solicitudId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.cancelarSolicitudAdmin("Bearer $token", solicitudId)
                if (respuesta.isSuccessful) {
                    Result.success(respuesta.body()?.mensaje ?: "Solicitud dada de baja correctamente")
                } else {
                    Result.failure(Exception("No se pudo cancelar la solicitud (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }
}