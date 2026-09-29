package com.example.servify.data.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://servify-backend-45ae.onrender.com/"
    // private const val BASE_URL = "http://192.168.18.7:3000/"

    // Convierte automáticamente 1/0, strings numéricos o booleanos a Boolean
    private val booleanDeserializer = JsonDeserializer { json, _, _ ->
        if (json.isJsonPrimitive) {
            val primitive = json.asJsonPrimitive
            when {
                primitive.isBoolean -> primitive.asBoolean
                primitive.isNumber -> primitive.asInt == 1
                primitive.isString -> primitive.asString == "1" || primitive.asString.equals("true", ignoreCase = true)
                else -> false
            }
        } else {
            false
        }
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, booleanDeserializer)
        .registerTypeAdapter(java.lang.Boolean::class.java, booleanDeserializer)
        .create()

    // Agregamos tiempo de espera extra por si Render está dormido
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}