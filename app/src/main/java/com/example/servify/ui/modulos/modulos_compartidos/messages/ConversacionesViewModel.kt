package com.example.servify.ui.modulos.modulos_compartidos.messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ConversacionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversacionesViewModel : ViewModel() {
    var conversaciones by mutableStateOf<List<ConversacionItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargar() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val (lista, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerConversaciones("Bearer $token")
                    if (res.isSuccessful) {
                        Triple(res.body() ?: emptyList(), null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(emptyList<ConversacionItem>(), e, res.code())
                    }
                }
                if (err == null) {
                    conversaciones = lista
                } else {
                    android.util.Log.e("CONVERSACIONES", "Código $code - $err")
                    mensajeError = "Error $code al cargar tus mensajes"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
