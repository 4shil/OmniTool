package com.omnitool.features.converter.color

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Color Format Converter Tool ViewModel
 * Converts between HEX, RGB, HSL, HSV
 */
@HiltViewModel
class ColorConverterViewModel @Inject constructor() : ViewModel() {
    
    var hexInput by mutableStateOf("#FF5733")
        private set
    
    var previewColor by mutableStateOf(Color(0xFFFF5733))
        private set
    
    var rgbResult by mutableStateOf("")
        private set
    
    var hslResult by mutableStateOf("")
        private set
    
    var hsvResult by mutableStateOf("")
        private set
    
    var cmykResult by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    init {
        convert()
    }
    
    fun updateHex(value: String) {
        hexInput = value.uppercase()
        convert()
    }
    
    fun setFromRgb(r: Int, g: Int, b: Int) {
        hexInput = String.format("#%02X%02X%02X", r, g, b)
        convert()
    }
    
    private fun convert() {
        try {
            val hex = hexInput.let {
                if (it.startsWith("#")) it else "#$it"
            }
            
            if (!hex.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errorMessage = "Enter a valid 6-digit hex color"
                return
            }
            
            val colorInt = hex.toColorInt()
            val r = (colorInt shr 16) and 0xFF
            val g = (colorInt shr 8) and 0xFF
            val b = colorInt and 0xFF
            
            previewColor = Color(colorInt)
            rgbResult = "rgb($r, $g, $b)"
            
            // HSL
            val hsl = rgbToHsl(r, g, b)
            hslResult = "hsl(${hsl[0].toInt()}, ${hsl[1].toInt()}%, ${hsl[2].toInt()}%)"
            
            // HSV
            val hsv = rgbToHsv(r, g, b)
            hsvResult = "hsv(${hsv[0].toInt()}, ${hsv[1].toInt()}%, ${hsv[2].toInt()}%)"
            
            // CMYK
            val cmyk = rgbToCmyk(r, g, b)
            cmykResult = "cmyk(${cmyk[0]}%, ${cmyk[1]}%, ${cmyk[2]}%, ${cmyk[3]}%)"
            
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Invalid color format"
        }
    }
    
    private fun rgbToHsl(r: Int, g: Int, b: Int): FloatArray {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f
        
        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val l = (max + min) / 2
        
        if (max == min) return floatArrayOf(0f, 0f, l * 100)
        
        val d = max - min
        val s = if (l > 0.5f) d / (2 - max - min) else d / (max + min)
        
        val h = when (max) {
            rf -> ((gf - bf) / d + (if (gf < bf) 6 else 0)) * 60
            gf -> ((bf - rf) / d + 2) * 60
            else -> ((rf - gf) / d + 4) * 60
        }
        
        return floatArrayOf(h, s * 100, l * 100)
    }
    
    private fun rgbToHsv(r: Int, g: Int, b: Int): FloatArray {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f
        
        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val d = max - min
        
        val v = max * 100
        val s = if (max == 0f) 0f else (d / max) * 100
        
        if (max == min) return floatArrayOf(0f, s, v)
        
        val h = when (max) {
            rf -> ((gf - bf) / d + (if (gf < bf) 6 else 0)) * 60
            gf -> ((bf - rf) / d + 2) * 60
            else -> ((rf - gf) / d + 4) * 60
        }
        
        return floatArrayOf(h, s, v)
    }
    
    private fun rgbToCmyk(r: Int, g: Int, b: Int): IntArray {
        if (r == 0 && g == 0 && b == 0) return intArrayOf(0, 0, 0, 100)
        
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f
        
        val k = 1 - maxOf(rf, gf, bf)
        val c = ((1 - rf - k) / (1 - k) * 100).toInt()
        val m = ((1 - gf - k) / (1 - k) * 100).toInt()
        val y = ((1 - bf - k) / (1 - k) * 100).toInt()
        
        return intArrayOf(c, m, y, (k * 100).toInt())
    }
    
    fun clearAll() {
        hexInput = "#"
        rgbResult = ""
        hslResult = ""
        hsvResult = ""
        cmykResult = ""
        previewColor = Color.Gray
    }
}
