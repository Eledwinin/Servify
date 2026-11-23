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
import com.example.servify.data.model.GananciasResumen
import com.example.servify.data.model.SolicitudItemTecnico
import com.example.servify.data.repository.SolicitudesRepository
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    var ganancias by mutableStateOf(GananciasResumen())
        private set

    var totalResenasTecnico by mutableStateOf(0)

    fun cargarSolicitudes() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                val (listaPub, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerSolicitudes(
                        "Bearer $token", SessionManager.latitud, SessionManager.longitud
                    )
                    if (res.isSuccessful) {
                        Triple(res.body() ?: emptyList(), null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(emptyList<SolicitudItemTecnico>(), e, res.code())
                    }
                }
                if (err != null) {
                    Log.e("MURO", "Código $code: $err")
                    mensajeError = "Error $code al cargar solicitudes"
                } else {
                    solicitudes = listaPub
                }

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

    fun cargarGanancias() {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            try {
                val r = RetrofitClient.instance.obtenerGanancias("Bearer $token")
                if (r.isSuccessful) {
                    ganancias = r.body() ?: GananciasResumen()
                } else {
                    Log.e("GANANCIAS", "Código ${r.code()} - ${r.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GANANCIAS", "Error: ${e.message}")
            }
        }
    }

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
                tecnicoId = 0
            )
            cargandoValidacionQr = false

            result.onSuccess { response ->
                mensajeExito = response.mensaje ?: "¡Servicio completado y cerrado exitosamente!"
                cargarSolicitudes()
                cargarGanancias()

                val solId = response.solicitudId ?: 0
                val cliId = response.clienteId ?: 0

                delay(5000)

                solicitudIdParaCalificar = solId
                clienteIdParaCalificar = cliId
                irACalificar = true

            }.onFailure { error ->
                Log.e("VALIDAR_QR", "Error al validar QR: ${error.message}", error)
                mensajeError = error.message ?: "Código QR inválido o expirado"
            }
        }
    }

    fun resenaNavegada() {
        irACalificar = false
        solicitudIdParaCalificar = null
        clienteIdParaCalificar = null
    }

    fun responderContratacion(idContratacion: Int, estado: String, motivo: String? = null) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            val resultado = solicitudesRepository.responderContratacionDirecta(
                token = token,
                idContratacion = idContratacion,
                estado = estado,
                motivo = motivo
            )
            resultado.onSuccess {
                if (estado == "aceptado") {
                    mensajeExito = "¡Solicitud aceptada!"
                    contratacionesDirectas = contratacionesDirectas.map { item ->
                        if (item.id == idContratacion) item.copy(estado = estado) else item
                    }
                } else {
                    mensajeExito = "Solicitud rechazada y notificada al cliente"
                    contratacionesDirectas = contratacionesDirectas.filter { it.id != idContratacion }
                }
            }.onFailure { err ->
                mensajeError = err.localizedMessage
            }
        }
    }

    fun ignorar(id: Int) {
        solicitudes = solicitudes.filter { it.id != id }
    }

    fun postular(
        solicitudId: Int,
        precio: Double,
        tiempo: String,
        mensaje: String,
        aceptaPresupuesto: Boolean = false
    ) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            try {
                val (ok, err, code) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.crearPostulacion(
                        "Bearer $token",
                        CrearPostulacionRequest(solicitudId, precio, tiempo, mensaje, aceptaPresupuesto)
                    )
                    if (res.isSuccessful) {
                        Triple(true, null, res.code())
                    } else {
                        val e = res.errorBody()?.string()
                        Triple(false, e, res.code())
                    }
                }
                if (ok) {
                    mensajeExito = "Postulación enviada"
                    ignorar(solicitudId)
                } else {
                    Log.e("POSTULACION", "Código $code: $err")
                    mensajeError = if (code == 409) "Ya te postulaste a esta solicitud"
                    else "Error $code al postularte"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            }
        }
    }
}