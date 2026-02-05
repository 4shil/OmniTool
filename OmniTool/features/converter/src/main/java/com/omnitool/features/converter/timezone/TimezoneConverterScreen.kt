package com.omnitool.features.converter.timezone

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Timezone Converter Screen
 */
@Composable
fun TimezoneConverterScreen(
    onBack: () -> Unit,
    viewModel: TimezoneConverterViewModel = hiltViewModel()
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    
    ToolWorkspaceScreen(
        toolName = "Timezone Converter",
        toolIcon = OmniToolIcons.Timezone,
        accentColor = OmniToolTheme.colors.coral,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time input
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerWheel(
                        value = viewModel.selectedHour,
                        range = 0..23,
                        onValueChange = { viewModel.setTime(it, viewModel.selectedMinute) }
                    )
                    Text(
                        text = ":",
                        style = OmniToolTheme.typography.headingLarge,
                        color = OmniToolTheme.colors.textPrimary
                    )
                    TimePickerWheel(
                        value = viewModel.selectedMinute,
                        range = 0..59,
                        onValueChange = { viewModel.setTime(viewModel.selectedHour, it) }
                    )
                }
                
                // From timezone
                TimezoneSelector(
                    label = "From",
                    timezone = viewModel.fromTimezone.id,
                    onClick = { showFromPicker = true }
                )
                
                // Swap button
                Box(
                    modifier = Modifier
                        .clip(OmniToolTheme.shapes.full)
                        .background(OmniToolTheme.colors.coral.copy(alpha = 0.15f))
                        .clickable { viewModel.swapTimezones() }
                        .padding(OmniToolTheme.spacing.xs)
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Refresh,
                        contentDescription = "Swap",
                        tint = OmniToolTheme.colors.coral,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // To timezone
                TimezoneSelector(
                    label = "To",
                    timezone = viewModel.toTimezone.id,
                    onClick = { showToPicker = true }
                )
            }
        },
        outputContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
            ) {
                Text(
                    text = viewModel.convertedTime,
                    style = OmniToolTheme.typography.headingLarge.copy(fontSize = 48.sp),
                    color = OmniToolTheme.colors.coral
                )
                Text(
                    text = viewModel.timeDifference,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        },
        primaryActionLabel = "Now",
        onPrimaryAction = { viewModel.useCurrentTime() }
    )
    
    // Timezone picker dialogs
    if (showFromPicker) {
        TimezonePickerDialog(
            timezones = viewModel.availableTimezones,
            onSelect = {
                viewModel.setFromTimezone(it)
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }
    
    if (showToPicker) {
        TimezonePickerDialog(
            timezones = viewModel.availableTimezones,
            onSelect = {
                viewModel.setToTimezone(it)
                showToPicker = false
            },
            onDismiss = { showToPicker = false }
        )
    }
}

@Composable
private fun TimePickerWheel(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { 
            val next = if (value >= range.last) range.first else value + 1
            onValueChange(next)
        }) {
            Icon(
                imageVector = OmniToolIcons.ExpandLess,
                contentDescription = "Increase",
                tint = OmniToolTheme.colors.textMuted
            )
        }
        
        Text(
            text = value.toString().padStart(2, '0'),
            style = OmniToolTheme.typography.headingLarge,
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
        
        IconButton(onClick = { 
            val prev = if (value <= range.first) range.last else value - 1
            onValueChange(prev)
        }) {
            Icon(
                imageVector = OmniToolIcons.ExpandMore,
                contentDescription = "Decrease",
                tint = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun TimezoneSelector(
    label: String,
    timezone: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timezone,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary
            )
            Icon(
                imageVector = OmniToolIcons.ChevronRight,
                contentDescription = null,
                tint = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun TimezonePickerDialog(
    timezones: List<TimezoneInfo>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTimezones = timezones.filter { 
        it.id.contains(searchQuery, ignoreCase = true) ||
        it.displayName.contains(searchQuery, ignoreCase = true)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Timezone") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(filteredTimezones.take(50)) { tz ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(tz.id) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tz.id,
                                style = OmniToolTheme.typography.body,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = tz.offset,
                                style = OmniToolTheme.typography.caption,
                                color = OmniToolTheme.colors.coral
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
