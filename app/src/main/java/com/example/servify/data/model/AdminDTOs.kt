package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

// Métricas del Dashboard
data class DashboardStatsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: DashboardData
)

data class DashboardData(
    @SerializedName("usuarios") val usuarios: StatsUsuarios,
    @SerializedName("solicitudes") val solicitudes: StatsSolicitudes,
    @SerializedName("total_categorias") val totalCategorias: Int
)

data class StatsUsuarios(
    @SerializedName("total_usuarios") val totalUsuarios: Int = 0,
    @SerializedName("total_clientes") val totalClientes: Int = 0,
    @SerializedName("total_tecnicos") val totalTecnicos: Int = 0,
    @SerializedName("total_admins") val totalAdmins: Int = 0
)

data class StatsSolicitudes(
    @SerializedName("total_solicitudes") val totalSolicitudes: Int = 0,
    @SerializedName("solicitudes_pendientes") val pendientes: Int = 0,
    @SerializedName("solicitudes_en_proceso") val enProceso: Int = 0,
    @SerializedName("solicitudes_completadas") val completadas: Int = 0,
    @SerializedName("solicitudes_canceladas") val canceladas: Int = 0
)

// Gestión de Usuarios
data class AdminUsuarioItem(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("rol") val rol: String,
    @SerializedName("creado_en") val creadoEn: String? = null
)

data class AdminUsuariosResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("usuarios") val usuarios: List<AdminUsuarioItem>
)

data class CambiarRolRequest(
    @SerializedName("nuevoRol") val nuevoRol: String
)

// Moderación de Solicitudes
data class AdminSolicitudItem(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("estado") val estado: String,
    @SerializedName("presupuesto_estimado") val presupuestoEstimado: String? = null,
    @SerializedName("creado_en") val creadoEn: String? = null,
    @SerializedName("cliente_nombre") val clienteNombre: String? = null,
    @SerializedName("cliente_correo") val clienteCorreo: String? = null
)

data class AdminSolicitudesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("solicitudes") val solicitudes: List<AdminSolicitudItem>
)