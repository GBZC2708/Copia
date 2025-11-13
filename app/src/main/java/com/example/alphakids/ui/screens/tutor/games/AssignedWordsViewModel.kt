package com.example.alphakids.ui.screens.tutor.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// 🧠 Estructura de UI State
data class AssignedWordsUiState(
    val isLoading: Boolean = false,
    val assignedWords: List<AsignacionPalabra> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AssignedWordsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignedWordsUiState())
    val uiState: StateFlow<AssignedWordsUiState> = _uiState.asStateFlow()

    // ============================================================
    // 🚀 1. CARGAR PALABRAS ASIGNADAS (sin duplicados)
    // ============================================================
    fun loadAssignedWords(studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                android.util.Log.d("AssignedWords", "=== LOADING ASSIGNMENTS ===")
                android.util.Log.d("AssignedWords", "Student ID: $studentId")

                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Usuario no autenticado. Por favor, inicia sesión."
                    )
                    return@launch
                }

                // 🔹 Consultar Firestore
                val assignments = firestore.collection("asignaciones")
                    .whereEqualTo("id_estudiante", studentId)
                    .whereEqualTo("estado", "PENDIENTE")
                    .get()
                    .await()
                    .toObjects(AsignacionPalabra::class.java)

                // 🔹 Evitar duplicados por texto de palabra
                val uniqueAssignments = assignments.distinctBy {
                    it.palabraTexto?.trim()?.uppercase()
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignedWords = uniqueAssignments
                )

                android.util.Log.d("AssignedWords", "Cargadas ${uniqueAssignments.size} palabras únicas.")

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("PERMISSION_DENIED") == true ->
                        "🔒 Error de permisos: Las reglas de Firestore bloquean el acceso."
                    e.message?.contains("UNAUTHENTICATED") == true ->
                        "Usuario no autenticado. Inicia sesión nuevamente."
                    else -> "Error al cargar asignaciones: ${e.message}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    // ============================================================
    // ⚡ 2. ELIMINAR PALABRA COMPLETADA (reactivo)
    // ============================================================
    fun markWordAsCompleted(completedWord: String) {
        val currentState = _uiState.value
        val updatedList = currentState.assignedWords.filterNot {
            it.palabraTexto?.trim()?.equals(completedWord.trim(), ignoreCase = true) == true
        }
        _uiState.value = currentState.copy(assignedWords = updatedList)

        android.util.Log.d("AssignedWords", "Palabra eliminada localmente: $completedWord")
        android.util.Log.d("AssignedWords", "Palabras restantes: ${updatedList.size}")
    }

    // ============================================================
    // ⚙️ 3. OPCIONAL: RESETEAR ERRORES O ESTADOS
    // ============================================================
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
