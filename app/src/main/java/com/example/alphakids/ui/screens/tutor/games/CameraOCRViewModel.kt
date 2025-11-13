package com.example.alphakids.ui.screens.tutor.games

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.Normalizer
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class ScanUiState {
    data object Scanner : ScanUiState()
    data object Evaluating : ScanUiState()
    data class Result(val success: Boolean, val text: String) : ScanUiState()
}

@HiltViewModel
class CameraOCRViewModel @Inject constructor() : ViewModel() {

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Scanner)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

    private val _isFlashEnabled = MutableStateFlow(false)
    val isFlashEnabled: StateFlow<Boolean> = _isFlashEnabled.asStateFlow()

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector: StateFlow<CameraSelector> = _cameraSelector.asStateFlow()

    private val lastRecognizedText = MutableStateFlow("")

    fun onTextDetected(text: String) {
        lastRecognizedText.value = text
    }

    fun onShutter(targetWord: String) {
        _scanState.value = ScanUiState.Evaluating
        val recognizedText = lastRecognizedText.value
        val success = norm(recognizedText).contains(norm(targetWord))
        _scanState.value = ScanUiState.Result(success = success, text = recognizedText)
    }

    fun toggleFlash() {
        _isFlashEnabled.update { !it }
    }

    fun flipCamera() {
        val current = _cameraSelector.value
        _cameraSelector.value = if (current == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    fun resetToScanner() {
        _scanState.value = ScanUiState.Scanner
    }

    private fun norm(value: String): String {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()
    }
}
