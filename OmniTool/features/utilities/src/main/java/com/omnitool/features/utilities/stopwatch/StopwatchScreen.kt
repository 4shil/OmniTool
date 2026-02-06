package com.omnitool.features.utilities.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Stopwatch Screen
 * 
 * Features:
 * - Large time display
 * - Start/Stop/Reset controls
 * - Lap functionality
 * - Lap list with times
 */
@Composable
fun StopwatchScreen(
    onBack: () -> Unit,
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Stopwatch",
        toolIcon = OmniToolIcons.Stopwatch,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            // Time display
            TimeDisplay(
                elapsedMs = viewModel.elapsedMs,
                isRunning = viewModel.isRunning
            )
        },
        outputContent = {
            // Lap times
            LapList(laps = viewModel.laps)
        },
        primaryActionLabel = if (viewModel.isRunning) "Stop" else "Start",
        onPrimaryAction = {
            if (viewModel.isRunning) viewModel.stop() else viewModel.start()
        },
        secondaryActionLabel = if (viewModel.isRunning) "Lap" else if (viewModel.elapsedMs > 0) "Reset" else null,
        onSecondaryAction = {
            if (viewModel.isRunning) viewModel.lap() else viewModel.reset()
        }
    )
}

@Composable
private fun TimeDisplay(
    elapsedMs: Long,
    isRunning: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = elapsedMs.formatTimeFull(),
            style = OmniToolTheme.typography.titleXL.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                letterSpacing = 2.sp
            ),
            color = if (isRunning) OmniToolTheme.colors.primaryLime 
                    else OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isRunning) "Running" else if (elapsedMs > 0) "Paused" else "Ready",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun LapList(laps: List<LapTime>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Laps",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Text(
                text = "${laps.size} laps",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 200.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
        ) {
            if (laps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(OmniToolTheme.spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap Lap while running to record times",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(OmniToolTheme.spacing.xs)
                ) {
                    items(laps.reversed()) { lap ->
                        LapItem(lap = lap)
                    }
                }
            }
        }
    }
}

@Composable
private fun LapItem(lap: LapTime) {
    // Find best and worst lap
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lap ${lap.number}",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Text(
            text = lap.lapTime.formatTime(),
            style = OmniToolTheme.typography.body.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = OmniToolTheme.colors.primaryLime
        )
        
        Text(
            text = lap.totalTime.formatTime(),
            style = OmniToolTheme.typography.body.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = OmniToolTheme.colors.textMuted
        )
    }
}
