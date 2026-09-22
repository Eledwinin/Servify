package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RecuperarPasswordViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var pasoActual by mutableIntStateOf(1)
        private set

    var cambioExitoso by mutableStateOf(false)
        private set

    var correo by mutableStateOf("")
        private set

    var codigoOtp by mutableStateOf("")
        private set

    var nuevaPassword by mutableStateOf("")
        private set

    var confirmarPassword by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun onCorreoChange(nuevoCorreo: String) {
        correo = nuevoCorreo
        mensajeError = null
    }

    fun onCodigoOtpChange(nuevoCodigo: String) {
        if (nuevoCodigo.length <= 6) {
            codigoOtp = nuevoCodigo
            mensajeError = null
        }
    }

    fun onNuevaPasswordChange(nueva: String) {
        nuevaPassword = nueva
        mensajeError = null
    }

    fun onConfirmarPasswordChange(confirmacion: String) {
        confirmarPassword = confirmacion
        mensajeError = null
    }

    fun retrocederPaso(onVolverALogin: () -> Unit) {
        if (pasoActual > 1) {
            pasoActual--
            mensajeError = null
        } else {
            onVolverALogin()
        }
    }

    fun solicitarCodigo() {
        val emailTrim = correo.trim()
        if (emailTrim.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
            mensajeError = "Ingresa un correo electrónico válido"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.solicitarRecuperacion(emailTrim)
            cargando = false

            resultado.fold(
                onSuccess = {
                    pasoActual = 2
                },
                onFailure = { error ->
                    mensajeError = error.message ?: "No se pudo enviar el código. Intenta de nuevo"
                }
            )
        }
    }

    fun verificarCodigoPaso() {
        val codigoTrim = codigoOtp.trim()
        if (codigoTrim.length != 6) {
            mensajeError = "El código debe tener 6 dígitos"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.verificarCodigoOtp(
                correo = correo.trim(),
                codigo = codigoTrim
            )
            cargando = false

            resultado.fold(
                onSuccess = {
                    pasoActual = 3
                },
                onFailure = {
                    mensajeError = "El código ingresado es incorrecto"
                }
            )
        }
    }

    fun restablecerPassword(onExito: () -> Unit) {
        val pass1 = nuevaPassword.trim()
        val pass2 = confirmarPassword.trim()

        if (pass1.isEmpty() || pass2.isEmpty()) {
            mensajeError = "Por favor completa ambos campos de contraseña"
            return
        }

        if (pass1.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (pass1 != pass2) {
            mensajeError = "Las contraseñas no coinciden. Verifícalas bien"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.cambiarPassword(
                correo = correo.trim(),
                codigo = codigoOtp.trim(),
                nuevaPassword = pass1
            )
            cargando = false

            resultado.fold(
                onSuccess = {
                    cambioExitoso = true
                    onExito()
                },
                onFailure = { error ->
                    val detalle = error.message ?: ""
                    mensajeError = when {
                        detalle.contains("código", ignoreCase = true) || detalle.contains("codigo", ignoreCase = true) ->
                            "El código ingresado no es válido"
                        detalle.contains("conexión", ignoreCase = true) || detalle.contains("conexion", ignoreCase = true) ->
                            "Error de red. Revisa tu conexión a internet"
                        else ->
                            "No se pudo actualizar la contraseña. Intenta nuevamente"
                    }
                }
            )
        }
    }
}