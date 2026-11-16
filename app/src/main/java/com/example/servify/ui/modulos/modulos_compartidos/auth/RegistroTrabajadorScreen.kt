package com.example.servify.ui.modulos.modulos_compartidos.auth

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servify.ui.componentes.botones.BotonPrincipal
import com.example.servify.ui.componentes.feedback.DialogoPoliticaPrivacidad
import com.example.servify.ui.componentes.feedback.DialogoTerminosCondiciones
import com.example.servify.ui.componentes.inputs.CampoTexto
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroTrabajadorScreen(
    onRegistroExitoso: () -> Unit = {},
    onIrALogin: () -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current
    val estadoRegistro by viewModel.estadoRegistro.collectAsState()
    val categoriasDisponibles by viewModel.categorias.collectAsState()
    val cargandoCategorias by viewModel.cargandoCategorias.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var menuCategoriasExpandido by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }

    var mostrarModalTerminos by remember { mutableStateOf(false) }
    var mostrarModalPoliticas by remember { mutableStateOf(false)}

    LaunchedEffect(Unit) {
        if (categoriasDisponibles.isEmpty()) {
            viewModel.cargarCategorias()
        }
    }


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

    val scrollState = rememberScrollState()

    val formularioValido = nombre.isNotBlank() &&
            email.isNotBlank() &&
            telefono.isNotBlank() &&
            especialidad.isNotBlank() &&
            aceptoTerminos &&
            password.isNotBlank() &&
            estadoRegistro !is RegistroState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ServifyBackground)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Retroceder a la izquierda
                IconButton(
                    onClick = { onVolver() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ServifySurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar",
                        tint = ServifyBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Icono / Herramienta a la derecha
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ServifyBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Build,
                        contentDescription = "Icono Profesional",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Perfil Profesional",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ServifyTextTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Regístrate para comenzar a recibir solicitudes.",
                fontSize = 13.sp,
                color = ServifyTextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                etiqueta = "NOMBRE COMPLETO",
                valor = nombre,
                onValorCambiado = { nombre = it },
                placeholder = "Ej. Mario Ramírez",
                iconoInicio = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Desplegable de Categorías
            Text(
                text = "OFICIO O ESPECIALIDAD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyBluePrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = menuCategoriasExpandido,
                onExpandedChange = {
                    if (!cargandoCategorias) {
                        menuCategoriasExpandido = !menuCategoriasExpandido
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                val textoVisual = when {
                    especialidad.isNotBlank() -> especialidad
                    cargandoCategorias -> "Cargando especialidades..."
                    else -> ""
                }

                OutlinedTextField(
                    value = textoVisual,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            text = "Selecciona tu especialidad",
                            color = ServifyTextPlaceholder,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                            tint = ServifyBluePrimary
                        )
                    },
                    trailingIcon = {
                        if (cargandoCategorias) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = ServifyBluePrimary
                            )
                        } else {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriasExpandido)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ServifySurface,
                        unfocusedContainerColor = ServifySurface,
                        focusedBorderColor = ServifyBluePrimary,
                        unfocusedBorderColor = ServifyBorder,
                        focusedTextColor = ServifyTextTitle,
                        unfocusedTextColor = ServifyTextBody
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = menuCategoriasExpandido,
                    onDismissRequest = { menuCategoriasExpandido = false },
                    modifier = Modifier.background(ServifySurface)
                ) {
                    if (categoriasDisponibles.isEmpty() && !cargandoCategorias) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = null,
                                        tint = ServifyBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reintentar cargar", color = ServifyBluePrimary, fontSize = 14.sp)
                                }
                            },
                            onClick = {
                                viewModel.cargarCategorias()
                            }
                        )
                    } else {
                        categoriasDisponibles.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = cat.nombre,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ServifyTextBody
                                    )
                                },
                                onClick = {
                                    especialidad = cat.nombre
                                    menuCategoriasExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "TELÉFONO DE CONTACTO",
                valor = telefono,
                onValorCambiado = { telefono = it },
                placeholder = "7000-0000",
                iconoInicio = Icons.Outlined.Phone,
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "CORREO ELECTRÓNICO",
                valor = email,
                onValorCambiado = { email = it },
                placeholder = "tu@correo.com",
                iconoInicio = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampoTexto(
                etiqueta = "CONTRASEÑA",
                valor = password,
                onValorCambiado = { password = it },
                placeholder = "••••••••",
                iconoInicio = Icons.Outlined.Lock,
                esPassword = true,
                tipoTeclado = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(18.dp))


            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it }
                )

                // Contenedor para los textos de los enlaces
                Row(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Acepto los ", fontSize = 12.sp, color = ServifyTextBody)
                    Text(
                        text = "Términos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { mostrarModalTerminos = true }
                    )
                    Text(text = " y las ", fontSize = 12.sp, color = ServifyTextBody)
                    Text(
                        text = "Políticas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { mostrarModalPoliticas = true }
                    )
                }
            }

            if (estadoRegistro is RegistroState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ServifyBluePrimary)
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

            Spacer(modifier = Modifier.height(20.dp))

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
                    color = ServifyBluePrimary,
                    modifier = Modifier.clickable { onIrALogin() }
                )
            }
        }
        if (mostrarModalTerminos) {
            DialogoTerminosCondiciones(
                rol = "TECNICO",
                onDismiss = { mostrarModalTerminos = false },
                onAceptar = {
                    aceptoTerminos = true
                    mostrarModalTerminos = false
                }
            )
        }

        if (mostrarModalPoliticas){
            DialogoPoliticaPrivacidad(
                onDismiss = {mostrarModalPoliticas=false},
                onAceptar = {
                    aceptoTerminos = true
                    mostrarModalPoliticas=false
                }
            )
        }
    }
}