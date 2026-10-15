package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Conversacion(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("tecnico_id") val tecnicoId: Int,
    @SerializedName("creado_en") val creadoEn: String? = null,
    @SerializedName("solicitud_id") val solicitudId: Int? = null
)