package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.Mensaje
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel : ViewModel() {
    val mensajes = mutableStateListOf<Mensaje>()
    var activa by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var miId by mutableStateOf(0)
        private set

    private var socket: ChatSocket? = null
    private var conversacionId = 0

    fun iniciar(id: Int) {
        if (conversacionId == id) return
        conversacionId = id
        val token = SessionManager.token ?: return
        miId = idDesdeToken(token)

        viewModelScope.launch {
            isLoading = true
            sincronizar()
            isLoading = false
        }

        val s = ChatSocket(token)
        s.onConectado = {
            s.unirse(id) { ok ->
                if (ok) viewModelScope.launch { sincronizar() }
            }
        }
        s.onMensaje = { m ->
            viewModelScope.launch {
                agregar(m)
                if (m.emisorId != miId) marcarLeidos()
            }
        }
        s.conectar()
        socket = s
    }

    // Trae el historial (o solo lo que falta después de reconectar)
    private suspend fun sincronizar() {
        val token = SessionManager.token ?: return
        try {
            val ultimo = mensajes.lastOrNull()?.id
            val res = RetrofitClient.instance.obtenerMensajes("Bearer $token", conversacionId, ultimo)
            if (res.isSuccessful) {
                val body = res.body()
                activa = body?.activa ?: true
                body?.mensajes?.forEach { agregar(it) }
                marcarLeidos()
            } else {
                android.util.Log.e("CHAT", "Código ${res.code()} - ${res.errorBody()?.string()}")
                mensajeError = "Error ${res.code()} al cargar el chat"
            }
        } catch (e: Exception) {
            mensajeError = "Error de conexión: ${e.message}"
        }
    }

    private suspend fun marcarLeidos() {
        val token = SessionManager.token ?: return
        try {
            RetrofitClient.instance.marcarLeidos("Bearer $token", conversacionId)
        } catch (_: Exception) { }
    }

    private fun agregar(m: Mensaje) {
        if (mensajes.none { it.id == m.id }) mensajes.add(m)
    }

    fun enviar(texto: String) {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return
        socket?.enviar(conversacionId, limpio) { ok, error ->
            if (!ok) viewModelScope.launch { mensajeError = error ?: "No se pudo enviar" }
        } ?: run { mensajeError = "Sin conexión con el chat" }
    }

    // El id del usuario viene dentro del JWT (payload.id)
    private fun idDesdeToken(token: String): Int = try {
        val payload = token.split(".")[1]
        val json = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
        JSONObject(json).optInt("id")
    } catch (e: Exception) { 0 }

    override fun onCleared() {
        socket?.desconectar()
        super.onCleared()
    }
}