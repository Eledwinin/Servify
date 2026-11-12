package com.example.servify.ui.modulos.cliente.contratacion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.MiSolicitud
import com.example.servify.data.repository.SolicitudesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MisSolicitudesViewModel(
    private val solicitudesRepository: SolicitudesRepository = SolicitudesRepository()
) : ViewModel() {
    var solicitudes by mutableStateOf<List<MiSolicitud>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // ==========================================
    // ESTADOS PARA CIERRE DE SERVICIO Y CÓDIGO QR
    // ==========================================
    var cargandoCierre by mutableStateOf(false)
    var qrTokenGenerado by mutableStateOf<String?>(null)
    var solicitudIdCierreSeleccionada by mutableStateOf<Int?>(null)

    // ESTADOS PARA REDIRECCIÓN A RESEÑA (CLIENTE)
    var irACalificarCliente by mutableStateOf(false)
    var tecnicoIdParaCalificar by mutableStateOf<Int?>(null)

    fun cargar() {
        val token = SessionManager.token ?: run {
            mensajeError = "Sesión no válida"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val (resultado, errorTexto, codigo) = withContext(Dispatchers.IO) {
                    val res = RetrofitClient.instance.obtenerMisSolicitudes("Bearer $token")
                    if (res.isSuccessful) {
                        Triple(res.body() ?: emptyList(), null, res.code())
                    } else {
                        val err = res.errorBody()?.string()
                        Triple(emptyList<MiSolicitud>(), err, res.code())
                    }
                }
                if (errorTexto == null) {
                    solicitudes = resultado
                } else {
                    Log.e("MIS_SOL", "Código $codigo: $errorTexto")
                    mensajeError = "Error $codigo al cargar tus solicitudes"
                }
            } catch (e: Exception) {
                Log.e("MIS_SOL", "Excepción al cargar solicitudes: ${e.message}", e)
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun iniciarCierreSolicitud(solicitudId: Int, metodoPago: String, tecnicoId: Int? = null) {
        val token = SessionManager.token ?: run {
            mensajeError = "Sesión no válida"
            return
        }

        viewModelScope.launch {
            cargandoCierre = true
            solicitudIdCierreSeleccionada = solicitudId
            tecnicoIdParaCalificar = tecnicoId

            val result = solicitudesRepository.iniciarCierre(
                token = token,
                solicitudId = solicitudId,
                metodoPago = metodoPago,
                tecnicoId = tecnicoId
            )

            cargandoCierre = false
            result.onSuccess { response ->
                qrTokenGenerado = response.qrToken
            }.onFailure { error ->
                Log.e("MIS_SOL", "Error en iniciarCierre: ${error.message}", error)
                mensajeError = error.message ?: "Error al iniciar el cierre"
            }
        }
    }

    fun prepararRedireccionResena(solId: Int, tecnicoId: Int) {
        solicitudIdCierreSeleccionada = solId
        tecnicoIdParaCalificar = if (tecnicoId > 0) tecnicoId else tecnicoIdParaCalificar
        irACalificarCliente = true
    }

    fun resenaClienteNavegada() {
        irACalificarCliente = false
    }

    fun limpiarEstadoCierre() {
        qrTokenGenerado = null
    }
}
