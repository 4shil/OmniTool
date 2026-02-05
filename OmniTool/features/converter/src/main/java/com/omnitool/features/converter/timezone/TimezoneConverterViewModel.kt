package com.omnitool.features.converter.timezone

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Timezone Converter Tool ViewModel
 */
@HiltViewModel
class TimezoneConverterViewModel @Inject constructor() : ViewModel() {
    
    var fromTimezone by mutableStateOf(TimeZone.getDefault())
        private set
    
    var toTimezone by mutableStateOf(TimeZone.getTimeZone("UTC"))
        private set
    
    var selectedHour by mutableStateOf(12)
        private set
    
    var selectedMinute by mutableStateOf(0)
        private set
    
    var convertedTime by mutableStateOf("")
        private set
    
    var timeDifference by mutableStateOf("")
        private set
    
    val availableTimezones: List<TimezoneInfo> by lazy {
        TimeZone.getAvailableIDs()
            .map { TimeZone.getTimeZone(it) }
            .distinctBy { it.id }
            .sortedBy { it.rawOffset }
            .map { tz ->
                TimezoneInfo(
                    id = tz.id,
                    displayName = tz.getDisplayName(false, TimeZone.SHORT),
                    offset = formatOffset(tz.rawOffset)
                )
            }
    }
    
    init {
        convert()
    }
    
    fun setFromTimezone(tzId: String) {
        fromTimezone = TimeZone.getTimeZone(tzId)
        convert()
    }
    
    fun setToTimezone(tzId: String) {
        toTimezone = TimeZone.getTimeZone(tzId)
        convert()
    }
    
    fun setTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        convert()
    }
    
    fun swapTimezones() {
        val temp = fromTimezone
        fromTimezone = toTimezone
        toTimezone = temp
        convert()
    }
    
    fun useCurrentTime() {
        val cal = Calendar.getInstance()
        selectedHour = cal.get(Calendar.HOUR_OF_DAY)
        selectedMinute = cal.get(Calendar.MINUTE)
        fromTimezone = TimeZone.getDefault()
        convert()
    }
    
    private fun convert() {
        val cal = Calendar.getInstance(fromTimezone)
        cal.set(Calendar.HOUR_OF_DAY, selectedHour)
        cal.set(Calendar.MINUTE, selectedMinute)
        cal.set(Calendar.SECOND, 0)
        
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = toTimezone
        
        convertedTime = sdf.format(cal.time)
        
        // Calculate difference
        val diffMs = toTimezone.rawOffset - fromTimezone.rawOffset
        val diffHours = diffMs / (1000 * 60 * 60)
        val diffMins = Math.abs((diffMs % (1000 * 60 * 60)) / (1000 * 60))
        
        timeDifference = when {
            diffHours == 0 && diffMins == 0 -> "Same timezone"
            diffHours >= 0 -> "+${diffHours}h ${if (diffMins > 0) "${diffMins}m" else ""}"
            else -> "${diffHours}h ${if (diffMins > 0) "${diffMins}m" else ""}"
        }
    }
    
    private fun formatOffset(offsetMs: Int): String {
        val hours = offsetMs / (1000 * 60 * 60)
        val mins = Math.abs((offsetMs % (1000 * 60 * 60)) / (1000 * 60))
        return if (hours >= 0) {
            "UTC+${hours}${if (mins > 0) ":${mins.toString().padStart(2, '0')}" else ""}"
        } else {
            "UTC${hours}${if (mins > 0) ":${mins.toString().padStart(2, '0')}" else ""}"
        }
    }
}

data class TimezoneInfo(
    val id: String,
    val displayName: String,
    val offset: String
)
