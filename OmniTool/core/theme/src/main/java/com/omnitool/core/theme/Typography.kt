package com.omnitool.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * OmniTool Typography System
 * Based on UI/UX specification
 * 
 * Font: Inter (fallback to system default)
 * Line spacing: 1.3-1.5
 */

// Font family - using system default, can be replaced with custom font
val OmniToolFontFamily = FontFamily.Default

/**
 * Typography Scale
 */
@Immutable
data class OmniToolTypography(
    val titleXL: TextStyle,    // 32sp
    val titleL: TextStyle,     // 24sp
    val header: TextStyle,     // 20sp
    val body: TextStyle,       // 16sp
    val label: TextStyle,      // 14sp
    val caption: TextStyle,    // 12sp
    val headingLarge: TextStyle,
    val headingMedium: TextStyle
)

val DefaultTypography = OmniToolTypography(
    titleXL = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 42.sp  // ~1.3 ratio
    ),
    titleL = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    header = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    body = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp  // 1.5 ratio
    ),
    label = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    caption = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    headingLarge = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headingMedium = TextStyle(
        fontFamily = OmniToolFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )
)

val LocalOmniToolTypography = staticCompositionLocalOf { DefaultTypography }
