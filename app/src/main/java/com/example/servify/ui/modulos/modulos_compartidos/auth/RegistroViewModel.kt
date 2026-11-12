package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.RegisterRequest
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    data class Success(val mensaje: String) : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}

class RegistroViewModel(
    private val repository: AuthRepository = AuthRepository(RetrofitClient.instance)
) : ViewModel() {

    private val _estadoRegistro = MutableStateFlow<RegistroState>(RegistroState.Idle)
    val estadoRegistro: StateFlow<RegistroState> = _estadoRegistro.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _cargandoCategorias = MutableStateFlow(false)
    val cargandoCategorias: StateFlow<Boolean> = _cargandoCategorias.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _cargandoCategorias.value = true
            try {
                val (lista, errorStr, code) = withContext(Dispatchers.IO) {
                    val response = RetrofitClient.instance.getCategories()
                    if (response.isSuccessful) {
                        Triple(response.body(), null, response.code())
                    } else {
                        val e = response.errorBody()?.string()
                        Triple(null, e, response.code())
                    }
                }
                Log.d("CATEGORIAS_DEBUG", "Status Code: $code")

                if (!lista.isNullOrEmpty()) {
                    Log.d("CATEGORIAS_DEBUG", "Categorias recibidas: $lista")
                    _categorias.value = lista
                } else if (errorStr != null) {
                    Log.e("CATEGORIAS_DEBUG", "Error HTTP $code: $errorStr")
                } else {
                    Log.w("CATEGORIAS_DEBUG", "La lista llegó vacía o nula")
                }
            } catch (e: Exception) {
                Log.e("CATEGORIAS_DEBUG", "Excepción al cargar categorías: ${e.message}", e)
            } finally {
                _cargandoCategorias.value = false
            }
        }
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        telefono: String,
        rol: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _estadoRegistro.value = RegistroState.Loading
            try {
                val request = RegisterRequest(
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    telefono = telefono,
                    rol = rol
                )

                val (isSuccess, mensajeExito, errorBody) = withContext(Dispatchers.IO) {
                    val response = repository.registrarUsuario(request)
                    if (response.isSuccessful && response.body() != null) {
                        Triple(true, response.body()!!.mensaje, null)
                    } else {
                        val err = response.errorBody()?.string() ?: "Error al registrar"
                        Triple(false, null, err)
                    }
                }

                if (isSuccess && mensajeExito != null) {
                    _estadoRegistro.value = RegistroState.Success(mensajeExito)
                    onSuccess()
                } else {
                    _estadoRegistro.value = RegistroState.Error(errorBody ?: "Error al registrar")
                }
            } catch (e: Exception) {
                _estadoRegistro.value = RegistroState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}
