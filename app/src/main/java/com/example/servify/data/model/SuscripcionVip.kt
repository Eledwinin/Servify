package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class SuscripcionVip(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("transaccion_id") val transaccionId: String? = null,
    @SerializedName("fecha_inicio") val fechaInicio: String,
    @SerializedName("fecha_fin") val fechaFin: String,
    @SerializedName("estado") val estado: String // "ACTIVA", "VENCIDA", "CANCELADA"
)