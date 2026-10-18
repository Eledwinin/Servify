package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.util.Log
import com.example.servify.data.model.Mensaje
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

object ChatConfig {
    const val SERVER_URL = "https://servify-backend-45ae.onrender.com/"
}

class ChatSocket(private val token: String) {
    private var socket: Socket? = null
    private val gson = Gson()

    var onConectado: (() -> Unit)? = null
    var onMensaje: ((Mensaje) -> Unit)? = null

    fun conectar() {
        val opts = IO.Options().apply {
            auth = mapOf("token" to token)
            reconnection = true
        }
        val s = IO.socket(ChatConfig.SERVER_URL, opts)

        s.on(Socket.EVENT_CONNECT) { onConectado?.invoke() }
        s.on(Socket.EVENT_CONNECT_ERROR) { args -> Log.e("CHAT_SOCKET", "Error: ${args.firstOrNull()}") }
        s.on("mensaje_nuevo") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            onMensaje?.invoke(gson.fromJson(json.toString(), Mensaje::class.java))
        }

        s.connect()
        socket = s
    }

    fun unirse(conversacionId: Int, resultado: (Boolean) -> Unit) {
        socket?.emit("unirse", conversacionId, Ack { args ->
            val ok = (args.firstOrNull() as? JSONObject)?.optBoolean("ok") ?: false
            resultado(ok)
        })
    }

    fun enviar(conversacionId: Int, texto: String, resultado: (Boolean, String?) -> Unit) {
        val datos = JSONObject().put("conversacionId", conversacionId).put("contenido", texto)
        socket?.emit("enviar_mensaje", datos, Ack { args ->
            val r = args.firstOrNull() as? JSONObject
            resultado(r?.optBoolean("ok") ?: false, r?.optString("error"))
        }) ?: resultado(false, "Sin conexión")
    }

    fun desconectar() {
        socket?.off()
        socket?.disconnect()
        socket = null
    }
}