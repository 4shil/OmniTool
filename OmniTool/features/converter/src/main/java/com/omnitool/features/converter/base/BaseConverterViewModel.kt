package com.omnitool.features.converter.base

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Number Base Converter Tool ViewModel
 * Converts between Binary, Octal, Decimal, Hexadecimal
 */
@HiltViewModel
class BaseConverterViewModel @Inject constructor() : ViewModel() {
    
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
    
    fun updateInput(value: String) {
        inputValue = value.uppercase()
        convert()
    }
    
    fun setFromBase(base: NumberBase) {
        fromBase = base
        convert()
    }
    
    private fun convert() {
        if (inputValue.isEmpty()) {
            clearResults()
            return
        }
        
        try {
            // First convert to decimal
            val decimal = when (fromBase) {
                NumberBase.BINARY -> inputValue.toLong(2)
                NumberBase.OCTAL -> inputValue.toLong(8)
                NumberBase.DECIMAL -> inputValue.toLong(10)
                NumberBase.HEXADECIMAL -> inputValue.toLong(16)
            }
            
            // Then convert to all bases
            binaryResult = formatBinary(decimal.toString(2).uppercase())
            octalResult = decimal.toString(8)
            decimalResult = formatDecimal(decimal)
            hexResult = decimal.toString(16).uppercase()
            errorMessage = null
        } catch (e: NumberFormatException) {
            clearResults()
            errorMessage = "Invalid ${fromBase.displayName} number"
        }
    }
    
    private fun formatBinary(binary: String): String {
        // Add spaces every 4 digits for readability
        return binary.reversed().chunked(4).joinToString(" ").reversed()
    }
    
    private fun formatDecimal(value: Long): String {
        // Add commas for thousands
        return String.format("%,d", value)
    }
    
    private fun clearResults() {
        binaryResult = ""
        octalResult = ""
        decimalResult = ""
        hexResult = ""
    }
    
    fun clearAll() {
        inputValue = ""
        clearResults()
        errorMessage = null
    }
}

enum class NumberBase(val displayName: String, val radix: Int, val prefix: String) {
    BINARY("Binary", 2, "0b"),
    OCTAL("Octal", 8, "0o"),
    DECIMAL("Decimal", 10, ""),
    HEXADECIMAL("Hex", 16, "0x")
}
