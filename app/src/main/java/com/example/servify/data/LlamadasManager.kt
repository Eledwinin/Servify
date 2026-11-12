package com.example.servify.data

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatConfig
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

data class LlamadaEntrante(val conversacionId: Int, val esVideo: Boolean, val deNombre: String)

object LlamadasManager {
    private var socket: Socket? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    var llamadaEntrante by mutableStateOf<LlamadaEntrante?>(null)
    var llamadaCancelada by mutableStateOf(0)
    var respuestaLlamada by mutableStateOf<Boolean?>(null)

    // Almacenamos temporalmente los datos de la llamada activa
    var ultimoTokenGenerado: String? = null
    var ultimoCanalGenerado: String? = null

    fun conectar(token: String) {
        if (socket?.connected() == true) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val opts = IO.Options().apply {
                    auth = mapOf("token" to token)
                    reconnection = true
                }
                val s = IO.socket(ChatConfig.SERVER_URL, opts)

                // 🔔 1. Evento de Llamada Entrante
                s.on("llamada_entrante") { args ->
                    val j = args.firstOrNull() as? JSONObject ?: return@on
                    val tokenGenerado = j.optString("token").takeIf { !it.isNullOrBlank() }
                    val canalGenerado = j.optString("canal").takeIf { !it.isNullOrBlank() }
                    val convId = j.optInt("conversacionId")
                    val esVideo = j.optBoolean("esVideo")
                    val deNombre = j.optString("deNombre")
                    val entrante = LlamadaEntrante(convId, esVideo, deNombre)

                    mainHandler.post {
                        ultimoTokenGenerado = tokenGenerado
                        ultimoCanalGenerado = canalGenerado
                        llamadaEntrante = entrante

                        // Notificación nativa de alta prioridad
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_LLAMADA_ENTRANTE,
                            titulo = if (esVideo) "Videollamada entrante 📹" else "Llamada entrante 📞",
                            mensaje = "$deNombre te está llamando",
                            extraId = convId,
                            extraStr = canalGenerado ?: "chat_$convId"
                        )
                    }
                }

                // Si el otro cancela la llamada, limpiamos de inmediato en el hilo principal
                s.on("llamada_cancelada") {
                    mainHandler.post {
                        llamadaEntrante = null
                        llamadaCancelada++
                    }
                }

                s.on("llamada_respondida") { args ->
                    val j = args.firstOrNull() as? JSONObject
                    val resp = j?.optBoolean("aceptada")
                    mainHandler.post {
                        respuestaLlamada = resp
                    }
                }

                // 🔔 2. Evento: Postulación / Propuesta Aceptada (en tiempo real)
                val onPropuestaAceptada = io.socket.emitter.Emitter.Listener { args ->
                    val j = args.firstOrNull() as? JSONObject
                    val titulo = j?.optString("titulo")?.takeIf { it.isNotBlank() } ?: "¡Propuesta Aceptada! 🎉"
                    val mensaje = j?.optString("mensaje")?.takeIf { it.isNotBlank() } ?: "Un cliente ha aceptado tu propuesta."
                    val solicitudId = j?.optInt("solicitudId") ?: 0

                    mainHandler.post {
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_POSTULACION_ACEPTADA,
                            titulo = titulo,
                            mensaje = mensaje,
                            extraId = solicitudId
                        )
                    }
                }
                s.on("postulacion_aceptada", onPropuestaAceptada)
                s.on("propuesta_aceptada", onPropuestaAceptada)
                s.on("propuesta_aceptada_evento", onPropuestaAceptada)

                // 🔔 3. Evento: Técnico en Camino
                s.on("tecnico_en_camino") { args ->
                    val j = args.firstOrNull() as? JSONObject
                    val titulo = j?.optString("titulo")?.takeIf { it.isNotBlank() } ?: "Técnico en camino 🚗"
                    val mensaje = j?.optString("mensaje")?.takeIf { it.isNotBlank() } ?: "El técnico va en camino hacia tu ubicación."
                    val solicitudId = j?.optInt("solicitudId") ?: 0

                    mainHandler.post {
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_TECNICO_EN_CAMINO,
                            titulo = titulo,
                            mensaje = mensaje,
                            extraId = solicitudId
                        )
                    }
                }

                // 🔔 4. Evento: Nueva Cotización / Propuesta
                s.on("nueva_cotizacion") { args ->
                    val j = args.firstOrNull() as? JSONObject
                    val titulo = j?.optString("titulo")?.takeIf { it.isNotBlank() } ?: "Nueva Cotización 💼"
                    val mensaje = j?.optString("mensaje")?.takeIf { it.isNotBlank() } ?: "Has recibido una nueva cotización para tu solicitud."
                    val solicitudId = j?.optInt("solicitudId") ?: 0

                    mainHandler.post {
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_NUEVA_COTIZACION,
                            titulo = titulo,
                            mensaje = mensaje,
                            extraId = solicitudId
                        )
                    }
                }

                // 🔔 5. Evento: Pago Confirmado
                s.on("pago_confirmado") { args ->
                    val j = args.firstOrNull() as? JSONObject
                    val titulo = j?.optString("titulo")?.takeIf { it.isNotBlank() } ?: "Pago Confirmado ✅"
                    val mensaje = j?.optString("mensaje")?.takeIf { it.isNotBlank() } ?: "El pago del servicio ha sido completado y verificado."
                    val solicitudId = j?.optInt("solicitudId") ?: 0

                    mainHandler.post {
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_PAGO_CONFIRMADO,
                            titulo = titulo,
                            mensaje = mensaje,
                            extraId = solicitudId
                        )
                    }
                }

                // 🔔 6. Evento: Mensaje Nuevo en Chat
                s.on("mensaje_nuevo") { args ->
                    val j = args.firstOrNull() as? JSONObject ?: return@on
                    val deNombre = j.optString("deNombre").takeIf { !it.isNullOrBlank() } ?: "Servify Chat"
                    val contenido = j.optString("contenido").takeIf { !it.isNullOrBlank() } ?: "Nuevo mensaje recibido"
                    val convId = j.optInt("conversacionId")

                    mainHandler.post {
                        ServifyNotificationHelper.procesarEventoSocket(
                            tipo = ServifyNotificationHelper.EVENTO_MENSAJE_NUEVO,
                            titulo = deNombre,
                            mensaje = contenido,
                            extraId = convId,
                            extraStr = deNombre
                        )
                    }
                }

                s.connect()
                socket = s
            } catch (e: Exception) {
                android.util.Log.e("LLAMADAS_SOCKET", "Error conectando socket de llamadas: ${e.message}")
            }
        }
    }

    fun llamar(conversacionId: Int, esVideo: Boolean, onTokenRecibido: ((canal: String, token: String) -> Unit)? = null) {
        val data = JSONObject()
            .put("conversacionId", conversacionId)
            .put("esVideo", esVideo)

        socket?.emit("llamar", data, io.socket.client.Ack { args ->
            val res = args.firstOrNull() as? JSONObject ?: return@Ack
            if (res.optBoolean("ok")) {
                val canal = res.optString("canal")
                val token = res.optString("token")

                mainHandler.post {
                    ultimoCanalGenerado = canal
                    ultimoTokenGenerado = token
                    onTokenRecibido?.invoke(canal, token)
                }
            }
        })
    }

    fun responder(conversacionId: Int, aceptar: Boolean) {
        socket?.emit("responder_llamada", JSONObject().put("conversacionId", conversacionId).put("aceptar", aceptar))
        mainHandler.post {
            llamadaEntrante = null
        }
    }

    fun cancelar(conversacionId: Int) {
        socket?.emit("cancelar_llamada", JSONObject().put("conversacionId", conversacionId))
        mainHandler.post {
            llamadaEntrante = null
        }
    }

    fun desconectar() {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.off()
            socket?.disconnect()
            socket = null
            mainHandler.post {
                llamadaEntrante = null
            }
        }
    }

    fun finalizar(conversacionId: Int) {
        socket?.emit("finalizar_llamada", JSONObject().put("conversacionId", conversacionId))
    }
}
