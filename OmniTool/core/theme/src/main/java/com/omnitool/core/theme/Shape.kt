package com.omnitool.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * OmniTool Shape System
 * Based on UI/UX specification
 * 
 * All corners must be rounded - no sharp corners anywhere.
 */
@Immutable
data class OmniToolShapes(
    val small: Shape,      // 8dp
    val medium: Shape,     // 14dp
    val large: Shape,      // 22dp
    val card: Shape,       // 28dp
    val pill: Shape        // Full rounded
)

val DefaultShapes = OmniToolShapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    card = RoundedCornerShape(28.dp),
    pill = RoundedCornerShape(50)  // Pill shape
)

/**
 * Radius values for custom usage
 */
object Radius {
    val Small: Dp = 8.dp
    val Medium: Dp = 14.dp
    val Large: Dp = 22.dp
    val Card: Dp = 28.dp
}

val LocalOmniToolShapes = staticCompositionLocalOf { DefaultShapes }
