package com.omnitool.features.converter.storage

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Data Size Converter Tool ViewModel
 * Converts between Bytes, KB, MB, GB, TB, PB
 */
@HiltViewModel
class StorageConverterViewModel @Inject constructor() : ViewModel() {
    
    var inputValue by mutableStateOf("")
        private set
    
    var fromUnit by mutableStateOf(StorageUnit.MEGABYTE)
        private set
    
    var results by mutableStateOf<Map<StorageUnit, String>>(emptyMap())
        private set
    
    fun updateInput(value: String) {
        inputValue = value
        convert()
    }
    
    fun setFromUnit(unit: StorageUnit) {
        fromUnit = unit
        convert()
    }
    
    private fun convert() {
        val input = inputValue.toDoubleOrNull()
        if (input == null) {
            results = emptyMap()
            return
        }
        
        // Convert to bytes first
        val bytes = input * fromUnit.bytes
        
        // Convert to all units
        results = StorageUnit.values().associate { unit ->
            unit to formatResult(bytes / unit.bytes)
        }
    }
    
    private fun formatResult(value: Double): String {
        return when {
            value == 0.0 -> "0"
            value < 0.0001 -> String.format("%.2e", value)
            value >= 1_000_000_000 -> String.format("%,.0f", value)
            value >= 1000 -> String.format("%,.2f", value)
            value >= 1 -> String.format("%.4f", value).trimEnd('0').trimEnd('.')
            else -> String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }
    
    fun clearAll() {
        inputValue = ""
        results = emptyMap()
    }
}

enum class StorageUnit(val displayName: String, val symbol: String, val bytes: Double) {
    BIT("Bit", "b", 0.125),
    BYTE("Byte", "B", 1.0),
    KILOBYTE("Kilobyte", "KB", 1024.0),
    MEGABYTE("Megabyte", "MB", 1024.0 * 1024),
    GIGABYTE("Gigabyte", "GB", 1024.0 * 1024 * 1024),
    TERABYTE("Terabyte", "TB", 1024.0 * 1024 * 1024 * 1024),
    PETABYTE("Petabyte", "PB", 1024.0 * 1024 * 1024 * 1024 * 1024)
}
