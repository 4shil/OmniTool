package com.omnitool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.PrimaryButton

/**
 * Vault Screen - Security UI style
 * 
 * Features:
 * - Darker background
 * - Minimal accents
 * - Biometric unlock
 * - Blurred preview before unlock
 * - Psychological separation from main UI
 */
@Composable
fun VaultScreen(
    modifier: Modifier = Modifier
) {
    // Darker background for vault
    val vaultBackground = OmniToolTheme.colors.background.copy(
        red = OmniToolTheme.colors.background.red * 0.8f,
        green = OmniToolTheme.colors.background.green * 0.8f,
        blue = OmniToolTheme.colors.background.blue * 0.8f
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(vaultBackground)
            .padding(horizontal = OmniToolTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        Text(
            text = "Vault",
            style = OmniToolTheme.typography.titleXL,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Lock icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = OmniToolTheme.colors.softCoral.copy(alpha = 0.15f),
                    shape = OmniToolTheme.shapes.card
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Vault locked",
                tint = OmniToolTheme.colors.softCoral,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        Text(
            text = "Your secure vault",
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        Text(
            text = "Store encrypted notes and files securely",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.lg))
        
        PrimaryButton(
            text = "Unlock with Biometrics",
            onClick = { /* Trigger biometric */ },
            accentColor = OmniToolTheme.colors.softCoral
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
