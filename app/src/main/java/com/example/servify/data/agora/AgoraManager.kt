package com.example.servify.data.agora

import android.content.Context
import android.view.TextureView
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import java.util.concurrent.atomic.AtomicBoolean

class AgoraManager(
    context: Context,
    appId: String,
    eventHandler: IRtcEngineEventHandler
) {
    companion object {
        // create() y destroy() usan el mismo singleton interno: se serializan para que un destroy
        // pendiente de una llamada anterior no destruya el motor de la siguiente
        private val candado = Any()
    }

    @Volatile private var rtcEngine: RtcEngine? = null
    private val liberado = AtomicBoolean(false)
    @Volatile private var publicarCamara = false

    init {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = appId
                mEventHandler = eventHandler
                mChannelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            }
            synchronized(candado) { rtcEngine = RtcEngine.create(config) }
        } catch (e: Exception) {
            android.util.Log.e("AGORA", "Error al crear el motor: ${e.message}")
        }
    }

    /** Video habilitado (para recibir el del otro) pero SIN capturar la cámara local. */
    fun prepararVideo() {
        rtcEngine?.apply {
            enableVideo()
            enableLocalVideo(false)
        }
    }

    fun configurarAltavoz(activo: Boolean) {
        rtcEngine?.setDefaultAudioRoutetoSpeakerphone(activo)
    }

    /** Se crea una sola vez; se vincula al motor y se queda en pantalla. */
    fun crearVistaLocal(context: Context): TextureView {
        val vista = TextureView(context)
        rtcEngine?.setupLocalVideo(VideoCanvas(vista, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        return vista
    }

    fun crearVistaRemota(context: Context, uid: Int): TextureView {
        val vista = TextureView(context)
        rtcEngine?.setupRemoteVideo(VideoCanvas(vista, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        return vista
    }

    /** Devuelve 0 si salió bien. La cámara entra apagada salvo que el usuario ya la haya prendido. */
    fun unirseCanal(channelName: String, token: String?, esVideo: Boolean, uid: Int = 0): Int {
        val engine = rtcEngine ?: return -1
        val opciones = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            publishMicrophoneTrack = true
            publishCameraTrack = publicarCamara
            autoSubscribeAudio = true
            autoSubscribeVideo = esVideo
        }
        val r = engine.joinChannel(token, channelName, uid, opciones)
        android.util.Log.d("AGORA", "joinChannel('$channelName') devolvió $r")
        return r
    }

    fun encenderCamara() {
        publicarCamara = true
        rtcEngine?.apply {
            enableLocalVideo(true)
            startPreview()
            // Si todavía no se ha unido, esta llamada se ignora y joinChannel usa publicarCamara
            updateChannelMediaOptions(ChannelMediaOptions().apply { publishCameraTrack = true })
        }
    }

    fun apagarCamara() {
        publicarCamara = false
        rtcEngine?.apply {
            updateChannelMediaOptions(ChannelMediaOptions().apply { publishCameraTrack = false })
            stopPreview()
            enableLocalVideo(false)
        }
    }

    fun silenciar(silenciar: Boolean) { rtcEngine?.muteLocalAudioStream(silenciar) }
    fun altavoz(activo: Boolean) { rtcEngine?.setEnableSpeakerphone(activo) }
    fun cambiarCamara() { rtcEngine?.switchCamera() }

    /** Se puede llamar las veces que sea: solo la primera hace algo. destroy() bloquea, va en otro hilo. */
    fun liberar() {
        if (!liberado.compareAndSet(false, true)) return
        val engine = rtcEngine ?: return
        rtcEngine = null
        Thread {
            synchronized(candado) {
                try {
                    engine.stopPreview()
                    engine.leaveChannel()
                } catch (_: Exception) { }
                RtcEngine.destroy()
            }
        }.start()
    }
}