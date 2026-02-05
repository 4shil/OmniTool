package com.omnitool.features.text.duplicateremover

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Duplicate Line Remover Tool ViewModel
 */
@HiltViewModel
class DuplicateRemoverViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var removedCount by mutableStateOf(0)
        private set
    
    var caseSensitive by mutableStateOf(true)
        private set
    
    var trimWhitespace by mutableStateOf(true)
        private set
    
    fun updateInput(text: String) {
        inputText = text
    }
    
    fun setCaseSensitive(enabled: Boolean) {
        caseSensitive = enabled
    }
    
    fun setTrimWhitespace(enabled: Boolean) {
        trimWhitespace = enabled
    }
    
    fun removeDuplicates() {
        if (inputText.isEmpty()) {
            outputText = ""
            removedCount = 0
            return
        }
        
        val lines = inputText.lines()
        val originalCount = lines.size
        
        val seen = mutableSetOf<String>()
        val uniqueLines = lines.filter { line ->
            val processedLine = when {
                trimWhitespace && !caseSensitive -> line.trim().lowercase()
                trimWhitespace -> line.trim()
                !caseSensitive -> line.lowercase()
                else -> line
            }
            seen.add(processedLine)
        }
        
        outputText = uniqueLines.joinToString("\n")
        removedCount = originalCount - uniqueLines.size
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        removedCount = 0
    }
}
