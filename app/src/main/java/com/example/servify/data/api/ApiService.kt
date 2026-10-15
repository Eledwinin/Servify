package com.example.servify.data.api

import com.example.servify.data.model.AceptarRespuesta
import com.example.servify.data.model.ActualizarPerfilRequest
import com.example.servify.data.model.AuthResponse
import com.example.servify.data.model.CambiarPasswordRequest
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.CrearPostulacionRequest
import com.example.servify.data.model.CrearSolicitudRequest
import com.example.servify.data.model.DetalleSolicitud
import com.example.servify.data.model.LoginRequest
import com.example.servify.data.model.LoginResponse
import com.example.servify.data.model.MensajeResponse
import com.example.servify.data.model.MiPostulacion
import com.example.servify.data.model.MiSolicitud
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.model.PropuestaItem
import com.example.servify.data.model.RegisterRequest
import com.example.servify.data.model.RespuestaVipDto
import com.example.servify.data.model.SolicitarRecuperacionRequest
import com.example.servify.data.model.SolicitudItemTecnico
import com.example.servify.data.model.SolicitudVipDto
import com.example.servify.data.model.UsuarioModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Categoria>>

    // Endpoint para el login
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Endpoint para Registro
    @POST("api/auth/register")
    suspend fun registrarUsuario(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    // Endpoint para Solicitar Recuperación
    @POST("api/auth/solicitar-recuperacion")
    suspend fun solicitarRecuperacion(
        @Body request: SolicitarRecuperacionRequest
    ): Response<MensajeResponse>

    // Endpoint para Cambiar Contraseña
    @POST("api/auth/cambiar-password")
    suspend fun cambiarPassword(
        @Body request: CambiarPasswordRequest
    ): Response<MensajeResponse>

    // Endpoint para verificar el código OTP
    @POST("api/auth/verificar-codigo")
    suspend fun verificarCodigoOtp(
        @Body request: CambiarPasswordRequest
    ): Response<MensajeResponse>

    // Endpoint para obtener el perfil del usuario autenticado
    @GET("api/users/perfil")
    suspend fun getPerfil(
        @Header("Authorization") token: String
    ): Response<UsuarioModel>

    // Endpoint para actualizar el perfil
    @PUT("api/users/perfil")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body request: ActualizarPerfilRequest
    ): Response<MensajeResponse>

    // para obtener el perfil del trabajador
    @GET("api/users/{id}")
    suspend fun getPerfilProfesional(
        @Path("id") id: String
    ): Response<ProfesionalPerfil>

    // Endpoint para listar todos los profesionales en el Home
    @GET("api/users/profesionales")
    suspend fun getProfesionales(): Response<List<ProfesionalPerfil>>

    @PUT("api/users/{id}/vip")
    suspend fun actualizarEstadoVip(
        @Path("id") idUsuario: Int,
        @Body body: SolicitudVipDto
    ): Response<RespuestaVipDto>

    // ==========================================
    // 1. ELIMINACIÓN DE CUENTA (Cliente y Técnico)
    // ==========================================

    // Endpoint para eliminar permanentemente la cuenta del usuario autenticado
    @DELETE("api/users/perfil/eliminar")
    suspend fun eliminarCuenta(
        @Header("Authorization") token: String
    ): Response<MensajeResponse>

    // ==========================================
    // 2. PORTAFOLIO DE TRABAJOS (Exclusivo Técnicos)
    // ==========================================

    // Endpoint para obtener las fotos del portafolio del técnico
    @GET("api/users/portafolio")
    suspend fun obtenerPortafolio(
        @Header("Authorization") token: String
    ): Response<List<FotoPortafolioResponse>>

    // Endpoint para subir una foto nueva al portafolio
    @Multipart
    @POST("api/users/portafolio")
    suspend fun subirFotoPortafolio(
        @Header("Authorization") token: String,
        @Part foto: MultipartBody.Part
    ): Response<SubirFotoResponse>

    // Endpoint para eliminar una foto del portafolio por su ID
    @DELETE("api/users/portafolio/{fotoId}")
    suspend fun eliminarFotoPortafolio(
        @Header("Authorization") token: String,
        @Path("fotoId") fotoId: Int
    ): Response<MensajeResponse>

    // Endpoint para crear una solicitud
    @POST("api/solicitudes")
    suspend fun crearSolicitud(
        @Header("Authorization") token: String,
        @Body request: CrearSolicitudRequest
    ): Response<MensajeResponse>


    //para obtener las solicitudes de los clientes
    // Solicitudes
    @GET("api/solicitudes")
    suspend fun obtenerSolicitudes(
        @Header("Authorization") token: String,
        @Query("lat") lat: Double?,
        @Query("lng") lng: Double?
    ): Response<List<SolicitudItemTecnico>>

    @GET("api/solicitudes/mias")
    suspend fun obtenerMisSolicitudes(@Header("Authorization") token: String): Response<List<MiSolicitud>>

    @GET("api/solicitudes/{id}")
    suspend fun obtenerDetalleSolicitud(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DetalleSolicitud>

    // Postulaciones
    @POST("api/postulaciones")
    suspend fun crearPostulacion(
        @Header("Authorization") token: String,
        @Body request: CrearPostulacionRequest
    ): Response<MensajeResponse>

    @GET("api/postulaciones/mias")
    suspend fun obtenerMisPostulaciones(@Header("Authorization") token: String): Response<List<MiPostulacion>>

    @GET("api/postulaciones/solicitud/{id}")
    suspend fun obtenerPropuestas(
        @Header("Authorization") token: String,
        @Path("id") solicitudId: Int
    ): Response<List<PropuestaItem>>

    @PUT("api/postulaciones/{id}/aceptar")
    suspend fun aceptarPropuesta(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AceptarRespuesta>

    @PUT("api/postulaciones/{id}/rechazar")
    suspend fun rechazarPropuesta(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MensajeResponse>

    @PUT("api/postulaciones/{id}/cancelar-acuerdo")
    suspend fun cancelarAcuerdo(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MensajeResponse>
}

// ==========================================
// DATA CLASSES / MODELOS DE RESPUESTA
// ==========================================

data class FotoPortafolioResponse(
    val id: Int,
    val foto_url: String,
    val fecha_subida: String? = null
)

data class SubirFotoResponse(
    val mensaje: String,
    val foto: FotoPortafolioResponse
)

