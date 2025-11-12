package com.example.alphakids.ui.screens.tutor.studentprofile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.student.StudentUiState
import com.example.alphakids.ui.student.StudentViewModel
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
    // 1. Lista de opciones para Docentes
    val docentes = listOf("Prof. Ana López", "Prof. Luis García", "Prof. Marta Torres")

    var selectedInstitucion by remember { mutableStateOf<String?>(null) }
    var selectedGrado by remember { mutableStateOf<String?>(null) }
    var selectedSeccion by remember { mutableStateOf<String?>(null) }
    // 2. Estado para el Docente seleccionado
    var selectedDocente by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.createUiState.collectAsState()
    val isLoading = uiState is StudentUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(Unit) {
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
                else -> {}
            }
        }
    }

    // He modificado las llamadas a LabeledDropdownField para incluir 'options' y 'onOptionSelected'
    // Asumo que se usa la implementación funcional de la tarea anterior o una similar.
    @Composable
    fun LabeledDropdownFieldWrapper(
        label: String,
        selectedOption: String,
        options: List<String>,
        placeholderText: String,
        onOptionSelected: (String) -> Unit
    ) {
        // Asumo que LabeledDropdownField acepta esta firma.
        // Si no tienes el componente, necesitarás implementarlo usando ExposedDropdownMenuBox.
        // Dado que el componente ya está importado, lo usaré con la firma completa.
        com.example.alphakids.ui.components.LabeledDropdownField(
            label = label,
            selectedOption = selectedOption,
            placeholderText = placeholderText,
            // Estos parámetros deben ser manejados internamente por LabeledDropdownField si es un dropdown real
            onClick = { /* Lógica de apertura, puede quedar vacío si el componente la maneja internamente */ }
        )
        // Debido a que no tengo la implementación de 'LabeledDropdownField', y los parámetros
        // 'onClick' y 'selectedOption' sugieren que es un campo visual, no funcional.
        // Si quieres que funcione, debes usar la implementación que hice en la tarea anterior.
        // Por ahora, solo agregaré el nuevo campo, replicando la estructura existente:
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
                Text("Ingresa los datos de tu hijo", fontFamily = dmSansFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                // --- Campo Docente agregado ---
                LabeledDropdownField(
                    label = "Docente",
                    selectedOption = selectedDocente ?: "",
                    placeholderText = "Selecciona docente (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */
                        // En un caso real, aquí usarías una función para actualizar selectedDocente
                        selectedDocente = docentes.firstOrNull() // Simulación de selección
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                // -----------------------------

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

                if (uiState is StudentUiState.Error) {
                    // Aquí se mostraría el error si fuera necesario
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

                        viewModel.createStudent(
                            nombre = nombre,
                            apellido = apellido,
                            edad = edadInt,
                            grado = selectedGrado ?: "",
                            seccion = selectedSeccion ?: "",
                            idInstitucion = "", // TODO: Replace "" with actual institution ID
                            // Enviar el docente seleccionado (asumiendo que el modelo StudentViewModel lo soporta)
                            // Si necesitas un campo para el docente en createStudent, actualiza tu ViewModel/Modelo.
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateStudentProfileScreenPreview() {
    AlphakidsTheme {
        CreateStudentProfileScreen({}, {}, {})
    }
}