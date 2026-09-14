package com.example.servify.ui.navigation

sealed class Rutas(val ruta: String) {
    // Pantallas públicas
    object Login : Rutas("login")
    object Registro : Rutas("registro")

    // Pestañas principales
    object Home : Rutas("home")
    object Actividad : Rutas("actividad")
    object Mensajes : Rutas("mensajes")
    object Perfil : Rutas("perfil")

    // Subflujos
    object CheckoutPago : Rutas("checkout_pago")
    object DetalleTecnico : Rutas("detalle_tecnico/{tecnicoId}") {
        fun crearRuta(tecnicoId: String) = "detalle_tecnico/$tecnicoId"
    }
}