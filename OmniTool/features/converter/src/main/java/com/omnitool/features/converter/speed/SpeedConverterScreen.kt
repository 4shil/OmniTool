package com.omnitool.features.converter.speed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Speed Converter Screen
 */
@Composable
fun SpeedConverterScreen(
    onBack: () -> Unit,
    viewModel: SpeedConverterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Speed Converter",
        toolIcon = OmniToolIcons.SpeedConverter,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Unit selector
                UnitSelector(
                    selected = viewModel.fromUnit,
                    onSelect = { viewModel.setFromUnit(it) }
                )
                
                // Input
                OutlinedTextField(
                    value = viewModel.inputValue,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter ${viewModel.fromUnit.displayName}") },
                    suffix = { Text(viewModel.fromUnit.symbol) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.skyBlue,
                        cursorColor = OmniToolTheme.colors.skyBlue
                    )
                )
            }
        },
        outputContent = {
            SpeedResults(
                results = viewModel.results,
                fromUnit = viewModel.fromUnit,
                onCopy = { copyToClipboard(context, it) }
            )
        },
        primaryActionLabel = if (viewModel.inputValue.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun UnitSelector(
    selected: SpeedUnit,
    onSelect: (SpeedUnit) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SpeedUnit.values().toList()) { unit ->
            val isSelected = unit == selected
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.skyBlue.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.elevatedSurface
                    )
                    .clickable { onSelect(unit) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = unit.symbol,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.skyBlue 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun SpeedResults(
    results: Map<SpeedUnit, String>,
    fromUnit: SpeedUnit,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter a value to convert",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            SpeedUnit.values().forEach { unit ->
                val value = results[unit] ?: ""
                SpeedResultRow(
                    unit = unit,
                    value = value,
                    isSource = unit == fromUnit,
                    onCopy = { onCopy("$value ${unit.symbol}") }
                )
            }
        }
    }
}

@Composable
private fun SpeedResultRow(
    unit: SpeedUnit,
    value: String,
    isSource: Boolean,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isSource) OmniToolTheme.colors.skyBlue.copy(alpha = 0.1f)
                else OmniToolTheme.colors.surface
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = unit.displayName,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = if (value.isNotEmpty()) "$value ${unit.symbol}" else "-",
                style = OmniToolTheme.typography.label,
                color = if (isSource) OmniToolTheme.colors.skyBlue 
                        else OmniToolTheme.colors.textPrimary
            )
        }
        
        if (value.isNotEmpty()) {
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = OmniToolIcons.Copy,
                    contentDescription = "Copy",
                    tint = OmniToolTheme.colors.skyBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Speed", text))
}
