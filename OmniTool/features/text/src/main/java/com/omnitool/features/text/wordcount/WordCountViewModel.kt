package com.omnitool.features.text.wordcount

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Word/Character Counter Tool ViewModel
 * Live statistics as you type
 */
@HiltViewModel
class WordCountViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var stats by mutableStateOf(TextStats())
        private set
    
    fun updateInput(text: String) {
        inputText = text
        stats = calculateStats(text)
    }
    
    private fun calculateStats(text: String): TextStats {
        if (text.isEmpty()) return TextStats()
        
        val characters = text.length
        val charactersNoSpaces = text.replace(" ", "").length
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }.size
        val paragraphs = text.split(Regex("\n\n+")).filter { it.isNotBlank() }.size
        val lines = text.lines().size
        
        // Reading time estimate (average 200 words per minute)
        val readingTimeMinutes = words / 200.0
        val readingTimeSeconds = (readingTimeMinutes * 60).toInt()
        
        // Speaking time estimate (average 150 words per minute)
        val speakingTimeMinutes = words / 150.0
        val speakingTimeSeconds = (speakingTimeMinutes * 60).toInt()
        
        return TextStats(
            characters = characters,
            charactersNoSpaces = charactersNoSpaces,
            words = words,
            sentences = sentences,
            paragraphs = paragraphs,
            lines = lines,
            readingTimeSeconds = readingTimeSeconds,
            speakingTimeSeconds = speakingTimeSeconds
        )
    }
    
    fun clearAll() {
        inputText = ""
        stats = TextStats()
    }
}

data class TextStats(
    val characters: Int = 0,
    val charactersNoSpaces: Int = 0,
    val words: Int = 0,
    val sentences: Int = 0,
    val paragraphs: Int = 0,
    val lines: Int = 0,
    val readingTimeSeconds: Int = 0,
    val speakingTimeSeconds: Int = 0
) {
    fun formatReadingTime(): String = formatTime(readingTimeSeconds)
    fun formatSpeakingTime(): String = formatTime(speakingTimeSeconds)
    
    private fun formatTime(seconds: Int): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
            else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
        }
    }
}
