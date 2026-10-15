package com.example.servify.ui.modulos.modulos_compartidos.actividad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.MiPostulacion
import kotlinx.coroutines.launch

class ActividadViewModel : ViewModel() {
    var postulaciones by mutableStateOf<List<MiPostulacion>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargarPostulaciones() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerMisPostulaciones("Bearer $token")
                if (res.isSuccessful) {
                    postulaciones = res.body() ?: emptyList()
                } else {
                    android.util.Log.e("ACTIVIDAD", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar tus postulaciones"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}