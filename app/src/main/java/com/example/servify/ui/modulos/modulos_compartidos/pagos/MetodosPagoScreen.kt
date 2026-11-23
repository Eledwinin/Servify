package com.example.servify.modulos_compartidos.pagos

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.data.model.EstadoPago
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetodosPagoScreen(
    viewModel: PagosViewModel,
    tipoPlan: String,
    monto: Double,
    onVolver: () -> Unit,
    onPagoExitoso: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var metodoSeleccionado by remember { mutableStateOf("Tarjeta de Crédito / Débito") }

    // 1. Integración de PaymentSheet en Compose
    val paymentSheet = rememberPaymentSheet { result ->
        viewModel.procesarResultadoStripe(
            result = result,
            tipoPlan = tipoPlan,
            onExito = onPagoExitoso
        )
    }

    // 2. Escuchar cuando el backend responde para presentar el PaymentSheet nativo
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
                Toast.makeText(context, "Error al abrir Stripe: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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

    LaunchedEffect(uiState) {
        when (uiState) {
            is PagoUiState.Error -> {
                Toast.makeText(context, (uiState as PagoUiState.Error).mensaje, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    val metodos = listOf(
        "Tarjeta de Crédito / Débito",
        "Transferencia Bancaria",
        "Chivo Wallet / Bitcoin"
    )
    val montoFormateado = String.format(Locale.US, "%.2f", monto)
    val estaCargando = viewModel.estadoPago is EstadoPago.Cargando || uiState is PagoUiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Método de Pago", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Resumen de la Orden", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Suscripción - $tipoPlan", fontWeight = FontWeight.Bold)
                            Text(text = "$$montoFormateado USD", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Selecciona cómo deseas pagar:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                metodos.forEach { metodo ->
                    val seleccionado = metodo == metodoSeleccionado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { metodoSeleccionado = metodo }
                            .border(
                                width = if (seleccionado) 2.dp else 1.dp,
                                color = if (seleccionado) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = metodo, fontSize = 15.sp)
                            RadioButton(
                                selected = seleccionado,
                                onClick = { metodoSeleccionado = metodo }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (metodoSeleccionado == "Tarjeta de Crédito / Débito") {
                        viewModel.iniciarPagoSuscripcionStripe(monto = monto, tipoPlan = tipoPlan)
                    } else {
                        viewModel.procesarPagoSuscripcion(
                            monto = monto,
                            tipoPlan = tipoPlan,
                            metodoPago = metodoSeleccionado,
                            onExito = {
                                viewModel.reiniciarEstado()
                                onPagoExitoso()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !estaCargando,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (metodoSeleccionado == "Tarjeta de Crédito / Débito") {
                            "Pagar con Stripe $$montoFormateado"
                        } else {
                            "Confirmar y Pagar $$montoFormateado"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
