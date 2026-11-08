package com.example.servify.ui.componentes

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

object QrGenerator {
    fun generarBitmapQr(contenido: String, ancho: Int = 512, alto: Int = 512): Bitmap? {
        return try {
            val bitMatrix = MultiFormatWriter().encode(
                contenido,
                BarcodeFormat.QR_CODE,
                ancho,
                alto
            )
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val pixels = IntArray(matrixWidth * matrixHeight)

            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}