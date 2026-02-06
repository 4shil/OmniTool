package com.omnitool.features.utilities.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Flashlight Screen
 * 
 * Features:
 * - Flashlight toggle
 * - Screen light mode
 * - Color options
 * - Brightness slider
 */
@Composable
fun FlashlightScreen(
    onBack: () -> Unit,
    viewModel: FlashlightViewModel = hiltViewModel()
) {
    // Full screen light mode
    if (viewModel.isScreenLightOn) {
        FullScreenLight(
            color = Color(viewModel.screenColor.colorValue),
            brightness = viewModel.screenBrightness,
            onDismiss = { viewModel.toggleScreenLight() }
        )
        return
    }
    
    ToolWorkspaceScreen(
        toolName = "Flashlight",
        toolIcon = OmniToolIcons.Flashlight,
        accentColor = OmniToolTheme.colors.warmYellow,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Main flashlight toggle
                if (viewModel.hasFlashlight) {
                    FlashlightToggle(
                        isOn = viewModel.isFlashlightOn,
                        onToggle = { viewModel.toggleFlashlight() }
                    )
                } else {
                    NoFlashlightMessage()
                }
                
                // Screen light options
                ScreenLightOptions(
                    brightness = viewModel.screenBrightness,
                    onBrightnessChange = { viewModel.setScreenBrightness(it) },
                    selectedColor = viewModel.screenColor,
                    onColorSelect = { viewModel.setScreenColor(it) }
                )
            }
        },
        outputContent = {
            // Screen light button
            ScreenLightButton(onClick = { viewModel.toggleScreenLight() })
        },
        primaryActionLabel = null,
        onPrimaryAction = {}
    )
}

@Composable
private fun FlashlightToggle(
    isOn: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(
                if (isOn) OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .clickable(onClick = onToggle)
            .padding(OmniToolTheme.spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isOn) OmniToolIcons.FlashlightOn else OmniToolIcons.FlashlightOff,
                contentDescription = "Flashlight",
                tint = if (isOn) OmniToolTheme.colors.warmYellow else OmniToolTheme.colors.textMuted,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (isOn) "ON" else "OFF",
                style = OmniToolTheme.typography.titleXL.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = if (isOn) OmniToolTheme.colors.warmYellow else OmniToolTheme.colors.textMuted
            )
            
            Text(
                text = "Tap to toggle flashlight",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun NoFlashlightMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No flashlight available on this device",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.softCoral,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScreenLightOptions(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    selectedColor: ScreenColor,
    onColorSelect: (ScreenColor) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Screen Light Options",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        // Color selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ScreenColor.values().toList()) { color ->
                val isSelected = color == selectedColor
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(OmniToolTheme.shapes.small)
                        .background(Color(color.colorValue))
                        .clickable { onColorSelect(color) }
                        .then(
                            if (isSelected) Modifier.padding(2.dp)
                                .background(Color.Transparent)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = OmniToolIcons.Check,
                            contentDescription = "Selected",
                            tint = if (color == ScreenColor.WHITE || color == ScreenColor.WARM) 
                                   Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        // Brightness slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Brightness",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = "${(brightness * 100).toInt()}%",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.warmYellow
            )
        }
        
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.1f..1f,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.warmYellow,
                activeTrackColor = OmniToolTheme.colors.warmYellow,
                inactiveTrackColor = OmniToolTheme.colors.surface
            )
        )
    }
}

@Composable
private fun ScreenLightButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.warmYellow.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = OmniToolIcons.ScreenLight,
                contentDescription = null,
                tint = OmniToolTheme.colors.warmYellow
            )
            Text(
                text = "Activate Screen Light",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.warmYellow
            )
        }
    }
}

@Composable
private fun FullScreenLight(
    color: Color,
    brightness: Float,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.copy(alpha = brightness))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap anywhere to close",
            style = OmniToolTheme.typography.body,
            color = if (color == Color.White || color == Color(0xFFFFF4E0)) 
                   Color.Gray else Color.White.copy(alpha = 0.7f)
        )
    }
}
