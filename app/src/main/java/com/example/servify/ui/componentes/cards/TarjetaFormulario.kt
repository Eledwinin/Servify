package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaFormulario(
    titulo: String,
    onTituloChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    categoriaSeleccionada: String,
    onCategoriaSelect: (String) -> Unit,
    presupuesto: String,
    onPresupuestoChange: (String) -> Unit
) {
    var expandirCategorias by remember { mutableStateOf(false) }
    val listaCategorias = listOf(
        "Plomería",
        "Electricidad",
        "Cerrajería",
        "Carpintería",
        "Refrigeración",
        "Mantenimiento de Cómputo"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(ServifyTealAvatar),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = ServifyGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("¿Qué necesitas?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ServifyTextTitle)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CATEGORÍA
            Text("CATEGORÍA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandirCategorias,
                onExpandedChange = { expandirCategorias = !expandirCategorias }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada.ifEmpty { "Seleccionar categoría..." },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirCategorias) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ServifyBorder, focusedBorderColor = ServifyGreenPrimary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandirCategorias,
                    onDismissRequest = { expandirCategorias = false },
                    modifier = Modifier.background(ServifySurface)
                ) {
                    listaCategorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria, color = ServifyTextBody) },
                            onClick = {
                                onCategoriaSelect(categoria)
                                expandirCategorias = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÍTULO CORTO
            Text("TÍTULO CORTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                placeholder = { Text("Ej: Fuga de agua en lavabo", color = ServifyTextPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ServifyBorder, focusedBorderColor = ServifyGreenPrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PRESUPUESTO
            Text("PRESUPUESTO ESTIMADO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = presupuesto,
                onValueChange = onPresupuestoChange,
                placeholder = { Text("Ej: 25.00", color = ServifyTextPlaceholder) },
                leadingIcon = { Text("$", color = ServifyTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ServifyBorder, focusedBorderColor = ServifyGreenPrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DESCRIPCIÓN DEL PROBLEMA
            Text("DESCRIPCIÓN DEL PROBLEMA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                placeholder = { Text("Describe los detalles del problema...", color = ServifyTextPlaceholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = ServifyBorder, focusedBorderColor = ServifyGreenPrimary)
            )
        }
    }
}