package com.example.servify.ui.modulos.modulos_compartidos.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.servify.ui.componentes.camara.RostroAnalyzer
import com.example.servify.ui.componentes.camara.ResultadoRostro
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CapturaRostroScreen(
    subiendo: Boolean,
    onCancelar: () -> Unit,
    onFotoLista: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val analisisExecutor = remember { Executors.newSingleThreadExecutor() }
    val onFoto by rememberUpdatedState(onFotoLista)

    var permiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permiso = it }
    LaunchedEffect(Unit) { if (!permiso) lanzador.launch(Manifest.permission.CAMERA) }

    var mensaje by remember { mutableStateOf("Coloca tu rostro dentro del óvalo") }
    var valido by remember { mutableStateOf(false) }
    var parpadeoVisto by remember { mutableStateOf(false) }
    var capturando by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build() }

    fun tomarFoto() {
        val archivo = File(context.cacheDir, "perfil_${System.currentTimeMillis()}.jpg")
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(archivo).build(), mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(o: ImageCapture.OutputFileResults) { onFoto(Uri.fromFile(archivo)) }
                override fun onError(e: ImageCaptureException) {
                    capturando = false; parpadeoVisto = false
                    mensaje = "No se pudo tomar la foto, inténtalo de nuevo"
                }
            }
        )
    }

    fun procesar(r: ResultadoRostro) {
        if (capturando) return
        valido = r.valido
        if (!r.valido) { parpadeoVisto = false; mensaje = r.mensaje; return }
        if (r.ojosCerrados) { parpadeoVisto = true; mensaje = "Perfecto, ahora abre los ojos"; return }
        if (!parpadeoVisto) { mensaje = "Parpadea una vez para confirmar que eres tú"; return }
        capturando = true
        mensaje = "¡No te muevas!"
        tomarFoto()
    }

    val analyzer = remember { RostroAnalyzer { r -> mainExecutor.execute { procesar(r) } } }

    // Si la subida falla, se permite reintentar
    LaunchedEffect(subiendo) { if (!subiendo) { capturando = false; parpadeoVisto = false } }

    DisposableEffect(permiso) {
        if (!permiso) return@DisposableEffect onDispose { }
        val futuro = ProcessCameraProvider.getInstance(context)
        futuro.addListener({
            val proveedor = futuro.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                .also { it.setAnalyzer(analisisExecutor, analyzer) }
            proveedor.unbindAll()
            proveedor.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analisis, imageCapture)
        }, mainExecutor)
        onDispose { runCatching { futuro.get().unbindAll() } }
    }
    DisposableEffect(Unit) { onDispose { analyzer.cerrar(); analisisExecutor.shutdown() } }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (permiso) {
            AndroidView({ previewView }, Modifier.fillMaxSize())
        } else {
            Text("Necesitamos acceso a la cámara para tomar tu foto de perfil.",
                color = Color.White, modifier = Modifier.align(Alignment.Center).padding(32.dp))
        }

        // Máscara con óvalo guía
        Canvas(Modifier.fillMaxSize().graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
            drawRect(Color.Black.copy(alpha = 0.65f))
            val ancho = size.width * 0.72f
            val alto = ancho * 1.3f
            val esquina = Offset((size.width - ancho) / 2, size.height * 0.45f - alto / 2)
            drawOval(Color.Black, esquina, Size(ancho, alto), blendMode = BlendMode.Clear)
            drawOval(if (valido) Color(0xFF10B981) else Color.White, esquina, Size(ancho, alto), style = Stroke(6f))
        }

        IconButton(onClick = onCancelar, modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp, start = 12.dp)) {
            Icon(Icons.Default.Close, "Cancelar", tint = Color.White)
        }

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
                Text(
                    if (subiendo) "Subiendo tu foto…" else mensaje,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF052659)
                )
            }
            if (subiendo) CircularProgressIndicator(color = Color.White)
            Text("La foto se toma automáticamente y debe ser de tu rostro",
                color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}