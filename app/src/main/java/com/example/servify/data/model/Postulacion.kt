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

data class GananciasResumen(
    @SerializedName("total_semana") val totalSemana: Double = 0.0,
    @SerializedName("trabajos_semana") val trabajosSemana: Int = 0,
    @SerializedName("total_mes") val totalMes: Double = 0.0,
    @SerializedName("trabajos_mes") val trabajosMes: Int = 0,
    @SerializedName("total_mes_anterior") val totalMesAnterior: Double = 0.0,
    @SerializedName("total_historico") val totalHistorico: Double = 0.0,
    @SerializedName("trabajos_historico") val trabajosHistorico: Int = 0
)

