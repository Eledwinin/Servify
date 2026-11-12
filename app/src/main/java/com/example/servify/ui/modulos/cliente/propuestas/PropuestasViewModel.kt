package com.example.servify.ui.modulos.cliente.propuestas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.PropuestaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                val (lista, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerPropuestas("Bearer $token", solicitudId)
                    if (res.isSuccessful) {
                        Triple(res.body() ?: emptyList(), null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(emptyList<PropuestaItem>(), e, res.code())
                    }
                }
                if (err == null) {
                    propuestas = lista
                } else {
                    android.util.Log.e("PROPUESTAS", "Código $code - $err")
                    mensajeError = "Error $code al cargar las propuestas"
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
                val (exitoLlamada, err, code) = withContext(Dispatchers.IO) {
                    val res = llamada("Bearer $token")
                    if (res.isSuccessful) {
                        Triple(true, null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(false, e, res.code())
                    }
                }
                if (exitoLlamada) {
                    mensajeExito = exito
                    cargar(solicitudActual)
                } else {
                    android.util.Log.e("PROPUESTAS", "Código $code - $err")
                    mensajeError = "Error $code: ${err ?: "no se pudo completar"}"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}
