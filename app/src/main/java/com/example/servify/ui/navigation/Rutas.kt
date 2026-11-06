package com.example.servify.ui.navigation

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

    object CheckoutPago : Rutas("checkout_pago")
    object DetalleTecnico : Rutas("detalle_tecnico/{tecnicoId}") {
        fun crearRuta(tecnicoId: String) = "detalle_tecnico/$tecnicoId"
    }

    object PropuestasSolicitud : Rutas("propuestas_solicitud/{solicitudId}") {
        fun crearRuta(solicitudId: Int) = "propuestas_solicitud/$solicitudId"
    }

    object Chat : Rutas("chat/{conversacionId}?nombre={nombre}") {
        fun crearRuta(conversacionId: Int, nombre: String) =
            "chat/$conversacionId?nombre=${android.net.Uri.encode(nombre)}"
    }

    object PlanesMembresia : Rutas("planes_membresia")
    object MetodosPago : Rutas("metodos_pago")
    object PagoExitoso : Rutas("pago_exitoso")

    // Módulo Administrador
    object AdminDashboard : Rutas("admin_dashboard")
    object AdminUsuarios : Rutas("admin_usuarios?rol={rol}") {
        fun crearRuta(rol: String = "") = "admin_usuarios?rol=$rol"
    }
    object AdminSolicitudes : Rutas("admin_solicitudes")
    object Mapa : Rutas(ruta = "mapa_screen")

    // Mapa de Solicitudes (Trabajador)
    object MapaSolicitudes : Rutas("mapa_solicitudes")

    //para llamadas y videollamadas
    object Call : Rutas("call_screen/{channelName}/{esVideo}") {
        fun crearRuta(channelName: String, esVideo: Boolean) = "call_screen/$channelName/$esVideo"
    }
}