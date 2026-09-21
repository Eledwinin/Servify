package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

data class ContratacionDirecta(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("trabajador_id") val trabajadorId: Int,
    @SerializedName("categoria_id") val categoriaId: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("precio_acordado") val precioAcordado: Double? = null,
    @SerializedName("estado") val estado: String, // "PENDIENTE", "ACEPTADA", "FINALIZADA", "CANCELADA"
    @SerializedName("creado_en") val creadoEn: String? = null
)