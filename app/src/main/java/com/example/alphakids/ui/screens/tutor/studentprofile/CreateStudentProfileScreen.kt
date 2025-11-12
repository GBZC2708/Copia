package com.example.alphakids.ui.screens.tutor.studentprofile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.student.DocenteUi
import com.example.alphakids.ui.student.StudentUiState
import com.example.alphakids.ui.student.StudentViewModel
import com.example.alphakids.ui.student.UiState
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledDropdownField
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateStudentProfileScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCreateSuccess: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edadString by remember { mutableStateOf("") }

    val instituciones = listOf("Institución A", "Institución B", "Otra")
    val grados = listOf("Inicial 3 años", "Inicial 4 años", "Inicial 5 años")
    val secciones = listOf("A", "B", "C")

    var selectedInstitucion by remember { mutableStateOf<String?>(null) }
    var selectedGrado by remember { mutableStateOf<String?>(null) }
    var selectedSeccion by remember { mutableStateOf<String?>(null) }
    var selectedDocenteId by remember { mutableStateOf<String?>(null) }
    var selectedDocenteName by remember { mutableStateOf<String?>(null) }
    var showDocenteDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.createUiState.collectAsState()
    val docentesUiState by viewModel.docentesUi.collectAsState()
    val docentes = remember(docentesUiState) {
        when (val state = docentesUiState) {
            is UiState.Success -> state.data
            else -> emptyList()
        }
    }
    val isLoading = uiState is StudentUiState.Loading
    val isDocentesLoading = docentesUiState is UiState.Loading
    val noDocentesAvailable = docentesUiState is UiState.Success && docentes.isEmpty()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadDocentes(selectedInstitucion?.takeIf { !it.isNullOrBlank() })
        viewModel.createUiState.collectLatest { state ->
            when (state) {
                is StudentUiState.Success -> {
                    Toast.makeText(context, "Perfil creado", Toast.LENGTH_SHORT).show()
                    onCreateSuccess()
                    viewModel.resetCreateState()
                }
                is StudentUiState.Error -> {
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetCreateState()
                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(selectedInstitucion) {
        viewModel.loadDocentes(selectedInstitucion?.takeIf { !it.isNullOrBlank() })
    }

    LaunchedEffect(docentesUiState) {
        if (docentesUiState is UiState.Error) {
            Toast.makeText(
                context,
                (docentesUiState as UiState.Error).message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(docentes) {
        if (selectedDocenteId != null && docentes.none { it.uid == selectedDocenteId }) {
            selectedDocenteId = null
            selectedDocenteName = null
        }
    }

    val docentePlaceholder = if (noDocentesAvailable) {
        "Sin docentes disponibles"
    } else {
        "Selecciona docente"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Crear Perfil",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                IconContainer(
                    icon = Icons.Rounded.ChildCare,
                    contentDescription = "Icono Niño"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Nuevo Perfil", fontFamily = dmSansFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    "Ingresa los datos de tu hijo",
                    fontFamily = dmSansFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                LabeledTextField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholderText = "Nombre del niño"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Apellido",
                    value = apellido,
                    onValueChange = { apellido = it },
                    placeholderText = "Apellido del niño"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Edad",
                    value = edadString,
                    onValueChange = { edadString = it.filter { char -> char.isDigit() } },
                    placeholderText = "Edad del niño (ej. 4)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledDropdownField(
                    label = "Institución",
                    selectedOption = selectedInstitucion ?: "",
                    placeholderText = "Selecciona institución (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledDropdownField(
                    label = "Grado",
                    selectedOption = selectedGrado ?: "",
                    placeholderText = "Selecciona grado (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledDropdownField(
                    label = "Sección",
                    selectedOption = selectedSeccion ?: "",
                    placeholderText = "Selecciona sección (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                LabeledDropdownField(
                    label = "Docente",
                    selectedOption = selectedDocenteName ?: "",
                    placeholderText = docentePlaceholder,
                    onClick = {
                        if (docentes.isEmpty()) {
                            Toast.makeText(context, "Sin docentes disponibles", Toast.LENGTH_SHORT).show()
                        } else {
                            showDocenteDialog = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState is StudentUiState.Error) {
                    // Error state handled via toast collector
                }

                PrimaryButton(
                    text = "Crear Perfil",
                    onClick = {
                        val edadInt = edadString.toIntOrNull()
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (apellido.isBlank()) {
                            Toast.makeText(context, "Ingresa el apellido", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (edadInt == null || edadInt <= 0) {
                            Toast.makeText(context, "Ingresa una edad válida", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (selectedDocenteId == null) {
                            Toast.makeText(context, "Selecciona un docente", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        viewModel.createStudent(
                            nombre = nombre,
                            apellido = apellido,
                            edad = edadInt,
                            grado = selectedGrado ?: "",
                            seccion = selectedSeccion ?: "",
                            idInstitucion = "", // TODO: Replace "" with actual institution ID
                            idDocente = selectedDocenteId!!
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !isDocentesLoading && docentes.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading || isDocentesLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }

    if (showDocenteDialog) {
        var docenteSearch by remember { mutableStateOf("") }
        val filteredDocentes = remember(docentes, docenteSearch) {
            if (docenteSearch.isBlank()) {
                docentes
            } else {
                docentes.filter { docente ->
                    docente.nombreCompleto.contains(docenteSearch, ignoreCase = true)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showDocenteDialog = false },
            confirmButton = {
                TextButton(onClick = { showDocenteDialog = false }) {
                    Text("Cerrar")
                }
            },
            title = {
                Text(
                    text = "Selecciona un docente",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    if (docentes.size > 5) {
                        OutlinedTextField(
                            value = docenteSearch,
                            onValueChange = { docenteSearch = it },
                            label = { Text("Buscar") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (filteredDocentes.isEmpty()) {
                        Text("Sin resultados")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp)
                        ) {
                            items(filteredDocentes, key = DocenteUi::uid) { docente ->
                                TextButton(
                                    onClick = {
                                        selectedDocenteId = docente.uid
                                        selectedDocenteName = docente.nombreCompleto
                                        showDocenteDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(docente.nombreCompleto)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateStudentProfileScreenPreview() {
    AlphakidsTheme {
        CreateStudentProfileScreen({}, {}, {})
    }
}
