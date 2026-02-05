package com.omnitool.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * OmniTool Theme
 * 
 * Provides access to colors, typography, shapes, spacing, and elevation
 * through the OmniToolTheme object.
 * 
 * Dark theme is the default as per UI/UX specification.
 */

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.primaryLime,
    secondary = DarkColors.skyBlue,
    tertiary = DarkColors.mint,
    background = DarkColors.background,
    surface = DarkColors.surface,
    surfaceVariant = DarkColors.elevatedSurface,
    onPrimary = DarkColors.background,
    onSecondary = DarkColors.background,
    onTertiary = DarkColors.background,
    onBackground = DarkColors.textPrimary,
    onSurface = DarkColors.textPrimary,
    onSurfaceVariant = DarkColors.textSecondary,
    error = DarkColors.softCoral,
    onError = DarkColors.textPrimary
)

@Composable
fun OmniToolTheme(
    darkTheme: Boolean = true, // Dark theme default
    content: @Composable () -> Unit
) {
    val colors = DarkColors
    val typography = DefaultTypography
    val shapes = DefaultShapes
    val spacing = DefaultSpacing
    val elevation = DefaultElevation
    
    CompositionLocalProvider(
        LocalOmniToolColors provides colors,
        LocalOmniToolTypography provides typography,
        LocalOmniToolShapes provides shapes,
        LocalOmniToolSpacing provides spacing,
        LocalOmniToolElevation provides elevation
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}

/**
 * Access theme values via OmniToolTheme object
 */
object OmniToolTheme {
    val colors: OmniToolColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniToolColors.current
    
    val typography: OmniToolTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniToolTypography.current
    
    val shapes: OmniToolShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniToolShapes.current
    
    val spacing: OmniToolSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniToolSpacing.current
    
    val elevation: OmniToolElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniToolElevation.current
}
