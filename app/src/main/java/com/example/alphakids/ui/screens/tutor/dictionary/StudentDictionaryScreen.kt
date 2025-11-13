package com.example.alphakids.ui.screens.tutor.dictionary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.*
import com.example.alphakids.ui.screens.tutor.games.WordStorage
import com.example.alphakids.ui.theme.AlphakidsTheme
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

@Composable
fun StudentDictionaryScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "dictionary"
) {
    val context = LocalContext.current

    // 🧠 Estado reactivo que contiene las palabras completadas
    var completedWords by remember { mutableStateOf(WordStorage.getCompletedWords(context)) }

    // 🔁 Efecto para refrescar el listado cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            completedWords = WordStorage.getCompletedWords(context)
            delay(1000)
        }
    }

    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("store", "Tienda", Icons.Rounded.Store),
        BottomNavItem("pets", "Mascotas", Icons.Rounded.Pets)
    )

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Mi Diccionario",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", modifier = Modifier.size(24.dp))
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", modifier = Modifier.size(24.dp))
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                items = studentItems,
                currentRoute = currentRoute,
                onNavigate = onBottomNavClick
            )
        },
        floatingActionButton = {
            CustomFAB(
                icon = Icons.Rounded.Settings,
                contentDescription = "Configuración",
                onClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholderText = "Buscar en mi diccionario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(text = "Aprendidas", isSelected = true)
                InfoChip(text = "Todas", isSelected = false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔍 Filtro por búsqueda
            val filteredWords = completedWords.filter {
                it.word.contains(searchQuery, ignoreCase = true)
            }

            // 🧾 Si no hay palabras completadas
            if (filteredWords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no has completado palabras.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredWords.size) { index ->
                        val word = filteredWords[index]
                        WordListItem(
                            title = word.word,
                            subtitle = "Aprendida el ${formatDate(word.timestamp)}",
                            icon = Icons.Rounded.Checkroom,
                            chipText = "Completada",
                            isSelected = false,
                            onClick = { onWordClick(word.word) },
                            imageUrl = null
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun StudentDictionaryScreenPreview() {
    AlphakidsTheme {
        StudentDictionaryScreen(
            onBackClick = {},
            onLogoutClick = {},
            onWordClick = {},
            onSettingsClick = {},
            onBottomNavClick = {}
        )
    }
}
