package com.omnitool.features.text.scratchpad

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Code Scratchpad Tool ViewModel
 * A simple notepad for code snippets with auto-save
 */
@HiltViewModel
class ScratchpadViewModel @Inject constructor() : ViewModel() {
    
    var content by mutableStateOf("")
        private set
    
    var language by mutableStateOf(ScratchpadLanguage.PLAIN)
        private set
    
    var lineCount by mutableStateOf(0)
        private set
    
    var charCount by mutableStateOf(0)
        private set
    
    var isSaved by mutableStateOf(true)
        private set
    
    fun updateContent(text: String) {
        content = text
        lineCount = text.lines().size
        charCount = text.length
        isSaved = false
    }
    
    fun setLanguage(lang: ScratchpadLanguage) {
        language = lang
    }
    
    fun save() {
        // In a real app, this would persist to DataStore or file
        viewModelScope.launch {
            isSaved = true
        }
    }
    
    fun clearAll() {
        content = ""
        lineCount = 0
        charCount = 0
        isSaved = true
    }
}

enum class ScratchpadLanguage(val displayName: String, val extension: String) {
    PLAIN("Plain Text", "txt"),
    KOTLIN("Kotlin", "kt"),
    JAVA("Java", "java"),
    JAVASCRIPT("JavaScript", "js"),
    PYTHON("Python", "py"),
    JSON("JSON", "json"),
    XML("XML", "xml"),
    HTML("HTML", "html"),
    CSS("CSS", "css"),
    SQL("SQL", "sql")
}
