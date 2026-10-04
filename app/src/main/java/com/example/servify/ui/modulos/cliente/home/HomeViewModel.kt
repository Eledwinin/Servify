package com.example.servify.ui.modulos.cliente.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.model.ProfesionalPerfil
import com.example.servify.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private var listaOriginal: List<ProfesionalPerfil> = emptyList()

    private val _listaProfesionales = MutableStateFlow<List<ProfesionalPerfil>>(emptyList())
    val listaProfesionales: StateFlow<List<ProfesionalPerfil>> = _listaProfesionales.asStateFlow()

    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    var latCliente: Double = 0.0
        private set
    var lngCliente: Double = 0.0
        private set

    init {
        cargarTecnicos()
    }

    fun cargarTecnicos() {
        viewModelScope.launch {
            _estaCargando.value = true
            _mensajeError.value = null
            val resultado = repository.obtenerProfesionales()
            resultado.onSuccess { data ->
                listaOriginal = data
                ordenarPorDistancia()
                _estaCargando.value = false
            }.onFailure { error ->
                _mensajeError.value = error.localizedMessage
                _estaCargando.value = false
            }
        }
    }

    fun actualizarUbicacionCliente(lat: Double, lng: Double) {
        latCliente = lat
        lngCliente = lng
        ordenarPorDistancia()
    }

    private fun ordenarPorDistancia() {
        if (latCliente == 0.0 || lngCliente == 0.0 || listaOriginal.isEmpty()) {
            _listaProfesionales.value = listaOriginal
            return
        }

        _listaProfesionales.value = listaOriginal.sortedBy { tecnico ->
            calcularDistanciaMetros(tecnico.latitud, tecnico.longitud)
        }
    }

    fun calcularDistanciaMetros(latTecnico: Double?, lngTecnico: Double?): Float {
        if (latCliente == 0.0 || lngCliente == 0.0 || latTecnico == null || lngTecnico == null) {
            return Float.MAX_VALUE
        }
        val resultado = FloatArray(1)
        Location.distanceBetween(latCliente, lngCliente, latTecnico, lngTecnico, resultado)
        return resultado[0]
    }
}