package com.omnitool.features.security.password

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.SecureRandom
import javax.inject.Inject

/**
 * Password Generator Tool ViewModel
 * 
 * Features:
 * - Configurable password length (8-128 characters)
 * - Character type toggles (uppercase, lowercase, numbers, symbols)
 * - Secure random generation using SecureRandom
 * - Copy-friendly output
 */
@HiltViewModel
class PasswordGeneratorViewModel @Inject constructor() : ViewModel() {
    
    private val secureRandom = SecureRandom()
    
    var generatedPassword by mutableStateOf("")
        private set
    
    var passwordLength by mutableIntStateOf(16)
        private set
    
    var includeUppercase by mutableStateOf(true)
        private set
    
    var includeLowercase by mutableStateOf(true)
        private set
    
    var includeNumbers by mutableStateOf(true)
        private set
    
    var includeSymbols by mutableStateOf(true)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // Character sets
    private val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
    private val numberChars = "0123456789"
    private val symbolChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?"
    
    fun updateLength(length: Int) {
        passwordLength = length.coerceIn(4, 128)
    }
    
    fun toggleUppercase() {
        includeUppercase = !includeUppercase
        validateOptions()
    }
    
    fun toggleLowercase() {
        includeLowercase = !includeLowercase
        validateOptions()
    }
    
    fun toggleNumbers() {
        includeNumbers = !includeNumbers
        validateOptions()
    }
    
    fun toggleSymbols() {
        includeSymbols = !includeSymbols
        validateOptions()
    }
    
    private fun validateOptions() {
        // Ensure at least one option is selected
        if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSymbols) {
            errorMessage = "At least one character type must be selected"
        } else {
            errorMessage = null
        }
    }
    
    fun generatePassword() {
        if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSymbols) {
            errorMessage = "At least one character type must be selected"
            generatedPassword = ""
            return
        }
        
        val characterPool = buildString {
            if (includeUppercase) append(uppercaseChars)
            if (includeLowercase) append(lowercaseChars)
            if (includeNumbers) append(numberChars)
            if (includeSymbols) append(symbolChars)
        }
        
        generatedPassword = (1..passwordLength)
            .map { characterPool[secureRandom.nextInt(characterPool.length)] }
            .joinToString("")
        
        errorMessage = null
    }
    
    fun clearPassword() {
        generatedPassword = ""
        errorMessage = null
    }
}
