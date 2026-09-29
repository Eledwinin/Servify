package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    token: String,
    onIrAEditarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Paleta de colores Servify
    val verdePrincipal = Color(0xFF168067)
    val verdeClaroFondo = Color(0xFFE6F4F1)
    val textoOscuro = Color(0xFF1E293B)
    val textoGris = Color(0xFF64748B)
    val fondoTarjeta = Color.White
    val bordeSuave = Color(0xFFE2E8F0)
    val rojoAlerta = Color(0xFFDC2626)
    val fondoRojoClaro = Color(0xFFFEF2F2)
    val doradoVip = Color(0xFFD97706)
    val fondoDoradoClaro = Color(0xFFFEF3C7)

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    val listaFotosPortafolio = remember { mutableStateListOf<Uri>() }

    val fotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            listaFotosPortafolio.add(uri)
            Toast.makeText(context, "Foto agregada al portafolio", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            viewModel.cargarPerfil(token)
        }
    }

    val user = uiState.usuario
    val esTecnico = user?.rol?.lowercase() in listOf("tecnico", "trabajador", "ambos")
    // Estado unificado: lee tanto memoria local como persistencia de backend
    val esVipActivo = SessionManager.esVip || (user?.esVip == true)

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = {
                Text("Cancelar Membresía PRO", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("¿Estás seguro de que deseas cancelar tu suscripción? Perderás el acceso prioritario al muro de clientes y tu insignia de verificación.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarMembresiaVip(esVip = false) { exito ->
                            SessionManager.cancelarSuscripcionVip()
                            mostrarDialogoCancelar = false
                            if (exito) {
                                Toast.makeText(context, "Suscripción cancelada exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Suscripción cancelada localmente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = rojoAlerta)
                ) {
                    Text("Sí, cancelar plan", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Mantener plan")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Servify",
                        color = verdePrincipal,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        if (esVipActivo) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = fondoDoradoClaro,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = doradoVip,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "PRO",
                                        color = doradoVip,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        user?.let { u ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = verdeClaroFondo
                            ) {
                                Text(
                                    text = u.rol.uppercase(),
                                    color = verdePrincipal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = verdePrincipal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TARJETA DE PERFIL
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fondoTarjeta, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (esVipActivo) doradoVip.copy(alpha = 0.5f) else bordeSuave,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    val iniciales = remember(user?.nombre) {
                        val partes = (user?.nombre ?: "Usuario").trim().split(" ")
                        if (partes.size >= 2) {
                            "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
                        } else {
                            (user?.nombre?.take(2) ?: "US").uppercase()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (esVipActivo) fondoDoradoClaro else verdeClaroFondo),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            color = if (esVipActivo) doradoVip else verdePrincipal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.nombre ?: "Usuario",
                                color = textoOscuro,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (esVipActivo) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "VIP Activo",
                                    tint = doradoVip,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (esVipActivo && esTecnico) "Técnico Verificado PRO" else (user?.rol?.replaceFirstChar { it.uppercase() } ?: "Cliente"),
                            color = if (esVipActivo && esTecnico) doradoVip else textoGris,
                            fontSize = 13.sp,
                            fontWeight = if (esVipActivo && esTecnico) FontWeight.Bold else FontWeight.Normal
                        )

                        if (esTecnico) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", user?.calificacionPromedio ?: 5.0),
                                    color = textoOscuro,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // TARJETA VIP (Si es técnico)
                if (esTecnico) {
                    if (esVipActivo) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = fondoDoradoClaro),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, doradoVip.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = doradoVip)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Plan Servify PRO Activo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF78350F)
                                        )
                                    }

                                    TextButton(
                                        onClick = { mostrarDialogoCancelar = true },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Cancelar Plan", color = rojoAlerta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "Disfrutas de visibilidad destacada, comisión reducida y postulaciones ilimitadas.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }

                // INFORMACIÓN DE CONTACTO
                Card(
                    colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, bordeSuave),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información de contacto",
                            color = textoOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        DatoContactoItem(
                            icono = Icons.Default.Email,
                            titulo = "Correo",
                            valor = user?.correo ?: "No registrado"
                        )

                        HorizontalDivider(color = bordeSuave, thickness = 0.5.dp)

                        DatoContactoItem(
                            icono = Icons.Default.Phone,
                            titulo = "Teléfono",
                            valor = user?.telefono?.ifBlank { "No registrado" } ?: "No registrado"
                        )

                        HorizontalDivider(color = bordeSuave, thickness = 0.5.dp)

                        DatoContactoItem(
                            icono = Icons.Default.LocationOn,
                            titulo = "Dirección",
                            valor = user?.direccionTexto?.ifBlank { "No especificada" } ?: "No especificada"
                        )
                    }
                }

                // PORTAFOLIO DE TRABAJOS (Solo visible si es técnico)
                if (esTecnico) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, bordeSuave),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mis Trabajos Realizados",
                                    color = textoOscuro,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(
                                    onClick = {
                                        fotoLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Agregar foto",
                                        tint = verdePrincipal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Subir foto",
                                        color = verdePrincipal,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (listaFotosPortafolio.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            fotoLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = textoGris,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Aún no has agregado fotos de tus trabajos.",
                                            color = textoGris,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Toca aquí para subir fotos de evidencias.",
                                            color = verdePrincipal,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(listaFotosPortafolio) { uriFoto ->
                                        val bitmap = remember(uriFoto) {
                                            try {
                                                context.contentResolver.openInputStream(uriFoto)?.use { stream ->
                                                    BitmapFactory.decodeStream(stream)
                                                }
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }

                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = "Trabajo técnico",
                                                modifier = Modifier
                                                    .size(90.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .border(1.dp, bordeSuave, RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // RESEÑAS
                Card(
                    colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, bordeSuave),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mis Reseñas",
                            color = textoOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aún no tienes reseñas registradas.",
                            color = textoGris,
                            fontSize = 13.sp
                        )
                    }
                }

                // BOTÓN EDITAR PERFIL
                Surface(
                    onClick = onIrAEditarPerfil,
                    shape = RoundedCornerShape(14.dp),
                    color = fondoTarjeta,
                    border = BorderStroke(1.dp, bordeSuave),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = textoOscuro,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Editar Perfil",
                            color = textoOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = textoGris,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // BOTÓN CERRAR SESIÓN
                Surface(
                    onClick = onCerrarSesion,
                    shape = RoundedCornerShape(14.dp),
                    color = fondoRojoClaro,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = rojoAlerta,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cerrar Sesión",
                            color = rojoAlerta,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun DatoContactoItem(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = titulo,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
            Text(
                text = valor,
                color = Color(0xFF1E293B),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}