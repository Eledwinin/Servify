package com.example.servify.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestor centralizado de presencia y actividad en tiempo real para usuarios de Servify.
 * Opera 100% en el cliente Android sin requerir modificaciones en el backend:
 * - Valida la vigencia de la sesión mediante [SessionManager.estaAutenticado].
 * - Monitorea eventos en tiempo real de Socket.IO (mensajes entrantes, llamadas, ofertas, eventos de presencia).
 * - Evalúa heurísticas de actividad reciente con marcas temporales (ISO 8601 UTC).
 */
object PresenciaManager {
    // Ventana máxima en milisegundos para considerar a un usuario activo/en línea por mensaje reciente (5 minutos)
    const val VENTANA_ACTIVIDAD_MS = 5 * 60 * 1000L

    // Estados de presencia conocidos por usuarioId: true = "En línea", false = "No está en línea"
    private val _estadosUsuarios = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val estadosUsuarios: StateFlow<Map<Int, Boolean>> = _estadosUsuarios.asStateFlow()

    // Marca temporal de última interacción registrada por usuarioId
    private val _ultimaActividad = ConcurrentHashMap<Int, Long>()

    /**
     * Registra actividad reciente (ej. mensaje recibido, llamada, oferta) para el usuario dado.
     */
    fun registrarActividad(usuarioId: Int, timestampMs: Long = System.currentTimeMillis()) {
        if (usuarioId <= 0) return
        _ultimaActividad[usuarioId] = timestampMs
        _estadosUsuarios.value += (usuarioId to true)
    }

    /**
     * Actualiza explícitamente el estado de un usuario (ej. evento de conexión/desconexión recibido por Socket).
     */
    fun actualizarEstado(usuarioId: Int, enLinea: Boolean) {
        if (usuarioId <= 0) return
        if (enLinea) {
            _ultimaActividad[usuarioId] = System.currentTimeMillis()
        }
        _estadosUsuarios.value += (usuarioId to enLinea)
    }

    /**
     * Determina si un usuario se encuentra actualmente "En línea" o "No está en línea":
     * 1. Valida que el usuario local cuente con sesión válida y autenticada en [SessionManager].
     * 2. Revisa si hay un estado explícito o actividad reciente dentro de la ventana de tiempo.
     * 3. Si se proporciona [ultimoMensajeEnIso], calcula la diferencia respecto a UTC.
     */
    fun estaEnLinea(usuarioId: Int, ultimoMensajeEnIso: String? = null): Boolean {
        // Si el usuario actual no está autenticado o su token expiró, no se computa presencia activa
        if (!SessionManager.estaAutenticado() || (usuarioId <= 0)) return false

        // 1. Estado explícito registrado
        val estadoActual = _estadosUsuarios.value[usuarioId]
        if (estadoActual == false) return false
        if (estadoActual == true) {
            val ultima = _ultimaActividad[usuarioId]
            if (ultima != null && (System.currentTimeMillis() - ultima) <= VENTANA_ACTIVIDAD_MS) {
                return true
            }
        }

        // 2. Actividad reciente registrada en memoria
        val ultima = _ultimaActividad[usuarioId]
        if (ultima != null && (System.currentTimeMillis() - ultima) <= VENTANA_ACTIVIDAD_MS) {
            return true
        }

        // 3. Heurística basada en el timestamp del último mensaje de la conversación (ISO 8601 UTC)
        val fechaMs = parsearIsoFechaUtc(ultimoMensajeEnIso)
        if (fechaMs != null) {
            val diferencia = System.currentTimeMillis() - fechaMs
            // Permite un margen de 2 minutos hacia el futuro en caso de desfases leves entre el reloj del dispositivo y el servidor
            if (diferencia in -120_000L..VENTANA_ACTIVIDAD_MS) {
                _ultimaActividad[usuarioId] = fechaMs
                _estadosUsuarios.value = _estadosUsuarios.value + (usuarioId to true)
                return true
            }
        }

        return false
    }

    /**
     * Parsea fechas ISO 8601 (UTC) de manera segura y resiliente.
     */
    fun parsearIsoFechaUtc(iso: String?): Long? {
        if (iso.isNullOrBlank() || iso.length < 19) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.parse(iso.take(19))?.time
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Limpia los estados almacenados al cerrar sesión.
     */
    fun limpiar() {
        _estadosUsuarios.value = emptyMap()
        _ultimaActividad.clear()
    }
}
