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
import com.example.servify.data.SessionManager
import com.example.servify.data.api.FotoPortafolioResponse
import com.example.servify.data.model.UsuarioModel
import com.example.servify.ui.componentes.cards.TarjetaBase
import com.example.servify.ui.modulos.modulos_compartidos.messages.ChatConfig
import com.example.servify.ui.theme.*

object PerfilColores {
    val verde = ServifyBluePrimary
    val verdeClaro = ServifyCianSoft
    val texto = ServifyTextTitle
    val gris = ServifyTextMuted
    val borde = ServifyBorder
    val rojo = ServifyDanger
    val rojoClaro = ServifyDangerBg
    val dorado = ServifyStarYellow
    val doradoClaro = ServifyPendingBg
}

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

@Composable
fun CabeceraPerfil(user: UsuarioModel?, esTecnico: Boolean, esVip: Boolean) {
    val c = PerfilColores

    val tipoPlanActual = user?.tipoPlan ?: SessionManager.tipoPlanActivo ?: ""
    val esSemanal = tipoPlanActual.contains("Semanal", ignoreCase = true)

    // Color de acento para VIP o Semanal, pero NEGRO/OSCURO para cuando no tenga plan
    val colorAcento = when {
        esSemanal -> Color(0xFF0284C7) // Celeste para el semanal
        esVip -> c.dorado            // Dorado para el PRO
        else -> c.texto              // <--- CAMBIO AQUÍ: Usamos el color de texto principal para que no se pierda
    }

    val fondoAvatar = when {
        esSemanal -> Color(0xFFE0F2FE)
        esVip -> c.doradoClaro
        else -> c.verdeClaro
    }

    val textoSubtitulo = when {
        esSemanal && esTecnico -> "Técnico con Plan Semanal"
        esSemanal -> "Plan Semanal Activo"
        esVip && esTecnico -> "Técnico Verificado PRO"
        else -> user?.rol?.replaceFirstChar { it.uppercase() } ?: "Cliente"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(ServifySurface, RoundedCornerShape(16.dp))
            .border(
                width = if (esVip || esSemanal) 2.dp else 1.dp,
                color = colorAcento.copy(alpha = if (esVip || esSemanal) 0.8f else 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(fondoAvatar),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales(user?.nombre),
                color = if (esVip || esSemanal) colorAcento else c.verde,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user?.nombre ?: "Usuario",
                    color = c.texto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (esVip) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = colorAcento,
                        modifier = Modifier.size(18.sp.value.dp) // o 18.dp
                    )
                }
            }

            // TEXTO DEL SUBTÍTULO CON BUEN CONTRASTE
            Text(
                text = textoSubtitulo,
                color = colorAcento, // Si no tiene plan, ahora usará c.texto (oscuro y legible)
                fontSize = 13.sp,
                fontWeight = if (esVip || esSemanal) FontWeight.Bold else FontWeight.Medium
            )

            if (esTecnico) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = ServifyStarYellow, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "%.1f".format(user?.calificacionPromedio ?: 0.0),
                        color = c.texto,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

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
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = c.verde,
            trackColor = c.verdeClaro
        )
        Text(
            "Te falta: ${faltantes.joinToString(", ")}. Toca para completarlo.",
            fontSize = 12.sp,
            color = c.gris
        )
    }
}

@Composable
fun TarjetaVip(tipoPlan: String?, onCancelar: () -> Unit) {
    val c = PerfilColores

    // Validamos si es el plan semanal o el pro para cambiar el texto y estilo
    val esSemanal = tipoPlan?.contains("Semanal", ignoreCase = true) == true

    val tituloPlan = if (esSemanal) "Plan Semanal Activo" else "Plan Servify PRO"
    val subtituloPlan = if (esSemanal) "$3.99 / semana •" else "Suscripción Mensual Exclusiva"
    val descripcionPlan = if (esSemanal)
        "Disfrutas de postulación prioritaria a solicitudes e insignia de técnico verificado por 7 días."
    else
        "Disfrutas de visibilidad destacada en el inicio, comisión reducida en servicios y postulaciones ilimitadas."

    Card(
        colors = CardDefaults.cardColors(
            containerColor = ServifyDarkNavy
        ),
        shape = RoundedCornerShape(16.dp),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = c.dorado.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = c.dorado,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = tituloPlan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = subtituloPlan,
                            fontSize = 11.sp,
                            color = c.dorado,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                TextButton(
                    onClick = onCancelar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ServifyDanger
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Cancelar Plan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 0.5.dp
            )

            Text(
                text = descripcionPlan,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun TarjetaSobreMi(biografia: String?, anosExperiencia: Int?, onEditar: () -> Unit) {
    val c = PerfilColores
    TarjetaBase {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sobre mí", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(
                    if (biografia.isNullOrBlank()) "Agregar" else "Editar",
                    color = c.verde,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            biografia?.takeIf { it.isNotBlank() }
                ?: "Cuéntales a los clientes quién eres y qué tipo de trabajos haces. Una buena descripción genera confianza.",
            fontSize = 13.sp,
            color = if (biografia.isNullOrBlank()) c.gris else Color.DarkGray,
            lineHeight = 19.sp
        )
        if ((anosExperiencia ?: 0) > 0) {
            Surface(color = c.verdeClaro, shape = RoundedCornerShape(10.dp)) {
                Text(
                    "$anosExperiencia años de experiencia",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = c.verde
                )
            }
        }
    }
}

@Composable
fun TarjetaContacto(user: UsuarioModel?) {
    val c = PerfilColores
    val esTecnico = SessionManager.esTecnico || (user?.rol?.lowercase() in listOf("tecnico", "trabajador", "ambos"))

    // Obtenemos el nombre del oficio/categoría únicamente si viene con texto real desde la BD
    val categoriaNombre = user?.categoriaNombre?.takeIf { it.isNotBlank() }
        ?: SessionManager.categoriaNombre?.takeIf { it.isNotBlank() }

    TarjetaBase {
        Text("Información de contacto", color = c.texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))

        // SOLO se renderiza si es técnico Y la categoría NO es nula ni vacía
        if (esTecnico && !categoriaNombre.isNullOrBlank()) {
            DatoContacto(
                icono = Icons.Default.Build,
                titulo = "ESPECIALIDAD / CATEGORÍA",
                valor = categoriaNombre
            )
            HorizontalDivider(color = c.borde, thickness = 0.5.dp)
        }

        DatoContacto(Icons.Default.Email, "CORREO", user?.correo ?: "No registrado")
        HorizontalDivider(color = c.borde, thickness = 0.5.dp)
        DatoContacto(Icons.Default.Phone, "TELÉFONO", user?.telefono?.ifBlank { null } ?: "No registrado")
        HorizontalDivider(color = c.borde, thickness = 0.5.dp)
        DatoContacto(Icons.Default.LocationOn, "DIRECCIÓN", user?.direccionTexto?.ifBlank { null } ?: "No especificada")
    }
}

@Composable
private fun DatoContacto(icono: ImageVector, titulo: String, valor: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Icon(icono, null, tint = ServifyBluePrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, color = PerfilColores.gris, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                valor,
                color = PerfilColores.texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SeccionResenasCliente() {
    val c = PerfilColores
    TarjetaBase {
        Text("Reseñas como Cliente", color = c.texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Aquí verás el historial de calificaciones que te han dejado los técnicos al finalizar los servicios.",
            fontSize = 12.sp,
            color = c.gris
        )
    }
}

@Composable
fun TarjetaPortafolio(
    fotos: List<FotoPortafolioResponse>,
    subiendo: Boolean,
    onSubir: () -> Unit,
    onEliminar: (Int) -> Unit
) {
    val c = PerfilColores
    TarjetaBase {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ServifyInputBackground,
                modifier = Modifier.fillMaxWidth().clickable { onSubir() }
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = c.gris, modifier = Modifier.size(30.dp))
                    Text("Aún no has agregado fotos de tus trabajos.", color = c.gris, fontSize = 12.sp)
                    Text(
                        "Toca aquí para subir evidencias fotográficas.",
                        color = c.verde,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(fotos, key = { it.id }) { foto ->
                    val url = if (foto.foto_url.startsWith("http")) foto.foto_url
                    else ChatConfig.SERVER_URL + foto.foto_url
                    Box(
                        Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, c.borde, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Trabajo técnico",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { onEliminar(foto.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, "Eliminar foto", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}