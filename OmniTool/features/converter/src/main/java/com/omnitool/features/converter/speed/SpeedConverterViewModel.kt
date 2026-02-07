package com.omnitool.features.converter.speed

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Speed Converter ViewModel
 * 
 * Features:
 * - Convert between speed units
 * - Real-time conversion
 */
@HiltViewModel
class SpeedConverterViewModel @Inject constructor() : ViewModel() {
    
    var inputValue by mutableStateOf("")
        private set
    
    var fromUnit by mutableStateOf(SpeedUnit.KMH)
        private set
    
    var results by mutableStateOf<Map<SpeedUnit, String>>(emptyMap())
        private set
    
    fun setInput(value: String) {
        inputValue = value.filter { it.isDigit() || it == '.' }
        convert()
    }
    
    fun setFromUnit(unit: SpeedUnit) {
        fromUnit = unit
        convert()
    }
    
    private fun convert() {
        val input = inputValue.toDoubleOrNull()
        if (input == null) {
            results = emptyMap()
            return
        }
        
        // Convert to m/s first (base unit)
        val metersPerSecond = when (fromUnit) {
            SpeedUnit.MS -> input
            SpeedUnit.KMH -> input / 3.6
            SpeedUnit.MPH -> input * 0.44704
            SpeedUnit.KNOTS -> input * 0.514444
            SpeedUnit.FPS -> input * 0.3048
        }
        
        // Convert from m/s to all units
        results = mapOf(
            SpeedUnit.MS to formatSpeed(metersPerSecond),
            SpeedUnit.KMH to formatSpeed(metersPerSecond * 3.6),
            SpeedUnit.MPH to formatSpeed(metersPerSecond / 0.44704),
            SpeedUnit.KNOTS to formatSpeed(metersPerSecond / 0.514444),
            SpeedUnit.FPS to formatSpeed(metersPerSecond / 0.3048)
        )
    }
    
    private fun formatSpeed(value: Double): String {
        return if (value >= 1000) {
            String.format("%.1f", value)
        } else {
            String.format("%.3f", value)
        }
    }
    
    fun clear() {
        inputValue = ""
        results = emptyMap()
    }
}

enum class SpeedUnit(val displayName: String, val symbol: String) {
    MS("Meters/Second", "m/s"),
    KMH("Kilometers/Hour", "km/h"),
    MPH("Miles/Hour", "mph"),
    KNOTS("Knots", "kn"),
    FPS("Feet/Second", "ft/s")
}
