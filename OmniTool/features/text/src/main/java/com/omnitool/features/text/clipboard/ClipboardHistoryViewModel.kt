package com.omnitool.features.text.clipboard

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Clipboard History Tool ViewModel
 * Stores and manages clipboard history entries
 */
@HiltViewModel
class ClipboardHistoryViewModel @Inject constructor() : ViewModel() {
    
    private val _historyItems = mutableStateListOf<ClipboardEntry>()
    val historyItems: List<ClipboardEntry> get() = _historyItems
    
    var searchQuery by mutableStateOf("")
        private set
    
    val filteredItems: List<ClipboardEntry>
        get() = if (searchQuery.isEmpty()) {
            _historyItems
        } else {
            _historyItems.filter { 
                it.content.contains(searchQuery, ignoreCase = true) 
            }
        }
    
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    
    fun addToHistory(content: String) {
        if (content.isBlank()) return
        
        // Remove if already exists (to move to top)
        _historyItems.removeAll { it.content == content }
        
        // Add to beginning
        _historyItems.add(0, ClipboardEntry(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis(),
            isPinned = false
        ))
        
        // Limit to 100 items
        while (_historyItems.size > 100) {
            val unpinnedItems = _historyItems.filter { !it.isPinned }
            if (unpinnedItems.isNotEmpty()) {
                _historyItems.remove(unpinnedItems.last())
            } else {
                break
            }
        }
    }
    
    fun togglePin(id: String) {
        val index = _historyItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _historyItems[index]
            _historyItems[index] = item.copy(isPinned = !item.isPinned)
        }
    }
    
    fun deleteItem(id: String) {
        _historyItems.removeAll { it.id == id }
    }
    
    fun clearUnpinned() {
        _historyItems.removeAll { !it.isPinned }
    }
    
    fun clearAll() {
        _historyItems.clear()
    }
}

data class ClipboardEntry(
    val id: String,
    val content: String,
    val timestamp: Long,
    val isPinned: Boolean
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun getPreview(maxLength: Int = 100): String {
        return if (content.length > maxLength) {
            content.take(maxLength) + "..."
        } else {
            content
        }
    }
}
