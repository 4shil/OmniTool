package com.omnitool.features.converter.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField
import com.omnitool.ui.screens.WorkspaceOutputField

/**
 * Unit Converter Screen
 */
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit,
    viewModel: UnitConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Unit Converter",
            toolIcon = OmniToolIcons.UnitConverter,
            accentColor = OmniToolTheme.colors.coral,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    // Category tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                    ) {
                        UnitCategory.values().forEach { category ->
                            CategoryChip(
                                category = category,
                                selected = category == viewModel.selectedCategory,
                                onClick = { viewModel.setCategory(category) }
                            )
                        }
                    }
                    
                    // From unit selector + input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UnitDropdown(
                            units = viewModel.selectedCategory.units,
                            selected = viewModel.fromUnit,
                            onSelect = { viewModel.setFromUnit(it) },
                            modifier = Modifier.weight(0.4f)
                        )
                        Box(modifier = Modifier.weight(0.6f)) {
                            WorkspaceInputField(
                                value = viewModel.inputValue,
                                onValueChange = { viewModel.updateInput(it) },
                                placeholder = "Enter value",
                                minLines = 1
                            )
                        }
                    }
                    
                    // Swap button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(OmniToolTheme.shapes.full)
                                .background(OmniToolTheme.colors.coral.copy(alpha = 0.15f))
                                .clickable { viewModel.swapUnits() }
                                .padding(OmniToolTheme.spacing.xs)
                        ) {
                            Icon(
                                imageVector = OmniToolIcons.Refresh,
                                contentDescription = "Swap",
                                tint = OmniToolTheme.colors.coral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // To unit selector
                    UnitDropdown(
                        units = viewModel.selectedCategory.units,
                        selected = viewModel.toUnit,
                        onSelect = { viewModel.setToUnit(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            outputContent = {
                WorkspaceOutputField(
                    value = if (viewModel.outputValue.isNotEmpty()) 
                        "${viewModel.outputValue} ${viewModel.toUnit?.symbol ?: ""}"
                    else "",
                    placeholder = "Result will appear here...",
                    onCopy = { toastState.show("Copied", ToastType.Success) }
                )
            },
            primaryActionLabel = "Clear",
            onPrimaryAction = { viewModel.clearAll() }
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            OmniToolToast(
                message = toastState.message,
                type = toastState.type,
                visible = toastState.isVisible,
                onDismiss = { toastState.dismiss() }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: UnitCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.coral.copy(alpha = 0.15f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = category.displayName,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.coral else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun UnitDropdown(
    units: List<UnitType>,
    selected: UnitType?,
    onSelect: (UnitType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.elevatedSurface)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.let { "${it.name} (${it.symbol})" } ?: "Select",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textPrimary
                )
                Icon(
                    imageVector = OmniToolIcons.ExpandMore,
                    contentDescription = null,
                    tint = OmniToolTheme.colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            "${unit.name} (${unit.symbol})",
                            style = OmniToolTheme.typography.body
                        )
                    },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
