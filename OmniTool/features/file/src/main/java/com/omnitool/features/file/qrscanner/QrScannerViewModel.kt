package com.omnitool.features.file.qrscanner

import android.content.Context
import android.graphics.ImageFormat
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.ByteBuffer
import javax.inject.Inject

/**
 * QR Scanner ViewModel
 * 
 * Features:
 * - Real-time QR code scanning
 * - Camera preview
 * - Scan history
 * - Copy/Share results
 */
@HiltViewModel
class QrScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var isScanning by mutableStateOf(false)
        private set
    
    var scannedResult by mutableStateOf<String?>(null)
        private set
    
    var scanHistory by mutableStateOf<List<ScanResult>>(emptyList())
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var needsPermission by mutableStateOf(true)
        private set
    
    var isFlashOn by mutableStateOf(false)
        private set
    
    private val multiFormatReader = MultiFormatReader().apply {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E
            )
        )
        setHints(hints)
    }
    
    fun setPermissionGranted(granted: Boolean) {
        needsPermission = !granted
    }
    
    fun startScanning() {
        if (!needsPermission) {
            isScanning = true
            scannedResult = null
            errorMessage = null
        }
    }
    
    fun stopScanning() {
        isScanning = false
    }
    
    fun processImage(imageProxy: ImageProxy) {
        try {
            val buffer = imageProxy.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            
            val source = PlanarYUVLuminanceSource(
                bytes,
                imageProxy.width,
                imageProxy.height,
                0,
                0,
                imageProxy.width,
                imageProxy.height,
                false
            )
            
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            
            try {
                val result = multiFormatReader.decodeWithState(binaryBitmap)
                handleScanResult(result.text, result.barcodeFormat.name)
            } catch (e: NotFoundException) {
                // No QR code found in this frame - this is normal
            } catch (e: Exception) {
                // Other decode errors
            }
        } catch (e: Exception) {
            errorMessage = "Scan error: ${e.message}"
        } finally {
            imageProxy.close()
            multiFormatReader.reset()
        }
    }
    
    private fun handleScanResult(content: String, format: String) {
        if (content != scannedResult) {
            scannedResult = content
            isScanning = false
            
            val scanResult = ScanResult(
                content = content,
                format = format,
                timestamp = System.currentTimeMillis(),
                type = detectContentType(content)
            )
            
            scanHistory = listOf(scanResult) + scanHistory.take(19) // Keep last 20
        }
    }
    
    private fun detectContentType(content: String): ContentType {
        return when {
            content.startsWith("http://") || content.startsWith("https://") -> ContentType.URL
            content.startsWith("tel:") || content.matches(Regex("^\\+?[0-9]{10,15}$")) -> ContentType.PHONE
            content.startsWith("mailto:") || content.contains("@") -> ContentType.EMAIL
            content.startsWith("WIFI:") -> ContentType.WIFI
            content.startsWith("BEGIN:VCARD") -> ContentType.CONTACT
            else -> ContentType.TEXT
        }
    }
    
    fun toggleFlash() {
        isFlashOn = !isFlashOn
    }
    
    fun clearResult() {
        scannedResult = null
        isScanning = true
    }
    
    fun clearHistory() {
        scanHistory = emptyList()
    }
}

data class ScanResult(
    val content: String,
    val format: String,
    val timestamp: Long,
    val type: ContentType
)

enum class ContentType(val displayName: String) {
    URL("Website"),
    PHONE("Phone"),
    EMAIL("Email"),
    WIFI("WiFi"),
    CONTACT("Contact"),
    TEXT("Text")
}
