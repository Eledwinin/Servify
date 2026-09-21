package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("foto_url")
    val fotoUrl: String? = null,

    @SerializedName("rol")
    val rol: String, // "cliente", "tecnico", "ambos", "admin"

    @SerializedName("latitud")
    val latitud: Double? = null,

    @SerializedName("longitud")
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

typealias Usuario = UsuarioModel

