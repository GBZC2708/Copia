package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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

/**
 * Pantalla completa que muestra el resultado exitoso de un juego.
 *
 * @param word La palabra que el usuario completó.
 * @param imageUrl La URL de la imagen asociada a la palabra (opcional).
 * @param onContinueClick Lambda para navegar a la siguiente actividad (ej. AssignedWordsScreen).
 * @param onBackClick Lambda para navegar de vuelta al menú principal (ej. StudentHomeScreen).
 */
@Composable
fun GameResultScreen(
    word: String,
    imageUrl: String?,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
            // Contenido de éxito adaptado del GameResultDialog
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
            // Adaptado de WordPuzzleCard para mostrar la imagen de la URL
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
                // Fallback si no hay imagen (como en el Dialog)
                IconContainer(
                    icon = Icons.Rounded.Checkroom, // Icono de fallback
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
            imageUrl = null, // Probar sin imagen
            onContinueClick = {},
            onBackClick = {}
        )
    }
}

