package com.example.servify.data.agora

import android.content.Context
import android.view.SurfaceView
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class AgoraManager(
    context: Context,
    appId: String,
    eventHandler: IRtcEngineEventHandler
) {
    private var rtcEngine: RtcEngine? = null

    init {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = appId
                mEventHandler = eventHandler
            }
            rtcEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            android.util.Log.e("AGORA", "Error al crear el motor: ${e.message}")
        }
    }

    fun crearVistaLocal(context: Context): SurfaceView {
        val vista = SurfaceView(context)
        vista.setZOrderMediaOverlay(true)
        rtcEngine?.setupLocalVideo(VideoCanvas(vista, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        return vista
    }

    fun crearVistaRemota(context: Context, uid: Int): SurfaceView {
        val vista = SurfaceView(context)
        rtcEngine?.setupRemoteVideo(VideoCanvas(vista, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        return vista
    }

    fun unirseCanal(channelName: String, token: String?, esVideo: Boolean, uid: Int = 0) {
        val engine = rtcEngine ?: run {
            android.util.Log.e("AGORA", "El motor es null, no se pudo crear")
            return
        }

        engine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        engine.enableAudio()
        if (esVideo) {
            engine.enableVideo()
            engine.startPreview()
        }

        val opciones = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            publishMicrophoneTrack = true
            publishCameraTrack = esVideo
            autoSubscribeAudio = true
            autoSubscribeVideo = esVideo
        }

        val resultado = engine.joinChannel(token, channelName, uid, opciones)
        android.util.Log.d("AGORA", "joinChannel('$channelName') devolvió $resultado")
    }

    fun salirCanal() {
        rtcEngine?.stopPreview()
        rtcEngine?.leaveChannel()
    }

    fun alternarAudio(silenciar: Boolean) { rtcEngine?.muteLocalAudioStream(silenciar) }
    fun alternarVideo(apagar: Boolean) { rtcEngine?.muteLocalVideoStream(apagar) }
    fun cambiarCamara() { rtcEngine?.switchCamera() }

    fun destruir() {
        RtcEngine.destroy()
        rtcEngine = null
    }
}