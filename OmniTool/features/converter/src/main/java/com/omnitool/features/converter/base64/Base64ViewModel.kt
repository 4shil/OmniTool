package com.omnitool.features.converter.base64

import android.util.Base64
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Base64 Encoder/Decoder ViewModel
 * 
 * Features:
 * - Encode text to Base64
 * - Decode Base64 to text
 * - URL-safe encoding option
 */
@HiltViewModel
class Base64ViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var mode by mutableStateOf(Base64Mode.ENCODE)
        private set
    
    var urlSafe by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    fun setInput(text: String) {
        inputText = text
        convert()
    }
    
    fun setMode(newMode: Base64Mode) {
        mode = newMode
        // Swap input/output when changing mode
        val temp = inputText
        inputText = outputText
        outputText = temp
        convert()
    }
    
    fun setUrlSafe(safe: Boolean) {
        urlSafe = safe
        convert()
    }
    
    private fun convert() {
        errorMessage = null
        
        if (inputText.isEmpty()) {
            outputText = ""
            return
        }
        
        try {
            val flags = if (urlSafe) Base64.URL_SAFE or Base64.NO_WRAP else Base64.NO_WRAP
            
            outputText = when (mode) {
                Base64Mode.ENCODE -> {
                    Base64.encodeToString(inputText.toByteArray(Charsets.UTF_8), flags)
                }
                Base64Mode.DECODE -> {
                    String(Base64.decode(inputText, flags), Charsets.UTF_8)
                }
            }
        } catch (e: Exception) {
            errorMessage = when (mode) {
                Base64Mode.ENCODE -> "Encoding error"
                Base64Mode.DECODE -> "Invalid Base64 input"
            }
            outputText = ""
        }
    }
    
    fun swap() {
        val temp = inputText
        inputText = outputText
        outputText = temp
        mode = if (mode == Base64Mode.ENCODE) Base64Mode.DECODE else Base64Mode.ENCODE
    }
    
    fun clear() {
        inputText = ""
        outputText = ""
        errorMessage = null
    }
}

enum class Base64Mode(val displayName: String) {
    ENCODE("Encode"),
    DECODE("Decode")
}
