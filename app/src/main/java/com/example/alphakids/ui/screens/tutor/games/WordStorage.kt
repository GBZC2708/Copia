package com.example.alphakids.ui.screens.tutor.games

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object WordStorage {
    private const val PREFS_NAME = "word_completion_history"
    private const val KEY_COMPLETED_WORDS = "completed_words"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCompletedWord(context: Context, word: String, assignmentId: String) {
        val prefs = getPreferences(context)
        val gson = Gson()

        // Get existing completed words
        val existingWordsJson = prefs.getString(KEY_COMPLETED_WORDS, "[]")
        val type = object : TypeToken<MutableList<CompletedWord>>() {}.type
        val completedWords: MutableList<CompletedWord> = gson.fromJson(existingWordsJson, type)

        // Create new completed word entry
        val newCompletedWord = CompletedWord(
            word = word,
            timestamp = System.currentTimeMillis()
        )

        // Add to list (most recent first)
        completedWords.add(0, newCompletedWord)

        // Keep only last 100 entries
        if (completedWords.size > 100) {
            completedWords.removeAt(completedWords.size - 1)
        }

        // Save back to  xpPYkul1tYW36kQTWrxwR2udw9s2 preferences
        val updatedJson = gson.toJson(completedWords)
        prefs.edit().putString(KEY_COMPLETED_WORDS, updatedJson).apply()

        android.util.Log.d("WordStorage", "Saved completed word: $word")
    }

    fun getCompletedWords(context: Context): List<CompletedWord> {
        val prefs = getPreferences(context)
        val gson = Gson()

        val wordsJson = prefs.getString(KEY_COMPLETED_WORDS, "[]")
        val type = object : TypeToken<List<CompletedWord>>() {}.type

        return gson.fromJson(wordsJson, type) ?: emptyList()
    }

    fun clearHistory(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_COMPLETED_WORDS).apply()
    }

    fun getCompletedWordsCount(context: Context): Int {
        return getCompletedWords(context).size
    }

    fun hasCompletedWord(context: Context, word: String): Boolean {
        val completedWords = getCompletedWords(context)
        return completedWords.any { it.word.equals(word, ignoreCase = true) }
    }
}