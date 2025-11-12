package com.example.alphakids.ui.screens.tutor.games

import ScannerOverlay
import android.Manifest
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MeteringPoint
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.alphakids.ui.components.NotificationCard
import com.example.alphakids.ui.components.TimerBar
import com.example.alphakids.ui.screens.tutor.games.components.CameraActionBar
import com.example.alphakids.ui.theme.dmSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors
import com.example.alphakids.ui.screens.tutor.games.WordStorage
import com.example.alphakids.ui.utils.MusicManager
import android.media.MediaPlayer

// 🔊 URLs de audio desde Firebase Storage
private const val AUDIO_EXITO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/audio_exito.mp3?alt=media&token=d484c88c-253e-4f41-a638-04da263d476a"

private const val AUDIO_FALLO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/audio_fallo.mp3?alt=media&token=EL_TOKEN_DE_FALLO"

// 🔊 MediaPlayer para efectos
private var sfxPlayer: MediaPlayer? = null

// 🔊 Función para reproducir efectos de sonido con control de volumen
fun playSfxAudioFromUrl(context: Context, url: String) {
    try {
        // 🔇 BAJAR volumen mientras suena el efecto
        MusicManager.setAppVolume(0.05f)
        MusicManager.setJuegoVolume(0.05f)

        sfxPlayer?.release()
        sfxPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { it.start() }
            setOnCompletionListener {
                it.release()
                sfxPlayer = null

                // 🔊 RESTAURAR volumen normal
                MusicManager.setAppVolume(1f)
                MusicManager.setJuegoVolume(1f)
            }
            prepareAsync()
        }
    } catch (e: Exception) {
        Log.e("AudioPlayer", "Error al reproducir SFX: ${e.message}")
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraOCRScreen(
    assignmentId: String,
    targetWord: String,
    studentId: String,
    targetImageUrl: String?,
    onBackClick: () -> Unit,
    onWordCompleted: (word: String, imageUrl: String?, studentId: String) -> Unit,
    onTimeExpired: (imageUrl: String?, studentId: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // --- ESTADOS ---
    var detectedText by remember { mutableStateOf("") }
    var isWordCompleted by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var torchOn by remember { mutableStateOf(false) }
    var lensFacingBack by remember { mutableStateOf(true) }
    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }
    var roiRect by remember { mutableStateOf<FloatArray?>(null) }
    var showNotification by remember { mutableStateOf(true) }

    val totalMillis = 60_000L
    var remainingMillis by remember { mutableStateOf(totalMillis) }
    var progress by remember { mutableStateOf(0f) }
    var isWarning by remember { mutableStateOf(false) }

    // Controlador de cámara
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Libera cámara seguro
    suspend fun safeReleaseCamera(delayMs: Long = 300L) {
        try {
            cameraController.unbind()
        } catch (_: Exception) { }
        try {
            executor.shutdownNow()
        } catch (_: Exception) { }
        delay(delayMs)
    }

    // Inicializar TTS + Música del juego
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                tts?.setSpeechRate(0.9f)

                MusicManager.pauseMusicaApp()
                MusicManager.startMusicaJuego(context)
            }
        }
    }

    // onDispose: liberar audio/cámara
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraController.unbind()
                executor.shutdownNow()
                tts?.stop()
                tts?.shutdown()
            } catch (_: Exception) {}

            sfxPlayer?.release()
            MusicManager.stopMusicaJuego()
            MusicManager.resumeMusicaApp()
        }
    }

    // Vincular cámara
    LaunchedEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    // OCR Analyzer
    LaunchedEffect(cameraController, targetWord) {
        val textAnalyzer = TextAnalyzer(
            targetWord = targetWord,
            onTextDetected = { text ->
                scope.launch(Dispatchers.Main) {
                    detectedText = text
                }
            }
        )
        cameraController.setImageAnalysisAnalyzer(executor, textAnalyzer)
    }

    // --- LÓGICA DE ÉXITO ---
    LaunchedEffect(detectedText, targetWord) {
        val cleanDetectedText = detectedText.trim().uppercase()
        val cleanTarget = targetWord.trim().uppercase()

        if (!isWordCompleted &&
            !isNavigating &&
            cleanDetectedText.contains(cleanTarget)
        ) {
            isWordCompleted = true
            isNavigating = true

            WordStorage.saveCompletedWord(context, targetWord, assignmentId)

            // 🔊 Sonido de éxito
            MusicManager.stopMusicaJuego()
            playSfxAudioFromUrl(context, AUDIO_EXITO_URL)

            scope.launch {
                withContext(Dispatchers.IO) { safeReleaseCamera() }
                withContext(Dispatchers.Main) {

                    delay(1200) // esperar a que el sonido termine

                    MusicManager.resumeMusicaApp()

                    onWordCompleted(targetWord, targetImageUrl, studentId)
                }
            }
        }
    }

    // --- LÓGICA DE PERDER ---
    LaunchedEffect(isWordCompleted) {
        while (remainingMillis > 0 && !isWordCompleted) {
            delay(1000)

            if (!isWordCompleted) {
                remainingMillis -= 1000
                progress = 1f - (remainingMillis.toFloat() / totalMillis)
                isWarning = remainingMillis <= 10_000L

                // Reproduce fallo SOLO en los últimos 10s
                if (isWarning) {
                    playSfxAudioFromUrl(context, AUDIO_FALLO_URL)
                }
            }
        }

        if (!isWordCompleted && remainingMillis <= 0) {
            isWordCompleted = true
            onTimeExpired(targetImageUrl, studentId)

            scope.launch {
                try { cameraController.unbind() } catch (_: Exception) {}
                delay(200)
                onTimeExpired(targetImageUrl, studentId)
            }
        }
    }

    fun formatTime(ms: Long): String {
        val s = (ms / 1000).toInt()
        return String.format("%d:%02d", s / 60, s % 60)
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        if (cameraPermissionState.status == PermissionStatus.Granted) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        controller = cameraController
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewViewRef.value = this
                    }
                }
            )
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Se necesita permiso de cámara",
                    color = Color.White,
                    fontFamily = dmSansFamily
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Conceder Permiso")
                }
            }
        }

        // Enfoque automático
        LaunchedEffect(roiRect, previewViewRef.value) {
            val pv = previewViewRef.value
            val rect = roiRect
            if (pv != null && rect != null && pv.width > 0) {
                val cx = ((rect[0] + rect[2]) / 2f) * pv.width
                val cy = ((rect[1] + rect[3]) / 2f) * pv.height
                val point = pv.meteringPointFactory.createPoint(cx, cy)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .addPoint(point, FocusMeteringAction.FLAG_AE)
                    .build()
                try { cameraController.cameraControl?.startFocusAndMetering(action) } catch (_: Exception) {}
            }
        }

        // Overlay del escáner
        ScannerOverlay(
            modifier = Modifier.fillMaxSize(),
            boxWidthPercent = 0.8f,
            boxAspectRatio = 1.6f,
            onBoxRectChange = { l, t, r, b ->
                roiRect = floatArrayOf(l, t, r, b)
            }
        )

        // Top Bar + Notificación
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(16.dp))
                TimerBar(
                    modifier = Modifier.weight(1f),
                    progress = progress,
                    timeText = formatTime(remainingMillis),
                    isWarning = isWarning
                )
            }

            if (showNotification) {
                NotificationCard(
                    modifier = Modifier.padding(top = 12.dp),
                    title = "Busca la palabra:",
                    content = targetWord,
                    imageUrl = targetImageUrl,
                    icon = Icons.Rounded.Checkroom,
                    onCloseClick = { showNotification = false }
                )
            }
        }

        // Barra inferior
        CameraActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onFlashClick = {
                torchOn = !torchOn
                cameraController.enableTorch(torchOn)
            },
            onShutterClick = null,
            onFlipCameraClick = {
                lensFacingBack = !lensFacingBack
                cameraController.cameraSelector =
                    if (lensFacingBack) CameraSelector.DEFAULT_BACK_CAMERA
                    else CameraSelector.DEFAULT_FRONT_CAMERA
            }
        )

        // Texto detectado
        if (detectedText.isNotEmpty() && !isWordCompleted) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    "Detectado: $detectedText",
                    color = Color.White,
                    fontFamily = dmSansFamily,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
