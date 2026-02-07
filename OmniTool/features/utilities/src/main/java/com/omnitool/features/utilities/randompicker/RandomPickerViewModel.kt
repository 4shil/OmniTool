package com.omnitool.features.utilities.randompicker

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

/**
 * Random Picker ViewModel
 * 
 * Features:
 * - Add items to list
 * - Pick random item
 * - History of picks
 */
@HiltViewModel
class RandomPickerViewModel @Inject constructor() : ViewModel() {
    
    var items by mutableStateOf<List<String>>(emptyList())
        private set
    
    var currentInput by mutableStateOf("")
        private set
    
    var selectedItem by mutableStateOf<String?>(null)
        private set
    
    var pickHistory by mutableStateOf<List<String>>(emptyList())
        private set
    
    var isPicking by mutableStateOf(false)
        private set
    
    fun updateInput(text: String) {
        currentInput = text
    }
    
    fun addItem() {
        if (currentInput.isNotBlank()) {
            items = items + currentInput.trim()
            currentInput = ""
        }
    }
    
    fun addItems(text: String) {
        // Add multiple items separated by newlines or commas
        val newItems = text.split(Regex("[,\\n]"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
        items = items + newItems
    }
    
    fun removeItem(item: String) {
        items = items - item
    }
    
    fun clearItems() {
        items = emptyList()
        selectedItem = null
    }
    
    fun pickRandom() {
        if (items.isEmpty()) return
        
        isPicking = true
        selectedItem = items[Random.nextInt(items.size)]
        pickHistory = listOf(selectedItem!!) + pickHistory.take(9)
        isPicking = false
    }
    
    fun removeSelected() {
        selectedItem?.let { selected ->
            items = items - selected
            selectedItem = null
        }
    }
    
    fun clearSelection() {
        selectedItem = null
    }
    
    fun clearHistory() {
        pickHistory = emptyList()
    }
}
