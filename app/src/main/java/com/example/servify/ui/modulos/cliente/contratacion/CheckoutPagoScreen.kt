package com.example.servify.ui.modulos.cliente.contratacion

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.servify.data.model.EstadoPago
import com.example.servify.ui.componentes.QrGenerator
import com.example.servify.ui.theme.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPagoScreen(
    solicitudId: Int,
    navController: NavController,
    viewModel: CheckoutPagoViewModel = viewModel()
) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 1. Integración de PaymentSheet en Compose mediante rememberPaymentSheet
    val paymentSheet = rememberPaymentSheet { result ->
        viewModel.procesarResultadoStripe(result, solicitudId)
    }

    // Cargar detalles de la solicitud al entrar a la pantalla
    LaunchedEffect(solicitudId) {
        viewModel.cargarDetalle(solicitudId)
    }

    // 2. Escuchar cuando el backend responde con los tokens de Stripe para abrir el PaymentSheet
    LaunchedEffect(viewModel.estadoPago) {
        val estado = viewModel.estadoPago
        if (estado is EstadoPago.ListoParaPresentar) {
            try {
                // Inicialización dinámica con la Publishable Key devuelta por el servidor
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
        }
    }

    // Generar el Bitmap del QR en cuanto se obtenga el token tras el pago
    LaunchedEffect(viewModel.qrTokenGenerado) {
        val token = viewModel.qrTokenGenerado
        if (!token.isNullOrEmpty()) {
            qrBitmap = QrGenerator.generarBitmapQr(token)
        }
    }

    // Notificaciones tipo Toast para mensajes al usuario
    LaunchedEffect(viewModel.mensajeNotificacion) {
        viewModel.mensajeNotificacion?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeNotificacion = null
        }
    }

    val estadoActual = viewModel.estadoPago
    val estaCargandoPago = estadoActual is EstadoPago.Cargando
    val yaFuePagado = !viewModel.qrTokenGenerado.isNullOrEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (yaFuePagado) "Comprobante de Pago" else "Pago Seguro con Stripe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
        },
        containerColor = ServifyBackground
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
            if (!yaFuePagado) {
                // ==========================================
                // VISTA DE PAGO ACTIVA (Antes de pagar)
                // ==========================================

                // 1. Tarjeta informativa del Servicio a pagar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DETALLE DEL SERVICIO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ServifyTextMuted,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                color = ServifyCianSoft,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = viewModel.solicitud?.categoriaNombre ?: "Servicio",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ServifyBluePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = viewModel.solicitud?.titulo ?: "Solicitud #$solicitudId",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextTitle
                        )

                        if (!viewModel.solicitud?.descripcion.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.solicitud!!.descripcion,
                                fontSize = 13.sp,
                                color = ServifyTextMuted,
                                maxLines = 3
                            )
                        }

                        if (!viewModel.solicitud?.clienteNombre.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = ServifyOnlineGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cliente: ${viewModel.solicitud?.clienteNombre}",
                                    fontSize = 12.sp,
                                    color = ServifyTextTitle
                                )
                            }
                        }
                    }
                }

                // 2. Tarjeta Interactiva de Monto a Pagar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyDarkNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                text = "Total a Pagar",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = ServifyCianSoft,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = viewModel.montoTexto,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "USD",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ServifyCianSoft,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo editable para ajustar el monto en caso de acuerdo mutuo
                        OutlinedTextField(
                            value = viewModel.montoTexto,
                            onValueChange = { nuevo ->
                                // Permite números y un único punto decimal
                                if (nuevo.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                    viewModel.montoTexto = nuevo
                                }
                            },
                            label = { Text("Monto acordado ($)", color = Color.White.copy(alpha = 0.7f)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ServifyCianSoft)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ServifyCianSoft,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                cursorColor = ServifyCianSoft
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // 3. Banner de Seguridad Stripe
                Surface(
                    color = ServifySurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ServifyCianSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = ServifyBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Procesamiento Seguro con Stripe",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ServifyTextTitle
                            )
                            Text(
                                text = "Tus datos bancarios están cifrados de extremo a extremo y nunca se almacenan en nuestros servidores.",
                                fontSize = 11.sp,
                                color = ServifyTextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // 4. Mensaje de Error si la pasarela o el backend fallan
                if (estadoActual is EstadoPago.Error) {
                    Surface(
                        color = ServifyDanger.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServifyDanger.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ServifyDanger,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = estadoActual.mensaje,
                                color = ServifyDanger,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Botón Principal: Iniciar Pago con Stripe
                Button(
                    onClick = { viewModel.iniciarPagoStripe(solicitudId) },
                    enabled = !estaCargandoPago,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ServifyBluePrimary,
                        disabledContainerColor = ServifyBluePrimary.copy(alpha = 0.5f)
                    )
                ) {
                    if (estaCargandoPago) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Conectando con Stripe...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Pagar con Tarjeta ›",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // ==========================================
                // VISTA DE CONFIRMACIÓN Y CÓDIGO QR DE CIERRE
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ServifyOnlineGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ServifyOnlineGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "¡Pago Confirmado por Stripe!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyTextTitle
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "El pago de $${viewModel.montoTexto} USD fue procesado correctamente.",
                            fontSize = 13.sp,
                            color = ServifyTextMuted
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(color = ServifyBackground)

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Código QR de Cierre para el Técnico",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ServifyTextTitle
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Muestra este código al técnico para que lo escanee desde su aplicación y complete el servicio.",
                            fontSize = 12.sp,
                            color = ServifyTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        qrBitmap?.let { bitmap ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "QR de Cierre",
                                    modifier = Modifier
                                        .size(230.dp)
                                        .padding(12.dp)
                                )
                            }
                        } ?: run {
                            CircularProgressIndicator(
                                color = ServifyBluePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
                        ) {
                            Text("Volver a Mis Solicitudes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
