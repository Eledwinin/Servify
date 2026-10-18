package com.example.servify.ui.modulos.modulos_compartidos.messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ConversacionItem
import kotlinx.coroutines.launch

class ConversacionesViewModel : ViewModel() {
    var conversaciones by mutableStateOf<List<ConversacionItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargar() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerConversaciones("Bearer $token")
                if (res.isSuccessful) {
                    conversaciones = res.body() ?: emptyList()
                } else {
                    android.util.Log.e("CONVERSACIONES", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar tus mensajes"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}