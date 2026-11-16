package com.example.servify.ui.componentes.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.servify.ui.theme.*

@Composable
fun DialogoPoliticaPrivacidad(
    onDismiss: () -> Unit,
    onAceptar: () -> Unit
) {
    val titulo = "Política de Privacidad"
    val subtitulo = "Protección de Datos y Marco Legal - Servify"

    val contenidoPoliticas = """
        POLITICAS DE PRIVACIDAD Y MARCO LEGAL - SERVIFY
        
        1. Responsable y Principio de Minimización
        El tratamiento de datos es gestionado por Servify conforme a la Ley para la Protección de Datos Personales de El Salvador. Solo se recopilan los datos estrictamente necesarios para el funcionamiento de la app:
        - Datos básicos (Nombre, correo, teléfono): Usados para identificar la cuenta y coordinar servicios. El correo y teléfono cuentan con restricciones estrictas de visibilidad pública.
        - Contraseñas: Guardadas de forma cifrada irreversible (hash con bcrypt).
        - Ubicación GPS: Requerida mediante permisos de Android solo al usar la app para calcular distancias y mostrar cercanía; la contraparte nunca ve un rastreo en tiempo real continuo.
        - Multimedia y Registros: Fotos de solicitudes/portafolio, mensajes y metadatos de llamadas almacenados bajo cifrado y estrictos controles de acceso.
        No se solicitan datos sensibles especiales (origen étnico, salud, creencias, vida sexual, datos biométricos). Se pide a los usuarios no incluirlos en mensajes, descripciones o fotos.
        
        2. Seguridad Técnica y Proveedores (Terceros)
        Medidas técnicas aplicadas en la plataforma:
        • Contraseñas almacenadas con cifrado irreversible (hash con bcrypt).
        • Sesiones mediante tokens firmados (JWT) con vencimiento.
        • Comunicación entre la app y el servidor, y entre el servidor y la base de datos, mediante conexiones cifradas.
        • Control de acceso en el servidor: cada usuario solo puede ver sus propias conversaciones, solicitudes y postulaciones; el servidor verifica la pertenencia en cada operación y nunca confía en el identificador enviado por la app.
        • Llamadas protegidas con tokens temporales generados por el servidor; el certificado de acceso no se almacena en la app.
        • Claves y credenciales guardadas como variables de entorno, fuera del código fuente.
        • Imágenes alojadas en un servicio especializado; en la base de datos solo se guarda la URL.
        
        3. Confidencialidad entre usuarios.
        3.1 Lo que cada usuario ve de otro se limita a lo necesario: el técnico ve el nombre del cliente, su calificación, la descripción y fotos de la solicitud y su distancia; el cliente ve el perfil profesional del técnico. El correo y la contraseña nunca se muestran a la contraparte.
        3.2 Los usuarios se comprometen a no divulgar, copiar ni usar con otros fines los datos personales de la otra parte que conozcan por el servicio (por ejemplo, la dirección del cliente o los mensajes del chat).
        3.3 Las capturas de pantalla o grabaciones de datos de terceros para difundirlos pueden constituir infracciones legales.
        
        4. Derechos ARCO y Conservación
        - 4.1 Los usuarios conservan en todo momento sus Derechos ARCO (Acceso, Rectificación, Supresión y Oposición), los cuales pueden gestionar desde el soporte de la app (servify124@gmail.com).
        - 4.2 Al eliminar una cuenta, se borran los datos del perfil y sus archivos asociados, salvo restricciones exigidas por ley o procesos judiciales vigentes.
        
        5. Menores de edad
        5.1 Servify não está dirigida a menores de 18 años ni recopila a sabiendas sus datos. Si se detecta una cuenta de un menor, se eliminará.
        
        6. Cambios y contacto
        6.1 Esta Política puede actualizarse. Se avisará de los cambios relevantes dentro de la app. Contacto de privacidad: servify124@gmail.co.
        
        MARCO LEGAL Y RESPONSABILIDADES PENALES
        
        1. Responsabilidades penales y legales de los usuarios
        1.1 Cada usuario responde personalmente, civil y penalmente, por los actos que realice usando la plataforma. Entre otros, pueden constituir delito o infracción:
        • Estafa o fraude entre cliente y técnico (cobros falsos, trabajos no realizados, ofertas engañosas).
        • Amenazas, acoso o coacción por chat o llamada.
        • Suplantación de identidad o creación de perfiles falsos.
        • Acceso no autorizado a cuentas o sistemas, o uso indebido de datos de otros usuarios.
        • Daños o hurtos durante la prestación de un servicio en un domicilio.
        • Difusión de imágenes, audios o datos privados sin consentimiento.
        1.2 El técnico responde por los daños y por la falta de licencias exigidas para su oficio; el cliente, por la veracidad de lo que publica y por la seguridad del lugar.
        
        2. Responsabilidades del equipo de Servify
        2.1 Servify, como responsable del tratamiento, asume los siguientes deberes:
        • Tratar los datos de forma lícita, con una finalidad definida y solo los necesarios.
        • Adoptar medidas razonables de seguridad y mantener la confidencialidad.
        • Informar al usuario sobre qué datos se recogen y obtener su consentimiento (incluida la ubicación).
        • Atender los derechos de los titulares y notificar los incidentes de seguridad.
        • No usar los datos con fines distintos, no venderlos y no permitir su acceso indebido.
        • Cooperar con las autoridades ante requerimientos legales válidos, entregando solo lo solicitado.
        2.2 El incumplimiento puede dar lugar a sanciones administrativas (la ley prevé multas para las infracciones más graves), a responsabilidad civil por daños y, si hay dolo, a responsabilidad penal por los delitos informáticos aplicables.
        2.3 El personal con acceso a datos (administradores, desarrolladores) responde individualmente por el uso indebido de la información.
        
        3. Reportes y denuncias
        3.1 Los usuarios podrán reportar conductas indebidas a servify124@gmail.com. Servify podrá suspender la cuenta de manera preventiva y conservar la evidencia necesaria para ponerla a disposición de las autoridades.
        3.2 Servify no sustituye a la Policía Nacional Civil, la Fiscalía General de la República ni a los tribunales; las víctimas pueden acudir directamente a ellos.
        """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = ServifySurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono representativo de Privacidad
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ServifyBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = null,
                        tint = ServifyBluePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = titulo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
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

                // Caja de Texto Amplia con Scroll
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = ServifyBackground,
                    border = BorderStroke(1.dp, ServifyBorder.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = contenidoPoliticas,
                                fontSize = 13.sp,
                                lineHeight = 21.sp,
                                color = ServifyTextBody
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de Acción
                Button(
                    onClick = onAceptar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Aceptar Políticas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cerrar",
                        color = ServifyTextMuted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}