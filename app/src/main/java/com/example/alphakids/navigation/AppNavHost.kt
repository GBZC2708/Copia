package com.example.alphakids.navigation

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
            Routes.MY_GAMES,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"
            MyGamesScreen(
                onBackClick = { navController.popBackStack() },
                onWordsGameClick = { navController.navigate(Routes.gameWordsRoute(studentId)) }
            )
        }

        composable(
            Routes.GAME_WORDS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) {
            GameWordsScreen(
                onBackClick = { navController.popBackStack() },
                onWordClick = { navController.navigate(Routes.GAME) }
            )
        }

        composable(
            Routes.ASSIGNED_WORDS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { entry ->
            val studentId = entry.arguments?.getString("studentId") ?: "default"

            AssignedWordsScreen(
                studentId = studentId,
                onBackClick = { navController.popBackStack() },
                onWordClick = { assignment ->
                    navController.navigate(Routes.wordPuzzleRoute(assignment.id ?: ""))
                }
            )
        }

        composable(
            Routes.WORD_PUZZLE,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { entry ->
            val assignmentId = entry.arguments?.getString("assignmentId") ?: ""

            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Routes.ASSIGNED_WORDS)
            }
            val viewModel: WordPuzzleViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(assignmentId) {
                viewModel.loadWordData(assignmentId)
            }

            WordPuzzleScreen(
                assignmentId = assignmentId,
                onBackClick = { navController.popBackStack() },
                onTakePhotoClick = {
                    val targetWord = uiState.assignment?.palabraTexto ?: ""
                    if (targetWord.isNotEmpty()) {
                        navController.navigate(Routes.cameraOCRRoute(assignmentId, targetWord))
                    }
                }
            )
        }

        composable(
            Routes.CAMERA_OCR,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.StringType },
                navArgument("targetWord") { type = NavType.StringType }
            )
        ) { entry ->
            CameraOCRScreen(
                assignmentId = entry.arguments?.getString("assignmentId") ?: "",
                targetWord = entry.arguments?.getString("targetWord") ?: "",
                onBackClick = { navController.popBackStack() },
                onWordCompleted = { navController.popBackStack() }
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
