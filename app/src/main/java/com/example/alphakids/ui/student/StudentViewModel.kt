package com.example.alphakids.ui.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.StudentRepository
import com.example.alphakids.domain.usecases.CreateStudentUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class DocenteUi(val uid: String, val nombreCompleto: String)

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val createStudentUseCase: CreateStudentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStudentsUseCase: GetStudentsUseCase,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _createUiState = MutableStateFlow<StudentUiState>(StudentUiState.Idle)
    val createUiState: StateFlow<StudentUiState> = _createUiState.asStateFlow()

    private val tutorIdFlow: Flow<String?> = getCurrentUserUseCase()
        .map { it?.uid }

    private val _docentesUi = MutableStateFlow<UiState<List<DocenteUi>>>(UiState.Idle)
    val docentesUi: StateFlow<UiState<List<DocenteUi>>> = _docentesUi.asStateFlow()

    private var loadDocentesJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Estudiante>> = tutorIdFlow
        .flatMapLatest { tutorId ->
            if (tutorId != null) {
                getStudentsUseCase(tutorId)
                    .catch { e ->
                        Log.e("StudentViewModel", "Error fetching students", e)
                        emit(emptyList())
                    }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun createStudent(
        nombre: String,
        apellido: String,
        edad: Int,
        grado: String,
        seccion: String,
        idInstitucion: String,
        idDocente: String
    ) {
        viewModelScope.launch {
            _createUiState.value = StudentUiState.Loading

            val currentUser = getCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _createUiState.value = StudentUiState.Error("No se pudo obtener el usuario actual.")
                return@launch
            }
            val tutorId = currentUser.uid

            val nuevoEstudiante = Estudiante(
                nombre = nombre,
                apellido = apellido,
                edad = edad,
                grado = grado,
                seccion = seccion,
                idTutor = tutorId,
                idInstitucion = idInstitucion,
                idDocente = idDocente,
                fotoPerfil = null
            )

            Log.d("StudentViewModel", "Intentando crear estudiante: ${nuevoEstudiante.nombre} con idTutor: $tutorId")

            val result = createStudentUseCase(nuevoEstudiante)

            if (result.isSuccess) {
                _createUiState.value = StudentUiState.Success(result.getOrNull() ?: "unknown_id")
            } else {
                _createUiState.value = StudentUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido al crear perfil."
                )
            }
        }
    }

    fun resetCreateState() {
        _createUiState.value = StudentUiState.Idle
    }

    fun loadDocentes(institucionId: String?) {
        loadDocentesJob?.cancel()
        loadDocentesJob = viewModelScope.launch {
            _docentesUi.value = UiState.Loading
            studentRepository.getDocentes(institucionId?.takeIf { it.isNotBlank() })
                .map { docentes -> docentes.map { (uid, nombre) -> DocenteUi(uid, nombre) } }
                .catch { e ->
                    Log.e("StudentViewModel", "Error loading docentes", e)
                    _docentesUi.value = UiState.Error(e.message ?: "Error al cargar docentes")
                }
                .collect { docentes ->
                    _docentesUi.value = UiState.Success(docentes)
                }
        }
    }
}
