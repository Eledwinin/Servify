package com.example.servify.ui.modulos.modulos_compartidos.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticasdePrivacidadLegalScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val tituloPrincipal = "Política de Privacidad y Marco Legal"
    val subtitulo = "Protección de Datos y Normativa - Servify"

    // Lista estructurada de bloques para dar formato y jerarquía visual profesional
    val seccionesPoliticas = listOf(
        "POLITICAS DE PRIVACIDAD Y MARCO LEGAL - SERVIFY",

        "1. Responsable y Principio de Minimización\nEl tratamiento de datos es gestionado por Servify conforme a la Ley para la Protección de Datos Personales de El Salvador. Solo se recopilan los datos estrictamente necesarios para el funcionamiento de la app:",
        "- Datos básicos (Nombre, correo, teléfono): Usados para identificar la cuenta y coordinar servicios. El correo y teléfono cuentan con restricciones estrictas de visibilidad pública.",
        "- Contraseñas: Guardadas de forma cifrada irreversible (hash con bcrypt).",
        "- Ubicación GPS: Requerida mediante permisos de Android solo al usar la app para calcular distancias y mostrar cercanía; la contraparte nunca ve un rastreo en tiempo real continuo.",
        "- Multimedia y Registros: Fotos de solicitudes/portafolio, mensajes y metadatos de llamadas almacenados bajo cifrado y estrictos controles de acceso.",
        "No se solicitan datos sensibles especiales (origen étnico, salud, creencias, vida sexual, datos biométricos). Se pide a los usuarios no incluirlos en mensajes, descripciones o fotos.",

        "2. Seguridad Técnica y Proveedores (Terceros)\nMedidas técnicas aplicadas en la plataforma:",
        "- Contraseñas almacenadas con cifrado irreversible (hash con bcrypt).",
        "- Sesiones mediante tokens firmados (JWT) con vencimiento.",
        "- Comunicación entre la app y el servidor, y entre el servidor y la base de datos, mediante conexiones cifradas.",
        "- Control de acceso en el servidor: cada usuario solo puede ver sus propias conversaciones, solicitudes y postulaciones; el servidor verifica la pertenencia en cada operación y nunca confía en el identificador enviado por la app.",
        "- Llamadas protegidas con tokens temporales generados por el servidor; el certificado de acceso no se almacena en la app.",
        "- Claves y credenciales guardadas como variables de entorno, fuera del código fuente.",
        "- Imágenes alojadas en un servicio especializado; en la base de datos solo se guarda la URL.",

        "3. Confidencialidad entre usuarios\n3.1 Lo que cada usuario ve de otro se limita a lo necesario: el técnico ve el nombre del cliente, su calificación, la descripción y fotos de la solicitud y su distancia; el cliente ve el perfil profesional del técnico. El correo y la contraseña nunca se muestran a la contraparte.\n3.2 Los usuarios se comprometen a no divulgar, copiar ni usar con otros fines los datos personales de la otra parte que conozcan por el servicio (por ejemplo, la dirección del cliente o los mensajes del chat).\n3.3 Las capturas de pantalla o grabaciones de datos de terceros para difundirlos pueden constituir infracciones legales.",

        "4. Derechos ARCO y Conservación",
        "4.1 Los usuarios conservan en todo momento sus Derechos ARCO (Acceso, Rectificación, Supresión y Oposición), los cuales pueden gestionar desde el soporte de la app (servify124@gmail.com).",
        "4.2 Al eliminar una cuenta, se borran los datos del perfil y sus archivos asociados, salvo restricciones exigidas por ley o procesos judiciales vigentes.",

        "5. Menores de edad\n5.1 Servify no está dirigida a menores de 18 años ni recopila a sabiendas sus datos. Si se detecta una cuenta de un menor, se eliminará.",

        "6. Cambios y contacto\n6.1 Esta Política puede actualizarse. Se avisará de los cambios relevantes dentro de la app. Contacto de privacidad: servify124@gmail.com.",

        "MARCO LEGAL Y RESPONSABILIDADES PENALES",

        "1. Responsabilidades penales y legales de los usuarios\n1.1 Cada usuario responde personalmente, civil y penalmente, por los actos que realice usando la plataforma. Entre otros, pueden constituir delito o infracción:",
        "- Estafa o fraude entre cliente y técnico (cobros falsos, trabajos no realizados, ofertas engañosas).",
        "- Amenazas, acoso o coacción por chat o llamada.",
        "- Suplantación de identidad o creación de perfiles falsos.",
        "- Acceso no autorizado a cuentas o sistemas, o uso indebido de datos de otros usuarios.",
        "- Daños o hurtos durante la prestación de un servicio en un domicilio.",
        "- Difusión de imágenes, audios o datos privados sin consentimiento.",
        "1.2 El técnico responde por los daños y por la falta de licencias exigidas para su oficio; el cliente, por la veracidad de lo que publica y por la seguridad del lugar.",

        "2. Responsabilidades del equipo de Servify\n2.1 Servify, como responsable del tratamiento, asume los siguientes deberes:",
        "- Tratar los datos de forma lícita, con una finalidad definida y solo los necesarios.",
        "- Adoptar medidas razonables de seguridad y mantener la confidencialidad.",
        "- Informar al usuario sobre qué datos se recogen y obtener su consentimiento (incluida la ubicación).",
        "- Atender los derechos de los titulares y notificar los incidentes de seguridad.",
        "- No usar los datos con fines distintos, no venderlos y no permitir su acceso indebido.",
        "- Cooperar con las autoridades ante requerimientos legales válidos, entregando solo lo solicitado.",
        "2.2 El incumplimiento puede dar lugar a sanciones administrativas (la ley prevé multas para las infracciones más graves), a responsabilidad civil por daños y, si hay dolo, a responsabilidad penal por los delitos informáticos aplicables.\n2.3 El personal con acceso a datos (administradores, desarrolladores) responde individualmente por el uso indebido de la información.",

        "3. Reportes y denuncias\n3.1 Los usuarios podrán reportar conductas indebidas a servify124@gmail.com. Servify podrá suspender la cuenta de manera preventiva y conservar la evidencia necesaria para ponerla a disposición de las autoridades.\n3.2 Servify no sustituye a la Policía Nacional Civil, la Fiscalía General de la República ni a los tribunales; las víctimas pueden acudir directamente a ellos."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Política de Privacidad", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado con Icono Representativo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ServifyBluePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = null,
                    tint = ServifyBluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tituloPrincipal,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ServifyTextTitle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitulo,
                fontSize = 13.sp,
                color = ServifyTextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta Contenedor con Estilo Limpio (Surface)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ServifySurface,
                border = BorderStroke(1.dp, ServifyBorder.copy(alpha = 0.5f)),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    seccionesPoliticas.forEach { bloque ->
                        val esTituloSeccion = bloque.startsWith("POLITICAS") || bloque.startsWith("MARCO LEGAL")
                        val esViñeta = bloque.startsWith("-")
                        val paddingTop = if (esTituloSeccion) 16.dp else 8.dp

                        Text(
                            text = bloque,
                            fontSize = 13.5.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (esTituloSeccion) ServifyBluePrimary else ServifyTextBody,
                            textAlign = if (esViñeta) TextAlign.Start else TextAlign.Justify,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = paddingTop, bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}