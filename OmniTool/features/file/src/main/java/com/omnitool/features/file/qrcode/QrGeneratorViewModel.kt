package com.omnitool.features.file.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * QR Code Generator ViewModel
 * 
 * Features:
 * - Generate QR codes from text/URLs
 * - Customizable size
 * - Support for various content types
 */
@HiltViewModel
class QrGeneratorViewModel @Inject constructor() : ViewModel() {
    
    var inputText by mutableStateOf("")
        private set
    
    var qrBitmap by mutableStateOf<ImageBitmap?>(null)
        private set
    
    var isGenerating by mutableStateOf(false)
        private set
    
    var qrSize by mutableIntStateOf(512)
        private set
    
    var contentType by mutableStateOf(QrContentType.TEXT)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // Pre-filled fields for structured content
    var urlInput by mutableStateOf("")
        private set
    var phoneInput by mutableStateOf("")
        private set
    var emailInput by mutableStateOf("")
        private set
    var emailSubject by mutableStateOf("")
        private set
    var wifiSsid by mutableStateOf("")
        private set
    var wifiPassword by mutableStateOf("")
        private set
    var wifiType by mutableStateOf(WifiType.WPA)
        private set
    
    fun updateInput(text: String) {
        inputText = text
        errorMessage = null
    }
    
    fun updateUrl(url: String) {
        urlInput = url
    }
    
    fun updatePhone(phone: String) {
        phoneInput = phone.filter { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }
    
    fun updateEmail(email: String) {
        emailInput = email
    }
    
    fun updateEmailSubject(subject: String) {
        emailSubject = subject
    }
    
    fun updateWifiSsid(ssid: String) {
        wifiSsid = ssid
    }
    
    fun updateWifiPassword(password: String) {
        wifiPassword = password
    }
    
    fun setWifiType(type: WifiType) {
        wifiType = type
    }
    
    fun setContentType(type: QrContentType) {
        contentType = type
        qrBitmap = null
        errorMessage = null
    }
    
    fun setSize(size: Int) {
        qrSize = size.coerceIn(128, 1024)
    }
    
    fun generate() {
        val content = when (contentType) {
            QrContentType.TEXT -> inputText
            QrContentType.URL -> {
                val url = urlInput.trim()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    "https://$url"
                } else url
            }
            QrContentType.PHONE -> "tel:${phoneInput.filter { it.isDigit() || it == '+' }}"
            QrContentType.EMAIL -> {
                if (emailSubject.isNotEmpty()) {
                    "mailto:$emailInput?subject=${emailSubject.replace(" ", "%20")}"
                } else {
                    "mailto:$emailInput"
                }
            }
            QrContentType.WIFI -> "WIFI:T:${wifiType.code};S:$wifiSsid;P:$wifiPassword;;"
        }
        
        if (content.isEmpty()) {
            errorMessage = "Please enter content to generate QR code"
            return
        }
        
        viewModelScope.launch {
            isGenerating = true
            errorMessage = null
            
            try {
                val bitmap = withContext(Dispatchers.Default) {
                    generateQrBitmap(content, qrSize)
                }
                qrBitmap = bitmap?.asImageBitmap()
                
                if (qrBitmap == null) {
                    errorMessage = "Failed to generate QR code"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isGenerating = false
            }
        }
    }
    
    private fun generateQrBitmap(content: String, size: Int): Bitmap? {
        // Simple QR code generation using a basic algorithm
        // For production, you would use ZXing library
        return try {
            val qrCodeWriter = SimpleQrEncoder()
            qrCodeWriter.encode(content, size)
        } catch (e: Exception) {
            null
        }
    }
    
    fun clear() {
        inputText = ""
        urlInput = ""
        phoneInput = ""
        emailInput = ""
        emailSubject = ""
        wifiSsid = ""
        wifiPassword = ""
        qrBitmap = null
        errorMessage = null
    }
}

enum class QrContentType(val displayName: String) {
    TEXT("Text"),
    URL("URL"),
    PHONE("Phone"),
    EMAIL("Email"),
    WIFI("WiFi")
}

enum class WifiType(val code: String, val displayName: String) {
    WPA("WPA", "WPA/WPA2"),
    WEP("WEP", "WEP"),
    OPEN("nopass", "Open")
}

/**
 * Simple QR code encoder
 * For real production use, integrate ZXing or similar library
 */
class SimpleQrEncoder {
    fun encode(content: String, size: Int): Bitmap {
        // Generate a simple data matrix pattern for demonstration
        // Real implementation would use proper QR encoding
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        val moduleCount = 25 // Standard QR size for small data
        val moduleSize = size / moduleCount
        
        // Generate deterministic pattern based on content hash
        val hash = content.hashCode()
        val pattern = generatePattern(content, moduleCount)
        
        for (row in 0 until moduleCount) {
            for (col in 0 until moduleCount) {
                val color = if (pattern[row][col]) Color.BLACK else Color.WHITE
                
                // Fill module
                for (y in row * moduleSize until (row + 1) * moduleSize) {
                    for (x in col * moduleSize until (col + 1) * moduleSize) {
                        if (x < size && y < size) {
                            bitmap.setPixel(x, y, color)
                        }
                    }
                }
            }
        }
        
        return bitmap
    }
    
    private fun generatePattern(content: String, size: Int): Array<BooleanArray> {
        val pattern = Array(size) { BooleanArray(size) }
        val hash = content.hashCode()
        
        // Add finder patterns (corners)
        addFinderPattern(pattern, 0, 0)
        addFinderPattern(pattern, 0, size - 7)
        addFinderPattern(pattern, size - 7, 0)
        
        // Add timing patterns
        for (i in 8 until size - 8) {
            pattern[6][i] = i % 2 == 0
            pattern[i][6] = i % 2 == 0
        }
        
        // Fill data area with content-based pattern
        var bitIndex = 0
        val contentBytes = content.toByteArray()
        
        for (row in 8 until size - 8) {
            for (col in 8 until size - 8) {
                if (row == 6 || col == 6) continue
                
                val byteIndex = bitIndex / 8
                val bitOffset = bitIndex % 8
                
                if (byteIndex < contentBytes.size) {
                    pattern[row][col] = ((contentBytes[byteIndex].toInt() shr (7 - bitOffset)) and 1) == 1
                } else {
                    // Fill remaining with hash-based pattern
                    pattern[row][col] = ((hash xor (row * 31 + col)) and (1 shl (bitOffset))) != 0
                }
                bitIndex++
            }
        }
        
        return pattern
    }
    
    private fun addFinderPattern(pattern: Array<BooleanArray>, startRow: Int, startCol: Int) {
        for (row in 0 until 7) {
            for (col in 0 until 7) {
                val isOuter = row == 0 || row == 6 || col == 0 || col == 6
                val isInner = row in 2..4 && col in 2..4
                pattern[startRow + row][startCol + col] = isOuter || isInner
            }
        }
    }
}
