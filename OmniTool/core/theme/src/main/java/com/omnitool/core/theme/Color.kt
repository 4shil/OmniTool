package com.omnitool.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * OmniTool Color System
 * Based on UI/UX specification
 */
@Immutable
data class OmniToolColors(
    // Base Colors - Dark hierarchy
    val background: Color,
    val surface: Color,
    val elevatedSurface: Color,
    
    // Accent Colors - Semantic meaning
    val primaryLime: Color,      // Safe / Confirm actions
    val skyBlue: Color,          // File tools
    val warmYellow: Color,       // Conversion tools
    val mint: Color,             // Text tools
    val softCoral: Color,        // Destructive actions
    
    // Text Colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textDisabled: Color,
    
    // Special
    val divider: Color,
    val overlay: Color,
    val shadowColor: Color,
    val highlightOverlay: Color,
    
    // Additional accent colors
    val accentOrange: Color,
    val lavender: Color,
    val coral: Color,
    val accentYellow: Color,
    
    // Tool Specific
    val screenLight: Color,
    val flashlightOn: Color,
    val flashlightOff: Color
)

/**
 * Dark Theme Colors (Default)
 */
val DarkColors = OmniToolColors(
    // Base Colors
    background = Color(0xFF0E0F13),
    surface = Color(0xFF161820),
    elevatedSurface = Color(0xFF1F2230),
    
    // Accent Colors
    primaryLime = Color(0xFFC7F36A),
    skyBlue = Color(0xFF8EDBFF),
    warmYellow = Color(0xFFFFD96B),
    mint = Color(0xFF6EF3B2),
    softCoral = Color(0xFFFF6A6A),
    
    // Text Colors
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB8BCC8),
    textMuted = Color(0xFF6D7285),
    textDisabled = Color(0xFF4A4F5E),
    
    // Special
    divider = Color(0xFF2A2D3A),
    overlay = Color(0x80000000),
    shadowColor = Color(0x59000000),  // rgba(0,0,0,0.35)
    highlightOverlay = Color(0x0DFFFFFF), // rgba(255,255,255,0.05)
    
    // Additional accent colors
    accentOrange = Color(0xFFFF5722),
    lavender = Color(0xFF9575CD),
    coral = Color(0xFFFF8A65),
    accentYellow = Color(0xFFFFEB3B),
    
    // Tool Specific
    screenLight = Color(0xFFFFFFFF),
    flashlightOn = Color(0xFFFFD600),
    flashlightOff = Color(0xFF212121)
)

/**
 * Category-specific accent color mapping
 */
enum class ToolCategory {
    TEXT,
    FILE,
    CONVERTER,
    SECURITY,
    UTILITIES
}

fun OmniToolColors.categoryAccent(category: ToolCategory): Color = when (category) {
    ToolCategory.TEXT -> mint
    ToolCategory.FILE -> skyBlue
    ToolCategory.CONVERTER -> warmYellow
    ToolCategory.SECURITY -> softCoral
    ToolCategory.UTILITIES -> primaryLime
}

/**
 * CompositionLocal for accessing colors throughout the app
 */
val LocalOmniToolColors = staticCompositionLocalOf { DarkColors }
