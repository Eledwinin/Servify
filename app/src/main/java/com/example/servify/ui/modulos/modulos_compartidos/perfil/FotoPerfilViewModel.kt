package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class FotoPerfilViewModel(
    private val repo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {
    var subiendo by mutableStateOf(false)
    var listo by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun subir(context: Context, uri: Uri) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            subiendo = true
            error = null
            repo.subirFotoPerfil(context, token, uri)
                .onSuccess { url ->
                    SessionManager.usuarioActual?.let {
                        SessionManager.guardarSesion(token, it.copy(fotoUrl = url))
                    }
                    listo = true
                }
                .onFailure { error = it.message }
            subiendo = false
        }
    }
}