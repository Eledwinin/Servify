package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class ActualizarPerfilRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("direccion_texto") val direccionTexto: String?,
    @SerializedName("biografia") val biografia: String? = null,
    @SerializedName("anos_experiencia") val anosExperiencia: Int? = null
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
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombreCompleto")
    val nombreCompleto: String,

    @SerializedName("oficio")
    val oficio: String,

    @SerializedName("anosExperiencia")
    val anosExperiencia: Int,

    @SerializedName("calificacionPromedio")
    val calificacionPromedio: Double,

    @SerializedName("totalResenas")
    val totalResenas: Int = 0,

    @SerializedName("biografia")
    val biografia: String? = null,

    @SerializedName("latitud")
    val latitud: Double? = null,

    @SerializedName("longitud")
    val longitud: Double? = null,

    @SerializedName("especialidades") val especialidades: List<String> = emptyList(),
    @SerializedName("trabajosCompletados") val trabajosCompletados: Int = 0,
    @SerializedName("esVip") val esVip: Int = 0,
    @SerializedName("resenas") val resenas: List<ResenaItem> = emptyList()


)

data class ResenaItem(
    @SerializedName("id") val id: Int,
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String? = null,
    @SerializedName("autorNombre") val autorNombre: String,
    @SerializedName("creadoEn") val creadoEn: String? = null
)