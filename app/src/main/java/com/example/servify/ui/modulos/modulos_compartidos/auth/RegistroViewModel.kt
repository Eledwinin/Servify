package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.Categoria
import com.example.servify.data.model.RegisterRequest
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                val response = RetrofitClient.instance.getCategories()
                Log.d("CATEGORIAS_DEBUG", "Status Code: ${response.code()}")

                if (response.isSuccessful) {
                    val lista = response.body()
                    Log.d("CATEGORIAS_DEBUG", "Categorias recibidas: $lista")
                    if (!lista.isNullOrEmpty()) {
                        _categorias.value = lista
                    } else {
                        Log.w("CATEGORIAS_DEBUG", "La lista llegó vacía o nula")
                    }
                } else {
                    val errorStr = response.errorBody()?.string()
                    Log.e("CATEGORIAS_DEBUG", "Error HTTP ${response.code()}: $errorStr")
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

                val response = repository.registrarUsuario(request)

                if (response.isSuccessful && response.body() != null) {
                    _estadoRegistro.value = RegistroState.Success(response.body()!!.mensaje)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al registrar"
                    _estadoRegistro.value = RegistroState.Error(errorBody)
                }
            } catch (e: Exception) {
                _estadoRegistro.value = RegistroState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}