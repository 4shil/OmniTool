package com.omnitool.features.utilities.worldclock

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * World Clock ViewModel
 * 
 * Features:
 * - Multiple time zones
 * - Live time updates
 * - Add/remove cities
 */
@HiltViewModel
class WorldClockViewModel @Inject constructor() : ViewModel() {
    
    var selectedClocks by mutableStateOf<List<ClockEntry>>(emptyList())
        private set
    
    var availableZones by mutableStateOf<List<TimeZoneInfo>>(emptyList())
        private set
    
    var searchQuery by mutableStateOf("")
        private set
    
    var currentLocalTime by mutableStateOf("")
        private set
    
    var is24Hour by mutableStateOf(false)
        private set
    
    private val timeFormatter12 = DateTimeFormatter.ofPattern("hh:mm:ss a")
    private val timeFormatter24 = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    
    init {
        // Initialize with popular cities
        val defaultZones = listOf(
            "America/New_York" to "New York",
            "Europe/London" to "London",
            "Asia/Tokyo" to "Tokyo",
            "Asia/Kolkata" to "New Delhi"
        )
        
        selectedClocks = defaultZones.mapNotNull { (zoneId, city) ->
            try {
                ClockEntry(
                    zoneId = ZoneId.of(zoneId),
                    cityName = city
                )
            } catch (e: Exception) { null }
        }
        
        // Build available zones list
        buildAvailableZones()
        
        // Start time updates
        startTimeUpdates()
    }
    
    private fun buildAvailableZones() {
        val popular = mapOf(
            "America/New_York" to "New York, USA",
            "America/Los_Angeles" to "Los Angeles, USA",
            "America/Chicago" to "Chicago, USA",
            "Europe/London" to "London, UK",
            "Europe/Paris" to "Paris, France",
            "Europe/Berlin" to "Berlin, Germany",
            "Asia/Tokyo" to "Tokyo, Japan",
            "Asia/Shanghai" to "Shanghai, China",
            "Asia/Kolkata" to "New Delhi, India",
            "Asia/Dubai" to "Dubai, UAE",
            "Asia/Singapore" to "Singapore",
            "Australia/Sydney" to "Sydney, Australia",
            "Pacific/Auckland" to "Auckland, NZ",
            "America/Sao_Paulo" to "São Paulo, Brazil"
        )
        
        availableZones = popular.map { (zoneId, name) ->
            val zone = ZoneId.of(zoneId)
            val offset = ZonedDateTime.now(zone).offset
            TimeZoneInfo(
                zoneId = zone,
                displayName = name,
                offsetString = offset.toString()
            )
        }.sortedBy { it.displayName }
    }
    
    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (isActive) {
                updateTimes()
                delay(1000)
            }
        }
    }
    
    private fun updateTimes() {
        val formatter = if (is24Hour) timeFormatter24 else timeFormatter12
        currentLocalTime = ZonedDateTime.now().format(formatter)
        
        selectedClocks = selectedClocks.map { clock ->
            val now = ZonedDateTime.now(clock.zoneId)
            clock.copy(
                currentTime = now.format(formatter),
                currentDate = now.format(dateFormatter),
                offsetHours = getOffsetFromLocal(clock.zoneId)
            )
        }
    }
    
    private fun getOffsetFromLocal(zoneId: ZoneId): String {
        val local = ZonedDateTime.now()
        val remote = ZonedDateTime.now(zoneId)
        val diffSeconds = remote.offset.totalSeconds - local.offset.totalSeconds
        val diffHours = diffSeconds / 3600.0
        
        return when {
            diffHours > 0 -> "+${formatHours(diffHours)}"
            diffHours < 0 -> formatHours(diffHours)
            else -> "Same time"
        }
    }
    
    private fun formatHours(hours: Double): String {
        return if (hours == hours.toLong().toDouble()) {
            "${hours.toLong()}h"
        } else {
            "${hours}h"
        }
    }
    
    fun addClock(zoneInfo: TimeZoneInfo) {
        if (selectedClocks.none { it.zoneId == zoneInfo.zoneId }) {
            selectedClocks = selectedClocks + ClockEntry(
                zoneId = zoneInfo.zoneId,
                cityName = zoneInfo.displayName.substringBefore(",")
            )
            updateTimes()
        }
    }
    
    fun removeClock(clock: ClockEntry) {
        selectedClocks = selectedClocks.filter { it.zoneId != clock.zoneId }
    }
    
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    
    fun toggle24Hour() {
        is24Hour = !is24Hour
        updateTimes()
    }
    
    fun getFilteredZones(): List<TimeZoneInfo> {
        return if (searchQuery.isBlank()) {
            availableZones
        } else {
            availableZones.filter { 
                it.displayName.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
}

data class ClockEntry(
    val zoneId: ZoneId,
    val cityName: String,
    val currentTime: String = "",
    val currentDate: String = "",
    val offsetHours: String = ""
)

data class TimeZoneInfo(
    val zoneId: ZoneId,
    val displayName: String,
    val offsetString: String
)
