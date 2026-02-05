package com.omnitool.features.converter.date

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Date Calculator Tool ViewModel
 * Calculate difference between dates or add/subtract from a date
 */
@HiltViewModel
class DateCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var startDate by mutableStateOf(Calendar.getInstance())
        private set
    
    var endDate by mutableStateOf(Calendar.getInstance())
        private set
    
    var calculationMode by mutableStateOf(DateCalcMode.DIFFERENCE)
        private set
    
    var daysToAdd by mutableStateOf("")
        private set
    
    var result by mutableStateOf("")
        private set
    
    var detailedResult by mutableStateOf("")
        private set
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    init {
        calculate()
    }
    
    fun setStartDate(year: Int, month: Int, day: Int) {
        startDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        calculate()
    }
    
    fun setEndDate(year: Int, month: Int, day: Int) {
        endDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        calculate()
    }
    
    fun setDaysToAdd(days: String) {
        daysToAdd = days
        calculate()
    }
    
    fun setMode(mode: DateCalcMode) {
        calculationMode = mode
        calculate()
    }
    
    fun getFormattedStartDate(): String = dateFormat.format(startDate.time)
    fun getFormattedEndDate(): String = dateFormat.format(endDate.time)
    
    private fun calculate() {
        when (calculationMode) {
            DateCalcMode.DIFFERENCE -> calculateDifference()
            DateCalcMode.ADD -> calculateAddDays()
            DateCalcMode.SUBTRACT -> calculateSubtractDays()
        }
    }
    
    private fun calculateDifference() {
        val diffMs = endDate.timeInMillis - startDate.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(diffMs)
        
        val totalDays = Math.abs(days)
        val weeks = totalDays / 7
        val remainingDays = totalDays % 7
        val months = totalDays / 30
        val years = totalDays / 365
        
        result = when {
            days < 0 -> "${Math.abs(days)} days ago"
            days == 0L -> "Same day"
            else -> "$days days"
        }
        
        detailedResult = buildString {
            if (years > 0) append("$years years, ")
            if (months > 0) append("${months % 12} months, ")
            append("$weeks weeks, $remainingDays days")
        }
    }
    
    private fun calculateAddDays() {
        val days = daysToAdd.toLongOrNull() ?: 0
        val resultCal = startDate.clone() as Calendar
        resultCal.add(Calendar.DAY_OF_MONTH, days.toInt())
        
        result = dateFormat.format(resultCal.time)
        detailedResult = "Adding $days days to ${getFormattedStartDate()}"
    }
    
    private fun calculateSubtractDays() {
        val days = daysToAdd.toLongOrNull() ?: 0
        val resultCal = startDate.clone() as Calendar
        resultCal.add(Calendar.DAY_OF_MONTH, -days.toInt())
        
        result = dateFormat.format(resultCal.time)
        detailedResult = "Subtracting $days days from ${getFormattedStartDate()}"
    }
    
    fun useToday() {
        startDate = Calendar.getInstance()
        calculate()
    }
}

enum class DateCalcMode(val displayName: String) {
    DIFFERENCE("Difference"),
    ADD("Add Days"),
    SUBTRACT("Subtract Days")
}
