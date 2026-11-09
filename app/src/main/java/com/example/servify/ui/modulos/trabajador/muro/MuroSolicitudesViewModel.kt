package com.example.servify.ui.modulos.trabajador.muro

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.ContratacionDirectaResponse
import com.example.servify.data.model.CrearPostulacionRequest
import com.example.servify.data.model.SolicitudItemTecnico
import com.example.servify.data.repository.SolicitudesRepository
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MuroSolicitudesViewModel(
    private val solicitudesRepository: SolicitudesRepository = SolicitudesRepository(),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    var solicitudes by mutableStateOf<List<SolicitudItemTecnico>>(emptyList())
    var contratacionesDirectas by mutableStateOf<List<ContratacionDirectaResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var cargandoValidacionQr by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf<String?>(null)

    // ⏱️ ESTADOS PARA LA REDIRECCIÓN A RESEÑA (5 SEGUNDOS)
    var irACalificar by mutableStateOf(false)
    var solicitudIdParaCalificar by mutableStateOf<Int?>(null)
    var clienteIdParaCalificar by mutableStateOf<Int?>(null)

    var gananciasSemana by mutableStateOf("0.00")
    var totalTrabajos by mutableStateOf(0)

    var totalResenasTecnico by mutableStateOf(0)

    fun cargarSolicitudes() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                // 1. Cargar solicitudes públicas
                val res = RetrofitClient.instance.obtenerSolicitudes(
                    "Bearer $token", SessionManager.latitud, SessionManager.longitud
                )
                if (res.isSuccessful) {
                    solicitudes = res.body() ?: emptyList()
                } else {
                    val err = res.errorBody()?.string()
                    Log.e("MURO", "Código ${res.code()}: $err")
                    mensajeError = "Error ${res.code()} al cargar solicitudes"
                }

                // 2. Cargar solicitudes directas (Sobrecito ✉️)
                val resDirectas = solicitudesRepository.obtenerContratacionesTecnico(token)
                resDirectas.onSuccess { lista ->
                    contratacionesDirectas = lista
                }

                cargarCantidadResenas()

            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    private fun cargarCantidadResenas() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            val resultado = usuarioRepository.obtenerResenasUsuario(token)
            resultado.onSuccess { listaResenas ->
                totalResenasTecnico = listaResenas.size
            }.onFailure { err ->
                Log.e("MURO_RESENAS", "Error al cargar reseñas: ${err.message}")
            }
        }
    }

    // ==========================================
    // VALIDAR QR DEL CLIENTE (TÉCNICO / TRABAJADOR)
    // ==========================================
    fun validarQrCliente(qrToken: String) {
        val token = SessionManager.token ?: run {
            mensajeError = "Sesión no válida"
            return
        }

        viewModelScope.launch {
            cargandoValidacionQr = true
            val result = solicitudesRepository.validarQr(
                token = token,
                qrToken = qrToken,
                tecnicoId = 0 // El backend auto-identifica al técnico mediante el token Bearer
            )
            cargandoValidacionQr = false

            result.onSuccess { response ->
                // 1. Mensaje de éxito inmediato
                mensajeExito = response.mensaje ?: "¡Servicio completado y cerrado exitosamente!"
                cargarSolicitudes() // Recarga la lista de solicitudes del muro

                val solId = response.solicitudId ?: 0
                val cliId = response.clienteId ?: 0

                // 2. Espera rápida de 5 SEGUNDOS (5,000 ms)
                delay(5000)

                // 3. Activa la bandera para que la pantalla navegue a la reseña
                solicitudIdParaCalificar = solId
                clienteIdParaCalificar = cliId
                irACalificar = true

            }.onFailure { error ->
                Log.e("VALIDAR_QR", "Error al validar QR: ${error.message}", error)
                mensajeError = error.message ?: "Código QR inválido o expirado"
            }
        }
    }

    // Limpia el evento de navegación tras redirigir
    fun resenaNavegada() {
        irACalificar = false
        solicitudIdParaCalificar = null
        clienteIdParaCalificar = null
    }

    fun responderContratacion(idContratacion: Int, estado: String) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            val resultado = solicitudesRepository.responderContratacionDirecta(token, idContratacion, estado)
            resultado.onSuccess {
                mensajeExito = if (estado == "aceptado") "¡Solicitud aceptada!" else "Solicitud rechazada"

                contratacionesDirectas = contratacionesDirectas.map { item ->
                    if (item.id == idContratacion) item.copy(estado = estado) else item
                }
            }.onFailure { err ->
                mensajeError = err.localizedMessage
            }
        }
    }

    // Quitar la contratación directa de la lista local al presionar la papelera 🗑️
    fun eliminarContratacionDirectaLocal(id: Int) {
        contratacionesDirectas = contratacionesDirectas.filter { it.id != id }
    }

    fun ignorar(id: Int) {
        solicitudes = solicitudes.filter { it.id != id }
    }

    fun postular(solicitudId: Int, precio: Double, tiempo: String, mensaje: String) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.crearPostulacion(
                    "Bearer $token",
                    CrearPostulacionRequest(solicitudId, precio, tiempo, mensaje)
                )
                if (res.isSuccessful) {
                    mensajeExito = "Postulación enviada"
                    ignorar(solicitudId)
                } else {
                    val err = res.errorBody()?.string()
                    Log.e("POSTULACION", "Código ${res.code()}: $err")
                    mensajeError = if (res.code() == 409) "Ya te postulaste a esta solicitud"
                    else "Error ${res.code()} al postularte"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}