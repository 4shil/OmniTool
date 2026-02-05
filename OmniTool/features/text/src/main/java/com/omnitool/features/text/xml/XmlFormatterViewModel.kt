package com.omnitool.features.text.xml

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.io.StringWriter
import javax.inject.Inject

/**
 * XML Formatter Tool ViewModel
 */
@HiltViewModel
class XmlFormatterViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var outputText by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isValid by mutableStateOf<Boolean?>(null)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        errorMessage = null
        isValid = null
    }
    
    fun formatXml(indent: Int = 2) {
        viewModelScope.launch {
            try {
                if (inputText.isEmpty()) {
                    errorMessage = "Please enter XML to format"
                    outputText = ""
                    isValid = null
                    return@launch
                }
                
                outputText = withContext(Dispatchers.Default) {
                    prettyPrintXml(inputText, indent)
                }
                errorMessage = null
                isValid = true
            } catch (e: Exception) {
                outputText = ""
                errorMessage = parseXmlError(e.message ?: "Invalid XML")
                isValid = false
            }
        }
    }
    
    fun minifyXml() {
        viewModelScope.launch {
            try {
                if (inputText.isEmpty()) {
                    errorMessage = "Please enter XML to minify"
                    outputText = ""
                    return@launch
                }
                
                outputText = withContext(Dispatchers.Default) {
                    inputText.lines()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString("")
                        .replace(Regex(">\\s+<"), "><")
                }
                errorMessage = null
                isValid = true
            } catch (e: Exception) {
                outputText = ""
                errorMessage = parseXmlError(e.message ?: "Invalid XML")
                isValid = false
            }
        }
    }
    
    fun validateXml() {
        viewModelScope.launch {
            try {
                if (inputText.isEmpty()) {
                    errorMessage = "Please enter XML to validate"
                    isValid = null
                    return@launch
                }
                
                withContext(Dispatchers.Default) {
                    val factory = XmlPullParserFactory.newInstance()
                    val parser = factory.newPullParser()
                    parser.setInput(StringReader(inputText))
                    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                        parser.next()
                    }
                }
                
                outputText = "✓ Valid XML"
                errorMessage = null
                isValid = true
            } catch (e: Exception) {
                outputText = ""
                errorMessage = parseXmlError(e.message ?: "Invalid XML")
                isValid = false
            }
        }
    }
    
    private fun prettyPrintXml(xml: String, indent: Int): String {
        val lines = mutableListOf<String>()
        var depth = 0
        val indentStr = " ".repeat(indent)
        
        // Simple regex-based formatting
        val tagPattern = Regex("<[^>]+>|[^<]+")
        val matches = tagPattern.findAll(xml.replace(Regex(">\\s+<"), "><"))
        
        for (match in matches) {
            val token = match.value.trim()
            if (token.isEmpty()) continue
            
            when {
                token.startsWith("</") -> {
                    depth--
                    lines.add(indentStr.repeat(depth) + token)
                }
                token.startsWith("<?") || token.startsWith("<!") -> {
                    lines.add(indentStr.repeat(depth) + token)
                }
                token.endsWith("/>") -> {
                    lines.add(indentStr.repeat(depth) + token)
                }
                token.startsWith("<") -> {
                    lines.add(indentStr.repeat(depth) + token)
                    depth++
                }
                else -> {
                    if (lines.isNotEmpty()) {
                        lines[lines.lastIndex] = lines.last() + token
                    } else {
                        lines.add(token)
                    }
                }
            }
        }
        
        return lines.joinToString("\n")
    }
    
    private fun parseXmlError(error: String): String {
        return when {
            error.contains("Unexpected end of document") -> 
                "XML is incomplete - missing closing tags"
            error.contains("not well-formed") ->
                "XML is not well-formed - check tag structure"
            else -> error
        }
    }
    
    fun clearAll() {
        inputText = ""
        outputText = ""
        errorMessage = null
        isValid = null
    }
}
