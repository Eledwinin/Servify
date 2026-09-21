

package com.example.servify.data.repository

import com.example.servify.data.api.ApiService
import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.RegisterRequest
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun registrarUsuario(request: RegisterRequest): Response<AuthResponse> {
        return apiService.registrarUsuario(request)
    }
}