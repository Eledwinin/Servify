package com.example.servify.data.repository

import android.content.Context
import android.net.Uri
import com.example.servify.data.api.ApiService
import com.example.servify.data.api.FotoPortafolioResponse
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.api.SubirFotoResponse
import com.example.servify.data.model.ActualizarPerfilRequest
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.model.SolicitudVipDto
import com.example.servify.data.model.UsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


class UsuarioRepository(
    private val apiService: ApiService = RetrofitClient.instance
) {
    suspend fun obtenerPerfil(token: String): Result<UsuarioModel> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getPerfil("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar perfil (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun actualizarPerfil(
        token: String,
        nombre: String,
        telefono: String?,
        direccion: String?
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val req = ActualizarPerfilRequest(nombre, telefono, direccion)
                val respuesta = apiService.actualizarPerfil("Bearer $token", req)
                if (respuesta.isSuccessful) {
                    Result.success(respuesta.body()?.mensaje ?: "Perfil actualizado correctamente")
                } else {
                    Result.failure(Exception("Error al guardar cambios (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun obtenerPerfilProfesional(id: String): Result<ProfesionalPerfil> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getPerfilProfesional(id)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar perfil (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun obtenerProfesionales(): Result<List<ProfesionalPerfil>> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.getProfesionales()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al cargar profesionales (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun actualizarEstadoVip(idUsuario: Int, esVip: Boolean): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.actualizarEstadoVip(idUsuario, SolicitudVipDto(esVip))
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!.esVip)
                } else {
                    Result.failure(Exception("Error al actualizar estado VIP (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // 1. ELIMINACIÓN DE CUENTA (Cliente y Técnico)
    suspend fun eliminarCuenta(token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.eliminarCuenta("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!.mensaje)
                } else {
                    Result.failure(Exception("Error al eliminar cuenta (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // 2. PORTAFOLIO DE TRABAJOS (Exclusivo Técnicos)

    suspend fun obtenerPortafolio(token: String): Result<List<FotoPortafolioResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.obtenerPortafolio("Bearer $token")
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener portafolio (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun subirFotoPortafolio(
        context: Context,
        token: String,
        uri: Uri
    ): Result<SubirFotoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val file = uriToFile(context, uri)
                    ?: return@withContext Result.failure(Exception("No se pudo procesar la imagen seleccionada"))

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)

                val respuesta = apiService.subirFotoPortafolio("Bearer $token", body)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!)
                } else {
                    Result.failure(Exception("Error al subir foto (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    suspend fun eliminarFotoPortafolio(token: String, fotoId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = apiService.eliminarFotoPortafolio("Bearer $token", fotoId)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    Result.success(respuesta.body()!!.mensaje)
                } else {
                    Result.failure(Exception("Error al eliminar foto (${respuesta.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Fallo de conexión: ${e.localizedMessage}"))
            }
        }
    }

    // ==========================================
    // FUNCIÓN AUXILIAR: URI A ARCHIVO
    // ==========================================
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("portfolio_temp_", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}