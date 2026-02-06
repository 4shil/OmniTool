package com.omnitool.features.utilities.tip

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Tip Calculator ViewModel
 * 
 * Features:
 * - Bill amount input
 * - Tip percentage slider
 * - Split bill option
 * - Round up option
 */
@HiltViewModel
class TipCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var billAmount by mutableStateOf("")
        private set
    
    var tipPercent by mutableIntStateOf(15)
        private set
    
    var splitCount by mutableIntStateOf(1)
        private set
    
    var roundUp by mutableStateOf(false)
        private set
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    
    // Calculated values
    val tipAmount: Double
        get() {
            val bill = billAmount.toDoubleOrNull() ?: 0.0
            return bill * tipPercent / 100.0
        }
    
    val totalAmount: Double
        get() {
            val bill = billAmount.toDoubleOrNull() ?: 0.0
            var total = bill + tipAmount
            if (roundUp) {
                total = ceil(total)
            }
            return total
        }
    
    val perPersonAmount: Double
        get() {
            var amount = totalAmount / splitCount
            if (roundUp) {
                amount = ceil(amount * 100) / 100 // Round to nearest cent
            }
            return amount
        }
    
    val formattedTip: String
        get() = formatCurrency(tipAmount)
    
    val formattedTotal: String
        get() = formatCurrency(totalAmount)
    
    val formattedPerPerson: String
        get() = formatCurrency(perPersonAmount)
    
    fun updateBillAmount(amount: String) {
        // Only allow valid currency input
        val filtered = amount.filter { it.isDigit() || it == '.' }
        // Only allow one decimal point and max 2 decimal places
        val parts = filtered.split(".")
        billAmount = if (parts.size > 2) {
            parts[0] + "." + parts[1]
        } else if (parts.size == 2 && parts[1].length > 2) {
            parts[0] + "." + parts[1].take(2)
        } else {
            filtered
        }
    }
    
    fun setTipPercent(percent: Int) {
        tipPercent = percent.coerceIn(0, 50)
    }
    
    fun setSplitCount(count: Int) {
        splitCount = count.coerceIn(1, 20)
    }
    
    fun toggleRoundUp() {
        roundUp = !roundUp
    }
    
    fun selectPresetTip(percent: Int) {
        tipPercent = percent
    }
    
    fun clear() {
        billAmount = ""
        tipPercent = 15
        splitCount = 1
        roundUp = false
    }
    
    private fun formatCurrency(amount: Double): String {
        return try {
            currencyFormatter.format(amount)
        } catch (e: Exception) {
            "$${String.format("%.2f", amount)}"
        }
    }
}
