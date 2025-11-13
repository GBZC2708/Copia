package com.example.alphakids.ui.screens.tutor.games

import ScannerOverlay
import android.Manifest
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alphakids.ui.screens.tutor.game.components.GameResultDialog
import com.example.alphakids.ui.screens.tutor.game.components.GameResultState
import com.example.alphakids.ui.screens.tutor.games.components.CameraActionBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.example.alphakids.ui.screens.tutor.games.TextAnalyzer

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalPermissionsApi::class)
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

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status != PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val cameraController = rememberCameraController(context)

    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose {
            cameraController.unbind()
            cameraController.clearImageAnalysisAnalyzer()
        }
    }

    val scanState by viewModel.scanState.collectAsStateWithLifecycle()
    val isFlashEnabled by viewModel.isFlashEnabled.collectAsStateWithLifecycle()
    val cameraSelector by viewModel.cameraSelector.collectAsStateWithLifecycle()

    LaunchedEffect(isFlashEnabled) {
        runCatching { cameraController.enableTorch(isFlashEnabled) }
    }

    LaunchedEffect(cameraSelector) {
        cameraController.cameraSelector = cameraSelector
    }

    LaunchedEffect(cameraPermissionState.status, targetWord) {
        if (cameraPermissionState.status == PermissionStatus.Granted && targetWord.isNotBlank()) {
            cameraController.setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                TextAnalyzer(targetWord) { text ->
                    viewModel.onTextDetected(text)
                }
            )
        } else {
            cameraController.clearImageAnalysisAnalyzer()
        }
    }

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
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        controller = cameraController
                    }
                }
            )

            ScannerOverlay(
                modifier = Modifier.matchParentSize(),
                boxWidthPercent = 0.8f,
                cornerLength = 28.dp,
                cornerStroke = 6.dp,
                cornerRadius = 16.dp
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = "Une la palabra",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Apunta a las letras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Se necesita permiso de cámara", color = Color.White, fontFamily = dmSansFamily)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Conceder Permiso")
                }
            }

            CameraActionBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onFlashClick = { viewModel.toggleFlash() },
                onShutterClick = { viewModel.onShutter(targetWord) },
                onFlipCameraClick = { viewModel.flipCamera() }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = "Se necesita permiso de cámara para usar esta función",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(text = "Conceder permiso")
                }
            }
        }

        if (scanState is ScanUiState.Evaluating) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        val resultState = scanState
        if (resultState is ScanUiState.Result) {
            GameResultDialog(
                state = if (resultState.success) {
                    GameResultState.Success(word = targetWord, imageIcon = Icons.Rounded.Checkroom)
                } else {
                    GameResultState.Failure(imageIcon = Icons.Rounded.Checkroom)
                },
                onDismiss = onBackClick,
                onPrimaryAction = {
                    viewModel.resetToScanner()
                },
                onSecondaryAction = onBackClick
            )
        }
    }
}

@Composable
private fun rememberCameraController(context: android.content.Context): LifecycleCameraController {
    val controller = androidx.compose.runtime.remember(context) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE)
        }
    }
    return controller
}
