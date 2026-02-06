package com.omnitool.features.utilities.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Compass ViewModel
 * 
 * Features:
 * - Real-time compass heading
 * - Cardinal direction display
 * - Sensor accuracy indicator
 */
@HiltViewModel
class CompassViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener {
    
    var heading by mutableFloatStateOf(0f)
        private set
    
    var cardinalDirection by mutableStateOf("N")
        private set
    
    var isActive by mutableStateOf(false)
        private set
    
    var hasSensor by mutableStateOf(false)
        private set
    
    var accuracy by mutableStateOf(SensorAccuracy.UNKNOWN)
        private set
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    
    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        hasSensor = accelerometer != null && magnetometer != null
    }
    
    fun start() {
        if (!hasSensor) return
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        isActive = true
    }
    
    fun stop() {
        sensorManager.unregisterListener(this)
        isActive = false
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, 3)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
            }
        }
        
        updateOrientation()
    }
    
    override fun onAccuracyChanged(sensor: Sensor, acc: Int) {
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            accuracy = when (acc) {
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                else -> SensorAccuracy.UNKNOWN
            }
        }
    }
    
    private fun updateOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            
            // Convert radians to degrees
            var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            if (azimuth < 0) azimuth += 360f
            
            heading = azimuth
            cardinalDirection = getCardinalDirection(azimuth)
        }
    }
    
    private fun getCardinalDirection(degrees: Float): String {
        val normalized = ((degrees + 360) % 360).roundToInt()
        return when (normalized) {
            in 0..22 -> "N"
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..337 -> "NW"
            else -> "N"
        }
    }
    
    fun getFullDirection(): String {
        return when (cardinalDirection) {
            "N" -> "North"
            "NE" -> "Northeast"
            "E" -> "East"
            "SE" -> "Southeast"
            "S" -> "South"
            "SW" -> "Southwest"
            "W" -> "West"
            "NW" -> "Northwest"
            else -> "Unknown"
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

enum class SensorAccuracy(val displayName: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    UNKNOWN("Unknown")
}
