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
fun TerminosCondicionesScreen(
    userRole: String, // Recibe el rol: "CLIENTE" o "TECNICO"
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val titulo = if (userRole == "TECNICO") "Términos y Condiciones - Técnico Socio" else "Términos y Condiciones - Cliente"

    val contenidoTexto = if (userRole == "TECNICO") {
        """
        PARTE 2: TÉRMINOS Y CONDICIONES: PROFESIONAL - SERVIFY 
            
        Artículo 1. Naturaleza del Vínculo y Verificación de Identidad
        Al registrarte como proveedor independiente en Servify, aceptas someterte a los protocolos obligatorios de validación de identidad para garantizar la seguridad del ecosistema y la confianza del usuario final.

        Artículo 2. Declaraciones y Requisitos del Profesional
        Declaras y garantizas formalmente que:
            - Cuentas con la capacidad, conocimientos y, cuando la ley de la República de El Salvador lo exija, los permisos, licencias o certificaciones oficiales para el servicio que ofreces.
            
            - La información proporcionada en tu perfil (oficio, años de experiencia, descripción y especialidades) es verdadera y comprobable.
            
            - Actúas como profesional independiente, por lo que eres el único responsable de tus impuestos, seguros y obligaciones legales o tributarias.


        Artículo 3. Postulaciones, Negociación y Ejecución de Servicios
            - Postulaciones: Debes postularte de buena fe, ofertando un precio y un tiempo que puedas cumplir responsablemente. No puedes postularte dos veces a una misma solicitud. Tiene la facultad de ignorar solicitudes, lo cual únicamente las ocultará de su vista.
            
            - Negociación: Puedes proponer en el chat un precio distinto al estimado inicialmente. Una oferta aceptada por ambas partes obliga a respetar rigurosamente dicho monto final.
            
            - Ejecución: Debes realizar la labor con diligencia profesional, acatar las normas de seguridad y dejar el área de trabajo limpia. Respondes civil y legalmente por daños causados por negligencia comprobada.


        Artículo 4. Ubicación Geográfica y Monitoreo GPS
        Autorizas de manera expresa el uso y rastreo de tu ubicación mediante el dispositivo móvil para calcular tu distancia exacta respecto a las solicitudes activas, optimizar la asignación de trabajos cercanos y permitir que el cliente verifique tu proximidad durante tu desplazamiento.

        Artículo 5. Comunicaciones en Vivo (Agora y WebRTC SDKs)
        El uso de las herramientas de comunicación (cámara, micrófono, chat y llamadas/videollamadas en tiempo real vía Agora y WebRTC) exige del Técnico un estricto apego a la ética profesional, el respeto absoluto a la propiedad privada y la confidencialidad total sobre la información, intimidad o bienes a los que tenga acceso en los domicilios de los Clientes.

        Artículo 6. Prohibiciones y Restricciones Operativas
        Queda terminantemente prohibido:
            - Cobrar montos distintos al acordado o solicitar pagos adelantados sin debida justificación.
            
            - Utilizar los datos personales del cliente para fines ajenos a la prestación del servicio.
            
            - Contactar al cliente fuera de los canales oficiales con fines de acoso, hostigamiento o publicidad no solicitada.
            
            - Ofrecer servicios ilícitos o de carácter peligroso.


        Artículo 7. Portafolio, Membresía Semanal y VIP
            - Portafolio: Las fotos de trabajos deben ser de tu autoría exclusiva. Otorgas a Servify una licencia limitada para exhibirlas en tu perfil público.
            
            - Membresías Semanales y VIP: Servify ofrece de manera optativa planes de membresía con renovación semanal o categoría VIP para potenciar la visibilidad del perfil, destacar en los listados de búsqueda y acceder a beneficios preferenciales de asignación. 
            
            - Condiciones de los Planes: Las tarifas aplicables, los métodos de pago autorizados, la vigencia exacta de cada ciclo semanal y las políticas de cancelación o renovación de estos planes se notificarán de forma transparente al técnico antes de confirmar su contratación. La falta de pago o vencimiento del período contratado retornará automáticamente el perfil a su visibilidad estándar sin derecho a reembolso de días no transcurridos.
        """.trimIndent()
    } else {
        """
           
        PARTE 2: TÉRMINOS Y CONDICIONES: CLIENTE - SERVIFY
        
        Artículo 1. Objeto y Aceptación de los Términos
        Los presentes términos regulan el uso de Servify por parte del Cliente. Al registrarte y presionar continuar, declaras formalmente que eres mayor de edad y otorgas tu consentimiento libre, previo e informado para el uso de la plataforma.

        Artículo 2. Recopilación y Finalidad de Datos de Contacto
        La plataforma recopila nombre completo, número telefónico activo y correo electrónico verificado para crear el perfil, notificar el estado de solicitudes y comunicarnos directamente con el soporte.

        Artículo 3. Geolocalización y Rastreo GPS
        Autorizas de manera expresa el acceso y rastreo de tu ubicación geográfica precisa mediante el GPS del dispositivo con los siguientes fines:
            - Ubicar con exactitud la dirección física donde requieres el servicio técnico.
            
            - Calcular distancias de proximidad y optimizar tiempos de llegada.
            
            - Conectarte con los técnicos calificados más cercanos a tu zona.


        Artículo 4. Comunicaciones en Vivo (Agora y WebRTC SDKs)
        Autorizas el uso de cámara y micrófono para llamadas y videollamadas en tiempo real:
            - Propósito: Evaluación técnica preliminar, recepción de presupuestos y diagnóstico de averías en vivo sin visitas presenciales inmediatas.
            
            - Privacidad garantizada: No se realizan grabaciones ni almacenamiento digital del contenido audiovisual, protegiendo la intimidad de tu hogar.


        Artículo 5. Obligaciones y Compromisos del Cliente
        Te comprometes a:
            - Describir solicitudes con claridad, honestidad y fotos reales.
            
            - Publicar exclusivamente servicios lícitos.
            
            - Tratar con respeto al técnico, estar disponible en la fecha acordada y permitir un acceso seguro.


        Artículo 6. Negociación, Acuerdos y Precios
            - Elección y acuerdo: Puedes aceptar o rechazar propuestas libremente. Al aceptar, se abre un chat exclusivo y la solicitud pasa a estado "en proceso".
            
            - Precio acordado: El monto aceptado de mutuo acuerdo en el chat es el valor final que las partes se comprometen a respetar.


        Artículo 7. Cancelaciones, Reseñas y Responsabilidad
            - Queda prohibido cancelar reiteradamente o dejar de responder al profesional. Las reseñas deben basarse en experiencias reales y respetuosas.
            
            - Eres el único responsable de verificar la identidad del técnico antes de permitirle el ingreso a tu domicilio.


        Artículo 8. Derechos ARCO
        Conservas tu derecho irrenunciable a ejercer tus Derechos ARCO (Acceso, Rectificación, Cancelación y Oposición) directamente a través del soporte de la app.
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Términos y Condiciones", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // PRIMERO: Llamamos a los términos generales
            TerminosGenerales()

            Spacer(modifier = Modifier.height(24.dp))

            // SEGUNDO: Imprimimos el contenido específico del rol
            Text(
                text = contenidoTexto,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@Composable
fun TerminosGenerales() {
    val textoGenerales = """
        TÉRMINOS Y CONDICIONES GENERALES DE USO - SERVIFY

        Artículo 1. Definiciones
        1.1 Servify («la Plataforma», «nosotros»): aplicación móvil que facilita el contacto entre personas que necesitan un servicio técnico y personas que lo ofrecen.
        1.2 Cliente: usuario que publica solicitudes de servicio y elige a un técnico.
        1.3 Técnico: usuario que ofrece servicios, consulta solicitudes y se postula a ellas con una propuesta.
        1.4 Administrador: personal autorizado de Servify con acceso a funciones de supervisión.
        1.5 Solicitud: descripción de un trabajo que el cliente requiere. Postulación: propuesta del técnico (precio, tiempo y mensaje). Acuerdo: monto final aceptado por ambas partes en el chat.
        1.6 Datos personales: toda información que identifica o hace identificable a una persona (por ejemplo nombre, teléfono, correo, ubicación).

        Artículo 2. Aceptación y requisitos para usar Servify
        2.1 Al crear una cuenta, el usuario declara que leyó y acepta estos Términos y la Política de Privacidad. Si no está de acuerdo, no debe registrarse ni usar la aplicación.
        2.2 Para registrarse se requiere ser mayor de 18 años y tener capacidad legal para obligarse. Servify no está dirigida a menores de edad.
        2.3 Cada persona puede tener una sola cuenta y debe elegir el rol que le corresponde (cliente o técnico).
        2.4 Servify guardará la fecha, la versión de los documentos y el rol con que el usuario aceptó, como constancia del consentimiento.

        Artículo 3. Naturaleza del servicio: Servify es un intermediario tecnológico
        3.1 Servify únicamente facilita el encuentro y la comunicación entre clientes y técnicos. No presta los servicios técnicos, no los supervisa y no es parte del contrato que se celebre entre ellos.
        3.2 Los técnicos son profesionales independientes. No existe relación laboral, de sociedad ni de representación entre Servify y los técnicos.
        3.3 Servify no garantiza la calidad, seguridad, legalidad o resultado de los trabajos, ni que un cliente reciba propuestas o que un técnico reciba trabajos.
        3.4 En la versión actual, los pagos por los trabajos se acuerdan y realizan directamente entre cliente y técnico. Servify no procesa, retiene ni garantiza pagos entre usuarios.

        Artículo 4. Cuenta y seguridad de acceso
        4.1 El usuario debe dar información veraz, completa y actualizada, y mantenerla al día.
        4.2 El usuario es responsable de la confidencialidad de su contraseña y de toda actividad realizada desde su cuenta. Debe avisar de inmediato al soporte de la aplicación si sospecha un uso no autorizado.
        4.3 Está prohibido suplantar la identidad de otra persona, crear cuentas con datos falsos o ceder la cuenta a terceros.

        Artículo 5. Comunicaciones: chat y llamadas
        5.1 El chat y las llamadas de voz o video son para coordinar el servicio. Deben usarse con respeto; están prohibidos el acoso, las amenazas, el contenido sexual, discriminatorio o ilegal y el envío de publicidad no solicitada.
        5.2 Los mensajes se almacenan para que los participantes puedan consultar el historial. Cada llamada deja un registro en el chat (tipo, resultado y duración). Servify no graba el audio ni el video de las llamadas.
        5.3 Al cancelar un acuerdo, el chat queda deshabilitado pero los mensajes se conservan. Cada participante decide si vacía o elimina el chat de su lista; vaciar solo afecta a quien lo hace.
        5.4 Ningún usuario puede grabar o difundir conversaciones, llamadas o imágenes de otra persona sin su consentimiento.

        Artículo 6. Contenido del usuario y reseñas
        6.1 El usuario conserva la titularidad del contenido que sube (fotos, textos, reseñas) y declara tener derecho a hacerlo.
        6.2 Otorga a Servify una licencia no exclusiva y limitada para almacenarlo y mostrarlo dentro de la plataforma con el fin de prestar el servicio.
        6.3 Servify puede retirar contenido que infrinja estos Términos o la ley.

        Artículo 7. Conductas prohibidas
        • Fraude, estafa o engaño a otros usuarios, incluido el uso de ofertas falsas.
        • Suplantar identidad, crear perfiles falsos o manipular calificaciones.
        • Acceder sin autorización a cuentas, sistemas o datos; intentar vulnerar la seguridad de la app o del servidor.
        • Usar bots, automatizaciones o ingeniería inversa para extraer datos de la plataforma.
        • Recopilar datos de otros usuarios con fines distintos al servicio.
        • Usar la plataforma para actividades ilícitas, o para acosar, amenazar o discriminar.

        Artículo 8. Seguridad personal
        8.1 Los servicios se prestan, en muchos casos, en el domicilio del cliente. Se recomienda a ambas partes: verificar la identidad de la otra persona, acordar el alcance y el precio por escrito en el chat antes de iniciar, y avisar a un familiar o conocido de la visita.
        8.2 Ante una situación de riesgo, el usuario debe contactar a las autoridades competentes. Servify no es un servicio de emergencia.

        Artículo 9. Suspresión, terminación y eliminación de cuenta
        9.1 Servify puede suspender o cerrar cuentas que incumplan estos Términos, ofrezcan riesgo para otros usuarios o sean requeridas por una autoridad competente.
        9.2 El usuario puede eliminar su cuenta desde su perfil. La eliminación borra sus datos personales según se explica en la Política de Privacidad.
        9.3 Las obligaciones ya asumidas (por ejemplo, un acuerdo en curso) deben cumplirse o cancelarse conforme a estos Términos antes de eliminar la cuenta.

        Artículo 10. Limitación de responsabilidad
        10.1 En la medida permitida por la ley, Servify no responde por daños, pérdidas o perjuicios derivados de los servicios prestados por los técnicos, de acuerdos entre usuarios o del contenido que ellos publiquen.
        10.2 Servify no garantiza disponibilidad continua. Puede haber interrupciones por mantenimiento, fallos de terceros (servidores, red, proveedores de llamadas) o causas de fuerza mayor.
        10.3 Nada en estos Términos limita derechos que la ley reconozca irrenunciablemente a los consumidores ni la responsabilidad que no pueda excluirse legalmente.

        Artículo 11. Propiedad intelectual
        11.1 El nombre Servify, su logotipo, diseño y código son propiedad de sus titulares. Se prohíbe copiarlos o usarlos sin autorización.

        Artículo 12. Cambios a los Términos
        12.1 Servify podrá actualizar estos Términos. Los cambios importantes se notificarán en la aplicación y, cuando la ley lo exija, se pedirá una nueva aceptación. Continuar usando la app tras el aviso implica aceptar los cambios.

        Artículo 13. Ley aplicable, controversias y contacto
        13.1 Estos Términos se rigen por las leyes de la República de El Salvador.
        13.2 Las partes intentarán resolver cualquier controversia de buena fe. De no lograrse, se someten a los tribunales competentes de Santa Ana, Santa Ana, sin perjuicio de los derechos de los consumidores.
        13.3 Contacto: servify124@gmail.com • Responsable: Servify
    """.trimIndent()

    Column {
        Text(
            text = "Disposiciones Generales (Aplica para Clientes y Técnicos)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = textoGenerales,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}