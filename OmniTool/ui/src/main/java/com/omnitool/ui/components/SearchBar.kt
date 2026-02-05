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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import com.omnitool.core.theme.OmniToolTheme

/**
 * Search bar component - Command palette style
 * Visually dominant, full width floating card
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Find a tool…"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = OmniToolTheme.elevation.medium,
                shape = OmniToolTheme.shapes.card,
                spotColor = OmniToolTheme.colors.shadowColor
            )
            .clip(OmniToolTheme.shapes.card)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(horizontal = OmniToolTheme.spacing.sm, vertical = OmniToolTheme.spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = OmniToolTheme.colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(OmniToolTheme.spacing.xs))
            
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted
                    )
                }
                
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = OmniToolTheme.typography.body.copy(
                        color = OmniToolTheme.colors.textPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
