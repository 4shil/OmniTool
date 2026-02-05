package com.omnitool.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * OmniTool Spacing System
 * Based on 8pt grid system from UI/UX specification
 */
@Immutable
data class OmniToolSpacing(
    val xxs: Dp,   // 4dp
    val xs: Dp,    // 8dp
    val sm: Dp,    // 16dp
    val md: Dp,    // 24dp
    val lg: Dp,    // 32dp
    val xl: Dp,    // 48dp
    val xxl: Dp    // 64dp
)

val DefaultSpacing = OmniToolSpacing(
    xxs = 4.dp,
    xs = 8.dp,
    sm = 16.dp,
    md = 24.dp,
    lg = 32.dp,
    xl = 48.dp,
    xxl = 64.dp
)

/**
 * Elevation values for depth hierarchy
 */
@Immutable
data class OmniToolElevation(
    val none: Dp,
    val low: Dp,
    val medium: Dp,
    val high: Dp
)

val DefaultElevation = OmniToolElevation(
    none = 0.dp,
    low = 2.dp,
    medium = 8.dp,
    high = 16.dp
)

/**
 * Touch target sizes for accessibility
 */
object TouchTarget {
    val Minimum: Dp = 44.dp  // Accessibility minimum
    val Standard: Dp = 48.dp
    val Large: Dp = 56.dp
}

val LocalOmniToolSpacing = staticCompositionLocalOf { DefaultSpacing }
val LocalOmniToolElevation = staticCompositionLocalOf { DefaultElevation }
