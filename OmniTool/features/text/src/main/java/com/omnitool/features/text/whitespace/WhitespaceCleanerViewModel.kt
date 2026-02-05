package com.omnitool.features.text.whitespace

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Whitespace Cleaner Tool ViewModel
 */
@HiltViewModel
class WhitespaceCleanerViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var options by mutableStateOf(CleanOptions())
        private set
    
    var cleaningStats by mutableStateOf<CleaningStats?>(null)
        private set
    
    fun updateInput(text: String) {
        inputText = text
    }
    
    fun updateOptions(newOptions: CleanOptions) {
        options = newOptions
    }
    
    fun clean() {
        if (inputText.isEmpty()) {
            outputText = ""
            cleaningStats = null
            return
        }
        
        var result = inputText
        var spacesRemoved = 0
        var tabsRemoved = 0
        var emptyLinesRemoved = 0
        var linesTrimed = 0
        
        if (options.removeTrailingSpaces) {
            val before = result.lines()
            result = result.lines().map { it.trimEnd() }.joinToString("\n")
            linesTrimed = before.count { it != it.trimEnd() }
        }
        
        if (options.removeLeadingSpaces) {
            result = result.lines().map { it.trimStart() }.joinToString("\n")
        }
        
        if (options.convertTabsToSpaces) {
            val tabCount = result.count { it == '\t' }
            result = result.replace("\t", "    ")
            tabsRemoved = tabCount
        }
        
        if (options.removeExtraSpaces) {
            val before = result.length
            result = result.replace(Regex(" {2,}"), " ")
            spacesRemoved = before - result.length
        }
        
        if (options.removeEmptyLines) {
            val linesBefore = result.lines().size
            result = result.lines().filter { it.isNotBlank() }.joinToString("\n")
            emptyLinesRemoved = linesBefore - result.lines().size
        }
        
        if (options.normalizeLineEndings) {
            result = result.replace("\r\n", "\n").replace("\r", "\n")
        }
        
        outputText = result
        cleaningStats = CleaningStats(
            charactersRemoved = inputText.length - result.length,
            spacesNormalized = spacesRemoved,
            tabsConverted = tabsRemoved,
            emptyLinesRemoved = emptyLinesRemoved
        )
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        cleaningStats = null
    }
}

data class CleanOptions(
    val removeTrailingSpaces: Boolean = true,
    val removeLeadingSpaces: Boolean = false,
    val removeExtraSpaces: Boolean = true,
    val removeEmptyLines: Boolean = false,
    val convertTabsToSpaces: Boolean = true,
    val normalizeLineEndings: Boolean = true
)

data class CleaningStats(
    val charactersRemoved: Int,
    val spacesNormalized: Int,
    val tabsConverted: Int,
    val emptyLinesRemoved: Int
)
