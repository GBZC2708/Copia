package com.example.alphakids.ui.screens.tutor.games

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alphakids.ui.screens.tutor.games.components.WordPuzzleCard
import com.example.alphakids.ui.theme.dmSansFamily
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPuzzleScreen(
    assignmentId: String,
    studentId: String,
    onBackClick: () -> Unit,
    onTakePhotoClick: (String) -> Unit,
    viewModel: WordPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedWord by viewModel.selectedWord.collectAsStateWithLifecycle()

    LaunchedEffect(assignmentId) {
        viewModel.loadWordData(assignmentId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "\uD83D\uDD0D ¡Busca las Letras! \uD83D\uDD0D",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = dmSansFamily
                    )
                }
            }

            // --- ¡CAMBIO 2! Añadido 'else if' para manejar el caso de que 'assignment' sea nulo ---
            uiState.assignment != null -> {
                // Asignamos las variables que necesitamos
                val targetWord = uiState.assignment!!.palabraTexto
                val wordImage = uiState.assignment!!.palabraImagen

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // ... en tu WordPuzzleScreen.kt

                    WordPuzzleCard(
                        wordLength = selectedWord?.length ?: 0,
                        wordImage = uiState.assignment?.palabraImagen,
                        difficulty = uiState.assignment?.palabraDificultad ?: "Normal",
                        onTakePhotoClick = {
                            selectedWord?.let(onTakePhotoClick)
                        }
                    )
// ...
                }
            }
        }
    }
}