package com.omnitool.features.security.otp

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import kotlin.math.pow

/**
 * OTP Generator ViewModel (TOTP/HOTP)
 * 
 * Implements RFC 6238 (TOTP) and RFC 4226 (HOTP)
 * For demonstration - actual use requires saved secrets
 */
@HiltViewModel
class OtpGeneratorViewModel @Inject constructor() : ViewModel() {
    
    var secretKey by mutableStateOf("")
        private set
    
    var currentOtp by mutableStateOf("")
        private set
    
    var timeRemaining by mutableIntStateOf(30)
        private set
    
    var mode by mutableStateOf(OtpMode.TOTP)
        private set
    
    var counter by mutableLongStateOf(0L)
        private set
    
    var digits by mutableIntStateOf(6)
        private set
    
    var period by mutableIntStateOf(30)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isGenerating by mutableStateOf(false)
        private set
    
    private var timerJob: Job? = null
    
    fun updateSecretKey(key: String) {
        // Allow only base32 characters
        secretKey = key.uppercase().filter { it in "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=" }
        errorMessage = null
    }
    
    fun setMode(newMode: OtpMode) {
        mode = newMode
        stopTimer()
        currentOtp = ""
    }
    
    fun setDigits(count: Int) {
        digits = count.coerceIn(6, 8)
    }
    
    fun setPeriod(seconds: Int) {
        period = seconds.coerceIn(30, 60)
    }
    
    fun incrementCounter() {
        counter++
    }
    
    fun generateOtp() {
        if (secretKey.isEmpty()) {
            errorMessage = "Please enter a secret key"
            currentOtp = ""
            return
        }
        
        if (secretKey.length < 16) {
            errorMessage = "Secret key should be at least 16 characters"
            currentOtp = ""
            return
        }
        
        try {
            val decodedKey = base32Decode(secretKey)
            
            when (mode) {
                OtpMode.TOTP -> {
                    val timeCounter = System.currentTimeMillis() / 1000 / period
                    currentOtp = generateHotp(decodedKey, timeCounter, digits)
                    startTimer()
                }
                OtpMode.HOTP -> {
                    currentOtp = generateHotp(decodedKey, counter, digits)
                }
            }
            
            isGenerating = true
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Invalid secret key format"
            currentOtp = ""
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentSeconds = (System.currentTimeMillis() / 1000) % period
                timeRemaining = (period - currentSeconds).toInt()
                
                // Regenerate when timer resets
                if (timeRemaining == period && secretKey.isNotEmpty()) {
                    try {
                        val decodedKey = base32Decode(secretKey)
                        val timeCounter = System.currentTimeMillis() / 1000 / period
                        currentOtp = generateHotp(decodedKey, timeCounter, digits)
                    } catch (_: Exception) {}
                }
                
                delay(1000)
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        isGenerating = false
    }
    
    fun generateRandomSecret(): String {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        return base32Encode(bytes)
    }
    
    fun setRandomSecret() {
        secretKey = generateRandomSecret()
    }
    
    fun clearAll() {
        stopTimer()
        secretKey = ""
        currentOtp = ""
        counter = 0
        errorMessage = null
        isGenerating = false
    }
    
    private fun generateHotp(key: ByteArray, counter: Long, digits: Int): String {
        val data = ByteBuffer.allocate(8).putLong(counter).array()
        val hash = hmacSha1(key, data)
        
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                     ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                     ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                     (hash[offset + 3].toInt() and 0xFF)
        
        val otp = binary % 10.0.pow(digits).toInt()
        return otp.toString().padStart(digits, '0')
    }
    
    private fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        return mac.doFinal(data)
    }
    
    private fun base32Decode(input: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleanInput = input.uppercase().replace("=", "")
        
        val output = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0
        
        for (char in cleanInput) {
            val value = alphabet.indexOf(char)
            if (value == -1) throw IllegalArgumentException("Invalid Base32 character")
            
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                output.add((buffer shr bitsLeft).toByte())
            }
        }
        
        return output.toByteArray()
    }
    
    private fun base32Encode(input: ByteArray): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val result = StringBuilder()
        var buffer = 0
        var bitsLeft = 0
        
        for (byte in input) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bitsLeft += 8
            
            while (bitsLeft >= 5) {
                bitsLeft -= 5
                result.append(alphabet[(buffer shr bitsLeft) and 0x1F])
            }
        }
        
        if (bitsLeft > 0) {
            result.append(alphabet[(buffer shl (5 - bitsLeft)) and 0x1F])
        }
        
        return result.toString()
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

enum class OtpMode {
    TOTP,  // Time-based
    HOTP   // Counter-based
}
