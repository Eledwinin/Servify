package com.example.servify.data.model

import com.google.gson.annotations.SerializedName

/**
 * Petición enviada al Backend para crear el PaymentSheet de Stripe.
 * Endpoint: POST /api/pagos/crear-payment-sheet
 * Soporta solicitudes de servicio ordinarias (solicitudId) y suscripciones VIP de técnicos (tipo = "vip").
 */
data class CrearPaymentSheetRequest(
    @SerializedName("solicitud_id", alternate = ["solicitudId"])
    val solicitudId: Int? = null,

    @SerializedName("monto")
    val monto: Double,

    @SerializedName("tipo")
    val tipo: String? = null
)

/**
 * Respuesta devuelta por el Backend con los datos necesarios para inicializar y presentar
 * el PaymentSheet en Android.
 */
data class PaymentSheetResponse(
    @SerializedName("ok")
    val ok: Boolean? = true,

    @SerializedName("paymentIntent", alternate = ["payment_intent"])
    val paymentIntent: String,

    @SerializedName("paymentIntentId", alternate = ["payment_intent_id"])
    val paymentIntentId: String? = null,

    @SerializedName("ephemeralKey", alternate = ["ephemeral_key"])
    val ephemeralKey: String,

    @SerializedName("customer", alternate = ["customer_id", "customerId"])
    val customer: String,

    @SerializedName("publishableKey", alternate = ["publishable_key"])
    val publishableKey: String
)

/**
 * Representa los diferentes estados de la máquina de estados del flujo de pago con Stripe.
 */
sealed interface EstadoPago {
    object Idle : EstadoPago
    object Cargando : EstadoPago
    data class ListoParaPresentar(val datos: PaymentSheetResponse, val tipoPlan: String = "") : EstadoPago
    data class Exitoso(val mensaje: String = "¡Pago completado con éxito!", val tipoPlan: String = "") : EstadoPago
    data class Cancelado(val mensaje: String = "Operación de pago cancelada") : EstadoPago
    data class Error(val mensaje: String) : EstadoPago
}
