package com.omnitool.features.utilities.level

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Level Tool Screen
 */
@Composable
fun LevelScreen(
    onBack: () -> Unit,
    viewModel: LevelViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.start()
    }
    
    DisposableEffect(Unit) {
        onDispose { viewModel.stop() }
    }
    
    ToolWorkspaceScreen(
        toolName = "Level",
        toolIcon = OmniToolIcons.Level,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            if (!viewModel.hasSensor) {
                NoSensorMessage()
            } else {
                BubbleLevel(
                    pitch = viewModel.pitch,
                    roll = viewModel.roll,
                    isLevel = viewModel.isLevel
                )
            }
        },
        outputContent = {
            if (viewModel.hasSensor) {
                AngleDisplay(
                    pitch = viewModel.formatAngle(viewModel.pitch),
                    roll = viewModel.formatAngle(viewModel.roll),
                    isLevel = viewModel.isLevel
                )
            }
        },
        primaryActionLabel = null,
        onPrimaryAction = {}
    )
}

@Composable
private fun BubbleLevel(
    pitch: Float,
    roll: Float,
    isLevel: Boolean
) {
    val animatedRoll by animateFloatAsState(
        targetValue = roll,
        animationSpec = tween(durationMillis = 100)
    )
    val animatedPitch by animateFloatAsState(
        targetValue = pitch,
        animationSpec = tween(durationMillis = 100)
    )
    
    val bubbleColor = if (isLevel) OmniToolTheme.colors.primaryLime else OmniToolTheme.colors.softCoral
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2
            
            // Outer circle
            drawCircle(
                color = OmniToolTheme.colors.surface,
                radius = maxRadius,
                center = center,
                style = Stroke(width = 4f)
            )
            
            // Middle circle
            drawCircle(
                color = OmniToolTheme.colors.surface,
                radius = maxRadius * 0.6f,
                center = center,
                style = Stroke(width = 2f)
            )
            
            // Inner circle (target)
            drawCircle(
                color = OmniToolTheme.colors.surface,
                radius = maxRadius * 0.2f,
                center = center,
                style = Stroke(width = 2f)
            )
            
            // Crosshairs
            drawLine(
                color = OmniToolTheme.colors.surface,
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = OmniToolTheme.colors.surface,
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1f
            )
            
            // Bubble position based on tilt
            val clampedRoll = animatedRoll.coerceIn(-45f, 45f)
            val clampedPitch = animatedPitch.coerceIn(-45f, 45f)
            
            val bubbleX = center.x + (clampedRoll / 45f) * maxRadius * 0.7f
            val bubbleY = center.y + (clampedPitch / 45f) * maxRadius * 0.7f
            
            // Bubble
            drawCircle(
                color = bubbleColor,
                radius = 30f,
                center = Offset(bubbleX, bubbleY)
            )
        }
        
        // Level indicator text
        if (isLevel) {
            Text(
                text = "LEVEL",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.primaryLime,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }
    }
}

@Composable
private fun NoSensorMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.Warning,
                contentDescription = null,
                tint = OmniToolTheme.colors.softCoral,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No accelerometer available",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.softCoral
            )
        }
    }
}

@Composable
private fun AngleDisplay(
    pitch: String,
    roll: String,
    isLevel: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AngleItem(label = "Pitch", value = pitch)
        AngleItem(label = "Roll", value = roll)
        AngleItem(
            label = "Status",
            value = if (isLevel) "Level" else "Tilted",
            valueColor = if (isLevel) OmniToolTheme.colors.primaryLime 
                        else OmniToolTheme.colors.softCoral
        )
    }
}

@Composable
private fun AngleItem(
    label: String,
    value: String,
    valueColor: Color = OmniToolTheme.colors.textPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.label,
            color = valueColor
        )
    }
}
