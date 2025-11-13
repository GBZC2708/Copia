package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alphakids.ui.components.ErrorButton
import com.example.alphakids.ui.components.ErrorTonalButton
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily
import com.example.alphakids.ui.utils.MusicManager

@Composable
fun GameFailureScreen(
    imageUrl: String?,
    onRetryClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val context = LocalContext.current

    // --- Reproducir audio de fallo correctamente usando los métodos de MusicManager ---
    LaunchedEffect(Unit) {
        MusicManager.pauseMusicaApp()
        MusicManager.startMusicaFallo(context)
    }

    // --- Cuando el Composable se destruye, restauramos música global ---
    DisposableEffect(Unit) {
        onDispose {
            MusicManager.stopMusicaFallo()
            MusicManager.resumeMusicaApp()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FailureContent(
                imageUrl = imageUrl,
                onPrimaryAction = onRetryClick,
                onSecondaryAction = onExitClick
            )
        }
    }
}

@Composable
private fun FailureContent(
    imageUrl: String?,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentVeryDissatisfied,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "¡Se acabó el tiempo!",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de la palabra",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                IconContainer(
                    icon = Icons.Rounded.Checkroom,
                    contentDescription = "Palabra",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = "Vuelve a intentarlo",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ErrorButton(
                text = "Reintentar",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth()
            )
            ErrorTonalButton(
                text = "Salir",
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameFailureScreenPreview() {
    AlphakidsTheme {
        GameFailureScreen(
            imageUrl = null,
            onRetryClick = {},
            onExitClick = {}
        )
    }
}
