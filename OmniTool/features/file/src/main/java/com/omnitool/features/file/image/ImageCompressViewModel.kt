package com.omnitool.features.file.image

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
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Image Compression ViewModel
 * 
 * Features:
 * - Quality slider
 * - Size preview
 * - Format selection
 * - Before/after comparison
 */
@HiltViewModel
class ImageCompressViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    
    var originalSize by mutableLongStateOf(0L)
        private set
    
    var compressedSize by mutableLongStateOf(0L)
        private set
    
    var quality by mutableIntStateOf(80)
        private set
    
    var outputFormat by mutableStateOf(ImageFormat.JPEG)
        private set
    
    var isProcessing by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var compressionResult by mutableStateOf<ByteArray?>(null)
        private set
    
    var originalBitmap by mutableStateOf<Bitmap?>(null)
        private set
    
    val compressionRatio: Float
        get() = if (originalSize > 0) {
            ((originalSize - compressedSize).toFloat() / originalSize * 100)
        } else 0f
    
    fun setImageUri(uri: Uri) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = null
            
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        originalSize = stream.available().toLong()
                    }
                    
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        originalBitmap = BitmapFactory.decodeStream(stream)
                    }
                }
                
                selectedImageUri = uri
                compressImage()
            } catch (e: Exception) {
                errorMessage = "Failed to load image: ${e.message}"
            } finally {
                isProcessing = false
            }
        }
    }
    
    fun setQuality(value: Int) {
        quality = value.coerceIn(1, 100)
        if (originalBitmap != null) {
            compressImage()
        }
    }
    
    fun setFormat(format: ImageFormat) {
        outputFormat = format
        if (originalBitmap != null) {
            compressImage()
        }
    }
    
    private fun compressImage() {
        val bitmap = originalBitmap ?: return
        
        viewModelScope.launch {
            isProcessing = true
            
            try {
                withContext(Dispatchers.Default) {
                    val outputStream = ByteArrayOutputStream()
                    
                    val compressFormat = when (outputFormat) {
                        ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
                        ImageFormat.PNG -> Bitmap.CompressFormat.PNG
                        ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP_LOSSY
                    }
                    
                    // PNG doesn't use quality parameter
                    val effectiveQuality = if (outputFormat == ImageFormat.PNG) 100 else quality
                    
                    bitmap.compress(compressFormat, effectiveQuality, outputStream)
                    
                    compressionResult = outputStream.toByteArray()
                    compressedSize = compressionResult!!.size.toLong()
                }
            } catch (e: Exception) {
                errorMessage = "Compression failed: ${e.message}"
            } finally {
                isProcessing = false
            }
        }
    }
    
    fun clear() {
        selectedImageUri = null
        originalBitmap = null
        originalSize = 0
        compressedSize = 0
        compressionResult = null
        errorMessage = null
    }
    
    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${String.format("%.1f", bytes / 1024f)} KB"
            else -> "${String.format("%.2f", bytes / (1024f * 1024f))} MB"
        }
    }
}

enum class ImageFormat(val displayName: String, val extension: String) {
    JPEG("JPEG", "jpg"),
    PNG("PNG", "png"),
    WEBP("WebP", "webp")
}
