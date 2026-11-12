package com.example.servify.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.servify.MainActivity
import com.example.servify.R
import com.example.servify.ui.navigation.Rutas

/**
 * Gestor nativo de notificaciones locales y en tiempo real para Servify.
 * Maneja canales de notificación, PendingIntents seguros, control de deduplicación
 * y construcción con NotificationCompat.
 */
object ServifyNotificationHelper {

    private const val TAG = "ServifyNotification"

    // Canales de Notificación Nativos
    const val CHANNEL_CRITICO_ID = "servify_canal_critico"
    private const val CHANNEL_CRITICO_NAME = "Alertas Críticas y Servicios"
    private const val CHANNEL_CRITICO_DESC = "Notificaciones de alta prioridad: servicios aceptados, técnico en camino, pagos y llamadas."

    const val CHANNEL_GENERAL_ID = "servify_canal_general"
    private const val CHANNEL_GENERAL_NAME = "Avisos Generales"
    private const val CHANNEL_GENERAL_DESC = "Notificaciones de prioridad estándar: cotizaciones, mensajes y novedades del sistema."

    // Tipos de Eventos en Tiempo Real
    const val EVENTO_POSTULACION_ACEPTADA = "postulacion_aceptada"
    const val EVENTO_PROPUESTA_ACEPTADA = "propuesta_aceptada"
    const val EVENTO_PROPUESTA_ACEPTADA_EVENTO = "propuesta_aceptada_evento"
    const val EVENTO_TECNICO_EN_CAMINO = "tecnico_en_camino"
    const val EVENTO_NUEVA_COTIZACION = "nueva_cotizacion"
    const val EVENTO_NUEVA_PROPUESTA = "nueva_propuesta"
    const val EVENTO_PAGO_CONFIRMADO = "pago_confirmado"
    const val EVENTO_MENSAJE_NUEVO = "mensaje_nuevo"
    const val EVENTO_LLAMADA_ENTRANTE = "llamada_entrante"

    private var appContext: Context? = null

    /**
     * Inicializa el helper guardando el contexto de la aplicación, creando canales e inicializando el control de deduplicación.
     */
    fun init(context: Context) {
        val app = context.applicationContext
        appContext = app
        crearCanales(app)
        NotificacionCacheManager.init(app)
    }

    /**
     * Crea los NotificationChannels requeridos para Android 8.0+ (API 26+)
     */
    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // 1. Canal de Alta Prioridad (IMPORTANCE_HIGH) para alertas críticas
            val canalCritico = NotificationChannel(
                CHANNEL_CRITICO_ID,
                CHANNEL_CRITICO_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_CRITICO_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 350, 200, 350)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 2. Canal de Prioridad Media (IMPORTANCE_DEFAULT) para avisos generales
            val canalGeneral = NotificationChannel(
                CHANNEL_GENERAL_ID,
                CHANNEL_GENERAL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_GENERAL_DESC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(canalCritico)
            notificationManager.createNotificationChannel(canalGeneral)
            Log.d(TAG, "Canales de notificación creados exitosamente.")
        }
    }

    /**
     * Construye y muestra una notificación nativa personalizada.
     */
    fun mostrarNotificacion(
        context: Context,
        id: Int,
        titulo: String,
        cuerpo: String,
        canalId: String = CHANNEL_CRITICO_ID,
        pendingIntent: PendingIntent? = null
    ) {
        crearCanales(context)

        // Verificación de permiso en Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (permiso != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permiso POST_NOTIFICATIONS no concedido. Omitiendo notificación.")
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val sonidoDefault = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val esCritico = (canalId == CHANNEL_CRITICO_ID)

        val builder = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setPriority(if (esCritico) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setSound(sonidoDefault)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        // Se usa el ID determinado para actualizar en lugar de acumular spam
        notificationManager.notify(id, builder.build())
        Log.d(TAG, "Notificación emitida con ID [$id]: $titulo - $cuerpo (Canal: $canalId)")
    }

    fun mostrarNotificacion(
        id: Int,
        titulo: String,
        cuerpo: String,
        canalId: String = CHANNEL_CRITICO_ID,
        pendingIntent: PendingIntent? = null
    ) {
        val ctx = appContext ?: return
        mostrarNotificacion(ctx, id, titulo, cuerpo, canalId, pendingIntent)
    }

    /**
     * Configura el PendingIntent seguro con flags FLAG_UPDATE_CURRENT o FLAG_IMMUTABLE
     * apuntando a MainActivity con la ruta de navegación correspondiente.
     */
    fun crearPendingIntent(context: Context, destinoRuta: String?, extraId: Int = 0): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("destino_ruta", destinoRuta)
            putExtra("extra_id", extraId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val requestCode = (destinoRuta?.hashCode() ?: 0) + extraId
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }

    /**
     * Procesa EXCLUSIVAMENTE eventos individuales en tiempo real recibidos por Socket.io.
     * Incorpora deduplicación estricta y asignación de ID determinista para no saturar al usuario.
     */
    fun procesarEventoSocket(
        tipo: String,
        titulo: String,
        mensaje: String,
        extraId: Int = 0,
        extraStr: String = ""
    ) {
        val ctx = appContext ?: run {
            Log.e(TAG, "Contexto nulo al procesar evento socket: $tipo")
            return
        }

        // CONTROL DE DEDUPLICACIÓN: Ignora eventos que ya fueron procesados previamente
        val claveDeduplicacion = NotificacionCacheManager.generarClave(tipo, extraId, mensaje)
        if (NotificacionCacheManager.yaFueProcesada(claveDeduplicacion)) {
            Log.d(TAG, "Deduplicación activa: notificación ignorada para clave [$claveDeduplicacion]")
            return
        }

        // Registra el evento inmediatamente en la caché persistente
        NotificacionCacheManager.registrarNotificacion(claveDeduplicacion)

        val (canalId, rutaDestino) = when (tipo) {
            EVENTO_POSTULACION_ACEPTADA, EVENTO_PROPUESTA_ACEPTADA, EVENTO_PROPUESTA_ACEPTADA_EVENTO -> {
                // Alerta crítica -> Redirige al módulo de Actividad
                Pair(CHANNEL_CRITICO_ID, Rutas.Actividad.ruta)
            }
            EVENTO_TECNICO_EN_CAMINO -> {
                // Alerta crítica -> Redirige a Actividad / Seguimiento
                Pair(CHANNEL_CRITICO_ID, Rutas.Actividad.ruta)
            }
            EVENTO_PAGO_CONFIRMADO -> {
                // Alerta crítica -> Redirige a Actividad
                Pair(CHANNEL_CRITICO_ID, Rutas.Actividad.ruta)
            }
            EVENTO_LLAMADA_ENTRANTE -> {
                // Alerta crítica -> Abre pantalla de llamada
                val canal = if (extraStr.isNotBlank()) extraStr else "chat_$extraId"
                Pair(CHANNEL_CRITICO_ID, Rutas.Call.crearRuta(canal, false))
            }
            EVENTO_NUEVA_COTIZACION, EVENTO_NUEVA_PROPUESTA -> {
                // Aviso general -> Abre las propuestas de la solicitud si está disponible
                val ruta = if (extraId > 0) Rutas.PropuestasSolicitud.crearRuta(extraId) else Rutas.Actividad.ruta
                Pair(CHANNEL_GENERAL_ID, ruta)
            }
            EVENTO_MENSAJE_NUEVO -> {
                // Aviso general -> Abre la conversación del chat
                val ruta = if (extraId > 0) Rutas.Chat.crearRuta(extraId, extraStr) else Rutas.Mensajes.ruta
                Pair(CHANNEL_GENERAL_ID, ruta)
            }
            else -> {
                Pair(CHANNEL_GENERAL_ID, Rutas.Home.ruta)
            }
        }

        val pendingIntent = crearPendingIntent(ctx, rutaDestino, extraId)

        // ID determinista basado en el ID de la solicitud (extraId) para actualizar la existente en lugar de acumular cientos
        val notifId = if (extraId > 0) {
            extraId.hashCode() and 0x7FFFFFFF
        } else {
            (tipo.hashCode() * 31 + mensaje.hashCode()) and 0x7FFFFFFF
        }

        mostrarNotificacion(
            context = ctx,
            id = notifId,
            titulo = titulo,
            cuerpo = mensaje,
            canalId = canalId,
            pendingIntent = pendingIntent
        )
    }
}
