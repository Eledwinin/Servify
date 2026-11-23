package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.servify.ui.componentes.inputs.CampoFechaCalendario
import com.example.servify.ui.componentes.inputs.formatearFechaUtc
import com.example.servify.ui.componentes.inputs.obtenerHoyUtcMillis
import com.example.servify.ui.theme.*
import java.util.Locale

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
    val hoyUtcMillis = remember { obtenerHoyUtcMillis() }

    var precioEstimado by remember { mutableStateOf("") }
    var fechaVisitaMillis by remember { mutableStateOf<Long?>(null) }
    var fechaEntregaMillis by remember { mutableStateOf<Long?>(null) }
    var mensajePropuesta by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var concuerdoConPrecio by remember { mutableStateOf(false) }

    val tiempoEstimadoFormateado = remember(fechaVisitaMillis, fechaEntregaMillis) {
        if (fechaVisitaMillis != null && fechaEntregaMillis != null) {
            val visitaIso = formatearFechaUtc(fechaVisitaMillis!!, "yyyy-MM-dd")
            val entregaIso = formatearFechaUtc(fechaEntregaMillis!!, "yyyy-MM-dd")
            "Visita: $visitaIso | Entrega: $entregaIso"
        } else ""
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
                            tint = ServifyBlueLight
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
                            val monto = precioEstimado.replace(",", ".").toDoubleOrNull()
                            when {
                                monto == null || monto <= 0.0 -> errorMensaje = "Por favor ingresa un presupuesto válido"
                                fechaVisitaMillis == null -> errorMensaje = "Por favor selecciona la fecha estimada de visita"
                                fechaEntregaMillis == null -> errorMensaje = "Por favor selecciona la fecha estimada de entrega"
                                else -> {
                                    errorMensaje = null
                                    viewModel.postular(
                                        solicitudId = idSolicitud,
                                        precio = monto,
                                        tiempo = tiempoEstimadoFormateado,
                                        mensaje = mensajePropuesta,
                                        aceptaPresupuesto = concuerdoConPrecio
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                    ) {
                        Text("Enviar Postulación", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            tint = ServifyBlueLight,
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
                    color = ServifySurface,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "El cliente ofrece $%.2f".format(Locale.US, presupuestoCliente),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ServifyTextTitle
                        )
                        Text(
                            text = if (concuerdoConPrecio) {
                                "Aceptaste el precio ofrecido. Completa las fechas y detalles y presiona \"Enviar Postulación\" abajo."
                            } else {
                                "Si el precio te parece bien, selecciónalo: se colocará en el presupuesto y podrás enviarlo con el botón de abajo."
                            },
                            fontSize = 12.sp,
                            color = ServifyTextBody
                        )
                        OutlinedButton(
                            onClick = {
                                concuerdoConPrecio = !concuerdoConPrecio
                                precioEstimado = if (concuerdoConPrecio) "%.2f".format(Locale.US, presupuestoCliente) else ""
                                errorMensaje = null
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, if (concuerdoConPrecio) ServifyDanger else ServifyBlueLight)
                        ) {
                            if (concuerdoConPrecio) {
                                Icon(Icons.Default.CheckCircle, null, tint = ServifyDanger, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (concuerdoConPrecio) "Cancelar acuerdo de precio" else "Concuerdo con el precio",
                                fontWeight = FontWeight.Bold,
                                color = if (concuerdoConPrecio) ServifyDanger else ServifyBlueLight
                            )
                        }
                    }
                }
            }

            val coloresCampo = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ServifyTextTitle,
                unfocusedTextColor = ServifyTextTitle,
                disabledTextColor = ServifyTextMuted,
                focusedLabelColor = ServifyBlueLight,
                unfocusedLabelColor = ServifyTextMuted,
                disabledLabelColor = ServifyTextMuted,
                focusedPlaceholderColor = ServifyTextMuted,
                unfocusedPlaceholderColor = ServifyTextMuted,
                focusedLeadingIconColor = ServifyBlueLight,
                unfocusedLeadingIconColor = ServifyTextMuted,
                disabledLeadingIconColor = ServifyTextMuted,
                focusedBorderColor = ServifyBlueLight,
                unfocusedBorderColor = ServifyBorder,
                disabledBorderColor = ServifyBorder.copy(alpha = 0.6f),
                focusedContainerColor = ServifyInputBackground,
                unfocusedContainerColor = ServifyInputBackground,
                disabledContainerColor = ServifyInputBackground.copy(alpha = 0.6f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ServifySurface, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Detalles de tu oferta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)

                OutlinedTextField(
                    value = precioEstimado,
                    onValueChange = {
                        if (!concuerdoConPrecio) {
                            precioEstimado = it
                            if (errorMensaje != null) errorMensaje = null
                        }
                    },
                    enabled = !concuerdoConPrecio,
                    label = { Text("Presupuesto / Mano de obra ($)") },
                    placeholder = { Text("Ej: 25.00") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo,
                    modifier = Modifier.fillMaxWidth()
                )

                CampoFechaCalendario(
                    label = "Fecha estimada de visita",
                    placeholder = "Toca para elegir fecha de visita",
                    fechaSeleccionadaMillis = fechaVisitaMillis,
                    fechaMinimaUtcMillis = hoyUtcMillis,
                    onFechaSeleccionada = { seleccionada ->
                        fechaVisitaMillis = seleccionada
                        if (fechaEntregaMillis != null && fechaEntregaMillis!! < seleccionada) {
                            fechaEntregaMillis = null
                        }
                    },
                    colores = coloresCampo,
                    icono = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    onAntesDeAbrir = { if (errorMensaje != null) errorMensaje = null }
                )

                CampoFechaCalendario(
                    label = "Fecha estimada de entrega",
                    placeholder = "Toca para elegir fecha de entrega",
                    fechaSeleccionadaMillis = fechaEntregaMillis,
                    fechaMinimaUtcMillis = fechaVisitaMillis ?: hoyUtcMillis,
                    onFechaSeleccionada = { fechaEntregaMillis = it },
                    colores = coloresCampo,
                    icono = { Icon(Icons.Default.EventAvailable, contentDescription = null) },
                    onAntesDeAbrir = { if (errorMensaje != null) errorMensaje = null }
                )

                OutlinedTextField(
                    value = mensajePropuesta,
                    onValueChange = { mensajePropuesta = it },
                    label = { Text("Mensaje o detalle de tu propuesta") },
                    placeholder = { Text("Explícale al cliente cómo resolverás su problema...") },
                    minLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMensaje != null) {
                Surface(color = ServifyDangerBg, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
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
}