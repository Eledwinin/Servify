package com.example.servify.ui.modulos.modulos_compartidos.actividad

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.MiPostulacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActividadViewModel : ViewModel() {
    var postulaciones by mutableStateOf<List<MiPostulacion>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargarPostulaciones(context: Context? = null) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val (lista, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerMisPostulaciones("Bearer $token")
                    if (res.isSuccessful) {
                        Triple(res.body() ?: emptyList(), null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(emptyList<MiPostulacion>(), e, res.code())
                    }
                }
                if (err == null) {
                    postulaciones = lista
                } else {
                    Log.e("ACTIVIDAD", "Código $code - $err")
                    mensajeError = "Error $code al cargar tus postulaciones"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
