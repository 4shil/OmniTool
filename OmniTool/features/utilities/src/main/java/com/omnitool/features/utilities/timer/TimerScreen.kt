package com.omnitool.features.utilities.timer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * Timer/Countdown Screen
 * 
 * Features:
 * - Time picker (hours, minutes, seconds)
 * - Large countdown display
 * - Start/Pause/Reset controls
 * - Complete notification
 */
@Composable
fun TimerScreen(
    onBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (viewModel.isComplete) OmniToolTheme.colors.softCoral.copy(alpha = 0.1f)
                      else OmniToolTheme.colors.background,
        animationSpec = tween(500),
        label = "bgColor"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ToolWorkspaceScreen(
            toolName = "Timer",
            toolIcon = OmniToolIcons.Timer,
            accentColor = OmniToolTheme.colors.warmYellow,
            onBack = onBack,
            inputContent = {
                if (viewModel.remainingMs == 0L && !viewModel.isRunning) {
                    // Time picker
                    TimePicker(
                        hours = viewModel.hours,
                        minutes = viewModel.minutes,
                        seconds = viewModel.seconds,
                        onHoursChange = { viewModel.setHours(it) },
                        onMinutesChange = { viewModel.setMinutes(it) },
                        onSecondsChange = { viewModel.setSeconds(it) }
                    )
                } else {
                    // Countdown display
                    CountdownDisplay(
                        remainingMs = viewModel.remainingMs,
                        totalMs = viewModel.totalMs,
                        isComplete = viewModel.isComplete
                    )
                }
            },
            outputContent = {
                // Quick presets
                if (viewModel.remainingMs == 0L && !viewModel.isRunning) {
                    QuickPresets(
                        onSelectPreset = { m ->
                            viewModel.setMinutes(m)
                            viewModel.setHours(0)
                            viewModel.setSeconds(0)
                        }
                    )
                } else {
                    // Running state info
                    TimerStatus(
                        isRunning = viewModel.isRunning,
                        isComplete = viewModel.isComplete,
                        onAddMinute = { viewModel.addMinute() },
                        onDismiss = { viewModel.dismissComplete() }
                    )
                }
            },
            primaryActionLabel = when {
                viewModel.isComplete -> "Done"
                viewModel.isRunning -> "Pause"
                viewModel.remainingMs > 0 -> "Resume"
                else -> "Start"
            },
            onPrimaryAction = {
                when {
                    viewModel.isComplete -> viewModel.reset()
                    viewModel.isRunning -> viewModel.pause()
                    else -> viewModel.start()
                }
            },
            secondaryActionLabel = if (viewModel.remainingMs > 0 || viewModel.isComplete) "Reset" else null,
            onSecondaryAction = if (viewModel.remainingMs > 0 || viewModel.isComplete) {
                { viewModel.reset() }
            } else null
        )
    }
}

@Composable
private fun TimePicker(
    hours: Int,
    minutes: Int,
    seconds: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Timer Duration",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeUnit(
                value = hours,
                label = "Hours",
                maxValue = 23,
                onValueChange = onHoursChange
            )
            
            Text(
                text = ":",
                style = OmniToolTheme.typography.titleXL,
                color = OmniToolTheme.colors.textMuted
            )
            
            TimeUnit(
                value = minutes,
                label = "Min",
                maxValue = 59,
                onValueChange = onMinutesChange
            )
            
            Text(
                text = ":",
                style = OmniToolTheme.typography.titleXL,
                color = OmniToolTheme.colors.textMuted
            )
            
            TimeUnit(
                value = seconds,
                label = "Sec",
                maxValue = 59,
                onValueChange = onSecondsChange
            )
        }
    }
}

@Composable
private fun TimeUnit(
    value: Int,
    label: String,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Increment button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
                .clickable { onValueChange((value + 1).coerceAtMost(maxValue)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = OmniToolIcons.ExpandLess,
                contentDescription = "Increase",
                tint = OmniToolTheme.colors.warmYellow
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Value display
        Text(
            text = "%02d".format(value),
            style = OmniToolTheme.typography.titleXL.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            ),
            color = OmniToolTheme.colors.textPrimary
        )
        
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Decrement button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
                .clickable { onValueChange((value - 1).coerceAtLeast(0)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = OmniToolIcons.ExpandMore,
                contentDescription = "Decrease",
                tint = OmniToolTheme.colors.warmYellow
            )
        }
    }
}

@Composable
private fun CountdownDisplay(
    remainingMs: Long,
    totalMs: Long,
    isComplete: Boolean
) {
    val progress = if (totalMs > 0) remainingMs.toFloat() / totalMs else 0f
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = remainingMs.formatCountdown(),
            style = OmniToolTheme.typography.titleXL.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = if (isComplete) 56.sp else 48.sp
            ),
            color = if (isComplete) OmniToolTheme.colors.softCoral 
                    else OmniToolTheme.colors.warmYellow
        )
        
        if (isComplete) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Time's Up!",
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.softCoral
            )
        } else {
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(OmniToolTheme.shapes.small),
                color = OmniToolTheme.colors.warmYellow,
                trackColor = OmniToolTheme.colors.surface
            )
        }
    }
}

@Composable
private fun QuickPresets(
    onSelectPreset: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = "Quick Presets",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 3, 5, 10, 15, 30).forEach { minutes ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(OmniToolTheme.shapes.small)
                        .background(OmniToolTheme.colors.surface)
                        .clickable { onSelectPreset(minutes) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${minutes}m",
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.warmYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerStatus(
    isRunning: Boolean,
    isComplete: Boolean,
    onAddMinute: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isComplete) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.2f))
                    .clickable(onClick = onDismiss)
                    .padding(OmniToolTheme.spacing.sm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = OmniToolIcons.Check,
                    contentDescription = null,
                    tint = OmniToolTheme.colors.softCoral,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dismiss",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.softCoral
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f))
                    .clickable(onClick = onAddMinute)
                    .padding(OmniToolTheme.spacing.sm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = OmniToolIcons.Add,
                    contentDescription = null,
                    tint = OmniToolTheme.colors.warmYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+1 Minute",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.warmYellow
                )
            }
        }
    }
}
