package com.example.servify.ui.modulos.trabajador

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.servify.data.SessionManager
import com.example.servify.ui.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesMembresiaScreen(
    navController: NavController,
    onPlanAdquirido: () -> Unit
) {
    val verdeServify = Color(0xFF1B7B61)
    val doradoVip = Color(0xFFD97706)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes de Membresía", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = doradoVip,
                modifier = Modifier.size(50.dp)
            )

            Text(
                text = "Impulsa tu Perfil Profesional",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Text(
                text = "Consigue más clientes y postulaciones prioritarias con nuestros planes.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta Plan Pro / VIP
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, doradoVip),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Plan Servify PRO", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = doradoVip)
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                "POPULAR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = doradoVip
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$9.99", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(" / mes", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    BeneficioItem("Postulaciones ilimitadas a averías")
                    BeneficioItem("Insignia de Técnico Verificado VIP")
                    BeneficioItem("Aparece primero en las búsquedas de clientes")
                    BeneficioItem("Notificaciones instantáneas de nuevos trabajos")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            navController.navigate(Rutas.MetodosPago.ruta) // Navega a la pantalla de pago
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = doradoVip)
                    ) {
                        Text("Suscribirme al Plan Pro", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun BeneficioItem(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, fontSize = 13.sp, color = Color.DarkGray)
    }
}