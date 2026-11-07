package com.example.servify.ui.componentes.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.servify.ui.theme.*

@Composable
fun TopBarPrincipal(
    titulo: String,
    fotoUrl: String? = null,
    inicialesUsuario: String = "EL",
    onPerfilClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ServifyBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto que cambia según la pantalla
            Text(
                text = titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ServifyBluePrimary
            )

            // Contenedor de la foto o iniciales
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ServifyCianSoft)
                    .clickable { onPerfilClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!fotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = fotoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = inicialesUsuario,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ServifyBluePrimary
                    )
                }
            }
        }
    }
}