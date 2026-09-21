package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Resena(
    @SerializedName("id") val id: Int,
    @SerializedName("autor_id") val autorId: Int,
    @SerializedName("destinatario_id") val destinatarioId: Int,
    @SerializedName("solicitud_id") val solicitudId: Int? = null,
    @SerializedName("contratacion_directa_id") val contratacionDirectaId: Int? = null,
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String? = null,
    @SerializedName("creado_en") val creadoEn: String? = null
)