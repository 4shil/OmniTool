package com.omnitool.features.utilities.soundmeter

import android.Manifest
import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Sound Meter ViewModel
 * 
 * Features:
 * - Real-time decibel measurement
 * - Min/Max tracking
 * - Visual level indicator
 */
@HiltViewModel
class SoundMeterViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var currentDb by mutableFloatStateOf(0f)
        private set
    
    var minDb by mutableFloatStateOf(Float.MAX_VALUE)
        private set
    
    var maxDb by mutableFloatStateOf(0f)
        private set
    
    var isRecording by mutableStateOf(false)
        private set
    
    var needsPermission by mutableStateOf(true)
        private set
    
    private var recordingJob: Job? = null
    private var audioRecord: AudioRecord? = null
    
    // Recent readings for smoothing
    private val recentReadings = mutableListOf<Float>()
    private val maxReadings = 5
    
    fun setPermissionGranted(granted: Boolean) {
        needsPermission = !granted
    }
    
    fun startRecording() {
        if (needsPermission) return
        
        isRecording = true
        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val sampleRate = 44100
                val bufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT
                )
                
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )
                
                audioRecord?.startRecording()
                val buffer = ShortArray(bufferSize)
                
                while (isActive && isRecording) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        val rms = calculateRms(buffer, read)
                        val db = calculateDb(rms)
                        
                        recentReadings.add(db)
                        if (recentReadings.size > maxReadings) {
                            recentReadings.removeAt(0)
                        }
                        
                        val smoothedDb = recentReadings.average().toFloat()
                        
                        currentDb = smoothedDb
                        if (smoothedDb < minDb && smoothedDb > 0) minDb = smoothedDb
                        if (smoothedDb > maxDb) maxDb = smoothedDb
                    }
                    delay(100) // Update ~10 times per second
                }
            } catch (e: Exception) {
                isRecording = false
            } finally {
                audioRecord?.stop()
                audioRecord?.release()
                audioRecord = null
            }
        }
    }
    
    fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null
    }
    
    fun resetStats() {
        minDb = Float.MAX_VALUE
        maxDb = 0f
        recentReadings.clear()
    }
    
    private fun calculateRms(buffer: ShortArray, read: Int): Double {
        var sum = 0.0
        for (i in 0 until read) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / read)
    }
    
    private fun calculateDb(rms: Double): Float {
        // Reference: 32767 = max 16-bit value
        // Standard reference for SPL is 20 µPa, but we use relative measurement
        return if (rms > 0) {
            (20 * log10(rms / 32767.0) + 90).toFloat().coerceIn(0f, 120f)
        } else {
            0f
        }
    }
    
    fun getNoiseLevel(): NoiseLevel {
        return when {
            currentDb < 30 -> NoiseLevel.QUIET
            currentDb < 60 -> NoiseLevel.MODERATE
            currentDb < 80 -> NoiseLevel.LOUD
            currentDb < 100 -> NoiseLevel.VERY_LOUD
            else -> NoiseLevel.DANGEROUS
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopRecording()
    }
}

enum class NoiseLevel(val displayName: String, val description: String) {
    QUIET("Quiet", "Library, whisper"),
    MODERATE("Moderate", "Normal conversation"),
    LOUD("Loud", "Busy traffic"),
    VERY_LOUD("Very Loud", "Power tools"),
    DANGEROUS("Dangerous", "Risk of hearing damage")
}
