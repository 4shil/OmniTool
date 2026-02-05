package com.omnitool.features.text.lorem

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

/**
 * Lorem Ipsum Generator Tool ViewModel
 */
@HiltViewModel
class LoremIpsumViewModel @Inject constructor() : ViewModel() {
    
    var outputText by mutableStateOf("")
        private set
    
    var paragraphCount by mutableStateOf(3)
        private set
    
    var generationType by mutableStateOf(GenerationType.PARAGRAPHS)
        private set
    
    fun setType(type: GenerationType) {
        generationType = type
    }
    
    fun setCount(count: Int) {
        paragraphCount = count.coerceIn(1, 20)
    }
    
    fun generate() {
        outputText = when (generationType) {
            GenerationType.PARAGRAPHS -> generateParagraphs(paragraphCount)
            GenerationType.SENTENCES -> generateSentences(paragraphCount)
            GenerationType.WORDS -> generateWords(paragraphCount * 10)
        }
    }
    
    private fun generateParagraphs(count: Int): String {
        return (1..count).joinToString("\n\n") {
            generateParagraph()
        }
    }
    
    private fun generateSentences(count: Int): String {
        return (1..count).joinToString(" ") {
            generateSentence()
        }
    }
    
    private fun generateWords(count: Int): String {
        return (1..count).map { WORDS.random() }.joinToString(" ")
    }
    
    private fun generateParagraph(): String {
        val sentenceCount = Random.nextInt(4, 8)
        val sentences = (1..sentenceCount).map { generateSentence() }
        return sentences.joinToString(" ")
    }
    
    private fun generateSentence(): String {
        val wordCount = Random.nextInt(8, 16)
        val words = (1..wordCount).map { WORDS.random() }
        return words.joinToString(" ").replaceFirstChar { it.uppercase() } + "."
    }
    
    fun clearAll() {
        outputText = ""
    }
    
    companion object {
        private val WORDS = listOf(
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
            "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
            "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud",
            "exercitation", "ullamco", "laboris", "nisi", "aliquip", "ex", "ea", "commodo",
            "consequat", "duis", "aute", "irure", "in", "reprehenderit", "voluptate",
            "velit", "esse", "cillum", "fugiat", "nulla", "pariatur", "excepteur", "sint",
            "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui", "officia",
            "deserunt", "mollit", "anim", "id", "est", "laborum", "cras", "fermentum",
            "odio", "eu", "feugiat", "pretium", "nibh", "mauris", "condimentum", "mattis",
            "pellentesque", "pulvinar", "elementum", "integer", "enim", "neque", "volutpat",
            "ac", "tincidunt", "vitae", "semper", "quis", "lectus", "nulla", "at", "volutpat"
        )
    }
}

enum class GenerationType(val displayName: String) {
    PARAGRAPHS("Paragraphs"),
    SENTENCES("Sentences"),
    WORDS("Words")
}
