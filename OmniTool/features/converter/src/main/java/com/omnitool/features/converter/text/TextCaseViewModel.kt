package com.omnitool.features.converter.text

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Text Case Converter ViewModel
 * 
 * Features:
 * - Multiple case conversions
 * - Character/word count
 * - Copy result
 */
@HiltViewModel
class TextCaseViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var selectedCase by mutableStateOf(TextCase.LOWERCASE)
        private set
    
    var charCount by mutableIntStateOf(0)
        private set
    
    var wordCount by mutableIntStateOf(0)
        private set
    
    var lineCount by mutableIntStateOf(0)
        private set
    
    fun setInput(text: String) {
        inputText = text
        updateCounts()
        convertText()
    }
    
    fun setCase(case: TextCase) {
        selectedCase = case
        convertText()
    }
    
    private fun updateCounts() {
        charCount = inputText.length
        wordCount = if (inputText.isBlank()) 0 else inputText.trim().split(Regex("\\s+")).size
        lineCount = if (inputText.isEmpty()) 0 else inputText.lines().size
    }
    
    private fun convertText() {
        outputText = when (selectedCase) {
            TextCase.LOWERCASE -> inputText.lowercase()
            TextCase.UPPERCASE -> inputText.uppercase()
            TextCase.TITLE_CASE -> toTitleCase(inputText)
            TextCase.SENTENCE_CASE -> toSentenceCase(inputText)
            TextCase.CAMEL_CASE -> toCamelCase(inputText)
            TextCase.SNAKE_CASE -> toSnakeCase(inputText)
            TextCase.KEBAB_CASE -> toKebabCase(inputText)
            TextCase.PASCAL_CASE -> toPascalCase(inputText)
            TextCase.CONSTANT_CASE -> toConstantCase(inputText)
            TextCase.ALTERNATING -> toAlternatingCase(inputText)
            TextCase.INVERSE -> inputText.map { 
                if (it.isUpperCase()) it.lowercase() else it.uppercase() 
            }.joinToString("")
        }
    }
    
    private fun toTitleCase(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
    
    private fun toSentenceCase(text: String): String {
        return text.lowercase().split(". ").joinToString(". ") { sentence ->
            sentence.replaceFirstChar { it.uppercase() }
        }
    }
    
    private fun toCamelCase(text: String): String {
        return text.split(Regex("[\\s_-]+"))
            .mapIndexed { index, word ->
                if (index == 0) word.lowercase()
                else word.lowercase().replaceFirstChar { it.uppercase() }
            }
            .joinToString("")
    }
    
    private fun toPascalCase(text: String): String {
        return text.split(Regex("[\\s_-]+"))
            .joinToString("") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
    
    private fun toSnakeCase(text: String): String {
        return text.trim()
            .replace(Regex("[\\s-]+"), "_")
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
    }
    
    private fun toKebabCase(text: String): String {
        return text.trim()
            .replace(Regex("[\\s_]+"), "-")
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()
    }
    
    private fun toConstantCase(text: String): String {
        return toSnakeCase(text).uppercase()
    }
    
    private fun toAlternatingCase(text: String): String {
        return text.mapIndexed { index, char ->
            if (index % 2 == 0) char.lowercase() else char.uppercase()
        }.joinToString("")
    }
    
    fun clear() {
        inputText = ""
        outputText = ""
        charCount = 0
        wordCount = 0
        lineCount = 0
    }
}

enum class TextCase(val displayName: String) {
    LOWERCASE("lowercase"),
    UPPERCASE("UPPERCASE"),
    TITLE_CASE("Title Case"),
    SENTENCE_CASE("Sentence case"),
    CAMEL_CASE("camelCase"),
    PASCAL_CASE("PascalCase"),
    SNAKE_CASE("snake_case"),
    KEBAB_CASE("kebab-case"),
    CONSTANT_CASE("CONSTANT_CASE"),
    ALTERNATING("aLtErNaTiNg"),
    INVERSE("iNVERSE")
}
