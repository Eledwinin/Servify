package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ActualizarPerfilRequest
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.model.UsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun obtenerPerfil(token: String): Result<UsuarioModel> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getPerfil("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar perfil (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun actualizarPerfil(
        token: String,
        nombre: String,
        telefono: String?,
        direccion: String?
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ActualizarPerfilRequest(nombre, telefono, direccion)
                val respuesta = apiService.actualizarPerfil("Bearer $token", req)
                if (respuesta.isSuccessful) {
                    Result.success(respuesta.body()?.mensaje ?: "Perfil actualizado correctamente")
                } else {
                    Result.failure(Exception("Error al guardar cambios (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun obtenerPerfilProfesional(id: String): Result<ProfesionalPerfil> {
        return try {
            val respuesta = apiService.getPerfilProfesional(id)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Error al cargar perfil (${respuesta.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}