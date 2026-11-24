package com.example.servify.ui.modulos.modulos_compartidos.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.PerfilCliente
import kotlinx.coroutines.launch

class PerfilClienteViewModel : ViewModel() {
    var perfil by mutableStateOf<PerfilCliente?>(null)
    var cargando by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)

    fun cargar(id: Int) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                val r = RetrofitClient.instance.getPerfilCliente("Bearer $token", id)
                if (r.isSuccessful) perfil = r.body()
                else error = Regex("\"mensaje\"\\s*:\\s*\"([^\"]+)\"")
                    .find(r.errorBody()?.string().orEmpty())?.groupValues?.get(1) ?: "Error ${r.code()}"
            } catch (e: Exception) { error = "Error de conexión" }
            finally { cargando = false }
        }
    }
}