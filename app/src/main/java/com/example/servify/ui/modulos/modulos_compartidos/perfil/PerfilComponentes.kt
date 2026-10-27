package com.example.servify.ui.modulos.modulos_compartidos.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.servify.data.api.FotoPortafolioResponse
import com.example.servify.data.model.UsuarioModel
import com.example.servify.ui.componentes.cards.TarjetaBase
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatConfig



object PerfilColores {
    val verde = Color(0xFF168067)
    val verdeClaro = Color(0xFFE6F4F1)
    val texto = Color(0xFF1E293B)
    val gris = Color(0xFF64748B)
    val borde = Color(0xFFE2E8F0)
    val rojo = Color(0xFFDC2626)
    val rojoClaro = Color(0xFFFEF2F2)
    val dorado = Color(0xFFD97706)
    val doradoClaro = Color(0xFFFEF3C7)
}

// ---------- Utilidades ----------

fun iniciales(nombre: String?): String {
    val partes = (nombre ?: "Usuario").trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
        partes.size == 1 -> partes[0].take(2).uppercase()
        else -> "US"
    }
}


fun calcularCompletitud(u: UsuarioModel?, fotosPortafolio: Int, esTecnico: Boolean): Pair<Int, List<String>> {
    if (u == null) return 0 to emptyList()
    val pasos = buildList {
        add("Teléfono" to !u.telefono.isNullOrBlank())
        add("Dirección" to !u.direccionTexto.isNullOrBlank())
        if (esTecnico) {
            add("Descripción «Sobre mí»" to !u.biografia.isNullOrBlank())
            add("Años de experiencia" to ((u.anosExperiencia ?: 0) > 0))
            add("Fotos de tus trabajos" to (fotosPortafolio > 0))
        }
    }
    val hechos = pasos.count { it.second }
    return (hechos * 100 / pasos.size) to pasos.filter { !it.second }.map { it.first }
}

// ---------- Contenedor base de tarjetas ----------

// ---------- Cabecera ----------

@Composable
fun CabeceraPerfil(user: UsuarioModel?, esTecnico: Boolean, esVip: Boolean) {
    val c = PerfilColores
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, if (esVip) c.dorado.copy(alpha = 0.5f) else c.borde, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(if (esVip) c.doradoClaro else c.verdeClaro),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciales(user?.nombre), color = if (esVip) c.dorado else c.verde,
                fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user?.nombre ?: "Usuario", color = c.texto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (esVip) {
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.WorkspacePremium, "VIP", tint = c.dorado, modifier = Modifier.size(18.dp))
                }
            }
            Text(
                if (esVip && esTecnico) "Técnico Verificado PRO"
                else user?.rol?.replaceFirstChar { it.uppercase() } ?: "Cliente",
                color = if (esVip && esTecnico) c.dorado else c.gris,
                fontSize = 13.sp,
                fontWeight = if (esVip && esTecnico) FontWeight.Bold else FontWeight.Normal
            )
            if (esTecnico) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("%.1f".format(user?.calificacionPromedio ?: 0.0), color = c.texto,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ---------- Barra de perfil completo ----------

@Composable
fun TarjetaCompletitud(porcentaje: Int, faltantes: List<String>, onCompletar: () -> Unit) {
    val c = PerfilColores
    TarjetaBase(Modifier.clickable { onCompletar() }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Perfil completo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = c.texto)
            Text("$porcentaje%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = c.verde)
        }
        LinearProgressIndicator(
            progress = { porcentaje / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = c.verde, trackColor = c.verdeClaro
        )
        Text("Te falta: ${faltantes.joinToString(", ")}. Toca para completarlo.",
            fontSize = 12.sp, color = c.gris)
    }
}

// ---------- Plan PRO ----------

@Composable
fun TarjetaVip(onCancelar: () -> Unit) {
    val c = PerfilColores
    Card(
        colors = CardDefaults.cardColors(containerColor = c.doradoClaro),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, c.dorado.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = c.dorado)
                    Spacer(Modifier.width(8.dp))
                    Text("Plan Servify PRO Activo", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF78350F))
                }
                TextButton(onClick = onCancelar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("Cancelar Plan", color = c.rojo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("Disfrutas de visibilidad destacada, comisión reducida y postulaciones ilimitadas.",
                fontSize = 12.sp, color = Color(0xFF92400E))
        }
    }
}

// ---------- Sobre mí (solo técnicos) ----------

@Composable
fun TarjetaSobreMi(biografia: String?, anosExperiencia: Int?, onEditar: () -> Unit) {
    val c = PerfilColores
    TarjetaBase {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Sobre mí", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(if (biografia.isNullOrBlank()) "Agregar" else "Editar",
                    color = c.verde, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            biografia?.takeIf { it.isNotBlank() }
                ?: "Cuéntales a los clientes quién eres y qué tipo de trabajos haces. Una buena descripción genera confianza.",
            fontSize = 13.sp, color = if (biografia.isNullOrBlank()) c.gris else Color.DarkGray, lineHeight = 19.sp
        )
        if ((anosExperiencia ?: 0) > 0) {
            Surface(color = c.verdeClaro, shape = RoundedCornerShape(10.dp)) {
                Text("$anosExperiencia años de experiencia",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = c.verde)
            }
        }
    }
}

// ---------- Contacto ----------

@Composable
fun TarjetaContacto(user: UsuarioModel?) {
    val c = PerfilColores
    TarjetaBase {
        Text("Información de contacto", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        DatoContacto(Icons.Default.Email, "Correo", user?.correo ?: "No registrado")
        HorizontalDivider(color = c.borde, thickness = 0.5.dp)
        DatoContacto(Icons.Default.Phone, "Teléfono", user?.telefono?.ifBlank { null } ?: "No registrado")
        HorizontalDivider(color = c.borde, thickness = 0.5.dp)
        DatoContacto(Icons.Default.LocationOn, "Dirección", user?.direccionTexto?.ifBlank { null } ?: "No especificada")
    }
}

@Composable
private fun DatoContacto(icono: ImageVector, titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icono, null, tint = PerfilColores.gris, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text(valor, color = PerfilColores.texto, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

// ---------- Portafolio ----------

@Composable
fun TarjetaPortafolio(
    fotos: List<FotoPortafolioResponse>,
    subiendo: Boolean,
    onSubir: () -> Unit,
    onEliminar: (Int) -> Unit
) {
    val c = PerfilColores
    TarjetaBase {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Mis Trabajos Realizados", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSubir, enabled = !subiendo, contentPadding = PaddingValues(horizontal = 8.dp)) {
                if (subiendo) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = c.verde, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.AddPhotoAlternate, "Agregar foto", tint = c.verde, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Subir foto", color = c.verde, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (fotos.isEmpty()) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth().clickable { onSubir() }) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = c.gris, modifier = Modifier.size(30.dp))
                    Text("Aún no has agregado fotos de tus trabajos.", color = c.gris, fontSize = 12.sp)
                    Text("Toca aquí para subir evidencias fotográficas.", color = c.verde,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(fotos, key = { it.id }) { foto ->
                    val url = if (foto.foto_url.startsWith("http")) foto.foto_url
                    else ChatConfig.SERVER_URL + foto.foto_url
                    Box(Modifier.size(90.dp).clip(RoundedCornerShape(12.dp))
                        .border(1.dp, c.borde, RoundedCornerShape(12.dp))) {
                        AsyncImage(
                            model = url, contentDescription = "Trabajo técnico",
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { onEliminar(foto.id) },
                            modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) { Icon(Icons.Default.Close, "Eliminar foto", tint = Color.White, modifier = Modifier.size(14.dp)) }
                    }
                }
            }
        }
    }
}
