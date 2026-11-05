package com.example.servify.data.agora

import android.content.Context
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.IRtcEngineEventHandler

class AgoraManager(
    private val context: Context,
    private val appId: String,
    private val eventHandler: IRtcEngineEventHandler
) {
    private var rtcEngine: RtcEngine? = null

    init {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context
                mAppId = appId
                mEventHandler = eventHandler
            }
            rtcEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unirseCanal(channelName: String, token: String?, uid: Int = 0) {
        rtcEngine?.let { engine ->
            engine.enableAudio()
            engine.enableVideo() // Omitir si es solo llamada de voz
            engine.joinChannel(token, channelName, "", uid)
        }
    }

    fun salirCanal() {
        rtcEngine?.leaveChannel()
    }

    fun alternarAudio(silenciar: Boolean) {
        rtcEngine?.muteLocalAudioStream(silenciar)
    }

    fun alternarVideo(apagar: Boolean) {
        rtcEngine?.muteLocalVideoStream(apagar)
    }

    fun destruir() {
        RtcEngine.destroy()
        rtcEngine = null
    }
}