package com.omnitool.features.file.rename

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Batch Rename ViewModel
 * 
 * Features:
 * - Pattern-based renaming
 * - Find and replace
 * - Numbering options
 * - Preview before apply
 */
@HiltViewModel
class BatchRenameViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var selectedFiles by mutableStateOf<List<FileItem>>(emptyList())
        private set
    
    var previewFiles by mutableStateOf<List<RenamePreview>>(emptyList())
        private set
    
    var renameMode by mutableStateOf(RenameMode.FIND_REPLACE)
        private set
    
    var findText by mutableStateOf("")
        private set
    
    var replaceText by mutableStateOf("")
        private set
    
    var prefix by mutableStateOf("")
        private set
    
    var suffix by mutableStateOf("")
        private set
    
    var startNumber by mutableIntStateOf(1)
        private set
    
    var numberPadding by mutableIntStateOf(2)
        private set
    
    var caseMode by mutableStateOf(CaseMode.NONE)
        private set
    
    fun addFiles(uris: List<Uri>) {
        val newFiles = uris.mapNotNull { uri ->
            getFileName(uri)?.let { name ->
                FileItem(uri = uri, originalName = name)
            }
        }
        selectedFiles = selectedFiles + newFiles
        updatePreview()
    }
    
    fun removeFile(file: FileItem) {
        selectedFiles = selectedFiles.filter { it.uri != file.uri }
        updatePreview()
    }
    
    fun clearFiles() {
        selectedFiles = emptyList()
        previewFiles = emptyList()
    }
    
    fun setMode(mode: RenameMode) {
        renameMode = mode
        updatePreview()
    }
    
    fun setFindText(text: String) {
        findText = text
        updatePreview()
    }
    
    fun setReplaceText(text: String) {
        replaceText = text
        updatePreview()
    }
    
    fun setPrefix(text: String) {
        prefix = text
        updatePreview()
    }
    
    fun setSuffix(text: String) {
        suffix = text
        updatePreview()
    }
    
    fun setStartNumber(num: Int) {
        startNumber = num.coerceAtLeast(0)
        updatePreview()
    }
    
    fun setPadding(padding: Int) {
        numberPadding = padding.coerceIn(1, 5)
        updatePreview()
    }
    
    fun setCaseMode(mode: CaseMode) {
        caseMode = mode
        updatePreview()
    }
    
    private fun updatePreview() {
        previewFiles = selectedFiles.mapIndexed { index, file ->
            val newName = generateNewName(file.originalName, index)
            RenamePreview(
                original = file.originalName,
                newName = newName,
                hasChange = file.originalName != newName
            )
        }
    }
    
    private fun generateNewName(originalName: String, index: Int): String {
        val extension = originalName.substringAfterLast('.', "")
        val baseName = if (extension.isNotEmpty()) {
            originalName.dropLast(extension.length + 1)
        } else {
            originalName
        }
        
        var newName = baseName
        
        // Apply transformations based on mode
        when (renameMode) {
            RenameMode.FIND_REPLACE -> {
                if (findText.isNotEmpty()) {
                    newName = newName.replace(findText, replaceText, ignoreCase = true)
                }
            }
            RenameMode.PREFIX_SUFFIX -> {
                newName = prefix + newName + suffix
            }
            RenameMode.NUMBERING -> {
                val num = startNumber + index
                val paddedNum = num.toString().padStart(numberPadding, '0')
                newName = "${paddedNum}_${newName}"
            }
        }
        
        // Apply case transformation
        newName = when (caseMode) {
            CaseMode.NONE -> newName
            CaseMode.LOWERCASE -> newName.lowercase()
            CaseMode.UPPERCASE -> newName.uppercase()
            CaseMode.TITLE_CASE -> newName.split(" ").joinToString(" ") { 
                it.lowercase().replaceFirstChar { c -> c.uppercase() }
            }
        }
        
        // Add extension back
        return if (extension.isNotEmpty()) "$newName.$extension" else newName
    }
    
    private fun getFileName(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (nameIndex >= 0) cursor.getString(nameIndex) else null
        }
    }
}

data class FileItem(
    val uri: Uri,
    val originalName: String
)

data class RenamePreview(
    val original: String,
    val newName: String,
    val hasChange: Boolean
)

enum class RenameMode(val displayName: String) {
    FIND_REPLACE("Find & Replace"),
    PREFIX_SUFFIX("Prefix/Suffix"),
    NUMBERING("Numbering")
}

enum class CaseMode(val displayName: String) {
    NONE("No change"),
    LOWERCASE("lowercase"),
    UPPERCASE("UPPERCASE"),
    TITLE_CASE("Title Case")
}
