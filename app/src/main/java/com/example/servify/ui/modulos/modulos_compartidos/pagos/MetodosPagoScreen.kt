package com.example.servify.modulos_compartidos.pagos

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.modulos_compartidos.pagos.PagoUiState
import com.example.servify.modulos_compartidos.pagos.PagosViewModel
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

    val metodos = listOf(
        "Tarjeta de Crédito / Débito",
        "Transferencia Bancaria",
        "Chivo Wallet / Bitcoin"
    )
    val montoFormateado = String.format(Locale.US, "%.2f", monto)

    LaunchedEffect(uiState) {
        when (uiState) {
            is PagoUiState.Error -> {
                Toast.makeText(context, (uiState as PagoUiState.Error).mensaje, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Método de Pago", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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

                        val montoFormateado = String.format(Locale.US, "%.2f", monto)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Suscripción - $tipoPlan", fontWeight = FontWeight.Bold)
                            Text(text = "$$montoFormateado USD", fontWeight = FontWeight.Bold) // <--- Aquí usamos la variable formateada
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
                    viewModel.procesarPagoSuscripcion(
                        monto = monto,
                        tipoPlan = tipoPlan,
                        metodoPago = metodoSeleccionado,
                        onExito = {
                            viewModel.reiniciarEstado()
                            onPagoExitoso()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is PagoUiState.Loading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is PagoUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirmar y Pagar $$montoFormateado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}