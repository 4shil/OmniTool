package com.omnitool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme

/**
 * Settings Screen - Simple list layout
 * 
 * Sections:
 * - Appearance
 * - Permissions
 * - Privacy
 * - About
 * - Upgrade
 * 
 * No deep navigation trees.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
            .padding(horizontal = OmniToolTheme.spacing.sm)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        Text(
            text = "Settings",
            style = OmniToolTheme.typography.titleXL,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // Appearance Section
        SettingsSection(title = "Appearance")
        SettingsItem(
            icon = Icons.Outlined.Palette,
            title = "Theme",
            subtitle = "Dark (default)",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Permissions Section
        SettingsSection(title = "Permissions")
        SettingsItem(
            icon = Icons.Outlined.Folder,
            title = "Storage Access",
            subtitle = "Required for file tools",
            onClick = { }
        )
        SettingsItem(
            icon = Icons.Outlined.CameraAlt,
            title = "Camera",
            subtitle = "Required for QR scanner",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Privacy Section
        SettingsSection(title = "Privacy")
        SettingsItem(
            icon = Icons.Outlined.Security,
            title = "Privacy Policy",
            subtitle = "View our privacy commitment",
            onClick = { }
        )
        SettingsItem(
            icon = Icons.Outlined.DeleteSweep,
            title = "Clear Data",
            subtitle = "Remove all local data",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // About Section
        SettingsSection(title = "About")
        SettingsItem(
            icon = Icons.Outlined.Info,
            title = "Version",
            subtitle = "1.0.0",
            onClick = { }
        )
        SettingsItem(
            icon = Icons.Outlined.Code,
            title = "Open Source Licenses",
            subtitle = "Third-party libraries",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Upgrade Section
        SettingsSection(title = "Upgrade")
        SettingsItem(
            icon = Icons.Outlined.Star,
            title = "Get OmniTool Pro",
            subtitle = "Remove ads, unlock themes & premium tools",
            accentColor = OmniToolTheme.colors.primaryLime,
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xl))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = OmniToolTheme.typography.label,
        color = OmniToolTheme.colors.textMuted,
        modifier = modifier.padding(vertical = OmniToolTheme.spacing.xs)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: androidx.compose.ui.graphics.Color = OmniToolTheme.colors.textSecondary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = OmniToolTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(OmniToolTheme.spacing.sm))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textSecondary
            )
        }
        
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "Navigate",
            tint = OmniToolTheme.colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
