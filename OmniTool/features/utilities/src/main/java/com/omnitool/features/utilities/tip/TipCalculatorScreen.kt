package com.omnitool.features.utilities.tip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
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
 * Tip Calculator Screen
 * 
 * Features:
 * - Bill input
 * - Tip percentage with presets
 * - Split bill
 * - Results display
 */
@Composable
fun TipCalculatorScreen(
    onBack: () -> Unit,
    viewModel: TipCalculatorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Tip Calculator",
        toolIcon = OmniToolIcons.TipCalculator,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Bill amount input
                BillInput(
                    value = viewModel.billAmount,
                    onValueChange = { viewModel.updateBillAmount(it) }
                )
                
                // Tip percentage
                TipSelector(
                    percent = viewModel.tipPercent,
                    onPercentChange = { viewModel.setTipPercent(it) },
                    onPresetSelect = { viewModel.selectPresetTip(it) }
                )
                
                // Split and round options
                OptionsRow(
                    splitCount = viewModel.splitCount,
                    onSplitChange = { viewModel.setSplitCount(it) },
                    roundUp = viewModel.roundUp,
                    onRoundToggle = { viewModel.toggleRoundUp() }
                )
            }
        },
        outputContent = {
            ResultsDisplay(
                tip = viewModel.formattedTip,
                total = viewModel.formattedTotal,
                perPerson = viewModel.formattedPerPerson,
                splitCount = viewModel.splitCount
            )
        },
        primaryActionLabel = null,
        onPrimaryAction = {},
        secondaryActionLabel = if (viewModel.billAmount.isNotEmpty()) "Clear" else null,
        onSecondaryAction = if (viewModel.billAmount.isNotEmpty()) {
            { viewModel.clear() }
        } else null
    )
}

@Composable
private fun BillInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = "Bill Amount",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0.00") },
            leadingIcon = {
                Text(
                    text = "$",
                    style = OmniToolTheme.typography.header,
                    color = OmniToolTheme.colors.primaryLime
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            textStyle = OmniToolTheme.typography.header.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 28.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.primaryLime,
                unfocusedBorderColor = OmniToolTheme.colors.surface,
                cursorColor = OmniToolTheme.colors.primaryLime,
                focusedTextColor = OmniToolTheme.colors.textPrimary,
                unfocusedTextColor = OmniToolTheme.colors.textPrimary
            ),
            singleLine = true
        )
    }
}

@Composable
private fun TipSelector(
    percent: Int,
    onPercentChange: (Int) -> Unit,
    onPresetSelect: (Int) -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tip Percentage",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "$percent%",
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.primaryLime
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Preset buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10, 15, 18, 20, 25).forEach { preset ->
                val isSelected = preset == percent
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(OmniToolTheme.shapes.small)
                        .background(
                            if (isSelected) OmniToolTheme.colors.primaryLime.copy(alpha = 0.2f)
                            else OmniToolTheme.colors.surface
                        )
                        .clickable { onPresetSelect(preset) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$preset%",
                        style = OmniToolTheme.typography.label,
                        color = if (isSelected) OmniToolTheme.colors.primaryLime 
                                else OmniToolTheme.colors.textSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = percent.toFloat(),
            onValueChange = { onPercentChange(it.toInt()) },
            valueRange = 0f..50f,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.primaryLime,
                activeTrackColor = OmniToolTheme.colors.primaryLime,
                inactiveTrackColor = OmniToolTheme.colors.surface
            )
        )
    }
}

@Composable
private fun OptionsRow(
    splitCount: Int,
    onSplitChange: (Int) -> Unit,
    roundUp: Boolean,
    onRoundToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Split count
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .padding(OmniToolTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Split",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                Text(
                    text = "$splitCount people",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(OmniToolTheme.shapes.small)
                        .background(OmniToolTheme.colors.surface)
                        .clickable { onSplitChange(splitCount - 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        style = OmniToolTheme.typography.header,
                        color = OmniToolTheme.colors.textPrimary
                    )
                }
                
                Text(
                    text = splitCount.toString(),
                    style = OmniToolTheme.typography.header,
                    color = OmniToolTheme.colors.primaryLime
                )
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(OmniToolTheme.shapes.small)
                        .background(OmniToolTheme.colors.surface)
                        .clickable { onSplitChange(splitCount + 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = OmniToolTheme.typography.header,
                        color = OmniToolTheme.colors.textPrimary
                    )
                }
            }
        }
        
        // Round up toggle
        Row(
            modifier = Modifier
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .clickable(onClick = onRoundToggle)
                .padding(OmniToolTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Round",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                Text(
                    text = "Up",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
            Switch(
                checked = roundUp,
                onCheckedChange = { onRoundToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OmniToolTheme.colors.primaryLime,
                    checkedTrackColor = OmniToolTheme.colors.primaryLime.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun ResultsDisplay(
    tip: String,
    total: String,
    perPerson: String,
    splitCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultRow(label = "Tip", value = tip)
        
        Divider(color = OmniToolTheme.colors.surface)
        
        ResultRow(label = "Total", value = total, highlight = true)
        
        if (splitCount > 1) {
            Divider(color = OmniToolTheme.colors.surface)
            ResultRow(
                label = "Per Person",
                value = perPerson,
                subtitle = "÷ $splitCount people"
            )
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        }
        
        Text(
            text = value,
            style = OmniToolTheme.typography.header.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = if (highlight) 28.sp else 22.sp
            ),
            color = if (highlight) OmniToolTheme.colors.primaryLime 
                    else OmniToolTheme.colors.textPrimary
        )
    }
}
