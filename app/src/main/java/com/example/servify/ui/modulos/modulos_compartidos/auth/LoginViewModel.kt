package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.model.UsuarioModel
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun onCorreoChange(nuevoCorreo: String) {
        correo = nuevoCorreo
        mensajeError = null
    }

    fun onPasswordChange(nuevaPassword: String) {
        password = nuevaPassword
        mensajeError = null
    }

    fun iniciarSesion(onSuccess: (UsuarioModel) -> Unit) {
        if (correo.isBlank() || password.isBlank()) {
            mensajeError = "Correo y contraseña son requeridos"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.iniciarSesion(correo, password)
            cargando = false

            resultado.fold(
                onSuccess = { usuario ->
                    onSuccess(usuario)
                },
                onFailure = { error ->
                    mensajeError = error.message ?: "Error desconocido"
                }
            )
        }
    }
}