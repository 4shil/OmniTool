package com.omnitool.features.utilities.speedometer

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
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
 * Speedometer Screen
 */
@Composable
fun SpeedometerScreen(
    onBack: () -> Unit,
    viewModel: SpeedometerViewModel = hiltViewModel()
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.checkPermission()
            viewModel.start()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.checkPermission()
        if (viewModel.hasPermission) {
            viewModel.start()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose { viewModel.stop() }
    }
    
    ToolWorkspaceScreen(
        toolName = "Speedometer",
        toolIcon = OmniToolIcons.Speed,
        accentColor = OmniToolTheme.colors.accentOrange,
        onBack = onBack,
        inputContent = {
            when {
                !viewModel.hasGps -> NoGpsMessage()
                !viewModel.hasPermission -> PermissionRequest(
                    onRequest = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
                else -> SpeedDial(
                    speed = viewModel.currentSpeed,
                    maxSpeed = 200f,
                    unit = viewModel.unit,
                    onToggleUnit = { viewModel.toggleUnit() }
                )
            }
        },
        outputContent = {
            if (viewModel.hasPermission && viewModel.hasGps) {
                SpeedStats(
                    maxSpeed = viewModel.formatSpeed(viewModel.maxSpeed),
                    avgSpeed = viewModel.formatSpeed(viewModel.avgSpeed),
                    unit = viewModel.unit.symbol,
                    onReset = { viewModel.resetStats() }
                )
            }
        },
        primaryActionLabel = null,
        onPrimaryAction = {}
    )
}

@Composable
private fun SpeedDial(
    speed: Float,
    maxSpeed: Float,
    unit: SpeedUnit,
    onToggleUnit: () -> Unit
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(durationMillis = 300)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        // Gauge arc
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            val strokeWidth = 20f
            val sweepAngle = 240f
            val startAngle = 150f
            
            // Background arc
            drawArc(
                color = OmniToolTheme.colors.surface,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Progress arc
            val progress = (animatedSpeed / maxSpeed).coerceIn(0f, 1f)
            drawArc(
                color = OmniToolTheme.colors.accentOrange,
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        // Speed display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onToggleUnit)
        ) {
            Text(
                text = String.format("%.0f", animatedSpeed),
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = unit.symbol,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.accentOrange
            )
            Text(
                text = "Tap to change unit",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun NoGpsMessage() {
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
                text = "GPS not available",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.softCoral
            )
        }
    }
}

@Composable
private fun PermissionRequest(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.skyBlue.copy(alpha = 0.1f))
            .clickable(onClick = onRequest),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.Location,
                contentDescription = null,
                tint = OmniToolTheme.colors.skyBlue,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location permission required",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.skyBlue
            )
            Text(
                text = "Tap to grant",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun SpeedStats(
    maxSpeed: String,
    avgSpeed: String,
    unit: String,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Max", value = "$maxSpeed $unit")
            StatItem(label = "Avg", value = "$avgSpeed $unit")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = onReset,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Reset Stats", color = OmniToolTheme.colors.accentOrange)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textPrimary
        )
    }
}
