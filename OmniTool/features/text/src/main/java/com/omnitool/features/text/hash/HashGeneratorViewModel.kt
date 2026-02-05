package com.omnitool.features.text.hash

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
 * Hash Generator Tool ViewModel
 * Supports MD5, SHA-1, SHA-256, SHA-512
 */
@HiltViewModel
class HashGeneratorViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var selectedAlgorithm by mutableStateOf(HashAlgorithm.SHA256)
        private set
    
    var hashResults by mutableStateOf<Map<HashAlgorithm, String>>(emptyMap())
        private set
    
    var isGenerating by mutableStateOf(false)
        private set
    
    fun updateInput(text: String) {
        inputText = text
    }
    
    fun setAlgorithm(algorithm: HashAlgorithm) {
        selectedAlgorithm = algorithm
    }
    
    fun generateHash() {
        if (inputText.isEmpty()) return
        
        viewModelScope.launch {
            isGenerating = true
            hashResults = withContext(Dispatchers.Default) {
                HashAlgorithm.values().associateWith { algo ->
                    computeHash(inputText, algo)
                }
            }
            isGenerating = false
        }
    }
    
    fun generateAllHashes() {
        if (inputText.isEmpty()) return
        
        viewModelScope.launch {
            isGenerating = true
            hashResults = withContext(Dispatchers.Default) {
                HashAlgorithm.values().associateWith { algo ->
                    computeHash(inputText, algo)
                }
            }
            isGenerating = false
        }
    }
    
    private fun computeHash(text: String, algorithm: HashAlgorithm): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm.algorithmName)
            val hashBytes = digest.digest(text.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
    
    fun clearAll() {
        inputText = ""
        hashResults = emptyMap()
    }
}

enum class HashAlgorithm(val algorithmName: String, val displayName: String) {
    MD5("MD5", "MD5"),
    SHA1("SHA-1", "SHA-1"),
    SHA256("SHA-256", "SHA-256"),
    SHA512("SHA-512", "SHA-512")
}
