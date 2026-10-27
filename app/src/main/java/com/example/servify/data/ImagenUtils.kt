package com.example.servify.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import kotlin.io.use
import kotlin.use

fun uriAJpegBytes(
    resolver: ContentResolver,
    uri: Uri,
    maxLado: Int = 1280,
    calidad: Int = 80
): ByteArray? {
    // 1) Leer solo las dimensiones
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
    if (limites.outWidth <= 0) return null

    // dcodifica reducido para no gastar memoria
    var muestra = 1
    while (limites.outWidth / muestra > maxLado * 2 || limites.outHeight / muestra > maxLado * 2) muestra *= 2
    val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
    var bmp = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
        ?: return null

    // Corrige rotación según EXIF
    val grados = try {
        resolver.openInputStream(uri)?.use { s ->
            when (ExifInterface(s).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f
    } catch (e: Exception) { 0f }

    if (grados != 0f) {
        val m = Matrix().apply { postRotate(grados) }
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
    }

    // ajusta al tamaño máximo y comprimir a JPEG
    val escala = minOf(1f, maxLado.toFloat() / maxOf(bmp.width, bmp.height))
    val final = if (escala < 1f)
        Bitmap.createScaledBitmap(bmp, (bmp.width * escala).toInt(), (bmp.height * escala).toInt(), true)
    else bmp

    val salida = ByteArrayOutputStream()
    final.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
    return salida.toByteArray()
}