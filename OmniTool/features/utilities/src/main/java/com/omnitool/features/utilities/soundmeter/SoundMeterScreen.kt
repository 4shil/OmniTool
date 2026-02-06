package com.omnitool.features.utilities.soundmeter

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
 * Sound Meter Screen
 * 
 * Features:
 * - Decibel gauge
 * - Noise level indicator
 * - Min/Max display
 * - Start/Stop controls
 */
@Composable
fun SoundMeterScreen(
    onBack: () -> Unit,
    viewModel: SoundMeterViewModel = hiltViewModel()
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setPermissionGranted(granted)
        if (granted) {
            viewModel.startRecording()
        }
    }
    
    ToolWorkspaceScreen(
        toolName = "Sound Meter",
        toolIcon = OmniToolIcons.SoundMeter,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            // Decibel gauge
            DecibelGauge(
                currentDb = viewModel.currentDb,
                noiseLevel = viewModel.getNoiseLevel()
            )
        },
        outputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Stats
                StatsRow(
                    minDb = viewModel.minDb,
                    maxDb = viewModel.maxDb,
                    onReset = { viewModel.resetStats() }
                )
                
                // Noise level description
                NoiseLevelInfo(noiseLevel = viewModel.getNoiseLevel())
            }
        },
        primaryActionLabel = if (viewModel.isRecording) "Stop" else "Start",
        onPrimaryAction = {
            if (viewModel.isRecording) {
                viewModel.stopRecording()
            } else if (viewModel.needsPermission) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                viewModel.startRecording()
            }
        }
    )
}

@Composable
private fun DecibelGauge(
    currentDb: Float,
    noiseLevel: NoiseLevel
) {
    val animatedDb by animateFloatAsState(
        targetValue = currentDb,
        animationSpec = tween(durationMillis = 100)
    )
    
    val gaugeColor = when (noiseLevel) {
        NoiseLevel.QUIET -> OmniToolTheme.colors.primaryLime
        NoiseLevel.MODERATE -> OmniToolTheme.colors.warmYellow
        NoiseLevel.LOUD -> OmniToolTheme.colors.accentOrange
        NoiseLevel.VERY_LOUD -> OmniToolTheme.colors.softCoral
        NoiseLevel.DANGEROUS -> Color.Red
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        // Arc gauge
        Canvas(
            modifier = Modifier
                .size(180.dp)
                .padding(16.dp)
        ) {
            val sweepAngle = (animatedDb / 120f) * 180f
            
            // Background arc
            drawArc(
                color = OmniToolTheme.colors.surface,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
            
            // Value arc
            drawArc(
                color = gaugeColor,
                startAngle = 180f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
        }
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 20.dp)
        ) {
            Text(
                text = String.format("%.1f", animatedDb),
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = gaugeColor
            )
            Text(
                text = "dB",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun StatsRow(
    minDb: Float,
    maxDb: Float,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            label = "Min",
            value = if (minDb < Float.MAX_VALUE) String.format("%.1f", minDb) else "--",
            color = OmniToolTheme.colors.primaryLime
        )
        
        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = OmniToolTheme.colors.surface
        )
        
        StatItem(
            label = "Max",
            value = if (maxDb > 0) String.format("%.1f", maxDb) else "--",
            color = OmniToolTheme.colors.softCoral
        )
        
        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = OmniToolTheme.colors.surface
        )
        
        TextButton(onClick = onReset) {
            Text(
                text = "Reset",
                color = OmniToolTheme.colors.skyBlue
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.header,
            color = color
        )
        Text(
            text = "dB",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun NoiseLevelInfo(noiseLevel: NoiseLevel) {
    val bgColor = when (noiseLevel) {
        NoiseLevel.QUIET -> OmniToolTheme.colors.primaryLime
        NoiseLevel.MODERATE -> OmniToolTheme.colors.warmYellow
        NoiseLevel.LOUD -> OmniToolTheme.colors.accentOrange
        NoiseLevel.VERY_LOUD -> OmniToolTheme.colors.softCoral
        NoiseLevel.DANGEROUS -> Color.Red
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(bgColor.copy(alpha = 0.15f))
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = noiseLevel.displayName,
                style = OmniToolTheme.typography.label,
                color = bgColor
            )
            Text(
                text = noiseLevel.description,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        
        if (noiseLevel == NoiseLevel.DANGEROUS) {
            Icon(
                imageVector = OmniToolIcons.Warning,
                contentDescription = "Warning",
                tint = Color.Red
            )
        }
    }
}
