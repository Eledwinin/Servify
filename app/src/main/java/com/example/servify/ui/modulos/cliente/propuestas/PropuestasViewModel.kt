package com.example.servify.ui.modulos.cliente.propuestas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.PropuestaItem
import kotlinx.coroutines.launch
import retrofit2.Response

class PropuestasViewModel : ViewModel() {
    var propuestas by mutableStateOf<List<PropuestaItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf<String?>(null)

    private var solicitudActual = 0

    fun cargar(solicitudId: Int) {
        solicitudActual = solicitudId
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerPropuestas("Bearer $token", solicitudId)
                if (res.isSuccessful) {
                    propuestas = res.body() ?: emptyList()
                } else {
                    android.util.Log.e("PROPUESTAS", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar las propuestas"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun aceptar(id: Int) = accion("Propuesta aceptada") { token ->
        RetrofitClient.instance.aceptarPropuesta(token, id)
    }

    fun rechazar(id: Int) = accion("Propuesta rechazada") { token ->
        RetrofitClient.instance.rechazarPropuesta(token, id)
    }

    fun cancelarAcuerdo(id: Int) = accion("Acuerdo cancelado") { token ->
        RetrofitClient.instance.cancelarAcuerdo(token, id)
    }

    private fun <T> accion(exito: String, llamada: suspend (String) -> Response<T>) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            try {
                val res = llamada("Bearer $token")
                if (res.isSuccessful) {
                    mensajeExito = exito
                    cargar(solicitudActual)
                } else {
                    val err = res.errorBody()?.string()
                    android.util.Log.e("PROPUESTAS", "Código ${res.code()} - $err")
                    mensajeError = "Error ${res.code()}: ${err ?: "no se pudo completar"}"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}