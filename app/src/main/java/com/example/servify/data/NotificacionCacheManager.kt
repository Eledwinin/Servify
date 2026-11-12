package com.example.servify.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Gestor de caché y control de deduplicación de notificaciones para Servify.
 * Previene el re-disparo de notificaciones ya procesadas o mostradas,
 * persistiendo los identificadores de eventos mostrados en SharedPreferences
 * y manteniéndolos en memoria para comprobación instantánea O(1).
 */
object NotificacionCacheManager {

    private const val TAG = "NotifCacheManager"
    private const val PREFS_NAME = "servify_notificaciones_dedup_cache"
    private const val KEY_PROCESSED_SET = "processed_notification_keys"
    private const val MAX_CACHE_SIZE = 300

    private val memoriaCache = HashSet<String>()
    private var prefs: SharedPreferences? = null

    /**
     * Inicializa el gestor de caché cargando las claves previamente registradas en disco.
     */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val guardados = prefs?.getStringSet(KEY_PROCESSED_SET, emptySet()) ?: emptySet()
            synchronized(memoriaCache) {
                memoriaCache.clear()
                memoriaCache.addAll(guardados)
            }
            Log.d(TAG, "NotificacionCacheManager inicializado con ${memoriaCache.size} claves en caché.")
        }
    }

    /**
     * Genera una clave única determinista para identificar el evento específico.
     * Para eventos con ID de solicitud/entidad (ej. postulaciones, cotizaciones, pagos),
     * vincula el tipo de evento con el ID para evitar disparar el mismo aviso más de una vez.
     */
    fun generarClave(tipo: String, extraId: Int, mensaje: String = ""): String {
        return if (extraId > 0) {
            "${tipo}_$extraId"
        } else {
            "${tipo}_${mensaje.hashCode()}"
        }
    }

    /**
     * Comprueba si una clave de notificación ya fue procesada y mostrada.
     */
    fun yaFueProcesada(clave: String): Boolean {
        synchronized(memoriaCache) {
            return memoriaCache.contains(clave)
        }
    }

    /**
     * Registra una clave de notificación como procesada para evitar duplicaciones futuras.
     */
    fun registrarNotificacion(clave: String) {
        synchronized(memoriaCache) {
            if (memoriaCache.size >= MAX_CACHE_SIZE) {
                // Si la caché excede el límite máximo, podamos los más antiguos para no saturar memoria/disco
                val itemsToRemove = memoriaCache.take(MAX_CACHE_SIZE / 2)
                memoriaCache.removeAll(itemsToRemove.toSet())
            }
            memoriaCache.add(clave)
            prefs?.edit()?.putStringSet(KEY_PROCESSED_SET, HashSet(memoriaCache))?.apply()
        }
        Log.d(TAG, "Notificación registrada en caché de deduplicación: $clave")
    }

    /**
     * Limpia la caché si es necesario (ej. al cerrar sesión).
     */
    fun limpiarCache() {
        synchronized(memoriaCache) {
            memoriaCache.clear()
            prefs?.edit()?.clear()?.apply()
        }
        Log.d(TAG, "Caché de deduplicación limpiada.")
    }
}
