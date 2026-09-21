package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.LoginRequest
import com.example.servify.data.model.RegisterRequest
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
}