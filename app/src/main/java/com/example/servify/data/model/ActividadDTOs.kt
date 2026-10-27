package com.example.servify.data.model

// Modelo para los trabajos del técnico (En curso / Historial)
data class TrabajoItem(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoriaNombre: String,
    val estado: String, // "EN_CURSO", "COMPLETADO", "CANCELADO"
    val fechaTexto: String,
    val calificacion: Double?,
    val presupuesto: Double?
)

// Modelo para las postulaciones enviadas
data class PostulacionItem(
    val id: Int,
    val solicitudId: Int,
    val tituloTrabajo: String,
    val categoriaNombre: String,
    val propuestaPrecio: Double,
    val estado: String // "PENDIENTE", "NO_SELECCIONADA", "ACEPTADA"
)