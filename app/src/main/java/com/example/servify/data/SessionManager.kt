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

    // Categoría / Especialidad del técnico
    var categoriaId: Int? = null
        private set

    var tipoPlanActivo: String? = null // Para guardar si es "Plan Semanal" o "Plan Servify PRO"
        private set

    var categoriaNombre: String? = null
        private set

    val esTecnico: Boolean
        get() = usuarioActual?.rol?.lowercase() in listOf("tecnico", "trabajador", "ambos")

    var esVip: Boolean by mutableStateOf(false)

    var usuarioActual: UsuarioModel? = null
        private set

    fun guardarSesion(nuevoToken: String?, usuario: UsuarioModel?) {
        token = nuevoToken

        // Preserva la categoría si el nuevo objeto de perfil la devuelve nula/vacía
        val idFinal = usuario?.categoriaId?.takeIf { it != 0 }
            ?: usuarioActual?.categoriaId
            ?: categoriaId

        val nombreFinal = usuario?.categoriaNombre?.takeIf { it.isNotBlank() }
            ?: usuarioActual?.categoriaNombre
            ?: categoriaNombre

        val usuarioCombinado = usuario?.copy(
            categoriaId = idFinal,
            categoriaNombre = nombreFinal
        )

        usuarioActual = usuarioCombinado
        categoriaId = idFinal
        categoriaNombre = nombreFinal
        esVip = usuarioCombinado?.esVip ?: false
    }

    fun guardarCategoriaSeleccionada(id: Int, nombre: String) {
        categoriaId = id
        categoriaNombre = nombre
        usuarioActual = usuarioActual?.copy(
            categoriaId = id,
            categoriaNombre = nombre
        )
    }

    fun actualizarUbicacion(lat: Double, lng: Double) {
        latitud = lat
        longitud = lng
    }

    fun activarSuscripcionVip(tipo: String = "VIP") {
        esVip = true
        tipoPlanActivo = tipo
        usuarioActual = usuarioActual?.copy(esVip = true, tipoPlan = tipo)
    }

    fun cancelarSuscripcionVip() {
        esVip = false
        tipoPlanActivo = null
        usuarioActual = usuarioActual?.copy(
            esVip = false,
            tipoPlan = null,
            fechaVencimientoVip = null
        )
    }

    fun cerrarSesion() {
        token = null
        usuarioActual = null
        categoriaId = null
        categoriaNombre = null
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
        return usuarioActual?.fotoUrl
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