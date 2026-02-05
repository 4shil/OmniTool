package com.omnitool.features.text.json

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * JSON Formatter Tool ViewModel
 * 
 * Features:
 * - Format/beautify JSON
 * - Validate JSON
 * - Minify JSON
 */
@HiltViewModel
class JsonFormatterViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isValid by mutableStateOf<Boolean?>(null)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        errorMessage = null
        isValid = null
    }
    
    fun formatJson(indent: Int = 4) {
        viewModelScope.launch {
            try {
                val trimmed = inputText.trim()
                if (trimmed.isEmpty()) {
                    errorMessage = "Please enter JSON to format"
                    outputText = ""
                    isValid = null
                    return@launch
                }
                
                val formatted = when {
                    trimmed.startsWith("{") -> {
                        val json = JSONObject(trimmed)
                        json.toString(indent)
                    }
                    trimmed.startsWith("[") -> {
                        val json = JSONArray(trimmed)
                        json.toString(indent)
                    }
                    else -> throw JSONException("JSON must start with { or [")
                }
                
                outputText = formatted
                errorMessage = null
                isValid = true
            } catch (e: JSONException) {
                outputText = ""
                errorMessage = parseJsonError(e.message ?: "Invalid JSON")
                isValid = false
            }
        }
    }
    
    fun minifyJson() {
        viewModelScope.launch {
            try {
                val trimmed = inputText.trim()
                if (trimmed.isEmpty()) {
                    errorMessage = "Please enter JSON to minify"
                    outputText = ""
                    return@launch
                }
                
                val minified = when {
                    trimmed.startsWith("{") -> {
                        val json = JSONObject(trimmed)
                        json.toString()
                    }
                    trimmed.startsWith("[") -> {
                        val json = JSONArray(trimmed)
                        json.toString()
                    }
                    else -> throw JSONException("JSON must start with { or [")
                }
                
                outputText = minified
                errorMessage = null
                isValid = true
            } catch (e: JSONException) {
                outputText = ""
                errorMessage = parseJsonError(e.message ?: "Invalid JSON")
                isValid = false
            }
        }
    }
    
    fun validateJson() {
        viewModelScope.launch {
            try {
                val trimmed = inputText.trim()
                if (trimmed.isEmpty()) {
                    errorMessage = "Please enter JSON to validate"
                    isValid = null
                    return@launch
                }
                
                when {
                    trimmed.startsWith("{") -> JSONObject(trimmed)
                    trimmed.startsWith("[") -> JSONArray(trimmed)
                    else -> throw JSONException("JSON must start with { or [")
                }
                
                outputText = "✓ Valid JSON"
                errorMessage = null
                isValid = true
            } catch (e: JSONException) {
                outputText = ""
                errorMessage = parseJsonError(e.message ?: "Invalid JSON")
                isValid = false
            }
        }
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        errorMessage = null
        isValid = null
    }
    
    private fun parseJsonError(error: String): String {
        // Make error messages more user-friendly
        return when {
            error.contains("Unterminated string") -> 
                "Missing closing quote for a string value"
            error.contains("Expected") -> 
                error.replace("at character", "near position")
            error.contains("No value for") ->
                "Missing value for key: ${error.substringAfter("No value for")}"
            else -> error
        }
    }
}
