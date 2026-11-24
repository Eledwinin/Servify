package com.example.servify.ui.modulos.modulos_compartidos.messages

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.TextureView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.BuildConfig
import com.example.servify.data.LlamadasManager
import com.example.servify.data.SessionManager
import com.example.servify.data.agora.AgoraManager
import com.example.servify.data.api.RetrofitClient
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CallViewModel(app: Application) : AndroidViewModel(app) {

    enum class Fase { CONECTANDO, LLAMANDO, EN_LLAMADA }

    sealed interface Evento {
        class Aviso(val texto: String) : Evento
        object Cerrar : Evento
    }

    var fase by mutableStateOf(Fase.CONECTANDO)
        private set
    var segundos by mutableIntStateOf(0)
        private set
    var uidRemoto by mutableStateOf<Int?>(null)
        private set
    var remotoConVideo by mutableStateOf(false)
        private set
    var microApagado by mutableStateOf(false)
        private set
    var camaraEncendida by mutableStateOf(false)
        private set
    var altavozActivo by mutableStateOf(false)
        private set
    var motorListo by mutableStateOf(false)
        private set

    private val _eventos = Channel<Evento>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    private var agora: AgoraManager? = null
    private var conversacionId = 0
    private var esVideo = false
    private var iniciado = false
    private var uniendose = false
    private var terminado = false            // solo se toca en el hilo principal
    private var jobTemporizador: Job? = null
    private var jobEspera: Job? = null

    private fun enMain(bloque: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main.immediate) { bloque() }
    }

    /** Crea el motor y deja el video preparado ANTES de que exista cualquier vista. */
    fun iniciar(conversacionId: Int, esVideo: Boolean) {
        if (iniciado) return
        iniciado = true
        this.conversacionId = conversacionId
        this.esVideo = esVideo
        altavozActivo = esVideo
        LlamadasManager.respuestaLlamada = null

        val handler = object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) = enMain {
                if (fase == Fase.CONECTANDO) fase = Fase.LLAMANDO
            }

            override fun onUserJoined(uid: Int, elapsed: Int) = enMain { alConectarOtro(uid) }

            override fun onUserOffline(uid: Int, reason: Int) = enMain {
                if (fase == Fase.EN_LLAMADA) terminar(avisarServidor = true, aviso = "La llamada terminó")
            }

            override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) = enMain {
                if (uid == uidRemoto) {
                    remotoConVideo = state == Constants.REMOTE_VIDEO_STATE_DECODING ||
                            state == Constants.REMOTE_VIDEO_STATE_FROZEN
                }
            }

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                Log.d("AGORA", "conexión: estado=$state razón=$reason")
                if (state == Constants.CONNECTION_STATE_FAILED) {
                    enMain { terminar(avisarServidor = true, aviso = "No se pudo conectar la llamada") }
                }
            }

            override fun onError(err: Int) { Log.e("AGORA", "onError: $err") }
        }

        agora = AgoraManager(getApplication(), BuildConfig.AGORA_APP_ID, handler).also {
            if (esVideo) it.prepararVideo()
            it.configurarAltavoz(esVideo)
        }
        motorListo = true
    }

    /** Pide el token y entra al canal. Se llama una sola vez, cuando ya hay permiso de micrófono. */
    fun unirse() {
        val manager = agora ?: return
        if (uniendose || terminado) return
        uniendose = true
        iniciarEspera()

        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.obtenerTokenLlamada("Bearer ${SessionManager.token}", conversacionId)
                if (terminado) return@launch
                val cuerpo = res.body()
                if (!res.isSuccessful || cuerpo?.token == null) {
                    terminar(true, "No se pudo iniciar la llamada (${res.code()})")
                    return@launch
                }
                val r = manager.unirseCanal(cuerpo.canal ?: "chat_$conversacionId", cuerpo.token, esVideo)
                if (r != 0) terminar(true, "No se pudo unir a la llamada (código $r)")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AGORA", "Token: ${e.message}")
                terminar(true, "Error de conexión con el servidor")
            }
        }
    }

    private fun alConectarOtro(uid: Int) {
        if (terminado) return
        uidRemoto = uid
        fase = Fase.EN_LLAMADA
        jobEspera?.cancel()
        if (jobTemporizador == null) {
            jobTemporizador = viewModelScope.launch {
                while (true) { delay(1000); segundos++ }
            }
        }
    }

    private fun iniciarEspera() {
        jobEspera = viewModelScope.launch {
            delay(40_000)
            if (fase != Fase.EN_LLAMADA) terminar(true, "No contestó")
        }
    }

    // ---------- Controles ----------
    fun alternarMicro() { microApagado = !microApagado; agora?.silenciar(microApagado) }

    fun alternarCamara() {
        if (!esVideo) return
        camaraEncendida = !camaraEncendida
        if (camaraEncendida) agora?.encenderCamara() else agora?.apagarCamara()
    }

    fun girarCamara() { if (camaraEncendida) agora?.cambiarCamara() }

    fun alternarAltavoz() { altavozActivo = !altavozActivo; agora?.altavoz(altavozActivo) }

    fun crearVistaLocal(ctx: Context): TextureView = agora?.crearVistaLocal(ctx) ?: TextureView(ctx)
    fun crearVistaRemota(ctx: Context, uid: Int): TextureView = agora?.crearVistaRemota(ctx, uid) ?: TextureView(ctx)

    // ---------- Cierre ----------
    fun colgar() = terminar(avisarServidor = true)

    fun alRechazar() = terminar(avisarServidor = false, aviso = "Llamada rechazada o sin respuesta")

    /** Único punto de salida: se ejecuta una sola vez sin importar cuántos eventos lo disparen. */
    fun terminar(avisarServidor: Boolean, aviso: String? = null, emitir: Boolean = true) {
        if (terminado) return
        terminado = true
        jobEspera?.cancel()
        jobTemporizador?.cancel()

        if (avisarServidor && iniciado) {
            if (fase == Fase.EN_LLAMADA) LlamadasManager.finalizar(conversacionId)
            else LlamadasManager.cancelar(conversacionId)
        }
        agora?.liberar()

        if (emitir) {
            aviso?.let { _eventos.trySend(Evento.Aviso(it)) }
            _eventos.trySend(Evento.Cerrar)
        }
    }

    override fun onCleared() {
        terminar(avisarServidor = true, emitir = false)   // por si se sale por una vía que no pasó por aquí
        super.onCleared()
    }
}