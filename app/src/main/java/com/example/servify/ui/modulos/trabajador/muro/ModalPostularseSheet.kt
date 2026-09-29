package com.example.servify.ui.modulos.trabajador.muro

//Modal inferior para enviar propuesta económica, permite al trabajador ingresar presupuesto estimado,
//tiempo aproximado de llegada y un mensaje aclaratorio al cliente


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalPostularseSheet(
    tituloTrabajo: String,
    onDismiss: () -> Unit,
    onEnviarPostulacion: (precio: Double, tiempoDias: String, mensaje: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val verdeServify = Color(0xFF1B7B61)

    var precioEstimado by remember { mutableStateOf("") }
    var tiempoEstimado by remember { mutableStateOf("") }
    var mensajePropuesta by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera con botón de cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enviar Propuesta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = tituloTrabajo,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

            // Input: Precio estimado
            OutlinedTextField(
                value = precioEstimado,
                onValueChange = {
                    precioEstimado = it
                    if (errorMensaje != null) errorMensaje = null
                },
                label = { Text("Presupuesto / Mano de obra estimado ($)") },
                placeholder = { Text("Ej: 25.00") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = verdeServify) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeServify,
                    focusedLabelColor = verdeServify
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Input: Tiempo estimado
            OutlinedTextField(
                value = tiempoEstimado,
                onValueChange = {
                    tiempoEstimado = it
                    if (errorMensaje != null) errorMensaje = null
                },
                label = { Text("Tiempo estimado de entrega/visita") },
                placeholder = { Text("Ej: 1 día, Mañana por la tarde") },
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = verdeServify) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeServify,
                    focusedLabelColor = verdeServify
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Input: Mensaje explicativo
            OutlinedTextField(
                value = mensajePropuesta,
                onValueChange = { mensajePropuesta = it },
                label = { Text("Mensaje o detalle de tu propuesta") },
                placeholder = { Text("Explícale al cliente cómo resolverás su problema...") },
                minLines = 3,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = verdeServify,
                    focusedLabelColor = verdeServify
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMensaje != null) {
                Text(
                    text = errorMensaje!!,
                    color = Color(0xFFDC2626),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón de Enviar
            Button(
                onClick = {
                    val monto = precioEstimado.toDoubleOrNull()
                    if (monto == null || monto <= 0.0) {
                        errorMensaje = "Por favor ingresa un monto válido"
                        return@Button
                    }
                    if (tiempoEstimado.isBlank()) {
                        errorMensaje = "Por favor indica un tiempo estimado"
                        return@Button
                    }
                    onEnviarPostulacion(monto, tiempoEstimado, mensajePropuesta)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeServify)
            ) {
                Text("Confirmar y Enviar Postulación", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}