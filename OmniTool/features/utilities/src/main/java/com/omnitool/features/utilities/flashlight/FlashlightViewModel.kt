package com.omnitool.features.utilities.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Flashlight ViewModel
 * 
 * Features:
 * - Toggle flashlight on/off
 * - Screen light mode
 * - SOS mode
 */
@HiltViewModel
class FlashlightViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var isFlashlightOn by mutableStateOf(false)
        private set
    
    var isScreenLightOn by mutableStateOf(false)
        private set
    
    var isSosMode by mutableStateOf(false)
        private set
    
    var hasFlashlight by mutableStateOf(false)
        private set
    
    var screenBrightness by mutableFloatStateOf(1f)
        private set
    
    var screenColor by mutableStateOf(ScreenColor.WHITE)
        private set
    
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null
    
    init {
        // Check if device has flashlight
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            hasFlashlight = cameraId != null
        } catch (e: Exception) {
            hasFlashlight = false
        }
    }
    
    fun toggleFlashlight() {
        try {
            cameraId?.let { id ->
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(id, isFlashlightOn)
                
                if (isFlashlightOn) {
                    isSosMode = false
                }
            }
        } catch (e: Exception) {
            isFlashlightOn = false
        }
    }
    
    fun toggleScreenLight() {
        isScreenLightOn = !isScreenLightOn
    }
    
    fun setScreenBrightness(brightness: Float) {
        screenBrightness = brightness.coerceIn(0.1f, 1f)
    }
    
    fun setScreenColor(color: ScreenColor) {
        screenColor = color
    }
    
    fun toggleSosMode() {
        isSosMode = !isSosMode
        // SOS would be implemented with a coroutine flashing pattern
        // For now, just toggle state
    }
    
    fun turnOffAll() {
        if (isFlashlightOn) {
            try {
                cameraId?.let { id ->
                    cameraManager.setTorchMode(id, false)
                }
            } catch (_: Exception) {}
        }
        isFlashlightOn = false
        isScreenLightOn = false
        isSosMode = false
    }
    
    override fun onCleared() {
        super.onCleared()
        turnOffAll()
    }
}

enum class ScreenColor(val displayName: String, val colorValue: Long) {
    WHITE("White", 0xFFFFFFFF),
    WARM("Warm", 0xFFFFF4E0),
    RED("Red", 0xFFFF0000),
    GREEN("Green", 0xFF00FF00),
    BLUE("Blue", 0xFF0000FF)
}
