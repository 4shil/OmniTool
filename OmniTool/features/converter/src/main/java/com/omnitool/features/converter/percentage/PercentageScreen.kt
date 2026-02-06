package com.omnitool.features.converter.percentage

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Percentage Calculator Screen
 */
@Composable
fun PercentageScreen(
    onBack: () -> Unit,
    viewModel: PercentageViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Percentage",
        toolIcon = OmniToolIcons.Percentage,
        accentColor = OmniToolTheme.colors.warmYellow,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Mode selector
                ModeSelector(
                    selected = viewModel.mode,
                    onSelect = { viewModel.setMode(it) }
                )
                
                // Formula hint
                Text(
                    text = viewModel.mode.formula,
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                // Input fields
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.valueA,
                        onValueChange = { viewModel.setValueA(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text(viewModel.mode.labelA) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.warmYellow
                        )
                    )
                    
                    OutlinedTextField(
                        value = viewModel.valueB,
                        onValueChange = { viewModel.setValueB(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text(viewModel.mode.labelB) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.warmYellow
                        )
                    )
                }
            }
        },
        outputContent = {
            ResultDisplay(result = viewModel.result)
        },
        primaryActionLabel = if (viewModel.valueA.isNotEmpty() || viewModel.valueB.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun ModeSelector(
    selected: PercentageMode,
    onSelect: (PercentageMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        PercentageMode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.warmYellow 
                            else OmniToolTheme.colors.textMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ResultDisplay(result: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Result",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = result.ifEmpty { "-" },
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (result.isNotEmpty()) OmniToolTheme.colors.warmYellow 
                        else OmniToolTheme.colors.textMuted
            )
        }
    }
}
