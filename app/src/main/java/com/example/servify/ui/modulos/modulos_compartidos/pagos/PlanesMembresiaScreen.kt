package com.example.servify.modulos_compartidos.pagos

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager
import com.example.servify.data.model.EstadoPago
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesMembresiaScreen(
    onVolver: () -> Unit,
    onSeleccionarPlan: (tipoPlan: String, monto: Double) -> Unit = { _, _ -> },
    viewModel: PagosViewModel = viewModel(),
    onPagoExitoso: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var mostrarDialogoExito by remember { mutableStateOf(false) }
    var planCompradoNombre by remember { mutableStateOf("") }

    // 1. Integración de Stripe PaymentSheet en Compose
    val paymentSheet = rememberPaymentSheet { result ->
        val plan = viewModel.planEnProceso ?: "Plan PRO"
        viewModel.procesarResultadoStripe(
            result = result,
            tipoPlan = plan,
            onExito = {
                planCompradoNombre = plan
                mostrarDialogoExito = true
            }
        )
    }

    // 2. Presentación del PaymentSheet nativo al recibir respuesta del backend
    LaunchedEffect(viewModel.estadoPago) {
        val estado = viewModel.estadoPago
        if (estado is EstadoPago.ListoParaPresentar) {
            try {
                PaymentConfiguration.init(context, estado.datos.publishableKey)

                val customerConfig = PaymentSheet.CustomerConfiguration(
                    id = estado.datos.customer,
                    ephemeralKeySecret = estado.datos.ephemeralKey
                )

                val configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "Servify",
                    customer = customerConfig,
                    allowsDelayedPaymentMethods = false
                )

                paymentSheet.presentWithPaymentIntent(
                    paymentIntentClientSecret = estado.datos.paymentIntent,
                    configuration = configuration
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Error al abrir pasarela Stripe: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                viewModel.restablecerEstado()
            }
        } else if (estado is EstadoPago.Error) {
            Toast.makeText(context, estado.mensaje, Toast.LENGTH_LONG).show()
            viewModel.restablecerEstado()
        } else if (estado is EstadoPago.Cancelado) {
            Toast.makeText(context, estado.mensaje, Toast.LENGTH_SHORT).show()
            viewModel.restablecerEstado()
        }
    }

    LaunchedEffect(viewModel.mensajeNotificacion) {
        viewModel.mensajeNotificacion?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeNotificacion = null
        }
    }

    // Detección reactiva de membresía activa en SessionManager
    val esVipActivo = SessionManager.esVip
    val planActivoActual = SessionManager.tipoPlanActivo ?: SessionManager.usuarioActual?.tipoPlan ?: ""
    val esSemanalActivo = esVipActivo && planActivoActual.contains("Semanal", ignoreCase = true)
    val esProActivo = esVipActivo && (planActivoActual.contains("PRO", ignoreCase = true) || !esSemanalActivo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes de Suscripción", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lleva tus servicios al siguiente nivel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Consigue más clientes directos y destaca frente a la competencia.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // 🌟 Banner reactivo de Plan Activo si el técnico ya es VIP
            if (esVipActivo) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "¡Membresía Activa!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "Actualmente cuentas con ${if (planActivoActual.isNotBlank()) planActivoActual else "Plan VIP PRO"}. Tu perfil goza de máxima visibilidad e insignia dorada.",
                                fontSize = 13.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            // --- PLAN BÁSICO / SEMANAL ($3.99) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = if (esSemanalActivo) BorderStroke(2.dp, Color(0xFF0284C7)) else null
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plan Semanal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (esSemanalActivo) {
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "ACTIVO",
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$3.99 / semana",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    BeneficioFila("Postulación prioritaria a solicitudes")
                    BeneficioFila("Insignia de técnico verificado")
                    BeneficioFila("Soporte estándar")

                    Spacer(modifier = Modifier.height(16.dp))

                    if (esSemanalActivo) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE2E8F0))
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Plan Actual Activo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        }
                    } else {
                        val cargandoSemanal = viewModel.estadoPago is EstadoPago.Cargando && viewModel.planEnProceso == "Plan Semanal"
                        Button(
                            onClick = {
                                viewModel.iniciarPagoSuscripcionStripe(monto = 3.99, tipoPlan = "Plan Semanal")
                            },
                            enabled = viewModel.estadoPago !is EstadoPago.Cargando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (cargandoSemanal) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Suscribirse con Stripe ($3.99)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        TextButton(
                            onClick = { onSeleccionarPlan("Plan Semanal", 3.99) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Otros métodos de pago", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // --- PLAN SERVIFY PRO / MENSUAL ($6.99) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plan Servify PRO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (esProActivo) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ACTIVO",
                                        color = Color(0xFFD97706),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = Color(0xFFFFB300)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$6.99 / mes",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    BeneficioFila("Insignia VIP dorada en tu perfil")
                    BeneficioFila("Aparición en 'Profesionales Destacados' del Home")
                    BeneficioFila("Máxima prioridad en búsquedas de clientes")
                    BeneficioFila("Recepción de solicitudes directas y llamadas")

                    Spacer(modifier = Modifier.height(20.dp))

                    if (esProActivo) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFFEF3C7))
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("¡Tu Plan PRO Activo!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                        }
                    } else {
                        val cargandoPro = viewModel.estadoPago is EstadoPago.Cargando && viewModel.planEnProceso == "Plan Servify PRO"
                        Button(
                            onClick = {
                                viewModel.iniciarPagoSuscripcionStripe(monto = 6.99, tipoPlan = "Plan Servify PRO")
                            },
                            enabled = viewModel.estadoPago !is EstadoPago.Cargando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (cargandoPro) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Suscribirse a PRO con Stripe ($6.99)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = { onSeleccionarPlan("Plan Servify PRO", 6.99) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Otros métodos de pago", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Modal de confirmación tras pago exitoso
    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExito = false },
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¡Membresía Activada!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Tu suscripción a '$planCompradoNombre' se ha completado exitosamente mediante Stripe.\n\nTu perfil ahora cuenta con insignia destacada y máxima visibilidad para nuevos clientes.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoExito = false
                        onPagoExitoso?.invoke()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¡Excelente!")
                }
            }
        )
    }
}

@Composable
private fun BeneficioFila(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
