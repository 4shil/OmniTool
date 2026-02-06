package com.omnitool.features.utilities.calculator

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * Calculator ViewModel
 * 
 * Standard calculator with:
 * - Basic operations (+, -, ×, ÷)
 * - Percentage
 * - Memory functions
 * - Clear/All Clear
 */
@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {
    
    var displayValue by mutableStateOf("0")
        private set
    
    var expression by mutableStateOf("")
        private set
    
    var memory by mutableDoubleStateOf(0.0)
        private set
    
    var hasMemory by mutableStateOf(false)
        private set
    
    private var currentNumber = ""
    private var previousNumber = ""
    private var operation: Operation? = null
    private var hasResult = false
    
    private val formatter = DecimalFormat("#,##0.########")
    
    fun onDigit(digit: String) {
        if (hasResult) {
            currentNumber = digit
            displayValue = digit
            expression = ""
            hasResult = false
        } else {
            if (currentNumber == "0" && digit == "0") return
            if (currentNumber == "0" && digit != ".") {
                currentNumber = digit
            } else {
                if (digit == "." && currentNumber.contains(".")) return
                currentNumber += digit
            }
            displayValue = formatNumber(currentNumber)
        }
    }
    
    fun onOperation(op: Operation) {
        if (currentNumber.isNotEmpty()) {
            if (operation != null && previousNumber.isNotEmpty()) {
                calculate()
            }
            previousNumber = currentNumber
            currentNumber = ""
            operation = op
            expression = "${formatNumber(previousNumber)} ${op.symbol}"
            hasResult = false
        } else if (previousNumber.isNotEmpty()) {
            operation = op
            expression = "${formatNumber(previousNumber)} ${op.symbol}"
        }
    }
    
    fun onEquals() {
        if (operation != null && previousNumber.isNotEmpty() && currentNumber.isNotEmpty()) {
            calculate()
        }
    }
    
    private fun calculate() {
        try {
            val num1 = previousNumber.toDouble()
            val num2 = currentNumber.toDouble()
            
            val result = when (operation) {
                Operation.ADD -> num1 + num2
                Operation.SUBTRACT -> num1 - num2
                Operation.MULTIPLY -> num1 * num2
                Operation.DIVIDE -> if (num2 != 0.0) num1 / num2 else Double.NaN
                null -> return
            }
            
            if (result.isNaN() || result.isInfinite()) {
                displayValue = "Error"
                expression = ""
                currentNumber = ""
                previousNumber = ""
                operation = null
                hasResult = true
                return
            }
            
            expression = "${formatNumber(previousNumber)} ${operation!!.symbol} ${formatNumber(currentNumber)} ="
            currentNumber = formatResultNumber(result)
            displayValue = formatNumber(currentNumber)
            previousNumber = ""
            operation = null
            hasResult = true
        } catch (e: Exception) {
            displayValue = "Error"
            clear()
        }
    }
    
    fun onPercent() {
        if (currentNumber.isNotEmpty()) {
            val num = currentNumber.toDoubleOrNull() ?: return
            currentNumber = formatResultNumber(num / 100)
            displayValue = formatNumber(currentNumber)
        }
    }
    
    fun onPlusMinus() {
        if (currentNumber.isNotEmpty() && currentNumber != "0") {
            currentNumber = if (currentNumber.startsWith("-")) {
                currentNumber.substring(1)
            } else {
                "-$currentNumber"
            }
            displayValue = formatNumber(currentNumber)
        }
    }
    
    fun clear() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = ""
            displayValue = "0"
        } else {
            allClear()
        }
    }
    
    fun allClear() {
        currentNumber = ""
        previousNumber = ""
        operation = null
        displayValue = "0"
        expression = ""
        hasResult = false
    }
    
    fun backspace() {
        if (currentNumber.isNotEmpty() && !hasResult) {
            currentNumber = currentNumber.dropLast(1)
            displayValue = if (currentNumber.isEmpty()) "0" else formatNumber(currentNumber)
        }
    }
    
    // Memory functions
    fun memoryClear() {
        memory = 0.0
        hasMemory = false
    }
    
    fun memoryRecall() {
        if (hasMemory) {
            currentNumber = formatResultNumber(memory)
            displayValue = formatNumber(currentNumber)
        }
    }
    
    fun memoryAdd() {
        val num = currentNumber.toDoubleOrNull() ?: return
        memory += num
        hasMemory = true
    }
    
    fun memorySubtract() {
        val num = currentNumber.toDoubleOrNull() ?: return
        memory -= num
        hasMemory = true
    }
    
    private fun formatNumber(value: String): String {
        return try {
            val num = value.toDouble()
            if (value.endsWith(".")) {
                formatter.format(num) + "."
            } else {
                formatter.format(num)
            }
        } catch (e: Exception) {
            value
        }
    }
    
    private fun formatResultNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}

enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("−"),
    MULTIPLY("×"),
    DIVIDE("÷")
}
