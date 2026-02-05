package com.omnitool.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme

/**
 * Primary action button - Large pastel floating button
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = OmniToolTheme.colors.primaryLime,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        label = "button_scale"
    )
    
    val backgroundColor = if (enabled) accentColor else OmniToolTheme.colors.textDisabled
    val textColor = if (enabled) OmniToolTheme.colors.background else OmniToolTheme.colors.textMuted
    
    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) OmniToolTheme.elevation.medium else OmniToolTheme.elevation.none,
                shape = OmniToolTheme.shapes.pill,
                spotColor = OmniToolTheme.colors.shadowColor
            )
            .clip(OmniToolTheme.shapes.pill)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = OmniToolTheme.spacing.md, vertical = OmniToolTheme.spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = OmniToolTheme.typography.label,
            color = textColor
        )
    }
}

/**
 * Secondary/outline button
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        label = "button_scale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(OmniToolTheme.shapes.pill)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = OmniToolTheme.spacing.md, vertical = OmniToolTheme.spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = OmniToolTheme.typography.label,
            color = if (enabled) OmniToolTheme.colors.textPrimary else OmniToolTheme.colors.textDisabled
        )
    }
}
