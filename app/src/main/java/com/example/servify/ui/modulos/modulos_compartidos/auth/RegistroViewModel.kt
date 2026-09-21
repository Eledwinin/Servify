package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.api.RetrofitClient
import com.example.servify.data.model.RegisterRequest
import com.example.servify.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Representa los estados de la pantalla durante el proceso de registro
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
    val estadoRegistro: StateFlow<RegistroState> = _estadoRegistro

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
                    onSuccess() // Ejecutamos la navegación o acción exitosa
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

