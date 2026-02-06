package com.omnitool.features.utilities.stopwatch

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stopwatch ViewModel
 * 
 * Features:
 * - Start/Stop/Reset
 * - Lap times
 * - Millisecond precision
 */
@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {
    
    var elapsedMs by mutableLongStateOf(0L)
        private set
    
    var isRunning by mutableStateOf(false)
        private set
    
    var laps by mutableStateOf<List<LapTime>>(emptyList())
        private set
    
    private var timerJob: Job? = null
    private var startTime = 0L
    private var pausedTime = 0L
    
    fun start() {
        if (isRunning) return
        
        isRunning = true
        startTime = System.currentTimeMillis() - pausedTime
        
        timerJob = viewModelScope.launch {
            while (isRunning) {
                elapsedMs = System.currentTimeMillis() - startTime
                delay(10) // Update every 10ms for smooth display
            }
        }
    }
    
    fun stop() {
        isRunning = false
        timerJob?.cancel()
        pausedTime = elapsedMs
    }
    
    fun reset() {
        stop()
        elapsedMs = 0L
        pausedTime = 0L
        laps = emptyList()
    }
    
    fun lap() {
        if (!isRunning) return
        
        val lapNumber = laps.size + 1
        val currentTime = elapsedMs
        val lapTime = if (laps.isEmpty()) currentTime else currentTime - laps.last().totalTime
        
        laps = laps + LapTime(
            number = lapNumber,
            lapTime = lapTime,
            totalTime = currentTime
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class LapTime(
    val number: Int,
    val lapTime: Long,
    val totalTime: Long
)

// Format milliseconds to display string
fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (this % 1000) / 10
    return "%02d:%02d.%02d".format(minutes, seconds, millis)
}

fun Long.formatTimeFull(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val millis = (this % 1000) / 10
    
    return if (hours > 0) {
        "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, millis)
    } else {
        "%02d:%02d.%02d".format(minutes, seconds, millis)
    }
}
