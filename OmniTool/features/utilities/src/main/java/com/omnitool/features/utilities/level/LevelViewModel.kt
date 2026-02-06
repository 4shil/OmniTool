package com.omnitool.features.utilities.level

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
import kotlin.math.abs

/**
 * Level Tool ViewModel
 * 
 * Features:
 * - Accelerometer-based leveling
 * - Pitch and roll angles
 * - Visual bubble indicator
 */
@HiltViewModel
class LevelViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener {
    
    var pitch by mutableFloatStateOf(0f)
        private set
    
    var roll by mutableFloatStateOf(0f)
        private set
    
    var isLevel by mutableStateOf(false)
        private set
    
    var hasSensor by mutableStateOf(false)
        private set
    
    var isActive by mutableStateOf(false)
        private set
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    
    private val levelThreshold = 2f // degrees
    
    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        hasSensor = accelerometer != null
    }
    
    fun start() {
        if (!hasSensor) return
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        isActive = true
    }
    
    fun stop() {
        sensorManager.unregisterListener(this)
        isActive = false
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            // Calculate pitch (forward/backward tilt) and roll (left/right tilt)
            pitch = Math.toDegrees(
                kotlin.math.atan2(y.toDouble(), z.toDouble())
            ).toFloat()
            
            roll = Math.toDegrees(
                kotlin.math.atan2(x.toDouble(), z.toDouble())
            ).toFloat()
            
            // Check if level
            isLevel = abs(pitch) < levelThreshold && abs(roll) < levelThreshold
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    
    fun formatAngle(angle: Float): String {
        return String.format("%.1f°", angle)
    }
    
    override fun onCleared() {
        super.onCleared()
        stop()
    }
}
