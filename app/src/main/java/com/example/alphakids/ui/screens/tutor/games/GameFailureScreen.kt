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
import com.example.alphakids.ui.components.ErrorButton
import com.example.alphakids.ui.components.ErrorTonalButton
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Pantalla completa que muestra que el tiempo se agotó.
 *
 * @param imageUrl La URL de la imagen asociada a la palabra (opcional).
 * @param onRetryClick Lambda para navegar de vuelta al puzzle (ej. WordPuzzleScreen).
 * @param onExitClick Lambda para navegar de vuelta al menú principal (ej. StudentHomeScreen).
 */
@Composable
fun GameFailureScreen(
    imageUrl: String?,
    onRetryClick: () -> Unit,
    onExitClick: () -> Unit
) {
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
            // Contenido de fallo adaptado del GameResultDialog
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
        // --- Título ---
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
                text = "¡Se acabó el tiempo!", // <-- Mensaje personalizado
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        // --- Imagen ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Usamos AsyncImage para mostrar la imagen de la URL
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
                // Fallback si no hay imagen (como en el Dialog)
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

        // --- Botones de Acción ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ErrorButton(
                text = "Reintentar", // <-- Texto del botón
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
            imageUrl = null, // Probar sin imagen
            onRetryClick = {},
            onExitClick = {}
        )
    }
}


