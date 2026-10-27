package com.example.servify.ui.modulos.cliente.contratacion

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.CrearSolicitudRequest
import com.example.servify.data.uriAJpegBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class NuevaSolicitudViewModel : ViewModel() {
    var categoria by mutableStateOf("")
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var presupuesto by mutableStateOf("")

    val fotos = mutableStateListOf<Uri>()

    var isLoading by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var solicitudExitosa by mutableStateOf(false)

    companion object { const val MAX_FOTOS = 5 }

    fun agregarFotos(nuevas: List<Uri>) {
        nuevas.forEach { if (fotos.size < MAX_FOTOS && it !in fotos) fotos.add(it) }
    }

    fun quitarFoto(uri: Uri) { fotos.remove(uri) }

    fun enviarSolicitud(tokenParam: String, resolver: ContentResolver) {
        val tokenValido = if (tokenParam.isNotBlank()) tokenParam else SessionManager.token ?: ""

        if (tokenValido.isBlank()) {
            mensajeError = "No hay sesión activa. Por favor, inicia sesión de nuevo."
            return
        }
        if (titulo.isBlank() || descripcion.isBlank() || categoria.isBlank()) {
            mensajeError = "Por favor llena la categoría, el título y la descripción."
            return
        }

        val lat = SessionManager.latitud
        val lng = SessionManager.longitud
        if (lat == null || lng == null) {
            mensajeError = "No se pudo obtener tu ubicación. Activa el GPS e inténtalo de nuevo."
            return
        }

        val categoriaId = when (categoria) {
            "Plomería" -> 1
            "Electricidad" -> 2
            "Cerrajería" -> 3
            "Carpintería" -> 4
            "Refrigeración" -> 5
            "Mantenimiento de Cómputo" -> 6
            else -> 1
        }

        val authHeader = if (tokenValido.startsWith("Bearer ")) tokenValido else "Bearer $tokenValido"

        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                val request = CrearSolicitudRequest(
                    categoriaId = categoriaId,
                    titulo = titulo,
                    descripcion = descripcion,
                    presupuesto = presupuesto.toDoubleOrNull(),
                    latitud = lat,
                    longitud = lng
                )

                val respuesta = RetrofitClient.instance.crearSolicitud(authHeader, request)

                if (!respuesta.isSuccessful) {
                    val errorReal = respuesta.errorBody()?.string() ?: "Error desconocido"
                    mensajeError = "Fallo del servidor: ${respuesta.code()} - $errorReal"
                    return@launch
                }

                val solicitudId = respuesta.body()?.id

                if (fotos.isNotEmpty()) {
                    if (solicitudId == null) {
                        mensajeError = "Sin id. Mensaje del servidor: '${respuesta.body()?.mensaje}'"
                        return@launch
                    }
                    val ok = subirFotos(authHeader, solicitudId, resolver)
                    if (!ok) {
                        mensajeError = "La solicitud se creó, pero no se pudieron subir las fotos."
                        return@launch
                    }
                }

                solicitudExitosa = true
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun subirFotos(auth: String, solicitudId: Int, resolver: ContentResolver): Boolean {
        return try {
            android.util.Log.d("DEBUG_FOTOS", "Iniciando subida. Total de fotos seleccionadas: ${fotos.size}")

            // Comprimir fuera del hilo principal
            val partes = withContext(Dispatchers.IO) {
                fotos.mapIndexedNotNull { i, uri ->
                    android.util.Log.d("DEBUG_FOTOS", "Procesando foto $i con Uri: $uri")
                    uriAJpegBytes(resolver, uri)?.let { bytes ->
                        android.util.Log.d("DEBUG_FOTOS", "Foto $i comprimida con éxito. Tamaño en bytes: ${bytes.size}")
                        MultipartBody.Part.createFormData(
                            "fotos", "foto_$i.jpg",
                            bytes.toRequestBody("image/jpeg".toMediaType())
                        )
                    } ?: run {
                        android.util.Log.e("DEBUG_FOTOS", "Error: uriAJpegBytes devolvió null para la foto $i")
                        null
                    }
                }
            }

            if (partes.isEmpty()) {
                android.util.Log.e("DEBUG_FOTOS", "La lista de partes multipart quedó vacía. No se enviará nada.")
                return false
            }

            android.util.Log.d("DEBUG_FOTOS", "Enviando ${partes.size} partes al servidor para la solicitud ID: $solicitudId")
            val res = RetrofitClient.instance.subirFotos(auth, solicitudId, partes)

            if (!res.isSuccessful) {
                android.util.Log.e("FOTOS", "Error del servidor al subir fotos. Código ${res.code()} - ${res.errorBody()?.string()}")
            } else {
                android.util.Log.d("FOTOS", "¡Fotos subidas exitosamente al servidor!")
            }
            res.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("FOTOS", "Excepción atrapada al subir fotos: ${e.message}")
            false
        }
    }
}