package com.example.alphakids.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    recognizedText: String = "",
    timeLeft: Int = 0
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val boxSize = width * 0.7f
        val left = (width - boxSize) / 2
        val top = (height - boxSize) / 3
        drawRect(
            color = Color.Green,
            style = Stroke(width = 6f),
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(boxSize, boxSize)
        )
    }
}


