package com.omnitool.features.text.regex

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

/**
 * Regex Tester Tool ViewModel
 */
@HiltViewModel
class RegexTesterViewModel @Inject constructor() : ViewModel() {
    
    var pattern by mutableStateOf("")
        private set
    
    var testString by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var matches by mutableStateOf<List<MatchResult>>(emptyList())
        private set
    
    var isMatch by mutableStateOf<Boolean?>(null)
        private set
    
    var flags by mutableStateOf(RegexFlags())
        private set
    
    fun updatePattern(newPattern: String) {
        pattern = newPattern
        testRegex()
    }
    
    fun updateTestString(text: String) {
        testString = text
        testRegex()
    }
    
    fun updateFlags(newFlags: RegexFlags) {
        flags = newFlags
        testRegex()
    }
    
    private fun testRegex() {
        if (pattern.isEmpty()) {
            matches = emptyList()
            isMatch = null
            errorMessage = null
            return
        }
        
        try {
            var flagsInt = 0
            if (flags.caseInsensitive) flagsInt = flagsInt or Pattern.CASE_INSENSITIVE
            if (flags.multiline) flagsInt = flagsInt or Pattern.MULTILINE
            if (flags.dotAll) flagsInt = flagsInt or Pattern.DOTALL
            
            val compiledPattern = Pattern.compile(pattern, flagsInt)
            val matcher = compiledPattern.matcher(testString)
            
            val foundMatches = mutableListOf<MatchResult>()
            while (matcher.find()) {
                foundMatches.add(
                    MatchResult(
                        value = matcher.group(),
                        start = matcher.start(),
                        end = matcher.end(),
                        groups = (0..matcher.groupCount()).mapNotNull { 
                            matcher.group(it) 
                        }
                    )
                )
            }
            
            matches = foundMatches
            isMatch = foundMatches.isNotEmpty()
            errorMessage = null
        } catch (e: PatternSyntaxException) {
            matches = emptyList()
            isMatch = null
            errorMessage = "Invalid regex: ${e.description}"
        }
    }
    
    fun clearAll() {
        pattern = ""
        testString = ""
        matches = emptyList()
        isMatch = null
        errorMessage = null
    }
}

data class MatchResult(
    val value: String,
    val start: Int,
    val end: Int,
    val groups: List<String>
)

data class RegexFlags(
    val caseInsensitive: Boolean = false,
    val multiline: Boolean = false,
    val dotAll: Boolean = false
)
