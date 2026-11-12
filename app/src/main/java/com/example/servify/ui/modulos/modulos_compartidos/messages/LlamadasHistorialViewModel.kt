package com.example.servify.ui.modulos.modulos_compartidos.messages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.LlamadaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LlamadasHistorialViewModel : ViewModel() {
    var llamadas by mutableStateOf<List<LlamadaItem>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun cargar() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val lista = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerLlamadas("Bearer $token")
                    if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
                }
                llamadas = lista
            } catch (_: Exception) { }
            finally { isLoading = false }
        }
    }
}
