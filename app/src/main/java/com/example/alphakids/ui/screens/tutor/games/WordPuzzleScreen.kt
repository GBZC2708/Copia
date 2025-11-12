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
import androidx.navigation.NavController // <-- ¡IMPORTADO!
import com.example.alphakids.navigation.Routes
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
    navController: NavController, // <-- ¡CAMBIO 1! Añadido NavController
    viewModel: WordPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        wordLength = targetWord.length,
                        wordImage = wordImage,
                        difficulty = uiState.assignment!!.palabraDificultad,

                        onTakePhotoClick = {
                            // 1. Codificar la URL opcional de la imagen
                            val encodedUrl = wordImage?.let {
                                URLEncoder.encode(it, StandardCharsets.UTF_8.name())
                            }

                            // 2. Codificar la palabra objetivo (CRÍTICO: por si tiene espacios)
                            val encodedTargetWord = URLEncoder.encode(targetWord, StandardCharsets.UTF_8.name())

                            // 3. 🚨 USAR LA FUNCIÓN HELPER DEL ARCHIVO ROUTES.KT CORREGIDO
                            val route = Routes.cameraOcrRoute(
                                assignmentId = assignmentId,
                                targetWord = encodedTargetWord, // Usa la palabra codificada
                                studentId = studentId,
                                imageUrl = encodedUrl // Usa la URL codificada (puede ser null)
                            )

                            Log.d("!!! DEBUG !!!", "Navegando con RUTA CORREGIDA: $route")
                            // Ejemplo de ruta generada ahora: camera_ocr/3UbgGgbvj0W9H5c3hrQ8/sapo/U53IJFxxGBs9SE2kjGde?imageUrl=...
                            navController.navigate(route)
                        }
                    )
// ...
                }
            }
        }
    }
}