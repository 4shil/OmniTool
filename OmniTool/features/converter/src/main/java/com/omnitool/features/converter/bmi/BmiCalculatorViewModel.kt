package com.omnitool.features.converter.bmi

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.pow

/**
 * BMI Calculator ViewModel
 * 
 * Features:
 * - Height & weight input
 * - Metric/Imperial units
 * - BMI category classification
 */
@HiltViewModel
class BmiCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var height by mutableStateOf("")
        private set
    
    var weight by mutableStateOf("")
        private set
    
    var unit by mutableStateOf(BmiUnit.METRIC)
        private set
    
    var bmiValue by mutableDoubleStateOf(0.0)
        private set
    
    var bmiCategory by mutableStateOf(BmiCategory.NORMAL)
        private set
    
    var hasResult by mutableStateOf(false)
        private set
    
    fun setHeight(value: String) {
        height = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setWeight(value: String) {
        weight = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setUnit(newUnit: BmiUnit) {
        unit = newUnit
        calculate()
    }
    
    private fun calculate() {
        val h = height.toDoubleOrNull() ?: 0.0
        val w = weight.toDoubleOrNull() ?: 0.0
        
        if (h <= 0 || w <= 0) {
            hasResult = false
            bmiValue = 0.0
            return
        }
        
        // Calculate BMI
        bmiValue = when (unit) {
            BmiUnit.METRIC -> {
                // Height in cm, weight in kg
                val heightM = h / 100
                w / heightM.pow(2)
            }
            BmiUnit.IMPERIAL -> {
                // Height in inches, weight in lbs
                (w / h.pow(2)) * 703
            }
        }
        
        // Determine category
        bmiCategory = when {
            bmiValue < 18.5 -> BmiCategory.UNDERWEIGHT
            bmiValue < 25 -> BmiCategory.NORMAL
            bmiValue < 30 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }
        
        hasResult = true
    }
    
    fun formatBmi(): String = String.format("%.1f", bmiValue)
    
    fun clear() {
        height = ""
        weight = ""
        bmiValue = 0.0
        hasResult = false
    }
}

enum class BmiUnit(val displayName: String, val heightLabel: String, val weightLabel: String) {
    METRIC("Metric", "cm", "kg"),
    IMPERIAL("Imperial", "in", "lbs")
}

enum class BmiCategory(val displayName: String, val range: String) {
    UNDERWEIGHT("Underweight", "< 18.5"),
    NORMAL("Normal", "18.5 - 24.9"),
    OVERWEIGHT("Overweight", "25 - 29.9"),
    OBESE("Obese", "≥ 30")
}
