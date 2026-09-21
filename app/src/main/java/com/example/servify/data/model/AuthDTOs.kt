package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

// 1. Lo que mandamos desde el formulario de registro a Node.js
data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    val rol: String
)

// 2. Lo que Node.js nos responde cuando el registro es exitoso
data class AuthResponse(
    val mensaje: String,
    val token: String,
    val usuario: UsuarioModel
)

data class LoginRequest(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("token")
    val token: String?,

    @SerializedName("usuario")
    val usuario: UsuarioModel?
)

// lo que mandamos para pedir el código de 6 dígitos
data class SolicitarRecuperacionRequest(
    @SerializedName("correo")
    val correo: String
)

// lo que mandamos para validar el código y cambiar la clave
data class CambiarPasswordRequest(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("nuevaPassword")
    val nuevaPassword: String
)

//lo que Node.js nos responde en ambos endpoints
data class MensajeResponse(
    @SerializedName("mensaje")
    val mensaje: String
)