package com.example.alphakids.ui.screens.tutor.games
import androidx.compose.ui.platform.LocalContext
import com.example.alphakids.ui.screens.tutor.games.WordHistoryStorage
import com.example.alphakids.ui.screens.tutor.games.CompletedWord
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
import com.example.alphakids.ui.screens.tutor.games.components.WordPuzzleCard
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
                    text = "Palabras Asignadas",
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadAssignedWords(studentId) }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            uiState.assignedWords.isEmpty() && !uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                        Button(
                            onClick = { viewModel.loadAssignedWords(studentId) }
                        ) {
                            Text("Actualizar")
                        }
                    }
                }
            }
            
            else -> {
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current
            
            // Palabras completadas normalizadas a UPPERCASE para comparación
            var completedSet by remember { mutableStateOf<Set<String>>(emptySet()) }
            LaunchedEffect(Unit) {
                completedSet = WordHistoryStorage
                    .getCompletedWords(context)
                    .map { it.word.trim().uppercase() }
                    .toSet()
            }
                val pendingAssignments = uiState.assignedWords.filter { assignment ->
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
        }
    }
}

@Composable
fun AssignedWordCard(
    assignment: AsignacionPalabra,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la palabra
            AsyncImage(
                model = assignment.palabraImagen,
                contentDescription = assignment.palabraTexto,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "", // no mostrar la palabra (en blanco)
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            
                Spacer(modifier = Modifier.height(4.dp))
            
                Text(
                    text = "Dificultad: ${assignment.palabraDificultad ?: "Normal"}",
                    fontFamily = dmSansFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Indicador de dificultad
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (assignment.palabraDificultad?.lowercase()) {
                    "fácil" -> Color(0xFF4CAF50)
                    "difícil" -> Color(0xFFF44336)
                    else -> Color(0xFFFF9800)
                }
            ) {
                Text(
                    text = assignment.palabraDificultad ?: "Normal",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}