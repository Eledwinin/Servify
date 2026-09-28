package com.example.servify.data

import com.example.servify.data.model.UsuarioModel

object SessionManager {
    var token: String? = null
        private set

    var usuarioActual: UsuarioModel? = null
        private set

    fun guardarSesion(nuevoToken: String?, usuario: UsuarioModel?) {
        token = nuevoToken
        usuarioActual = usuario
    }

    fun cerrarSesion() {
        token = null
        usuarioActual = null
    }

    fun estaAutenticado(): Boolean = !token.isNullOrBlank()
}