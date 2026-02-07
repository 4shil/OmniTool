package com.omnitool.features.utilities.countdown

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Countdown Timer ViewModel
 * 
 * Features:
 * - Set target date/time
 * - Show remaining time
 * - Presets for common events
 */
@HiltViewModel
class CountdownTimerViewModel @Inject constructor() : ViewModel() {
    
    var targetDate by mutableStateOf(LocalDate.now().plusDays(1))
        private set
    
    var targetTime by mutableStateOf(LocalTime.NOON)
        private set
    
    var eventName by mutableStateOf("")
        private set
    
    var daysRemaining by mutableLongStateOf(0L)
        private set
    
    var hoursRemaining by mutableLongStateOf(0L)
        private set
    
    var minutesRemaining by mutableLongStateOf(0L)
        private set
    
    var secondsRemaining by mutableLongStateOf(0L)
        private set
    
    var isExpired by mutableStateOf(false)
        private set
    
    var savedCountdowns by mutableStateOf<List<SavedCountdown>>(emptyList())
        private set
    
    private var countdownJob: Job? = null
    
    init {
        startCountdown()
    }
    
    fun setTargetDate(date: LocalDate) {
        targetDate = date
        startCountdown()
    }
    
    fun setTargetTime(time: LocalTime) {
        targetTime = time
        startCountdown()
    }
    
    fun setEventName(name: String) {
        eventName = name
    }
    
    fun saveCountdown() {
        if (eventName.isNotBlank()) {
            val countdown = SavedCountdown(
                name = eventName,
                targetDateTime = LocalDateTime.of(targetDate, targetTime)
            )
            savedCountdowns = savedCountdowns + countdown
        }
    }
    
    fun loadCountdown(countdown: SavedCountdown) {
        targetDate = countdown.targetDateTime.toLocalDate()
        targetTime = countdown.targetDateTime.toLocalTime()
        eventName = countdown.name
        startCountdown()
    }
    
    fun removeCountdown(countdown: SavedCountdown) {
        savedCountdowns = savedCountdowns - countdown
    }
    
    fun setPreset(preset: CountdownPreset) {
        when (preset) {
            CountdownPreset.NEW_YEAR -> {
                targetDate = LocalDate.of(LocalDate.now().year + 1, 1, 1)
                targetTime = LocalTime.MIDNIGHT
                eventName = "New Year"
            }
            CountdownPreset.TOMORROW -> {
                targetDate = LocalDate.now().plusDays(1)
                targetTime = LocalTime.MIDNIGHT
                eventName = "Tomorrow"
            }
            CountdownPreset.NEXT_WEEK -> {
                targetDate = LocalDate.now().plusWeeks(1)
                targetTime = LocalTime.now()
                eventName = "Next Week"
            }
            CountdownPreset.NEXT_MONTH -> {
                targetDate = LocalDate.now().plusMonths(1)
                targetTime = LocalTime.now()
                eventName = "Next Month"
            }
        }
        startCountdown()
    }
    
    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                updateRemaining()
                delay(1000)
            }
        }
    }
    
    private fun updateRemaining() {
        val now = LocalDateTime.now()
        val target = LocalDateTime.of(targetDate, targetTime)
        
        if (now.isAfter(target)) {
            isExpired = true
            daysRemaining = 0
            hoursRemaining = 0
            minutesRemaining = 0
            secondsRemaining = 0
            return
        }
        
        isExpired = false
        
        var tempDateTime = now
        
        daysRemaining = ChronoUnit.DAYS.between(tempDateTime, target)
        tempDateTime = tempDateTime.plusDays(daysRemaining)
        
        hoursRemaining = ChronoUnit.HOURS.between(tempDateTime, target)
        tempDateTime = tempDateTime.plusHours(hoursRemaining)
        
        minutesRemaining = ChronoUnit.MINUTES.between(tempDateTime, target)
        tempDateTime = tempDateTime.plusMinutes(minutesRemaining)
        
        secondsRemaining = ChronoUnit.SECONDS.between(tempDateTime, target)
    }
    
    fun clear() {
        targetDate = LocalDate.now().plusDays(1)
        targetTime = LocalTime.NOON
        eventName = ""
        startCountdown()
    }
    
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

data class SavedCountdown(
    val name: String,
    val targetDateTime: LocalDateTime
)

enum class CountdownPreset(val displayName: String) {
    NEW_YEAR("New Year"),
    TOMORROW("Tomorrow"),
    NEXT_WEEK("Next Week"),
    NEXT_MONTH("Next Month")
}
