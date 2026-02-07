package com.omnitool.features.utilities.countdown

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import java.time.format.DateTimeFormatter

/**
 * Countdown Timer Screen
 */
@Composable
fun CountdownTimerScreen(
    onBack: () -> Unit,
    viewModel: CountdownTimerViewModel = hiltViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    ToolWorkspaceScreen(
        toolName = "Countdown",
        toolIcon = OmniToolIcons.Countdown,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Event name input
                OutlinedTextField(
                    value = viewModel.eventName,
                    onValueChange = { viewModel.setEventName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Event Name") },
                    placeholder = { Text("What are you counting down to?") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.skyBlue,
                        cursorColor = OmniToolTheme.colors.skyBlue
                    )
                )
                
                // Date selector
                DateSelector(
                    date = viewModel.targetDate,
                    onClick = { showDatePicker = true }
                )
                
                // Presets
                PresetRow(
                    onPresetSelect = { viewModel.setPreset(it) }
                )
            }
        },
        outputContent = {
            CountdownDisplay(
                days = viewModel.daysRemaining,
                hours = viewModel.hoursRemaining,
                minutes = viewModel.minutesRemaining,
                seconds = viewModel.secondsRemaining,
                isExpired = viewModel.isExpired,
                eventName = viewModel.eventName
            )
        },
        primaryActionLabel = if (viewModel.eventName.isNotEmpty()) "Save" else null,
        onPrimaryAction = { viewModel.saveCountdown() },
        secondaryActionLabel = "Clear",
        onSecondaryAction = { viewModel.clear() }
    )
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = viewModel.targetDate.toEpochDay() * 86400000L
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.setTargetDate(
                                java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = OmniToolTheme.colors.skyBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = OmniToolTheme.colors.textMuted)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DateSelector(
    date: java.time.LocalDate,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Target Date",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
        }
        Icon(
            imageVector = OmniToolIcons.DateCalc,
            contentDescription = "Select date",
            tint = OmniToolTheme.colors.skyBlue
        )
    }
}

@Composable
private fun PresetRow(
    onPresetSelect: (CountdownPreset) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CountdownPreset.values().toList()) { preset ->
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.skyBlue.copy(alpha = 0.15f))
                    .clickable { onPresetSelect(preset) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = preset.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.skyBlue
                )
            }
        }
    }
}

@Composable
private fun CountdownDisplay(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    isExpired: Boolean,
    eventName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (eventName.isNotEmpty()) {
            Text(
                text = eventName,
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (isExpired) {
            Text(
                text = "Event has passed!",
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.softCoral,
                textAlign = TextAlign.Center
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeUnit(value = days, label = "Days")
                TimeSeparator()
                TimeUnit(value = hours, label = "Hours")
                TimeSeparator()
                TimeUnit(value = minutes, label = "Min")
                TimeSeparator()
                TimeUnit(value = seconds, label = "Sec")
            }
        }
    }
}

@Composable
private fun TimeUnit(
    value: Long,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%02d".format(value),
            style = OmniToolTheme.typography.titleXL.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            ),
            color = OmniToolTheme.colors.skyBlue
        )
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun TimeSeparator() {
    Text(
        text = ":",
        style = OmniToolTheme.typography.titleXL.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        ),
        color = OmniToolTheme.colors.textMuted
    )
}
