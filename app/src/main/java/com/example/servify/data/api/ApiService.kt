package com.example.servify.data.api

import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.LoginRequest
import com.example.servify.data.model.LoginResponse
import com.example.servify.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Categoria>>

    //endpoint para el login
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Endpoint para Registro
    @POST("api/auth/register")
    suspend fun registrarUsuario(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
}