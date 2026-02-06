package com.omnitool.features.utilities.speedometer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Speedometer ViewModel
 * 
 * Features:
 * - GPS-based speed measurement
 * - Speed unit toggle (km/h, mph)
 * - Max speed tracking
 */
@HiltViewModel
class SpeedometerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), LocationListener {
    
    var currentSpeed by mutableFloatStateOf(0f)
        private set
    
    var maxSpeed by mutableFloatStateOf(0f)
        private set
    
    var avgSpeed by mutableFloatStateOf(0f)
        private set
    
    var unit by mutableStateOf(SpeedUnit.KMH)
        private set
    
    var isActive by mutableStateOf(false)
        private set
    
    var hasPermission by mutableStateOf(false)
        private set
    
    var hasGps by mutableStateOf(false)
        private set
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val speedHistory = mutableListOf<Float>()
    
    init {
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    
    fun checkPermission() {
        hasPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun start() {
        if (!hasPermission) return
        
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500L,  // Min time between updates (ms)
                1f,    // Min distance (meters)
                this
            )
            isActive = true
        } catch (e: SecurityException) {
            hasPermission = false
        }
    }
    
    fun stop() {
        locationManager.removeUpdates(this)
        isActive = false
    }
    
    fun toggleUnit() {
        unit = if (unit == SpeedUnit.KMH) SpeedUnit.MPH else SpeedUnit.KMH
    }
    
    fun resetStats() {
        maxSpeed = 0f
        avgSpeed = 0f
        speedHistory.clear()
    }
    
    override fun onLocationChanged(location: Location) {
        if (location.hasSpeed()) {
            // Speed from GPS is in m/s
            val speedMs = location.speed
            currentSpeed = when (unit) {
                SpeedUnit.KMH -> speedMs * 3.6f
                SpeedUnit.MPH -> speedMs * 2.237f
            }
            
            // Track max
            if (currentSpeed > maxSpeed) {
                maxSpeed = currentSpeed
            }
            
            // Calculate average
            speedHistory.add(currentSpeed)
            avgSpeed = speedHistory.average().toFloat()
        }
    }
    
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {
        hasGps = true
    }
    override fun onProviderDisabled(provider: String) {
        hasGps = false
    }
    
    fun formatSpeed(value: Float): String {
        return String.format("%.1f", value)
    }
    
    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

enum class SpeedUnit(val displayName: String, val symbol: String) {
    KMH("Kilometers", "km/h"),
    MPH("Miles", "mph")
}
