package com.example.alphakids.ui.screens.tutor.games

import android.annotation.SuppressLint // <-- Importado
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextAnalyzer(
    private val targetWord: String,
    private val onTextDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var lastAnalyzedTimestamp = 0L
    private val throttleIntervalMs = 200L

    @SuppressLint("UnsafeOptInUsageError") // <-- ¡CORRECCIÓN AÑADIDA!
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp - lastAnalyzedTimestamp >= throttleIntervalMs) {
            lastAnalyzedTimestamp = currentTimestamp

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val detectedText = visionText.text
                        if (detectedText.isNotBlank()) {
                            onTextDetected(detectedText)
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle error silently or log if needed
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}