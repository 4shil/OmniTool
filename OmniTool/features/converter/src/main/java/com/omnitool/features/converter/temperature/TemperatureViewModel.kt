package com.omnitool.features.converter.temperature

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Temperature Converter ViewModel
 * 
 * Features:
 * - Celsius, Fahrenheit, Kelvin
 * - Real-time conversion
 */
@HiltViewModel
class TemperatureViewModel @Inject constructor() : ViewModel() {
    
    var inputValue by mutableStateOf("")
        private set
    
    var fromUnit by mutableStateOf(TempUnit.CELSIUS)
        private set
    
    var celsiusResult by mutableStateOf("")
        private set
    
    var fahrenheitResult by mutableStateOf("")
        private set
    
    var kelvinResult by mutableStateOf("")
        private set
    
    fun setInput(value: String) {
        inputValue = value.filter { it.isDigit() || it == '.' || it == '-' }
        convert()
    }
    
    fun setFromUnit(unit: TempUnit) {
        fromUnit = unit
        convert()
    }
    
    private fun convert() {
        val input = inputValue.toDoubleOrNull()
        if (input == null) {
            celsiusResult = ""
            fahrenheitResult = ""
            kelvinResult = ""
            return
        }
        
        // Convert to Celsius first
        val celsius = when (fromUnit) {
            TempUnit.CELSIUS -> input
            TempUnit.FAHRENHEIT -> (input - 32) * 5 / 9
            TempUnit.KELVIN -> input - 273.15
        }
        
        // Convert from Celsius to all units
        celsiusResult = formatTemp(celsius)
        fahrenheitResult = formatTemp(celsius * 9 / 5 + 32)
        kelvinResult = formatTemp(celsius + 273.15)
    }
    
    private fun formatTemp(value: Double): String {
        return String.format("%.2f", value)
    }
    
    fun clear() {
        inputValue = ""
        celsiusResult = ""
        fahrenheitResult = ""
        kelvinResult = ""
    }
}

enum class TempUnit(val displayName: String, val symbol: String) {
    CELSIUS("Celsius", "°C"),
    FAHRENHEIT("Fahrenheit", "°F"),
    KELVIN("Kelvin", "K")
}
