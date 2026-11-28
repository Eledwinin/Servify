package com.example.servify.ui.componentes.indicadores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatConfig
import com.example.servify.ui.theme.ServifyBluePrimary
import com.example.servify.ui.theme.ServifyCianSoft

@Composable
fun AvatarUsuario(
    nombre: String?,
    fotoUrl: String?,
    tamano: Dp = 48.dp,
    fondo: Color = ServifyCianSoft,
    colorTexto: Color = ServifyBluePrimary,
    modifier: Modifier = Modifier
) {
    val iniciales = remember(nombre) {
        nombre.orEmpty().split(" ").filter { it.isNotBlank() }.take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifEmpty { "?" }
    }

    val urlCompleta = remember(fotoUrl) {
        when {
            fotoUrl.isNullOrBlank() -> null
            fotoUrl.startsWith("http") -> fotoUrl
            else -> ChatConfig.SERVER_URL.trimEnd('/') + "/" + fotoUrl.trimStart('/')
        }
    }

    Box(
        modifier = modifier.size(tamano).clip(CircleShape).background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            color = colorTexto,
            fontWeight = FontWeight.Bold,
            fontSize = (tamano.value * 0.36f).sp
        )
        if (!urlCompleta.isNullOrBlank()) {
            AsyncImage(
                model = urlCompleta,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}