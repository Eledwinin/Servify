package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.UsuarioModel
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    // --- Estados para selección de categoría (Técnico) ---
    var mostrarDialogoCategorias by mutableStateOf(false)
        private set

    var categoriasDisponibles by mutableStateOf<List<Categoria>>(emptyList())
        private set

    var cargandoCategorias by mutableStateOf(false)
        private set

    var usuarioPendienteTecnico by mutableStateOf<UsuarioModel?>(null)
        private set

    fun onCorreoChange(nuevoCorreo: String) {
        correo = nuevoCorreo
        if (mensajeError != null) mensajeError = null
    }

    fun onPasswordChange(nuevaPassword: String) {
        password = nuevaPassword
        if (mensajeError != null) mensajeError = null
    }

    private fun validarCampos(): Boolean {
        val emailTrim = correo.trim()
        if (emailTrim.isEmpty()) {
            mensajeError = "Por favor ingresa tu correo electrónico"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
            mensajeError = "El formato de correo no es válido"
            return false
        }
        if (password.isEmpty()) {
            mensajeError = "Por favor ingresa tu contraseña"
            return false
        }
        if (password.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }

    fun iniciarSesion(onSuccess: (UsuarioModel) -> Unit) {
        if (!validarCampos()) return

        cargando = true
        mensajeError = null

        viewModelScope.launch {
            val resultado = repository.iniciarSesion(correo.trim(), password)
            cargando = false

            resultado.fold(
                onSuccess = { usuario ->
                    val esRolTecnico = usuario.rol.equals("tecnico", ignoreCase = true) ||
                            usuario.rol.equals("trabajador", ignoreCase = true)

                    if (esRolTecnico) {
                        // Guardamos temporalmente el usuario y cargamos las categorías
                        usuarioPendienteTecnico = usuario
                        cargarCategoriasParaTecnico()
                    } else {
                        // Admin o Cliente entran directamente
                        onSuccess(usuario)
                    }
                },
                onFailure = { error ->
                    mensajeError = error.message ?: "Ocurrió un error inesperado al conectar"
                }
            )
        }
    }

    private fun cargarCategoriasParaTecnico() {
        cargandoCategorias = true
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.instance.getCategories()
                if (respuesta.isSuccessful && !respuesta.body().isNullOrEmpty()) {
                    categoriasDisponibles = respuesta.body()!!
                    mostrarDialogoCategorias = true
                } else {
                    mensajeError = "No se pudieron obtener las categorías disponibles"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión al cargar categorías: ${e.localizedMessage}"
            } finally {
                cargandoCategorias = false
            }
        }
    }

    fun cerrarDialogoCategorias() {
        mostrarDialogoCategorias = false
        usuarioPendienteTecnico = null
    }

    fun confirmarCategoriaTecnico(categoria: Categoria, onCompletado: (UsuarioModel, Categoria) -> Unit) {
        val usuario = usuarioPendienteTecnico ?: return
        mostrarDialogoCategorias = false
        usuarioPendienteTecnico = null
        onCompletado(usuario, categoria)
    }
}