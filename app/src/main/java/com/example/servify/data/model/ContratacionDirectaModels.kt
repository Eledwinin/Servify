package com.example.servify.data.model

// Objeto para enviar la solicitud directa al Backend
data class ContratacionDirectaRequest(
    val tecnico_id: Int,
    val categoria_id: Int,
    val descripcion: String,
    val monto_acordado: Double? = null
)

// Objeto que responde el Backend al consultar las solicitudes del técnico (Sobrecito)
data class ContratacionDirectaResponse(
    val id: Int,
    val cliente_id: Int,
    val tecnico_id: Int,
    val categoria_id: Int,
    val descripcion: String,
    val monto_acordado: Double? = null,
    val estado: String = "pendiente", // 'pendiente', 'aceptado', 'rechazado'
    val creado_en: String? = null,
    val cliente_nombre: String = "Cliente",
    val cliente_foto: String? = null,
    val cliente_telefono: String? = null,
    val categoria_nombre: String = "General"
)

// Objeto para cambiar el estado (Aceptar o Rechazar)
data class ResponderContratacionRequest(
    val estado: String // "aceptado" o "rechazado"
)