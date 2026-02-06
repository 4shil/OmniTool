package com.omnitool.features.utilities.timer

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Timer/Countdown ViewModel
 * 
 * Features:
 * - Customizable duration
 * - Start/Pause/Reset
 * - Alarm when complete
 */
@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {
    
    var hours by mutableIntStateOf(0)
        private set
    
    var minutes by mutableIntStateOf(5)
        private set
    
    var seconds by mutableIntStateOf(0)
        private set
    
    var remainingMs by mutableLongStateOf(0L)
        private set
    
    var totalMs by mutableLongStateOf(0L)
        private set
    
    var isRunning by mutableStateOf(false)
        private set
    
    var isComplete by mutableStateOf(false)
        private set
    
    private var timerJob: Job? = null
    private var endTime = 0L
    
    fun setHours(value: Int) {
        hours = value.coerceIn(0, 23)
    }
    
    fun setMinutes(value: Int) {
        minutes = value.coerceIn(0, 59)
    }
    
    fun setSeconds(value: Int) {
        seconds = value.coerceIn(0, 59)
    }
    
    fun start() {
        if (isRunning) return
        
        // If not paused, calculate total from input
        if (remainingMs == 0L) {
            totalMs = (hours * 3600L + minutes * 60L + seconds) * 1000L
            remainingMs = totalMs
        }
        
        if (remainingMs <= 0L) return
        
        isRunning = true
        isComplete = false
        endTime = System.currentTimeMillis() + remainingMs
        
        timerJob = viewModelScope.launch {
            while (isRunning && remainingMs > 0) {
                remainingMs = (endTime - System.currentTimeMillis()).coerceAtLeast(0)
                
                if (remainingMs == 0L) {
                    isRunning = false
                    isComplete = true
                }
                
                delay(100) // Update every 100ms
            }
        }
    }
    
    fun pause() {
        isRunning = false
        timerJob?.cancel()
    }
    
    fun reset() {
        pause()
        remainingMs = 0L
        totalMs = 0L
        isComplete = false
    }
    
    fun dismissComplete() {
        isComplete = false
    }
    
    fun addMinute() {
        if (isRunning) {
            remainingMs += 60_000L
            endTime += 60_000L
        } else {
            minutes = (minutes + 1).coerceAtMost(59)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

// Format milliseconds to display string
fun Long.formatCountdown(): String {
    val totalSeconds = (this + 999) / 1000 // Round up
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    
    return if (h > 0) {
        "%02d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}
