package com.omnitool.features.text.randomstring

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

/**
 * Random String Generator Tool ViewModel
 */
@HiltViewModel
class RandomStringViewModel @Inject constructor() : ViewModel() {
    
    var outputText by mutableStateOf("")
        private set
    
    var length by mutableStateOf(16)
        private set
    
    var count by mutableStateOf(1)
        private set
    
    var options by mutableStateOf(GeneratorOptions())
        private set
    
    fun setLength(newLength: Int) {
        length = newLength.coerceIn(1, 256)
    }
    
    fun setCount(newCount: Int) {
        count = newCount.coerceIn(1, 100)
    }
    
    fun updateOptions(newOptions: GeneratorOptions) {
        options = newOptions
    }
    
    fun generate() {
        val charset = buildCharset()
        if (charset.isEmpty()) {
            outputText = "Select at least one character type"
            return
        }
        
        outputText = (1..count).joinToString("\n") {
            generateString(charset, length)
        }
    }
    
    private fun buildCharset(): String {
        val builder = StringBuilder()
        if (options.uppercase) builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        if (options.lowercase) builder.append("abcdefghijklmnopqrstuvwxyz")
        if (options.numbers) builder.append("0123456789")
        if (options.symbols) builder.append("!@#\$%^&*()_+-=[]{}|;:,.<>?")
        if (options.excludeAmbiguous) {
            // Remove ambiguous characters: 0, O, l, 1, I
            val ambiguous = setOf('0', 'O', 'l', '1', 'I')
            return builder.toString().filter { it !in ambiguous }
        }
        return builder.toString()
    }
    
    private fun generateString(charset: String, length: Int): String {
        return (1..length)
            .map { charset[Random.nextInt(charset.length)] }
            .joinToString("")
    }
    
    fun clearAll() {
        outputText = ""
    }
}

data class GeneratorOptions(
    val uppercase: Boolean = true,
    val lowercase: Boolean = true,
    val numbers: Boolean = true,
    val symbols: Boolean = false,
    val excludeAmbiguous: Boolean = false
)
