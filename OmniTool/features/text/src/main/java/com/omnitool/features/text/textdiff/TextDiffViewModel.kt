package com.omnitool.features.text.textdiff

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Text Diff Compare Tool ViewModel
 */
@HiltViewModel
class TextDiffViewModel @Inject constructor() : ViewModel() {
    
    var textA by mutableStateOf("")
        private set
    
    var textB by mutableStateOf("")
        private set
    
    var diffResult by mutableStateOf<List<DiffLine>>(emptyList())
        private set
    
    var stats by mutableStateOf<DiffStats?>(null)
        private set
    
    fun updateTextA(text: String) {
        textA = text
    }
    
    fun updateTextB(text: String) {
        textB = text
    }
    
    fun compare() {
        val linesA = textA.lines()
        val linesB = textB.lines()
        
        val result = mutableListOf<DiffLine>()
        var additions = 0
        var deletions = 0
        var unchanged = 0
        
        // Simple line-by-line diff algorithm
        val maxLines = maxOf(linesA.size, linesB.size)
        
        for (i in 0 until maxLines) {
            val lineA = linesA.getOrNull(i)
            val lineB = linesB.getOrNull(i)
            
            when {
                lineA == lineB -> {
                    if (lineA != null) {
                        result.add(DiffLine(lineA, DiffType.UNCHANGED))
                        unchanged++
                    }
                }
                lineA == null -> {
                    result.add(DiffLine(lineB!!, DiffType.ADDED))
                    additions++
                }
                lineB == null -> {
                    result.add(DiffLine(lineA, DiffType.REMOVED))
                    deletions++
                }
                else -> {
                    result.add(DiffLine(lineA, DiffType.REMOVED))
                    result.add(DiffLine(lineB, DiffType.ADDED))
                    additions++
                    deletions++
                }
            }
        }
        
        diffResult = result
        stats = DiffStats(
            additions = additions,
            deletions = deletions,
            unchanged = unchanged,
            similarityPercent = if (maxLines > 0) (unchanged * 100 / maxLines) else 100
        )
    }
    
    fun swapTexts() {
        val temp = textA
        textA = textB
        textB = temp
        if (diffResult.isNotEmpty()) {
            compare()
        }
    }
    
    fun clearAll() {
        textA = ""
        textB = ""
        diffResult = emptyList()
        stats = null
    }
}

data class DiffLine(
    val content: String,
    val type: DiffType
)

enum class DiffType {
    ADDED,
    REMOVED,
    UNCHANGED
}

data class DiffStats(
    val additions: Int,
    val deletions: Int,
    val unchanged: Int,
    val similarityPercent: Int
)
