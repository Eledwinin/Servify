package com.example.servify.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.servify.data.model.UsuarioModel

object SessionManager {
    var token: String? = null
        private set

    // Ubicación actual del usuario (se llena desde HomeScreen)
    var latitud: Double? = null
    var longitud: Double? = null

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

    fun actualizarUbicacion(lat: Double, lng: Double) {
        latitud = lat
        longitud = lng
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
        latitud = null
        longitud = null
    }

    fun estaAutenticado(): Boolean = !token.isNullOrBlank()

    // Obtiene el nombre completo del usuario de forma segura
    fun obtenerNombre(): String {
        return usuarioActual?.nombre ?: "Usuario Servify"
    }

    // Obtiene la URL de la foto de perfil desde el modelo de usuario actual
    fun obtenerFotoUrl(): String? {
        return usuarioActual?.fotoUrl // Asegúrate de que en UsuarioModel tu variable de foto se llame asi (o fotoUrl)
    }

    // Genera las iniciales automáticamente a partir del nombre
    fun obtenerIniciales(): String {
        val nombre = obtenerNombre()
        return nombre.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .ifEmpty { "SV" }
    }
}