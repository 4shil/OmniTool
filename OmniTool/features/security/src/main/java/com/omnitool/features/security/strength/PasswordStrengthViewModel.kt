package com.omnitool.features.security.strength

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.log2

/**
 * Password Strength Checker ViewModel
 * 
 * Evaluates password strength based on:
 * - Length
 * - Character variety (uppercase, lowercase, numbers, symbols)
 * - Common patterns and sequences
 * - Entropy calculation
 */
@HiltViewModel
class PasswordStrengthViewModel @Inject constructor() : ViewModel() {
    
    var inputPassword by mutableStateOf("")
        private set
    
    var strengthLevel by mutableStateOf(StrengthLevel.NONE)
        private set
    
    var strengthScore by mutableIntStateOf(0)
        private set
    
    var feedback by mutableStateOf<List<StrengthFeedback>>(emptyList())
        private set
    
    var entropyBits by mutableStateOf(0.0)
        private set
    
    // Common weak passwords
    private val commonPasswords = setOf(
        "password", "123456", "12345678", "qwerty", "abc123",
        "password1", "admin", "letmein", "welcome", "monkey",
        "dragon", "master", "login", "princess", "iloveyou"
    )
    
    fun updatePassword(password: String) {
        inputPassword = password
        analyzePassword(password)
    }
    
    private fun analyzePassword(password: String) {
        if (password.isEmpty()) {
            strengthLevel = StrengthLevel.NONE
            strengthScore = 0
            feedback = emptyList()
            entropyBits = 0.0
            return
        }
        
        val feedbackList = mutableListOf<StrengthFeedback>()
        var score = 0
        
        // Length checks
        when {
            password.length >= 16 -> {
                score += 30
                feedbackList.add(StrengthFeedback.LengthExcellent)
            }
            password.length >= 12 -> {
                score += 20
                feedbackList.add(StrengthFeedback.LengthGood)
            }
            password.length >= 8 -> {
                score += 10
                feedbackList.add(StrengthFeedback.LengthFair)
            }
            else -> {
                feedbackList.add(StrengthFeedback.LengthWeak)
            }
        }
        
        // Character variety
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasNumbers = password.any { it.isDigit() }
        val hasSymbols = password.any { !it.isLetterOrDigit() }
        
        if (hasUppercase) {
            score += 15
            feedbackList.add(StrengthFeedback.HasUppercase)
        } else {
            feedbackList.add(StrengthFeedback.MissingUppercase)
        }
        
        if (hasLowercase) {
            score += 15
            feedbackList.add(StrengthFeedback.HasLowercase)
        } else {
            feedbackList.add(StrengthFeedback.MissingLowercase)
        }
        
        if (hasNumbers) {
            score += 15
            feedbackList.add(StrengthFeedback.HasNumbers)
        } else {
            feedbackList.add(StrengthFeedback.MissingNumbers)
        }
        
        if (hasSymbols) {
            score += 20
            feedbackList.add(StrengthFeedback.HasSymbols)
        } else {
            feedbackList.add(StrengthFeedback.MissingSymbols)
        }
        
        // Pattern detection
        if (commonPasswords.contains(password.lowercase())) {
            score = 5 // Override score for common passwords
            feedbackList.add(StrengthFeedback.CommonPassword)
        }
        
        if (hasRepeatingPatterns(password)) {
            score -= 10
            feedbackList.add(StrengthFeedback.RepeatingPattern)
        }
        
        if (hasSequentialChars(password)) {
            score -= 10
            feedbackList.add(StrengthFeedback.SequentialChars)
        }
        
        // Calculate entropy
        entropyBits = calculateEntropy(password)
        
        // Clamp score
        strengthScore = score.coerceIn(0, 100)
        
        // Determine strength level
        strengthLevel = when {
            strengthScore >= 80 -> StrengthLevel.VERY_STRONG
            strengthScore >= 60 -> StrengthLevel.STRONG
            strengthScore >= 40 -> StrengthLevel.FAIR
            strengthScore >= 20 -> StrengthLevel.WEAK
            else -> StrengthLevel.VERY_WEAK
        }
        
        feedback = feedbackList
    }
    
    private fun hasRepeatingPatterns(password: String): Boolean {
        // Check for character repetition (aaa, 111)
        for (i in 0 until password.length - 2) {
            if (password[i] == password[i + 1] && password[i] == password[i + 2]) {
                return true
            }
        }
        return false
    }
    
    private fun hasSequentialChars(password: String): Boolean {
        val sequences = listOf(
            "abcdefghijklmnopqrstuvwxyz",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "qwertyuiopasdfghjklzxcvbnm"
        )
        
        for (sequence in sequences) {
            for (i in 0..sequence.length - 3) {
                val sub = sequence.substring(i, i + 3)
                if (password.contains(sub, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }
    
    private fun calculateEntropy(password: String): Double {
        if (password.isEmpty()) return 0.0
        
        var poolSize = 0
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        if (password.any { !it.isLetterOrDigit() }) poolSize += 32
        
        return if (poolSize > 0) {
            password.length * log2(poolSize.toDouble())
        } else 0.0
    }
    
    fun clearPassword() {
        inputPassword = ""
        strengthLevel = StrengthLevel.NONE
        strengthScore = 0
        feedback = emptyList()
        entropyBits = 0.0
    }
}

enum class StrengthLevel {
    NONE,
    VERY_WEAK,
    WEAK,
    FAIR,
    STRONG,
    VERY_STRONG
}

enum class StrengthFeedback(val message: String, val isPositive: Boolean) {
    // Length feedback
    LengthExcellent("Excellent length (16+ characters)", true),
    LengthGood("Good length (12+ characters)", true),
    LengthFair("Fair length (8+ characters)", false),
    LengthWeak("Too short (less than 8 characters)", false),
    
    // Character variety feedback
    HasUppercase("Contains uppercase letters", true),
    HasLowercase("Contains lowercase letters", true),
    HasNumbers("Contains numbers", true),
    HasSymbols("Contains symbols", true),
    MissingUppercase("Add uppercase letters for more strength", false),
    MissingLowercase("Add lowercase letters for more strength", false),
    MissingNumbers("Add numbers for more strength", false),
    MissingSymbols("Add symbols (!@#$%) for more strength", false),
    
    // Pattern feedback
    CommonPassword("This is a commonly used password", false),
    RepeatingPattern("Contains repeating characters", false),
    SequentialChars("Contains sequential characters", false)
}
