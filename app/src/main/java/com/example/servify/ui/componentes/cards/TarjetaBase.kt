package com.example.servify.ui.componentes.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.servify.ui.theme.ServifyBorder
import com.example.servify.ui.theme.ServifySurface

@Composable
fun TarjetaBase(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ServifySurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ServifyBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}