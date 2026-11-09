package com.example.servify.ui.modulos.modulos_compartidos.pagos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.*

enum class TipoPlan(
    val titulo: String,
    val precio: String,
    val monto: Double,
    val badge: String?,
    val beneficios: List<String>
) {
    ESTANDAR(
        titulo = "Plan Estándar",
        precio = "$3.99",
        monto = 3.99,
        badge = null,
        beneficios = listOf(
            "Hasta 15 postulaciones al mes",
            "Notificaciones de nuevos trabajos",
            "Perfil técnico visible para clientes"
        )
    ),
    VIP(
        titulo = "Plan Servify PRO",
        precio = "$6.99",
        monto = 6.99,
        badge = "POPULAR",
        beneficios = listOf(
            "Postulaciones ilimitadas a averías",
            "Insignia de Técnico Verificado VIP",
            "Aparece primero en las búsquedas de clientes",
            "Notificaciones instantáneas de nuevos trabajos"
        )
    )
}

@Composable
fun PlanesMembresiaScreen(
    onVolver: () -> Unit,
    onContinuarPago: (monto: Double, tipoPlan: String) -> Unit
) {
    var planSeleccionado by remember { mutableStateOf(TipoPlan.VIP) }

    Scaffold(
        containerColor = ServifyBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onVolver() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = ServifyBluePrimary
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Planes de Membresía",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ServifyBluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Impulsa tu Perfil Profesional",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ServifyTextTitle
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Consigue más clientes y postulaciones prioritarias con nuestros planes.",
                fontSize = 13.sp,
                color = ServifyTextMuted,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            TipoPlan.values().forEach { plan ->
                val esElegido = planSeleccionado == plan
                TarjetaOpcionPlan(
                    plan = plan,
                    seleccionado = esElegido,
                    onSelect = { planSeleccionado = plan }
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onContinuarPago(planSeleccionado.monto, planSeleccionado.titulo)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ServifyBluePrimary)
            ) {
                Text(
                    text = "Suscribirme al ${planSeleccionado.titulo}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TarjetaOpcionPlan(
    plan: TipoPlan,
    seleccionado: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (seleccionado) 2.dp else 1.dp,
            color = if (seleccionado) ServifyBluePrimary else ServifyBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ServifyTextTitle
                )

                if (plan.badge != null) {
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plan.badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ServifyBluePrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.precio,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ServifyBluePrimary
                )
                Text(
                    text = "/mes",
                    fontSize = 13.sp,
                    color = ServifyTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = ServifyBorder,
                thickness = 0.5.dp
            )

            plan.beneficios.forEach { beneficio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = beneficio,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ServifyTextBody
                    )
                }
            }
        }
    }
}