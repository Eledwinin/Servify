package com.example.servify.ui.modulos.modulos_compartidos.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.model.PerfilCliente
import com.example.servify.ui.componentes.indicadores.AvatarUsuario
import com.example.servify.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun miembroDesde(iso: String?): String = try {
    val f = LocalDate.parse(iso!!.take(10)).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "SV")))
    "Cliente desde $f"
} catch (e: Exception) { "Cliente de Servify" }

private fun fechaCorta(iso: String?): String = try {
    LocalDate.parse(iso!!.take(10)).format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "SV")))
} catch (e: Exception) { "" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilClienteScreen(
    clienteId: Int,
    onBackClick: () -> Unit,
    viewModel: PerfilClienteViewModel = viewModel()
) {
    LaunchedEffect(clienteId) { viewModel.cargar(clienteId) }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            TopAppBar(
                title = { Text("Perfil del cliente", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        val p = viewModel.perfil
        when {
            viewModel.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ServifyBluePrimary)
            }
            p == null -> Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Text(viewModel.error ?: "No se pudo cargar el perfil", color = ServifyTextMuted)
            }
            else -> ContenidoPerfilCliente(p, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ContenidoPerfilCliente(p: PerfilCliente, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarUsuario(p.nombre, p.fotoUrl, 72.dp)
            Spacer(Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(p.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)
                Text(miembroDesde(p.miembroDesde), fontSize = 12.sp, color = ServifyTextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = ServifyStarYellow, fontSize = 15.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (p.totalResenas == 0) "Sin reseñas todavía"
                        else "%.1f (%d %s)".format(Locale.US, p.calificacion, p.totalResenas,
                            if (p.totalResenas == 1) "reseña" else "reseñas"),
                        fontSize = 13.sp, color = ServifyTextBody
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EstadisticaCliente("Solicitudes publicadas", p.solicitudesPublicadas.toString(), Modifier.weight(1f))
            EstadisticaCliente("Trabajos completados", p.solicitudesCompletadas.toString(), Modifier.weight(1f))
        }

        Text("Reseñas de otros técnicos", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = ServifyTextTitle)

        if (p.resenas.isEmpty()) {
            Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Este cliente aún no tiene reseñas. Las opiniones aparecerán cuando termine sus primeros trabajos.",
                    Modifier.padding(16.dp), fontSize = 13.sp, color = ServifyTextMuted
                )
            }
        } else {
            p.resenas.forEach { r ->
                Surface(color = Color.White, shape = RoundedCornerShape(14.dp), shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AvatarUsuario(r.autorNombre, r.autorFoto, 30.dp)
                                Text(r.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text("★".repeat(r.calificacion.coerceIn(0, 5)), color = ServifyStarYellow, fontSize = 14.sp)
                        }
                        if (!r.comentario.isNullOrBlank()) {
                            Text(r.comentario, fontSize = 13.sp, color = ServifyTextBody, lineHeight = 18.sp)
                        }
                        Text(
                            listOfNotNull(r.solicitudTitulo?.takeIf { it.isNotBlank() }, fechaCorta(r.creadoEn).ifBlank { null })
                                .joinToString(" · "),
                            fontSize = 11.sp, color = ServifyTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadisticaCliente(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ServifyBluePrimary)
            Text(etiqueta, fontSize = 11.sp, color = ServifyTextMuted)
        }
    }
}