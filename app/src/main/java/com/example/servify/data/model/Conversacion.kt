package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Conversacion(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("trabajador_id") val trabajadorId: Int,
    @SerializedName("creado_en") val creadoEn: String? = null
)