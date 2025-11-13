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

data class WordPuzzleUiState(
    val isLoading: Boolean = false,
    val assignment: AsignacionPalabra? = null,
    val error: String? = null
)

@HiltViewModel
class WordPuzzleViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordPuzzleUiState())
    val uiState: StateFlow<WordPuzzleUiState> = _uiState.asStateFlow()

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord.asStateFlow()

    fun loadWordData(assignmentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            _selectedWord.value = null

            try {
                val assignment = firestore.collection("asignaciones")
                    .document(assignmentId)
                    .get()
                    .await()
                    .toObject(AsignacionPalabra::class.java)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignment = assignment
                )
                _selectedWord.value = assignment?.palabraTexto
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
                _selectedWord.value = null
            }
        }
    }
}
