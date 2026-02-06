package com.omnitool.features.converter.url

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject

/**
 * URL Encoder/Decoder ViewModel
 * 
 * Features:
 * - URL encode text
 * - URL decode text
 * - Character encoding options
 */
@HiltViewModel
class UrlEncoderViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var mode by mutableStateOf(UrlMode.ENCODE)
        private set
    
    var encoding by mutableStateOf(CharEncoding.UTF_8)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    fun setInput(text: String) {
        inputText = text
        convert()
    }
    
    fun setMode(newMode: UrlMode) {
        mode = newMode
        convert()
    }
    
    fun setEncoding(enc: CharEncoding) {
        encoding = enc
        convert()
    }
    
    private fun convert() {
        errorMessage = null
        
        if (inputText.isEmpty()) {
            outputText = ""
            return
        }
        
        try {
            outputText = when (mode) {
                UrlMode.ENCODE -> URLEncoder.encode(inputText, encoding.charset)
                UrlMode.DECODE -> URLDecoder.decode(inputText, encoding.charset)
            }
        } catch (e: Exception) {
            errorMessage = "Invalid input for ${mode.displayName.lowercase()}"
            outputText = ""
        }
    }
    
    fun swap() {
        val temp = inputText
        inputText = outputText
        outputText = temp
        mode = if (mode == UrlMode.ENCODE) UrlMode.DECODE else UrlMode.ENCODE
    }
    
    fun clear() {
        inputText = ""
        outputText = ""
        errorMessage = null
    }
}

enum class UrlMode(val displayName: String) {
    ENCODE("Encode"),
    DECODE("Decode")
}

enum class CharEncoding(val displayName: String, val charset: String) {
    UTF_8("UTF-8", "UTF-8"),
    ASCII("ASCII", "US-ASCII"),
    ISO_8859_1("ISO-8859-1", "ISO-8859-1")
}
