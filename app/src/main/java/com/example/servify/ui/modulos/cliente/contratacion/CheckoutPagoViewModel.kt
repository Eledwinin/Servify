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
    var fechaExpiracion by mutableStateOf("") // Guarda solo números (ej: "1226")
    var cvc by mutableStateOf("")
    var nombreTitular by mutableStateOf("")

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var qrTokenGenerado by mutableStateOf<String?>(null)

    fun procesarPago(solicitudId: Int, onExito: () -> Unit) {
        val digitosTarjeta = numeroTarjeta.replace(" ", "")
        if (digitosTarjeta.length < 16) {
            mensajeError = "Ingresa un número de tarjeta válido (16 dígitos)"
            return
        }

        // Filtra únicamente números por seguridad
        val digitosFecha = fechaExpiracion.filter { it.isDigit() }
        if (digitosFecha.length < 4) {
            mensajeError = "Ingresa la fecha de expiración completa (MM/AA)"
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

        // 🎯 Formatea automáticamente "1226" a "12/26" si la API requiere la barra diagonal
        val fechaFormateada = "${digitosFecha.take(2)}/${digitosFecha.drop(2)}"

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            try {
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