package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.util.Patterns
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
        if (mensajeError != null) mensajeError = null
    }

    fun onPasswordChange(nuevaPassword: String) {
        password = nuevaPassword
        if (mensajeError != null) mensajeError = null
    }

    private fun validarCampos(): Boolean {
        val emailTrim = correo.trim()
        if (emailTrim.isEmpty()) {
            mensajeError = "Por favor ingresa tu correo electrónico"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
            mensajeError = "El formato de correo no es válido"
            return false
        }
        if (password.isEmpty()) {
            mensajeError = "Por favor ingresa tu contraseña"
            return false
        }
        if (password.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }

    fun iniciarSesion(onSuccess: (UsuarioModel) -> Unit) {
        if (!validarCampos()) return

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.iniciarSesion(correo.trim(), password)
            cargando = false

            resultado.fold(
                onSuccess = { usuario ->
                    onSuccess(usuario)
                },
                onFailure = { error ->
                    mensajeError = error.message ?: "Ocurrió un error inesperado al conectar"
                }
            )
        }
    }
}