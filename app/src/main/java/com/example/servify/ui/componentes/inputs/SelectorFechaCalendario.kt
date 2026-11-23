package com.example.servify.ui.componentes.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.servify.ui.theme.ServifyBlueLight
import com.example.servify.ui.theme.ServifyTextMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Devuelve la medianoche UTC de hoy para validar fechas mínimas en DatePicker. */
fun obtenerHoyUtcMillis(): Long {
    val localCal = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            localCal.get(Calendar.YEAR),
            localCal.get(Calendar.MONTH),
            localCal.get(Calendar.DAY_OF_MONTH)
        )
    }.timeInMillis
}

/** Formatea milisegundos UTC (ej. "dd/MM/yyyy" para UI o "yyyy-MM-dd" para BD). */
fun formatearFechaUtc(utcMillis: Long, patron: String): String {
    val formatter = SimpleDateFormat(patron, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(utcMillis))
}

/**
 * Campo de texto de solo lectura que abre un DatePickerDialog de Material 3 al tocarlo,
 * bloqueando cualquier fecha anterior a [fechaMinimaUtcMillis].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaCalendario(
    label: String,
    placeholder: String,
    fechaSeleccionadaMillis: Long?,
    fechaMinimaUtcMillis: Long,
    onFechaSeleccionada: (Long) -> Unit,
    colores: TextFieldColors,
    icono: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onAntesDeAbrir: () -> Unit = {}
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val textoMostrado = remember(fechaSeleccionadaMillis) {
        fechaSeleccionadaMillis?.let { formatearFechaUtc(it, "dd/MM/yyyy") } ?: ""
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textoMostrado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = icono,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = colores,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onAntesDeAbrir()
                        mostrarDialogo = true
                    }
                )
        )
    }

    if (mostrarDialogo) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaSeleccionadaMillis?.takeIf { it >= fechaMinimaUtcMillis }
                ?: fechaMinimaUtcMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= fechaMinimaUtcMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(onFechaSeleccionada)
                        mostrarDialogo = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold, color = ServifyBlueLight)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = ServifyTextMuted)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}