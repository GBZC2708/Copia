package com.example.alphakids.navigation

object Routes {
// ============================================================
// ROLES
// ============================================================
    const val ROLE_TEACHER = "teacher"
    const val ROLE_TUTOR = "tutor"

    // ============================================================
    // AUTENTICACIÓN
    // ============================================================
    const val ROLE_SELECTION = "role_selection"

    const val LOGIN_BASE = "login"
    const val LOGIN = "$LOGIN_BASE/{role}"
    fun loginRoute(role: String) = "$LOGIN_BASE/$role"

    const val REGISTER_BASE = "register"
    const val REGISTER = "$REGISTER_BASE/{role}"
    fun registerRoute(role: String) = "$REGISTER_BASE/$role"

    // ============================================================
    // PERFIL DE TUTOR
    // ============================================================
    const val PROFILES = "perfiles"

    // ============================================================
    // HOME ESTUDIANTE + NAVEGACIÓN BASE
    // ============================================================
    const val HOME_BASE = "home"
    const val HOME = "$HOME_BASE/{studentId}"
    fun homeRoute(studentId: String) = "$HOME_BASE/$studentId"

    // BottomNav estudiante
    const val STORE = "store/{studentId}"
    fun storeRoute(studentId: String) = "store/$studentId"

    const val PETS = "pets/{studentId}"
    fun petsRoute(studentId: String) = "pets/$studentId"

    const val DICTIONARY = "dictionary/{studentId}"
    fun dictionaryRoute(studentId: String) = "dictionary/$studentId"

    const val ACHIEVEMENTS = "achievements/{studentId}"
    fun achievementsRoute(studentId: String) = "achievements/$studentId"

    // ============================================================
    // SUBRUTAS DE TIENDA
    // ============================================================
    const val STORE_PETS = "store/pets/{studentId}"
    fun storePetsRoute(studentId: String) = "store/pets/$studentId"

    const val STORE_ACCESSORIES = "store/accessories/{studentId}"
    fun storeAccessoriesRoute(studentId: String) = "store/accessories/$studentId"

    // ============================================================
    // DETALLE DE MASCOTA
    // ============================================================
    const val PET_DETAIL_BASE = "pet_detail"
    const val PET_DETAIL = "$PET_DETAIL_BASE/{studentId}/{petName}"
    fun petDetailRoute(studentId: String, petName: String) =
        "$PET_DETAIL_BASE/$studentId/$petName"

    // ============================================================
    // JUEGOS
    // ============================================================
    const val GAME_RESULT_BASE = "game_result"
    const val GAME_RESULT = "$GAME_RESULT_BASE/{word}/{studentId}?imageUrl={imageUrl}"
    /** 🚨 ¡MODIFICACIÓN! Ahora incluye studentId como requerido. */
    fun gameResultRoute(word: String, studentId: String, imageUrl: String?) =
        "$GAME_RESULT_BASE/$word/$studentId${if (imageUrl != null) "?imageUrl=$imageUrl" else ""}"

    // RUTAS SI FALLA
    const val GAME_FAILURE_BASE = "game_failure"
    const val GAME_FAILURE = "$GAME_FAILURE_BASE/{studentId}?imageUrl={imageUrl}"
    /** 🚨 ¡MODIFICACIÓN! Ahora incluye studentId como requerido. */
    fun gameFailureRoute(studentId: String, imageUrl: String?) =
        "$GAME_FAILURE_BASE/$studentId${if (imageUrl != null) "?imageUrl=$imageUrl" else ""}"

    const val MY_GAMES_BASE = "my_games"
    const val MY_GAMES = "$MY_GAMES_BASE/{studentId}"
    fun myGamesRoute(studentId: String) = "$MY_GAMES_BASE/$studentId"

    const val GAME_WORDS_BASE = "game_words"
    const val GAME_WORDS = "$GAME_WORDS_BASE/{studentId}"
    fun gameWordsRoute(studentId: String) = "$GAME_WORDS_BASE/$studentId"

    const val ASSIGNED_WORDS = "assigned_words/{studentId}"
    fun assignedWordsRoute(studentId: String) = "assigned_words/$studentId"

    const val WORD_PUZZLE_BASE = "word_puzzle"
    const val WORD_PUZZLE = "$WORD_PUZZLE_BASE/{assignmentId}/{studentId}"
    /** 🚨 ¡MODIFICACIÓN! Ahora incluye studentId como requerido. */
    fun wordPuzzleRoute(assignmentId: String, studentId: String) = "$WORD_PUZZLE_BASE/$assignmentId/$studentId"

    const val GAME = "game"

    // OCR
    const val CAMERA_OCR_BASE = "camera_ocr"
    /** 🚨 ¡MODIFICACIÓN CRÍTICA! Ahora assignmentId, targetWord y studentId son requeridos en la ruta base. */
    const val CAMERA_OCR = "$CAMERA_OCR_BASE/{assignmentId}/{targetWord}/{studentId}?imageUrl={imageUrl}"

    fun cameraOcrRoute(assignmentId: String, targetWord: String, studentId: String, imageUrl: String?) =
        "$CAMERA_OCR_BASE/$assignmentId/$targetWord/$studentId${if (imageUrl != null) "?imageUrl=$imageUrl" else ""}"

    // ============================================================
    // DOCENTE
    // ============================================================
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_STUDENTS = "teacher_students"
    const val WORDS = "words"

    // Detalle de estudiante
    const val TEACHER_STUDENT_DETAIL = "teacher_student_detail/{studentId}"
    fun teacherStudentDetailRoute(studentId: String) =
        "teacher_student_detail/$studentId"

    // CRUD palabras
    const val WORD_EDIT_BASE = "word_edit"
    const val WORD_EDIT = "$WORD_EDIT_BASE?wordId={wordId}"
    fun createWordRoute() = WORD_EDIT_BASE
    fun editWordRoute(wordId: String) = "$WORD_EDIT_BASE?wordId=$wordId"

    const val WORD_DETAIL = "word_detail/{wordId}"
    // comentando//........
    fun wordDetailRoute(wordId: String) = "word_detail/$wordId"

    const val ASSIGN_WORD = "assign_word"

    // ============================================================
    // PERFILES Y CONFIGURACIONES
    // ============================================================
    const val EDIT_PROFILE_BASE = "edit_profile"
    const val EDIT_PROFILE = "$EDIT_PROFILE_BASE/{role}"
    fun editProfileRoute(role: String) = "$EDIT_PROFILE_BASE/$role"

    const val STUDENT_PROFILE_CREATE = "student_profile_create"

    const val STUDENT_PROFILE_EDIT_BASE = "student_profile_edit"
    const val STUDENT_PROFILE_EDIT = "$STUDENT_PROFILE_EDIT_BASE/{studentId}"
    fun editStudentProfileRoute(studentId: String) =
        "$STUDENT_PROFILE_EDIT_BASE/$studentId"
}
