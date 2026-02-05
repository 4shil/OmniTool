package com.omnitool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.SearchBar
import com.omnitool.ui.components.ToolCard
import com.omnitool.ui.components.ToolCardSize

/**
 * Home Screen - Command center layout
 * 
 * Structure:
 * - Search Bar (top, visually dominant)
 * - Recent Tools Strip (horizontal scroll)
 * - Favorites Grid
 * - Category Cards
 */
@Composable
fun HomeScreen(
    onToolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
            .padding(horizontal = OmniToolTheme.spacing.sm)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // App title
        Text(
            text = "OmniTool",
            style = OmniToolTheme.typography.titleXL,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Search Bar - Visually dominant
        SearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // Recent Tools Section
        SectionHeader(title = "Recent")
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // Placeholder recent tools - will be populated from data layer
            items(8) { index ->
                ToolCard(
                    icon = Icons.Outlined.Code,
                    label = "Tool ${index + 1}",
                    accentColor = OmniToolTheme.colors.mint,
                    onClick = { /* Navigate to tool */ },
                    size = ToolCardSize.Small
                )
            }
        }
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // Categories Section
        SectionHeader(title = "Categories")
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        // Category cards
        CategoryCard(
            title = "Fix Text",
            description = "Format, validate, transform text",
            accentColor = OmniToolTheme.colors.mint,
            onClick = { /* Expand category */ }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        CategoryCard(
            title = "Convert Files",
            description = "Images, PDFs, media conversion",
            accentColor = OmniToolTheme.colors.skyBlue,
            onClick = { /* Expand category */ }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        CategoryCard(
            title = "Convert Values",
            description = "Units, currency, dates",
            accentColor = OmniToolTheme.colors.warmYellow,
            onClick = { /* Expand category */ }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        CategoryCard(
            title = "Secure Data",
            description = "Passwords, encryption, vault",
            accentColor = OmniToolTheme.colors.softCoral,
            onClick = { /* Expand category */ }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        CategoryCard(
            title = "Quick Utilities",
            description = "Timer, calculator, notes",
            accentColor = OmniToolTheme.colors.primaryLime,
            onClick = { /* Expand category */ }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xl))
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = OmniToolTheme.typography.header,
        color = OmniToolTheme.colors.textPrimary,
        modifier = modifier
    )
}

@Composable
private fun CategoryCard(
    title: String,
    description: String,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = OmniToolTheme.colors.elevatedSurface,
                shape = OmniToolTheme.shapes.large
            )
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Column {
            Row {
                Box(
                    modifier = Modifier
                        .size(4.dp, 20.dp)
                        .background(accentColor, OmniToolTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(OmniToolTheme.spacing.xs))
                Text(
                    text = title,
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textSecondary
            )
        }
    }
}
