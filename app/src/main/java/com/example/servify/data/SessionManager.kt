package com.example.servify.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.servify.data.model.UsuarioModel

object SessionManager {
    var token: String? = null
        private set

    val esTecnico: Boolean
        get() = usuarioActual?.rol?.lowercase() in listOf("tecnico", "trabajador")

    var esVip: Boolean by mutableStateOf(false)

    var usuarioActual: UsuarioModel? = null
        private set

    fun guardarSesion(nuevoToken: String?, usuario: UsuarioModel?) {
        token = nuevoToken
        usuarioActual = usuario
        esVip = usuario?.esVip ?: false
    }

    fun activarSuscripcionVip() {
        esVip = true
    }

    fun cancelarSuscripcionVip() {
        esVip = false
    }

    fun cerrarSesion() {
        token = null
        usuarioActual = null
        esVip = false
    }

    fun estaAutenticado(): Boolean = !token.isNullOrBlank()
}