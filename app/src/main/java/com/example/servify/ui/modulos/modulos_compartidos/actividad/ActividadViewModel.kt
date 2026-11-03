package com.example.servify.ui.modulos.modulos_compartidos.actividad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.MiPostulacion
import kotlinx.coroutines.launch

class ActividadViewModel : ViewModel() {
    var postulaciones by mutableStateOf<List<MiPostulacion>>(emptyList())
    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // Set en memoria para notificar cada postulación una sola vez durante la sesión
    private val postulacionesNotificadas = mutableSetOf<Int>()

    fun cargarPostulaciones(context: Context? = null) {
        val token = SessionManager.token ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerMisPostulaciones("Bearer $token")
                if (res.isSuccessful) {
                    val lista = res.body() ?: emptyList()
                    postulaciones = lista

                    // Evaluar notificaciones si tenemos el contexto
                    context?.let { ctx ->
                        evaluarNotificacionesAceptadas(ctx, lista)
                    }
                } else {
                    Log.e("ACTIVIDAD", "Código ${res.code()} - ${res.errorBody()?.string()}")
                    mensajeError = "Error ${res.code()} al cargar tus postulaciones"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun evaluarNotificacionesAceptadas(context: Context, lista: List<MiPostulacion>) {
        lista.forEach { p ->
            val estadoNormalizado = p.estado.lowercase()

            // Si la postulación fue aceptada y aún no la hemos notificado en esta sesión
            if (estadoNormalizado == "aceptada" && !postulacionesNotificadas.contains(p.id)) {
                Log.d("NOTIFICACION", "Disparando notificación para postulación aceptada: ${p.titulo}")
                lanzarNotificacion(context, p.titulo)
                postulacionesNotificadas.add(p.id)
            }
        }
    }

    private fun lanzarNotificacion(context: Context, tituloTrabajo: String) {
        val channelId = "servify_postulaciones_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificaciones (Requerido para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                channelId,
                "Postulaciones Aceptadas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de propuestas aceptadas por los clientes"
            }
            manager.createNotificationChannel(canal)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("¡Postulación Aceptada! 🎉")
            .setContentText("Tu propuesta para '$tituloTrabajo' ha sido aceptada.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}