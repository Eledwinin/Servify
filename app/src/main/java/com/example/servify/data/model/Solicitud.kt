package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class Solicitud(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("categoria_id") val categoriaId: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: Double? = null,
    @SerializedName("latitud") val latitud: Double? = null,
    @SerializedName("longitud") val longitud: Double? = null,
    @SerializedName("direccion_texto") val direccionTexto: String? = null,
    @SerializedName("estado") val estado: String, // "PENDIENTE", "EN_PROCESO", "COMPLETADA", "CANCELADA"
    @SerializedName("creado_en") val creadoEn: String? = null
)
// DTO con los datos combinados (JOIN de SQL) para pintar la tarjeta del técnico
data class SolicitudItemTecnico(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("cliente_nombre") val clienteNombre: String,
    @SerializedName("categoria_nombre") val categoriaNombre: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("distancia_km") val distanciaKm: Double = 0.0,
    @SerializedName("creado_en") val creadoEn: String? = null
)

data class SolicitudVipDto(
    @SerializedName("es_vip") val esVip: Boolean
)

data class RespuestaVipDto(
    val mensaje: String,
    @SerializedName("es_vip") val esVip: Boolean
)