package com.example.servify.ui.modulos.cliente.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servify.data.SessionManager
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

    // Estados para Búsqueda y Filtro de Categoría
    private val _queryBusqueda = MutableStateFlow("")
    val queryBusqueda: StateFlow<String> = _queryBusqueda.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

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
                aplicarFiltrosYOrden()
                _estaCargando.value = false
            }.onFailure { error ->
                _mensajeError.value = error.localizedMessage
                _estaCargando.value = false
            }
        }
    }

    private val _esVipUsuario = MutableStateFlow(SessionManager.esVip)
    val esVipUsuario: StateFlow<Boolean> = _esVipUsuario.asStateFlow()

    fun refrescarEstadoSesion() {
        _esVipUsuario.value = SessionManager.esVip
    }

    fun actualizarUbicacionCliente(lat: Double, lng: Double) {
        latCliente = lat
        lngCliente = lng
        aplicarFiltrosYOrden()
    }

    // Funciones de actualización del buscador y categorías
    fun onQueryBusquedaChange(nuevaQuery: String) {
        _queryBusqueda.value = nuevaQuery
        aplicarFiltrosYOrden()
    }


    fun seleccionarCategoria(categoria: String?) {
        _categoriaSeleccionada.value = if (_categoriaSeleccionada.value == categoria) null else categoria
        aplicarFiltrosYOrden()
    }

    private fun aplicarFiltrosYOrden() {
        var listaFiltrada = listaOriginal

        // 1. Filtrar por texto de búsqueda (nombre u oficio)
        val query = _queryBusqueda.value.trim()
        if (query.isNotEmpty()) {
            listaFiltrada = listaFiltrada.filter { tecnico ->
                tecnico.nombreCompleto.contains(query, ignoreCase = true) ||
                        tecnico.oficio.contains(query, ignoreCase = true)
            }
        }

        // 2. Filtrar por categoría / oficio seleccionado
        val cat = _categoriaSeleccionada.value
        if (cat != null) {
            listaFiltrada = listaFiltrada.filter { tecnico ->
                tecnico.oficio.equals(cat, ignoreCase = true)
            }
        }

        // 3. Ordenar por distancia si hay coordenadas GPS activas
        if (latCliente != 0.0 && lngCliente != 0.0) {
            listaFiltrada = listaFiltrada.sortedBy { tecnico ->
                calcularDistanciaMetros(tecnico.latitud, tecnico.longitud)
            }
        }

        _listaProfesionales.value = listaFiltrada
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