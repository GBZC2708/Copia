package com.example.alphakids.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.ui.auth.AuthViewModel
import com.example.alphakids.ui.word.WordUiState
import com.example.alphakids.ui.word.WordViewModel
import com.example.alphakids.ui.components.ActionDialog

// Teacher
import com.example.alphakids.ui.screens.teacher.home.TeacherHomeScreen
import com.example.alphakids.ui.screens.teacher.students.*
import com.example.alphakids.ui.screens.teacher.words.*

// Tutor - General
import com.example.alphakids.ui.screens.tutor.home.StudentHomeScreen
import com.example.alphakids.ui.screens.tutor.profile_selection.ProfileSelectionScreen
import com.example.alphakids.ui.screens.tutor.dictionary.StudentDictionaryScreen
import com.example.alphakids.ui.screens.tutor.achievements.StudentAchievementsScreen

// Tutor - Profiles
import com.example.alphakids.ui.screens.tutor.studentprofile.CreateStudentProfileScreen
import com.example.alphakids.ui.screens.tutor.studentprofile.EditStudentProfileScreen

// Tutor - Games
import com.example.alphakids.ui.screens.tutor.games.MyGamesScreen
import com.example.alphakids.ui.screens.tutor.games.GameWordsScreen
import com.example.alphakids.ui.screens.tutor.games.AssignedWordsScreen
import com.example.alphakids.ui.screens.tutor.games.WordPuzzleScreen
import com.example.alphakids.ui.screens.tutor.games.WordPuzzleViewModel
import com.example.alphakids.ui.screens.tutor.games.CameraOCRScreen

// Tutor - Store
import com.example.alphakids.ui.screens.tutor.store.StudentStoreScreen
import com.example.alphakids.ui.screens.tutor.store.StudentPetsStoreScreen
import com.example.alphakids.ui.screens.tutor.store.StudentAccessoriesStoreScreen

// Tutor - Pets
import com.example.alphakids.ui.screens.tutor.pets.StudentPetsScreen
import com.example.alphakids.ui.screens.tutor.pets.StudentPetDetailScreen

// Profile
import com.example.alphakids.ui.screens.profile.EditProfileScreen

import androidx.compose.material.icons.rounded.Warning
import com.example.alphakids.ui.screens.tutor.games.GameFailureScreen
import com.example.alphakids.ui.screens.tutor.games.GameResultScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = when {
        currentUser == null -> Routes.ROLE_SELECTION
        currentUser?.rol == UserRole.TUTOR -> Routes.PROFILES
        currentUser?.rol == UserRole.DOCENTE -> Routes.TEACHER_HOME
        else -> Routes.ROLE_SELECTION
    }

    val onLogout = { authViewModel.logout() }

    // Navegación bottom nav estudiante
    val navigateToStudentBottomNav: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Navegación bottom nav docente
    val navigateToTeacherBottomNav: (String) -> Unit = { base ->
        val targetRoute = when (base) {
            "students" -> Routes.TEACHER_STUDENTS
            "words" -> Routes.WORDS
            else -> Routes.TEACHER_HOME
        }
        navController.navigate(targetRoute) {
            popUpTo(Routes.TEACHER_HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // ============================================================
        // ROL Y AUTENTICACIÓN
        // ============================================================
        composable(Routes.ROLE_SELECTION) {
            com.example.alphakids.ui.screens.common.RoleSelectScreen(
                onTutorClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TUTOR)) },
                onTeacherClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TEACHER)) }
            )
        }

        composable(
            Routes.LOGIN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { entry ->
            val role = entry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR

            com.example.alphakids.ui.auth.LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onLoginSuccess = {
                    val next = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(next) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {},
                onRegisterClick = { navController.navigate(Routes.registerRoute(role)) },
                isTutorLogin = isTutor
            )
        }

        composable(
            Routes.REGISTER,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { entry ->
            val role = entry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR

            com.example.alphakids.ui.auth.RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    val next = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(next) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                isTutorRegister = isTutor
            )
        }


        // ============================================================
        // PERFILES (TUTOR)
        // ============================================================
        composable(Routes.PROFILES) {
            ProfileSelectionScreen(
                onProfileClick = { navController.navigate(Routes.homeRoute(it)) },
                onAddProfileClick = { navController.navigate(Routes.STUDENT_PROFILE_CREATE) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TUTOR)) },
                onLogoutClick = onLogout
            )
        }

        // ============================================================
        // HOME DEL ESTUDIANTE
        // ============================================================
        composable(
            Routes.HOME,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentHomeScreen(
                studentName = "Estudiante",
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onPlayClick = {
                    navController.navigate(Routes.assignedWordsRoute(studentId))
                },
                onDictionaryClick = { navigateToStudentBottomNav(Routes.dictionaryRoute(studentId)) },
                onAchievementsClick = { navigateToStudentBottomNav(Routes.achievementsRoute(studentId)) },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val target = when (route) {
                        "store" -> Routes.storeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.homeRoute(studentId)
                    }
                    navigateToStudentBottomNav(target)
                },
                currentRoute = "home"
            )
        }

        // ============================================================
        // JUEGOS
        // ============================================================
        composable(
            Routes.ASSIGNED_WORDS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            AssignedWordsScreen(
                studentId = studentId,
                onBackClick = { navController.popBackStack() },
                onWordClick = { assignment ->
                    // 🚨 CORRECCIÓN: Ahora pasamos el studentId a la ruta de WordPuzzle
                    navController.navigate(Routes.wordPuzzleRoute(assignment.id ?: "", studentId))
                }
            )
        }

        composable(
            route = Routes.WORD_PUZZLE,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType } // 🚨 AÑADIDO: Requerido
            )
        ) { entry ->
            val assignmentId = entry.arguments?.getString("assignmentId") ?: ""
            val studentId = entry.arguments?.getString("studentId") ?: "default" // 🚨 AÑADIDO

            // Usamos un NavBackStackEntry que contiene los datos del assignment
            val parentEntry = remember(entry) {
                // En este caso, usamos el entry actual si el VM es específico para el puzzle
                entry
                // Si el VM es compartido, el backstack de ASSIGNED_WORDS no siempre es correcto.
                // Mejor hiltViewModel() sin un entry si el VM carga datos con assignmentId.
                // Mantendremos la estructura original de usar el entry actual para simplicidad.
            }
            val viewModel: WordPuzzleViewModel = hiltViewModel(parentEntry)

            WordPuzzleScreen(
                assignmentId = assignmentId,
                studentId = studentId, // 🚨 PASADO: El studentId
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }


        composable(
            route = Routes.CAMERA_OCR,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.StringType },
                navArgument("targetWord") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType }, // 🚨 CRÍTICO: Requerido
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->

            val encodedUrl = entry.arguments?.getString("imageUrl")
            val targetWord = entry.arguments?.getString("targetWord") ?: ""
            val assignmentId = entry.arguments?.getString("assignmentId") ?: ""
            val studentId = entry.arguments?.getString("studentId") ?: "default" // 🚨 AÑADIDO

            // 🚨 DECODIFICACIÓN CRÍTICA
            val decodedWord = URLDecoder.decode(targetWord, StandardCharsets.UTF_8.name())
            val decodedUrl = encodedUrl?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            }

            Log.d("DebugImagen", "PASO 2 (NAVHOST): ¿URL recibida y decodificada? URL = $decodedUrl")

            CameraOCRScreen(
                assignmentId = assignmentId,
                targetWord = decodedWord,
                studentId = studentId, // 🚨 PASADO: El studentId
                targetImageUrl = decodedUrl,
                onBackClick = { navController.popBackStack() },

                // --- LÓGICA DE ÉXITO CORREGIDA (Usando helper) ---
                onWordCompleted = { word, completedImageUrl, sId ->
                    val encodedResultWord = URLEncoder.encode(word, StandardCharsets.UTF_8.name())
                    val encodedResultUrl = completedImageUrl?.let {
                        URLEncoder.encode(it, StandardCharsets.UTF_8.name())
                    }

                    // 🚨 Usamos la función helper que incluye el studentId y la imagen
                    val newRoute = Routes.gameResultRoute(encodedResultWord, sId, encodedResultUrl)

                    navController.navigate(newRoute) {
                        popUpTo(Routes.WORD_PUZZLE_BASE) { inclusive = true } // Elimina Puzzle y Camera
                    }
                },

                // --- Lógica de Tiempo Agotado (Usando helper) ---
                onTimeExpired = { expiredImageUrl, sId ->
                    val encodedResultUrl = expiredImageUrl?.let {
                        URLEncoder.encode(it, StandardCharsets.UTF_8.name())
                    }

                    // 🚨 Usamos la función helper que incluye el studentId y la imagen
                    val newRoute = Routes.gameFailureRoute(sId, encodedResultUrl)

                    navController.navigate(newRoute) {
                        popUpTo(Routes.WORD_PUZZLE_BASE) { inclusive = true } // Elimina Puzzle y Camera
                    }
                }
            )
        }

        // --- Pantalla de Éxito ---
        composable(
            route = Routes.GAME_RESULT,
            arguments = listOf(
                navArgument("word") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType }, // 🚨 CRÍTICO: Requerido
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val word = entry.arguments?.getString("word") ?: "PALABRA"
            val studentId = entry.arguments?.getString("studentId") ?: "default" // 🚨 AÑADIDO
            val encodedUrl = entry.arguments?.getString("imageUrl")

            // Decodificamos el word (por si venía codificado con espacios)
            val decodedWord = URLDecoder.decode(word, StandardCharsets.UTF_8.name())

            val imageUrl = encodedUrl?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            }

            GameResultScreen(
                word = decodedWord,
                imageUrl = imageUrl,
                onContinueClick = {
                    // "Continuar" te devuelve a la lista de palabras asignadas para el estudiante
                    navController.popBackStack(Routes.ASSIGNED_WORDS, inclusive = false)
                },
                onBackClick = {
                    // "Volver al Menú" te lleva al Home
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        // --- Pantalla de Fallo (Tiempo Agotado) ---
        composable(
            route = Routes.GAME_FAILURE,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType }, // 🚨 CRÍTICO: Requerido
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default" // 🚨 AÑADIDO
            val encodedUrl = entry.arguments?.getString("imageUrl")
            val imageUrl = encodedUrl?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            }

            GameFailureScreen(
                imageUrl = imageUrl,
                onRetryClick = {
                    // "Reintentar" (Asumo que vuelve al listado de palabras asignadas para reelegir)
                    navController.popBackStack(Routes.ASSIGNED_WORDS, inclusive = false)
                },

                onExitClick = {
                    // "Salir" te lleva al Home del estudiante
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }


        // ============================================================
        // DICCIONARIO
        // ============================================================
        composable(
            Routes.DICTIONARY,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentDictionaryScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onWordClick = {},
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val target = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "store" -> Routes.storeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.dictionaryRoute(studentId)
                    }
                    navigateToStudentBottomNav(target)
                },
                currentRoute = "dictionary"
            )
        }

        // ============================================================
        // LOGROS
        // ============================================================
        composable(
            Routes.ACHIEVEMENTS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentAchievementsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val target = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "store" -> Routes.storeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.achievementsRoute(studentId)
                    }
                    navigateToStudentBottomNav(target)
                },
                currentRoute = "achievements"
            )
        }


        // ============================================================
        // DOCENTE
        // ============================================================
        composable(Routes.TEACHER_HOME) {
            TeacherHomeScreen(
                onAssignWordsClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "home"
            )
        }

        composable(Routes.TEACHER_STUDENTS) {
            TeacherStudentsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onStudentClick = { navController.navigate(Routes.teacherStudentDetailRoute(it)) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "students"
            )
        }

        composable(
            Routes.TEACHER_STUDENT_DETAIL,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: ""

            StudentDetailScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onAssignWordClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onWordClick = { navController.navigate(Routes.wordDetailRoute(it)) },
                onSettingsClick = {},
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = Routes.TEACHER_STUDENTS
            )
        }

        composable(Routes.WORDS) { entry ->
            val viewModel: WordViewModel = hiltViewModel(entry)
            val words by viewModel.words.collectAsState()
            val filter by viewModel.filterDifficulty.collectAsState()

            WordsScreen(
                words = words,
                currentDifficultyFilter = filter,
                onSetDifficultyFilter = viewModel::setDifficultyFilter,
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onCreateWordClick = { navController.navigate(Routes.createWordRoute()) },
                onAssignWordClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onWordClick = { navController.navigate(Routes.wordDetailRoute(it)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "words"
            )
        }

        composable(
            Routes.WORD_EDIT,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType; nullable = true })
        ) { entry ->
            val wordId = entry.arguments?.getString("wordId")
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Routes.WORDS)
            }
            val viewModel: WordViewModel = hiltViewModel(parentEntry)
            val words by viewModel.words.collectAsState()
            val word = words.find { it.id == wordId }

            WordEditScreen(
                viewModel = viewModel,
                word = word,
                isEditing = wordId != null,
                onCloseClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() }
            )
        }

        composable(
            Routes.WORD_DETAIL,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { entry ->
            val wordId = entry.arguments?.getString("wordId") ?: ""
            var showDeleteDialog by remember { mutableStateOf(false) }

            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Routes.WORDS)
            }
            val viewModel: WordViewModel = hiltViewModel(parentEntry)
            val words by viewModel.words.collectAsState()
            val uiState by viewModel.uiState.collectAsState()
            val word = words.find { it.id == wordId }

            WordDetailScreen(
                word = word,
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onEditWordClick = { navController.navigate(Routes.editWordRoute(wordId)) },
                onDeleteWordClick = { showDeleteDialog = true },
                onStudentClick = {},
                onSettingsClick = {},
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "words"
            )

            if (showDeleteDialog) {
                ActionDialog(
                    icon = Icons.Rounded.Warning,
                    message = "¿Estás seguro de eliminar esta palabra?",
                    primaryButtonText = "Eliminar",
                    onPrimaryButtonClick = { viewModel.deleteWord(wordId) },
                    secondaryButtonText = "Cancelar",
                    onSecondaryButtonClick = { showDeleteDialog = false },
                    onDismissRequest = { showDeleteDialog = false },
                    isError = true
                )
            }

            LaunchedEffect(uiState) {
                when (uiState) {
                    is WordUiState.Success -> {
                        if ((uiState as WordUiState.Success).message.contains("eliminada")) {
                            showDeleteDialog = false
                            viewModel.resetUiState()
                            navController.popBackStack(Routes.WORDS, false)
                        }
                    }
                    is WordUiState.Error -> {
                        showDeleteDialog = false
                        viewModel.resetUiState()
                    }
                    else -> Unit
                }
            }
        }

        composable(Routes.ASSIGN_WORD) {
            AssignWordScreen(
                onBackClick = { navController.popBackStack() },
                onStudentClick = {}
            )
        }

        // ============================================================
        // PERFIL GENERAL
        // ============================================================
        composable(
            Routes.EDIT_PROFILE,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { entry ->
            val isTutor = entry.arguments?.getString("role") == Routes.ROLE_TUTOR
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                isTutorProfile = isTutor
            )
        }

        composable(Routes.STUDENT_PROFILE_CREATE) {
            CreateStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onCreateSuccess = { navController.popBackStack() }
            )
        }

        composable(
            Routes.STUDENT_PROFILE_EDIT,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) {
            EditStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // ============================================================
        // TIENDA
        // ============================================================
        composable(
            route = Routes.STORE,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentStoreScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.storeRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "store",
                onPetsStoreClick = { navController.navigate(Routes.storePetsRoute(studentId)) },
                onAccessoriesStoreClick = { navController.navigate(Routes.storeAccessoriesRoute(studentId)) }
            )
        }

        // Tienda - Mascotas
        composable(
            route = Routes.STORE_PETS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentPetsStoreScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = onLogout,
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.storeRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "store",
                studentId = studentId,
                dogImageResId = com.example.alphakids.R.drawable.ic_happy_dog,
                catImageResId = com.example.alphakids.R.drawable.ic_happy_cat,
                coins = 123
            )
        }

        // Tienda - Accesorios
        composable(
            route = Routes.STORE_ACCESSORIES,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentAccessoriesStoreScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = onLogout,
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "pets" -> Routes.petsRoute(studentId)
                        else -> Routes.storeRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "store",
                studentId = studentId,
                coins = 123,
                croquetasImageResId = com.example.alphakids.R.drawable.ic_kibble_dog_cat,
                huesoImageResId = com.example.alphakids.R.drawable.ic_bone_dog,
                pescadoImageResId = com.example.alphakids.R.drawable.ic_fish_cat
            )
        }

        // ============================================================
        // MASCOTAS
        // ============================================================
        composable(
            route = Routes.PETS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            StudentPetsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "store" -> Routes.storeRoute(studentId)
                        else -> Routes.petsRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "pets",
                onPetDetailClick = { petName ->
                    navController.navigate(Routes.petDetailRoute(studentId, petName))
                }
            )
        }

        // Mascota Detalle
        composable(
            route = Routes.PET_DETAIL,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType }
            )
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"
            val petName = entry.arguments?.getString("petName") ?: "Mi Mascota"

            StudentPetDetailScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = onLogout,
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "store" -> Routes.storeRoute(studentId)
                        else -> Routes.petsRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "pets",
                petName = petName
            )
        }
    }
}
