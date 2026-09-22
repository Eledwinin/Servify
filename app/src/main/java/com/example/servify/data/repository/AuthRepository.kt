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
                    val errorRaw = respuesta.errorBody()?.string() ?: ""
                    val mensajeAmigable = when (respuesta.code()) {
                        401 -> "Correo o contraseña incorrectos. Verifica tus datos."
                        404 -> "El usuario no existe."
                        500 -> "Error en el servidor. Intenta de nuevo más tarde."
                        else -> "No se pudo iniciar sesión (Error ${respuesta.code()})"
                    }
                    Result.failure(Exception(mensajeAmigable))
                }
            } catch (e: Exception) {
                Result.failure(Exception("No hay conexión con el servidor. Revisa tu internet."))
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
                // Esto te dirá si es 500, 404, 400 y el texto exacto que escupió Node.js
                val errorText = respuesta.errorBody()?.string() ?: "Sin detalle"
                Result.failure(Exception("Error HTTP ${respuesta.code()}: $errorText"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
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

    suspend fun verificarCodigoOtp(correo: String, codigo: String): Result<String> {
        return try {
            val respuesta = apiService.verificarCodigoOtp(
                CambiarPasswordRequest(correo = correo, codigo = codigo, nuevaPassword = "")
            )
            if (respuesta.isSuccessful) {
                Result.success(respuesta.body()?.mensaje ?: "Código válido")
            } else {
                val errorText = respuesta.errorBody()?.string() ?: "Código incorrecto"
                Result.failure(Exception(errorText))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }
}