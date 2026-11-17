package com.example.servify.ui.componentes.camara

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.abs

data class ResultadoRostro(val valido: Boolean, val ojosCerrados: Boolean, val mensaje: String)

class RostroAnalyzer(private val onResultado: (ResultadoRostro) -> Unit) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(proxy: ImageProxy) {
        val media = proxy.image
        if (media == null) { proxy.close(); return }
        val rot = proxy.imageInfo.rotationDegrees
        val ancho = if (rot % 180 == 0) proxy.width else proxy.height
        val alto = if (rot % 180 == 0) proxy.height else proxy.width

        detector.process(InputImage.fromMediaImage(media, rot))
            .addOnSuccessListener { onResultado(evaluar(it, ancho, alto)) }
            .addOnFailureListener { onResultado(ResultadoRostro(false, false, "No se pudo analizar la imagen")) }
            .addOnCompleteListener { proxy.close() }
    }

    private fun evaluar(caras: List<Face>, ancho: Int, alto: Int): ResultadoRostro {
        if (caras.isEmpty()) return ResultadoRostro(false, false, "No detectamos tu rostro")
        if (caras.size > 1) return ResultadoRostro(false, false, "Debe aparecer una sola persona")

        val cara = caras[0]
        val caja = cara.boundingBox
        val proporcion = caja.width().toFloat() / ancho
        if (proporcion < 0.35f) return ResultadoRostro(false, false, "Acércate un poco")
        if (proporcion > 0.85f) return ResultadoRostro(false, false, "Aléjate un poco")

        val cx = caja.centerX().toFloat() / ancho
        val cy = caja.centerY().toFloat() / alto
        if (abs(cx - 0.5f) > 0.15f || abs(cy - 0.5f) > 0.2f)
            return ResultadoRostro(false, false, "Centra tu rostro en el óvalo")

        if (abs(cara.headEulerAngleY) > 12f || abs(cara.headEulerAngleX) > 15f || abs(cara.headEulerAngleZ) > 12f)
            return ResultadoRostro(false, false, "Mira de frente a la cámara")

        val izq = cara.leftEyeOpenProbability
        val der = cara.rightEyeOpenProbability
        val cerrados = izq != null && der != null && izq < 0.3f && der < 0.3f
        return ResultadoRostro(true, cerrados, "")
    }

    fun cerrar() = detector.close()
}