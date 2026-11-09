package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

// Petición y Respuesta para Iniciar Cierre (Cliente)
data class IniciarCierreRequest(
    @SerializedName("metodo_pago") val metodoPago: String, // "efectivo" o "tarjeta"
    @SerializedName("tecnico_id") val tecnicoId: Int?,
    @SerializedName("monto") val monto: Double? = null
)

data class IniciarCierreResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("qr_token") val qrToken: String?,
    @SerializedName("metodo_pago") val metodoPago: String?,
    @SerializedName("estado_pago") val estadoPago: String?
)

// Petición y Respuesta para Validar QR (Técnico)
data class ValidarQrRequest(
    @SerializedName("qr_token") val qrToken: String,
    @SerializedName("tecnico_id") val tecnicoId: Int
)

data class ValidarQrResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("solicitud_id") val solicitudId: Int?,
    @SerializedName("cliente_id") val clienteId: Int?,
    @SerializedName("tecnico_id") val tecnicoId: Int?
)

// Petición para Crear Reseña (Cliente & Técnico)
data class ResenaRequest(
    @SerializedName("autor_id") val autorId: Int,
    @SerializedName("destinatario_id") val destinatarioId: Int,
    @SerializedName("solicitud_id") val solicitudId: Int? = null,
    @SerializedName("contratacion_directa_id") val contratacionDirectaId: Int? = null,
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String? = null
)

// Respuesta Genérica para Peticiones HTTP
data class GenericResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("error") val error: String? = null
)