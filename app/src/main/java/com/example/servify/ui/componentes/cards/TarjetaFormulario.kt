package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var mostrarBuscadorCategorias by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }

    val listaCategorias = listOf(
        "Plomería",
        "Electricidad",
        "Cerrajería",
        "Carpintería",
        "Refrigeración",
        "Mantenimiento de Cómputo"
    )

    val categoriasFiltradas = remember(textoBusqueda) {
        if (textoBusqueda.isBlank()) listaCategorias
        else listaCategorias.filter { it.contains(textoBusqueda, ignoreCase = true) }
    }

    // Diálogo con Buscador de Categorías
    if (mostrarBuscadorCategorias) {
        AlertDialog(
            onDismissRequest = {
                mostrarBuscadorCategorias = false
                textoBusqueda = ""
            },
            containerColor = ServifySurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Seleccionar Categoría",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ServifyTextTitle
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo de búsqueda
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { textoBusqueda = it },
                        placeholder = { Text("Buscar categoría...", color = ServifyTextPlaceholder) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = ServifyTextMuted)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ServifyBorder,
                            focusedBorderColor = ServifyBluePrimary,
                            unfocusedContainerColor = ServifyInputBackground,
                            focusedContainerColor = ServifyInputBackground,
                            focusedTextColor = ServifyTextTitle,
                            unfocusedTextColor = ServifyTextTitle
                        )
                    )

                    // Lista de resultados filtrados
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(categoriasFiltradas) { cat ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategoriaSelect(cat)
                                        mostrarBuscadorCategorias = false
                                        textoBusqueda = ""
                                    },
                                color = if (cat == categoriaSeleccionada) ServifyCianSoft else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    fontSize = 14.sp,
                                    fontWeight = if (cat == categoriaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                    color = if (cat == categoriaSeleccionada) ServifyBluePrimary else ServifyTextTitle
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarBuscadorCategorias = false
                    textoBusqueda = ""
                }) {
                    Text("Cerrar", color = ServifyBluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Círculo del paso 1 unificado exactamente con el mismo estilo azul oscuro y texto blanco
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(ServifyBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("¿Qué necesitas?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ServifyTextTitle)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CATEGORÍA (Con apertura de Buscador)
            Text("CATEGORÍA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedCard(
                onClick = { mostrarBuscadorCategorias = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ServifyBorder),
                colors = CardDefaults.outlinedCardColors(containerColor = ServifyInputBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoriaSeleccionada.ifEmpty { "Seleccionar categoría..." },
                        color = if (categoriaSeleccionada.isEmpty()) ServifyTextPlaceholder else ServifyTextTitle,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar",
                        tint = ServifyTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÍTULO CORTO
            Text("TÍTULO CORTO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                placeholder = { Text("Ej: Fuga de agua en lavabo", color = ServifyTextPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ServifyBorder,
                    focusedBorderColor = ServifyBluePrimary,
                    unfocusedContainerColor = ServifyInputBackground,
                    focusedContainerColor = ServifyInputBackground,
                    focusedTextColor = ServifyTextTitle,
                    unfocusedTextColor = ServifyTextTitle
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PRESUPUESTO
            Text("PRESUPUESTO ESTIMADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = presupuesto,
                onValueChange = onPresupuestoChange,
                placeholder = { Text("Ej: 25.00", color = ServifyTextPlaceholder) },
                leadingIcon = { Text("$", color = ServifyTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ServifyBorder,
                    focusedBorderColor = ServifyBluePrimary,
                    unfocusedContainerColor = ServifyInputBackground,
                    focusedContainerColor = ServifyInputBackground,
                    focusedTextColor = ServifyTextTitle,
                    unfocusedTextColor = ServifyTextTitle
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DESCRIPCIÓN DEL PROBLEMA
            Text("DESCRIPCIÓN DEL PROBLEMA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ServifyTextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                placeholder = { Text("Describe los detalles del problema...", color = ServifyTextPlaceholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ServifyBorder,
                    focusedBorderColor = ServifyBluePrimary,
                    unfocusedContainerColor = ServifyInputBackground,
                    focusedContainerColor = ServifyInputBackground,
                    focusedTextColor = ServifyTextTitle,
                    unfocusedTextColor = ServifyTextTitle
                )
            )
        }
    }
}