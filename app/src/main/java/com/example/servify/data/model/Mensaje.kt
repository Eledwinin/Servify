package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Mensaje(
    @SerializedName("id") val id: Int,
    @SerializedName("conversacion_id") val conversacionId: Int,
    @SerializedName("emisor_id") val emisorId: Int,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("leido") val leido: Int = 0,
    @SerializedName("enviado_en") val enviadoEn: String? = null
)