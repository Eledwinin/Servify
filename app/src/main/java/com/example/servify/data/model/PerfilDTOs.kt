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

data class PerfilTecnicoResponse(
    @SerializedName("id")
    val idTecnico: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("oficio")
    val oficio: String?,

    @SerializedName("tarifa_hora")
    val tarifaHora: Double?,

    @SerializedName("experiencia_anos")
    val experienciaAnos: Int?,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("calificacion_promedio")
    val calificacionPromedio: Double? = 0.0
)

data class ProfesionalPerfil(
    val id: Int,
    val nombreCompleto: String,
    val oficio: String,
    val anosExperiencia: Int,
    val calificacionPromedio: Double,
    val totalResenas: Int,
    val biografia: String
)