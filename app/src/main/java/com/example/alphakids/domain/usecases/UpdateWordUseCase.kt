package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordRepository
import javax.inject.Inject

class   UpdateWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(word: Word): Result<Unit> {
        return repository.updateWord(word)
    }
}
