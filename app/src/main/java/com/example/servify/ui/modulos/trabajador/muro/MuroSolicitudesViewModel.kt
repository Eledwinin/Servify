package com.example.servify.ui.modulos.trabajador.muro

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.data.model.CrearPostulacionRequest
import com.example.servify.data.model.SolicitudItemTecnico
import com.example.servify.data.repository.SolicitudesRepository
import kotlinx.coroutines.launch

class MuroSolicitudesViewModel(
    private val solicitudesRepository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {

    var solicitudes by mutableStateOf<List<SolicitudItemTecnico>>(emptyList())
    var contratacionesDirectas by mutableStateOf<List<ContratacionDirectaResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf<String?>(null)

    fun cargarSolicitudes() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                // 1. Cargar solicitudes públicas
                val res = RetrofitClient.instance.obtenerSolicitudes(
                    "Bearer $token", SessionManager.latitud, SessionManager.longitud
                )
                if (res.isSuccessful) {
                    solicitudes = res.body() ?: emptyList()
                } else {
                    val err = res.errorBody()?.string()
                    Log.e("MURO", "Código ${res.code()}: $err")
                    mensajeError = "Error ${res.code()} al cargar solicitudes"
                }

                // 2. Cargar solicitudes directas (Sobrecito ✉️)
                val resDirectas = solicitudesRepository.obtenerContratacionesTecnico(token)
                resDirectas.onSuccess { lista ->
                    // Guardamos la lista completa para poder gestionar pendientes y aceptadas
                    contratacionesDirectas = lista
                }

            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun responderContratacion(idContratacion: Int, estado: String) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            val resultado = solicitudesRepository.responderContratacionDirecta(token, idContratacion, estado)
            resultado.onSuccess {
                mensajeExito = if (estado == "aceptado") "¡Solicitud aceptada!" else "Solicitud rechazada"

                // Actualizamos el estado localmente para que la tarjeta cambie y muestre el botón de Chat
                contratacionesDirectas = contratacionesDirectas.map { item ->
                    if (item.id == idContratacion) item.copy(estado = estado) else item
                }
            }.onFailure { err ->
                mensajeError = err.localizedMessage
            }
        }
    }

    // Quitar la contratación directa de la lista local al presionar la papelera 🗑️
    fun eliminarContratacionDirectaLocal(id: Int) {
        contratacionesDirectas = contratacionesDirectas.filter { it.id != id }
    }

    fun ignorar(id: Int) {
        solicitudes = solicitudes.filter { it.id != id }
    }

    fun postular(solicitudId: Int, precio: Double, tiempo: String, mensaje: String) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.crearPostulacion(
                    "Bearer $token",
                    CrearPostulacionRequest(solicitudId, precio, tiempo, mensaje)
                )
                if (res.isSuccessful) {
                    mensajeExito = "Postulación enviada"
                    ignorar(solicitudId) // la quitamos del muro
                } else {
                    val err = res.errorBody()?.string()
                    Log.e("POSTULACION", "Código ${res.code()}: $err")
                    mensajeError = if (res.code() == 409) "Ya te postulaste a esta solicitud"
                    else "Error ${res.code()} al postularte"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}