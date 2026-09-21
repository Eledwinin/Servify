package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.theme.ServifyBorder
import com.example.servify.ui.theme.ServifyGreenLight
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifySurface
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun RegistroTrabajadorScreen(
    onRegistroExitoso: () -> Unit = {},
    onIrALogin: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current
    val estadoRegistro by viewModel.estadoRegistro.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para el archivo PDF
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfNombre by remember { mutableStateOf<String?>(null) }

    // Manejo de respuestas del servidor
    LaunchedEffect(estadoRegistro) {
        when (val estado = estadoRegistro) {
            is RegistroState.Error -> {
                Toast.makeText(context, estado.mensaje, Toast.LENGTH_LONG).show()
            }
            is RegistroState.Success -> {
                Toast.makeText(context, estado.mensaje, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Selector de archivos del sistema
    val selectorPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        pdfUri = uri
        if (uri != null) {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    pdfNombre = cursor.getString(nameIndex)
                }
            }
        } else {
            pdfNombre = null
        }
    }

    val scrollState = rememberScrollState()

    val formularioValido = nombre.isNotBlank() &&
            email.isNotBlank() &&
            telefono.isNotBlank() &&
            especialidad.isNotBlank() &&
            password.isNotBlank() &&
            estadoRegistro !is RegistroState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifySurface)
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(color = ServifyGreenPrimary, shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = "Logo Servify",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Perfil Profesional",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Regístrate para comenzar a recibir solicitudes.",
                fontSize = 14.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                etiqueta = "Nombre Completo",
                valor = nombre,
                onValorCambiado = { nombre = it },
                placeholder = "Ej. Mario Ramírez",
                iconoInicio = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Oficio o Especialidad",
                valor = especialidad,
                onValorCambiado = { especialidad = it },
                placeholder = "Ej. Plomero, Electricista, Soporte PC",
                iconoInicio = Icons.Outlined.Build
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Teléfono de Contacto",
                valor = telefono,
                onValorCambiado = { telefono = it },
                placeholder = "7000-0000",
                iconoInicio = Icons.Outlined.Phone,
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Correo Electrónico",
                valor = email,
                onValorCambiado = { email = it },
                placeholder = "tu@correo.com",
                iconoInicio = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "Contraseña",
                valor = password,
                onValorCambiado = { password = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Sección de curriculum
            Text(
                text = "Currículum o Certificados (Opcional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (pdfUri == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectorPdf.launch("application/pdf") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyGreenLight.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, ServifyGreenPrimary.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.UploadFile,
                            contentDescription = "Subir PDF",
                            tint = ServifyGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adjuntar CV en formato PDF",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ServifyGreenPrimary
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurface),
                    border = BorderStroke(1.dp, ServifyBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "PDF adjunto",
                            tint = ServifyGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pdfNombre ?: "documento.pdf",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ServifyTextTitle,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        IconButton(
                            onClick = {
                                pdfUri = null
                                pdfNombre = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Eliminar PDF",
                                tint = ServifyTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (estadoRegistro is RegistroState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ServifyGreenPrimary)
                }
            } else {
                BotonPrincipal(
                    texto = "Registrarme como Profesional",
                    onClick = {
                        viewModel.registrarUsuario(
                            nombre = nombre,
                            correo = email,
                            password = password,
                            telefono = telefono,
                            rol = "tecnico",
                            onSuccess = onRegistroExitoso
                        )
                    },
                    habilitado = formularioValido
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes cuenta? ", fontSize = 13.sp, color = ServifyTextMuted)
                Text(
                    text = "Inicia Sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyGreenPrimary,
                    modifier = Modifier.clickable { onIrALogin() }
                )
            }
        }
    }
}