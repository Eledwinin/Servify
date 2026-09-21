

package com.example.servify.data.model

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