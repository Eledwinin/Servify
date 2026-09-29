package com.example.servify.ui.modulos.modulos_compartidos.pagos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.navigation.Rutas

enum class TipoMetodoPago {
    VISA, MASTERCARD, APPLE_PAY
}

@Composable
fun MetodosPagoScreen(
    navController: NavController,
    onPagoConfirmado: () -> Unit = {}
) {
    val verdeServify = Color(0xFF1B7B61)
    val bordeGris = Color(0xFFE2E8F0)
    val textoOscuro = Color(0xFF1E293B)

    var metodoSeleccionado by remember { mutableStateOf(TipoMetodoPago.VISA) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servify",
                    color = verdeServify,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "MODO TRABAJADOR",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(bordeGris)
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        onPagoConfirmado()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
                ) {
                    Text(
                        text = "Confirmar y Pagar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textoOscuro
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Métodos de pago",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoOscuro
                )
            }

            TextButton(
                onClick = { },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = verdeServify,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nuevo método de pago",
                    color = verdeServify,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TarjetaMetodoItem(
                    titulo = "•••• 4242",
                    logoTexto = "VISA",
                    logoColor = Color(0xFF1A1F71),
                    estaSeleccionado = metodoSeleccionado == TipoMetodoPago.VISA,
                    onClick = { metodoSeleccionado = TipoMetodoPago.VISA },
                    verdeServify = verdeServify,
                    bordeGris = bordeGris
                )

                TarjetaMetodoItem(
                    titulo = "•••• 1234",
                    logoTexto = "MC",
                    logoColor = Color(0xFFEB001B),
                    estaSeleccionado = metodoSeleccionado == TipoMetodoPago.MASTERCARD,
                    onClick = { metodoSeleccionado = TipoMetodoPago.MASTERCARD },
                    verdeServify = verdeServify,
                    bordeGris = bordeGris
                )

                TarjetaMetodoItem(
                    titulo = "Apple Pay",
                    logoTexto = "Pay",
                    logoColor = Color.Black,
                    estaSeleccionado = metodoSeleccionado == TipoMetodoPago.APPLE_PAY,
                    onClick = { metodoSeleccionado = TipoMetodoPago.APPLE_PAY },
                    verdeServify = verdeServify,
                    bordeGris = bordeGris
                )
            }
        }
    }
}

@Composable
private fun TarjetaMetodoItem(
    titulo: String,
    logoTexto: String,
    logoColor: Color,
    estaSeleccionado: Boolean,
    onClick: () -> Unit,
    verdeServify: Color,
    bordeGris: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) Color(0xFFF2FBF8) else Color.White
        ),
        border = BorderStroke(
            width = if (estaSeleccionado) 1.5.dp else 1.dp,
            color = if (estaSeleccionado) verdeServify else bordeGris
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, bordeGris),
                    modifier = Modifier.size(width = 46.dp, height = 30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = logoTexto,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = logoColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            RadioButton(
                selected = estaSeleccionado,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = verdeServify,
                    unselectedColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}