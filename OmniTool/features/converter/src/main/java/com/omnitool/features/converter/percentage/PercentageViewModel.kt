package com.omnitool.features.converter.percentage

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Percentage Calculator ViewModel
 * 
 * Features:
 * - What is X% of Y
 * - X is what % of Y
 * - Percentage increase/decrease
 */
@HiltViewModel
class PercentageViewModel @Inject constructor() : ViewModel() {
    
    var mode by mutableStateOf(PercentageMode.WHAT_IS_X_OF_Y)
        private set
    
    var valueA by mutableStateOf("")
        private set
    
    var valueB by mutableStateOf("")
        private set
    
    var result by mutableStateOf("")
        private set
    
    fun setMode(newMode: PercentageMode) {
        mode = newMode
        valueA = ""
        valueB = ""
        result = ""
    }
    
    fun setValueA(value: String) {
        valueA = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setValueB(value: String) {
        valueB = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    private fun calculate() {
        val a = valueA.toDoubleOrNull()
        val b = valueB.toDoubleOrNull()
        
        if (a == null || b == null) {
            result = ""
            return
        }
        
        result = when (mode) {
            PercentageMode.WHAT_IS_X_OF_Y -> {
                // What is X% of Y -> (X/100) * Y
                formatResult((a / 100) * b)
            }
            PercentageMode.X_IS_WHAT_OF_Y -> {
                // X is what % of Y -> (X/Y) * 100
                if (b == 0.0) "∞" else formatResult((a / b) * 100) + "%"
            }
            PercentageMode.INCREASE -> {
                // From X to Y, what is % increase -> ((Y-X)/X) * 100
                if (a == 0.0) "∞" else formatResult(((b - a) / a) * 100) + "%"
            }
            PercentageMode.DECREASE -> {
                // From X to Y, what is % decrease -> ((X-Y)/X) * 100
                if (a == 0.0) "∞" else formatResult(((a - b) / a) * 100) + "%"
            }
        }
    }
    
    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.2f", value)
        }
    }
    
    fun clear() {
        valueA = ""
        valueB = ""
        result = ""
    }
}

enum class PercentageMode(val displayName: String, val formula: String, val labelA: String, val labelB: String) {
    WHAT_IS_X_OF_Y("X% of Y", "What is X% of Y?", "Percentage (%)", "Number"),
    X_IS_WHAT_OF_Y("X is % of Y", "X is what % of Y?", "Number", "Of"),
    INCREASE("% Increase", "% change from X to Y", "From", "To"),
    DECREASE("% Decrease", "% decrease from X to Y", "From", "To")
}
