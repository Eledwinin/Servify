package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class ActualizarPerfilRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("direccion_texto")
    val direccionTexto: String?
)