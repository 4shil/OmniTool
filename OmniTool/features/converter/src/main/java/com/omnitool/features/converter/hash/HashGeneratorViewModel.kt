package com.omnitool.features.converter.hash

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Hash Generator ViewModel
 * 
 * Features:
 * - Multiple hash algorithms
 * - Real-time hash generation
 * - Uppercase/lowercase toggle
 */
@HiltViewModel
class HashGeneratorViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var hashes by mutableStateOf<Map<HashAlgorithm, String>>(emptyMap())
        private set
    
    var selectedAlgorithm by mutableStateOf(HashAlgorithm.SHA256)
        private set
    
    var isUppercase by mutableStateOf(false)
        private set
    
    var isProcessing by mutableStateOf(false)
        private set
    
    fun setInput(text: String) {
        inputText = text
        generateHashes()
    }
    
    fun setAlgorithm(algorithm: HashAlgorithm) {
        selectedAlgorithm = algorithm
    }
    
    fun toggleCase() {
        isUppercase = !isUppercase
        // Re-apply case to existing hashes
        hashes = hashes.mapValues { (_, hash) ->
            if (isUppercase) hash.uppercase() else hash.lowercase()
        }
    }
    
    private fun generateHashes() {
        if (inputText.isEmpty()) {
            hashes = emptyMap()
            return
        }
        
        viewModelScope.launch {
            isProcessing = true
            
            withContext(Dispatchers.Default) {
                val newHashes = mutableMapOf<HashAlgorithm, String>()
                
                HashAlgorithm.values().forEach { algorithm ->
                    try {
                        val digest = MessageDigest.getInstance(algorithm.algorithmName)
                        val hashBytes = digest.digest(inputText.toByteArray(Charsets.UTF_8))
                        val hashString = hashBytes.joinToString("") { "%02x".format(it) }
                        newHashes[algorithm] = if (isUppercase) hashString.uppercase() else hashString
                    } catch (e: Exception) {
                        newHashes[algorithm] = "Error"
                    }
                }
                
                hashes = newHashes
            }
            
            isProcessing = false
        }
    }
    
    fun getSelectedHash(): String {
        return hashes[selectedAlgorithm] ?: ""
    }
    
    fun clear() {
        inputText = ""
        hashes = emptyMap()
    }
}

enum class HashAlgorithm(val displayName: String, val algorithmName: String) {
    MD5("MD5", "MD5"),
    SHA1("SHA-1", "SHA-1"),
    SHA256("SHA-256", "SHA-256"),
    SHA384("SHA-384", "SHA-384"),
    SHA512("SHA-512", "SHA-512")
}
