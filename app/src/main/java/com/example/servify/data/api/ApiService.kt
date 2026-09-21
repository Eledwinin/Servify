package com.example.servify.data.api

import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun registrarUsuario(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
}