package com.example.servify.data.model


import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String? = null,

    @SerializedName("foto_url")
    val fotoUrl: String? = null,

    val rol: String, // "cliente", "tecnico", "ambos", "admin"

    val latitud: Double? = null,
    val longitud: Double? = null,

    @SerializedName("direccion_texto")
    val direccionTexto: String? = null,

    @SerializedName("es_vip")
    val esVip: Boolean = false,

    @SerializedName("calificacion_promedio")
    val calificacionPromedio: Double = 0.0,

    @SerializedName("creado_en")
    val creadoEn: String? = null,

    @SerializedName("actualizado_en")
    val actualizadoEn: String? = null
)

