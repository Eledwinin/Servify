package com.example.servify.ui.modulos.trabajador.muro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
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
    var tiempoEstimado by remember { mutableStateOf("") }
    var mensajePropuesta by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

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
                            val monto = precioEstimado.toDoubleOrNull()
                            if (monto == null || monto <= 0.0) {
                                errorMensaje = "Por favor ingresa un presupuesto válido"
                                return@Button
                            }
                            if (tiempoEstimado.isBlank()) {
                                errorMensaje = "Por favor indica un tiempo estimado"
                                return@Button
                            }
                            errorMensaje = null
                            viewModel.postular(idSolicitud, monto, tiempoEstimado, mensajePropuesta)
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
                                if (tiempoEstimado.isBlank()) {
                                    errorMensaje = "Por favor indica un tiempo estimado antes de concordar con el precio"
                                    return@OutlinedButton
                                }
                                errorMensaje = null
                                viewModel.postular(
                                    solicitudId = idSolicitud,
                                    precio = presupuestoCliente,
                                    tiempo = tiempoEstimado,
                                    mensaje = mensajePropuesta,
                                    aceptaPresupuesto = true
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ServifyBlueLight)
                        ) {
                            Text(
                                text = "Concuerdo con el precio",
                                fontWeight = FontWeight.Bold,
                                color = ServifyBlueLight
                            )
                        }
                    }
                }
            }

            val coloresCampo = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ServifyTextTitle,
                unfocusedTextColor = ServifyTextTitle,
                focusedLabelColor = ServifyBlueLight,
                unfocusedLabelColor = ServifyTextMuted,
                focusedPlaceholderColor = ServifyTextMuted,
                unfocusedPlaceholderColor = ServifyTextMuted,
                focusedLeadingIconColor = ServifyBlueLight,
                unfocusedLeadingIconColor = ServifyTextMuted,
                focusedBorderColor = ServifyBlueLight,
                unfocusedBorderColor = ServifyBorder,
                focusedContainerColor = ServifyInputBackground,
                unfocusedContainerColor = ServifyInputBackground
            )

            // Sección de Inputs
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
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo,
                    modifier = Modifier.fillMaxWidth()
                )

                // Input: Tiempo estimado
                OutlinedTextField(
                    value = tiempoEstimado,
                    onValueChange = {
                        tiempoEstimado = it
                        if (errorMensaje != null) errorMensaje = null
                    },
                    label = { Text("Tiempo estimado de entrega o visita") },
                    placeholder = { Text("Ej: 1 día, Mañana por la tarde") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo,
                    modifier = Modifier.fillMaxWidth()
                )

                // Input: Mensaje explicativo
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
}