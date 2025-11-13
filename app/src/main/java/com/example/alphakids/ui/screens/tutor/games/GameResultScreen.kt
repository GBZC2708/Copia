package com.example.alphakids.ui.screens.tutor.games

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LetterBox
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.PrimaryTonalButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily
import com.example.alphakids.ui.utils.MusicManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// ----------------------------------------------------------------------
// ✅ IMPORTACIONES DE AUDIO (Necesarias para el audio de éxito)
// ----------------------------------------------------------------------
import com.example.alphakids.ui.screens.tutor.games.AUDIO_EXITO_URL

/**
 * Pantalla completa que muestra el resultado exitoso de un juego.
 */
@Composable
fun GameResultScreen(
    word: String,
    imageUrl: String?,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // 🎵 CONTROL DE MÚSICA Y AUDIO
    LaunchedEffect(Unit) {
        // 🛑 Detenemos toda música activa
        MusicManager.stopMusicaJuego()
        MusicManager.pauseMusicaApp()
        delay(500) // pequeño margen para liberar recursos

        // 🎶 Reproducir el audio de éxito con su propio MediaPlayer independiente
        withContext(Dispatchers.IO) {
            try {
                val player = MediaPlayer().apply {
                    setDataSource(AUDIO_EXITO_URL)
                    setOnPreparedListener { it.start() }
                    setOnCompletionListener {
                        it.release()
                    }
                    setOnErrorListener { mp, what, extra ->
                        mp.release()
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer = player
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // 🔄 Cuando el usuario salga de esta pantalla...
    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (_: Exception) {
            }
            mediaPlayer = null
            MusicManager.resumeMusicaApp() // 🔊 Reanudar música global
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SuccessContent(
                word = word,
                imageUrl = imageUrl,
                onPrimaryAction = onContinueClick,
                onSecondaryAction = onBackClick
            )
        }
    }
}

@Composable
private fun SuccessContent(
    word: String,
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
        // --- Título ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentSatisfied,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¡Lo lograste!",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- Imagen y Palabra ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de $word",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                IconContainer(
                    icon = Icons.Rounded.Checkroom,
                    contentDescription = "Palabra"
                )
            }

            // Muestra la palabra completada en LetterBoxes
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                word.uppercase().forEach { char ->
                    LetterBox(
                        letter = char,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // --- Botones de Acción ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryButton(
                text = "Continuar",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryTonalButton(
                text = "Volver al Menú",
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameResultScreenPreview() {
    AlphakidsTheme {
        GameResultScreen(
            word = "GATO",
            imageUrl = null,
            onContinueClick = {},
            onBackClick = {}
        )
    }
}
