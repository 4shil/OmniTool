package com.omnitool.features.security.random

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.SecureRandom
import javax.inject.Inject

/**
 * Random Number Generator ViewModel
 * 
 * Features:
 * - Configurable range (min/max)
 * - Generate single or multiple numbers
 * - Cryptographically secure random
 * - History of generated numbers
 */
@HiltViewModel
class RandomNumberViewModel @Inject constructor() : ViewModel() {
    
    private val secureRandom = SecureRandom()
    
    var minValue by mutableStateOf("1")
        private set
    
    var maxValue by mutableStateOf("100")
        private set
    
    var count by mutableIntStateOf(1)
        private set
    
    var generatedNumbers by mutableStateOf<List<Long>>(emptyList())
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var allowDuplicates by mutableStateOf(true)
        private set
    
    fun updateMinValue(value: String) {
        minValue = value.filter { it.isDigit() || it == '-' }
        errorMessage = null
    }
    
    fun updateMaxValue(value: String) {
        maxValue = value.filter { it.isDigit() || it == '-' }
        errorMessage = null
    }
    
    fun updateCount(value: Int) {
        count = value.coerceIn(1, 100)
    }
    
    fun toggleDuplicates() {
        allowDuplicates = !allowDuplicates
    }
    
    fun generate() {
        val min = minValue.toLongOrNull()
        val max = maxValue.toLongOrNull()
        
        if (min == null || max == null) {
            errorMessage = "Please enter valid numbers"
            generatedNumbers = emptyList()
            return
        }
        
        if (min >= max) {
            errorMessage = "Minimum must be less than maximum"
            generatedNumbers = emptyList()
            return
        }
        
        val range = max - min + 1
        
        // Check if we can generate unique numbers
        if (!allowDuplicates && count > range) {
            errorMessage = "Cannot generate $count unique numbers in range $min to $max"
            generatedNumbers = emptyList()
            return
        }
        
        generatedNumbers = if (allowDuplicates) {
            (1..count).map {
                min + (secureRandom.nextLong().mod(range))
            }
        } else {
            // Generate unique numbers using shuffle approach for small ranges
            if (range <= Int.MAX_VALUE) {
                (min..max).shuffled(secureRandom).take(count)
            } else {
                // For very large ranges, use rejection sampling
                val result = mutableSetOf<Long>()
                while (result.size < count) {
                    result.add(min + (secureRandom.nextLong().mod(range)))
                }
                result.toList()
            }
        }
        
        errorMessage = null
    }
    
    fun clearResults() {
        generatedNumbers = emptyList()
        errorMessage = null
    }
}
