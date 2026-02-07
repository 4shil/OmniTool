package com.omnitool.features.security.notes

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import android.util.Base64

/**
 * Encrypted Notes ViewModel
 * 
 * Features:
 * - PIN-protected notes
 * - AES encryption
 * - Secure storage
 */
@HiltViewModel
class EncryptedNotesViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val prefs = context.getSharedPreferences("encrypted_notes", Context.MODE_PRIVATE)
    private val keyAlias = "encrypted_notes_key"
    
    var isLocked by mutableStateOf(true)
        private set
    
    var hasPin by mutableStateOf(false)
        private set
    
    var notes by mutableStateOf<List<EncryptedNote>>(emptyList())
        private set
    
    var currentNote by mutableStateOf<EncryptedNote?>(null)
        private set
    
    var editingContent by mutableStateOf("")
        private set
    
    var editingTitle by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    init {
        hasPin = prefs.contains("pin_hash")
        if (!hasPin) {
            isLocked = false
        }
    }
    
    fun setupPin(pin: String) {
        if (pin.length < 4) {
            errorMessage = "PIN must be at least 4 digits"
            return
        }
        prefs.edit().putString("pin_hash", pin.hashCode().toString()).apply()
        hasPin = true
        isLocked = false
        errorMessage = null
    }
    
    fun unlock(pin: String): Boolean {
        val storedHash = prefs.getString("pin_hash", null)
        if (storedHash == pin.hashCode().toString()) {
            isLocked = false
            loadNotes()
            errorMessage = null
            return true
        }
        errorMessage = "Incorrect PIN"
        return false
    }
    
    fun lock() {
        isLocked = true
        notes = emptyList()
        currentNote = null
    }
    
    private fun loadNotes() {
        viewModelScope.launch {
            val noteIds = prefs.getStringSet("note_ids", emptySet()) ?: emptySet()
            notes = noteIds.mapNotNull { id ->
                try {
                    val encryptedTitle = prefs.getString("note_${id}_title", null) ?: return@mapNotNull null
                    val encryptedContent = prefs.getString("note_${id}_content", null) ?: return@mapNotNull null
                    val timestamp = prefs.getLong("note_${id}_time", 0)
                    
                    EncryptedNote(
                        id = id,
                        title = decrypt(encryptedTitle),
                        content = decrypt(encryptedContent),
                        timestamp = timestamp
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.timestamp }
        }
    }
    
    fun createNote() {
        currentNote = null
        editingTitle = ""
        editingContent = ""
    }
    
    fun editNote(note: EncryptedNote) {
        currentNote = note
        editingTitle = note.title
        editingContent = note.content
    }
    
    fun updateTitle(title: String) {
        editingTitle = title
    }
    
    fun updateContent(content: String) {
        editingContent = content
    }
    
    fun saveNote() {
        viewModelScope.launch {
            try {
                val id = currentNote?.id ?: System.currentTimeMillis().toString()
                val timestamp = System.currentTimeMillis()
                
                withContext(Dispatchers.IO) {
                    prefs.edit()
                        .putString("note_${id}_title", encrypt(editingTitle))
                        .putString("note_${id}_content", encrypt(editingContent))
                        .putLong("note_${id}_time", timestamp)
                        .putStringSet("note_ids", 
                            (prefs.getStringSet("note_ids", emptySet()) ?: emptySet()) + id)
                        .apply()
                }
                
                loadNotes()
                currentNote = null
                editingTitle = ""
                editingContent = ""
            } catch (e: Exception) {
                errorMessage = "Failed to save note: ${e.message}"
            }
        }
    }
    
    fun deleteNote(note: EncryptedNote) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val ids = (prefs.getStringSet("note_ids", emptySet()) ?: emptySet()) - note.id
                prefs.edit()
                    .remove("note_${note.id}_title")
                    .remove("note_${note.id}_content")
                    .remove("note_${note.id}_time")
                    .putStringSet("note_ids", ids)
                    .apply()
            }
            loadNotes()
        }
    }
    
    fun cancelEdit() {
        currentNote = null
        editingTitle = ""
        editingContent = ""
    }
    
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        
        return if (keyStore.containsAlias(keyAlias)) {
            (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }
    
    private fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    private fun decrypt(data: String): String {
        val combined = Base64.decode(data, Base64.DEFAULT)
        val iv = combined.sliceArray(0 until 12)
        val encrypted = combined.sliceArray(12 until combined.size)
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}

data class EncryptedNote(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long
)
