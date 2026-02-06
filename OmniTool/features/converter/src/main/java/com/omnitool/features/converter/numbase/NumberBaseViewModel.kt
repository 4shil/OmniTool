package com.omnitool.features.converter.numbase

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Number Base Converter ViewModel
 * 
 * Features:
 * - Convert between binary, octal, decimal, hex
 * - Real-time conversion
 * - Input validation
 */
@HiltViewModel
class NumberBaseViewModel @Inject constructor() : ViewModel() {
    
    var inputValue by mutableStateOf("")
        private set
    
    var fromBase by mutableStateOf(NumberBase.DECIMAL)
        private set
    
    var binaryResult by mutableStateOf("")
        private set
    
    var octalResult by mutableStateOf("")
        private set
    
    var decimalResult by mutableStateOf("")
        private set
    
    var hexResult by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    fun setInput(value: String) {
        // Validate input based on current base
        val filtered = when (fromBase) {
            NumberBase.BINARY -> value.filter { it in "01" }
            NumberBase.OCTAL -> value.filter { it in "01234567" }
            NumberBase.DECIMAL -> value.filter { it.isDigit() }
            NumberBase.HEXADECIMAL -> value.filter { it.isDigit() || it.uppercaseChar() in "ABCDEF" }.uppercase()
        }
        inputValue = filtered
        convert()
    }
    
    fun setFromBase(base: NumberBase) {
        fromBase = base
        inputValue = "" // Clear input when base changes
        clearResults()
    }
    
    private fun convert() {
        errorMessage = null
        
        if (inputValue.isEmpty()) {
            clearResults()
            return
        }
        
        try {
            // Convert input to decimal first
            val decimalValue = inputValue.toLong(fromBase.radix)
            
            // Convert to all bases
            binaryResult = decimalValue.toString(2)
            octalResult = decimalValue.toString(8)
            decimalResult = decimalValue.toString(10)
            hexResult = decimalValue.toString(16).uppercase()
            
        } catch (e: NumberFormatException) {
            errorMessage = "Invalid number for ${fromBase.displayName}"
            clearResults()
        }
    }
    
    private fun clearResults() {
        binaryResult = ""
        octalResult = ""
        decimalResult = ""
        hexResult = ""
    }
    
    fun clear() {
        inputValue = ""
        errorMessage = null
        clearResults()
    }
    
    fun getResultForBase(base: NumberBase): String {
        return when (base) {
            NumberBase.BINARY -> binaryResult
            NumberBase.OCTAL -> octalResult
            NumberBase.DECIMAL -> decimalResult
            NumberBase.HEXADECIMAL -> hexResult
        }
    }
}

enum class NumberBase(val displayName: String, val radix: Int, val prefix: String) {
    BINARY("Binary", 2, "0b"),
    OCTAL("Octal", 8, "0o"),
    DECIMAL("Decimal", 10, ""),
    HEXADECIMAL("Hex", 16, "0x")
}
