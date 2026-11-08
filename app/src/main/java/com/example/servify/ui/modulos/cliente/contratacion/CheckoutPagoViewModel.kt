package com.example.servify.ui.modulos.cliente.contratacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.repository.SolicitudesRepository
import kotlinx.coroutines.launch

class CheckoutPagoViewModel(
    private val repository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {

    var numeroTarjeta by mutableStateOf("")
    var fechaExpiracion by mutableStateOf("")
    var cvc by mutableStateOf("")
    var nombreTitular by mutableStateOf("")

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var qrTokenGenerado by mutableStateOf<String?>(null)

    fun procesarPago(solicitudId: Int, onExito: () -> Unit) {
        if (numeroTarjeta.replace(" ", "").length < 16) {
            mensajeError = "Ingresa un número de tarjeta válido (16 dígitos)"
            return
        }
        if (fechaExpiracion.length < 5) {
            mensajeError = "Ingresa la fecha de expiración (MM/AA)"
            return
        }
        if (cvc.length < 3) {
            mensajeError = "Ingresa un CVC válido (3 dígitos)"
            return
        }
        if (nombreTitular.isBlank()) {
            mensajeError = "Ingresa el nombre del titular"
            return
        }

        val token = SessionManager.token
        if (token.isNullOrEmpty()) {
            mensajeError = "Sesión no válida"
            return
        }

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            try {
                // Llamada a iniciarCierre coincidiendo exactamente con la firma de tu SolicitudesRepository
                val resultado = repository.iniciarCierre(
                    token = token,
                    solicitudId = solicitudId,
                    metodoPago = "tarjeta",
                    tecnicoId = null,
                    monto = null
                )
                if (resultado.isSuccess) {
                    qrTokenGenerado = resultado.getOrNull()?.qrToken
                    onExito()
                } else {
                    mensajeError = resultado.exceptionOrNull()?.message ?: "Error al procesar el pago"
                }
            } catch (e: Exception) {
                mensajeError = e.localizedMessage ?: "Error de conexión"
            } finally {
                cargando = false
            }
        }
    }
}