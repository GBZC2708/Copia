package com.example.alphakids.ui.screens.tutor.games

import android.Manifest
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.theme.dmSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraOCRScreen(
    assignmentId: String,
    targetWord: String,
    onBackClick: () -> Unit,
    onWordCompleted: () -> Unit,
    viewModel: CameraOCRViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var detectedText by remember { mutableStateOf("") }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isWordCompleted by remember { mutableStateOf(false) }

    // TTS Setup
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                tts?.setSpeechRate(0.9f)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    // Check for word completion
    LaunchedEffect(detectedText, targetWord) {
        if (!isWordCompleted && detectedText.trim().uppercase() == targetWord.trim().uppercase()) {
            isWordCompleted = true
            showSuccessAnimation = true

            // Play TTS
            tts?.speak(
                "¡Bien hecho! La palabra es $targetWord",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )

            // Save to storage
            WordHistoryStorage.saveCompletedWord(context, targetWord)

            // Hide animation after 3 seconds and complete
            delay(3000)
            onWordCompleted()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera Preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        previewView = this
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ROI Overlay
            val density = LocalDensity.current
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Define ROI dimensions (centered rectangle)
                val roiWidth = canvasWidth * 0.8f
                val roiHeight = canvasHeight * 0.3f
                val roiLeft = (canvasWidth - roiWidth) / 2
                val roiTop = (canvasHeight - roiHeight) / 2

                // Draw semi-transparent overlay
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset.Zero,
                    size = size
                )

                // Clear the ROI area
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(roiLeft, roiTop),
                    size = Size(roiWidth, roiHeight),
                    blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                )

                // Draw ROI border
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(roiLeft, roiTop),
                    size = Size(roiWidth, roiHeight),
                    style = Stroke(width = with(density) { 4.dp.toPx() })
                )
            }

            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Busca: $targetWord",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            )

            // Detected Text Display
            if (detectedText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "Texto detectado: $detectedText",
                        color = Color.White,
                        fontFamily = dmSansFamily,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Success Animation
            AnimatedVisibility(
                visible = showSuccessAnimation,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "¡Palabra Completada!",
                            color = Color.White,
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = targetWord,
                            color = Color.White,
                            fontFamily = dmSansFamily,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }

        // Setup Camera
        LaunchedEffect(previewView) {
            previewView?.let { preview ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProvider = cameraProviderFuture.get()

                setupCamera(
                    cameraProvider = cameraProvider!!,
                    previewView = preview,
                    lifecycleOwner = lifecycleOwner,
                    targetWord = targetWord,
                    onTextDetected = { text ->
                        detectedText = text
                    }
                )
            }
        }
    } else {
        // Permission Request
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Se necesita permiso de cámara para usar esta función",
                fontFamily = dmSansFamily,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() }
            ) {
                Text("Conceder Permiso")
            }
        }
    }
}



private fun setupCamera(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    targetWord: String,
    onTextDetected: (String) -> Unit
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    val imageAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(
                ContextCompat.getMainExecutor(previewView.context),
                TextAnalyzer(targetWord, onTextDetected)
            )
        }

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalyzer
        )
    } catch (exc: Exception) {
        Log.e("CameraOCR", "Use case binding failed", exc)
    }
}