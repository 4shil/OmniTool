package com.omnitool.features.converter.fuel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Fuel Calculator Screen
 */
@Composable
fun FuelCalculatorScreen(
    onBack: () -> Unit,
    viewModel: FuelCalculatorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Fuel Calculator",
        toolIcon = OmniToolIcons.FuelCalc,
        accentColor = OmniToolTheme.colors.warmYellow,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Distance input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.distance,
                        onValueChange = { viewModel.setDistance(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Distance") },
                        suffix = { Text(viewModel.distanceUnit.symbol) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.warmYellow,
                            cursorColor = OmniToolTheme.colors.warmYellow
                        )
                    )
                    UnitToggle(
                        options = DistanceUnit.values().toList(),
                        selected = viewModel.distanceUnit,
                        onSelect = { viewModel.setDistanceUnit(it) },
                        getLabel = { it.symbol }
                    )
                }
                
                // Fuel used input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.fuelUsed,
                        onValueChange = { viewModel.setFuelUsed(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Fuel Used") },
                        suffix = { Text(viewModel.fuelUnit.symbol) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.warmYellow,
                            cursorColor = OmniToolTheme.colors.warmYellow
                        )
                    )
                    UnitToggle(
                        options = FuelVolumeUnit.values().toList(),
                        selected = viewModel.fuelUnit,
                        onSelect = { viewModel.setFuelUnit(it) },
                        getLabel = { it.symbol }
                    )
                }
                
                // Fuel price input (optional)
                OutlinedTextField(
                    value = viewModel.fuelPrice,
                    onValueChange = { viewModel.setFuelPrice(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fuel Price (optional)") },
                    placeholder = { Text("Per ${viewModel.fuelUnit.symbol}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.warmYellow,
                        cursorColor = OmniToolTheme.colors.warmYellow
                    )
                )
            }
        },
        outputContent = {
            FuelResults(
                efficiency = viewModel.fuelEfficiency,
                efficiencyInverse = viewModel.fuelEfficiencyInverse,
                tripCost = viewModel.tripCost,
                costPerKm = viewModel.costPerKm
            )
        },
        primaryActionLabel = if (viewModel.distance.isNotEmpty() || viewModel.fuelUsed.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun <T> UnitToggle(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    getLabel: (T) -> String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = getLabel(option),
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.warmYellow 
                            else OmniToolTheme.colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun FuelResults(
    efficiency: String?,
    efficiencyInverse: String?,
    tripCost: String?,
    costPerKm: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (efficiency == null && efficiencyInverse == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter distance and fuel to calculate",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            // Fuel efficiency
            ResultRow(
                label = "Fuel Consumption",
                value = efficiency ?: "-",
                isHighlight = true
            )
            
            ResultRow(
                label = "Distance per Unit",
                value = efficiencyInverse ?: "-",
                isHighlight = false
            )
            
            // Cost results (if price provided)
            if (tripCost != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = OmniToolTheme.colors.border
                )
                
                ResultRow(
                    label = "Trip Cost",
                    value = tripCost,
                    isHighlight = true
                )
                
                if (costPerKm != null) {
                    ResultRow(
                        label = "Cost per Distance",
                        value = costPerKm,
                        isHighlight = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    isHighlight: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isHighlight) OmniToolTheme.colors.warmYellow.copy(alpha = 0.1f)
                else OmniToolTheme.colors.surface
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.label.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isHighlight) OmniToolTheme.colors.warmYellow 
                    else OmniToolTheme.colors.textPrimary
        )
    }
}
