package com.example.servify.ui.componentes.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.navigation.Rutas
import com.example.servify.ui.theme.*

@Composable
fun TopBarConMenu(
    navController: NavController,
    titulo: String = "Servify",
    accionesDerecha: @Composable RowScope.() -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var mostrarDialogoCierre by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ServifyBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Título de la app a la izquierda
        Text(
            text = titulo,
            color = ServifyBluePrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Elementos extra opcionales (botón VIP)
            accionesDerecha()

            Spacer(modifier = Modifier.width(6.dp))

            // Botón de menú hamburguesa con superficie estilizada
            Box {
                Surface(
                    onClick = { expanded = true },
                    color = ServifySurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú de opciones",
                            tint = ServifyBluePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Menú desplegable con diseño moderno e iconos en fila
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(ServifySurface)
                        .width(235.dp)
                        .padding(vertical = 6.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = ServifyBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Ver Perfil", color = ServifyTextTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            expanded = false
                            navController.navigate(Rutas.Perfil.ruta)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = ServifyTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Términos y Condiciones", color = ServifyTextTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            expanded = false
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = ServifyTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Ayuda y Soporte", color = ServifyTextTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            expanded = false
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Divider(
                        color = ServifyBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = ServifyDanger,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Cerrar Sesión", color = ServifyDanger, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            expanded = false
                            mostrarDialogoCierre = true
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }

    // Diálogo de alerta para cerrar sesión con diseño moderno
    if (mostrarDialogoCierre) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCierre = false },
            containerColor = ServifySurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Cerrar Sesión",
                    color = ServifyTextTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cerrar sesión en Servify?",
                    color = ServifyTextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCierre = false
                        SessionManager.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServifyDanger)
                ) {
                    Text("Sí, salir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoCierre = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ServifyBluePrimary)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}