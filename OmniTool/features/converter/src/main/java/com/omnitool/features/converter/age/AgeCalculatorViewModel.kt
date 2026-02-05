package com.omnitool.features.converter.age

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Age Calculator Tool ViewModel
 */
@HiltViewModel
class AgeCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var birthDate by mutableStateOf<Calendar?>(null)
        private set
    
    var targetDate by mutableStateOf(Calendar.getInstance())
        private set
    
    var ageYears by mutableStateOf(0)
        private set
    
    var ageMonths by mutableStateOf(0)
        private set
    
    var ageDays by mutableStateOf(0)
        private set
    
    var totalDays by mutableStateOf(0L)
        private set
    
    var totalWeeks by mutableStateOf(0L)
        private set
    
    var totalMonths by mutableStateOf(0L)
        private set
    
    var nextBirthday by mutableStateOf("")
        private set
    
    var daysUntilBirthday by mutableStateOf(0L)
        private set
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    fun setBirthDate(year: Int, month: Int, day: Int) {
        birthDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        calculate()
    }
    
    fun setTargetDate(year: Int, month: Int, day: Int) {
        targetDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        calculate()
    }
    
    fun useToday() {
        targetDate = Calendar.getInstance()
        calculate()
    }
    
    fun getFormattedBirthDate(): String = birthDate?.let { dateFormat.format(it.time) } ?: "Select date"
    fun getFormattedTargetDate(): String = dateFormat.format(targetDate.time)
    
    private fun calculate() {
        val birth = birthDate ?: return
        
        // Calculate age
        var years = targetDate.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = targetDate.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        var days = targetDate.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)
        
        if (days < 0) {
            months--
            val lastMonth = (targetDate.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
            }
            days += lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        
        if (months < 0) {
            years--
            months += 12
        }
        
        ageYears = years
        ageMonths = months
        ageDays = days
        
        // Total calculations
        val diffMs = targetDate.timeInMillis - birth.timeInMillis
        totalDays = diffMs / (1000 * 60 * 60 * 24)
        totalWeeks = totalDays / 7
        totalMonths = (years * 12 + months).toLong()
        
        // Next birthday
        calculateNextBirthday(birth)
    }
    
    private fun calculateNextBirthday(birth: Calendar) {
        val today = Calendar.getInstance()
        val nextBday = Calendar.getInstance().apply {
            set(Calendar.MONTH, birth.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, birth.get(Calendar.DAY_OF_MONTH))
        }
        
        if (nextBday.before(today)) {
            nextBday.add(Calendar.YEAR, 1)
        }
        
        nextBirthday = dateFormat.format(nextBday.time)
        daysUntilBirthday = (nextBday.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)
    }
}
