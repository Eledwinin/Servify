package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticasdePrivacidadLegalScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val contenidoTexto = """
        POLÍTICA DE PRIVACIDAD Y MARCO LEGAL - SERVIFY

        1. Responsable y Principio de Minimización
            El tratamiento de datos es gestionado por Servify conforme a la Ley para la Protección de Datos Personales de El Salvador. Solo se recopilan los datos estrictamente necesarios:
            • Datos básicos (Nombre, correo, teléfono): Usados para identificar la cuenta y coordinar servicios. El correo y teléfono cuentan con restricciones estrictas de visibilidad pública.
            • Contraseñas: Guardadas de forma cifrada irreversible (hash con bcrypt).
            • Ubicación GPS: Requerida mediante permisos de Android solo al usar la app para calcular distancias; la contraparte nunca ve un rastreo en tiempo real continuo.
            • Multimedia y Registros: Fotos, mensajes y metadatos de llamadas almacenados bajo cifrado. No se solicitan datos sensibles especiales.

        2. Seguridad Técnica y Proveedores (Terceros)
            Medidas aplicadas en la plataforma:
            • Sesiones mediante tokens firmados (JWT) con vencimiento.
            • Comunicaciones cifradas de extremo a extremo.
            • Control de acceso en el servidor: cada usuario solo ve sus propias conversaciones, solicitudes y postulaciones.
            • Llamadas protegidas con tokens temporales generados por el servidor.

        3. Confidencialidad entre Usuarios
            • Lo que cada usuario ve se limita a lo estrictamente necesario. El correo y la contraseña nunca se muestran a la contraparte.
            • Los usuarios se comprometen a no divulgar, copiar ni usar con otros fines los datos personales de la otra parte.
            • Las capturas de pantalla o grabaciones de datos de terceros para difundirlos pueden constituir infracciones legales.

        4. Derechos ARCO y Conservación
            • Los usuarios conservan en todo momento sus Derechos ARCO (Acceso, Rectificación, Supresión y Oposición), gestionables mediante el correo de soporte: servify124@gmail.com. 
            • Al eliminar una cuenta, se borran los datos del perfil salvo restricciones legales.
            
        5. Menores de edad
            • Servify no está dirigida a menores de 18 años ni recopila a sabiendas sus datos. Si se detecta una cuenta de un menor, se eliminará.

        6. Cambios y contacto
            • Esta Política puede actualizarse. Se avisará de los cambios relevantes dentro de la app. Contacto de privacidad: servify124@gmail.co.
    

        7. Responsabilidades penales y legales de los usuarios

            7.1  Cada usuario responde personalmente, civil y penalmente, por los actos que realice usando la plataforma. Entre otros, pueden constituir delito o infracción:
            •	Estafa o fraude entre cliente y técnico (cobros falsos, trabajos no realizados, ofertas engañosas).
            •	Amenazas, acoso o coacción por chat o llamada.
            •	Suplantación de identidad o creación de perfiles falsos.
            •	Acceso no autorizado a cuentas o sistemas, o uso indebido de datos de otros usuarios.
            •	Daños o hurtos durante la prestación de un servicio en un domicilio.
            •	Difusión de imágenes, audios o datos privados sin consentimiento.
            
            7.2 El técnico responde por los daños y por la falta de licencias exigidas para su oficio; el cliente, por la veracidad de lo que publica y por la seguridad del lugar.
            
        8. Responsabilidades del equipo de Servify
        
            8.1 Servify, como responsable del tratamiento, asume los siguientes deberes:
            •	Tratar los datos de forma lícita, con una finalidad definida y solo los necesarios.
            •	Adoptar medidas razonables de seguridad y mantener la confidencialidad.
            •	Informar al usuario sobre qué datos se recogen y obtener su consentimiento (incluida la ubicación).
            •	Atender los derechos de los titulares y notificar los incidentes de seguridad.
            •	No usar los datos con fines distintos, no venderlos y no permitir su acceso indebido.
            •	Cooperar con las autoridades ante requerimientos legales válidos, entregando solo lo solicitado.
            
            8.2 El incumplimiento puede dar lugar a sanciones administrativas (la ley prevé multas para las infracciones más graves), a responsabilidad civil por daños y, si hay dolo, a responsabilidad penal por los delitos informáticos aplicables.
            
            8.3 El personal con acceso a datos (administradores, desarrolladores) responde individualmente por el uso indebido de la información.
            
        9. Reportes y denuncias
        
            9.1 Los usuarios podrán reportar conductas indebidas a servify124@gmail.com. Servify podrá suspender la cuenta de manera preventiva y conservar la evidencia necesaria para ponerla a disposición de las autoridades.
            9.2 Servify no sustituye a la Policía Nacional Civil, la Fiscalía General de la República ni a los tribunales; las víctimas pueden acudir directamente a ellos.
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Política de Privacidad y Legal", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Política de Privacidad y Confidencialidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = contenidoTexto,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}