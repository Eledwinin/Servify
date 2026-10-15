package com.example.servify.ui.modulos.cliente.contratacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.MiSolicitud
import kotlinx.coroutines.launch

class MisSolicitudesViewModel : ViewModel() {
    var solicitudes by mutableStateOf<List<MiSolicitud>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargar() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerMisSolicitudes("Bearer $token")
                if (res.isSuccessful) {
                    solicitudes = res.body() ?: emptyList()
                } else {
                    android.util.Log.e("MIS_SOL", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar tus solicitudes"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}