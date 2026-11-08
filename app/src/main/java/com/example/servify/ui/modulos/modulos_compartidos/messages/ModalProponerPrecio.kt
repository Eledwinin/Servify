package com.example.servify.ui.modulos.modulos_compartidos.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalProponerPrecio(
    onDismiss: () -> Unit,
    onEnviar: (Double) -> Unit
) {
    val verdePrincipal = Color(0xFF0F2C59)
    var montoStr by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Proponer Precio / Regatear", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("Ingresa la cantidad monetaria en dólares que propones para este servicio.", fontSize = 13.sp, color = Color(0xFF64748B))

            OutlinedTextField(
                value = montoStr,
                onValueChange = { montoStr = it; error = null },
                label = { Text("Monto en USD ($)") },
                placeholder = { Text("Ej: 45.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdePrincipal,
                    focusedLabelColor = verdePrincipal,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(error!!, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = {
                    val montoLimpio = montoStr.replace(",", ".").trim()
                    val monto = montoLimpio.toDoubleOrNull()

                    if (monto == null || monto <= 0) {
                        error = "Ingresa un monto válido mayor a 0"
                        return@Button
                    }
                    onEnviar(monto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal)
            ) {
                Text("Enviar Propuesta Económica", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}