package com.omnitool.features.utilities.colorpicker

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Color Picker ViewModel
 * 
 * Features:
 * - RGB sliders
 * - HSL mode
 * - Hex input
 * - Color preview
 * - Copy formats
 */
@HiltViewModel
class ColorPickerViewModel @Inject constructor() : ViewModel() {
    
    var red by mutableIntStateOf(128)
        private set
    
    var green by mutableIntStateOf(128)
        private set
    
    var blue by mutableIntStateOf(128)
        private set
    
    var alpha by mutableIntStateOf(255)
        private set
    
    var hexInput by mutableStateOf("808080")
        private set
    
    var currentColor by mutableStateOf(Color(128, 128, 128))
        private set
    
    var savedColors by mutableStateOf<List<Color>>(emptyList())
        private set
    
    fun setRed(value: Int) {
        red = value.coerceIn(0, 255)
        updateColor()
    }
    
    fun setGreen(value: Int) {
        green = value.coerceIn(0, 255)
        updateColor()
    }
    
    fun setBlue(value: Int) {
        blue = value.coerceIn(0, 255)
        updateColor()
    }
    
    fun setAlpha(value: Int) {
        alpha = value.coerceIn(0, 255)
        updateColor()
    }
    
    fun setHexInput(hex: String) {
        val cleanHex = hex.replace("#", "").take(8).filter { it.isDigit() || it.uppercaseChar() in 'A'..'F' }
        hexInput = cleanHex
        
        if (cleanHex.length == 6 || cleanHex.length == 8) {
            try {
                if (cleanHex.length == 8) {
                    alpha = cleanHex.substring(0, 2).toInt(16)
                    red = cleanHex.substring(2, 4).toInt(16)
                    green = cleanHex.substring(4, 6).toInt(16)
                    blue = cleanHex.substring(6, 8).toInt(16)
                } else {
                    red = cleanHex.substring(0, 2).toInt(16)
                    green = cleanHex.substring(2, 4).toInt(16)
                    blue = cleanHex.substring(4, 6).toInt(16)
                    alpha = 255
                }
                updateColor()
            } catch (_: Exception) {}
        }
    }
    
    private fun updateColor() {
        currentColor = Color(red, green, blue, alpha)
        hexInput = String.format("%02X%02X%02X", red, green, blue)
    }
    
    fun saveColor() {
        if (!savedColors.contains(currentColor)) {
            savedColors = (listOf(currentColor) + savedColors).take(12)
        }
    }
    
    fun selectSavedColor(color: Color) {
        red = (color.red * 255).roundToInt()
        green = (color.green * 255).roundToInt()
        blue = (color.blue * 255).roundToInt()
        alpha = (color.alpha * 255).roundToInt()
        updateColor()
    }
    
    fun removeSavedColor(color: Color) {
        savedColors = savedColors.filter { it != color }
    }
    
    // Output formats
    val hexString: String
        get() = "#${String.format("%02X%02X%02X", red, green, blue)}"
    
    val hexWithAlpha: String
        get() = "#${String.format("%02X%02X%02X%02X", alpha, red, green, blue)}"
    
    val rgbString: String
        get() = "rgb($red, $green, $blue)"
    
    val rgbaString: String
        get() = "rgba($red, $green, $blue, ${String.format("%.2f", alpha / 255f)})"
    
    val hslString: String
        get() {
            val (h, s, l) = rgbToHsl(red, green, blue)
            return "hsl(${h.roundToInt()}, ${(s * 100).roundToInt()}%, ${(l * 100).roundToInt()}%)"
        }
    
    val androidColor: String
        get() = "Color(0xFF${String.format("%02X%02X%02X", red, green, blue)})"
    
    private fun rgbToHsl(r: Int, g: Int, b: Int): Triple<Float, Float, Float> {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f
        
        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val l = (max + min) / 2
        
        if (max == min) {
            return Triple(0f, 0f, l)
        }
        
        val d = max - min
        val s = if (l > 0.5f) d / (2 - max - min) else d / (max + min)
        
        val h = when {
            max == rf -> (gf - bf) / d + (if (gf < bf) 6 else 0)
            max == gf -> (bf - rf) / d + 2
            else -> (rf - gf) / d + 4
        } * 60
        
        return Triple(h, s, l)
    }
}
