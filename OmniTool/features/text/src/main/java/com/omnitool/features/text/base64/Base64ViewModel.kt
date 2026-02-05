package com.omnitool.features.text.base64

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Base64
import javax.inject.Inject

/**
 * Base64 Encode/Decode Tool ViewModel
 */
@HiltViewModel
class Base64ViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var mode by mutableStateOf(Base64Mode.ENCODE)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        errorMessage = null
    }
    
    fun setMode(newMode: Base64Mode) {
        mode = newMode
        // Re-process with new mode if there's input
        if (inputText.isNotEmpty()) {
            process()
        }
    }
    
    fun process() {
        viewModelScope.launch {
            try {
                if (inputText.isEmpty()) {
                    errorMessage = "Please enter text to ${if (mode == Base64Mode.ENCODE) "encode" else "decode"}"
                    outputText = ""
                    return@launch
                }
                
                outputText = when (mode) {
                    Base64Mode.ENCODE -> {
                        Base64.getEncoder().encodeToString(inputText.toByteArray(Charsets.UTF_8))
                    }
                    Base64Mode.DECODE -> {
                        String(Base64.getDecoder().decode(inputText.trim()), Charsets.UTF_8)
                    }
                }
                errorMessage = null
            } catch (e: IllegalArgumentException) {
                outputText = ""
                errorMessage = "Invalid Base64 string. Make sure input contains only valid Base64 characters."
            } catch (e: Exception) {
                outputText = ""
                errorMessage = "Error: ${e.message}"
            }
        }
    }
    
    fun swapInputOutput() {
        val temp = outputText
        outputText = inputText
        inputText = temp
        mode = if (mode == Base64Mode.ENCODE) Base64Mode.DECODE else Base64Mode.ENCODE
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        errorMessage = null
    }
}

enum class Base64Mode {
    ENCODE,
    DECODE
}
