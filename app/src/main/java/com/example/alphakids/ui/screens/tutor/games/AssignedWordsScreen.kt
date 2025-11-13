package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.ui.theme.dmSansFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedWordsScreen(
    studentId: String,
    onBackClick: () -> Unit,
    onWordClick: (AsignacionPalabra) -> Unit,
    viewModel: AssignedWordsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar asignaciones al entrar
    LaunchedEffect(studentId) {
        viewModel.loadAssignedWords(studentId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "\uD83D\uDE80 ¡Adivina y Escanea! \uD83D\uDE80",
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
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        when {
            uiState.isLoading -> LoadingState()

            uiState.error != null -> ErrorState(
                error = uiState.error ?: "",
                onRetry = { viewModel.loadAssignedWords(studentId) }
            )

            uiState.assignedWords.isEmpty() -> EmptyState(
                onRetry = { viewModel.loadAssignedWords(studentId) }
            )

            else -> AssignedWordsList(
                assignedWords = uiState.assignedWords,
                onWordClick = onWordClick
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No tienes palabras asignadas",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu tutor te asignará palabras para practicar",
                fontFamily = dmSansFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Actualizar")
            }
        }
    }
}

@Composable
private fun AssignedWordsList(
    assignedWords: List<AsignacionPalabra>,
    onWordClick: (AsignacionPalabra) -> Unit
) {
    val context = LocalContext.current

    // 🧠 Estado con palabras completadas
    var completedSet by remember { mutableStateOf<Set<String>>(emptySet()) }

    // 🔁 Actualiza reactivamente cada vez que el usuario completa una palabra
    LaunchedEffect(Unit) {
        while (true) {
            completedSet = WordStorage
                .getCompletedWords(context)
                .map { it.word.trim().uppercase() }
                .toSet()
            kotlinx.coroutines.delay(1000) // refresca cada segundo
        }
    }

    // ✨ 1. Evitar duplicados
    val uniqueAssignments = assignedWords.distinctBy {
        it.palabraTexto?.trim()?.uppercase()
    }

    // ✨ 2. Filtrar las completadas
    val pendingAssignments = uniqueAssignments.filter { assignment ->
        val word = (assignment.palabraTexto ?: "").trim().uppercase()
        word !in completedSet
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pendingAssignments) { assignment ->
            AssignedWordCard(
                assignment = assignment,
                onClick = { onWordClick(assignment) }
            )
        }
    }
}


@Composable
fun AssignedWordCard(
    assignment: AsignacionPalabra,
    onClick: () -> Unit
) {
    // Lógica para limpiar la dificultad
    val dificultadMostrada = if (assignment.palabraDificultad.isNullOrBlank() ||
        assignment.palabraDificultad.equals("desconocida", ignoreCase = true)) {
        "Normal"
    } else {
        assignment.palabraDificultad!! // Sabemos que no es nulo o blanco aquí
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        // --- CAMBIO: Quitamos el .clickable de la Card ---
        // .clickable { onClick() }, <-- ESTO SE FUE
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Mantener la alineación de la fila principal
        ) {
            // ... (Tu columna de Imagen y Categoría se mantiene igual) ...
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = assignment.palabraImagen,
                    contentDescription = assignment.palabraTexto,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Categoría: Neutra",
                    fontFamily = dmSansFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ... (Tu columna de Texto y Dificultad se mantiene igual) ...
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "*".repeat(assignment.palabraTexto?.length ?: 0),
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dificultad: $dificultadMostrada",
                    fontFamily = dmSansFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- ¡CAMBIO GRANDE AQUÍ! ---
            // Reemplazamos el DifficultyChip por un Button
            Button(
                onClick = onClick, // El botón ahora maneja el clic
                shape = RoundedCornerShape(16.dp), // Forma similar al chip
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp) // Padding
            ) {
                Text(
                    text = "Adivinar",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            // --- FIN DEL CAMBIO ---
        }
    }
}
