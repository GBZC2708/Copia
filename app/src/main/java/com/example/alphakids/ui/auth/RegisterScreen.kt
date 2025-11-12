package com.example.alphakids.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.dmSansFamily
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    isTutorRegister: Boolean = true,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.authUiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading

    LaunchedEffect(Unit) {
        viewModel.authUiState.collectLatest { state ->
            if (state is AuthUiState.Success) {
                onRegisterSuccess()
                viewModel.resetAuthState()
            }
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Registro",
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
                    icon = if (isTutorRegister) Icons.Rounded.Face else Icons.Rounded.School,
                    contentDescription = "Icono de Registro"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Registro",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (isTutorRegister) "Crea tu cuenta de tutor" else "Crea tu cuenta de docente",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                LabeledTextField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholderText = "Escribe tu nombre"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Apellido",
                    value = apellido,
                    onValueChange = { apellido = it },
                    placeholderText = "Escribe tu apellido"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Escribe tu email"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo de Contraseña con Toggle de Visibilidad (Soluciona errores de LabeledTextField) ---
                PasswordInputField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Escribe tu contraseña",
                    isPasswordVisible = isPasswordVisible,
                    onToggleVisibility = { isPasswordVisible = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo de Confirmación de Contraseña con Toggle de Visibilidad ---
                PasswordInputField(
                    label = "Confirmar Contraseña",
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    placeholderText = "Confirma tu contraseña",
                    isPasswordVisible = isPasswordVisible,
                    onToggleVisibility = { isPasswordVisible = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Teléfono",
                    value = telefono,
                    onValueChange = { telefono = it },
                    placeholderText = "Escribe tu teléfono"
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                PrimaryButton(
                    text = "Crear cuenta",
                    onClick = {
                        val rol = if (isTutorRegister) "tutor" else "docente"

                        if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
                            Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        if (password != passwordConfirm) {
                            Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        viewModel.register(
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            clave = password,
                            telefono = telefono,
                            rol = rol
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// NUEVO COMPONENTE para manejar la lógica del campo de contraseña (resuelve errores de trailingIcon y colores)
@Composable
private fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholderText) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // TrailingIcon de Material 3 que funciona directamente
            trailingIcon = {
                IconButton(onClick = { onToggleVisibility(!isPasswordVisible) }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            // Uso de TextFieldDefaults.colors (Material 3) para evitar el error 'outlinedTextFieldColors'
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
            )
        )
    }
}