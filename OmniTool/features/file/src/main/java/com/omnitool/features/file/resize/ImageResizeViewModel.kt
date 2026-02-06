package com.omnitool.features.file.resize

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Image Resize ViewModel
 * 
 * Features:
 * - Resize by percentage or dimensions
 * - Maintain aspect ratio
 * - Multiple presets
 * - Preview before/after
 */
@HiltViewModel
class ImageResizeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    
    var originalBitmap by mutableStateOf<Bitmap?>(null)
        private set
    
    var resizedBitmap by mutableStateOf<Bitmap?>(null)
        private set
    
    var originalWidth by mutableIntStateOf(0)
        private set
    
    var originalHeight by mutableIntStateOf(0)
        private set
    
    var targetWidth by mutableIntStateOf(0)
        private set
    
    var targetHeight by mutableIntStateOf(0)
        private set
    
    var maintainAspectRatio by mutableStateOf(true)
        private set
    
    var resizeMode by mutableStateOf(ResizeMode.PERCENTAGE)
        private set
    
    var resizePercent by mutableIntStateOf(50)
        private set
    
    var isProcessing by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    private val aspectRatio: Float
        get() = if (originalHeight > 0) originalWidth.toFloat() / originalHeight else 1f
    
    fun setImageUri(uri: Uri) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = null
            
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        originalBitmap = bitmap
                        originalWidth = bitmap.width
                        originalHeight = bitmap.height
                        targetWidth = bitmap.width
                        targetHeight = bitmap.height
                    }
                }
                selectedImageUri = uri
                applyResize()
            } catch (e: Exception) {
                errorMessage = "Failed to load image: ${e.message}"
            } finally {
                isProcessing = false
            }
        }
    }
    
    fun setResizeMode(mode: ResizeMode) {
        resizeMode = mode
        applyResize()
    }
    
    fun setResizePercent(percent: Int) {
        resizePercent = percent.coerceIn(1, 200)
        applyResize()
    }
    
    fun setTargetWidth(width: Int) {
        targetWidth = width.coerceAtLeast(1)
        if (maintainAspectRatio && originalWidth > 0) {
            targetHeight = (width / aspectRatio).toInt().coerceAtLeast(1)
        }
        applyResize()
    }
    
    fun setTargetHeight(height: Int) {
        targetHeight = height.coerceAtLeast(1)
        if (maintainAspectRatio && originalHeight > 0) {
            targetWidth = (height * aspectRatio).toInt().coerceAtLeast(1)
        }
        applyResize()
    }
    
    fun setMaintainAspectRatio(maintain: Boolean) {
        maintainAspectRatio = maintain
        if (maintain) {
            setTargetWidth(targetWidth) // Recalculate height
        }
    }
    
    fun applyPreset(preset: ResizePreset) {
        resizeMode = ResizeMode.DIMENSIONS
        targetWidth = preset.width
        targetHeight = preset.height
        maintainAspectRatio = false
        applyResize()
    }
    
    private fun applyResize() {
        val bitmap = originalBitmap ?: return
        
        viewModelScope.launch {
            isProcessing = true
            
            try {
                val newWidth: Int
                val newHeight: Int
                
                when (resizeMode) {
                    ResizeMode.PERCENTAGE -> {
                        newWidth = (bitmap.width * resizePercent / 100).coerceAtLeast(1)
                        newHeight = (bitmap.height * resizePercent / 100).coerceAtLeast(1)
                        targetWidth = newWidth
                        targetHeight = newHeight
                    }
                    ResizeMode.DIMENSIONS -> {
                        newWidth = targetWidth
                        newHeight = targetHeight
                    }
                }
                
                withContext(Dispatchers.Default) {
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                }
            } catch (e: Exception) {
                errorMessage = "Resize failed: ${e.message}"
            } finally {
                isProcessing = false
            }
        }
    }
    
    fun clear() {
        selectedImageUri = null
        originalBitmap = null
        resizedBitmap = null
        originalWidth = 0
        originalHeight = 0
        targetWidth = 0
        targetHeight = 0
        resizePercent = 50
        errorMessage = null
    }
    
    fun formatDimensions(width: Int, height: Int): String {
        return "${width} × ${height}"
    }
}

enum class ResizeMode(val displayName: String) {
    PERCENTAGE("Percentage"),
    DIMENSIONS("Dimensions")
}

enum class ResizePreset(val displayName: String, val width: Int, val height: Int) {
    ICON_32("32×32", 32, 32),
    ICON_64("64×64", 64, 64),
    ICON_128("128×128", 128, 128),
    ICON_256("256×256", 256, 256),
    THUMB_150("Thumbnail", 150, 150),
    SOCIAL_1080("Social 1080p", 1080, 1080),
    HD_720("720p", 1280, 720),
    HD_1080("1080p", 1920, 1080)
}
