package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.CambiarPasswordRequest
import com.example.servify.data.model.LoginRequest
import com.example.servify.data.model.RegisterRequest
import com.example.servify.data.model.SolicitarRecuperacionRequest
import com.example.servify.data.model.UsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {

    suspend fun registrarUsuario(request: RegisterRequest): Response<AuthResponse> {
        return withContext(Dispatchers.IO) {
            apiService.registrarUsuario(request)
        }
    }

    suspend fun iniciarSesion(correo: String, clave: String): Result<UsuarioModel> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.login(LoginRequest(correo = correo, password = clave))

                if (respuesta.isSuccessful) {
                    val cuerpo = respuesta.body()
                    val usuario = cuerpo?.usuario

                    if (cuerpo?.success == true && usuario != null) {
                        Result.success(usuario)
                    } else {
                        Result.failure(Exception(cuerpo?.message ?: "Error al autenticar usuario"))
                    }
                } else {
                    Result.failure(Exception("Error del servidor: código HTTP ${respuesta.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("No se pudo conectar con el servidor: ${e.localizedMessage ?: "Error de red"}"))
            }
        }
    }

    suspend fun solicitarRecuperacion(correo: String): Result<String> {
        return try {
            val respuesta = apiService.solicitarRecuperacion(
                SolicitarRecuperacionRequest(correo)
            )
            if (respuesta.isSuccessful) {
                Result.success(respuesta.body()?.mensaje ?: "Código enviado exitosamente")
            } else {
                Result.failure(Exception("Error al enviar código: correo no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    suspend fun cambiarPassword(correo: String, codigo: String, nuevaPassword: String): Result<String> {
        return try {
            val respuesta = apiService.cambiarPassword(
                CambiarPasswordRequest(correo, codigo, nuevaPassword)
            )
            if (respuesta.isSuccessful) {
                Result.success(respuesta.body()?.mensaje ?: "Contraseña actualizada exitosamente")
            } else {
                Result.failure(Exception("El código es incorrecto o ha expirado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }
}