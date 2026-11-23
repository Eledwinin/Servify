package com.example.servify.ui.modulos.trabajador.agenda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.AgendaItemResponse
import com.example.servify.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgendaTecnicoViewModel : ViewModel() {
    var itemsAgenda by mutableStateOf<List<AgendaItemResponse>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var errorMensaje by mutableStateOf<String?>(null)
        private set

    init {
        cargarAgenda()
    }

    fun cargarAgenda() {
        val token = SessionManager.token
        if (token.isNullOrBlank()) {
            errorMensaje = "Sesión no válida"
            return
        }

        viewModelScope.launch {
            cargando = true
            errorMensaje = null
            try {
                val res = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.obtenerAgendaTecnico("Bearer $token")
                }
                if (res.isSuccessful) {
                    itemsAgenda = res.body().orEmpty()
                } else {
                    errorMensaje = "No se pudo cargar la agenda (${res.code()})"
                }
            } catch (e: Exception) {
                errorMensaje = "Error de conexión: ${e.localizedMessage ?: "Intenta de nuevo"}"
            } finally {
                cargando = false
            }
        }
    }
}