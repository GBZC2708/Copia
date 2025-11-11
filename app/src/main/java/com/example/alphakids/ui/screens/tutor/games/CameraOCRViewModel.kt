package com.example.alphakids.ui.screens.tutor.games

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class CameraOCRUiState(
    val targetWord: String = "",
    val detectedText: String = "",
    val isWordDetected: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CameraOCRViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraOCRUiState())
    val uiState: StateFlow<CameraOCRUiState> = _uiState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var lastSpokenText = ""
    private var wordDetectedTime = 0L

    fun initializeTTS(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale("es", "ES"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Spanish language not supported")
                        // Fallback to default language
                        tts.setLanguage(Locale.getDefault())
                    }
                    tts.setSpeechRate(0.9f)
                }
            } else {
                Log.e("TTS", "TTS initialization failed")
            }
        }
    }

    fun setTargetWord(word: String) {
        _uiState.value = _uiState.value.copy(targetWord = word.uppercase())
    }

    fun processDetectedText(detectedText: String) {
        val cleanText = detectedText.trim().uppercase()
        val targetWord = _uiState.value.targetWord

        _uiState.value = _uiState.value.copy(detectedText = cleanText)

        // Check if target word is found in detected text
        val isWordFound = cleanText.contains(targetWord) && targetWord.isNotEmpty()

        if (isWordFound && !_uiState.value.isWordDetected) {
            // Word detected for the first time
            wordDetectedTime = System.currentTimeMillis()
            _uiState.value = _uiState.value.copy(isWordDetected = true)

            // Speak success message
            val successMessage = "¡Bien hecho! La palabra es $targetWord"
            speakText(successMessage)

            // Save completion to history
            saveWordCompletion(targetWord)

            Log.d("CameraOCR", "Word '$targetWord' detected successfully!")
        } else if (!isWordFound && _uiState.value.isWordDetected) {
            // Word no longer detected, reset after a delay
            val currentTime = System.currentTimeMillis()
            if (currentTime - wordDetectedTime > 3000) { // 3 seconds grace period
                _uiState.value = _uiState.value.copy(isWordDetected = false)
            }
        }
    }

    private fun speakText(text: String) {
        if (text != lastSpokenText) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            lastSpokenText = text
            Log.d("TTS", "Speaking: $text")
        }
    }

    private fun saveWordCompletion(word: String) {
        viewModelScope.launch {
            try {
                // Save to Firestore (optional)
                val completedWordData = hashMapOf(
                    "word" to word,
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("completed_words")
                    .add(completedWordData)
                    .addOnSuccessListener {
                        Log.d("CameraOCRViewModel", "Word saved to Firestore: $word")
                    }
                    .addOnFailureListener { e ->
                        Log.e("CameraOCRViewModel", "Error saving to Firestore", e)
                    }
            } catch (e: Exception) {
                Log.e("CameraOCRViewModel", "Error in saveWordCompletion", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}