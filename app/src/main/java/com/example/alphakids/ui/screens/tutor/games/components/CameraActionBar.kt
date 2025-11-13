package com.example.alphakids.ui.screens.tutor.games.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun CameraActionBar(
    modifier: Modifier = Modifier,
    onFlashClick: () -> Unit,
    onShutterClick: (() -> Unit)? = null, // <-- ¡CAMBIO 1! Hecho nulable
    onFlipCameraClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Lateral (Pequeño)
        CustomFAB(
            icon = Icons.Rounded.FlashOn,
            contentDescription = "Activar Flash",
            onClick = onFlashClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            containerSize = 52.dp,
            iconSize = 32.dp,
            shape = RoundedCornerShape(18.dp)
        )

        // Botón Central (Grande y Circular)
        // --- ¡CAMBIO 2! ---
        // Este botón solo aparece si onShutterClick NO es nulo
        if (onShutterClick != null) {
            CustomFAB(
                icon = Icons.Rounded.CameraAlt,
                contentDescription = "Tomar Foto",
                onClick = onShutterClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerSize = 68.dp,
                iconSize = 48.dp,
                shape = FloatingActionButtonDefaults.shape
            )
        }

        // Botón Lateral (Pequeño)
        CustomFAB(
            icon = Icons.Rounded.FlipCameraAndroid,
            contentDescription = "Voltear Cámara",
            onClick = onFlipCameraClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            containerSize = 52.dp,
            iconSize = 32.dp,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
fun CameraActionBarPreview() {
    AlphakidsTheme {
        CameraActionBar(
            onFlashClick = {},
            onShutterClick = {},
            onFlipCameraClick = {}
        )
    }
}

// Preview sin el botón de obturador
@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
fun CameraActionBarNoShutterPreview() {
    AlphakidsTheme {
        CameraActionBar(
            onFlashClick = {},
            onShutterClick = null, // <-- Así se verá en tu OCRScreen
            onFlipCameraClick = {}
        )
    }
}