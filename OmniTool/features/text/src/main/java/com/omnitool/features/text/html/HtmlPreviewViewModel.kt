package com.omnitool.features.text.html

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HTML Preview Tool ViewModel
 * Live HTML to rendered preview
 */
@HiltViewModel
class HtmlPreviewViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var showPreview by mutableStateOf(true)
        private set
    
    fun updateInput(text: String) {
        inputText = text
    }
    
    fun toggleView() {
        showPreview = !showPreview
    }
    
    fun clearAll() {
        inputText = ""
    }
    
    // Strip dangerous tags for safety
    fun getSafeHtml(): String {
        return inputText
            .replace(Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<style[^>]*>.*?</style>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("on\\w+\\s*=", RegexOption.IGNORE_CASE), "data-removed=")
    }
    
    fun insertTemplate() {
        inputText = """<!DOCTYPE html>
<html>
<head>
    <title>My Page</title>
</head>
<body>
    <h1>Hello World</h1>
    <p>Start editing your HTML here...</p>
</body>
</html>"""
    }
}
