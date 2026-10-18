package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class ConversacionItem(
    @SerializedName("id") val id: Int,
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("activa") val activa: Int = 1,
    @SerializedName("solicitud_titulo") val solicitudTitulo: String,
    @SerializedName("otro_id") val otroId: Int,
    @SerializedName("otro_nombre") val otroNombre: String,
    @SerializedName("ultimo_mensaje") val ultimoMensaje: String? = null,
    @SerializedName("ultimo_mensaje_en") val ultimoMensajeEn: String? = null,
    @SerializedName("no_leidos") val noLeidos: Int = 0
)

data class MensajesRespuesta(
    @SerializedName("activa") val activa: Boolean = true,
    @SerializedName("mensajes") val mensajes: List<Mensaje> = emptyList()
)