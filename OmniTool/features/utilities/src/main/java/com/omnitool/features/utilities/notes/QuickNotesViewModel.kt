package com.omnitool.features.utilities.notes

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Quick Notes ViewModel
 * 
 * Features:
 * - Simple note taking
 * - Auto-save
 * - Multiple notes
 * - Search
 */
@HiltViewModel
class QuickNotesViewModel @Inject constructor() : ViewModel() {
    
    var notes by mutableStateOf<List<QuickNote>>(emptyList())
        private set
    
    var currentNote by mutableStateOf<QuickNote?>(null)
        private set
    
    var editContent by mutableStateOf("")
        private set
    
    var editTitle by mutableStateOf("")
        private set
    
    var searchQuery by mutableStateOf("")
        private set
    
    var isEditing by mutableStateOf(false)
        private set
    
    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    
    val filteredNotes: List<QuickNote>
        get() = if (searchQuery.isEmpty()) {
            notes
        } else {
            notes.filter { 
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
            }
        }
    
    fun updateSearch(query: String) {
        searchQuery = query
    }
    
    fun createNewNote() {
        currentNote = null
        editTitle = ""
        editContent = ""
        isEditing = true
    }
    
    fun editNote(note: QuickNote) {
        currentNote = note
        editTitle = note.title
        editContent = note.content
        isEditing = true
    }
    
    fun updateTitle(title: String) {
        editTitle = title
    }
    
    fun updateContent(content: String) {
        editContent = content
    }
    
    fun saveNote() {
        if (editContent.isBlank() && editTitle.isBlank()) {
            isEditing = false
            return
        }
        
        val title = editTitle.ifBlank { 
            editContent.take(30).replace("\n", " ").trim()
        }
        
        val now = System.currentTimeMillis()
        
        if (currentNote != null) {
            // Update existing note
            notes = notes.map { 
                if (it.id == currentNote!!.id) {
                    it.copy(
                        title = title,
                        content = editContent,
                        updatedAt = now
                    )
                } else it
            }
        } else {
            // Create new note
            val newNote = QuickNote(
                id = UUID.randomUUID().toString(),
                title = title,
                content = editContent,
                createdAt = now,
                updatedAt = now
            )
            notes = listOf(newNote) + notes
        }
        
        isEditing = false
        currentNote = null
        editTitle = ""
        editContent = ""
    }
    
    fun deleteNote(note: QuickNote) {
        notes = notes.filter { it.id != note.id }
        if (currentNote?.id == note.id) {
            isEditing = false
            currentNote = null
        }
    }
    
    fun cancelEdit() {
        isEditing = false
        currentNote = null
        editTitle = ""
        editContent = ""
    }
    
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
}

data class QuickNote(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
