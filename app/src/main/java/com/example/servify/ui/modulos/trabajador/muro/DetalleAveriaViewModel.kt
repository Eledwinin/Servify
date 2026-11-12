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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
                val (det, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerDetalleSolicitud("Bearer $token", id)
                    if (res.isSuccessful) {
                        Triple(res.body(), null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(null, e, res.code())
                    }
                }
                if (det != null) {
                    detalle = det
                } else {
                    android.util.Log.e("DETALLE", "Código $code - $err")
                    mensajeError = "Error $code al cargar la solicitud"
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
                val (ok, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.crearPostulacion(
                        "Bearer $token",
                        CrearPostulacionRequest(d.id, precio, tiempo, mensaje)
                    )
                    if (res.isSuccessful) {
                        Triple(true, null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(false, e, res.code())
                    }
                }
                if (ok) {
                    postulacionExitosa = true
                } else {
                    android.util.Log.e("POSTULACION", "Código $code - $err")
                    mensajeError = if (code == 409) "Ya te postulaste a esta solicitud"
                    else "Error $code al postularte"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }

    fun tiempoRelativo(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val fecha = formato.parse(iso.take(19)) ?: return ""
            val ahora = System.currentTimeMillis()
            val diffMs = ahora - fecha.time
            val diffMinutos = (diffMs / (1000 * 60)).toInt()
            val diffHoras = (diffMinutos / 60)
            val diffDias = (diffHoras / 24)

            return when {
                diffMinutos < 1 -> "Hace un momento"
                diffMinutos < 60 -> "Hace $diffMinutos min"
                diffHoras < 24 -> "Hace $diffHoras h"
                diffDias == 1 -> "Ayer"
                diffDias < 7 -> "Hace $diffDias días"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun calcularDiasCliente(fechaIso: String?): Int {
        if (fechaIso.isNullOrBlank()) return 0
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val fechaRegistro = formato.parse(fechaIso.take(19)) ?: return 0
            val diffMs = System.currentTimeMillis() - fechaRegistro.time
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
