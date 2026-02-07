package com.omnitool.features.utilities.compass

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Compass Screen
 * 
 * Features:
 * - Animated compass dial
 * - Heading display
 * - Cardinal direction
 * - Accuracy indicator
 */
@Composable
fun CompassScreen(
    onBack: () -> Unit,
    viewModel: CompassViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.start()
    }
    
    DisposableEffect(Unit) {
        onDispose { viewModel.stop() }
    }
    
    ToolWorkspaceScreen(
        toolName = "Compass",
        toolIcon = OmniToolIcons.Compass,
        accentColor = OmniToolTheme.colors.accentOrange,
        onBack = onBack,
        inputContent = {
            if (!viewModel.hasSensor) {
                NoSensorMessage()
            } else {
                CompassDial(
                    heading = viewModel.heading,
                    cardinalDirection = viewModel.cardinalDirection
                )
            }
        },
        outputContent = {
            if (viewModel.hasSensor) {
                CompassInfo(
                    heading = viewModel.heading,
                    direction = viewModel.getFullDirection(),
                    accuracy = viewModel.accuracy
                )
            }
        },
        primaryActionLabel = null,
        onPrimaryAction = null
    )
}

@Composable
private fun CompassDial(
    heading: Float,
    cardinalDirection: String
) {
    val animatedHeading by animateFloatAsState(
        targetValue = -heading,
        animationSpec = tween(durationMillis = 200)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        // Compass rose
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .rotate(animatedHeading)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            
            // Outer circle
            drawCircle(
                color = OmniToolTheme.colors.surface,
                radius = radius,
                center = center,
                style = Stroke(width = 4f)
            )
            
            // Tick marks
            for (i in 0 until 360 step 10) {
                rotate(i.toFloat(), center) {
                    val startRadius = if (i % 30 == 0) radius - 30f else radius - 15f
                    drawLine(
                        color = if (i % 90 == 0) OmniToolTheme.colors.accentOrange 
                               else OmniToolTheme.colors.textMuted,
                        start = Offset(center.x, center.y - startRadius),
                        end = Offset(center.x, center.y - radius + 5f),
                        strokeWidth = if (i % 30 == 0) 3f else 1f
                    )
                }
            }
            
            // North indicator (red triangle)
            val northPath = Path().apply {
                moveTo(center.x, center.y - radius + 60f)
                lineTo(center.x - 15f, center.y - radius + 90f)
                lineTo(center.x + 15f, center.y - radius + 90f)
                close()
            }
            drawPath(northPath, color = Color.Red)
        }
        
        // Center indicator (fixed)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${heading.toInt()}°",
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = cardinalDirection,
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.accentOrange
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
                text = "No compass sensor available",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.softCoral,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompassInfo(
    heading: Float,
    direction: String,
    accuracy: SensorAccuracy
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoItem(label = "Heading", value = "${heading.toInt()}°")
        InfoItem(label = "Direction", value = direction)
        InfoItem(
            label = "Accuracy", 
            value = accuracy.displayName,
            valueColor = when (accuracy) {
                SensorAccuracy.HIGH -> OmniToolTheme.colors.primaryLime
                SensorAccuracy.MEDIUM -> OmniToolTheme.colors.warmYellow
                SensorAccuracy.LOW -> OmniToolTheme.colors.softCoral
                SensorAccuracy.UNKNOWN -> OmniToolTheme.colors.textMuted
            }
        )
    }
}

@Composable
private fun InfoItem(
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
