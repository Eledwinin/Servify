package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.model.ResenaRequest
import com.example.servify.data.repository.SolicitudesRepository
import kotlinx.coroutines.launch

class CrearResenaViewModel(
    private val repository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {

    var calificacion by mutableStateOf(5)
    var comentario by mutableStateOf("")

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var resenaEnviada by mutableStateOf(false)

    fun enviarResena(solicitudId: Int, destinatarioId: Int) {
        val token = SessionManager.token
        val autorId = SessionManager.usuarioActual?.id

        if (token.isNullOrEmpty() || autorId == null || autorId == 0) {
            mensajeError = "Sesión no válida o expirada"
            return
        }

        // 🛑 VALIDACIÓN PARA EVITAR ERROR 404 POR IDs NULOS O EN 0
        if (solicitudId <= 0 || destinatarioId <= 0) {
            Log.e("CREAR_RESENA", "IDs inválidos - solicitudId: $solicitudId, destinatarioId: $destinatarioId")
            mensajeError = "No se encontró la información del destinatario o solicitud"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            try {
                val req = ResenaRequest(
                    autorId = autorId,
                    destinatarioId = destinatarioId,
                    solicitudId = solicitudId,
                    calificacion = calificacion,
                    comentario = comentario.ifBlank { null }
                )

                val resultado = repository.crearResena(token, req)
                if (resultado.isSuccess) {
                    resenaEnviada = true
                } else {
                    val error = resultado.exceptionOrNull()
                    Log.e("CREAR_RESENA", "Falla backend: ${error?.message}", error)
                    mensajeError = error?.message ?: "Error al guardar la reseña (HTTP 404/500)"
                }
            } catch (e: Exception) {
                Log.e("CREAR_RESENA", "Excepción al enviar reseña: ${e.message}", e)
                mensajeError = e.localizedMessage ?: "Error de conexión"
            } finally {
                cargando = false
            }
        }
    }
}