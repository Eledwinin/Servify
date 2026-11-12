package com.example.servify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.servify.data.ServifyNotificationHelper
import com.example.servify.ui.navigation.AppNavigation
import com.example.servify.ui.theme.ServifyTheme

class MainActivity : ComponentActivity() {

    companion object {
        // Almacena la ruta de destino recibida desde un PendingIntent de notificación
        var rutaDestinoPendiente = mutableStateOf<String?>(null)
    }

    // Lanzador para solicitar permiso de notificaciones en Android 13+ (API 33+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            android.util.Log.d("NOTIF_PERMISSION", "Permiso POST_NOTIFICATIONS concedido por el usuario.")
        } else {
            android.util.Log.w("NOTIF_PERMISSION", "Permiso POST_NOTIFICATIONS denegado por el usuario.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar helper nativo de notificaciones y crear canales
        ServifyNotificationHelper.init(applicationContext)

        // Solicitar permiso en tiempo de ejecución para Android 13+
        solicitarPermisoNotificaciones()

        // Capturar intención si la app fue abierta desde una notificación
        procesarIntentNotificacion(intent)

        setContent {
            ServifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntentNotificacion(intent)
    }

    private fun procesarIntentNotificacion(intent: Intent?) {
        val ruta = intent?.getStringExtra("destino_ruta")
        if (!ruta.isNullOrBlank()) {
            android.util.Log.d("NOTIF_NAV", "Ruta de notificación detectada: $ruta")
            rutaDestinoPendiente.value = ruta
        }
    }

    fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val estado = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (estado != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
