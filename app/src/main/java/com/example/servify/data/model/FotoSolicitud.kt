package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class FotoSolicitud(
    @SerializedName("id") val id: Int,
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("foto_url") val fotoUrl: String,
    @SerializedName("creado_en") val creadoEn: String? = null
)