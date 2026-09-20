
package com.example.servify.data.api

import com.example.servify.data.model.Categoria
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Categoria>>
}
