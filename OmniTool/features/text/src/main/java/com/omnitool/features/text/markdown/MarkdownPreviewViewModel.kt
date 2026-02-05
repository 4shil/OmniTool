package com.omnitool.features.text.markdown

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Markdown Preview Tool ViewModel
 * Converts markdown to styled preview
 */
@HiltViewModel
class MarkdownPreviewViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var previewHtml by mutableStateOf("")
        private set
    
    fun updateInput(text: String) {
        inputText = text
        previewHtml = convertToHtml(text)
    }
    
    private fun convertToHtml(markdown: String): String {
        var html = markdown
        
        // Headers
        html = html.replace(Regex("^### (.+)$", RegexOption.MULTILINE), "<h3>$1</h3>")
        html = html.replace(Regex("^## (.+)$", RegexOption.MULTILINE), "<h2>$1</h2>")
        html = html.replace(Regex("^# (.+)$", RegexOption.MULTILINE), "<h1>$1</h1>")
        
        // Bold and Italic
        html = html.replace(Regex("\\*\\*\\*(.+?)\\*\\*\\*"), "<b><i>$1</i></b>")
        html = html.replace(Regex("\\*\\*(.+?)\\*\\*"), "<b>$1</b>")
        html = html.replace(Regex("\\*(.+?)\\*"), "<i>$1</i>")
        html = html.replace(Regex("__(.+?)__"), "<b>$1</b>")
        html = html.replace(Regex("_(.+?)_"), "<i>$1</i>")
        
        // Code
        html = html.replace(Regex("```([^`]+)```"), "<pre><code>$1</code></pre>")
        html = html.replace(Regex("`([^`]+)`"), "<code>$1</code>")
        
        // Links
        html = html.replace(Regex("\\[(.+?)\\]\\((.+?)\\)"), "<a href=\"$2\">$1</a>")
        
        // Lists
        html = html.replace(Regex("^- (.+)$", RegexOption.MULTILINE), "<li>$1</li>")
        html = html.replace(Regex("^\\* (.+)$", RegexOption.MULTILINE), "<li>$1</li>")
        html = html.replace(Regex("^\\d+\\. (.+)$", RegexOption.MULTILINE), "<li>$1</li>")
        
        // Blockquotes
        html = html.replace(Regex("^> (.+)$", RegexOption.MULTILINE), "<blockquote>$1</blockquote>")
        
        // Horizontal rules
        html = html.replace(Regex("^---$", RegexOption.MULTILINE), "<hr>")
        html = html.replace(Regex("^\\*\\*\\*$", RegexOption.MULTILINE), "<hr>")
        
        // Line breaks
        html = html.replace("\n\n", "<br><br>")
        
        return html
    }
    
    fun clearAll() {
        inputText = ""
        previewHtml = ""
    }
}
