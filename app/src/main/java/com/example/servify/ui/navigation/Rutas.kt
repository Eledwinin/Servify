package com.example.servify.ui.navigation

import android.net.Uri

sealed class Rutas(val ruta: String) {
    // Pantallas públicas
    object Login : Rutas("login")
    object RecuperarPassword : Rutas("recuperar_password")
    object SeleccionRol : Rutas("seleccion_rol")
    object RegistroCliente : Rutas("registro_cliente")
    object RegistroTrabajador : Rutas("registro_trabajador")

    // Pestañas principales
    object Home : Rutas("home")
    object Actividad : Rutas("actividad")
    object Mensajes : Rutas("mensajes")
    object Perfil : Rutas("perfil")
    object MuroSolicitudes : Rutas("muro_solicitudes")
    object NuevaSolicitud : Rutas("nueva_solicitud")

    // Subflujos de Perfil separados
    object EditarPerfilCliente : Rutas("editar_perfil_cliente")
    object EditarPerfilTrabajador : Rutas("editar_perfil_trabajador")
    object Resenas : Rutas("resenas")

    // Checkout de Pago con Tarjeta (Cliente)
    object CheckoutPago : Rutas("checkout_pago?solicitudId={solicitudId}") {
        fun crearRuta(solicitudId: Int) = "checkout_pago?solicitudId=$solicitudId"
    }

    // Detalle Técnico con soporte para Int o String
    object DetalleTecnico : Rutas("detalle_tecnico/{idTecnico}") {
        fun crearRuta(idTecnico: Int) = "detalle_tecnico/$idTecnico"
        fun crearRuta(idTecnico: String) = "detalle_tecnico/$idTecnico"
    }

    object PropuestasSolicitud : Rutas("propuestas_solicitud/{solicitudId}") {
        fun crearRuta(solicitudId: Int) = "propuestas_solicitud/$solicitudId"
    }

    object Chat : Rutas("chat/{conversacionId}?nombre={nombre}") {
        fun crearRuta(conversacionId: Int, nombre: String) =
            "chat/$conversacionId?nombre=${Uri.encode(nombre)}"
    }

    // ==========================================
    // FLUJO DE MONETIZACIÓN Y PLANES (TRABAJADOR)
    // ==========================================
    object PlanesMembresia : Rutas("planes_membresia")

    object MetodosPago : Rutas("metodos_pago/{monto}/{tipoPlan}") {
        fun crearRuta(monto: Double, tipoPlan: String) =
            "metodos_pago/$monto/${Uri.encode(tipoPlan)}"
    }

    object PagoExitoso : Rutas("pago_exitoso/{monto}/{tipoPlan}?metodo={metodo}") {
        fun crearRuta(monto: Double, tipoPlan: String, metodo: String = "Visa •••• 4242") =
            "pago_exitoso/$monto/${Uri.encode(tipoPlan)}?metodo=${Uri.encode(metodo)}"
    }

    // Módulo Administrador
    object AdminDashboard : Rutas("admin_dashboard")
    object AdminUsuarios : Rutas("admin_usuarios?rol={rol}") {
        fun crearRuta(rol: String = "") = "admin_usuarios?rol=$rol"
    }
    object AdminSolicitudes : Rutas("admin_solicitudes")

    // Mapa con soporte de parámetros opcionales para solicitudes
    object Mapa : Rutas("mapa_screen?lat={lat}&lng={lng}&titulo={titulo}") {
        fun crearRuta(lat: Double, lng: Double, titulo: String) =
            "mapa_screen?lat=$lat&lng=$lng&titulo=${Uri.encode(titulo)}"
    }

    // Mapa de Solicitudes (Trabajador)
    object MapaSolicitudes : Rutas("mapa_solicitudes")

    // Para llamadas y videollamadas
    object Call : Rutas("call_screen/{channelName}/{esVideo}") {
        fun crearRuta(channelName: String, esVideo: Boolean) = "call_screen/$channelName/$esVideo"
    }
}