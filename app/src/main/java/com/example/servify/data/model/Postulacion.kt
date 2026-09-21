package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Postulacion(
    @SerializedName("id") val id: Int,
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("trabajador_id") val trabajadorId: Int,
    @SerializedName("precio_propuesto") val precioPropuesto: Double? = null,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("estado") val estado: String, // "PENDIENTE", "ACEPTADA", "RECHAZADA"
    @SerializedName("creado_en") val creadoEn: String? = null
)