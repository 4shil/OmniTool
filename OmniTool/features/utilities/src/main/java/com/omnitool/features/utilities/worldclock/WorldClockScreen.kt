package com.omnitool.features.utilities.worldclock

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * World Clock Screen
 * 
 * Features:
 * - Clock list
 * - Add city dialog
 * - Live time display
 */
@Composable
fun WorldClockScreen(
    onBack: () -> Unit,
    viewModel: WorldClockViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    ToolWorkspaceScreen(
        toolName = "World Clock",
        toolIcon = OmniToolIcons.WorldClock,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Local time header
                LocalTimeHeader(
                    localTime = viewModel.currentLocalTime,
                    is24Hour = viewModel.is24Hour,
                    onToggleFormat = { viewModel.toggle24Hour() }
                )
                
                // Clock list
                ClockList(
                    clocks = viewModel.selectedClocks,
                    onRemove = { viewModel.removeClock(it) }
                )
            }
        },
        outputContent = {
            // Add button
            AddCityButton(onClick = { showAddDialog = true })
        },
        primaryActionLabel = null,
        onPrimaryAction = null
    )
    
    // Add city dialog
    if (showAddDialog) {
        AddCityDialog(
            searchQuery = viewModel.searchQuery,
            onSearchChange = { viewModel.updateSearchQuery(it) },
            zones = viewModel.getFilteredZones(),
            onSelect = { 
                viewModel.addClock(it)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun LocalTimeHeader(
    localTime: String,
    is24Hour: Boolean,
    onToggleFormat: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.skyBlue.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Local Time",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = localTime,
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = OmniToolTheme.colors.skyBlue
            )
            TextButton(onClick = onToggleFormat) {
                Text(
                    text = if (is24Hour) "12-hour" else "24-hour",
                    color = OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ClockList(
    clocks: List<ClockEntry>,
    onRemove: (ClockEntry) -> Unit
) {
    if (clocks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No clocks added",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(clocks) { clock ->
                ClockItem(clock = clock, onRemove = { onRemove(clock) })
            }
        }
    }
}

@Composable
private fun ClockItem(
    clock: ClockEntry,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = clock.cityName,
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "${clock.currentDate} • ${clock.offsetHours}",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        
        Text(
            text = clock.currentTime,
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.skyBlue
        )
        
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = OmniToolIcons.Close,
                contentDescription = "Remove",
                tint = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun AddCityButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.skyBlue.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = OmniToolIcons.Add,
                contentDescription = null,
                tint = OmniToolTheme.colors.skyBlue
            )
            Text(
                text = "Add City",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.skyBlue
            )
        }
    }
}

@Composable
private fun AddCityDialog(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    zones: List<TimeZoneInfo>,
    onSelect: (TimeZoneInfo) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add City") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search cities...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(zones) { zone ->
                        ListItem(
                            headlineContent = { Text(zone.displayName) },
                            supportingContent = { Text("UTC${zone.offsetString}") },
                            modifier = Modifier.clickable { onSelect(zone) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
