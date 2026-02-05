package com.omnitool.features.text.caseconverter

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

/**
 * Case Converter Tool ViewModel
 * Supports: UPPER, lower, Title Case, Sentence case, camelCase, snake_case, kebab-case
 */
@HiltViewModel
class CaseConverterViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputs by mutableStateOf<Map<CaseType, String>>(emptyMap())
        private set
    
    fun updateInput(text: String) {
        inputText = text
        if (text.isNotEmpty()) {
            convertAll()
        } else {
            outputs = emptyMap()
        }
    }
    
    private fun convertAll() {
        outputs = CaseType.values().associateWith { caseType ->
            convertTo(inputText, caseType)
        }
    }
    
    private fun convertTo(text: String, caseType: CaseType): String {
        return when (caseType) {
            CaseType.UPPER -> text.uppercase(Locale.getDefault())
            CaseType.LOWER -> text.lowercase(Locale.getDefault())
            CaseType.TITLE -> text.split(" ").joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
            CaseType.SENTENCE -> text.split(". ").joinToString(". ") { sentence ->
                sentence.lowercase().replaceFirstChar { it.uppercase() }
            }
            CaseType.CAMEL -> {
                val words = text.split(Regex("[\\s_-]+"))
                words.mapIndexed { index, word ->
                    if (index == 0) word.lowercase()
                    else word.lowercase().replaceFirstChar { it.uppercase() }
                }.joinToString("")
            }
            CaseType.SNAKE -> {
                text.replace(Regex("[\\s-]+"), "_")
                    .replace(Regex("([a-z])([A-Z])"), "$1_$2")
                    .lowercase()
            }
            CaseType.KEBAB -> {
                text.replace(Regex("[\\s_]+"), "-")
                    .replace(Regex("([a-z])([A-Z])"), "$1-$2")
                    .lowercase()
            }
            CaseType.PASCAL -> {
                text.split(Regex("[\\s_-]+")).joinToString("") { word ->
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
            }
            CaseType.CONSTANT -> {
                text.replace(Regex("[\\s-]+"), "_")
                    .replace(Regex("([a-z])([A-Z])"), "$1_$2")
                    .uppercase()
            }
            CaseType.ALTERNATING -> {
                text.mapIndexed { index, char ->
                    if (index % 2 == 0) char.lowercase() else char.uppercase()
                }.joinToString("")
            }
        }
    }
    
    fun clearAll() {
        inputText = ""
        outputs = emptyMap()
    }
}

enum class CaseType(val displayName: String) {
    UPPER("UPPER CASE"),
    LOWER("lower case"),
    TITLE("Title Case"),
    SENTENCE("Sentence case"),
    CAMEL("camelCase"),
    PASCAL("PascalCase"),
    SNAKE("snake_case"),
    KEBAB("kebab-case"),
    CONSTANT("CONSTANT_CASE"),
    ALTERNATING("aLtErNaTiNg")
}
