package com.example.alphakids.ui.screens.tutor.profile_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.auth.AuthViewModel
import com.example.alphakids.ui.student.StudentViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.screens.tutor.profile_selection.components.StudentProfileCard
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

// 🛑 ELIMINAMOS las importaciones antiguas
// import com.example.alphakids.ui.utils.playBackgroundMusic
// import com.example.alphakids.ui.utils.stopBackgroundMusic
// import com.example.alphakids.ui.utils.MUSICA_FONDO_APP_URL

// ✅ Importamos el nuevo gestor centralizado de audio
import com.example.alphakids.ui.utils.MusicManager


@Composable
fun ProfileSelectionScreen(
    onProfileClick: (profileId: String) -> Unit,
    onAddProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current // Necesario para iniciar la música con el Contexto
    val currentUser by authViewModel.currentUser.collectAsState()
    val tutorName = currentUser?.nombre
    val students by studentViewModel.students.collectAsState()

    // --- LÓGICA DE MÚSICA DE FONDO ---

    // 1. Iniciar la música cuando el Composable se carga por primera vez
    LaunchedEffect(Unit) {
        // ✅ USAMOS MusicManager para iniciar la música de la APP
        MusicManager.startMusicaApp(context)
    }

    // 2. Control de pausa/reinicio: Se deja el onDispose vacío,
    // ya que la lógica de pausa la gestiona el juego (CameraOCRScreen.kt),
    // y la detención final la gestiona MainActivity (onStop/onDestroy).
    DisposableEffect(Unit) {
        onDispose {
            // La música de la APP DEBE persistir al navegar al perfil del hijo.
            // Si la app se destruye aquí (lo cual no debería pasar en navegación normal),
            // MainActivity.onStop() la detendrá.
        }
    }

    // --- FIN LÓGICA DE MÚSICA DE FONDO ---

    Scaffold(
        // ... (Resto del Scaffold y la UI se mantienen sin cambios)
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Perfiles",
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
                .padding(top = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tutorName?.let { name ->
                    Text(
                        text = "¡Hola, $name!",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Text(
                    text = "¿Quién jugará hoy?",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Selecciona un perfil para comenzar",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(students, key = { it.id }) { student ->
                    StudentProfileCard(
                        title = student.nombre,
                        description = student.grado,
                        icon = Icons.Rounded.Face, // TODO: Usar student.fotoPerfil
                        onClick = { onProfileClick(student.id) }
                    )
                }
                item {
                    StudentProfileCard(
                        title = "Agregar perfil",
                        description = "Crea el perfil de tu hijo",
                        icon = Icons.Rounded.Add,
                        onClick = onAddProfileClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSelectionScreenPreview() {
    AlphakidsTheme {
        ProfileSelectionScreen(
            onProfileClick = {},
            onAddProfileClick = {},
            onSettingsClick = {},
            onLogoutClick = {}
        )
    }
}