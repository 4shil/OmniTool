package com.omnitool.features.converter.unit

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Unit Converter Tool ViewModel
 * Supports Length, Weight, Volume, Area, Temperature
 */
@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {
    
    var inputValue by mutableStateOf("")
        private set
    
    var outputValue by mutableStateOf("")
        private set
    
    var selectedCategory by mutableStateOf(UnitCategory.LENGTH)
        private set
    
    var fromUnit by mutableStateOf<UnitType?>(null)
        private set
    
    var toUnit by mutableStateOf<UnitType?>(null)
        private set
    
    init {
        fromUnit = UnitCategory.LENGTH.units.first()
        toUnit = UnitCategory.LENGTH.units[1]
    }
    
    fun updateInput(value: String) {
        inputValue = value
        convert()
    }
    
    fun setCategory(category: UnitCategory) {
        selectedCategory = category
        fromUnit = category.units.first()
        toUnit = category.units[1]
        convert()
    }
    
    fun setFromUnit(unit: UnitType) {
        fromUnit = unit
        convert()
    }
    
    fun setToUnit(unit: UnitType) {
        toUnit = unit
        convert()
    }
    
    fun swapUnits() {
        val temp = fromUnit
        fromUnit = toUnit
        toUnit = temp
        convert()
    }
    
    private fun convert() {
        val input = inputValue.toDoubleOrNull()
        if (input == null || fromUnit == null || toUnit == null) {
            outputValue = ""
            return
        }
        
        // Convert to base unit, then to target unit
        val baseValue = input * fromUnit!!.toBase
        val result = baseValue / toUnit!!.toBase
        
        outputValue = formatResult(result)
    }
    
    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }
    
    fun clearAll() {
        inputValue = ""
        outputValue = ""
    }
}

enum class UnitCategory(val displayName: String, val units: List<UnitType>) {
    LENGTH("Length", listOf(
        UnitType("Meter", "m", 1.0),
        UnitType("Kilometer", "km", 1000.0),
        UnitType("Centimeter", "cm", 0.01),
        UnitType("Millimeter", "mm", 0.001),
        UnitType("Mile", "mi", 1609.344),
        UnitType("Yard", "yd", 0.9144),
        UnitType("Foot", "ft", 0.3048),
        UnitType("Inch", "in", 0.0254)
    )),
    WEIGHT("Weight", listOf(
        UnitType("Kilogram", "kg", 1.0),
        UnitType("Gram", "g", 0.001),
        UnitType("Milligram", "mg", 0.000001),
        UnitType("Pound", "lb", 0.453592),
        UnitType("Ounce", "oz", 0.0283495),
        UnitType("Ton", "t", 1000.0)
    )),
    VOLUME("Volume", listOf(
        UnitType("Liter", "L", 1.0),
        UnitType("Milliliter", "mL", 0.001),
        UnitType("Gallon (US)", "gal", 3.78541),
        UnitType("Quart", "qt", 0.946353),
        UnitType("Pint", "pt", 0.473176),
        UnitType("Cup", "cup", 0.236588),
        UnitType("Fluid Oz", "fl oz", 0.0295735)
    )),
    AREA("Area", listOf(
        UnitType("Square Meter", "m²", 1.0),
        UnitType("Square Km", "km²", 1000000.0),
        UnitType("Hectare", "ha", 10000.0),
        UnitType("Acre", "ac", 4046.86),
        UnitType("Square Foot", "ft²", 0.092903),
        UnitType("Square Inch", "in²", 0.00064516)
    )),
    TEMPERATURE("Temperature", listOf(
        UnitType("Celsius", "°C", 1.0),
        UnitType("Fahrenheit", "°F", 1.0),
        UnitType("Kelvin", "K", 1.0)
    ))
}

data class UnitType(
    val name: String,
    val symbol: String,
    val toBase: Double
)
