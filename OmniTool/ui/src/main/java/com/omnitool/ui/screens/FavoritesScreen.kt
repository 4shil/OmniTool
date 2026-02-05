package com.omnitool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ToolCard
import com.omnitool.ui.components.ToolCardSize

/**
 * Favorites Screen - User's pinned tools
 * 
 * Grid layout with drag-to-reorder support
 * Acts as personal dashboard
 */
@Composable
fun FavoritesScreen(
    onToolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
            .padding(horizontal = OmniToolTheme.spacing.sm)
    ) {
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        Text(
            text = "Favorites",
            style = OmniToolTheme.typography.titleXL,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        Text(
            text = "Long press to reorder",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // Placeholder - will show actual favorites from data layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No favorites yet",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                Text(
                    text = "Pin tools from Home to see them here",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        }
    }
}
