package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnviarPropuestaScreen(
    idSolicitud: Int,
    tituloTrabajo: String,
    presupuestoCliente: Double? = null,
    navController: NavController,
    viewModel: MuroSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var precioEstimado by remember { mutableStateOf("") }
    var fechaVisitaMillis by remember { mutableStateOf<Long?>(null) }
    var fechaEntregaMillis by remember { mutableStateOf<Long?>(null) }
    var mostrarCalendarioVisita by remember { mutableStateOf(false) }
    var mostrarCalendarioEntrega by remember { mutableStateOf(false) }
    var mensajePropuesta by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Medianoche UTC de hoy para bloquear días anteriores en el DatePicker de Material 3
    val hoyUtcMillis = remember {
        val localCal = Calendar.getInstance()
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                localCal.get(Calendar.YEAR),
                localCal.get(Calendar.MONTH),
                localCal.get(Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis
    }

    val textoFechaVisita = remember(fechaVisitaMillis) {
        fechaVisitaMillis?.let { formatearFechaUtc(it, "dd/MM/yyyy") } ?: ""
    }
    val textoFechaEntrega = remember(fechaEntregaMillis) {
        fechaEntregaMillis?.let { formatearFechaUtc(it, "dd/MM/yyyy") } ?: ""
    }

    // Formato estructurado que se guarda en base de datos dentro de tiempo_estimado
    val tiempoEstimadoFormateado = remember(fechaVisitaMillis, fechaEntregaMillis) {
        if (fechaVisitaMillis != null && fechaEntregaMillis != null) {
            val visitaIso = formatearFechaUtc(fechaVisitaMillis!!, "yyyy-MM-dd")
            val entregaIso = formatearFechaUtc(fechaEntregaMillis!!, "yyyy-MM-dd")
            "Visita: $visitaIso | Entrega: $entregaIso"
        } else {
            ""
        }
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    LaunchedEffect(viewModel.mensajeExito) {
        viewModel.mensajeExito?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Enviar Propuesta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ServifyTextTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ServifySurface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = ServifySurface) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            val monto = precioEstimado.toDoubleOrNull()
                            if (monto == null || monto <= 0.0) {
                                errorMensaje = "Por favor ingresa un presupuesto válido"
                                return@Button
                            }
                            if (fechaVisitaMillis == null) {
                                errorMensaje = "Por favor selecciona la fecha estimada de visita"
                                return@Button
                            }
                            if (fechaEntregaMillis == null) {
                                errorMensaje = "Por favor selecciona la fecha estimada de entrega"
                                return@Button
                            }
                            errorMensaje = null
                            viewModel.postular(idSolicitud, monto, tiempoEstimadoFormateado, mensajePropuesta)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                    ) {
                        Text(
                            text = "Enviar Postulación",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        containerColor = ServifyBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cabecera tipo tarjeta executive (Dark Navy) con el título del trabajo
            Card(
                colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ServifyCianSoft.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = ServifyCianSoft,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TRABAJO SOLICITADO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBlueLight,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tituloTrabajo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2
                        )
                    }
                }
            }

            if (presupuestoCliente != null && presupuestoCliente > 0) {
                Surface(
                    color = ServifyCianSoft.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "El cliente ofrece $%.2f".format(presupuestoCliente),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ServifyTextTitle
                        )
                        Text(
                            text = "Si el precio te parece bien, acéptalo: ese será el monto final del trabajo y no habrá que negociar.",
                            fontSize = 12.sp,
                            color = ServifyTextBody
                        )
                        OutlinedButton(
                            onClick = {
                                if (fechaVisitaMillis == null || fechaEntregaMillis == null) {
                                    errorMensaje = "Selecciona la fecha de visita y la fecha de entrega antes de concordar con el precio"
                                    return@OutlinedButton
                                }
                                errorMensaje = null
                                viewModel.postular(
                                    solicitudId = idSolicitud,
                                    precio = presupuestoCliente,
                                    tiempo = tiempoEstimadoFormateado,
                                    mensaje = mensajePropuesta,
                                    aceptaPresupuesto = true
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Concuerdo con el precio",
                                fontWeight = FontWeight.Bold,
                                color = ServifyBluePrimary
                            )
                        }
                    }
                }
            }

            // Sección de Inputs con selectores de fecha reales
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ServifySurface, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Detalles de tu oferta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )

                // Input: Presupuesto estimado
                OutlinedTextField(
                    value = precioEstimado,
                    onValueChange = {
                        precioEstimado = it
                        if (errorMensaje != null) errorMensaje = null
                    },
                    label = { Text("Presupuesto / Mano de obra ($)") },
                    placeholder = { Text("Ej: 25.00") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = ServifyBluePrimary)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ServifyBluePrimary,
                        focusedLabelColor = ServifyBluePrimary,
                        unfocusedBorderColor = ServifyBorder,
                        focusedContainerColor = ServifyInputBackground,
                        unfocusedContainerColor = ServifyInputBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector 1: Fecha estimada de visita
                CampoFechaSelector(
                    label = "Fecha estimada de visita",
                    placeholder = "Toca para elegir fecha de visita",
                    valorMostrado = textoFechaVisita,
                    icono = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ServifyBluePrimary)
                    },
                    onClick = {
                        if (errorMensaje != null) errorMensaje = null
                        mostrarCalendarioVisita = true
                    }
                )

                // Selector 2: Fecha estimada de entrega
                CampoFechaSelector(
                    label = "Fecha estimada de entrega",
                    placeholder = "Toca para elegir fecha de entrega",
                    valorMostrado = textoFechaEntrega,
                    icono = {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = ServifyBluePrimary)
                    },
                    onClick = {
                        if (errorMensaje != null) errorMensaje = null
                        mostrarCalendarioEntrega = true
                    }
                )

                // Input: Mensaje explicativo
                OutlinedTextField(
                    value = mensajePropuesta,
                    onValueChange = { mensajePropuesta = it },
                    label = { Text("Mensaje o detalle de tu propuesta") },
                    placeholder = { Text("Explícale al cliente cómo resolverás su problema...") },
                    minLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ServifyBluePrimary,
                        focusedLabelColor = ServifyBluePrimary,
                        unfocusedBorderColor = ServifyBorder,
                        focusedContainerColor = ServifyInputBackground,
                        unfocusedContainerColor = ServifyInputBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMensaje != null) {
                Surface(
                    color = ServifyDangerBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMensaje!!,
                        color = ServifyDanger,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }

    // Diálogo Calendario 1: Fecha de Visita (>= hoy)
    if (mostrarCalendarioVisita) {
        val datePickerStateVisita = rememberDatePickerState(
            initialSelectedDateMillis = fechaVisitaMillis ?: hoyUtcMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= hoyUtcMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioVisita = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val seleccionada = datePickerStateVisita.selectedDateMillis
                        if (seleccionada != null) {
                            fechaVisitaMillis = seleccionada
                            // Si la entrega elegida quedó antes de la nueva visita, se limpia para obligar a actualizarla
                            if (fechaEntregaMillis != null && fechaEntregaMillis!! < seleccionada) {
                                fechaEntregaMillis = null
                            }
                        }
                        mostrarCalendarioVisita = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold, color = ServifyBluePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendarioVisita = false }) {
                    Text("Cancelar", color = ServifyTextMuted)
                }
            }
        ) {
            DatePicker(state = datePickerStateVisita)
        }
    }

    // Diálogo Calendario 2: Fecha Estimada de Entrega (>= fechaVisita o >= hoy)
    if (mostrarCalendarioEntrega) {
        val fechaMinimaEntrega = fechaVisitaMillis ?: hoyUtcMillis
        val datePickerStateEntrega = rememberDatePickerState(
            initialSelectedDateMillis = fechaEntregaMillis?.takeIf { it >= fechaMinimaEntrega } ?: fechaMinimaEntrega,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= fechaMinimaEntrega
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioEntrega = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val seleccionada = datePickerStateEntrega.selectedDateMillis
                        if (seleccionada != null) {
                            fechaEntregaMillis = seleccionada
                        }
                        mostrarCalendarioEntrega = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold, color = ServifyBluePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendarioEntrega = false }) {
                    Text("Cancelar", color = ServifyTextMuted)
                }
            }
        ) {
            DatePicker(state = datePickerStateEntrega)
        }
    }
}

@Composable
private fun CampoFechaSelector(
    label: String,
    placeholder: String,
    valorMostrado: String,
    icono: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valorMostrado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = icono,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ServifyBluePrimary,
                focusedLabelColor = ServifyBluePrimary,
                unfocusedBorderColor = ServifyBorder,
                focusedContainerColor = ServifyInputBackground,
                unfocusedContainerColor = ServifyInputBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente encima para capturar el toque en toda el área del campo sin abrir el teclado
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
    }
}

private fun formatearFechaUtc(utcMillis: Long, patron: String): String {
    val formatter = SimpleDateFormat(patron, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(utcMillis))
}