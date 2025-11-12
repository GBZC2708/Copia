package com.example.alphakids.ui.screens.teacher.words

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.alphakids.domain.models.Word
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.LetterBox
import com.example.alphakids.ui.components.LabeledTextField // Mantener import
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.PrimaryIconButton
import com.example.alphakids.ui.components.SecondaryTonalButton
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.screens.teacher.words.components.ImageUploadBox
import com.example.alphakids.ui.theme.dmSansFamily
import com.example.alphakids.ui.word.WordUiState
import com.example.alphakids.ui.word.WordViewModel

// *** Implementación de LabeledDropdownField refactorizado para usar ExposedDropdownMenuBox ***
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledDropdownField(
    label: String,
    selectedOption: String,
    options: List<String>,
    placeholderText: String,
    onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Este es el campo de texto que muestra la opción seleccionada
            OutlinedTextField(
                value = selectedOption.ifEmpty { placeholderText },
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholderText) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expandir menú",
                        modifier = Modifier
                            .clickable { isExpanded = true }
                            .menuAnchor() // Necesario para ExposedDropdownMenuBox
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(28.dp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    // Cambiar containerColor a unfocusedContainerColor (o mantener containerColor si la versión lo permite)
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // Añadir este para el estado de foco
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    // Reemplazar readOnlyTextColor por disabledTextColor ya que readOnly=true
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Este es el menú desplegable
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
// *** Fin de la implementación de LabeledDropdownField refactorizado ***


@Composable
fun WordEditScreen(
    viewModel: WordViewModel = hiltViewModel(),
    word: Word? = null,
    isEditing: Boolean,
    onCloseClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val context = LocalContext.current

    // --- Definición de las listas de opciones ---
    val dificultadOptions = listOf("Fácil", "Intermedio", "Difícil")
    val categoriaOptions = listOf("Animales", "Objetos", "Comida", "Lugares") // Se puede editar

    val wordUiState by viewModel.uiState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    var texto by remember(word) { mutableStateOf(word?.texto ?: "") }
    var categoria by remember(word) { mutableStateOf(word?.categoria ?: "") }
    var dificultad by remember(word) { mutableStateOf(word?.nivelDificultad ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.setSelectedImageUri(uri)
            }
        }
    )

    LaunchedEffect(wordUiState) {
        when (wordUiState) {
            is WordUiState.Success -> {
                Toast.makeText(context, (wordUiState as WordUiState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
                onCloseClick()
            }
            is WordUiState.Error -> {
                Toast.makeText(context, (wordUiState as WordUiState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            WordUiState.Loading -> {}
            WordUiState.Idle -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = if (isEditing) "Editar palabra" else "Crear palabra",
                actionIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LabeledTextField(
                label = "Palabra",
                value = texto,
                onValueChange = { texto = it },
                placeholderText = "Escribe la palabra"
            )

            Text(
                text = "Imagen",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            ImageUploadBox(
                imageUri = selectedImageUri,
                imageUrl = word?.imagenUrl,
                onClick = { imagePickerLauncher.launch("image/*") }
            )

            // --- Uso del Dropdown para Categoría ---
            LabeledDropdownField(
                label = "Categoría",
                selectedOption = categoria,
                options = categoriaOptions,
                placeholderText = "Selecciona categoría",
                onOptionSelected = { categoria = it }
            )

            // --- Uso del Dropdown para Dificultad ---
            LabeledDropdownField(
                label = "Dificultad",
                selectedOption = dificultad,
                options = dificultadOptions,
                placeholderText = "Selecciona dificultad",
                onOptionSelected = { dificultad = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                SecondaryTonalButton(text = "Cancelar", onClick = onCancelClick)
                PrimaryButton(
                    text = if (isEditing) "Guardar" else "Crear palabra",
                    icon = if (!isEditing) Icons.Rounded.Add else null,
                    enabled = wordUiState != WordUiState.Loading,
                    onClick = {
                        if (isEditing && word != null) {
                            val updatedWord = word.copy(
                                texto = texto,
                                categoria = categoria,
                                nivelDificultad = dificultad,
                                imagenUrl = word.imagenUrl
                            )
                            viewModel.updateWord(updatedWord)
                        } else {
                            viewModel.createWord(
                                texto = texto,
                                categoria = categoria,
                                nivelDificultad = dificultad,
                                audioUrl = "url_audio_mock"
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Vista previa",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WordPreview(
                        word = texto,
                        imageUri = selectedImageUri,
                        imageUrl = word?.imagenUrl,
                        icon = Icons.Rounded.Checkroom,
                        difficulty = dificultad.ifEmpty { "Fácil" }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
//... (El resto de la función WordPreview se mantiene sin cambios)
@Composable
private fun WordPreview(
    word: String,
    imageUri: Uri? = null,
    imageUrl: String? = null,
    icon: ImageVector,
    difficulty: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val imageSource = imageUri ?: imageUrl
            if (imageSource != null && (imageSource is Uri || (imageSource is String && imageSource.isNotEmpty()))) {
                AsyncImage(
                    model = imageSource,
                    contentDescription = "Vista previa de la palabra",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "¿Qué es esto?",
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(word.length.coerceAtLeast(4)) {
                LetterBox(
                    letter = null,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        PrimaryIconButton(
            icon = Icons.Rounded.CameraAlt,
            contentDescription = null,
            onClick = {},
            enabled = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoChip(
            text = difficulty,
            isSelected = true
        )
    }
}