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
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: Double? = null,
    @SerializedName("distancia_km") val distanciaKm: Double? = null,
    @SerializedName("creado_en") val creadoEn: String? = null
)

data class CrearPostulacionRequest(
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("monto_propuesto") val montoPropuesto: Double,
    @SerializedName("tiempo_estimado") val tiempoEstimado: String,
    @SerializedName("mensaje") val mensaje: String
)

data class SolicitudVipDto(
    @SerializedName("es_vip") val esVip: Boolean
)

data class RespuestaVipDto(
    val mensaje: String,
    @SerializedName("es_vip") val esVip: Boolean
)

// DTO para enviar los datos de la solicitud al servidor
data class CrearSolicitudRequest(
    @SerializedName("categoria_id") val categoriaId: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto") val presupuesto: Double?,
    @SerializedName("latitud") val latitud: Double? = null,
    @SerializedName("longitud") val longitud: Double? = null,
    @SerializedName("direccion_texto") val direccionTexto: String? = null
)

data class MiSolicitud(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: Double? = null,
    @SerializedName("estado") val estado: String,
    @SerializedName("categoria_nombre") val categoriaNombre: String,
    @SerializedName("total_propuestas") val totalPropuestas: Int = 0,
    @SerializedName("creado_en") val creadoEn: String? = null
)

data class PropuestaItem(
    @SerializedName("id") val id: Int,
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("tecnico_id") val tecnicoId: Int,
    @SerializedName("tecnico_nombre") val tecnicoNombre: String,
    @SerializedName("tecnico_calificacion") val tecnicoCalificacion: Double? = null,
    @SerializedName("monto_propuesto") val montoPropuesto: Double,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("estado") val estado: String,
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: Double? = null,
    @SerializedName("conversacion_id") val conversacionId: Int? = null
)

data class AceptarRespuesta(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("conversacion_id") val conversacionId: Int? = null
)

data class MiPostulacion(
    @SerializedName("id") val id: Int,
    @SerializedName("solicitud_id") val solicitudId: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria_nombre") val categoriaNombre: String,
    @SerializedName("monto_propuesto") val montoPropuesto: Double,
    @SerializedName("estado") val estado: String,
    @SerializedName("conversacion_id") val conversacionId: Int? = null,
    @SerializedName("creado_en") val creadoEn: String? = null
)

data class DetalleSolicitud(
    @SerializedName("id") val id: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("cliente_nombre") val clienteNombre: String,
    @SerializedName("cliente_calificacion") val clienteCalificacion: Double? = null,
    @SerializedName("categoria_nombre") val categoriaNombre: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: Double? = null,
    @SerializedName("direccion_texto") val direccionTexto: String? = null,
    @SerializedName("distancia_km") val distanciaKm: Double? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("ya_postulado") val yaPostulado: Boolean = false,
    @SerializedName("fotos") val fotos: List<String> = emptyList()
)

data class CrearSolicitudRespuesta(
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName(value = "id", alternate = ["insertId"]) val id: Int?
)