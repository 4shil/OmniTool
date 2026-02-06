package com.omnitool.features.converter.loan

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.pow

/**
 * Loan Calculator ViewModel
 * 
 * Features:
 * - EMI calculation
 * - Total interest calculation
 * - Amortization breakdown
 */
@HiltViewModel
class LoanCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var principal by mutableStateOf("")
        private set
    
    var interestRate by mutableStateOf("")
        private set
    
    var tenure by mutableStateOf("")
        private set
    
    var tenureType by mutableStateOf(TenureType.YEARS)
        private set
    
    var emi by mutableDoubleStateOf(0.0)
        private set
    
    var totalInterest by mutableDoubleStateOf(0.0)
        private set
    
    var totalPayment by mutableDoubleStateOf(0.0)
        private set
    
    var hasResult by mutableStateOf(false)
        private set
    
    fun setPrincipal(value: String) {
        principal = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setInterestRate(value: String) {
        interestRate = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setTenure(value: String) {
        tenure = value.filter { it.isDigit() }
        calculate()
    }
    
    fun setTenureType(type: TenureType) {
        tenureType = type
        calculate()
    }
    
    private fun calculate() {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = (interestRate.toDoubleOrNull() ?: 0.0) / 100 / 12 // Monthly rate
        val n = (tenure.toIntOrNull() ?: 0) * (if (tenureType == TenureType.YEARS) 12 else 1)
        
        if (p <= 0 || r <= 0 || n <= 0) {
            hasResult = false
            emi = 0.0
            totalInterest = 0.0
            totalPayment = 0.0
            return
        }
        
        // EMI = P × r × (1 + r)^n / ((1 + r)^n - 1)
        val onePlusRPowN = (1 + r).pow(n.toDouble())
        emi = p * r * onePlusRPowN / (onePlusRPowN - 1)
        totalPayment = emi * n
        totalInterest = totalPayment - p
        hasResult = true
    }
    
    fun formatCurrency(value: Double): String {
        return "₹${String.format("%,.2f", value)}"
    }
    
    fun getInterestPercentage(): Float {
        return if (totalPayment > 0) {
            (totalInterest / totalPayment * 100).toFloat()
        } else 0f
    }
    
    fun clear() {
        principal = ""
        interestRate = ""
        tenure = ""
        emi = 0.0
        totalInterest = 0.0
        totalPayment = 0.0
        hasResult = false
    }
}

enum class TenureType(val displayName: String) {
    YEARS("Years"),
    MONTHS("Months")
}
