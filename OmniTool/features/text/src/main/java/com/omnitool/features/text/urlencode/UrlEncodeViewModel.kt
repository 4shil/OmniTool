package com.omnitool.features.text.urlencode

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject

/**
 * URL Encode/Decode Tool ViewModel
 */
@HiltViewModel
class UrlEncodeViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var mode by mutableStateOf(UrlMode.ENCODE)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        errorMessage = null
    }
    
    fun setMode(newMode: UrlMode) {
        mode = newMode
        if (inputText.isNotEmpty()) {
            process()
        }
    }
    
    fun process() {
        viewModelScope.launch {
            try {
                if (inputText.isEmpty()) {
                    errorMessage = "Please enter text to ${if (mode == UrlMode.ENCODE) "encode" else "decode"}"
                    outputText = ""
                    return@launch
                }
                
                outputText = when (mode) {
                    UrlMode.ENCODE -> URLEncoder.encode(inputText, "UTF-8")
                    UrlMode.DECODE -> URLDecoder.decode(inputText, "UTF-8")
                }
                errorMessage = null
            } catch (e: IllegalArgumentException) {
                outputText = ""
                errorMessage = "Invalid URL encoded string"
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
        mode = if (mode == UrlMode.ENCODE) UrlMode.DECODE else UrlMode.ENCODE
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        errorMessage = null
    }
}

enum class UrlMode {
    ENCODE,
    DECODE
}
