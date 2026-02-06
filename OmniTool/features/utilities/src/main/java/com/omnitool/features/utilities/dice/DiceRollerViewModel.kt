package com.omnitool.features.utilities.dice

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.SecureRandom
import javax.inject.Inject

/**
 * Dice Roller / Random Picker ViewModel
 * 
 * Features:
 * - Multiple dice types (D4, D6, D8, D10, D12, D20, D100)
 * - Roll multiple dice
 * - Roll history
 * - Custom range picker
 */
@HiltViewModel
class DiceRollerViewModel @Inject constructor() : ViewModel() {
    
    private val random = SecureRandom()
    
    var selectedDice by mutableStateOf(DiceType.D6)
        private set
    
    var diceCount by mutableIntStateOf(1)
        private set
    
    var results by mutableStateOf<List<Int>>(emptyList())
        private set
    
    var total by mutableIntStateOf(0)
        private set
    
    var history by mutableStateOf<List<RollResult>>(emptyList())
        private set
    
    var isRolling by mutableStateOf(false)
        private set
    
    fun selectDice(type: DiceType) {
        selectedDice = type
        results = emptyList()
        total = 0
    }
    
    fun setDiceCount(count: Int) {
        diceCount = count.coerceIn(1, 20)
    }
    
    fun roll() {
        isRolling = true
        
        results = (1..diceCount).map {
            random.nextInt(selectedDice.sides) + 1
        }
        total = results.sum()
        
        // Add to history
        history = listOf(
            RollResult(
                diceType = selectedDice,
                count = diceCount,
                results = results,
                total = total
            )
        ) + history.take(9) // Keep last 10 rolls
        
        isRolling = false
    }
    
    fun clearHistory() {
        history = emptyList()
    }
    
    fun clearResults() {
        results = emptyList()
        total = 0
    }
}

enum class DiceType(val sides: Int, val displayName: String) {
    D4(4, "D4"),
    D6(6, "D6"),
    D8(8, "D8"),
    D10(10, "D10"),
    D12(12, "D12"),
    D20(20, "D20"),
    D100(100, "D100")
}

data class RollResult(
    val diceType: DiceType,
    val count: Int,
    val results: List<Int>,
    val total: Int
)
