package com.omnitool.features.text.csv

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * CSV Viewer Tool ViewModel
 * Parses CSV data and displays as a table
 */
@HiltViewModel
class CsvViewerViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var parsedData by mutableStateOf<List<List<String>>>(emptyList())
        private set
    
    var headers by mutableStateOf<List<String>>(emptyList())
        private set
    
    var rows by mutableStateOf<List<List<String>>>(emptyList())
        private set
    
    var delimiter by mutableStateOf(',')
        private set
    
    var hasHeaders by mutableStateOf(true)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var columnCount by mutableStateOf(0)
        private set
    
    var rowCount by mutableStateOf(0)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        parseData()
    }
    
    fun setDelimiter(newDelimiter: Char) {
        delimiter = newDelimiter
        parseData()
    }
    
    fun setHasHeaders(value: Boolean) {
        hasHeaders = value
        reorganizeData()
    }
    
    private fun parseData() {
        try {
            if (inputText.isEmpty()) {
                parsedData = emptyList()
                headers = emptyList()
                rows = emptyList()
                columnCount = 0
                rowCount = 0
                errorMessage = null
                return
            }
            
            val lines = inputText.lines().filter { it.isNotBlank() }
            parsedData = lines.map { line ->
                parseCsvLine(line, delimiter)
            }
            
            columnCount = parsedData.maxOfOrNull { it.size } ?: 0
            rowCount = parsedData.size
            
            reorganizeData()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Error parsing CSV: ${e.message}"
        }
    }
    
    private fun parseCsvLine(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == delimiter && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())
        
        return result
    }
    
    private fun reorganizeData() {
        if (parsedData.isEmpty()) {
            headers = emptyList()
            rows = emptyList()
            return
        }
        
        if (hasHeaders && parsedData.isNotEmpty()) {
            headers = parsedData.first()
            rows = parsedData.drop(1)
        } else {
            headers = (1..columnCount).map { "Col $it" }
            rows = parsedData
        }
    }
    
    fun clearAll() {
        inputText = ""
        parsedData = emptyList()
        headers = emptyList()
        rows = emptyList()
        columnCount = 0
        rowCount = 0
        errorMessage = null
    }
}
