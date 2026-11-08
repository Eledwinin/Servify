package com.example.servify.ui.modulos.cliente.contratacion

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.ui.componentes.QrGenerator
import com.example.servify.ui.theme.*

// Transformador para insertar automáticamente el '/' después de los primeros 2 dígitos
class ExpiracionVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0, 4) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPagoScreen(
    solicitudId: Int,
    navController: NavController,
    viewModel: CheckoutPagoViewModel = viewModel()
) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(viewModel.qrTokenGenerado) {
        val token = viewModel.qrTokenGenerado
        if (!token.isNullOrEmpty()) {
            qrBitmap = QrGenerator.generarBitmapQr(token)
        }
    }

    LaunchedEffect(viewModel.mensajeError) {
        viewModel.mensajeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.mensajeError = null
        }
    }

    // Formateador visual para mostrar en la tarjeta de arriba
    val fechaTarjeta = remember(viewModel.fechaExpiracion) {
        val digitos = viewModel.fechaExpiracion.filter { it.isDigit() }
        if (digitos.length >= 2) {
            "${digitos.take(2)}/${digitos.drop(2)}"
        } else {
            digitos
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.qrTokenGenerado.isNullOrEmpty()) "Pago con Tarjeta" else "QR de Cierre",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ServifySurface,
                    titleContentColor = ServifyTextTitle
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (viewModel.qrTokenGenerado.isNullOrEmpty()) {
                // Tarjeta visual interactiva
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyBluePrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Servify Pay",
                                color = ServifySurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = ServifySurface
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = if (viewModel.numeroTarjeta.isBlank()) "**** **** **** ****" else viewModel.numeroTarjeta,
                            color = ServifySurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("TITULAR", fontSize = 10.sp, color = ServifySurface.copy(alpha = 0.7f))
                                Text(
                                    text = if (viewModel.nombreTitular.isBlank()) "NOMBRE APELLIDO" else viewModel.nombreTitular.uppercase(),
                                    fontSize = 12.sp,
                                    color = ServifySurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("EXPIRA", fontSize = 10.sp, color = ServifySurface.copy(alpha = 0.7f))
                                Text(
                                    text = if (fechaTarjeta.isBlank()) "MM/AA" else fechaTarjeta,
                                    fontSize = 12.sp,
                                    color = ServifySurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campos del Formulario
                OutlinedTextField(
                    value = viewModel.numeroTarjeta,
                    onValueChange = { if (it.length <= 16) viewModel.numeroTarjeta = it.filter { char -> char.isDigit() } },
                    label = { Text("Número de tarjeta") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.nombreTitular,
                    onValueChange = { viewModel.nombreTitular = it },
                    label = { Text("Nombre en la tarjeta") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.fechaExpiracion,
                        onValueChange = { input ->
                            // Filtra solo los números y limita a 4 dígitos (MMAA)
                            val soloNumeros = input.filter { it.isDigit() }
                            if (soloNumeros.length <= 4) {
                                viewModel.fechaExpiracion = soloNumeros
                            }
                        },
                        label = { Text("MM/AA") },
                        placeholder = { Text("MMAA") },
                        visualTransformation = ExpiracionVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = viewModel.cvc,
                        onValueChange = { if (it.length <= 4) viewModel.cvc = it.filter { char -> char.isDigit() } },
                        label = { Text("CVC") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.procesarPago(solicitudId) {
                            Toast.makeText(context, "¡Pago procesado con éxito!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !viewModel.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                ) {
                    if (viewModel.cargando) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Pagar y Generar QR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Pantalla del QR listo tras la aprobación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Pago Confirmado!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBluePrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Muestra este código QR al técnico para finalizar el servicio.",
                            fontSize = 14.sp,
                            color = ServifyTextMuted
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        qrBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "QR de Cierre",
                                modifier = Modifier.size(240.dp)
                            )
                        } ?: CircularProgressIndicator(color = ServifyBluePrimary)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                        ) {
                            Text("Volver a Mis Solicitudes")
                        }
                    }
                }
            }
        }
    }
}