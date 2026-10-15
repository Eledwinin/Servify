package com.example.servify.ui.modulos.cliente.contratacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.CrearSolicitudRequest
import kotlinx.coroutines.launch

class NuevaSolicitudViewModel : ViewModel() {
    var categoria by mutableStateOf("")
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var presupuesto by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var solicitudExitosa by mutableStateOf(false)


    fun enviarSolicitud(tokenParam: String) {
        val tokenValido = if (tokenParam.isNotBlank()) tokenParam else SessionManager.token ?: ""

        if (tokenValido.isBlank()) {
            mensajeError = "No hay sesión activa. Por favor, inicia sesión de nuevo."
            return
        }

        if (titulo.isBlank() || descripcion.isBlank() || categoria.isBlank()) {
            mensajeError = "Por favor llena la categoría, el título y la descripción."
            return
        }

        val lat = SessionManager.latitud
        val lng = SessionManager.longitud
        if (lat == null || lng == null) {
            mensajeError = "No se pudo obtener tu ubicación. Activa el GPS e inténtalo de nuevo."
            return
        }

        val categoriaId = when (categoria) {
            "Plomería" -> 1
            "Electricidad" -> 2
            "Cerrajería" -> 3
            "Carpintería" -> 4
            "Refrigeración" -> 5
            "Mantenimiento de Cómputo" -> 6
            else -> 1
        }

        val presupuestoDouble = presupuesto.toDoubleOrNull()

        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                val request = CrearSolicitudRequest(
                    categoriaId = categoriaId,
                    titulo = titulo,
                    descripcion = descripcion,
                    presupuesto = presupuestoDouble,
                    latitud = lat,
                    longitud = lng
                )
                val authHeader = if (tokenValido.startsWith("Bearer ")) tokenValido else "Bearer $tokenValido"

                val respuesta = RetrofitClient.instance.crearSolicitud(authHeader, request)

                if (respuesta.isSuccessful) {
                    solicitudExitosa = true
                } else {
                    val errorReal = respuesta.errorBody()?.string() ?: "Error desconocido"
                    android.util.Log.e("SOLICITUD", "Código ${respuesta.code()} - $errorReal")
                    mensajeError = "Fallo del servidor: ${respuesta.code()} - $errorReal"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}