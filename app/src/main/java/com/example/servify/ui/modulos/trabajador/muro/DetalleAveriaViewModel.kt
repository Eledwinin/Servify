package com.example.servify.ui.modulos.trabajador.muro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.CrearPostulacionRequest
import com.example.servify.data.model.DetalleSolicitud
import kotlinx.coroutines.launch

class DetalleAveriaViewModel : ViewModel() {
    var detalle by mutableStateOf<DetalleSolicitud?>(null)
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var postulacionExitosa by mutableStateOf(false)

    fun cargar(id: Int) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerDetalleSolicitud("Bearer $token", id)
                if (res.isSuccessful) {
                    detalle = res.body()
                } else {
                    android.util.Log.e("DETALLE", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar la solicitud"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun postular(precio: Double, tiempo: String, mensaje: String) {
        val token = SessionManager.token ?: return
        val d = detalle ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.crearPostulacion(
                    "Bearer $token",
                    CrearPostulacionRequest(d.id, precio, tiempo, mensaje)
                )
                if (res.isSuccessful) {
                    postulacionExitosa = true
                } else {
                    android.util.Log.e("POSTULACION", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = if (res.code() == 409) "Ya te postulaste a esta solicitud"
                    else "Error ${res.code()} al postularte"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}