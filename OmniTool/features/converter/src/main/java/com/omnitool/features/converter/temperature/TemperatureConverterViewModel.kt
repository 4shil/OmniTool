package com.omnitool.features.converter.temperature

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Temperature Converter Tool ViewModel
 */
@HiltViewModel
class TemperatureConverterViewModel @Inject constructor() : ViewModel() {
    
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
    
    fun updateInput(value: String) {
        inputValue = value
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
        
        // Then convert to all units
        celsiusResult = formatResult(celsius)
        fahrenheitResult = formatResult(celsius * 9 / 5 + 32)
        kelvinResult = formatResult(celsius + 273.15)
    }
    
    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.2f", value)
        }
    }
    
    fun clearAll() {
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
