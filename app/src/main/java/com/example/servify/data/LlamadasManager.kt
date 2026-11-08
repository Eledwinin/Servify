package com.example.servify.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatConfig
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

data class LlamadaEntrante(val conversacionId: Int, val esVideo: Boolean, val deNombre: String)

object LlamadasManager {
    private var socket: Socket? = null

    var llamadaEntrante by mutableStateOf<LlamadaEntrante?>(null)
    var llamadaCancelada by mutableStateOf(0)
    var respuestaLlamada by mutableStateOf<Boolean?>(null)

    // Almacenamos temporalmente los datos de la llamada activa
    var ultimoTokenGenerado: String? = null
    var ultimoCanalGenerado: String? = null

    fun conectar(token: String) {
        if (socket?.connected() == true) return
        val opts = IO.Options().apply {
            auth = mapOf("token" to token)
            reconnection = true
        }
        val s = IO.socket(ChatConfig.SERVER_URL, opts)

        s.on("llamada_entrante") { args ->
            val j = args.firstOrNull() as? JSONObject ?: return@on
            // Capturamos también el token y el canal si vienen en la llamada entrante
            ultimoTokenGenerado = j.optString("token").takeIf { !it.isNullOrBlank() }
            ultimoCanalGenerado = j.optString("canal").takeIf { !it.isNullOrBlank() }

            llamadaEntrante = LlamadaEntrante(
                j.optInt("conversacionId"), j.optBoolean("esVideo"), j.optString("deNombre")
            )
        }
        // Si el otro cancela la llamada, limpiamos de inmediato la pantalla o diálogo entrante
        s.on("llamada_cancelada") {
            llamadaEntrante = null
            llamadaCancelada++
        }
        s.on("llamada_respondida") { args ->
            val j = args.firstOrNull() as? JSONObject
            respuestaLlamada = j?.optBoolean("aceptada")
        }
        s.connect()
        socket = s
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

                ultimoCanalGenerado = canal
                ultimoTokenGenerado = token

                onTokenRecibido?.invoke(canal, token)
            }
        })
    }

    fun responder(conversacionId: Int, aceptar: Boolean) {
        socket?.emit("responder_llamada", JSONObject().put("conversacionId", conversacionId).put("aceptar", aceptar))
        llamadaEntrante = null
    }

    fun cancelar(conversacionId: Int) {
        socket?.emit("cancelar_llamada", JSONObject().put("conversacionId", conversacionId))
        llamadaEntrante = null // Limpiar localmente también
    }

    fun desconectar() {
        socket?.off()
        socket?.disconnect()
        socket = null
        llamadaEntrante = null
    }

    fun finalizar(conversacionId: Int) {
        socket?.emit("finalizar_llamada", JSONObject().put("conversacionId", conversacionId))
    }
}